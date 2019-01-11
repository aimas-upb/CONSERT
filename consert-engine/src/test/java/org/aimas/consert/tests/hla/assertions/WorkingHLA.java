package org.aimas.consert.tests.hla.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.tests.hla.entities.HLAType;
import org.aimas.consert.tests.hla.entities.LLAType;
import org.aimas.consert.tests.hla.entities.Person;

/*
 * ReplaceAssertionOperation for modeling a working  high level activity
*/
public class WorkingHLA extends HLA {
    public WorkingHLA() {
        super(new HLAType("WORKING"));
    }

    @Override
    public ContextAssertion cloneContent() {
        return new WorkingHLA(person, null);
    }

    public WorkingHLA(Person person, AnnotationData annotationData) {
        super(person, new HLAType("WORKING"), annotationData);
    }

}
