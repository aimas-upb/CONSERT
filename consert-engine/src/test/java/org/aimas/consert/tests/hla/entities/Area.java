package org.aimas.consert.tests.hla.entities;

import org.aimas.consert.model.content.ContextEntity;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

@RDFNamespaces(
    "area = http://example.org/hlatest/" 
)
@RDFBean("area:AreaType")
public enum Area implements ContextEntity {
    WORK_AREA,
    DINING_AREA,
    SITTING_AREA,
    CONFERENCE_AREA,
    ENTERTAINMENT_AREA,
    SNACK_AREA,
    EXERCISE_AREA,
    HYGENE_AREA;

	@Override
    public boolean isLiteral() {
        return true;
    }

	@Override
    public Object getValue() {
        return this;
    }

	@Override
	@RDFSubject(prefix = "area:AreaType#")
    public String getEntityId() {
	    // TODO Auto-generated method stub
	    return this.name();
    }
}