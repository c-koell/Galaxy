package org.mule.galaxy.event.listener.activity;

import org.mule.galaxy.activity.ActivityManager;
import static org.mule.galaxy.event.DefaultEvents.ENTRY_VERSION_DELETED;
import org.mule.galaxy.event.EntryVersionDeletedEvent;
import org.mule.galaxy.event.annotation.Async;
import org.mule.galaxy.event.annotation.BindToEvent;
import org.mule.galaxy.event.annotation.OnEvent;

import java.text.MessageFormat;

@BindToEvent(ENTRY_VERSION_DELETED)
public class EntryVersionDeletedEventListener extends AbstractActivityLoggingListener {

    @OnEvent
    @Async
    public void onEvent(EntryVersionDeletedEvent event) {
        final String message = MessageFormat.format(
                "Version {0} of entry {1} was deleted",
                event.getVersionLabel(), event.getArtifactPath());

        getActivityManager().logActivity(event.getUser(), message, ActivityManager.EventType.INFO);
    }
}