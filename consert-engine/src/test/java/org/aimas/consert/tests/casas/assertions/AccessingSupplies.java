package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.model.content.StringLiteral;
import org.aimas.consert.model.content.UnaryContextAssertion;


public class AccessingSupplies extends UnaryContextAssertion {

    public AccessingSupplies() {}

    public AccessingSupplies(AnnotationData annotations) {
        super(new StringLiteral("AccessingSupplies"), AcquisitionType.DERIVED, annotations);

    }

    public AccessingSupplies(String sensorId, AnnotationData annotations) {
        super(new StringLiteral(sensorId), AcquisitionType.DERIVED, annotations);
    }

    @Override
    public ContextAssertion cloneContent() {
        return new AccessingSupplies("AccessingSupplies", null);
    }
}