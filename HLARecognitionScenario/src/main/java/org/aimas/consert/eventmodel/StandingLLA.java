package org.aimas.consert.eventmodel;
/*
 * Class for modeling a standing  low level activity
*/
public class StandingLLA extends LLA {
    public StandingLLA() {
        super(Type.STANDING);
    }

    public StandingLLA(Person person, AnnotationInfo context) {
        super(person, Type.STANDING, context);
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
