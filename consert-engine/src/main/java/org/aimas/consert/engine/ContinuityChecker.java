package org.aimas.consert.engine;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.ContextAssertion;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.aimas.consert.engine.TrackedAssertionStore.TrackedAssertionData;

public class ContinuityChecker {
    public static class ContinuityResult {
        boolean hasExtension;

        ContextAssertion existingAssertion;
        FactHandle existingAssertionHandle;

        ContextAssertion extendedAssertion;


        ContinuityResult(boolean hasExtension, ContextAssertion existingEvent, FactHandle existingEventHandle,
                                ContextAssertion extendedAssertion) {
            this.hasExtension = hasExtension;
            this.existingAssertion = existingEvent;
            this.existingAssertionHandle = existingEventHandle;
            this.extendedAssertion = extendedAssertion;
        }


        boolean hasExtension() {
            return hasExtension;
        }

        public void setHasExtension(boolean hasExtension) {
            this.hasExtension = hasExtension;
        }

        ContextAssertion getExistingAssertion() {
            return existingAssertion;
        }

        public void setExistingAssertion(ContextAssertion existingAssertion) {
            this.existingAssertion = existingAssertion;
        }

        FactHandle getExistingAssertionHandle() {
            return existingAssertionHandle;
        }

        public void setExistingAssertionHandle(FactHandle existingAssertionHandle) {
            this.existingAssertionHandle = existingAssertionHandle;
        }

        ContextAssertion getExtendedAssertion() {
            return extendedAssertion;
        }

        public void setExtendedAssertion(ContextAssertion extendedAssertion) {
            this.extendedAssertion = extendedAssertion;
        }

        public String getExistingEventStream() {
            if (existingAssertion != null)
                return existingAssertion.getStreamName();

            return null;
        }

        String getExtendedEventStream() {
            if (extendedAssertion != null)
                return extendedAssertion.getStreamName();

            return null;
        }

        TrackedAssertionData getTrackedAssertionData(KieSession kSession) {
            if (existingAssertionHandle == null)
                return null;

            EntryPoint existingAssertionEntryPoint = kSession.getEntryPoint(existingAssertion.getStreamName());
            return new TrackedAssertionData(existingAssertionHandle, existingAssertionEntryPoint, existingAssertion);
        }
    }


    private EventTracker eventTracker;
    private TrackedAssertionStore trackedAssertionStore;

    public ContinuityChecker(EventTracker eventTracker, TrackedAssertionStore assertionStore) {
        this.eventTracker = eventTracker;
        this.trackedAssertionStore = assertionStore;
    }

    public ContinuityResult check(ContextAssertion newAssertion) {
        // check to see if the newAssertion matches one of the previous stored events by content
        TrackedAssertionData existingAssertionData = trackedAssertionStore.searchTrackedAssertionByContent(newAssertion);

        if (existingAssertionData != null) {
            // if it DOES match any monitored event by content
            FactHandle existingAssertionHandle = existingAssertionData.getExistingHandle();
            ContextAssertion existingAssertion = existingAssertionData.getExistingEvent();

            if (existingAssertion.getAnnotations().allowsAnnotationContinuity(newAssertion.getAnnotations())) {
                // if the event allows continuity, create a content clone of the existing assertion
                // and update its annotations
                ContextAssertion extendedAssertion = existingAssertion.cloneContent();

                AnnotationData extendedAnnotations = existingAssertion.getAnnotations()
                        .applyExtensionOperator(newAssertion.getAnnotations());
                extendedAssertion.setAnnotations(extendedAnnotations);

                return new ContinuityResult(true, existingAssertion, existingAssertionHandle, extendedAssertion);
            } else {
                // if NO annotation continuity (either because of timestamp or confidence)
                System.out.println("NO ANNOTATION EXTENSION FOUND for existing event: " + existingAssertion + ", new event: " + newAssertion);
                return new ContinuityResult(false, existingAssertion, existingAssertionHandle, null);
            }
        }
        else {
            return new ContinuityResult(false, null, null, null);
        }
    }
}
