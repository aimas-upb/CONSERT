package org.aimas.consert.tests.hla.entities;

import org.aimas.consert.model.content.ContextEntity;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

import com.fasterxml.jackson.annotation.JsonIgnore;

/*
 * Class for modeling a Person.
 */

@RDFNamespaces({
    "person = http://example.com/hlatest/" 
})
@RDFBean("person:Person")
public class Person implements ContextEntity {
    
	String name; /* name of the person*/

    public Person() {}

    public Person(String name) {
        this.name = name;
    }
    
    @RDF("person:name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    
    @Override
    public int hashCode() {
	    return name.hashCode();
    }

	@Override
    public boolean equals(Object obj) {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    Person other = (Person) obj;
	    if (name == null) {
		    if (other.name != null)
			    return false;
	    }
	    else if (!name.equals(other.name))
		    return false;
	    return true;
    }

	@Override
    public String toString() {
        return "Person [name=" + name + "]";
    }

	@Override
    public boolean isLiteral() {
	    return false;
    }

	@Override
	@JsonIgnore
    public Object getValue() {
	    return this;
    }

	@Override
	@RDFSubject(prefix = "person:")
    public String getEntityId() {
	    return name;
    }
}
