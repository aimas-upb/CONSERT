package org.aimas.consert.eventmodel;
/*
 * Class for modeling a working  high level activity
*/
public class WorkingHLA extends HLA
{
    public WorkingHLA()
    {
        this.type = TYPE.WORKING;
    }
    public WorkingHLA(Person person, ContextInfo context)
    {
        this.person = person;
        this.context = context;
        this.type = TYPE.WORKING;
    }
}
