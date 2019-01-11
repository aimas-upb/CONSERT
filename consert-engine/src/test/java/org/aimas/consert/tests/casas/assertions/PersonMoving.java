package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.model.content.UnaryContextAssertion;
import org.aimas.consert.tests.casas.entities.StringLiteral;


public class PersonMoving extends UnaryContextAssertion {

    public PersonMoving() {}

    @Override
    public ContextAssertion cloneContent() {
        return new PersonMoving(null);
    }

    public PersonMoving(AnnotationData annotations) {
        super(new StringLiteral("PersonMoving"), AcquisitionType.DERIVED, annotations);

    }
    public PersonMoving(String sensorId, AnnotationData annotations) {
        super(new StringLiteral(sensorId), AcquisitionType.DERIVED, annotations);
    }
}