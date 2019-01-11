package org.aimas.consert.tests.casas.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.ContextAssertion;
import org.aimas.consert.model.content.UnaryContextAssertion;
import org.aimas.consert.tests.casas.entities.StringLiteral;

public class PersonLocation extends UnaryContextAssertion {
	private String location;
	
	public PersonLocation() {
	}

	@Override
	public ContextAssertion cloneContent() {
		return new PersonLocation(location, null);
	}

	public PersonLocation(String location, AnnotationData annotations) {
		super(new StringLiteral(location), AcquisitionType.DERIVED, annotations);
		
		this.location = location;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
		setInvolvedEntity(new StringLiteral(location));
	}
}
