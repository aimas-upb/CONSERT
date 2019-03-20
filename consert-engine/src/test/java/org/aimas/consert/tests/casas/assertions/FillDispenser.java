package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.model.content.ContextEntity;
import org.aimas.consert.model.content.StringLiteral;
import org.aimas.consert.model.content.UnaryContextAssertion;

public class FillDispenser extends UnaryContextAssertion {
	
	public FillDispenser() {
		setEntity(new StringLiteral("FillDispenser"));
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
