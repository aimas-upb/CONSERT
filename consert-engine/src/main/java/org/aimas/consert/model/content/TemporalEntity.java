package org.aimas.consert.model.content;

/**
 * Simple time interval interface for elements of the CONSERT model which contain a temporal aspect:
 * ContextAssertions with associated temporal validity, as well as EventWindows
 * @author Alex Sorici
 *
 */
public interface TemporalEntity {
	
	/**
	 * @return Return the start of the interval as a unix timestamp
	 */
	long getStart();
	
	/**
	 * 
	 * @return Return the end of the interval as a unix timestamp
	 */
	long getEnd();
	
	/**
	 * 
	 * @return Return the duration of the interval as a unix timestamp
	 */
	long getDuration();
	
	/**
	 * Specifies whether the interval is in fact a single point in time, i.e. start and end are the same
	 * @return <code>True</code>, whether the interval refers to an atomic time point and <code>False</code> otherwise
	 */
	boolean isSinglePoint();
	
	/**
	 * 
	 * @return <code>True</code>, if the interval has an upper bound and <code>False</code> otherwise
	 */
	boolean hasUpperBound();
	
	/**
	 * 
	 * @return <code>True</code>, if the interval has a lower bound and <code>False</code> otherwise
	 */
	boolean hasLowerBound();
	
	/**
	 * Specifies whether the interval has no upper and lower bound
	 * @return <code>True</code>, if the interval is unbounded and <code>False</code> otherwise
	 */
	boolean isInfinite();
	
}
