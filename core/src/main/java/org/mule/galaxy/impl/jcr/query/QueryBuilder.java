package org.mule.galaxy.impl.jcr.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.query.OpRestriction.Operator;

public abstract class QueryBuilder {
    protected Collection<String> properties = new ArrayList<String>();
    private boolean artifactProperty;
    
    public QueryBuilder(boolean artifactProperty) {
        this(null, artifactProperty);
    }

    public QueryBuilder(String[] properties, boolean artifactProperty) {
        super();
        if (properties != null) {
            Collections.addAll(this.properties, properties);
        }
        this.artifactProperty = artifactProperty;
    }

    public Collection<String> getProperties() {
        return properties;
    }

    /**
     * Will this QueryBuilder be searching properties on the artifact itself?
     * Or on the artifact version node?
     * @return
     */
    public boolean isArtifactProperty() {
        return artifactProperty;
    }

    public abstract void build(StringBuilder query, 
                               String property, 
                               Object right, 
                               boolean not,
                               Operator operator) throws QueryException ;
}