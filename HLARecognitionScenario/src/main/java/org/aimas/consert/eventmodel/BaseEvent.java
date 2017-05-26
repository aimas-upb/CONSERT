package org.aimas.consert.eventmodel;

public abstract class BaseEvent {
	
	AnnotationInfo annotations;
	
	double startTimestamp;
	long eventDuration;
	long processingTimeStamp;
	public BaseEvent() {}
	
	public BaseEvent(AnnotationInfo annotations) {
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

    public void setProcessingTimeStamp(long processingTimeStamp) {
        this.processingTimeStamp = processingTimeStamp;
    }

	public long getProcessingTimeStamp() {
		return processingTimeStamp;
	}
}
