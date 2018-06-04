package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.ContextEntity;
import org.aimas.consert.model.content.UnaryContextAssertion;
import org.aimas.consert.tests.casas.entities.StringLiteral;

public class FillDispenser extends UnaryContextAssertion {
	
	public FillDispenser() {
		setInvolvedEntity(new StringLiteral("person"));
		setAcquisitionType(AcquisitionType.DERIVED);
	}
	
	public FillDispenser(AnnotationData annotations) {
		super(new StringLiteral("person"), AcquisitionType.DERIVED, annotations);
	}
	
	public FillDispenser(ContextEntity involvedEntity,
	        AcquisitionType generationType, AnnotationData annotations) {
		super(involvedEntity, generationType, annotations);
	}
}
