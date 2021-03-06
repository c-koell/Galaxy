/*
 * $Id: LicenseHeader-GPLv2.txt 288 2008-01-29 00:59:35Z andrew $
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

package org.mule.galaxy.web.client.admin;

import static org.mule.galaxy.web.client.ClientId.GROUP_LIST_PANEL_USERS_BUTTON_NEW_ID;
import static org.mule.galaxy.web.client.ClientId.GROUP_LIST_PANEL_USERS_GRID_ID;
import static org.mule.galaxy.web.client.ClientId.GROUP_LIST_PANEL_USERS_ID;
import static org.mule.galaxy.web.client.ClientId.GROUP_LIST_PANEL_USERS_SEARCH_ID;

import java.util.ArrayList;
import java.util.List;

import org.mule.galaxy.web.client.ui.button.ToolbarButton;
import org.mule.galaxy.web.client.ui.panel.FullContentPanel;
import org.mule.galaxy.web.client.ui.panel.InlineHelpPanel;
import org.mule.galaxy.web.client.ui.renderer.FauxLinkRenderer;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WUser;
import org.mule.galaxy.web.client.ui.help.AdministrationConstants;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowNumberer;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;




public class UserListPanel extends AbstractAdministrationComposite {
	
	private final AdministrationConstants administrationMessages = (AdministrationConstants) GWT.create(AdministrationConstants.class);

    public UserListPanel(AdministrationPanel a) {
        super(a);
    }

    @Override
    public void doShowPage() {
        super.doShowPage();
        adminPanel.getSecurityService().getUsers(new AbstractCallback<List<WUser>>(adminPanel) {

            public void onCallSuccess(List<WUser> users) {
                panel.clear();
                showUsers(users);
            }
        });
    }

    private void showUsers(List<WUser> users) {
        ContentPanel contentPanel = new FullContentPanel();
        contentPanel.setId(GROUP_LIST_PANEL_USERS_ID);
        contentPanel.setHeading(administrationMessages.users());

        // Add inline help string and widget.
        contentPanel.setTopComponent(new InlineHelpPanel(administrationMessages.usersTip(), 19));

        BeanModelFactory factory = BeanModelLookup.get().getFactory(WUser.class);

        List<BeanModel> list = factory.createModel(users);
        final ListStore<BeanModel> store = new ListStore<BeanModel>();
        store.add(list);

        RowNumberer r = new RowNumberer();
        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
        columns.add(r);
        ColumnConfig userConfig = new ColumnConfig("username", administrationMessages.username(), 100);
        userConfig.setRenderer(new FauxLinkRenderer());
        columns.add(userConfig);
        columns.add(new ColumnConfig("name", administrationMessages.userName(), 200));
        columns.add(new ColumnConfig("email", administrationMessages.emailAddress(), 200));
        ColumnModel cm = new ColumnModel(columns);

        Grid grid = new Grid<BeanModel>(store, cm);
        grid.setId(GROUP_LIST_PANEL_USERS_GRID_ID);
        grid.setStripeRows(true);
        grid.addPlugin(r);
        grid.setAutoWidth(true);
        grid.setAutoHeight(true);
        grid.setAutoExpandColumn("email");
        grid.addListener(Events.CellClick, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                GridEvent ge = (GridEvent) be;
                WUser s = store.getAt(ge.getRowIndex()).getBean();
                History.newItem("users/" + s.getId());
            }
        });

        // Search filter.
        StoreFilterField<BeanModel> filter = new StoreFilterField<BeanModel>() {
            @Override
            protected boolean doSelect(Store<BeanModel> store,
                                       BeanModel parent,
                                       BeanModel record,
                                       String property,
                                       String filter) {
                String name = record.get("name");
                name = name.toLowerCase();

                String username = record.get("username");
                username = username.toLowerCase();

                String email = record.get("email");
                email = email.toLowerCase();

                if (name.indexOf(filter.toLowerCase()) != -1 || username.indexOf(filter.toLowerCase()) != -1
                        || email.indexOf(filter.toLowerCase()) != -1) {
                    return true;
                }
                return false;
            }
        };

        filter.setName(administrationMessages.search());
        filter.setId(GROUP_LIST_PANEL_USERS_SEARCH_ID);
        filter.setFieldLabel(administrationMessages.search());
        filter.setWidth(300);
        filter.setTriggerStyle("x-form-search-trigger");
        filter.addStyleName("x-form-search-field");
        // Bind the filter field to your grid store (grid.getStore())
        filter.bind(store);

        ToolBar toolbar = new ToolBar();
        toolbar.add(filter);
        toolbar.add(new FillToolItem());

        String buttonTip = adminPanel.getGalaxy().getAdministrationConstants().usersNew();
        ToolbarButton button = createToolbarHistoryButton(administrationMessages.newUser(), "users/new", buttonTip);
        button.setId(GROUP_LIST_PANEL_USERS_BUTTON_NEW_ID);
        toolbar.add(button);

        contentPanel.add(toolbar);
        contentPanel.add(grid);

        panel.add(contentPanel);
    }
}
