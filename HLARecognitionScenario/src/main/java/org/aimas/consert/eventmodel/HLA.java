package org.aimas.consert.eventmodel;
/*
 *  Abstract class for modeling a high level activity.
 */
public abstract class HLA extends BaseEvent {
    
	Person person;                  /* the person which does the HLA */
    Type type;                      /* HLA type */

    public enum Type
    {
        DISCUSSING, EXERCISE, WORKING
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
        return "HLA [" + "person=" + person + ",  type=" + type + ", annotations=" + annotations + "]";
    }
}
