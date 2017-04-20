package org.aimas.consert.eventmodel;
/*
 * Abstract class for modeling a low level activity.
 */
public abstract class LLA extends BaseEvent
{
    public enum Type
    {
        SITTING, STANDING, WALKING
    }

    Person person;                  /* the person which does the LLA */
    Type type;                      /* LLA type */

    protected LLA(Type type) {
    	this.type = type;
    }

    protected LLA(Person person, Type type, AnnotationInfo annotations) {
    	super(annotations);
        this.annotations = annotations;
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
    public String toString() {
        return "LLA [" + "person=" + person + ", type=" + type + ", annotations=" + annotations + "]\n";
    }
}

