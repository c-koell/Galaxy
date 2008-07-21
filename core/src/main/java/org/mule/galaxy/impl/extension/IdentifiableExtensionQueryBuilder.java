package org.mule.galaxy.impl.extension;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.mule.galaxy.Dao;
import org.mule.galaxy.Identifiable;
import org.mule.galaxy.impl.jcr.query.QueryBuilder;
import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.query.OpRestriction.Operator;

public class IdentifiableExtensionQueryBuilder extends QueryBuilder {

    private String[] properties;
    private Dao dao;
    private String root;
    
    public IdentifiableExtensionQueryBuilder(IdentifiableExtension e) throws IntrospectionException {
	super(false);
	
	dao = e.getDao();
	Class<?> typeClass = dao.getTypeClass();
	
	initProperties(typeClass);
    }

    private void initProperties(Class<?> type) throws IntrospectionException {
	root = type.getSimpleName();
	
	root = root.substring(0, 1).toLowerCase() + root.substring(1);
	
	List<String> listOfProps = new ArrayList<String>();
        BeanInfo info = Introspector.getBeanInfo(type);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            listOfProps.add(root + "." + pd.getName());
        }
        properties = listOfProps.toArray(new String[0]);
    }

    @Override
    public String[] getProperties() {
	return properties;
    }

    public void build(StringBuilder query, String property, Object right, boolean not, Operator operator)
        throws QueryException {
        
        List<String> matches = getMatches(right, property, operator);
        
        if (matches.size() == 0) {
            return;
        }
        
        if (not) {
            query.append("not(");
        }
        
        if (matches.size() > 1) {
            Collection<?> rightCol = (Collection<?>) right;
            if (rightCol.size() > 0) {
                boolean first = true;
                for (String value : matches) {
                    if (value == null) {
                        continue;
                    }
                    
                    if (first) {
                        query.append("(");
                        first = false;
                    } else {
                        query.append(" or ");
                    }
    
                    query.append("@")
                         .append(root)
                         .append("='")
                         .append(value)
                         .append("'");
                }
                query.append(")");
            }
        } else if (matches.size() == 1) {
            query.append("@")
                .append(root)
                .append("='")
                .append(matches.get(0))
                .append("'");
        }
        
        if (not) {
            query.append(")");
        }
    }
    
    @SuppressWarnings("unchecked")
    protected List<String> getMatches(Object o, String property, Operator operator) throws QueryException {
	property = property.substring(property.indexOf('.') + 1);
	if (Operator.EQUALS == operator) {
	    List results = dao.find(property, o.toString());
	   
	    return asIds((List<Identifiable>) results);
	}
	return Collections.emptyList();
    }

    private List<String> asIds(List<Identifiable> results) {
	ArrayList<String> ids = new ArrayList<String>();
	for (Identifiable result : results) {
	    ids.add(result.getId());
	}
	
	return ids;
    }

}
