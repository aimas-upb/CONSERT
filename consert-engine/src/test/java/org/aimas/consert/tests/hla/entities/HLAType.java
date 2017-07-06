package org.aimas.consert.tests.hla.entities;

import org.aimas.consert.model.content.ContextEntity;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;


@RDFNamespaces(
	    "hla = http://example.org/hlatest/" 
)
@RDFBean("hla:HLAType")
public enum HLAType implements ContextEntity {
    DISCUSSING, EXERCISE, WORKING, DINING;

	@Override
    public boolean isLiteral() {
        return true;
    }

	@Override
    public Object getValue() {
        return this;
    }

	@Override
	@RDFSubject(prefix = "hla:")
    public String getEntityId() {
	    return this.name();
    }
}