package org.aimas.consert.tests.hla.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.BinaryContextAssertion;
import org.aimas.consert.tests.hla.entities.LLAType;
import org.aimas.consert.tests.hla.entities.Person;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

/*
 * Abstract class for modeling a low level activity.
 */
@RDFNamespaces("hlatest = http://example.org/hlatest/")
@RDFBean("hlatest:LLA")
public abstract class LLA extends BinaryContextAssertion {
	

    Person person;                  /* the person which does the LLA */
    LLAType type;                   /* LLA type */

    protected LLA(LLAType type) {
    	this.type = type;
    }

    protected LLA(Person person, LLAType type, AnnotationData annotations) {
    	super(person, type, AcquisitionType.SENSED, annotations);
        this.person = person;
        this.type = type;
    }
    
    @RDF("hlatest:hasLLATypeRole")
    public LLAType getType() {
        return type;
    }

    public void setType(LLAType type) {
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
        return "LLA [" + "person=" + person + ", type=" + type + ", annotations=" + annotationData + "]";
    }
       
    
    @Override
	public String getAtomicStreamName() {
		return LLA.class.getSimpleName() + "Stream";
	}
	
	public String getExtendedStreamName() {
		return "Extended" + getAtomicStreamName();
	}
}

