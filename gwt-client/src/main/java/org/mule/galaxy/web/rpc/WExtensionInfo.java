package org.mule.galaxy.web.rpc;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class WExtensionInfo implements IsSerializable, Comparable {
    private String id;
    private List<String> configurationKeys;
    private String description;
    private boolean multivalueSupported;
    
    public WExtensionInfo(String id, String description, List<String> configurationKeys, boolean multivalueSupported) {
        super();
        this.configurationKeys = configurationKeys;
        this.description = description;
        this.id = id;
        this.multivalueSupported = multivalueSupported;
    }
    public WExtensionInfo() {
        super();
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public List<String> getConfigurationKeys() {
        return configurationKeys;
    }
    public void setConfigurationKeys(List<String> configurationKeys) {
        this.configurationKeys = configurationKeys;
    }
    public String getDescription() {
        return description;
    }
    public int compareTo(Object arg0) {
        return getDescription().compareTo(((WExtensionInfo) arg0).getDescription());
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public boolean isMultivalueSupported() {
        return multivalueSupported;
    }
    public void setMultivalueSupported(boolean multivalueSupported) {
        this.multivalueSupported = multivalueSupported;
    }
    
    
}
