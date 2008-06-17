package org.mule.galaxy;


import java.util.Calendar;
import java.util.List;

import javax.activation.MimeType;
import javax.xml.namespace.QName;

/**
 * An artifact which can be versioned inside a repository. Can be anything
 * from a configuration file to a wsdl to a jar etc.
 */
public interface Artifact extends Item<Workspace> {
    
    String getPath();
    
    Workspace getParent();
    
    Calendar getCreated();
    
    Calendar getUpdated();
    
    String getName();
    
    void setName(String name);
    
    String getDescription();
    
    void setDescription(String description);
    
    MimeType getContentType();
    
    QName getDocumentType();
    
    void setDocumentType(QName documentType);
    
    List<ArtifactVersion> getVersions();

    ArtifactVersion getVersion(String versionName);

    /**
     * Get the default version of this artifact. If this hasn't been specifically set,
     * its the latest version of the artifact.
     */
    ArtifactVersion getDefaultOrLastVersion();

    ContentHandler getContentHandler();
}
