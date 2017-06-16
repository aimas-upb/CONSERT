package org.aimas.consert.tests.hla;

import org.aimas.consert.model.AnnotationData;
import org.aimas.consert.model.BinaryContextAssertion;
import org.aimas.consert.model.ContextAssertion;
import org.aimas.consert.model.ContextEntity;


/*
 *  Abstract class for modeling a high level activity.
 */
public abstract class HLA extends BinaryContextAssertion {

	public enum Type implements ContextEntity {
        DISCUSSING, EXERCISE, WORKING, DINING;

		@Override
        public boolean isLiteral() {
	        return true;
        }
    }
	
	
	Person person;                  /* the person which does the HLA */
    Type type;                      /* HLA type */
    
    protected HLA(Type type) {
    	this.type = type;
    }

    protected HLA(Person person, Type type, AnnotationData annotations) {
    	super(person, type, AcquisitionType.DERIVED, annotations);
        this.person = person;
        this.type = type;
    }
    
    
    public Type getType()
    {
        return type;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    
    @Override
    public int getContentHash() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((person == null) ? 0 : person.hashCode());
	    result = prime * result + ((type == null) ? 0 : type.hashCode());
	    return result;
    }
    
    
    @Override
    public String toString() {
        return "HLA [" + "person=" + person + ",  type=" + type + ", annotations=" + annotationData + "]";
    }
    
    
    @Override
    public boolean allowsContentContinuity(ContextAssertion event) {
    	HLA otherEvent = (HLA)event;
    	
    	if (type == otherEvent.getType() && person.equals(otherEvent.getPerson())) {
    		return true;
    	}
    	
    	return false;
    }
    
    
    
    @Override
	public String getStreamName() {
		return getClass().getSimpleName() + "Stream";
	}
	
	public String getExtendedStreamName() {
		return "Extended" + getStreamName();
	}
}
