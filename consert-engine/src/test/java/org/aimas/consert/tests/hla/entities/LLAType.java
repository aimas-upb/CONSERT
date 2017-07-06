package org.aimas.consert.tests.hla.entities;

import org.aimas.consert.model.content.ContextEntity;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

@RDFNamespaces(
	    "lla = http://example.com/hlatest/" 
	)
@RDFBean("lla:LLAType")
public enum LLAType implements ContextEntity {
    SITTING, STANDING, WALKING;

	@Override
    public boolean isLiteral() {
        return true;
    }

	@Override
    public Object getValue() {
        return this;
    }

	@Override
	@RDFSubject(prefix = "lla:")
    public String getEntityId() {
	    return this.name();
    }
}
