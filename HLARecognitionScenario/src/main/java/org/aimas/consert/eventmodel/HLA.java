package org.aimas.consert.eventmodel;
/*
 *  Abstract class for modeling a high level activity.
 */
public abstract class HLA
{
    Person person;                  /* the person which does the HLA */
    AnnotationInfo annotations;     /* annotations info about the HLA */
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

    public AnnotationInfo getAnnotations() {
        return annotations;
    }

    public void setAnnotations(AnnotationInfo annotations) {
        this.annotations = annotations;
    }

    @Override
    public String toString() {
        return "HLA [" + "person=" + person + ",  type=" + type + ", annotations=" + annotations + "]";
    }
}
