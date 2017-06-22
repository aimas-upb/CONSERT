package org.aimas.consert.model;

import java.util.Map;

public abstract class ContextAssertion {
	
	/* ================== ContextAssertion characterization ================== */
	public enum AcquisitionType {
		DERIVED,
		PROFILED,
		SENSED
	}
	
	public static final int UNARY = 1;
	public static final int BINARY = 2;
	public static final int NARY = 3;
	
	
	public ContextAssertion() {}
	
	public ContextAssertion(AcquisitionType acquisitionType, int arity, AnnotationData annotationData) {
		this.acquisitionType = acquisitionType;
		this.arity = arity;
		setAnnotations(annotationData);
	}
	
	
	/* ================== ContextAssertion content ================== */
	protected abstract Map<String, ContextEntity> getEntities();
	
	
	protected int arity = BINARY;
	protected AcquisitionType acquisitionType = AcquisitionType.SENSED;
	
	/**
	 * Get the acquisition method of the ContextAssertion
	 * @return the {@see org.aimas.consert.model.AcquisitionType} of the ContextAssertion
	 */
	public AcquisitionType getAcquisitionType() {
		return acquisitionType;
	}
	
	public void setAcquisitionType(AcquisitionType acquisitionType) {
		this.acquisitionType = acquisitionType;
	}
	
	public boolean isDerived() {
		return acquisitionType == AcquisitionType.DERIVED;
	}
	
	
	public boolean isSensed() {
		return acquisitionType == AcquisitionType.SENSED;
	}
	
	public boolean isProfiled() {
		return acquisitionType == AcquisitionType.PROFILED;
	}
	
	
	/**
	 * Get the arity of this ContextAssertion
	 * @return 1 for UnaryContextAssertion, 2 for binary and 3 for n-ary
	 */
	public int getAssertionArity() {
		return arity;
	}
	
	
	public boolean isUnary() {
		return arity == UNARY;
	}
	
	public boolean isBinary() {
		return arity == BINARY;
	}
	
	public boolean isNary() {
		return arity == NARY;
	}
	
	
	/* ================== ContextAssertion annotations ================== */
	protected AnnotationData annotationData;
	
	
	protected double startTimestamp;
	protected long eventDuration;
	protected long ID;

	private void setOccurrenceInfo(AnnotationData annotationData) {
		if (annotationData != null) {
			startTimestamp = annotationData.getTimestamp();
	    	eventDuration = annotationData.getDuration();
		}
	}
	
	
	/* ================== Internal performance monitoring ================== */ 
	protected long processingTimeStamp;
	
	public void setProcessingTimeStamp(long processingTimeStamp) {
        this.processingTimeStamp = processingTimeStamp;
    }

	public long getProcessingTimeStamp() {
		return processingTimeStamp;
	}

	public long getID() {
		return ID;
	}

	public void setID(long ID) {
		this.ID = ID;
	}
	public AnnotationData getAnnotations() {
		return annotationData;
	}

	public void setAnnotations(AnnotationData annotationData) {
		this.annotationData = annotationData;
		setOccurrenceInfo(annotationData);
	}
	
	public double getStartTimestamp() {
		return startTimestamp;
	}

	public long getEventDuration() {
		return eventDuration;
	}
	
	
	/* ================== Auxiliary methods ================== */ 
	public String getStreamName() {
		return getClass().getSimpleName() + "Stream";
	};
	
	public String getExtendedStreamName() {
		return "Extended" + getStreamName();
	}
	
	public int getContentHash() {
		final int prime = 31;
		int result = 1;
	    
		for (ContextEntity entity : getEntities().values()) {
			result = prime * result + ((entity == null) ? 0 : entity.hashCode());
		}
	    
	    return result;
	}
	
	
	public boolean allowsContentContinuity(ContextAssertion event) {
		if (event == null)
			return false;
		
		if (!getClass().equals(event.getClass()))
			return false;
		
		Map<String, ContextEntity> myEntities = getEntities();
		Map<String, ContextEntity> otherEntities = event.getEntities();
		
		for (String entityRole : myEntities.keySet()) {
			if (!otherEntities.containsKey(entityRole)) {
				return false;
			}
			else {
				Object myEntityVal = myEntities.get(entityRole).getValue();
				Object otherEntityVal = otherEntities.get(entityRole).getValue();
				
				if (!myEntityVal.equals(otherEntityVal))
					return false;
			}
		}
		
		return true;
	}
	
	public boolean isOverlappedBy(ContextAssertion event) {
	    return AnnotationUtils.isValidityOverlap(getAnnotations(), event.getAnnotations());
	}    

}
