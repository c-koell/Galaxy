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

package org.mule.galaxy.web.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

public class MenuPanel extends AbstractErrorShowingComposite {

    private DockPanel panel;
    private FlowPanel leftMenuContainer;
    private Widget mainWidget;
    private FlowPanel topPanel;
    private Widget topWidget;
    private FlowPanel leftMenu;
    private FlowPanel centerPanel;
    
    public MenuPanel() {
        super();
        
        panel = new DockPanel();
        panel.setSpacing(0);

        leftMenu = new FlowPanel() {
            protected void onLoad() {

                Element br = DOM.createElement("br");
                DOM.setElementAttribute(br, "class", "clearit");
                DOM.appendChild(DOM.getParent(this.getElement()), br);
            }
        };
        leftMenu.setStyleName("left-menu");
        
        panel.add(leftMenu, DockPanel.WEST);
        
        leftMenuContainer = new FlowPanel(){

            protected void onLoad() {

                Element br = DOM.createElement("br");
                DOM.setElementAttribute(br, "class", "clearit");
                DOM.appendChild(DOM.getParent(this.getElement()), br);
            }
            
        };
        leftMenuContainer.setStyleName("left-menu-container");
        
        leftMenu.add(leftMenuContainer);

        centerPanel = new FlowPanel();
        panel.add(centerPanel, DockPanel.CENTER);
        panel.setCellWidth(centerPanel, "100%");
        
        centerPanel.add(getMainPanel());
        
        topPanel = new FlowPanel();
        topPanel.setStyleName("top-panel");
        
        initWidget(panel);
    }
    
    public void onShow(List params) {
        if (mainWidget instanceof AbstractComposite) {
            ((AbstractComposite) mainWidget).onShow(params);
        }
    }

    public void addMenuItem(Widget widget) {
        leftMenuContainer.add(widget);
    }
    
    public void removeMenuItem(Widget widget) {
        leftMenuContainer.remove(widget);
    }
    
    public void setMain(Widget widget) {
        FlowPanel mainPanel = getMainPanel();
        
        if (mainWidget != null) {
            mainPanel.remove(mainWidget);
        }
        
        clearErrorMessage();
        
        this.mainWidget = widget;
        
        mainPanel.add(widget);
    }    
    
    public void setTop(Widget widget) {
        if (centerPanel.getWidgetIndex(topPanel) == -1) {
            centerPanel.insert(topPanel, 0);
        }
        
        if (topWidget != null)
            topPanel.remove(topWidget);
        
        if (widget instanceof AbstractComposite) {
            ((AbstractComposite)widget).onShow();
        }
        topWidget = widget;
        if (widget != null) {
            topPanel.add(widget);
        }
    }
    
    public Widget getMain() {
        return mainWidget;
    };
}
