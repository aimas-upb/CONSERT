package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.model.content.ContextEntity;
import org.aimas.consert.model.content.StringLiteral;
import org.aimas.consert.model.content.UnaryContextAssertion;

public class WriteBirthdayCard extends UnaryContextAssertion {
	
	public WriteBirthdayCard() {
		setEntity(new StringLiteral("WriteBirthdayCard"));
		setAcquisitionType(AcquisitionType.DERIVED);
	}

	@Override
	public ContextAssertion cloneContent() {
		return new WriteBirthdayCard(null);
	}

	public WriteBirthdayCard(AnnotationData annotations) {
		super(new StringLiteral("WriteBirthdayCard"), AcquisitionType.DERIVED, annotations);
	}
	
	public WriteBirthdayCard(ContextEntity involvedEntity,
	        AcquisitionType generationType, AnnotationData annotations) {
		super(involvedEntity, generationType, annotations);
	}
	
}
