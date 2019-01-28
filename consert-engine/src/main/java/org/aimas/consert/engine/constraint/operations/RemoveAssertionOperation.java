package org.aimas.consert.engine.constraint.operations;

import org.aimas.consert.engine.core.EventTracker;
import org.aimas.consert.engine.core.TrackedAssertionStore;
import org.aimas.consert.model.content.ContextAssertion;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

public class RemoveAssertionOperation implements IResolutionOperation {

    private ContextAssertion cachedAssertion;
    private FactHandle cachedAssertionHandle;
    private boolean wasTracked;

    private EventTracker eventTracker;

    public RemoveAssertionOperation(EventTracker eventTracker,
                                    ContextAssertion cachedAssertion,
                                    FactHandle cachedAssertionHandle,
                                    boolean wasTracked) {
        this.eventTracker = eventTracker;

        this.wasTracked = wasTracked;
        this.cachedAssertion = cachedAssertion;
        this.cachedAssertionHandle = cachedAssertionHandle;
    }

    @Override
    public void apply() {
        // get knowledge session and TrackedAssertion store references
        KieSession kSession = eventTracker.getKnowledgeSession();
        TrackedAssertionStore trackedAssertionStore = eventTracker.getTrackedAssertionStore();

        // delete the assertion and cache it
        kSession.getEntryPoint(cachedAssertion.getStreamName()).delete(cachedAssertionHandle);

        // if it was supposed to be tracked, remove it from the trackedAssertionStore
        if (wasTracked) {
            trackedAssertionStore.removeAssertion(cachedAssertion);
        }
    }

    @Override
    public void reverse() {
        // add the cached assertion back to the knowledge base, as well as to trackedAssertionStore
        // (if ever it was tracked)
        KieSession kSession = eventTracker.getKnowledgeSession();
        TrackedAssertionStore trackedAssertionStore = eventTracker.getTrackedAssertionStore();

        FactHandle fh = kSession.getEntryPoint(cachedAssertion.getStreamName()).insert(cachedAssertion);
        if (wasTracked) {
            trackedAssertionStore.trackAssertion(cachedAssertion, fh,
                    kSession.getEntryPoint(cachedAssertion.getStreamName()));
        }
    }
}
