package org.mule.galaxy.impl.event;

import org.mule.galaxy.event.GalaxyEvent;
import org.mule.galaxy.event.annotation.BindToEvent;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Delegates to a single method marked with the {@link org.mule.galaxy.event.annotation.OnEvent} annotation.
 */
class DelegatingSingleEventListener extends AbstractDelegatingGalaxyEventListener {
    private final Method method;

    public DelegatingSingleEventListener(final Annotation annotation, final Object listenerCandidate, final Method method, final ThreadPoolTaskExecutor executor) {
        super(listenerCandidate, executor);
        this.method = method;
        validateMethodParams(method);
        final String eventName = ((BindToEvent) annotation).value() + "Event";
        final String callbackParam = method.getParameterTypes()[0].getSimpleName();
        if (!callbackParam.equals(eventName)) {
            throw new IllegalArgumentException(
                    String.format("Listener %s is bound to the %s, but " +
                                  "callback method param %s doesn't match it.",
                                  listenerCandidate.getClass().getName(),
                                  eventName, callbackParam));
        }
    }

    public void onEvent(final GalaxyEvent event) {
        // no extra checks for single-event listener
        internalOnEvent(event, method);
    }

    public Object getDelegateListener() {
        return delegate;
    }

}
