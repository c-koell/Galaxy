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

package org.mule.galaxy.web.client.util;

import org.mule.galaxy.web.rpc.WWorkspace;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;

import java.util.Collection;
import java.util.Iterator;

public class WorkspacesListBox extends Composite {

    private ListBox workspacesLB;

    public WorkspacesListBox(Collection workspaces, 
                             String childrenToHideId, 
                             String selectedWorkspaceId,
                             boolean allowNoWorkspace) {
        super();
        
        workspacesLB = new ListBox();
        
        if (allowNoWorkspace) {
            workspacesLB.addItem("[No parent]");
        }
        
        addWorkspaces(workspaces, workspacesLB, selectedWorkspaceId, childrenToHideId);
        
        if ("".equals(selectedWorkspaceId) || selectedWorkspaceId == null) {
            workspacesLB.setSelectedIndex(0);
        }
        
        initWidget(workspacesLB);
    }

    private void addWorkspaces(final Collection workspaces, 
                               ListBox workspacesLB, 
                               String selectedWorkspaceId,
                               String childrenToHideId) {
        for (Iterator itr = workspaces.iterator(); itr.hasNext();) {
            WWorkspace w = (WWorkspace) itr.next();
            
            if (childrenToHideId == null || !childrenToHideId.equals(w.getId())) {
                workspacesLB.addItem(w.getPath(), w.getId());
                
                if (w.getId().equals(selectedWorkspaceId)) {
                    workspacesLB.setSelectedIndex(workspacesLB.getItemCount() - 1);
                }
                
                Collection children = w.getWorkspaces();
                if (children != null && children.size() > 0) {
                    addWorkspaces(children, workspacesLB, selectedWorkspaceId, childrenToHideId);
                }
            }
        }
    }

    public String getSelectedValue() {
        return workspacesLB.getValue(workspacesLB.getSelectedIndex());
    }

    public void setName(String string) {
        workspacesLB.setName(string);
    }
}