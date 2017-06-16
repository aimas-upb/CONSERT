package org.aimas.consert.tests.hla;

import org.aimas.consert.model.AnnotationData;

/*
 * Class for modeling a working  high level activity
*/
public class WorkingHLA extends HLA {
    public WorkingHLA() {
        super(Type.WORKING);
    }

    public WorkingHLA(Person person, AnnotationData annotationData) {
        super(person, Type.WORKING, annotationData);
    }

}
