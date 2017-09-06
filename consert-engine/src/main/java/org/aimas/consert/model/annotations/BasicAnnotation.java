package org.aimas.consert.model.annotations;

import org.cyberborean.rdfbeans.annotations.RDFBean;

@RDFBean("annotation:BasicAnnotation")
public abstract class BasicAnnotation extends ContextAnnotation {
	
	@Override
	public boolean isStructured() {
		return false;
	}
}
