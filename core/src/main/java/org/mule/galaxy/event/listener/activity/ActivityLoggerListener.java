package org.mule.galaxy.event.listener.activity;

import org.mule.galaxy.activity.ActivityManager;
import static org.mule.galaxy.event.DefaultEvents.WORKSPACE_CREATED;
import static org.mule.galaxy.event.DefaultEvents.WORKSPACE_DELETED;
import org.mule.galaxy.event.GalaxyEvent;
import org.mule.galaxy.event.WorkspaceCreatedEvent;
import org.mule.galaxy.event.WorkspaceDeletedEvent;
import org.mule.galaxy.event.annotation.BindToEvents;

@BindToEvents({
        WORKSPACE_CREATED,
        WORKSPACE_DELETED})
// TODO refactor this
public class ActivityLoggerListener {

    private ActivityManager activityManager;

    public void internalOnEvent(final WorkspaceCreatedEvent e) {
        System.out.println("WORKSPACE CREATED");
    }

    public void internalOnEvent(final WorkspaceDeletedEvent e) {
        activityManager.logActivity(e.getUser(), " >listener< " + e.getMessage(), ActivityManager.EventType.INFO);
        System.out.println("WORKSPACE DELETED");
    }


    public void onEvent(final GalaxyEvent event) {
        if (event instanceof WorkspaceCreatedEvent) {
            internalOnEvent((WorkspaceCreatedEvent) event);
        } else if (event instanceof WorkspaceDeletedEvent) {
            internalOnEvent((WorkspaceDeletedEvent) event);
        } else {
            System.out.println("NOT SUPPORTED");
        }

    }

    public ActivityManager getActivityManager() {
        return activityManager;
    }

    public void setActivityManager(final ActivityManager activityManager) {
        this.activityManager = activityManager;
    }
}