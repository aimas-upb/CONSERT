package org.aimas.consert.model.constraint.operations;

import org.aimas.consert.engine.EventTracker;
import org.aimas.consert.engine.TrackedAssertionStore;
import org.aimas.consert.model.content.ContextAssertion;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

public class ReplaceAssertionOperation implements IResolutionOperation {

    private ContextAssertion cachedAssertion;
    private FactHandle cachedAssertionHandle;
    private boolean wasTracked;

    private ContextAssertion newAssertion;

    private EventTracker eventTracker;

    public ReplaceAssertionOperation(EventTracker eventTracker,
                                     ContextAssertion cachedAssertion, FactHandle cachedAssertionHandle,
                                     boolean wasTracked,
                                     ContextAssertion newAssertion) {
        this.eventTracker = eventTracker;

        this.cachedAssertion = cachedAssertion;
        this.cachedAssertionHandle = cachedAssertionHandle;
        this.wasTracked = wasTracked;
        this.newAssertion = newAssertion;
    }

    @Override
    public void apply() {
        KieSession kSession = eventTracker.getKnowledgeSession();
        TrackedAssertionStore trackedAssertionStore = eventTracker.getTrackedAssertionStore();

        kSession.getEntryPoint(cachedAssertion.getStreamName()).delete(cachedAssertionHandle);

        if (wasTracked)
            trackedAssertionStore.remove(cachedAssertion);
    }

    @Override
    public void reverse() {

    }
}
