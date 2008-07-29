package org.mule.galaxy.mule1;


import org.mule.galaxy.mule1.config.GalaxyConfigurationBuilder;
import org.mule.galaxy.test.AbstractAtomTest;
import org.mule.umo.manager.UMOManager;
import org.mule.umo.model.UMOModel;

public class GalaxyConfigurationBuilderTest extends AbstractAtomTest
{
    private UMOManager manager;

    public void testMuleConfig() throws Exception
    {
        String configURL = "http://admin:admin@localhost:9002/api/registry?q=select artifact where mule.server.id = 'hello-server'";

        GalaxyConfigurationBuilder builder = new GalaxyConfigurationBuilder();

        manager = builder.configure(configURL);

        //Assert components
        UMOModel model = (UMOModel)manager.getModels().get("main");
        assertNotNull(model);
        assertNotNull(model.getComponent("GreeterUMO"));
        assertNotNull(model.getComponent("ChitChatUMO"));

    }

    public void testMuleConfigWithProperties() throws Exception
    {

        String configURL = "http://localhost:9002/api/registry";

        GalaxyConfigurationBuilder builder = new GalaxyConfigurationBuilder();

        manager = builder.configure(configURL, "credentials.properties");

        //Assert components
        UMOModel model = (UMOModel)manager.getModels().get("main");
        assertNotNull(model);
        assertNotNull(model.getComponent("GreeterUMO"));
        assertNotNull(model.getComponent("ChitChatUMO"));

    }


    @Override
    protected void tearDown() throws Exception
    {
        try
        {
            if(manager!=null)
            {
                manager.dispose();
            }
        }
        finally
        {
            super.tearDown();
        }
    }
}