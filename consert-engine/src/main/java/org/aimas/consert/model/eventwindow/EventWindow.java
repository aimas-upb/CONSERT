package org.aimas.consert.model.eventwindow;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.model.content.ContextAssertionContent;

public class EventWindow implements Comparator<EventWindow> {
	
	private static class AssertionTimestampComparator implements Comparator<ContextAssertion> {
		@Override
        public int compare(ContextAssertion a1, ContextAssertion a2) {
	        double ts1 = a1.getAnnotations().getTimestamp();
	        double ts2 = a2.getAnnotations().getTimestamp();
	        
	        if (ts1 < ts2)
	        	return -1;
	        else if (ts1 > ts2)
	        	return 1;
	        
	        return 0;
        }
		
	}
	
	private ContextAssertionContent possibleAssertion;
	
	private SortedSet<ContextAssertion> supportingAssertions;
	
	
	
	public EventWindow() {
		this(null, null);
	}

	public EventWindow(ContextAssertionContent possibleAssertion) {
	    this(possibleAssertion, null);
    }
	
	public EventWindow(ContextAssertionContent possibleAssertion, List<ContextAssertion> supportingAssertions) {
		this.supportingAssertions = new TreeSet<ContextAssertion>(new AssertionTimestampComparator());
		
		if (possibleAssertion != null) {
			this.possibleAssertion = possibleAssertion;
		}
		
		if (supportingAssertions != null) {
			this.supportingAssertions.addAll(supportingAssertions);
		}
	}
	
	
	/**
	 * @return the possibleAssertion
	 */
	public ContextAssertionContent getPossibleAssertion() {
		return possibleAssertion;
	}

	/**
	 * @param possibleAssertion the possibleAssertion to set
	 */
	public void setPossibleAssertion(ContextAssertionContent possibleAssertion) {
		this.possibleAssertion = possibleAssertion;
	}
	
	
	public void addSupportingAssertion(ContextAssertion assertion) {
		supportingAssertions.add(assertion);
	}
	
	
	public void addSupportingAssertions(List<ContextAssertion> assertions) {
		supportingAssertions.addAll(assertions);
	}
	
	
	public List<ContextAssertion> getSupportingAssertions() {
		return new LinkedList<ContextAssertion>(supportingAssertions);
		
	}
	
	
	/**
	 * Gets the start of the event window as the timestamp of the first assertion supporting the {@link possibleAssertion}
	 * @return The timestamp of the first supporting assertion in the window, or -1 if the window is empty
	 */
	public double getWindowStart() {
		if (!supportingAssertions.isEmpty())
			return supportingAssertions.first().getAnnotations().getTimestamp();
		
		return -1;
			
	}
	
	
	/**
	 * Gets the start of the event window as the timestamp of the last assertion supporting the {@link possibleAssertion}
	 * @return The timestamp of the last supporting assertion in the window, or -1 if the window is empty
	 */
	public double getWindowEnd() {
		if (!supportingAssertions.isEmpty())
			return supportingAssertions.last().getAnnotations().getTimestamp();
		
		return -1;
	}

	/**
	 * Compares two EventWindows based on their start timestamps
	 */
	@Override
    public int compare(EventWindow o1, EventWindow o2) {
	    if (o1.getWindowStart() < o2.getWindowStart())
	    	return -1;
	    			
	    if(o1.getWindowStart() > o2.getWindowStart())
	    	return 1;
	    
	    return 0;
    }
	
}
