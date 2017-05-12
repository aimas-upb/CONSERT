package org.aimas.consert.eventmodel;
/*
 *Class for modeling a positioning event
*/
public class Position extends BaseEvent {
    
	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((person == null) ? 0 : person.hashCode());
	    result = prime * result + ((type == null) ? 0 : type.hashCode());
	    return result;
    }

	public static final double CONFIDENCE_VALUE_THRESHOLD 	= 0.5;
	public static final double CONFIDENCE_DIFF_THRESHOLD 	= 0.3;
	
	public static final long TIMESTAMP_DIFF_THRESHOLD 		= 5000;		// in ms
	
	public enum Type {
        WORK_AREA,
        DINING_AREA,
        SITTING_AREA,
        CONFERENCE_AREA,
        ENTERTAINMENT_AREA,
        SNACK_AREA,
        EXERCISE_AREA,
        HYGENE_AREA;
    }

    Person person;                  /* The person which is implied in the positioning event */
    Type type;                      /* Positioning type*/

    public Position() {
    }

    public Position(Person person, Type type, AnnotationInfo annotations) {
        super(annotations);
    	this.person = person;
        this.type = type;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
    
    public int getContentHash() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((person == null) ? 0 : person.hashCode());
	    result = prime * result + ((type == null) ? 0 : type.hashCode());
	    return result;
    }
    
    @Override
    public String toString() {
        return "Position [" + "person=" + person + ", type=" + type + ", annotations=" + annotations + "]\n";
    }
    
    @Override
    public boolean allowsContentContinuity(BaseEvent event) {
    	Position otherEvent = (Position)event;
    	
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

	@Override
    public double getConfidenceValueThreshold() {
	    return CONFIDENCE_VALUE_THRESHOLD;
    }

	@Override
    public double getConfidenceDiffThreshold() {
	    return CONFIDENCE_DIFF_THRESHOLD;
    }

	@Override
    public long getTimestampThreshold() {
	    return TIMESTAMP_DIFF_THRESHOLD;
    }


}
