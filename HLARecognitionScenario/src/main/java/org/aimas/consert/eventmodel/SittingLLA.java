package org.aimas.consert.eventmodel;
/*
 * Class for modeling a sitting  low level activity
*/
public class SittingLLA extends LLA
{
    public SittingLLA()
    {
        this.type = TYPE.SITTING;
    }
    public SittingLLA(Person person, ContextInfo context)
    {
        this.person = person;
        this.context = context;
        this.type = TYPE.SITTING;
    }
}
