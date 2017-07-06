package org.aimas.consert.tests.hla.assertions;

import org.aimas.consert.model.annotations.AnnotationData;
import org.aimas.consert.model.content.BinaryContextAssertion;
import org.aimas.consert.tests.hla.entities.Area;
import org.aimas.consert.tests.hla.entities.Person;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

/*
 *Class for modeling a positioning event
*/
@RDFNamespaces("hlatest = http://example.org/hlatest/")
@RDFBean("hlatest:Position")
public class Position extends BinaryContextAssertion {
    
	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((person == null) ? 0 : person.hashCode());
	    result = prime * result + ((type == null) ? 0 : type.hashCode());
	    return result;
    }
	
    Person person;                  /* The person which is implied in the positioning event */
    Area type;                      /* Positioning type*/

    public Position() {
    }

    public Position(Person person, Area type, AnnotationData annotations) {
        super(person, type, AcquisitionType.SENSED, annotations);
    	this.person = person;
        this.type = type;
    }
    
    @RDF("hlatest:hasPersonRole")
    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
        setSubject(person);
    }
    
    @RDF("hlatest:hasAreaRole")
    public Area getType() {
        return type;
    }

    public void setType(Area type) {
        this.type = type;
        setObject(type);
    }
    
    
    @Override
    public String toString() {
        return "Position [" + "person=" + person + ", type=" + type + ", annotations=" + annotationData + "]";
    }

}
