package org.aimas.consert.engine.constraint.operations;

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
    private FactHandle newAssertionHandle;
    private TrackedAssertionStore.TrackedAssertionData newAssertionData;

    private EventTracker eventTracker;

    public ReplaceAssertionOperation(EventTracker eventTracker,
                                     ContextAssertion cachedAssertion,
                                     FactHandle cachedAssertionHandle,
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
        // get knowledge session and TrackedAssertion store references
        KieSession kSession = eventTracker.getKnowledgeSession();
        TrackedAssertionStore trackedAssertionStore = eventTracker.getTrackedAssertionStore();

        // delete the old assertion - which gets cached
        kSession.getEntryPoint(cachedAssertion.getStreamName()).delete(cachedAssertionHandle);
        if (wasTracked)
            trackedAssertionStore.removeAssertion(cachedAssertion);

        // add the new one
        newAssertionHandle =
                kSession.getEntryPoint(newAssertion.getStreamName()).insert(newAssertion);
        newAssertionData = trackedAssertionStore.trackAssertion(newAssertion, newAssertionHandle,
                kSession.getEntryPoint(newAssertion.getStreamName()));
    }

    @Override
    public void reverse() {
        // get knowledge session and TrackedAssertion store references
        KieSession kSession = eventTracker.getKnowledgeSession();
        TrackedAssertionStore trackedAssertionStore = eventTracker.getTrackedAssertionStore();

        // Step 1) - remove the new Assertion
        // delete the new assertion - having cached the newAssertionHandle
        kSession.getEntryPoint(newAssertion.getStreamName()).delete(newAssertionHandle);

        // remove it from the tracked assertion store
        trackedAssertionStore.removeAssertion(newAssertionData);

        // Step 2) - add back the cached one
        FactHandle fh = kSession.getEntryPoint(cachedAssertion.getStreamName()).insert(cachedAssertion);
        trackedAssertionStore.trackAssertion(cachedAssertion, fh,
                kSession.getEntryPoint(cachedAssertion.getStreamName()));
    }
}
