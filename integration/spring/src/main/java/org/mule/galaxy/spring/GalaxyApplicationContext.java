/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.galaxy.spring;

import org.mule.galaxy.config.ConfigurationSupport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

/**
 * A Mule Configuration Builder used for configuring a Mule instance from a Galaxy Registry URL. The can include Login
 * credentials, the location of the server and a query string used to locate artifacts in the registry. For example -
 * <code>
 * http://admin:admin@localhost:9002/api/registry?q=select artifact where mule.service = 'GreeterUMO'"
 * </code>
 *
 * Note that if the query returns more than one artifact they all get loaded, so its important that the query only returns
 * 'mule' artifacts and only artifacts targeted for this Mule instance.
 */
public class GalaxyApplicationContext extends AbstractXmlApplicationContext
{
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    /**
     * logger used by this class
     */
    protected transient final Log logger = LogFactory.getLog(GalaxyApplicationContext.class);

    private URL configURL;

    public GalaxyApplicationContext(URL configURL)
    {
        super();
        this.configURL = configURL;
        refresh();
    }

    public GalaxyApplicationContext(URL configURL, ApplicationContext applicationContext)
    {
        super(applicationContext);
        this.configURL = configURL;
        refresh();
    }

    @Override
    protected Resource[] getConfigResources()
    {
        try
        {
            ConfigurationSupport configSupport = new ConfigurationSupport();
            org.mule.galaxy.config.Resource[] resources = configSupport.getArtifacts(configURL.toString(), null);


            ByteArrayResource[] springResources = new ByteArrayResource[resources.length];
            for (int i = 0; i < resources.length; i++)
            {

                springResources[i] = new ByteArrayResource(readBytesFromStream(resources[i].getInputStream())
                        , resources[i].getName());
            }
            return springResources;
        }
        catch (IOException e)
        {
            throw new BeanCreationException("Failed to get Spring beans from the registry: " + e.getMessage(), e);
        }
    }

    public static byte[] readBytesFromStream(InputStream in) throws IOException {
        int i = in.available();
        if (i < DEFAULT_BUFFER_SIZE) {
            i = DEFAULT_BUFFER_SIZE;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream(i);
        copy(in, bos, DEFAULT_BUFFER_SIZE);
        in.close();
        return bos.toByteArray();
    }

    public static int copy(final InputStream input,
                           final OutputStream output,
                           int bufferSize)
       throws IOException {
       int avail = input.available();
       if (avail > 262144) {
           avail = 262144;
       }
       if (avail > bufferSize) {
           bufferSize = avail;
       }
       final byte[] buffer = new byte[bufferSize];
       int n = 0;
       n = input.read(buffer);
       int total = 0;
       while (-1 != n) {
           output.write(buffer, 0, n);
           total += n;
           n = input.read(buffer);
       }
       return total;
   }
}