package org.aimas.consert.eventmodel;
/*
 * Class for modeling a standing  low level activity
*/
public class StandingLLA extends LLA
{
    public StandingLLA()
    {
        super(Type.STANDING);
    }

    public StandingLLA(Person person, AnnotationInfo context)
    {
        super(person, Type.STANDING, context);
    }
}
