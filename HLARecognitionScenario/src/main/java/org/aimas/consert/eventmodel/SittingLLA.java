package org.aimas.consert.eventmodel;
/*
 * Class for modeling a sitting  low level activity
*/
public class SittingLLA extends LLA {
    public SittingLLA() {
        super(Type.SITTING);
    }

    public SittingLLA(Person person, AnnotationInfo context) {
        super(person, Type.SITTING, context);
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
