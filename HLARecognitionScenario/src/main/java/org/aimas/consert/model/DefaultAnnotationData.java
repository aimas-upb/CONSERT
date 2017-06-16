package org.aimas.consert.model;
import java.util.Calendar;

/*
 * Class for modeling annotations information and metadata
 * when an atomic event arrives.
 */
public class DefaultAnnotationData implements AnnotationData {
    
	public static final double CONFIDENCE_VALUE_THRESHOLD 	= 0.5;
	public static final double CONFIDENCE_DIFF_THRESHOLD 	= 0.3;
	
	public static final long TIMESTAMP_DIFF_THRESHOLD 		= 5000;		// in ms
	
	
	double lastUpdated; 	/* last Updated time*/
    double confidence;  	/* confidence for the event */
    Calendar startTime; 	/* start time of the event */
    Calendar endTime; 		/* end time of the event */
    long duration;			/* duration of the event */
    
    public DefaultAnnotationData() {}
    
    public DefaultAnnotationData(double lastUpdated, double confidence, Calendar startTime, Calendar endTime) {
	    this.lastUpdated = lastUpdated;
	    this.confidence = confidence;
	    this.startTime = startTime;
	    this.endTime = endTime;
	    
	    setDuration(startTime, endTime);
    }

	public double getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(double lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }
    
    
	public void setDuration(long duration) {
		this.duration = duration;
	}

	
	private void setDuration(Calendar startTime, Calendar endTime) {
		duration = endTime.getTimeInMillis() - startTime.getTimeInMillis();
	}
	
	
	@Override
    public long getDuration() {
		return duration;
	}
    
    @Override
    public double getTimestamp() {
    	return lastUpdated;
    }
	
    
	@Override
    public String toString() {
        return "Annotations [" + "lastUpdated=" + (long)lastUpdated + ", confidence=" + confidence + ", startTime=" +
                startTime.getTimeInMillis() + ", endTime=" + endTime.getTimeInMillis() + "]";
    }
	

	@Override
	public boolean allowsAnnotationContinuity(AnnotationData annotationData) {
		DefaultAnnotationData otherAnnotations = (DefaultAnnotationData)annotationData;
		
    	// check timestamp continuity
    	if (!AnnotationUtils.allowsTimestampContinuity(
    			getEndTime().getTimeInMillis(), 
    			otherAnnotations.getStartTime().getTimeInMillis(), 
    			TIMESTAMP_DIFF_THRESHOLD)) 
    		return false;
    		
    	// check confidence continuity
    	if (!AnnotationUtils.allowsConfidenceContinuity(
    			otherAnnotations.getConfidence(), 
    			CONFIDENCE_VALUE_THRESHOLD)) 
    		return false;
    	
    	if (!AnnotationUtils.allowsConfidenceContinuity(
    			getConfidence(), 
    			otherAnnotations.getConfidence(), 
    			CONFIDENCE_DIFF_THRESHOLD)) 
    		return false;
    	
    	return true;
    }
	
	@Override
    public boolean allowsAnnotationInsertion() {
	    return AnnotationUtils.allowsConfidenceContinuity(
	    		getConfidence(), 
	    		CONFIDENCE_VALUE_THRESHOLD);
    }

	@Override
    public AnnotationData applyCombinationOperator(AnnotationData otherAnn) {
		DefaultAnnotationData ann = (DefaultAnnotationData)otherAnn;
		
		double maxTimestamp = AnnotationUtils.maxTimestamp(getLastUpdated(),  ann.getLastUpdated());
		double maxConfidence = AnnotationUtils.maxConfidence(getConfidence(), ann.getConfidence());
		
		ValidityInterval hlaInterval = AnnotationUtils.computeIntersection(
                getStartTime(), getEndTime(),
                ann.getStartTime(), ann.getEndTime());
		
		DefaultAnnotationData updatedAnnotations = new DefaultAnnotationData(
				maxTimestamp, maxConfidence, 
				hlaInterval.getStart(),
				hlaInterval.getEnd()
		);
		
		return updatedAnnotations;
    }

	
	@Override
    public AnnotationData applyExtensionOperator(AnnotationData otherAnn) {
		DefaultAnnotationData ann = (DefaultAnnotationData)otherAnn;
		
		double maxTimestamp = AnnotationUtils.maxTimestamp(getLastUpdated(),  ann.getLastUpdated());
		
		double meanConfidence = AnnotationUtils.meanConfidence(getConfidence(), ann.getConfidence());
		
		DefaultAnnotationData updatedAnnotations = new DefaultAnnotationData(
				maxTimestamp, meanConfidence, 
				getStartTime(),
				ann.getEndTime()
		);
		
		return updatedAnnotations;
    }

	@Override
    public boolean hasSameValidity(AnnotationData otherAnn) {
		DefaultAnnotationData ann = (DefaultAnnotationData)otherAnn;
		
		if (getStartTime().equals(ann.getStartTime()) && getEndTime().equals(ann.getEndTime())) {
			return true;
		}
	    
		return false;
    }
	
	
	
}
