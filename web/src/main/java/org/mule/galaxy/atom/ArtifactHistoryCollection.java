/*
 * $Id: ContextPathResolver.java 794 2008-04-23 22:23:10Z andrew $
 * --------------------------------------------------------------------------------------
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.mule.galaxy.atom;


import org.mule.galaxy.ArtifactResult;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.Registry;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.security.User;

import java.io.InputStream;

import javax.activation.MimeType;

import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;

public class ArtifactHistoryCollection extends AbstractArtifactCollection {
    
    public ArtifactHistoryCollection(Registry registry, LifecycleManager lifecycleManager) {
        super(registry, lifecycleManager);
    }

    @Override
    public String getMediaName(ArtifactVersion version) {
        return super.getMediaName(version);
    }
    
    @Override
    public String getQueryParameters(ArtifactVersion version, RequestContext request) {
        return "version=" + version.getVersionLabel();
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
        return super.getName(version);
    }

    public String getTitle(RequestContext request) {
        return getArtifact(request).getName() + " Revisions";
    }

    public String getTitle(ArtifactVersion entry) {
        return entry.getParent().getName() + " Version " + entry.getVersionLabel();
    }


}
