package org.aimas.consert.engine.constraint.operations;

import org.aimas.consert.engine.core.EventTracker;
import org.aimas.consert.engine.core.TrackedAssertionStore;
import org.aimas.consert.model.content.ContextAssertion;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

public class InsertAssertionOperation implements IResolutionOperation {

    private ContextAssertion newAssertion;
    private boolean isTracked;

    private FactHandle newAssertionHandle;
    private TrackedAssertionStore.TrackedAssertionData newAssertionData;

    private EventTracker eventTracker;

    public InsertAssertionOperation(EventTracker eventTracker,
                                    ContextAssertion newAssertion,
                                     boolean isTracked) {
        this.eventTracker = eventTracker;

        this.isTracked = isTracked;
        this.newAssertion = newAssertion;
    }

    @Override
    public void apply() {
        // get knowledge session and TrackedAssertion store references
        KieSession kSession = eventTracker.getKnowledgeSession();
        TrackedAssertionStore trackedAssertionStore = eventTracker.getTrackedAssertionStore();

        // add the new assertion
        newAssertionHandle =
                kSession.getEntryPoint(newAssertion.getStreamName()).insert(newAssertion);

        // if it is supposed to be tracked, add it to the trackedAssertionStore and cache the data
        if (isTracked) {
            newAssertionData = trackedAssertionStore.trackAssertion(newAssertion, newAssertionHandle,
                    kSession.getEntryPoint(newAssertion.getStreamName()));
        }
    }

    @Override
    public void reverse() {
        // remove the assertion from both knowledge base, as well as trackedAssertionStore (if ever it was stored)
        KieSession kSession = eventTracker.getKnowledgeSession();
        TrackedAssertionStore trackedAssertionStore = eventTracker.getTrackedAssertionStore();

        kSession.getEntryPoint(newAssertion.getStreamName()).delete(newAssertionHandle);
        if (isTracked && newAssertionData != null) {
            trackedAssertionStore.removeAssertion(newAssertionData);
        }
    }

}
