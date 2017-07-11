package org.aimas.consert.tests.hla.entities;

import org.aimas.consert.model.content.ContextEntity;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

@RDFNamespaces({
    "area = http://example.org/hlatest/",
    "rdfs = http://www.w3.org/2000/01/rdf-schema#"
})
@RDFBean("area:AreaType")
public class Area implements ContextEntity {
    /*
	WORK_AREA,
    DINING_AREA,
    SITTING_AREA,
    CONFERENCE_AREA,
    ENTERTAINMENT_AREA,
    SNACK_AREA,
    EXERCISE_AREA,
    HYGENE_AREA;
     */
	
	String type;
	
	public Area() {}
	
	public Area(String type) {
		this.type = type;
	}

	@RDF("rdfs:label")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
    public boolean isLiteral() {
        return true;
    }

	@Override
    public Object getValue() {
        return this;
    }

	@Override
	@RDFSubject(prefix = "area:")
    public String getEntityId() {
		return type;
    }
	
	public void setEntityId(String entityIdentifier) {
		this.type = entityIdentifier;
	}
}