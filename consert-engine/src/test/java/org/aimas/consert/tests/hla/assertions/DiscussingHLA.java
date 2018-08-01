package org.aimas.consert.tests.hla.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.tests.hla.entities.HLAType;
import org.aimas.consert.tests.hla.entities.Person;

/*
 * Class for modeling a discussing  high level activity 
*/
public class DiscussingHLA extends HLA {
    public DiscussingHLA() {
        super(new HLAType("DISCUSSING"));
    }

    @Override
    public ContextAssertion cloneContent() {
        return new DiscussingHLA(person, null);
    }


    public DiscussingHLA(Person person, AnnotationData annotationData) {
        super(person, new HLAType("DISCUSSING"), annotationData);
    }
}
