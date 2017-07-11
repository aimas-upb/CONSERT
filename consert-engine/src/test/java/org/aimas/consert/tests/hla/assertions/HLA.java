package org.aimas.consert.tests.hla.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.BinaryContextAssertion;
import org.aimas.consert.tests.hla.entities.HLAType;
import org.aimas.consert.tests.hla.entities.Person;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;


/*
 *  Abstract class for modeling a high level activity.
 */
@RDFNamespaces("hlatest = http://example.org/hlatest/")
@RDFBean("hlatest:HLA")
public abstract class HLA extends BinaryContextAssertion {

	Person person;                  /* the person which does the HLA */
    HLAType type;                      /* HLA type */
    
    protected HLA(HLAType type) {
    	this.type = type;
    }
    
    protected HLA(Person person, HLAType type, AnnotationData annotations) {
    	super(person, type, AcquisitionType.DERIVED, annotations);
        this.person = person;
        this.type = type;
    }
    
    @RDF("hlatest:hasHLATypeRole")
    public HLAType getType()
    {
        return type;
    }
    
    public void setType(HLAType type)
    {
        this.type = type;
        setObject(type);
    }
    
    @RDF("hlatest:hasPersonRole")
    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
        setSubject(person);
    }
    
    @Override
    public String toString() {
        return "HLA [" + "person=" + person + ",  type=" + type + ", annotations=" + annotationData + "]";
    }
    
    @Override
	public String getStreamName() {
		return getClass().getSimpleName() + "Stream";
	}
	
    @Override
	public String getExtendedStreamName() {
		return "Extended" + getStreamName();
	}
}
