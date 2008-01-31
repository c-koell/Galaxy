package org.mule.galaxy.view;

import org.mule.galaxy.api.Artifact;
import org.mule.galaxy.api.util.Constants;
import org.mule.galaxy.api.view.ViewManager;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class ArtifactViewTest extends AbstractGalaxyTest {
    protected ViewManager viewManager;
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-test.xml" };
    }
    
    public void testView() throws Exception {
        assertNotNull(viewManager);
        
        CustomArtifactTypeView view = (CustomArtifactTypeView) viewManager.getArtifactTypeView(Constants.WSDL_DEFINITION_QNAME);
        
        assertEquals(9, view.getColumns().size());
        
        // Import a document which should now be indexed
        Artifact artifact = importHelloWsdl();
        
        // Check and see if our view works
        String[] columns = view.getColumnNames();
        assertEquals(9, columns.length);
        
        String name = artifact.getName();
        assertEquals(name, view.getColumnValue(artifact, 0));
        assertEquals("/Default Workspace/", view.getColumnValue(artifact, 1));
        assertEquals("application/xml", view.getColumnValue(artifact, 2));
        assertEquals("http://mule.org/hello_world", view.getColumnValue(artifact, 3));
        assertEquals("0.1", view.getColumnValue(artifact, 4));
        assertEquals("Created", view.getColumnValue(artifact, 5));
        assertEquals("1", view.getColumnValue(artifact, 6));
    }
    
}
