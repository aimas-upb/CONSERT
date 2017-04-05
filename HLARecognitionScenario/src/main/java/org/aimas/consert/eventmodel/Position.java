package org.aimas.consert.eventmodel;
/*
 *Class for modeling a positioning event
*/
public class Position
{
    Person person; /* The person which is implied in the positioning event */
    ContextInfo context; /* Context for the positioning event */
    TYPE type; /* Positioning type*/
    public enum TYPE
    {
        WORK_AREA,
        DINING_AREA,
        SITTING_AREA,
        CONFERENCE_AREA,
        ENTERTAINMENT_AREA,
        SNACK_AREA,
        HYGENE_AREA;
    }

}
