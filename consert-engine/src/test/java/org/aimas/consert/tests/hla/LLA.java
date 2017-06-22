package org.aimas.consert.tests.hla;

import org.aimas.consert.model.AnnotationData;
import org.aimas.consert.model.BinaryContextAssertion;
import org.aimas.consert.model.ContextAssertion;
import org.aimas.consert.model.ContextEntity;

/*
 * Abstract class for modeling a low level activity.
 */
public abstract class LLA extends BinaryContextAssertion {
	public enum Type implements ContextEntity {
        SITTING {
            @Override
            public Object getValue() {
                return SITTING;
            }
        }, STANDING {
            @Override
            public Object getValue() {
                return STANDING;
            }
        }, WALKING {
            @Override
            public Object getValue() {
                return WALKING;
            }
        };

		@Override
        public boolean isLiteral() {
	        return true;
        }
    }

    Person person;                  /* the person which does the LLA */
    Type type;                      /* LLA type */

    protected LLA(Type type) {
    	this.type = type;
    }

    protected LLA(Person person, Type type, AnnotationData annotations) {
    	super(person, type, AcquisitionType.SENSED, annotations);
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
        return "LLA [" + "person=" + person + ", type=" + type + ", annotations=" + annotationData + "]";
    }
    
    @Override
    public boolean allowsContentContinuity(ContextAssertion event) {
    	LLA otherEvent = (LLA)event;
    	
    	if (type == otherEvent.getType() && person.equals(otherEvent.getPerson())) {
    		return true;
    	}
    	
    	return false;
    }
    
    
    @Override
	public String getStreamName() {
		return LLA.class.getSimpleName() + "Stream";
	}
	
	public String getExtendedStreamName() {
		return "Extended" + getStreamName();
	}
}

