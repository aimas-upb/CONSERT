package org.aimas.consert.tests.hla.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.tests.hla.entities.HLAType;
import org.aimas.consert.tests.hla.entities.Person;


/*
 *   ReplaceAssertionOperation for modeling a dining  high level activity
*/
public class DiningHLA extends HLA {

    public DiningHLA() {
        super(new HLAType("DINING"));
    }

    @Override
    public ContextAssertion cloneContent() {
        return new DiningHLA(person, null);
    }

    public DiningHLA(Person person, AnnotationData annotationData) {
        super(person, new HLAType("DINING"), annotationData);
    }
}
