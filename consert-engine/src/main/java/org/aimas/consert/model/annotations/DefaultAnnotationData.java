package org.aimas.consert.model.annotations;
import java.util.Date;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;

/*
 * Class for modeling annotations information and metadata
 * when an atomic event arrives.
 */

@RDFBean("annotation:DefaultAnnotationData")
public class DefaultAnnotationData implements AnnotationData {
    
	public static final double CONFIDENCE_VALUE_THRESHOLD 	= 0.5;
	public static final double CONFIDENCE_DIFF_THRESHOLD 	= 0.3;
	
	public static final long TIMESTAMP_DIFF_THRESHOLD 		= 10000;		// in ms
	
	public static final long MAX_DURATION = Long.MAX_VALUE;
	
	double lastUpdated; 	/* last Updated time*/
    double confidence;  	/* confidence for the event */
    Date startTime; 	/* start time of the event */
    Date endTime; 		/* end time of the event */
    long duration;			/* duration of the event */
    
    public DefaultAnnotationData() {}
    
    public DefaultAnnotationData(double lastUpdated) {
    	this.lastUpdated = lastUpdated;
    	this.confidence = 1;
    	
    	this.startTime = new Date((long)lastUpdated);
    	this.endTime = this.startTime;
    	
    	setDuration(startTime, endTime);
    }
    
    public DefaultAnnotationData(double lastUpdated, double confidence) {
    	this.lastUpdated = lastUpdated;
    	this.confidence = confidence;
    	
    	this.startTime = new Date((long)lastUpdated);
    	this.endTime = this.startTime;
    	
    	setDuration(startTime, endTime);
    }
    
    public DefaultAnnotationData(double lastUpdated, double confidence, Date startTime, Date endTime) {
	    this.lastUpdated = lastUpdated;
	    this.confidence = confidence;
	    this.startTime = startTime;
	    this.endTime = endTime;
	    
	    setDuration(startTime, endTime);
    }

    @RDF("annotation:lastUpdated")
	public double getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(double lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @RDF("annotation:confidence")
    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    @RDF("annotation:endTime")
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
        setDuration(this.startTime, this.endTime);
    }

    @RDF("annotation:startTime")
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
        setDuration(this.startTime, this.endTime);
    }
    
    
	public void setDuration(long duration) {
		this.duration = duration;
	}

	
	private void setDuration(Date startTime, Date endTime) {
		// if any of the endpoints are null, we consider the interval to be infinitely long
		if (startTime == null || endTime == null) {
			duration = MAX_DURATION;
		}
		else {
			duration = endTime.getTime() - startTime.getTime();
		}
	}
	
	
	@Override
    public long getDuration() {
		return duration;
	}

    @Override
    public double getTimestamp() {
    	return lastUpdated;
    }
    
    public void setTimestamp(double timestamp) {
    	this.setLastUpdated(timestamp);
    }
	
    
	@Override
    public String toString() {
        return "Annotations [" + "lastUpdated=" + (long)lastUpdated + ", confidence=" + confidence + ", startTime=" +
                startTime.getTime() + ", endTime=" + endTime.getTime() + "]";
    }
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
    @Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    long temp;
	    
	    temp = Double.doubleToLongBits(lastUpdated);
	    result = prime * result + (int) (temp ^ (temp >>> 32));
	    
	    temp = Double.doubleToLongBits(confidence);
	    result = prime * result + (int) (temp ^ (temp >>> 32));
	    result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
	    result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
	    
	    return result;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
    @Override
    public boolean equals(Object obj) {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    
	    DefaultAnnotationData other = (DefaultAnnotationData) obj;
	    
	    if (Double.doubleToLongBits(lastUpdated) != Double.doubleToLongBits(other.lastUpdated))
		    return false;
	    
	    if (Double.doubleToLongBits(confidence) != Double.doubleToLongBits(other.confidence))
		    return false;
	    
	    if (startTime == null) {
		    if (other.startTime != null)
			    return false;
	    }
	    else if (!startTime.equals(other.startTime))
		    return false;
	    
	    if (endTime == null) {
		    if (other.endTime != null)
			    return false;
	    }
	    else if (!endTime.equals(other.endTime))
		    return false;
	    
	    return true;
    }

	@Override
	public boolean allowsAnnotationContinuity(AnnotationData annotationData) {
		DefaultAnnotationData otherAnnotations = (DefaultAnnotationData)annotationData;
		
    	// check timestamp continuity
    	if (!AnnotationUtils.allowsTimestampContinuity(
    			getEndTime().getTime(), 
    			otherAnnotations.getStartTime().getTime(), 
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
		
		DatetimeInterval hlaInterval = AnnotationUtils.computeIntersection(
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
