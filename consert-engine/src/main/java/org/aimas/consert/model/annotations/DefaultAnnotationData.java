package org.aimas.consert.model.annotations;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.LinkedList;

import org.aimas.consert.model.content.ContextAssertion;
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
	

    long duration;			/* duration of the event */
    
    public DefaultAnnotationData() {}

    public DefaultAnnotationData(double lastUpdated, double confidence) {


    	//setDuration(startTime, endTime);
    }

    public DefaultAnnotationData(double lastUpdated, double confidence, Date startTime, Date endTime) {


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

		for (int i=0; i<this.size(); i++) {
			for (int j = 0; j < ((DefaultAnnotationData) annotationData).size(); j++) {
				if (((DefaultAnnotationData) annotationData).get(j).getClass().equals(this.get(i).getClass())) {
					String method = ((StructuredAnnotation) (otherAnnotations).get(j)).getContinuityFunction();
					Method meth = null;
					try {
						meth = AnnotationUtils.class.getMethod(method, this.get(i).getValue().getClass(), this.get(i).getValue().getClass());
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
					Object rez = new Object();
					Object[] Args = new Object[2];
					Args[0] = ((StructuredAnnotation) ((DefaultAnnotationData) this).get(i)).getValue();
					Args[1] = ((StructuredAnnotation) ((DefaultAnnotationData) otherAnnotations).get(j)).getValue();
					try {
						rez = meth.invoke(AnnotationUtils.class, Args);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					if ((boolean) rez == false) {
						return false;
					}
				}
			}
		}

    	// check confidence continuity
    	/*for (int i=0; i<this.size(); i++) {
			if (this.get(i) instanceof NumericCertaintyAnnotation) {
				for (int j = 0; j < ((DefaultAnnotationData) annotationData).size(); j++) {
					if (((DefaultAnnotationData) annotationData).get(j) instanceof NumericCertaintyAnnotation)
						if (!((NumericCertaintyAnnotation) this.get(i)).allowsContinuity((NumericCertaintyAnnotation) ((DefaultAnnotationData) annotationData).get(j)))
							return false;
				}
			}
		}*/
    	return true;
    	//return false;

    }
	
	@Override
    public boolean allowsAnnotationInsertion() {
		for (int i=0; i<this.size(); i++) {
			if (this.get(i) instanceof NumericCertaintyAnnotation)
				if (!((NumericCertaintyAnnotation) this.get(i)).allowsInsertion())
					return false;
		}
		return true;
    }

	@Override
    public AnnotationData applyCombinationOperator(AnnotationData otherAnn) {
		DefaultAnnotationData ann = (DefaultAnnotationData)otherAnn;
		DefaultAnnotationData updatedAnnotations = new DefaultAnnotationData();

		for (int i=0; i<this.size(); i++) {
			for (int j = 0; j < ((DefaultAnnotationData) otherAnn).size(); j++) {
				if (((DefaultAnnotationData) otherAnn).get(j).getClass().equals(this.get(i).getClass())) {
					String method = ((StructuredAnnotation) ((DefaultAnnotationData) otherAnn).get(j)).getCombinationOperator();
					Method meth = null;
					try {
						meth = AnnotationUtils.class.getMethod(method, this.get(i).getValue().getClass(), this.get(i).getValue().getClass());
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
					Object rez = new Object();
					Object[] Args = new Object[2];

					Args[0] = ((StructuredAnnotation) ((DefaultAnnotationData) this).get(i)).getValue();
					Args[1] = ((StructuredAnnotation) ((DefaultAnnotationData) otherAnn).get(j)).getValue();
					try {
						rez = meth.invoke(AnnotationUtils.class, Args);
						String name = ((DefaultAnnotationData) this).get(i).getClass().toString().substring(6);
						Class<?> aClass = Class.forName(name);
						Constructor<?> ctor = aClass.getConstructor(this.get(i).getValue().getClass(),String.class,String.class, String.class);
						Object object = ctor.newInstance(rez, ((StructuredAnnotation) ((DefaultAnnotationData) otherAnn).get(j)).getContinuityFunction(),
								((StructuredAnnotation) ((DefaultAnnotationData) otherAnn).get(j)).getExtensionOperator(), method);
						updatedAnnotations.add((ContextAnnotation) object);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return updatedAnnotations;
    }

	
	@Override
    public AnnotationData applyExtensionOperator(AnnotationData otherAnn) {
		DefaultAnnotationData ann = (DefaultAnnotationData)otherAnn;
		DefaultAnnotationData updatedAnnotations = new DefaultAnnotationData();

		for (int i=0; i<this.size(); i++) {
			for (int j = 0; j < ((DefaultAnnotationData) otherAnn).size(); j++) {
				if (((DefaultAnnotationData) otherAnn).get(j).getClass().equals(this.get(i).getClass())) {
					String method = ((StructuredAnnotation) ((DefaultAnnotationData) otherAnn).get(j)).getExtensionOperator();
					Method meth = null;
					try {
						meth = AnnotationUtils.class.getMethod(method, this.get(i).getValue().getClass(), this.get(i).getValue().getClass());
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					}
					Object rez = new Object();
					Object[] Args = new Object[2];

					Args[0] = ((StructuredAnnotation) ((DefaultAnnotationData) this).get(i)).getValue();
					Args[1] = ((StructuredAnnotation) ((DefaultAnnotationData) otherAnn).get(j)).getValue();
					try {
						rez = meth.invoke(AnnotationUtils.class, Args);
						String name = ((DefaultAnnotationData) this).get(i).getClass().toString().substring(6);
						Class<?> aClass = Class.forName(name);
						Constructor<?> ctor = aClass.getConstructor(this.get(i).getValue().getClass(),String.class,String.class, String.class);
						Object object = ctor.newInstance(rez, ((StructuredAnnotation) ((DefaultAnnotationData) otherAnn).get(j)).getContinuityFunction(),
								method,   ((StructuredAnnotation) ((DefaultAnnotationData) otherAnn).get(j)).getCombinationOperator());
						updatedAnnotations.add((ContextAnnotation) object);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					}
				}
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
}
