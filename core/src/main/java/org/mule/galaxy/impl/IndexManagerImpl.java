package org.mule.galaxy.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.sf.saxon.javax.xml.xquery.XQConnection;
import net.sf.saxon.javax.xml.xquery.XQDataSource;
import net.sf.saxon.javax.xml.xquery.XQItem;
import net.sf.saxon.javax.xml.xquery.XQPreparedExpression;
import net.sf.saxon.javax.xml.xquery.XQResultSequence;
import net.sf.saxon.xqj.SaxonXQDataSource;
import org.apache.commons.lang.BooleanUtils;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.ContentService;
import org.mule.galaxy.Dao;
import org.mule.galaxy.GalaxyException;
import org.mule.galaxy.Index;
import org.mule.galaxy.IndexManager;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.XmlContentHandler;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;
import org.mule.galaxy.util.DOMUtils;
import org.mule.galaxy.util.LogUtils;
import org.mule.galaxy.util.QNameUtil;
import org.springmodules.jcr.JcrCallback;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class IndexManagerImpl extends AbstractReflectionDao<Index> implements IndexManager {
    private Logger LOGGER = LogUtils.getL7dLogger(IndexManagerImpl.class);

    private ContentService contentService;

    private XPathFactory factory = XPathFactory.newInstance();

    public IndexManagerImpl() throws Exception {
        super(Index.class, "indexes", false);
    }

    @SuppressWarnings("unchecked")
    public Collection<Index> getIndexes() {
        return listAll();
    }

    @Override
    protected String getObjectNodeName(Index t) {
        return t.getId();
    }

    @Override
    public Index build(Node node, Session session) throws Exception {
        Index i = super.build(node, session);
        i.setId(node.getName());
        return i;
    }

    protected Node findNode(String id, Session session) throws RepositoryException {
        try {
            return getObjectsNode().getNode(id);
        } catch (PathNotFoundException e) {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public Set<Index> getIndices(final QName documentType) {
        return (Set<Index>) execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                QueryManager qm = getQueryManager(session);
                Query query = qm.createQuery("//indexes/*[@documentTypes=" 
                                                 + JcrUtil.stringToXPathLiteral(documentType.toString()) + "]", 
                                             Query.XPATH);
                
                QueryResult result = query.execute();
                
                Set<Index> indices = new HashSet<Index>();
                for (NodeIterator nodes = result.getNodes(); nodes.hasNext();) {
                    Node node = nodes.nextNode();
                    
                    try {
                        indices.add(build(node, session));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                return indices;
            }
        });
    }

    public Index getIndex(final String id) throws NotFoundException {
        Index i = get(id);
        if (i == null) {
            throw new NotFoundException(id);
        }
        return i;
    }

    public void index(ArtifactVersion version) {
        QName dt = version.getParent().getDocumentType();
        if (dt == null) return;
        
        Collection<Index> indices = getIndices(dt);
        
        for (Index idx : indices) {
            try {
                switch (idx.getLanguage()) {
                case XQUERY:
                    indexWithXQuery(version, idx);
                    break;
                case XPATH:
                    indexWithXPath(version, idx);
                    break;
                default:
                    throw new UnsupportedOperationException();
                }
            } catch (Throwable t) {
                LOGGER.log(Level.SEVERE, "Could not process index " + idx.getId(), t);
            }
        }
    }

    private void indexWithXPath(ArtifactVersion jcrVersion, Index idx) throws GalaxyException {
        XmlContentHandler ch = (XmlContentHandler) contentService.getContentHandler(jcrVersion.getParent().getContentType());
        try {
            Document document = ch.getDocument(jcrVersion.getData());
            
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile(idx.getExpression());

            Object result = expr.evaluate(document, XPathConstants.STRING);
            
            if (result instanceof String) {
                jcrVersion.setProperty(idx.getId(), result);
                jcrVersion.setLocked(idx.getId(), true);
            }
        } catch (IOException e) {
            throw new GalaxyException(e);
        } catch (XPathExpressionException e) {
            throw new GalaxyException(e);
        } catch (PropertyException e) {
            throw new GalaxyException(e);
        }
        
    }

    private void indexWithXQuery(ArtifactVersion jcrVersion, Index idx) throws GalaxyException {
        XQDataSource ds = new SaxonXQDataSource();
        
        try {
            XQConnection conn = ds.getConnection();
            
            XQPreparedExpression ex = conn.prepareExpression(idx.getExpression());
            XmlContentHandler ch = (XmlContentHandler) contentService.getContentHandler(jcrVersion.getParent().getContentType());
            
            ex.bindNode(new QName("document"), ch.getDocument(jcrVersion.getData()), null);
            
            XQResultSequence result = ex.executeQuery();
            
            List<Object> results = new ArrayList<Object>();
            
            boolean visible = true;
            
            if (result.next()) {
                XQItem item = result.getItem();

                org.w3c.dom.Node values = item.getNode();
                
                // check locking & visibility
                NamedNodeMap atts = values.getAttributes();
                org.w3c.dom.Node visibleNode = atts.getNamedItem("visible");
                if (visibleNode != null) {
                    visible = BooleanUtils.toBoolean(visibleNode.getNodeValue());
                }
                
                // loop through the values
                Element value = DOMUtils.getFirstElement(values);
                while (value != null) {
                    Object content = DOMUtils.getContent(value);
                    
                    LOGGER.info("Adding value " + content + " to index " + idx.getId());
    
                    if (idx.getQueryType().equals(QName.class)) {
                        results.add(QNameUtil.fromString(content.toString())); 
                    } else {
                        results.add(content);
                    }
                    
                    value = (Element) DOMUtils.getNext(value, "value", org.w3c.dom.Node.ELEMENT_NODE);
                }
            }
            
            jcrVersion.setProperty(idx.getId(), results);
            jcrVersion.setLocked(idx.getId(), true);
            jcrVersion.setVisible(idx.getId(), visible);
        } catch (Exception e) {
            // TODO: better error handling for frontends
            // We should log this and make the logs retrievable
            // We should also prepare the expressions when the expression is created
            // or on startup
            throw new GalaxyException(e);
        }
        
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

}
