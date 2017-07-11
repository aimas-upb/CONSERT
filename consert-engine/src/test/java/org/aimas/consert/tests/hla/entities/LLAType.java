package org.aimas.consert.tests.hla.entities;

import org.aimas.consert.model.content.ContextEntity;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;
import org.cyberborean.rdfbeans.annotations.RDFSubject;

@RDFNamespaces(
	    "lla = http://example.com/hlatest/" 
	)
@RDFBean("lla:LLAType")
public class LLAType implements ContextEntity {
    String type;
	
    public LLAType() {}
    
    public LLAType(String type) {
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
        return type;
    }

	@Override
	@RDFSubject(prefix = "lla:")
    public String getEntityId() {
		return type;
    }
	
	public void setEntityId(String entityIdentifier) {
		this.type = entityIdentifier;
	}

	@Override
    public int hashCode() {
	    return type.hashCode();
    }

	@Override
    public boolean equals(Object obj) {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    LLAType other = (LLAType) obj;
	    if (type == null) {
		    if (other.type != null)
			    return false;
	    }
	    else if (!type.equals(other.type))
		    return false;
	    return true;
    }

	@Override
    public String toString() {
	    return "LLAType [type=" + type + "]";
    }
}
