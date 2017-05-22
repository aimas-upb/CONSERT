package org.aimas.consert.eventmodel;
/*
 * Abstract class for modeling a low level activity.
 */
public abstract class LLA extends BaseEvent
{
    
	public static final double CONFIDENCE_VALUE_THRESHOLD 	= 0.5;
	public static final double CONFIDENCE_DIFF_THRESHOLD 	= 0.3;
	
	public static final long TIMESTAMP_DIFF_THRESHOLD 		= 5000;		// in ms
	
	
	public enum Type
    {
        SITTING, STANDING, WALKING
    }

    Person person;                  /* the person which does the LLA */
    Type type;                      /* LLA type */

    protected LLA(Type type) {
    	this.type = type;
    }

    protected LLA(Person person, Type type, AnnotationInfo annotations) {
    	super(annotations);
        this.person = person;
        this.type = type;
    }

    public Type getType()
    {
        return type;
    }

    public void setType(Type type)
    {
        this.type = type;
    }


    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
    
    @Override
    public int getContentHash() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((person == null) ? 0 : person.hashCode());
	    result = prime * result + ((type == null) ? 0 : type.hashCode());
	    return result;
    }
    
    
    @Override
    public String toString() {
        return "LLA [" + "person=" + person + ", type=" + type + ", annotations=" + annotations + "]";
    }
    
    @Override
    public boolean allowsContentContinuity(BaseEvent event) {
    	LLA otherEvent = (LLA)event;
    	
    	if (type == otherEvent.getType() && person.equals(otherEvent.getPerson())) {
    		return true;
    	}
    	
    	return false;
    }
    
    
    public boolean allowsAnnotationContinuity(AnnotationInfo otherAnnotations) {
    	// check timestamp continuity
    	if (!AnnotationUtils.allowsTimestampContinuity(
    			annotations.getEndTime().getTimeInMillis(), 
    			otherAnnotations.getStartTime().getTimeInMillis(), 
    			getTimestampThreshold())) 
    		return false;
    		
    	// check confidence continuity
    	if (!AnnotationUtils.allowsConfidenceContinuity(
    			otherAnnotations.getConfidence(), 
    			getConfidenceValueThreshold())) 
    		return false;
    	
    	if (!AnnotationUtils.allowsConfidenceContinuity(
    			annotations.getConfidence(), 
    			otherAnnotations.getConfidence(), 
    			getConfidenceDiffThreshold())) 
    		return false;
    	
    	return true;
    }
}

