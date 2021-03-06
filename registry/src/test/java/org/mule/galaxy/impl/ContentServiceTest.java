package org.mule.galaxy.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.activation.MimeType;

import org.mule.galaxy.artifact.ContentHandler;
import org.mule.galaxy.artifact.ContentService;
import org.mule.galaxy.impl.content.DefaultContentHandler;
import org.mule.galaxy.impl.content.XmlDocumentContentHandler;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.util.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ContentServiceTest extends AbstractGalaxyTest {

    ContentService contentService;

    public void testCHs() throws Exception {
        contentService = (ContentService) applicationContext.getBean("contentService");
        
        ContentHandler ch = contentService.getContentHandler(new MimeType("application/xml"));
        
        assertNotNull(ch);
        assertTrue(ch instanceof XmlDocumentContentHandler);
        
        XmlDocumentContentHandler docHandler = (XmlDocumentContentHandler) ch;
        
        Document document = DOMUtils.createDocument();
        Element el = document.createElement("foo");
        document.appendChild(el);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        docHandler.write(document, out);
        
        Document doc2 = (Document) docHandler.read(new ByteArrayInputStream(out.toByteArray()), null);
        assertNotNull(doc2);
        
        ch = contentService.getContentHandler(Document.class);
        assertNotNull(ch);
        assertTrue(ch instanceof XmlDocumentContentHandler);
        
        ch = contentService.getContentHandler(new MimeType("application/wsdl+xml"));
        assertNotNull(ch);

        ch = contentService.getContentHandler(new MimeType("text/plain"));
        assertNotNull(ch);
        assertTrue(ch instanceof DefaultContentHandler);
    }
}
