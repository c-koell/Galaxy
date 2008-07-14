package org.mule.galaxy.event.listener.activity;

import org.mule.galaxy.activity.ActivityManager;
import static org.mule.galaxy.event.DefaultEvents.LIFECYCLE_TRANSITION;
import org.mule.galaxy.event.LifecycleTransitionEvent;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

import java.text.MessageFormat;

@BindToEvent(LIFECYCLE_TRANSITION)
public class LifecycleTransitionEventListener extends AbstractActivityLoggingListener {

    @OnEvent
    public void onEvent(LifecycleTransitionEvent event) {
        final String message = MessageFormat.format("Artifact {0} (version {1}) was transitioned to phase {2} " +
                                                    "in lifecycle {3}",
                                                    event.getArtifactPath(), event.getVersionLabel(),
                                                    event.getNewPhaseName(), event.getLifecycleName());
        getActivityManager().logActivity(event.getUser(), message, ActivityManager.EventType.INFO);
    }
}