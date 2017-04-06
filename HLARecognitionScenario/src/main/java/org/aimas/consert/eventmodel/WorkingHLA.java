package org.aimas.consert.eventmodel;
/*
 * Class for modeling a working  high level activity
*/
public class WorkingHLA extends HLA
{
    public WorkingHLA()
    {

        this.type = Type.WORKING;
    }

    public WorkingHLA(Person person, AnnotationInfo context)
    {
        this.person = person;
        this.annotations = context;
        this.type = Type.WORKING;
    }
}
