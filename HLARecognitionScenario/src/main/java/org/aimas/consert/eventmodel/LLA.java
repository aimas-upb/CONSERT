package org.aimas.consert.eventmodel;
/*
 * Abstract class for modeling a low level activity.
 */
public abstract class LLA
{
    Person person; /* the person which does the LLA */
    ContextInfo context; /* context info about the LLA */
    TYPE type; /* LLA type */
    
    public enum TYPE
    {
           SITTING, STANDING, WALKING;
    }

    TYPE getType()
    {
        return type;
    }
    void SetType(TYPE type)
    {
        this.type = type;
    }
}

