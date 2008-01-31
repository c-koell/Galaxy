package org.mule.galaxy.atom;


import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.abdera.i18n.text.UrlEncoding;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.RequestContext.Scope;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.mule.galaxy.api.Artifact;
import org.mule.galaxy.api.ArtifactPolicyException;
import org.mule.galaxy.api.ArtifactResult;
import org.mule.galaxy.api.ArtifactVersion;
import org.mule.galaxy.api.Registry;
import org.mule.galaxy.api.RegistryException;
import org.mule.galaxy.api.security.User;

public class ArtifactHistoryCollection extends AbstractArtifactCollection {
    
    public ArtifactHistoryCollection(Registry registry) {
        super(registry);
    }

    @Override
    public String getMediaName(ArtifactVersion version) {
        return super.getMediaName(version) + "?version=" + version.getVersionLabel();
    }

    @Override
    public ArtifactVersion getEntry(String name, RequestContext request) throws ResponseContextException {
        // TODO Auto-generated method stub
        return super.getEntry(name, request);
    }

    @Override
    protected ArtifactResult postMediaEntry(String slug,
                                              MimeType mimeType, 
                                              String version,
                                              InputStream inputStream, 
                                              User user,
                                              RequestContext ctx)
        throws ResponseContextException {
        throw new ResponseContextException(501);
    }
    
    @Override
    public void deleteEntry(String entry, RequestContext arg1) throws ResponseContextException {
        throw new ResponseContextException(501);
    }

    @Override
    public Iterable<ArtifactVersion> getEntries(RequestContext request) throws ResponseContextException {
        return getArtifact(request).getVersions();
    }

    @Override
    public String getId(RequestContext request) {
        return "tag:galaxy.mulesource.com,2008:registry:" + registry.getUUID() + ":history:feed";
    }

    @Override
    public String getId(ArtifactVersion entry) throws ResponseContextException {
        return ID_PREFIX + entry.getParent().getId() + ":" + entry.getVersionLabel();
    }

    @Override
    public String getName(ArtifactVersion version) {
        return super.getName(version) + "?version=" + version.getVersionLabel();
    }

    @Override
    public void putEntry(ArtifactVersion arg0, String arg1, Date arg2, List<Person> arg3, String arg4,
                            Content arg5, RequestContext arg6) throws ResponseContextException {
        throw new ResponseContextException(501);
    }

    public String getTitle(RequestContext request) {
        return getArtifact(request).getName() + " Revisions";
    }

    public String getTitle(ArtifactVersion entry) {
        return entry.getParent().getName() + " Version " + entry.getVersionLabel();
    }


}
