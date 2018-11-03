package org.aimas.consert.unittest;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.aimas.consert.engine.EventTracker;
import org.aimas.consert.model.annotations.DefaultAnnotationData;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.tests.hla.assertions.Position;
import org.aimas.consert.tests.hla.entities.Area;
import org.aimas.consert.tests.hla.entities.Person;

public class TestInserter {
	public static final String POSITION_ENTRYPOINT 	= "PositionStream";
	public static final int NUM_EVENTS = 10;
	
	private boolean isFinished = false;
	private Object syncObj = new Object();
	
	private EventTracker eventTracker;
	
	private Queue<Object> events;
	
	private ScheduledExecutorService readerService;
	private ExecutorService insertionService;
	
	public TestInserter(EventTracker eventTracker) {
		this.eventTracker = eventTracker;
		events = generateEvents();
	}
	
	private Queue<Object> generateEvents() {
		Queue<Object> events = new LinkedList<Object>();
		Person testPerson = new Person("mishu");
		Calendar now = Calendar.getInstance();
		
		for (int i  = 0; i< NUM_EVENTS; i++) {
			Calendar next = (Calendar)now.clone();
			now.add(Calendar.SECOND, 1);
			
			DefaultAnnotationData ann = new DefaultAnnotationData(next.getTimeInMillis(), 1.0, next.getTime(), next.getTime());
			Position pos =  new Position(testPerson, new Area("WORK_AREA"), ann);
			events.offer(pos);			
		}
		
		return events;
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
			// filter by event instance type to see on which stream to insert
			
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
