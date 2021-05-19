package org.aimas.consert.tests.casas;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.aimas.consert.engine.core.EventTracker;
import org.aimas.consert.model.annotations.DefaultAnnotationData;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.model.content.ContextAssertion.AcquisitionType;
import org.aimas.consert.tests.casas.assertions.Cabinet;
import org.aimas.consert.tests.casas.assertions.Item;
import org.aimas.consert.tests.casas.assertions.Phone;
import org.aimas.consert.tests.utils.EventReader;

public class CASASEventInserter {
	
	private boolean isFinished = false;
	private Object syncObj = new Object();
	
	private File eventInputFile;
	private EventReader eventReader;
	
	private EventTracker eventTracker;
	
	private Queue<Object> events;
	
	private ScheduledExecutorService readerService;
	private ExecutorService insertionService;
	
	private Map<String, ContextAssertion> itemActivationMap = new HashMap<String, ContextAssertion>();
	
	public CASASEventInserter(File eventInputFile, EventReader reader, EventTracker eventTracker) {
		this.eventInputFile = eventInputFile;
		this.eventReader = reader;
		this.eventTracker = eventTracker;
		events = parseEvents();
	}
	
	private Queue<Object> parseEvents() {
	    return eventReader.parseEvents(eventInputFile);
    }

	public void start() {
		// set unfinished
		setFinished(false);
		
		// instantiate readerService and insertionService
		if (readerService == null || readerService.isShutdown()) {
			readerService = Executors.newScheduledThreadPool(1);
			insertionService = Executors.newSingleThreadExecutor();
		}
		
		// start readerService
		readerService.execute(new EventReadTask());
    }
	
	public void stop() {
		// stop the readerService and the insertionService if they are still active
		readerService.shutdownNow();
		insertionService.shutdownNow();
	}
	
	public boolean isFinished() {
		synchronized(syncObj) {
			return isFinished;
		}
	}
	
	private void setFinished(boolean finished) {
		synchronized(syncObj) {
			isFinished = finished;
		}
	}
	
	
	private class EventReadTask implements Runnable {
		
		public void run() {
			// get event to be inserted
			ContextAssertion event = (ContextAssertion)events.poll();
			if (event != null) {
				// look at the next event if there is one
				ContextAssertion nextEvent = (ContextAssertion)events.peek();
				
				// submit insertion task
				insertionService.execute(new EventInsertionTask(event));
				
				if (nextEvent != null) {
					long delay = (long)(nextEvent.getStartTimestamp() - event.getStartTimestamp());
					//System.out.println("Next Event due in " + delay + " ms");
					
					readerService.schedule(new EventReadTask(), delay, TimeUnit.MILLISECONDS);
					//readerService.schedule(new EventReadTask(), 1, TimeUnit.MILLISECONDS);
				}
				else {
					setFinished(true);
				}
			}
			else {
				setFinished(true);
			}
        }
		
	}
	
	private class EventInsertionTask implements Runnable {
		private ContextAssertion event;
		
		EventInsertionTask(ContextAssertion event) {
			this.event = event;
		}
		
		public void run() {
			// If the events are of a sensed acquisition type, insert them as simple events and set their timestamp at insertion
			if (event.getAcquisitionType() == AcquisitionType.SENSED) {
				event.setProcessingTimeStamp(System.currentTimeMillis());
				//eventTracker.insertSimpleEvent(event, false);
				eventTracker.insertSimpleEvent(event, true);
			}
			else {
				// insert the events as PROFILED ones, set their duration and timestamp
				String key = null;
				if (event instanceof Item || event instanceof Cabinet)
					key = ((Item)event).getSensorId();
				else if (event instanceof Phone)
					key = "phone";
				
				if (itemActivationMap.containsKey(key)) {
					ContextAssertion prevEvent = itemActivationMap.remove(key);
					eventTracker.deleteEvent(prevEvent);
				}
				
				itemActivationMap.put(key, event);
				DefaultAnnotationData ann = (DefaultAnnotationData)event.getAnnotations();
				
				long ts = eventTracker.getCurrentTime();
				ann.setLastUpdated(ts);
				ann.setStartTime(new Date(ts));
				ann.setEndTime(null);
				
				eventTracker.insertSimpleEvent(event, false);
			}
        }
	}
}
