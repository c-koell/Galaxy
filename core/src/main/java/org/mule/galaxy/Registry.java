package org.mule.galaxy;


import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;

import javax.activation.MimeTypeParseException;
import javax.jcr.query.Query;
import javax.xml.namespace.QName;

import org.mule.galaxy.Index.Language;

import org.w3c.dom.Document;

public interface Registry {

    Workspace getWorkspace(String id) throws RegistryException, NotFoundException;
    
    Collection<Workspace> getWorkspaces() throws RegistryException;
    
    Artifact createArtifact(Workspace workspace, Object data) throws RegistryException, MimeTypeParseException;
    
    Artifact createArtifact(Workspace workspace, String contentType, String name, InputStream inputStream) 
        throws RegistryException, IOException, MimeTypeParseException;
    
    ArtifactVersion newVersion(Artifact artifact, Object data) throws RegistryException, IOException;
    
    ArtifactVersion newVersion(Artifact artifact, InputStream inputStream) throws RegistryException, IOException;

    Collection<Artifact> getArtifacts(Workspace workspace) throws RegistryException;
    
    Artifact getArtifact(String id) throws NotFoundException;
    
    void delete(Artifact artifact) throws RegistryException;

    Index registerIndex(String indexId, 
                        String displayName, 
                        Language language,
                        Class<?> resultType, 
                        String indexExpression, 
                        QName... documentTypes) throws RegistryException;

    Set<Artifact> search(String index, Object input) throws RegistryException;

    Set<Index> getIndices(QName documentType) throws RegistryException;
}
