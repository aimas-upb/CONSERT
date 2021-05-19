package org.aimas.consert.engine.core;

import java.util.LinkedList;
import java.util.List;

import org.aimas.consert.engine.api.ChangePointListener;
import org.aimas.consert.model.content.ContextAssertion;

public class ChangePointManager {
	/** Default value of the maximum number of changepoints that will be buffered by this changepoint manager **/
	public static final int DEFAULT_MAX_CP = 1000;
	
	private LinkedList<ContextAssertion> changePointQueue;
	private int limit;
	
	private ChangePointListener eventTracker;
	
	public ChangePointManager(ChangePointListener eventTracker, int changePointQueueLimit) {
		this.eventTracker = eventTracker;
		this.limit = changePointQueueLimit;
		
		this.changePointQueue = new LinkedList<ContextAssertion>();
	}
	
	/**
	 * Adds a ContextAssertion to the ChangePoint List, returning True if the assertion was inserted WITHOUT surpassing the the max size
	 * and False otherwise
	 * @param assertion
	 * @return True if the assertion was added as a ChangePoint without removing any older assertion (i.e. without exceeding the max size of the change point queue),
	 * and False otherwise
	 */
	public boolean add(ContextAssertion assertion) {
		changePointQueue.add(assertion);
		
		boolean removed = false;
		while (changePointQueue.size() > limit) { 
			changePointQueue.remove();
			if (!removed) 
				removed = true;
		}
		
		eventTracker.notifyChangePointAdded(assertion);
		
		return removed;
	}
	
	public List<ContextAssertion> getChangePoints() {
		return changePointQueue;
	}
}
