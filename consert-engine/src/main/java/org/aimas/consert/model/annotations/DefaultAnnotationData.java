package org.aimas.consert.model.annotations;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.eclipse.rdf4j.query.algebra.Str;

/*
 * Class for modeling annotations information and metadata
 * when an atomic event arrives.
 */

@RDFBean("annotation:DefaultAnnotationData")
public class DefaultAnnotationData extends LinkedList<ContextAnnotation> implements AnnotationData  {
    
	public static final double CONFIDENCE_VALUE_THRESHOLD 	= 0.5;
	public static final double CONFIDENCE_DIFF_THRESHOLD 	= 0.3;
	
	public static final long TIMESTAMP_DIFF_THRESHOLD 		= 10000;		// in ms
	private Map<String, ContextAnnotation> Annotations;

    long duration;			/* duration of the event */
    
    public DefaultAnnotationData() {
    	Annotations = new HashMap<String, ContextAnnotation>();
	}

	public  Map<String, ContextAnnotation> getAnnotationsMap()
	{
		return Annotations;
	}
    @Override
	public boolean add(ContextAnnotation ctx)
	{
		super.add(ctx);
		Annotations.put(ctx.getClass().toString(), ctx);

		return true;
	}

	@Override
	public boolean remove(Object ctx)
	{
		super.remove(ctx);
		Annotations.remove(ctx.getClass().toString());

		return true;
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
		for (int i= 0; i< this.size(); i++)
		{
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
				{
					return ((TemporalValidityAnnotation) this.get(i)).getValue().getStart();
				}
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
		return 0;
    }
    
    public void setTimestamp(double timestamp) {
		for (int i= 0; i< this.size(); i++)
		{
			if (this.get(i) instanceof NumericTimestampAnnotation)
				((NumericTimestampAnnotation) this.get(i)).setValue(getTimestamp());
		}
    }
	
    
	@Override
    public String toString() {
        return "Annotations [" + "lastUpdated=" + (long) getTimestamp() + ", confidence=" + getConfidence() + ", startTime=" +
                getStartTime().getTime() + ", endTime=" + getEndTime().getTime() + "]";
    }
	

	@Override
	public boolean allowsAnnotationContinuity(AnnotationData annotationData) {
		DefaultAnnotationData otherAnnotations = (DefaultAnnotationData)annotationData;

		for (String str: Annotations.keySet())
		{
			if (otherAnnotations.getAnnotationsMap().get(str)!=null)
			{
				if ( !((StructuredAnnotation)Annotations.get(str)).allowsContinuity(((StructuredAnnotation)otherAnnotations.getAnnotationsMap().get(str))))
				{
					return false;
				}
			}
		}

    	return true;

    }
	
	@Override
    public boolean allowsAnnotationInsertion() {
		ContextAnnotation ctx = Annotations.get(new NumericCertaintyAnnotation().getClass().toString());
		if (ctx!=null)
			if (!((NumericCertaintyAnnotation)ctx).allowsInsertion())
					return false;
		return true;
    }

	@Override
    public AnnotationData applyCombinationOperator(AnnotationData otherAnn) {
		DefaultAnnotationData ann = (DefaultAnnotationData)otherAnn;
		DefaultAnnotationData updatedAnnotations = new DefaultAnnotationData();

		for (String str: Annotations.keySet()) {
			if (ann.getAnnotationsMap().get(str) != null) {
				ContextAnnotation ctx = ((StructuredAnnotation) Annotations.get(str)).applyCombinationOperator(((StructuredAnnotation) ann.getAnnotationsMap().get(str)));
				updatedAnnotations.add(ctx);
			}
		}

		return updatedAnnotations;
    }

	
	@Override
    public AnnotationData applyExtensionOperator(AnnotationData otherAnn) {
		DefaultAnnotationData ann = (DefaultAnnotationData)otherAnn;
		DefaultAnnotationData updatedAnnotations = new DefaultAnnotationData();

		for (String str: Annotations.keySet()) {
			if (ann.getAnnotationsMap().get(str) != null) {
				ContextAnnotation ctx = ((StructuredAnnotation) Annotations.get(str)).applyExtensionOperator(((StructuredAnnotation) ann.getAnnotationsMap().get(str)));
				updatedAnnotations.add(ctx);
			}
		}

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

	@Override
	public List<ContextAnnotation> listAnnotations() {
		return this;
	}
}
