package org.mule.galaxy.impl.artifact;

import java.util.Collection;

import javax.wsdl.xml.WSDLLocator;
import javax.xml.namespace.QName;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactType;
import org.mule.galaxy.Index;
import org.mule.galaxy.util.Constants;
import org.mule.galaxy.view.Column;
import org.mule.galaxy.view.ColumnEvaluator;
import org.mule.galaxy.view.CustomArtifactTypeView;


import org.xml.sax.InputSource;

public class WsdlArtifactPlugin extends AbstractArtifactPlugin {

    public void initializeOnce() throws Exception {
        artifactTypeDao.save(new ArtifactType("WSDL Documents", "application/wsdl+xml", Constants.WSDL_DEFINITION_QNAME));
        
        /**
         * Creates a document like:
         * <values>
         * <value>{http://acme.com}EchoService</value>
         * <value>{http://acme.com}EchoService2</value>
         * </values>
         * etc.
         */
        String exp = 
            "declare namespace wsdl=\"http://schemas.xmlsoap.org/wsdl/\";\n" +
            "declare variable $document external;\n" +
            "" +
            "<values> {\n" +
            "for $svc in $document//wsdl:service\n" +
            "let $ns := $document/wsdl:definition/@targetNamespace\n" +
            "    return <value>{data($svc/@name)}</value>\n" +
            "} </values>";
       
        indexManager.save(new Index("wsdl.service", // index field name
                               "WSDL Services", // Display Name
                               Index.Language.XQUERY,
                               QName.class, // search input type
                               exp, // the xquery expression
                               Constants.WSDL_DEFINITION_QNAME), true); // document QName which this applies to
        exp = 
            "" +
            "declare namespace wsdl=\"http://schemas.xmlsoap.org/wsdl/\";\n" +
            "declare variable $document external;\n" +
            "" +
            "<values> {\n" +
            "for $ep in $document//wsdl:service/wsdl:port\n" +
            "let $ns := $document/wsdl:definition/@targetNamespace\n" +
            "    return <value>{data($ep/@name)}</value>\n" +
            "} </values>";
       
        indexManager.save(new Index("wsdl.endpoint", // index field name
                                    "WSDL Endpoints", // Display Name
                                    Index.Language.XQUERY,
                                    QName.class, // search input type
                                    exp, // the xquery expression
                                    Constants.WSDL_DEFINITION_QNAME), true); // document QName which this applies to
       
        exp = 
            "" +
            "declare namespace wsdl=\"http://schemas.xmlsoap.org/wsdl/\";\n" +
            "declare variable $document external;\n" +
            "" +
            "<values> {\n" +
            "for $b in $document//wsdl:bindingt\n" +
            "let $ns := $document/wsdl:definition/@targetNamespace\n" +
            "    return <value>{data($b/@name)}</value>\n" +
            "} </values>";
       
        indexManager.save(new Index("wsdl.binding", // index field name
                               "WSDL Bindings", // Display Name
                               Index.Language.XQUERY,
                               QName.class, // search input type
                               exp, // the xquery expression
                               Constants.WSDL_DEFINITION_QNAME), true); // document QName which this applies to
       
        exp = 
            "" +
            "declare namespace wsdl=\"http://schemas.xmlsoap.org/wsdl/\";\n" +
            "declare variable $document external;\n" +
            "" +
            "<values> {\n" +
            "for $pt in $document//wsdl:portType\n" +
            "let $ns := $document/wsdl:definition/@targetNamespace\n" +
            "    return <value>{data($pt/@name)}</value>\n" +
            "} </values>";
       
        indexManager.save(new Index("wsdl.portType", // index field name
                               "WSDL PortTypes", // Display Name
                               Index.Language.XQUERY,
                               QName.class, // search input type
                               exp, // the xquery expression
                               Constants.WSDL_DEFINITION_QNAME), true); // document QName which this applies to
       
        // Index the target namespace
        indexManager.save(new Index("wsdl.targetNamespace", // index field name
                               "WSDL Target Namespace", // Display Name
                               Index.Language.XPATH,
                               String.class, // search input type
                               "/*/@targetNamespace", // the xquery expression
                               Constants.WSDL_DEFINITION_QNAME), true); // document QName which this applies to
                 

    }

    public void initializeEverytime() throws Exception {
        
        CustomArtifactTypeView view = new CustomArtifactTypeView();
        view.getColumns().add(new Column("Port Types", true, false, new ColumnEvaluator() {
            public Object getValue(Object artifact) {
                Object o = ((Artifact)artifact).getActiveVersion().getProperty("wsdl.portType");
                
                if (o != null) {
                    return ((Collection) o).size();
                }
                return 0;
            }
        }));

        view.getColumns().add(new Column("Bindings", true, false, new ColumnEvaluator() {
            public Object getValue(Object artifact) {
                Object o = ((Artifact)artifact).getActiveVersion().getProperty("wsdl.binding");
                
                if (o != null) {
                    return ((Collection) o).size();
                }
                return 0;
            }
        }));
        view.getColumns().add(new Column("Services", true, false, new ColumnEvaluator() {
            public Object getValue(Object artifact) {
                Object o = ((Artifact)artifact).getActiveVersion().getProperty("wsdl.service");
                
                if (o != null) {
                    return ((Collection) o).size();
                }
                return 0;
            }
        }));
        view.getColumns().add(3, new Column("Namespace", true, false, new ColumnEvaluator() {
            public Object getValue(Object artifact) {
                return ((Artifact)artifact).getActiveVersion().getProperty("wsdl.targetNamespace");
            }
        }));
        viewManager.addView(view, Constants.WSDL_DEFINITION_QNAME);
    }


}
