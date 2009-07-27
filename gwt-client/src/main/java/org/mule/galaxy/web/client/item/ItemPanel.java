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

package org.mule.galaxy.web.client.item;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

import org.mule.galaxy.web.client.AbstractFlowComposite;
import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.admin.PolicyPanel;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.ShowableTabListener;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ItemInfo;
import org.mule.galaxy.web.rpc.SecurityService;


/**
 * Contains:
 * - BasicArtifactInfo
 * - Service dependencies
 * - Depends on...
 * - Comments
 * - Governance tab
 * (with history)
 * - View Artiact
 */
public class ItemPanel extends AbstractFlowComposite {

    private Galaxy galaxy;
    private ItemInfo info;
    private int selectedTab = -1;
    private String itemId;
    private List<String> params;
    private ErrorPanel errorPanel;
    
    public ItemPanel(Galaxy galaxy, ErrorPanel errorPanel) {
        this.galaxy = galaxy;
        this.errorPanel = errorPanel;
    }

    @Override
    public void showPage(List<String> params) {
        this.params = params;
        panel.clear();
        panel.add(new Label("Loading..."));

        if (params.size() > 0) {
            itemId = params.get(0);
        }

        if (params.size() >= 2) {
            selectedTab = new Integer(params.get(1)).intValue();
        } else {
            selectedTab = 0;
        }

        if (itemId != null) {
            fetchItem();
        } 
    }

    private void fetchItem() {
        AbstractCallback callback = new AbstractCallback(errorPanel) {
            public void onSuccess(Object o) {
                info = (ItemInfo) o;

                init();
            }
        };

        galaxy.getRegistryService().getItemInfo(itemId, true, callback);
    }

    private void init() {
        panel.clear();
//        initLinks();
        initTabs();
    }
    
   
    private void initTabs() {
        ContentPanel cp = new ContentPanel();
    
        final TabPanel tabPanel = new TabPanel();
        tabPanel.setStyleName("x-tab-panel-header_sub1");
        tabPanel.setAutoWidth(true);
        tabPanel.setAutoHeight(true);
    
        TabItem items = new TabItem("Items");
        items.add(new Label("blank"));
        tabPanel.add(items);
        
        TabItem infoTab = new TabItem("Info");
        infoTab.add(new ItemInfoPanel(galaxy, errorPanel, info, this, params));
        tabPanel.add(infoTab);

        if (galaxy.hasPermission("MANAGE_POLICIES") && info.isLocal()) {
            TabItem tab = new TabItem("Policies");
            tab.add(new PolicyPanel(errorPanel, galaxy, itemId));
            tabPanel.add(tab);
        }

        if (galaxy.hasPermission("MANAGE_GROUPS") && info.isLocal()) {
            TabItem tab = new TabItem("Security");
            tab.add(new ItemGroupPermissionPanel(galaxy, errorPanel, info.getId(), SecurityService.ITEM_PERMISSIONS));
            tabPanel.add(tab);
        }
        
        /**
         * Lazily initialize the panels with the proper parameters.
         */
        tabPanel.addListener(Events.Select, new ShowableTabListener(params));
        
        cp.add(tabPanel);
        panel.add(cp);
        
        

        if (selectedTab > -1) {
            tabPanel.setSelection(tabPanel.getItem(selectedTab));
        } else {
            tabPanel.setSelection(tabPanel.getItem(0));
        }
    }

    public void initLinks() {
        String style = "artifactToolbarItemFirst";

        if (info.isModifiable()) {
            Image addImg = new Image("images/add_obj.gif");
            addImg.addClickListener(new ClickListener() {
                public void onClick(Widget w) {
                    w.addStyleName("gwt-Hyperlink");

                    History.newItem("add-item/" + info.getId());
                }
            });

            Hyperlink addLink = new Hyperlink("New", "add-item/" + info.getId());
            InlineFlowPanel p = asHorizontal(addImg, new Label(" "), addLink);
            p.setStyleName(style);
            style = "artifactToolbarItem";
        }

        if (info.isDeletable()) {
            ClickListener cl = new ClickListener() {
                public void onClick(Widget arg0) {
                    warnDelete();
                }
            };
            Image img = new Image("images/delete_config.gif");
            img.setStyleName("icon-baseline");
            img.addClickListener(cl);
            Hyperlink hl = new Hyperlink("Delete", "artifact/" + info.getId());
            hl.addClickListener(cl);

            InlineFlowPanel p = asHorizontal(img, new Label(" "), hl);
            p.setStyleName(style);
            style = "artifactToolbarItem";
        }

        ClickListener cl = new ClickListener() {

            public void onClick(Widget sender) {
                Window.open(info.getArtifactFeedLink(), null, "scrollbars=yes");
            }
        };

        Image img = new Image("images/feed-icon.png");
//        img.setStyleName("feed-icon");
        img.setTitle("Versions Atom Feed");
        img.addClickListener(cl);
        img.setStyleName("icon-baseline");

        Hyperlink hl = new Hyperlink("Feed", "feed/" + info.getId());
        hl.addClickListener(cl);

        InlineFlowPanel p = asHorizontal(img, new Label(" "), hl);
        p.setStyleName(style);
        style = "artifactToolbarItem";

        // spacer to divide the actions
        SimplePanel spacer = new SimplePanel();
        spacer.addStyleName("hr");
    }

    protected void warnDelete() {

        final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent ce) {
                com.extjs.gxt.ui.client.widget.button.Button btn = ce.getButtonClicked();

                if (Dialog.YES.equals(btn.getItemId())) {
                    galaxy.getRegistryService().delete(info.getId(), new AbstractCallback(errorPanel) {
                        public void onSuccess(Object arg0) {
                            galaxy.setMessageAndGoto("browse", "Item was deleted.");
                        }
                    });
                }
            }
        };

        MessageBox.confirm("Confirm", "Are you sure you want to delete this item?", l);
    }

}
