package org.aimas.consert.engine;

import java.util.*;

import org.aimas.consert.model.content.ContextAssertion;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;

public class TrackedAssertionStore {
	
	public static class TrackedAssertionData {
		private FactHandle existingHandle;
		private EntryPoint existingEventEntryPoint;
		private ContextAssertion existingEvent;
		
		TrackedAssertionData(FactHandle existingHandle, EntryPoint existingEventEntryPoint, ContextAssertion existingEvent) {
			this.existingHandle = existingHandle;
	        this.existingEventEntryPoint = existingEventEntryPoint;
	        this.existingEvent = existingEvent;
        }

		FactHandle getExistingHandle() {
			return existingHandle;
		}

		public void setExistingHandle(FactHandle existingHandle) {
			this.existingHandle = existingHandle;
		}

		EntryPoint getExistingEventEntryPoint() {
			return existingEventEntryPoint;
		}

		void setExistingEventEntryPoint(EntryPoint existingEventEntryPoint) {
			this.existingEventEntryPoint = existingEventEntryPoint;
		}

		ContextAssertion getExistingEvent() {
			return existingEvent;
		}

		void setExistingEvent(ContextAssertion existingEvent) {
			this.existingEvent = existingEvent;
		}
		
		void clearHandle() {
			this.existingHandle = null;
		}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TrackedAssertionData that = (TrackedAssertionData) o;
            return Objects.equals(existingHandle, that.existingHandle) &&
                    Objects.equals(existingEvent, that.existingEvent);
        }

        @Override
        public int hashCode() {
            return Objects.hash(existingHandle, existingEvent);
        }
    }

	
	private final Map<Class<? extends ContextAssertion>, List<TrackedAssertionData>> lastValidEventMap;

	private final Set<FactHandle> deleteCache;

	private KieSession kSession;

	private TrackedAssertionStore(KieSession kSession) {
		lastValidEventMap = new HashMap<>();
		deleteCache = new HashSet<>();

		this.kSession = kSession;
	}
	
	static TrackedAssertionStore getNewInstance(KieSession kSession) {
		return new TrackedAssertionStore(kSession);
	}

	public TrackedAssertionData searchTrackedAssertionByContent(ContextAssertion event) {
        synchronized (lastValidEventMap) {
            return searchEventByContent(lastValidEventMap, event);
        }
    }


	private TrackedAssertionData searchEventByContent(Map<Class<? extends ContextAssertion>, List<TrackedAssertionData>> recencyMap, ContextAssertion event) {
		List<TrackedAssertionData> trackedEventList = recencyMap.get(event.getClass());
		for (TrackedAssertionData existingTrack : trackedEventList) {
			ContextAssertion existingEvent = existingTrack.getExistingEvent();
			
			if (existingEvent.getContentHash() == event.getContentHash()) {
				if (existingEvent.allowsContentContinuity(event)) {
					return existingTrack;
				}
			}
		}
		
		return null;
	}
	
	
	private TrackedAssertionData searchEventByHandle(Map<Class<? extends ContextAssertion>, List<TrackedAssertionData>> recencyMap,
                                                     Class<? extends ContextAssertion> assertionType,
													 FactHandle eventHandle) {
		
		List<TrackedAssertionData> trackedEventList = recencyMap.get(assertionType);
		for (TrackedAssertionData existingTrack : trackedEventList) {
			FactHandle existingEventHandle = existingTrack.getExistingHandle();
			
			if (existingEventHandle.equals(eventHandle)) {
				return existingTrack;
				
			}
		}
		
		return null;
	}


	void updateTrackedAssertion(TrackedAssertionData oldData, ContextAssertion newAssertion,
                                FactHandle newAssertionHandle) {

        removeAssertion(oldData);
        trackAssertion(newAssertion, newAssertionHandle, kSession.getEntryPoint(newAssertion.getStreamName()));

    }


	// ================================= SENSED ================================= 
	public boolean tracksAssertion(Class<? extends ContextAssertion> sensedAssertionType) {
		synchronized(lastValidEventMap) {
			return lastValidEventMap.containsKey(sensedAssertionType);
		}
    }

	public TrackedAssertionData trackAssertion(ContextAssertion event, FactHandle handle, EntryPoint entryPoint) {
		TrackedAssertionData assertionData = new TrackedAssertionData(handle, entryPoint, event);

	    synchronized(lastValidEventMap) {
			List<TrackedAssertionData> eventList = lastValidEventMap.get(event.getClass());
			if (eventList == null) {
				eventList = new LinkedList<TrackedAssertionData>();
				lastValidEventMap.put(event.getClass(), eventList);
			}

			eventList.add(assertionData);
		}

		return assertionData;
    }
	
	public boolean removeAssertion(TrackedAssertionData existingEventData) {
		synchronized(deleteCache) {
			synchronized(lastValidEventMap) {
				List<TrackedAssertionData> eventList = lastValidEventMap.get(existingEventData.getExistingEvent().getClass());
				boolean res = eventList.remove(existingEventData);
				deleteCache.add(existingEventData.getExistingHandle());

				return res;
			}
		}
    }

    public boolean removeAssertion(ContextAssertion assertion) {
        synchronized (deleteCache) {
            synchronized (lastValidEventMap) {
                TrackedAssertionData assertionData = searchTrackedAssertionByContent(assertion);

                if (assertionData != null) {
                    List<TrackedAssertionData> eventList = lastValidEventMap.get(assertion.getClass());
                    boolean res = eventList.remove(assertionData);
                    deleteCache.add(assertionData.getExistingHandle());

                    return res;
                }

                return false;
            }
        }
	}


	public boolean untrack(FactHandle deletedHandle, ContextAssertion deletedAssertion) {
		synchronized(deleteCache) {
			return deleteCache.remove(deletedHandle);
		}
    }

	void markExpired(FactHandle deletedHandle, ContextAssertion deletedAssertion) {
	    synchronized (lastValidEventMap) {
            TrackedAssertionData eventData = searchEventByHandle(lastValidEventMap, deletedAssertion.getClass(), deletedHandle);
            if (eventData != null) {
                eventData.clearHandle();
            }
        }
    }
}
