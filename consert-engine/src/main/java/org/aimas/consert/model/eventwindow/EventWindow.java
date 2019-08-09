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
	
	private long initTimestamp;
	
	private ContextAssertionContent possibleAssertion;
	private SortedSet<ContextAssertion> supportingAssertions;
	
	private int maxSupportingAssertions;
	private double maxDuration;
	
	
	/**
	 * Create a default EventWindow, with no limit on number of assertions or time and no possible or supporting assertions
	 * @param timestamp: the timestamp at which this EventWindow is created
	 */
	public EventWindow(long timestamp) {
		this(null, timestamp, null, 0, 0);
	}

	/**
	 * Create a default EventWindow for a possible assertion. The window has no supporting assertions and no time or event number limits 
	 * @param possibleAssertion
	 * @param timestamp: the timestamp at which this EventWindow is created
	 */
	public EventWindow(ContextAssertionContent possibleAssertion, long timestamp) {
	    this(possibleAssertion, timestamp, null, 0, 0);
    }
	
	/**
	 * Create a default EventWindow for a possible assertion and its supporting assertions. 
	 * The window has no time or event number limits.
	 * @param possibleAssertion
	 * @param supportingAssertions
	 * @param timestamp: the timestamp at which this EventWindow is created
	 */
	public EventWindow(ContextAssertionContent possibleAssertion, long timestamp, List<ContextAssertion> supportingAssertions) {
	    this(possibleAssertion, timestamp, supportingAssertions, 0, 0);
    }
	
	/**
	 * Create an empty EventWindow with a maximum number of supporting assertions
	 * @param possibleAssertion
	 * @param maxSupportingAssertions
	 * @param timestamp: the timestamp at which this EventWindow is created
	 */
	public EventWindow(ContextAssertionContent possibleAssertion, long timestamp, int maxSupportingAssertions) {
	    this(possibleAssertion, timestamp, null, maxSupportingAssertions, 0);
    }
	
	/**
	 * Create an empty EventWindow with a maximum duration 
	 * @param possibleAssertion
	 * @param maxDuration 
	 * @param timestamp: the timestamp at which this EventWindow is created
	 */
	public EventWindow(ContextAssertionContent possibleAssertion, long timestamp, double maxDuration) {
	    this(possibleAssertion, timestamp, null, 0, maxDuration);
    }
	
	
	/**
	 * Create an empty EventWindow with a maximum duration (given in milliseconds) and a maximum number of supporting assertions
	 * @param possibleAssertion
	 * @param maxSupportingAssertions The maximum number of assertions. If less than or equal to 0, 
	 * the maximum number of supporting assertions is considered to be infinite.
	 * @param maxDuration The maximum duration (given in milliseconds). If less than or equal to 0, the duration is considered to be infinite
	 * @param timestamp: the timestamp at which this EventWindow is created
	 */
	public EventWindow(ContextAssertionContent possibleAssertion, long timestamp, int maxSupportingAssertions, double maxDuration) {
	    this(possibleAssertion, timestamp, null, maxSupportingAssertions, maxDuration);
    }
	
	/**
	 * Create an EventWindow with a maximum duration (given in milliseconds) and a maximum number of supporting assertions
	 * @param possibleAssertion
	 * @param supportingAssertions
	 * @param maxSupportingAssertions The maximum number of assertions. If less than or equal to 0, 
	 * the maximum number of supporting assertions is considered to be infinite.
	 * @param maxDuration The maximum duration (given in milliseconds). If less than or equal to 0, the duration is considered to be infinite.
	 * @param timestamp: the timestamp at which this EventWindow is created
	 */
	public EventWindow(ContextAssertionContent possibleAssertion, long timestamp, List<ContextAssertion> supportingAssertions, int maxSupportingAssertions, double maxDuration) {
		this.initTimestamp = timestamp;
		
		this.supportingAssertions = new TreeSet<ContextAssertion>(new AssertionTimestampComparator());
		
		if (possibleAssertion != null) {
			this.possibleAssertion = possibleAssertion;
		}
		
		if (supportingAssertions != null) {
			this.supportingAssertions.addAll(supportingAssertions);
		}
		
		this.maxSupportingAssertions = maxSupportingAssertions;
		this.maxDuration = maxDuration;
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
	
	/**
	 * @return The initialization timestamp of this EventWindow
	 */
	public long getInitTimestamp() {
		return initTimestamp;
	}
	
	/**
	 * Set the initialization timestamp of this EventWindow
	 * @param initTimestamp
	 */
	public void setInitTimestamp(long initTimestamp) {
		this.initTimestamp = initTimestamp;
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
	
	
	public boolean hasSupport() {
		return !supportingAssertions.isEmpty();
	}
	
	public int getNumSupportingAssertions() {
		return supportingAssertions.size();
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
	
	
	public double getWindowDuration() {
		if (supportingAssertions.isEmpty())
			return 0;
		else 
			return supportingAssertions.last().getAnnotations().getTimestamp() - supportingAssertions.first().getAnnotations().getTimestamp();
	}
	
	public int getMaxSupportingAssertions() {
		return maxSupportingAssertions;
	}

	
	public void setMaxSupportingAssertions(int maxSupportingAssertions) {
		this.maxSupportingAssertions = maxSupportingAssertions;
	}

	
	public double getMaxDuration() {
		return maxDuration;
	}

	
	public void setMaxDuration(double maxDuration) {
		this.maxDuration = maxDuration;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((possibleAssertion == null) ? 0 : possibleAssertion.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventWindow other = (EventWindow) obj;
		if (possibleAssertion == null) {
			if (other.possibleAssertion != null)
				return false;
		} else if (!possibleAssertion.equals(other.possibleAssertion))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EventWindow [possibleAssertion=" + possibleAssertion + ", maxSupportingAssertions=" + maxSupportingAssertions + ", maxDuration=" + maxDuration + 
				", supportingAssertions=" + supportingAssertions + "]";
	}
	
	
	
	
}
