package org.aimas.consert.eventmodel;
/*
 *Class for modeling a positioning event
*/
public class Position
{
    public enum Type
    {
        WORK_AREA,
        DINING_AREA,
        SITTING_AREA,
        CONFERENCE_AREA,
        ENTERTAINMENT_AREA,
        SNACK_AREA,
        HYGENE_AREA;
    }

    Person person;                  /* The person which is implied in the positioning event */
    AnnotationInfo annotations;     /* Meta properties (annotations) for the positioning event */
    Type type;                      /* Positioning type*/

    public Position() {

    }

    public Position(Person person, Type type, AnnotationInfo annotations) {
        this.person = person;
        this.annotations = annotations;
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

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Position [" + "person=" + person + ", type=" + type + ", annotations=" + annotations + "]\n";
    }
}
