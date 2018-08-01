package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.model.content.ContextEntity;
import org.aimas.consert.model.content.UnaryContextAssertion;
import org.aimas.consert.tests.casas.entities.StringLiteral;

public class FillDispenser extends UnaryContextAssertion {
	
	public FillDispenser() {
		setInvolvedEntity(new StringLiteral("FillDispenser"));
		setAcquisitionType(AcquisitionType.DERIVED);
	}

	@Override
	public ContextAssertion cloneContent() {
		return new FillDispenser(null);
	}

	public FillDispenser(AnnotationData annotations) {
		super(new StringLiteral("FillDispenser"), AcquisitionType.DERIVED, annotations);
	}
	
	public FillDispenser(ContextEntity involvedEntity,
	        AcquisitionType generationType, AnnotationData annotations) {
		super(involvedEntity, generationType, annotations);
	}
}
