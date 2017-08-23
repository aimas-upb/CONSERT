package org.aimas.consert.model.annotations;
import java.util.Date;
import java.util.LinkedList;

import org.aimas.consert.model.content.ContextAssertion;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;

/*
 * Class for modeling annotations information and metadata
 * when an atomic event arrives.
 */

@RDFBean("annotation:DefaultAnnotationData")
public class DefaultAnnotationData extends LinkedList<ContextAnnotation> implements AnnotationData  {
    
	public static final double CONFIDENCE_VALUE_THRESHOLD 	= 0.5;
	public static final double CONFIDENCE_DIFF_THRESHOLD 	= 0.3;
	
	public static final long TIMESTAMP_DIFF_THRESHOLD 		= 10000;		// in ms
	
	
	double lastUpdated; 	/* last Updated time*/
    double confidence;  	/* confidence for the event */
    Date startTime; 	/* start time of the event */
    Date endTime; 		/* end time of the event */
    long duration;			/* duration of the event */
    
    public DefaultAnnotationData() {}
    
    public DefaultAnnotationData(double lastUpdated, double confidence) {
    	this.lastUpdated = lastUpdated;
    	this.confidence = confidence;
    	
    	this.startTime = new Date((long)lastUpdated);
    	this.endTime = this.startTime;
    	
    	//setDuration(startTime, endTime);
    }
    
    public DefaultAnnotationData(double lastUpdated, double confidence, Date startTime, Date endTime) {
	    this.lastUpdated = lastUpdated;
	    this.confidence = confidence;
	    this.startTime = startTime;
	    this.endTime = endTime;
	    
	   // setDuration(startTime, endTime);
    }

    @RDF("annotation:lastUpdated")
	public double getLastUpdated() {
		return getTimestamp();
    }

    public void setLastUpdated(double lastUpdated) {
		setTimestamp(lastUpdated);
    }

    @RDF("annotation:confidence")
    public double getConfidence() {
		for (int i= 0; i< this.size(); i++)
		{
			if (this.get(i) instanceof NumericCertaintyAnnotation)
				return ((NumericCertaintyAnnotation) this.get(i)).getValue();
		}
		return 0;
    }

    public void setConfidence(double confidence) {
		for (int i= 0; i< this.size(); i++)
		{
			if (this.get(i) instanceof NumericCertaintyAnnotation)
				((NumericCertaintyAnnotation) this.get(i)).setValue(confidence);
		}
    }

    @RDF("annotation:endTime")
    public Date getEndTime() {
		//System.out.println(this.size());
		for (int i= 0; i< this.size(); i++)
		{
			//System.out.println(this.get(i).getClass());
			if (this.get(i) instanceof TemporalValidityAnnotation)
			{
				if (((TemporalValidityAnnotation) this.get(i)).getValue().getEnd()!=null)
				return ((TemporalValidityAnnotation) this.get(i)).getValue().getEnd();
			}

		}
		return new Date(0);
    }

    public void setEndTime(Date endTime) {
    	DatetimeInterval date  = new DatetimeInterval();
    	date.setStart(getStartTime());
		date.setEnd(endTime);
		for (int i= 0; i< this.size(); i++) {
			if (this.get(i) instanceof TemporalValidityAnnotation)
				((TemporalValidityAnnotation) this.get(i)).setValue(date);
		}
    }

    @RDF("annotation:startTime")
    public Date getStartTime() {
		for (int i= 0; i< this.size(); i++)
		{
			if (this.get(i) instanceof TemporalValidityAnnotation)
			{
				if (((TemporalValidityAnnotation) this.get(i)).getValue().getStart()!=null)
				return ((TemporalValidityAnnotation) this.get(i)).getValue().getStart();
			}

		}
		return new Date(0);
    }

    public void setStartTime(Date startTime) {
		DatetimeInterval date  = new DatetimeInterval();
		date.setStart(startTime);
		date.setEnd(getEndTime());
		for (int i= 0; i< this.size(); i++) {
			if (this.get(i) instanceof TemporalValidityAnnotation)
				((TemporalValidityAnnotation) this.get(i)).setValue(date);
		}
    }
    
    
	public void setDuration(long duration) {
		this.duration = duration;
	}

	
	private void setDuration(Date startTime, Date endTime) {
		duration = endTime.getTime() - startTime.getTime();
	}

	
	
	@Override
    public long getDuration() {
    	if (getEndTime()!=null && getStartTime() != null)
			return getEndTime().getTime() - getStartTime().getTime();
    	return 0;
	}

    @Override
    public double getTimestamp() {
		for (int i= 0; i< this.size(); i++)
		{
			if (this.get(i) instanceof NumericTimestampAnnotation)
			{
				return ((NumericTimestampAnnotation) this.get(i)).getValue();
			}

		}
		//System.out.println("n-a gasit nimik!");
		return 0;
    }
    
    public void setTimestamp(double timestamp) {
		for (int i= 0; i< this.size(); i++)
		{
			if (this.get(i) instanceof NumericTimestampAnnotation)
				((NumericTimestampAnnotation) this.get(i)).setValue(lastUpdated);
		}
    }
	
    
	@Override
    public String toString() {
        return "Annotations [" + "lastUpdated=" + (long)lastUpdated + ", confidence=" + confidence + ", startTime=" +
                getStartTime().getTime() + ", endTime=" + getEndTime().getTime() + "]";
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
    	//return false;

    }
	
	@Override
    public boolean allowsAnnotationInsertion() {
	    return AnnotationUtils.allowsConfidenceContinuity(
	    		getConfidence(), 
	    		CONFIDENCE_VALUE_THRESHOLD);
		//return false;
    }

	@Override
    public AnnotationData applyCombinationOperator(AnnotationData otherAnn) {
		DefaultAnnotationData ann = (DefaultAnnotationData)otherAnn;
		
		double maxTimestamp = AnnotationUtils.maxTimestamp(getLastUpdated(),  ann.getLastUpdated());
		double maxConfidence = AnnotationUtils.maxConfidence(getConfidence(), ann.getConfidence());
		
		DatetimeInterval hlaInterval = AnnotationUtils.computeIntersection(
                getStartTime(), getEndTime(),
                ann.getStartTime(), ann.getEndTime());
		
		DefaultAnnotationData updatedAnnotations = new DefaultAnnotationData();

		NumericCertaintyAnnotation NumCertAnn = new NumericCertaintyAnnotation();
		NumCertAnn.setValue(maxConfidence);
		updatedAnnotations.add(NumCertAnn);

		NumericTimestampAnnotation NumTimeAnn = new NumericTimestampAnnotation();
		NumTimeAnn.setValue(maxTimestamp);
		updatedAnnotations.add(NumTimeAnn);

		TemporalValidityAnnotation TempValAnn = new TemporalValidityAnnotation();
		TempValAnn.setValue(hlaInterval);
		updatedAnnotations.add(TempValAnn);

		return updatedAnnotations;
    }

	
	@Override
    public AnnotationData applyExtensionOperator(AnnotationData otherAnn) {
		DefaultAnnotationData ann = (DefaultAnnotationData)otherAnn;

		double maxTimestamp = AnnotationUtils.maxTimestamp(getLastUpdated(),  ann.getLastUpdated());
		
		double meanConfidence = AnnotationUtils.meanConfidence(getConfidence(), ann.getConfidence());

		DatetimeInterval DateInt = new DatetimeInterval(startTime, ann.getEndTime());

		DefaultAnnotationData updatedAnnotations = new DefaultAnnotationData();

		NumericCertaintyAnnotation NumCertAnn = new NumericCertaintyAnnotation();
		NumCertAnn.setValue(meanConfidence);
		updatedAnnotations.add(NumCertAnn);

		NumericTimestampAnnotation NumTimeAnn = new NumericTimestampAnnotation();
		NumTimeAnn.setValue(maxTimestamp);
		updatedAnnotations.add(NumTimeAnn);

		TemporalValidityAnnotation TempValAnn = new TemporalValidityAnnotation();
		TempValAnn.setValue(DateInt);
		updatedAnnotations.add(TempValAnn);

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
