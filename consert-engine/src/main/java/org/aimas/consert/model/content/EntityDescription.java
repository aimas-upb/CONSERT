package org.aimas.consert.model.content;

import org.aimas.consert.model.Constants;
import org.cyberborean.rdfbeans.annotations.RDF;
import org.cyberborean.rdfbeans.annotations.RDFBean;
import org.cyberborean.rdfbeans.annotations.RDFNamespaces;

@RDFNamespaces({
	"core = " + Constants.CORE_NS,
	"rdfbeans = " + Constants.RDFBEANS_URI
})
@RDFBean("core:EntityDescription")
public abstract class EntityDescription {
	
	protected ContextEntity subject;
	protected ContextEntity object;
	
	public EntityDescription() {
	}
	
	public EntityDescription(ContextEntity subject, ContextEntity object) {
		this.subject = subject;
		this.object = object;
	}
	
	@RDF("core:entityDescriptionSubject")
	public ContextEntity getSubject() {
		return subject;
	}

	public void setSubject(ContextEntity subject) {
		this.subject = subject;
	}
	
	@RDF("core:entityDescriptionObject")
	public ContextEntity getObject() {
		return object;
	}

	public void setObject(ContextEntity object) {
		this.object = object;
	}

	@Override
    public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((object == null || object.getValue() == null) ? 0 : object.getValue().hashCode());
	    result = prime * result + ((subject == null || subject.getValue() == null) ? 0 : subject.getValue().hashCode());
	    return result;
    }

	@Override
    public boolean equals(Object obj) {
	    if (this == obj) {
		    return true;
	    }
	    if (obj == null) {
		    return false;
	    }
	    if (!(obj instanceof EntityDescription)) {
		    return false;
	    }
	    EntityDescription other = (EntityDescription) obj;
	    if (object == null) {
		    if (other.object != null) {
			    return false;
		    }
	    }
	    else if (object.getValue() == null) {
	    	if (other.object.getValue() != null) {
	    		return false;
	    	}
	    }
	    else if (!object.getValue().equals(other.object.getValue())) {
		    return false;
	    }
	    
	    if (subject == null) {
		    if (other.subject != null) {
			    return false;
		    }
	    }
	    else if (subject.getValue() == null) {
		    if (other.subject.getValue() != null) {
			    return false;
		    }
	    }
	    else if (!subject.getValue().equals(other.subject.getValue())) {
		    return false;
	    }
	    
	    return true;
    }
	
	
}
