package org.aimas.consert.eventmodel;
/*
 *  Abstract class for modeling a high level activity.
 */
public abstract class HLA
{
    Person person; /* the person which does the HLA */
    ContextInfo context; /* context info about the HLA */
    TYPE type; /* HLA type */

    public enum TYPE
    {
        DISCUSSING, EXERCISE, WORKING;
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
