package org.aimas.consert.eventmodel;
/*
 * Class for modeling a walking  low level activity
*/
public class WalkingLLA extends LLA
{
    public WalkingLLA()
    {
        this.type = TYPE.WALKING;
    }
    public WalkingLLA(Person person, ContextInfo context)
    {
        this.person = person;
        this.context = context;
        this.type = TYPE.WALKING;
    }
}
