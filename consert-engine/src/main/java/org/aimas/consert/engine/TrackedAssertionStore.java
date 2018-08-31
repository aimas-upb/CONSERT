package org.aimas.consert.engine;

import java.util.*;

import org.aimas.consert.model.content.ContextAssertion;
import org.drools.core.factmodel.Fact;
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

	private final Map<Class<? extends ContextAssertion>, List<TrackedAssertionData>> lastValidDeducedMap;
	private final Set<FactHandle> deleteCache;

	private KieSession kSession;

	private TrackedAssertionStore(KieSession kSession) {
		lastValidEventMap = new HashMap<>();
		lastValidDeducedMap = new HashMap<>();
		deleteCache = new HashSet<>();

		this.kSession = kSession;
	}
	
	static TrackedAssertionStore getNewInstance(KieSession kSession) {
		return new TrackedAssertionStore(kSession);
	}

	TrackedAssertionData searchTrackedAssertionByContent(ContextAssertion event) {
	    TrackedAssertionData assertionData = searchSensedAssertionByContent(event);
	    if (assertionData != null)
	        return assertionData;
	    else
	        return searchDerivedAssertionByContent(event);
    }

	TrackedAssertionData searchSensedAssertionByContent(ContextAssertion event) {
		synchronized (lastValidEventMap) {
			return searchEventByContent(lastValidEventMap, event);
        }
	}


	TrackedAssertionData searchDerivedAssertionByContent(ContextAssertion event) {
		synchronized (lastValidDeducedMap) {
			return searchEventByContent(lastValidDeducedMap, event);
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
                                FactHandle newAssertionHandle, boolean derived) {
        if (!derived) {
            removeSensed(oldData);
            trackSensed(newAssertion, newAssertionHandle, kSession.getEntryPoint(newAssertion.getStreamName()));
        }
        else {
            removeDerived(oldData);
            trackDerived(newAssertion, newAssertionHandle, kSession.getEntryPoint(newAssertion.getStreamName()));
        }
    }


	// ================================= SENSED ================================= 
	public boolean tracksSensed(Class<? extends ContextAssertion> sensedAssertionType) {
		synchronized(lastValidEventMap) {
			return lastValidEventMap.containsKey(sensedAssertionType);
		}
    }

	public void trackSensed(ContextAssertion event, FactHandle handle, EntryPoint entryPoint) {
		synchronized(lastValidEventMap) {
			List<TrackedAssertionData> eventList = lastValidEventMap.get(event.getClass());
			if (eventList == null) {
				eventList = new LinkedList<TrackedAssertionData>();
				lastValidEventMap.put(event.getClass(), eventList);
			}

			eventList.add(new TrackedAssertionData(handle, entryPoint, event));
		}
		
    }
	
	boolean removeSensed(TrackedAssertionData existingEventData) {
		synchronized(deleteCache) {
			synchronized(lastValidEventMap) {
				List<TrackedAssertionData> eventList = lastValidEventMap.get(existingEventData.getExistingEvent().getClass());
				boolean res = eventList.remove(existingEventData);
				deleteCache.add(existingEventData.getExistingHandle());

				return res;
			}
		}
    }

    boolean removeSensed(ContextAssertion assertion) {
        synchronized (deleteCache) {
            synchronized (lastValidEventMap) {
                TrackedAssertionData assertionData = searchSensedAssertionByContent(assertion);

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
	
	// ================================= DERIVED ================================= 
	public boolean tracksDerived(Class<? extends ContextAssertion> derivedAssertionType) {
		synchronized(lastValidDeducedMap) {
			return lastValidDeducedMap.containsKey(derivedAssertionType);
		}
    }
	
	public void trackDerived(ContextAssertion event, FactHandle handle, EntryPoint entryPoint) {
		synchronized(lastValidDeducedMap) {
			List<TrackedAssertionData> eventList = lastValidDeducedMap.get(event.getClass());
			if (eventList == null) {
				eventList = new LinkedList<TrackedAssertionData>();
				lastValidDeducedMap.put(event.getClass(), eventList);
			}
		
			eventList.add(new TrackedAssertionData(handle, entryPoint, event));
		}
		
    }
	
	boolean removeDerived(TrackedAssertionData existingEventData) {
		synchronized(deleteCache) {
			synchronized(lastValidDeducedMap) {
				List<TrackedAssertionData> eventList = lastValidDeducedMap.get(existingEventData.getExistingEvent().getClass());
				boolean res = eventList.remove(existingEventData);
				deleteCache.add(existingEventData.getExistingHandle());

				return res;
			}
		}
    }

    boolean removeDerived(ContextAssertion assertion) {
        synchronized(deleteCache) {
            synchronized(lastValidDeducedMap) {
                TrackedAssertionData assertionData = searchDerivedAssertionByContent(assertion);
                if (assertionData != null) {
                    List<TrackedAssertionData> eventList = lastValidDeducedMap.get(assertionData.getExistingEvent().getClass());
                    boolean res = eventList.remove(assertionData);
                    deleteCache.add(assertionData.getExistingHandle());

                    return res;
                }

                return false;
            }
        }
	}


	public boolean remove(ContextAssertion assertion) {
	    TrackedAssertionData assertionData = searchSensedAssertionByContent(assertion);
	    if (assertionData != null) {
	        return removeSensed(assertionData);
        }
        else {
	        assertionData = searchDerivedAssertionByContent(assertion);
	        if (assertionData != null) {
	            return removeDerived(assertionData);
            }
        }

        return false;
    }

    public boolean remove(TrackedAssertionData assertionData) {
        boolean res = removeSensed(assertionData);

        if (res)
            return true;
        else
            return removeDerived(assertionData);
    }


	public boolean untrack(FactHandle deletedHandle, ContextAssertion deletedAssertion) {
		synchronized(deleteCache) {
			return deleteCache.remove(deletedHandle);
		}
    }

	void markExpired(FactHandle deletedHandle, ContextAssertion deletedAssertion) {
	    TrackedAssertionData eventData = null;
		
	    if (!deletedAssertion.isDerived()) {
	    	synchronized (lastValidEventMap) {
	    		eventData = searchEventByHandle(lastValidEventMap, deletedAssertion.getClass(), deletedHandle);
	    		if (eventData != null) {
	    			eventData.clearHandle();
	    		}
            }
	    	
	    }
	    else {
	    	synchronized (lastValidDeducedMap) {
	    		eventData = searchEventByHandle(lastValidDeducedMap, deletedAssertion.getClass(), deletedHandle);
	    		if (eventData != null) {
	    			eventData.clearHandle();
	    		}
            }
	    }
    }
}
