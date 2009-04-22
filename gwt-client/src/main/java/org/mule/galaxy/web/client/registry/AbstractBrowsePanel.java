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

package org.mule.galaxy.web.client.registry;

import java.util.List;

import org.mule.galaxy.web.client.AbstractErrorShowingComposite;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.util.Toolbox;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.WSearchResults;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * The basis for any form that lists out groups of artifacts.
 */
public abstract class AbstractBrowsePanel extends AbstractErrorShowingComposite {
    protected Toolbox artifactTypesBox;
    protected RegistryServiceAsync service;
    protected ArtifactListPanel artifactListPanel;
    protected FlowPanel currentTopPanel;
    protected final Galaxy galaxy;
    protected RegistryMenuPanel menuPanel;
    private boolean first = true;
    protected int resultStart;

    public AbstractBrowsePanel(Galaxy galaxy) {
        this.galaxy = galaxy;
        this.service = galaxy.getRegistryService();

        menuPanel = createRegistryMenuPanel();

        initWidget(menuPanel);
    }


    protected RegistryMenuPanel createRegistryMenuPanel() {
        return new RegistryMenuPanel(galaxy);
    }

    public void onShow(List<String> params) {
        int resultStartParamIdx = getResultStartParameterIndex();
        
        if (params.size() > resultStartParamIdx) {
            try {
                resultStart = Integer.valueOf(params.get(resultStartParamIdx)).intValue();
            } catch (NumberFormatException e) {
            }

            if (resultStart < 0) {
                resultStart = 0;
            }
        } else {
            resultStart = 0;
        }

        if (first) {
            artifactListPanel = new ArtifactListPanel(this, galaxy);
        }

        artifactListPanel.setResultStart(resultStart);

        if (first) {
            initializeMenuAndTop();

            first = false;
        }

        menuPanel.onShow();
        refresh();

        if (currentTopPanel != null) {
            menuPanel.setTop(currentTopPanel);
        }
    }

    protected int getResultStartParameterIndex() {
        return 1;
    }

    protected abstract String getHistoryToken();

    protected void initializeMenuAndTop() {

    }

    protected void initializeBulkEdit() {

    }

    public void refresh() {
//        refreshArtifactTypes();

        menuPanel.loadViews();
    }

    public void refreshArtifacts() {
        menuPanel.setMain(artifactListPanel);
        refreshArtifacts(artifactListPanel.getResultStart(),
                         artifactListPanel.getMaxResults());
    }

    public void refreshArtifacts(int resultStart, int maxResults) {
        artifactListPanel.showLoadingMessage();
        AbstractCallback callback = new AbstractCallback(this) {

            public void onSuccess(Object o) {
                artifactListPanel.initArtifacts((WSearchResults) o);
            }

            public void onFailure(Throwable caught) {
                menuPanel.setMessage(caught.getMessage());
                if (artifactListPanel != null) {
                    artifactListPanel.clear();
                }
            }
        };

        fetchArtifacts(resultStart, maxResults, callback);
    }

    @Override
    public void onHide() {
        artifactListPanel.clear();
    }

    // TODO
    protected int getErrorPanelPosition() {
        return 0;
    }

    protected abstract void fetchArtifacts(int resultStart, int maxResults, AbstractCallback callback);
}
