package org.aimas.consert.eventmodel;
/*
 * Class for modeling a discussing  high level activity 
*/
public class DiscussingHLA extends HLA {
    public DiscussingHLA() {
        super(Type.DISCUSSING);
    }
    
    
    public DiscussingHLA(Person person, AnnotationInfo context) {
        super(person, Type.DISCUSSING, context);
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
