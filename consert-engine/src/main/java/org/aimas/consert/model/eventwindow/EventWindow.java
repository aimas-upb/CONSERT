package org.aimas.consert.model.eventwindow;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.aimas.consert.model.content.ContextAssertion;

public class EventWindow {
	
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
	
	private ContextAssertion possibleAssertion;
	
	private SortedSet<ContextAssertion> supportingAssertions;
	
	
	
	public EventWindow() {
		supportingAssertions = new TreeSet<ContextAssertion>(new AssertionTimestampComparator());
	}

	public EventWindow(ContextAssertion possibleAssertion) {
	    this.possibleAssertion = possibleAssertion;
	    supportingAssertions = new TreeSet<ContextAssertion>(new AssertionTimestampComparator());
    }

	/**
	 * @return the possibleAssertion
	 */
	public ContextAssertion getPossibleAssertion() {
		return possibleAssertion;
	}

	/**
	 * @param possibleAssertion the possibleAssertion to set
	 */
	public void setPossibleAssertion(ContextAssertion possibleAssertion) {
		this.possibleAssertion = possibleAssertion;
	}
	
	
	public void addSupportingAssertions(ContextAssertion assertion) {
		supportingAssertions.add(assertion);
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
	
}
