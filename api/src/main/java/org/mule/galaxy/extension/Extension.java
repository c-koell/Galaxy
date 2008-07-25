package org.mule.galaxy.extension;

import java.util.Collection;

import org.mule.galaxy.Identifiable;
import org.mule.galaxy.Item;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.type.PropertyDescriptor;

public interface Extension extends Identifiable {

    /**
     * Properties could be lifecycle information, associated contacts, links, etc.
     * This method will store them in  
     * @param entry
     * @param properties
     */
    Object getInternalValue(Item entry, PropertyDescriptor pd, Object externalValue) throws PolicyException;
    
    Object getExternalValue(Item entry, PropertyDescriptor pd, Object storedValue);
    
    boolean isMultivalueSupported();
}
