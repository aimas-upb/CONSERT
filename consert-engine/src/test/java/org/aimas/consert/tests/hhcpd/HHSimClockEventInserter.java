package org.aimas.consert.tests.hhcpd;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.aimas.consert.engine.core.EventTracker;
import org.aimas.consert.model.annotations.DefaultAnnotationData;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.model.content.ContextAssertion.AcquisitionType;
import org.aimas.consert.tests.casas.assertions.Cabinet;
import org.aimas.consert.tests.casas.assertions.Item;
import org.aimas.consert.tests.casas.assertions.Phone;
import org.aimas.consert.tests.utils.EventReader;
import org.drools.core.time.SessionPseudoClock;
import org.kie.api.runtime.KieSession;

public class HHSimClockEventInserter {
	
	/** 
	 * Default delay interval for pseudo clock advancement when there are no other events in between.
	 * Measured in milliseconds.
	 */
	public static int EVENT_DIFF_DEFAULT = 100;
	
	private AtomicBoolean finished = new AtomicBoolean(false);
	
	private File eventInputFile;
	private EventReader eventReader;
	
	private KieSession kSession;
	private EventTracker eventTracker;
	
	private Queue<Object> events;
	private boolean firstEvent = true;
	
	private long prevEventTimestamp;
	
	private ScheduledExecutorService readerService;
	private ExecutorService insertionService;
	
	private Map<String, ContextAssertion> itemActivationMap = new HashMap<String, ContextAssertion>();
	
	public HHSimClockEventInserter(File eventInputFile, EventReader reader, 
			KieSession kSession, EventTracker eventTracker) {
		this.eventInputFile = eventInputFile;
		this.eventReader = reader;
		this.kSession = kSession;
		this.eventTracker = eventTracker;
		
		events = parseEvents();
	}
	
	private Queue<Object> parseEvents() {
	    
		return eventReader.parseEvents(eventInputFile);
    }

	public void start() {
		// set unfinished
		setFinished(false);
		firstEvent = true;
		
		// instantiate readerService and insertionService
		if (readerService == null || readerService.isShutdown()) {
			readerService = Executors.newScheduledThreadPool(1, new ThreadFactory() {
				
				@Override
				public Thread newThread(Runnable r) {
					Thread eventReaderThread = new Thread(r);
					eventReaderThread.setName("HH Event Reader Thread");
					
					return eventReaderThread;
				}
			});
			insertionService = Executors.newSingleThreadExecutor(new ThreadFactory() {
				
				@Override
				public Thread newThread(Runnable r) {
					Thread eventInsertionThread = new Thread(r);
					eventInsertionThread.setName("HH Event Insertion Thread");
					
					return eventInsertionThread;
				}
			});
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
		return finished.get();
	}
	
	private void setFinished(boolean finished) {
		this.finished.set(finished);
	}
	
	
	private class EventReadTask implements Runnable {
		
		public void run() {
			// get current simulated session clock
			SessionPseudoClock clock = kSession.getSessionClock();
			
			if (firstEvent) {
				// get event to be inserted
				ContextAssertion event = (ContextAssertion)events.poll();
				prevEventTimestamp = (long)event.getStartTimestamp();
				
				insertionService.execute(new EventInsertionTask(event));
				firstEvent = false;
				
				// look at the next event if there is one
				ContextAssertion nextEvent = (ContextAssertion)events.peek();
				long delay = (long)(nextEvent.getStartTimestamp() - prevEventTimestamp);
				
				if (delay >= EVENT_DIFF_DEFAULT) {
					clock.advanceTime(EVENT_DIFF_DEFAULT, TimeUnit.MILLISECONDS);
					prevEventTimestamp += EVENT_DIFF_DEFAULT;
					
					readerService.schedule(new EventReadTask(), 1, TimeUnit.MILLISECONDS);
				}
				else {
					readerService.schedule(new EventReadTask(), 1, TimeUnit.MILLISECONDS);
				}		
			}
			else {
				// peek at event that follows
				ContextAssertion upcomingEvent = (ContextAssertion)events.peek();
				long ts = (long)upcomingEvent.getStartTimestamp();
				
				long eventTimestampDiff = ts - prevEventTimestamp; 
				
				if (eventTimestampDiff >= EVENT_DIFF_DEFAULT) {
					clock.advanceTime(EVENT_DIFF_DEFAULT, TimeUnit.MILLISECONDS);
					prevEventTimestamp += EVENT_DIFF_DEFAULT;
					
					readerService.schedule(new EventReadTask(), 1, TimeUnit.MILLISECONDS);
				}
				else {
					// get event to be inserted
					ContextAssertion event = (ContextAssertion)events.poll();
					
					if (event != null) {
						// look at the next event if there is one
						ContextAssertion nextEvent = (ContextAssertion)events.peek();
						
						// submit insertion task
						clock.advanceTime(eventTimestampDiff, TimeUnit.MILLISECONDS);
						prevEventTimestamp = ts;
						insertionService.execute(new EventInsertionTask(event));
						
						if (nextEvent != null) {
							long delay = (long)(nextEvent.getStartTimestamp() - prevEventTimestamp);
							//System.out.println("Next Event due in " + delay + " ms");
							
							if (delay >= EVENT_DIFF_DEFAULT) {
								clock.advanceTime(EVENT_DIFF_DEFAULT, TimeUnit.MILLISECONDS);
								prevEventTimestamp += EVENT_DIFF_DEFAULT;
								
								readerService.schedule(new EventReadTask(), 1, TimeUnit.MILLISECONDS);
							}
							else {
								readerService.schedule(new EventReadTask(), 1, TimeUnit.MILLISECONDS);
								//readerService.schedule(new EventReadTask(), 1, TimeUnit.MILLISECONDS);
							}							
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
        }
		
	}
	
	private class EventInsertionTask implements Runnable {
		private ContextAssertion event;
		
		EventInsertionTask(ContextAssertion event) {
			this.event = event;
		}
		
		public void run() {
			event.setProcessingTimeStamp(System.currentTimeMillis());
			
			// If the events are of a sensed acquisition type, insert them as simple events and set their timestamp at insertion
			if (event.getAcquisitionType() == AcquisitionType.SENSED) {
				
				//eventTracker.insertSimpleEvent(event, false);
				eventTracker.insertSimpleEvent(event, true);
			}
			else {
				// insert the events as PROFILED ones, set their duration and timestamp
				String key = null;
				if (event instanceof Item) 
					key = ((Item)event).getSensorId();
				else if (event instanceof Cabinet)
					key = ((Cabinet)event).getSensorId();
				else if (event instanceof Phone) {
					key = "phone";
				}
				
				long ts = eventTracker.getCurrentTime();
				
				if (itemActivationMap.containsKey(key)) {
					ContextAssertion prevEvent = itemActivationMap.remove(key);
					
					// first delete the old event
					eventTracker.deleteEvent(prevEvent);
					
					// then update its endtime and reinsert it
					DefaultAnnotationData ann = (DefaultAnnotationData)prevEvent.getAnnotations();
					ann.setEndTime(new Date(ts));
					
					eventTracker.insertSimpleEvent(prevEvent, false);
				}
				
				itemActivationMap.put(key, event);
				DefaultAnnotationData ann = (DefaultAnnotationData)event.getAnnotations();
				ann.setLastUpdated(ts);
				ann.setStartTime(new Date(ts));
				ann.setEndTime(new Date(Integer.MAX_VALUE));
				
				//ann.setEndTime(null);
				eventTracker.insertSimpleEvent(event, false);
			}
        }
	}
}
