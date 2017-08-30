package org.aimas.consert.model.annotations;

import java.util.Calendar;
import java.util.Date;


public class AnnotationUtils {

	public static final double CONFIDENCE_VALUE_THRESHOLD 	= 0.5;
	public static final double CONFIDENCE_DIFF_THRESHOLD 	= 0.3;
	public static final long TIMESTAMP_DIFF_THRESHOLD 		= 10000;		// in ms

	
	public static double maxConfidence(Double c1, Double c2) {
		if (c1 > c2)
			return c1;
		return c2;
	}

	public static double avgConfidence(Double c1, Double c2) {
		return (c1 + c2) / 2;
	}
	
	public static double minConfidence(Double c1, Double c2) {
		if (c1 < c2) 
			return c1;
		return c2;
	}

	public static double maxTimestamp(Double t1, Double t2) {
		if (t1 > t2)
			return t1;
		return t2;
	}
	
	public static double minTimestamp(Double t1, Double t2) {
		if (t1 < t2)
			return t1;
		return t2;
	}

	public static DatetimeInterval extendTimeInterval(DatetimeInterval t1, DatetimeInterval t2)
	{
		DatetimeInterval t = new DatetimeInterval(t1.getStart(), t2.getEnd());
		return t;
	}
	
	public static DatetimeInterval computeIntersection(DatetimeInterval t1, DatetimeInterval t2) {

		Date firstStart = t1.getStart();
		Date firstEnd = t1.getEnd();
		Date secondStart = t2.getStart();
		Date secondEnd = t2.getEnd();

		Date intersectStart = null;
		Date intersectEnd = null;
		
		if (firstEnd.before(secondStart))
			return null;

		if (secondEnd.before(firstStart))
			return null;
		
		intersectStart = firstStart.before(secondStart) ? (Date)secondStart.clone() : (Date)firstStart.clone();		// max start
		intersectEnd = firstEnd.before(secondEnd) ? (Date)firstEnd.clone() : (Date)secondEnd.clone();				// min end
		
		return new DatetimeInterval(intersectStart, intersectEnd);
	}
	
	
	public static DatetimeInterval computeIntersection(long firstTsStart, long firstTsEnd, long secondTsStart, long secondTsEnd) {
		Calendar firstStart = Calendar.getInstance();
		firstStart.setTimeInMillis(firstTsStart);
		
		Calendar firstEnd = Calendar.getInstance();
		firstEnd.setTimeInMillis(firstTsEnd);
		
		Calendar secondStart = Calendar.getInstance();
		secondStart.setTimeInMillis(secondTsStart);
		
		Calendar secondEnd = Calendar.getInstance();
		secondEnd.setTimeInMillis(secondTsEnd);

		DatetimeInterval t1 = new DatetimeInterval(firstStart.getTime(), firstEnd.getTime());
		DatetimeInterval t2 = new DatetimeInterval(secondStart.getTime(), secondEnd.getTime());
		return computeIntersection(t1,t2);
	}
	
	public static boolean intersects(AnnotationData thisAnn, AnnotationData otherAnn) {
		if (thisAnn instanceof DefaultAnnotationData && otherAnn instanceof DefaultAnnotationData) {
			DefaultAnnotationData ann1 = (DefaultAnnotationData) thisAnn;
			DefaultAnnotationData ann2 = (DefaultAnnotationData) otherAnn;
			if (ann1.getEndTime()!=null && ann1.getStartTime()!=null && ann2.getEndTime()!=null && ann2.getStartTime()!=null)
				return intersects(ann1.getStartTime(), ann1.getEndTime(), ann2.getStartTime(), ann2.getEndTime());
		}
		
		return false;
	}
	
	public static boolean intersects(Date firstStart, Date firstEnd, Date secondStart, Date secondEnd) {
		return !(firstEnd.compareTo(secondStart) <= 0 || secondEnd.compareTo(firstStart) <= 0);
	}
	
	
	public static boolean isValidityOverlap(AnnotationData ann1, AnnotationData ann2) {
	    
		if (ann1 == null || !(ann1 instanceof DefaultAnnotationData) )
	    	return false;
	    
		if (ann2 == null || !(ann2 instanceof DefaultAnnotationData) )
	    	return false;
	    
		DefaultAnnotationData my = (DefaultAnnotationData)ann1;
		DefaultAnnotationData other = (DefaultAnnotationData)ann2;
		
	    if (my.getEndTime() == null)
	    	return false;
	    
	    if (other.getEndTime() == null)
	    	return false;
		
		if (my.getStartTime().equals(other.getStartTime()) && 
				my.getEndTime().compareTo(other.getEndTime()) <= 0)
			return true;
		else
			return false;
    }
}
