package org.aimas.consert.model.annotations;

import java.util.Calendar;
import java.util.Date;


public class AnnotationUtils {
	
	public static double meanConfidence(double... confidenceValues) {
		int nrVals = confidenceValues.length;
		
		if (nrVals == 0) 
			return 0;
		
		double sum = 0;
		for (double val : confidenceValues) {
			sum += val;
		}
		
		return sum / nrVals;
	}
	
	
	public static double maxConfidence(double... confidenceValues) {
		double max = 0;
		
		for (double val : confidenceValues) {
			if (val > max) 
				max = val;
		}
		
		return max;
	}
	
	
	public static double minConfidence(double... confidenceValues) {
		if (confidenceValues.length == 0)
			return 0;
		
		double min = confidenceValues[0];
		for (double val : confidenceValues) {
			if (val < min) 
				min = val;
		}
		
		return min;
	}
	
	
	public static double maxTimestamp(double... timestampValues) {
		double max = 0;
		
		for (double val : timestampValues) {
			if (val > max) 
				max = val;
		}
		
		return max;
	}
	
	
	public static double minTimestamp(double... timestampValues) {
		
		if (timestampValues.length == 0) 
			return 0;
		
		double min = timestampValues[0];
		for (double val : timestampValues) {
			if (val < min) 
				min = val;
		}
		
		return min;
	}
	
	
	public static boolean allowsTimestampContinuity(long firstEventEnd, long secondEventStart, long threshold) {
		return secondEventStart - firstEventEnd < threshold;
	}
	
	
	public static boolean allowsConfidenceContinuity(double confidence, double valueThreshold) {
		if (confidence < valueThreshold) 
			return false;
		
		return true;
	}
	
	
	public static boolean allowsConfidenceContinuity(double firstEventConfidence, double secondEventConfidence, double differenceThreshold) {
		if (Math.abs(firstEventConfidence - secondEventConfidence) > differenceThreshold)
			return false;
		
		return true;
	}
	
	
	public static DatetimeInterval computeIntersection(Date firstStart, Date firstEnd, Date secondStart, Date secondEnd) {
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
		
		return computeIntersection(firstStart.getTime(), firstEnd.getTime(), secondStart.getTime(), secondEnd.getTime());
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
