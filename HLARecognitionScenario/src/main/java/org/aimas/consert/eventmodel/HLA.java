package org.aimas.consert.eventmodel;


/*
 *  Abstract class for modeling a high level activity.
 */
public abstract class HLA extends BaseEvent 
{

	public static final double CONFIDENCE_VALUE_THRESHOLD 	= 0.5;
	public static final double CONFIDENCE_DIFF_THRESHOLD 	= 0.3;
	
	public static final long TIMESTAMP_DIFF_THRESHOLD 		= 5000;		// in ms
	
	public enum Type
    {
        DISCUSSING, EXERCISE, WORKING, DINING
    }
	
	
	Person person;                  /* the person which does the HLA */
    Type type;                      /* HLA type */
    
    protected HLA(Type type) {
    	this.type = type;
    }

    protected HLA(Person person, Type type, AnnotationInfo annotations) {
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
        return "HLA [" + "person=" + person + ",  type=" + type + ", annotations=" + annotations + "]";
    }
    
    
    @Override
    public boolean allowsContentContinuity(BaseEvent event) {
    	HLA otherEvent = (HLA)event;
    	
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
