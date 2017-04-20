package org.aimas.consert.eventmodel;
/*
 *Class for modeling a positioning event
*/
public class Position extends BaseEvent
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
    Type type;                      /* Positioning type*/

    public Position() {
    }

    public Position(Person person, Type type, AnnotationInfo annotations) {
        super(annotations);
    	this.person = person;
        this.type = type;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
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
