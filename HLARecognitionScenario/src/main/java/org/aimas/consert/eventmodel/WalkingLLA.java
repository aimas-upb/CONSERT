package org.aimas.consert.eventmodel;
/*
 * Class for modeling a walking  low level activity
*/
public class WalkingLLA extends LLA {
    
	public WalkingLLA() {
        super(Type.WALKING);
    }

    public WalkingLLA(Person person, AnnotationInfo context) {
        super(person, Type.WALKING, context);
    }
    
    @Override
    public double getConfidenceValueThreshold() {
	    return LLA.CONFIDENCE_VALUE_THRESHOLD;
    }


	@Override
    public double getConfidenceDiffThreshold() {
	    return LLA.CONFIDENCE_DIFF_THRESHOLD;
    }


	@Override
    public long getTimestampThreshold() {
	    return LLA.TIMESTAMP_DIFF_THRESHOLD;
    }
}
