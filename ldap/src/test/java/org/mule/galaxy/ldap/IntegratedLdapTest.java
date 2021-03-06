package org.mule.galaxy.ldap;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;

import org.apache.directory.server.ldap.LdapService;
import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.mule.galaxy.Item;
import org.mule.galaxy.collab.Comment;
import org.mule.galaxy.security.Group;
import org.mule.galaxy.security.User;
import org.mule.galaxy.security.UserManager;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.util.GalaxyUtils;
import org.mule.galaxy.util.SecurityUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.ldap.core.ContextExecutor;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;

/**
 * Tests LDAP + Galaxy.
 */
public class IntegratedLdapTest extends AbstractGalaxyTest {
    protected  ContextSource initialDirContextFactory;
    
    final String groupName = "Long - name with asdfsadfdwfsd";
    final String name = "cn=" + groupName + ",ou=groups,ou=system";

    public void testUserManager() throws Exception {
        if (true) return;
        UserManager userManager = (UserManager) applicationContext.getBean("userManager");

        User user = SecurityUtils.getCurrentUser();
        assertNotNull(user.getId());
        assertNotNull(user.getUsername());
        
        assertEquals(1, user.getGroups().size());
        
        user.setProperties(GalaxyUtils.asMap("foo", (Object)"bar"));
        userManager.save(user);
        
        user = userManager.get(user.getId());
        assertNotNull(user);
        assertNotNull(user.getProperties());
        assertEquals("bar", user.getProperties().get("foo"));
        
        List<User> users = userManager.listAll();
        assertNotNull(users);
        
        assertEquals(1, users.size());
        
        user = users.iterator().next();
        assertEquals("admin", user.getUsername());
        assertNull(user.getEmail());
        assertEquals("system administrator", user.getName());

        Item a = importHelloWsdl();
        
        // do our perms work?
        Item w = registry.getItems().iterator().next();
        Collection<Item> artifacts = w.getItems();
        
        assertEquals(1, artifacts.size());

        Comment c = new Comment();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        c.setDate(cal);
        c.setUser(user);
        c.setText("Hello.");
        c.setItem(a);
        
        commentManager.addComment(c);
        
        List<Comment> comments = commentManager.getComments(a.getId());
        assertEquals(1, comments.size());
        
        Comment c2 = comments.iterator().next();
        assertNotNull(c2.getUser());
    }

    public void testGroupsWithSpaces() throws Exception {
        if (true) return;
        Group group = new Group();
        group.setName(groupName);
        accessControlManager.save(group);
        
        ContextSource factory = (ContextSource) applicationContext.getBean("initialDirContextFactory");
        LdapTemplate ldapTemplate = new LdapTemplate(factory);
        
        ldapTemplate.executeReadWrite(new ContextExecutor() {
            public Object executeWithContext(DirContext ctx) throws NamingException {
                BasicAttributes atts = new BasicAttributes();
                atts.put("test", "value");
                atts.put("objectClass", "groupOfUniqueNames");
                atts.put("uniqueMember", "0.9.2342.19200300.100.1.1=admin,2.5.4.11=system");
                
                ctx.createSubcontext(name, atts);
                ctx.close();
                return null;
            }
        });
        
        logout();
        login("admin", "secret");
        User user = SecurityUtils.getCurrentUser();
        assertEquals(2, user.getGroups().size());
    }
    
    @Override
    protected void onTearDown() throws Exception {
        LdapTemplate ldapTemplate = new LdapTemplate(initialDirContextFactory);
        
        ldapTemplate.executeReadWrite(new ContextExecutor() {
            public Object executeWithContext(DirContext ctx) throws NamingException {
                try {
                    ctx.destroySubcontext(name);
                } catch (Exception e) {
                }
                ctx.close();
                return null;
            }
            
        });
        
        LdapService svc = (LdapService) applicationContext.getBean("ldapService");
        svc.stop();
        
        super.onTearDown();
    }

    protected String getPassword() {
        return "secret";
    }
    
    @Override
    protected ConfigurableApplicationContext createApplicationContext(
        String[] locations) {
    if (System.getProperty("basedir") == null) {
        System.setProperty("basedir", "");
    }

    // Apache DS uses XBean
    return new ClassPathXmlApplicationContext(locations);
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml",
                              "/META-INF/applicationContext-cache.xml",
                              "/META-INF/applicationContext-spring-security.xml",
                              "/META-INF/applicationContext-test.xml",
                              "/server.xml",
                              "classpath*:/META-INF/galaxy-applicationContext.xml"};
    }

}
