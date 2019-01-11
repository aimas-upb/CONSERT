package org.aimas.consert.engine;

import org.aimas.consert.engine.TrackedAssertionStore.TrackedAssertionData;
import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.ContextAssertion;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;

public class ContinuityChecker {
    public static class ContinuityResult {
        boolean hasExtension;

        ContextAssertion existingAssertion;
        FactHandle existingAssertionHandle;

        ContextAssertion insertedAssertion;
        FactHandle insertedAssertionHandle;
        
        ContextAssertion extendedAssertion;
        FactHandle extendedAssertionHandle;


        ContinuityResult(boolean hasExtension, ContextAssertion existingAssertion, FactHandle existingAssertionHandle,
        						ContextAssertion insertedAssertion, FactHandle insertedAssertionHandle,
                                ContextAssertion extendedAssertion, FactHandle extendedAssertionHandle) {
            this.hasExtension = hasExtension;
            this.existingAssertion = existingAssertion;
            this.existingAssertionHandle = existingAssertionHandle;
            
            this.insertedAssertion = insertedAssertion;
            this.insertedAssertionHandle = insertedAssertionHandle;
            
            this.extendedAssertion = extendedAssertion;
            this.extendedAssertionHandle = extendedAssertionHandle;
        }


        boolean hasExtension() {
            return hasExtension;
        }

        void setHasExtension(boolean hasExtension) {
            this.hasExtension = hasExtension;
        }

        ContextAssertion getExistingAssertion() {
            return existingAssertion;
        }
        
        boolean hasExistingAssertion() {
        	return existingAssertion != null;
        }
        
        void setExistingAssertion(ContextAssertion existingAssertion) {
            this.existingAssertion = existingAssertion;
        }

        FactHandle getExistingAssertionHandle() {
            return existingAssertionHandle;
        }

        void setExistingAssertionHandle(FactHandle existingAssertionHandle) {
            this.existingAssertionHandle = existingAssertionHandle;
        }
        
        void setInsertedAssertion(ContextAssertion insertedAssertion) {
        	this.insertedAssertion = insertedAssertion;
        }
        
        ContextAssertion getInsertedAssertion() {
        	return insertedAssertion;
        }
        
        
        FactHandle getInsertedAssertionHandle() {
			return insertedAssertionHandle;
		}
        
		void setInsertedAssertionHandle(FactHandle insertedAssertionHandle) {
			this.insertedAssertionHandle = insertedAssertionHandle;
		}


		ContextAssertion getExtendedAssertion() {
            return extendedAssertion;
        }

        void setExtendedAssertion(ContextAssertion extendedAssertion) {
            this.extendedAssertion = extendedAssertion;
        }

        
        FactHandle getExtendedAssertionHandle() {
			return extendedAssertionHandle;
		}
        
		void setExtendedAssertionHandle(FactHandle extendedAssertionHandle) {
			this.extendedAssertionHandle = extendedAssertionHandle;
		}
		
		// ==== Generalize between the newAssertion and the extendedAssertion depending on whether an extension exists ====
		ContextAssertion getContinuityAssertion() {
			if (hasExtension) {
				return extendedAssertion;
			}
			
			return insertedAssertion;
		}
		
		FactHandle getContinuityAssertionHandle() {
			if (hasExtension) {
				return extendedAssertionHandle;
			}
			
			return insertedAssertionHandle;
		}
		
		String getContinuityEventStream() {
			if (hasExtension) {
				return extendedAssertion.getStreamName();
			}
			else if (insertedAssertion != null) {
				return insertedAssertion.getStreamName();
			}
			
			return null;
		}
		
		String getExistingEventStream() {
            if (existingAssertion != null)
                return existingAssertion.getStreamName();

            return null;
        }

        String getExtendedEventStream() {
            if (extendedAssertion != null)
                return extendedAssertion.getStreamName();

            return null;
        }

        TrackedAssertionData getExistingAssertionData(KieSession kSession) {
            if (existingAssertionHandle == null)
                return null;

            EntryPoint existingAssertionEntryPoint = kSession.getEntryPoint(existingAssertion.getStreamName());
            return new TrackedAssertionData(existingAssertionHandle, existingAssertionEntryPoint, existingAssertion);
        }
    }


    private EventTracker eventTracker;
    private TrackedAssertionStore trackedAssertionStore;

    public ContinuityChecker(EventTracker eventTracker) {
        this.eventTracker = eventTracker;
        this.trackedAssertionStore = eventTracker.getTrackedAssertionStore();
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

                return new ContinuityResult(true, existingAssertion, existingAssertionHandle, newAssertion, null, extendedAssertion, null);
            } else {
                // if NO annotation continuity (either because of timestamp or confidence)
                System.out.println("NO ANNOTATION EXTENSION FOUND for existing event: " + existingAssertion + ", new event: " + newAssertion);
                return new ContinuityResult(false, existingAssertion, existingAssertionHandle, newAssertion, null, null, null);
            }
        }
        else {
            return new ContinuityResult(false, null, null, newAssertion, null, null, null);
        }
    }
}
