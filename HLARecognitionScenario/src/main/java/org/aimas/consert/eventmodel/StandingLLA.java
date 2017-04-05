package org.aimas.consert.eventmodel;
/*
 * Class for modeling a standing  low level activity
*/
public class StandingLLA extends LLA
{
    public StandingLLA()
    {
        this.type = TYPE.STANDING;
    }
    public StandingLLA(Person person, ContextInfo context)
    {
        this.person = person;
        this.context = context;
        this.type = TYPE.STANDING;
    }
}
