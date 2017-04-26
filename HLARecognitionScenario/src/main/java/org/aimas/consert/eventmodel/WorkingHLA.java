package org.aimas.consert.eventmodel;
/*
 * Class for modeling a working  high level activity
*/
public class WorkingHLA extends HLA {
    public WorkingHLA() {
        super(Type.WORKING);
    }

    public WorkingHLA(Person person, AnnotationInfo context) {
        super(person, Type.WORKING, context);
    }
    
    @Override
    public double getConfidenceValueThreshold() {
	    return HLA.CONFIDENCE_VALUE_THRESHOLD;
    }


	@Override
    public double getConfidenceDiffThreshold() {
	    return HLA.CONFIDENCE_DIFF_THRESHOLD;
    }


	@Override
    public long getTimestampThreshold() {
	    return HLA.TIMESTAMP_DIFF_THRESHOLD;
    }
}
