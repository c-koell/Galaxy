package org.mule.galaxy.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.Identifiable;
import org.mule.galaxy.extension.Extension;
import org.mule.galaxy.mapping.OneToMany;

public class PropertyDescriptor implements Identifiable {
    private String id;
    private String property;
    private String description;
    private boolean multivalued;
    private Extension extension;
    private List<Class> appliesTo;
    private Map<String, String> configuration;
    private boolean index;
    
    public PropertyDescriptor(String property, String description, boolean multivalued, boolean index) {
        super();
        this.property = property;
        this.description = description;
        this.multivalued = multivalued;
	this.index = index;
    }

    public PropertyDescriptor() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isMultivalued() {
        return multivalued;
    }

    public void setMultivalued(boolean multivalued) {
        this.multivalued = multivalued;
    }

    public Extension getExtension() {
        return extension;
    }

    public void setExtension(Extension extension) {
        this.extension = extension;
    }

    @OneToMany(treatAsField=true)
    public List<Class> getAppliesTo() {
        return appliesTo;
    }

    public void setAppliesTo(List<Class> appliesTo) {
        this.appliesTo = appliesTo;
    }
    
    public void addAppliesTo(Class c) {
	if (appliesTo == null) {
	    appliesTo = new ArrayList<Class>();
	}
	appliesTo.add(c);
    }

    public Map<String, String> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map<String, String> configuration) {
        this.configuration = configuration;
    }

    public boolean isIndex() {
        return index;
    }

    public void setIndex(boolean index) {
        this.index = index;
    }
    
}
