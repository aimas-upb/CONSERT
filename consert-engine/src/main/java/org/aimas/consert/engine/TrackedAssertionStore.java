package org.aimas.consert.engine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aimas.consert.model.content.ContextAssertion;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;

public class TrackedAssertionStore {
	
	public static class TrackedEventData {
		private FactHandle existingHandle;
		private EntryPoint existingEventEntryPoint;
		private ContextAssertion existingEvent;
		
		public TrackedEventData(FactHandle existingHandle, EntryPoint existingEventEntryPoint, ContextAssertion existingEvent) {
	        this.existingHandle = existingHandle;
	        this.existingEventEntryPoint = existingEventEntryPoint;
	        this.existingEvent = existingEvent;
        }

		public FactHandle getExistingHandle() {
			return existingHandle;
		}

		public void setExistingHandle(FactHandle existingHandle) {
			this.existingHandle = existingHandle;
		}

		public EntryPoint getExistingEventEntryPoint() {
			return existingEventEntryPoint;
		}

		public void setExistingEventEntryPoint(EntryPoint existingEventEntryPoint) {
			this.existingEventEntryPoint = existingEventEntryPoint;
		}

		public ContextAssertion getExistingEvent() {
			return existingEvent;
		}

		public void setExistingEvent(ContextAssertion existingEvent) {
			this.existingEvent = existingEvent;
		}
		
		public void clearHandle() {
			this.existingHandle = null;
		}
	}
	
	
	private static TrackedAssertionStore store = null;
	
	private Map<Class<? extends ContextAssertion>, List<TrackedEventData>> lastValidEventMap;
	private Map<Class<? extends ContextAssertion>, List<TrackedEventData>> lastValidDeducedMap;
	private Set<FactHandle> deleteCache;
	
	private TrackedAssertionStore() {
		lastValidEventMap = new HashMap<Class<? extends ContextAssertion>, List<TrackedEventData>>();
		lastValidDeducedMap = new HashMap<Class<? extends ContextAssertion>, List<TrackedEventData>>();
		
		deleteCache = new HashSet<FactHandle>();
	}
	
	public static TrackedAssertionStore getInstance() {
		if (store == null) {
			store = new TrackedAssertionStore();
		}
		
		return store;
	}
	
	public TrackedEventData searchSensedAssertionByContent(ContextAssertion event) {
		synchronized (lastValidEventMap) {
			return searchEventByContent(lastValidEventMap, event);
        }
	}
	
	
	public TrackedEventData searchDerivedAssertionByContent(ContextAssertion event) {
		synchronized (lastValidDeducedMap) {
			return searchEventByContent(lastValidDeducedMap, event);    
        }
	}
	
	
	private TrackedEventData searchEventByContent(Map<Class<? extends ContextAssertion>, List<TrackedEventData>> recencyMap, ContextAssertion event) {
		List<TrackedEventData> trackedEventList = recencyMap.get(event.getClass());
		for (TrackedEventData existingTrack : trackedEventList) {
			ContextAssertion existingEvent = existingTrack.getExistingEvent();
			
			if (existingEvent.getContentHash() == event.getContentHash()) {
				if (existingEvent.allowsContentContinuity(event)) {
					return existingTrack;
				}
			}
		}
		
		return null;
	}
	
	
	private TrackedEventData searchEventByHandle(Map<Class<? extends ContextAssertion>, List<TrackedEventData>> recencyMap, Class<? extends ContextAssertion> assertionType, 
			FactHandle eventHandle) {
		
		List<TrackedEventData> trackedEventList = recencyMap.get(assertionType);
		for (TrackedEventData existingTrack : trackedEventList) {
			FactHandle existingEventHandle = existingTrack.getExistingHandle();
			
			if (existingEventHandle.equals(eventHandle)) {
				return existingTrack;
				
			}
		}
		
		return null;
	}
	
	
	// ================================= SENSED ================================= 
	public boolean tracksSensed(Class<? extends ContextAssertion> sensedAssertionType) {
		synchronized(lastValidEventMap) {
			return lastValidEventMap.containsKey(sensedAssertionType);
		}
    }

	public void trackSensed(ContextAssertion event, FactHandle handle, EntryPoint entryPoint) {
		synchronized(lastValidEventMap) {
			List<TrackedEventData> eventList = lastValidEventMap.get(event.getClass());
			if (eventList == null) {
				eventList = new LinkedList<TrackedEventData>();
				lastValidEventMap.put(event.getClass(), eventList);
			}
		
			eventList.add(new TrackedEventData(handle, entryPoint, event));
		}
		
    }
	
	public void removeSensed(TrackedEventData existingEventData) {
		synchronized(deleteCache) {
			synchronized(lastValidEventMap) {
				List<TrackedEventData> eventList = lastValidEventMap.get(existingEventData.getExistingEvent().getClass());
				eventList.remove(existingEventData);
				
				deleteCache.add(existingEventData.getExistingHandle());
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
			List<TrackedEventData> eventList = lastValidDeducedMap.get(event.getClass());
			if (eventList == null) {
				eventList = new LinkedList<TrackedEventData>();
				lastValidDeducedMap.put(event.getClass(), eventList);
			}
		
			eventList.add(new TrackedEventData(handle, entryPoint, event));
		}
		
    }
	
	public void removeDerived(TrackedEventData existingEventData) {
		synchronized(deleteCache) {
			synchronized(lastValidDeducedMap) {
				List<TrackedEventData> eventList = lastValidDeducedMap.get(existingEventData.getExistingEvent().getClass());
				eventList.remove(existingEventData);
				
				deleteCache.add(existingEventData.getExistingHandle());
			}
		}
    }

	public boolean wasUntracked(FactHandle deletedHandle, ContextAssertion deletedAssertion) {
		synchronized(deleteCache) {
			return deleteCache.remove(deletedHandle);
		}
    }

	public void markExpired(FactHandle deletedHandle, ContextAssertion deletedAssertion) {
	    TrackedEventData eventData = null;
		
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
