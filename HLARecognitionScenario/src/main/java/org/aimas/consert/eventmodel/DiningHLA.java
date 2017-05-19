package org.aimas.consert.eventmodel;


/*
 *   Class for modeling a dining  high level activity
*/
public class DiningHLA extends HLA {

    public DiningHLA() {
        super(Type.DINING);
    }

    public DiningHLA(Person person, AnnotationInfo context) {
        super(person, Type.DINING, context);
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
