package org.aimas.consert.tests.hla;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.aimas.consert.engine.core.EventTracker;
import org.aimas.consert.model.content.ContextAssertion;

public class HLAEventInserter {
	
	public static final String POSITION_ENTRYPOINT 	= "PositionStream";
	public static final String LLA_ENTRYPOINT 		= "LLAStream";
	
	private boolean isFinished = false;
	private Object syncObj = new Object();
	
	private File eventInputFile;
	private EventTracker eventTracker;
	
	private Queue<Object> events;
	
	private ScheduledExecutorService readerService;
	private ExecutorService insertionService;
	
	public HLAEventInserter(File eventInputFile, EventTracker eventTracker) {
		this.eventInputFile = eventInputFile;
		this.eventTracker = eventTracker;
		events = parseEvents();
	}
	
	private Queue<Object> parseEvents() {
	    return HLAEventReader.parseEvents(eventInputFile);
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
					
					//readerService.schedule(new EventReadTask(), delay, TimeUnit.MILLISECONDS);
					readerService.schedule(new EventReadTask(), 1, TimeUnit.MILLISECONDS);
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
			// filter by event instance type to see on which stream to insert
			event.setProcessingTimeStamp(System.currentTimeMillis());
			eventTracker.insertEvent(event);
			
//			if (event instanceof Position) {
//				EntryPoint positionStream = kSession.getEntryPoint(POSITION_ENTRYPOINT);
//				positionStream.insert(event);
//			}
//			else if (event instanceof LLA) {
//				EntryPoint llaStream = kSession.getEntryPoint(LLA_ENTRYPOINT);
//				System.out.println(llaStream);
//				llaStream.insert(event);
//			}
        }
	}
}
