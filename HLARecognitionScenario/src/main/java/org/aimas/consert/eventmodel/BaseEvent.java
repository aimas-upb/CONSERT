package org.aimas.consert.eventmodel;

public abstract class BaseEvent {
	
	public enum EventGenerationType {
		DERIVED,
		PROFILED,
		SENSED
	}
	
	AnnotationInfo annotations;
	
	double startTimestamp;
	long eventDuration;
	
	EventGenerationType generationType = EventGenerationType.SENSED; 
	
	public BaseEvent() {}
	
	public BaseEvent(AnnotationInfo annotations, EventGenerationType generationType) {
		this.generationType = generationType;
		setAnnotations(annotations);
	}
	
	private void setOccurrenceInfo(AnnotationInfo annotations) {
		if (annotations != null && annotations.getStartTime() != null) {
			startTimestamp = annotations.getStartTime().getTimeInMillis();
	    
	    	if (annotations.getEndTime() != null) {
	    		eventDuration = annotations.getDuration();
	    	}
		}
	}
	
	public EventGenerationType getGenerationType() {
		return generationType;
	}

	public void setGenerationType(EventGenerationType generationType) {
		this.generationType = generationType;
	}

	
	public boolean isDerived() {
		return generationType == EventGenerationType.DERIVED;
	}
	
	public boolean isSensed() {
		return generationType == EventGenerationType.SENSED;
	}
	
	public AnnotationInfo getAnnotations() {
		return annotations;
	}

	public void setAnnotations(AnnotationInfo annotations) {
		this.annotations = annotations;
		setOccurrenceInfo(annotations);
	}
	
	public double getStartTimestamp() {
		return startTimestamp;
	}

	public long getEventDuration() {
		return eventDuration;
	}
	
	public abstract double getConfidenceValueThreshold();
	
	public abstract double getConfidenceDiffThreshold();
	
	public abstract long getTimestampThreshold();
	
	public abstract boolean allowsContentContinuity(BaseEvent event);
	
	public abstract boolean allowsAnnotationContinuity(AnnotationInfo annotations);
	
	public abstract int getContentHash();
	
	
	public abstract String getStreamName();
	
	public abstract String getExtendedStreamName();
	
	
	public boolean isOverlappedBy(BaseEvent event) {
	    if (getAnnotations() == null)
	    	return false;
	    
	    if (getAnnotations().getEndTime() == null)
	    	return false;
	    
	    if (event.getAnnotations() == null)
	    	return false;
	    
	    if (event.getAnnotations().getEndTime() == null)
	    	return false;
		
		if (getAnnotations().getStartTime().equals(event.getAnnotations().getStartTime()) && 
				getAnnotations().getEndTime().compareTo(event.getAnnotations().getEndTime()) <= 0)
			return true;
		else
			return false;
    }
}
