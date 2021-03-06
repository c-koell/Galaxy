package org.mule.galaxy.impl.policy;

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.mule.galaxy.Item;
import org.mule.galaxy.artifact.Artifact;
import org.mule.galaxy.policy.AbstractPolicy;
import org.mule.galaxy.type.TypeManager;

/**
 * A helper class for policies which apply to XML documents.
 * 
 */
public abstract class AbstractXmlPolicy extends AbstractPolicy {
    protected Set<QName> supportedDocumentTypes = new HashSet<QName>();
    
    protected AbstractXmlPolicy(String id, String name, String description) {
        super(id, name, description);
    }

    public boolean applies(Item item) {
        return item.getType().inheritsFrom(TypeManager.ARTIFACT_VERSION) && 
            applies(item, (Artifact) item.getProperty("artifact"));
    }

    public boolean applies(Item item, Artifact artifact) {
        if (artifact == null) return false;
        
        return applies(item, artifact.getDocumentType());
    }
    
    public boolean applies(Item item, QName documentType) {
        return supportedDocumentTypes.contains(documentType);
    }
}
