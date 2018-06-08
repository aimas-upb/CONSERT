package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.ContextEntity;
import org.aimas.consert.model.content.UnaryContextAssertion;
import org.aimas.consert.tests.casas.entities.StringLiteral;

public class WriteBirthdayCard extends UnaryContextAssertion {
	
	public WriteBirthdayCard() {
		setInvolvedEntity(new StringLiteral("birthday_card"));
		setAcquisitionType(AcquisitionType.DERIVED);
	}
	
	public WriteBirthdayCard(AnnotationData annotations) {
		super(new StringLiteral("birthday_card"), AcquisitionType.DERIVED, annotations);
	}
	
	public WriteBirthdayCard(ContextEntity involvedEntity,
	        AcquisitionType generationType, AnnotationData annotations) {
		super(involvedEntity, generationType, annotations);
	}
	
}
