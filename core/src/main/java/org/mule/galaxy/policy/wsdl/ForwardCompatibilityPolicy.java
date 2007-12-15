package org.mule.galaxy.policy.wsdl;

import java.util.Set;

import javax.wsdl.Definition;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.policy.Approval;
import org.mule.galaxy.policy.ArtifactPolicy;
import org.mule.galaxy.wsdl.diff.DifferenceEvent;
import org.mule.galaxy.wsdl.diff.DifferenceListener;
import org.mule.galaxy.wsdl.diff.WsdlDiff;

public class ForwardCompatibilityPolicy extends AbstractWsdlVersioningPolicy {
    private static final String WSDL_FORWARD_COMPAT = "wsdl-forward-compat";

    public String getId() {
        return WSDL_FORWARD_COMPAT;
    }
    public String getDescription() {
        return "Enforces restrictions to ensure all new WSDL versions are forward compatabile.";
    }

    public String getName() {
        return "WSDL Forward Compatability";
    }

    protected void check(final Approval app, DifferenceEvent event) {
        if (!event.isForwardCompatabile()) {
            app.getMessages().add(event.getDescription());
            app.setApproved(false);
        }
    }
}
