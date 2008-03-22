package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import org.mule.galaxy.web.client.AbstractMenuPanel;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.Toolbox;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.SecurityServiceAsync;
import org.mule.galaxy.web.rpc.WArtifactType;
import org.mule.galaxy.web.rpc.WGroup;
import org.mule.galaxy.web.rpc.WLifecycle;

public class AdministrationPanel extends AbstractMenuPanel {

    private SecurityServiceAsync userService;
    private Hyperlink add;
    private RegistryServiceAsync registryService;

    public AdministrationPanel(Galaxy galaxy) {
        super(galaxy);
        this.registryService = galaxy.getRegistryService();
        this.userService = galaxy.getSecurityService();

        Toolbox manageBox = new Toolbox(false);
        manageBox.setTitle("Manage");
        addMenuItem(manageBox);

        final AdministrationPanel adminPanel = this;
        
        if (galaxy.hasPermission("MANAGE_ARTIFACT_TYPES")) {
            Hyperlink link = new Hyperlink("Artifact Types", "artifact-types");
            createPageInfo(link.getTargetHistoryToken(), new ArtifactTypeListPanel(adminPanel));
    
            add = new Hyperlink("Add", "add-artifact-type");
            createPageInfo(add.getTargetHistoryToken(), new ArtifactTypeForm(adminPanel, new WArtifactType(), true));
            
            createDivWithAdd(manageBox, link);
        }        

        if (galaxy.hasPermission("MANAGE_GROUPS")) {
            Hyperlink link = new Hyperlink("Groups", "groups");
            createPageInfo(link.getTargetHistoryToken(), new GroupPanel(adminPanel));
    
            add = new Hyperlink("Add", "add-group");
            createPageInfo(add.getTargetHistoryToken(), new GroupForm(adminPanel, new WGroup(), true));
            
            createDivWithAdd(manageBox, link);
        }
        
        if (galaxy.hasPermission("MANAGE_LIFECYCLES")) {
            Hyperlink link = new Hyperlink("Lifecycles", "lifecycles");
            createPageInfo(link.getTargetHistoryToken(), new LifecycleListPanel(adminPanel));
    
            add = new Hyperlink("Add", "add-lifecycle");
            createPageInfo(add.getTargetHistoryToken(), new LifecycleForm(adminPanel, new WLifecycle(), true));
            
            createDivWithAdd(manageBox, link);
        }
        
        if (galaxy.hasPermission("MANAGE_INDEXES")) {
            Hyperlink link = new Hyperlink("Indexes", "indexes");
            manageBox.add(link);
            createPageInfo(link.getTargetHistoryToken(), new IndexListPanel(adminPanel));
    
            add = new Hyperlink("Add", "add-index");
            createPageInfo(add.getTargetHistoryToken(), new IndexForm(adminPanel));
    
            createDivWithAdd(manageBox, link);
        }
        
        if (galaxy.hasPermission("MANAGE_POLICIES")) {
            Hyperlink link = new Hyperlink("Policies", "policies");
            createPageInfo(link.getTargetHistoryToken(), new PolicyPanel(adminPanel, registryService));
            manageBox.add(link);
        }

        if (galaxy.hasPermission("MANAGE_USERS")) {
            Hyperlink link = new Hyperlink("Users", "users");
            createPageInfo(link.getTargetHistoryToken(), new UserListPanel(adminPanel));
            
            add = new Hyperlink("Add", "add-user-form");
            createPageInfo(add.getTargetHistoryToken(), new UserForm(adminPanel));
    
            createDivWithAdd(manageBox, link);
        }
    }

    private void createDivWithAdd(Toolbox manageBox, Hyperlink link) {
        InlineFlowPanel item = new InlineFlowPanel();
        item.add(link);
        item.add(new Label(" ["));
        item.add(add);
        item.add(new Label("]"));
   
        manageBox.add(item);
    }

    public void showUsers() {
        History.newItem("users");
    }

    public SecurityServiceAsync getUserService() {
        return userService;
    }

    public RegistryServiceAsync getRegistryService() {
        return registryService;
    }
}
