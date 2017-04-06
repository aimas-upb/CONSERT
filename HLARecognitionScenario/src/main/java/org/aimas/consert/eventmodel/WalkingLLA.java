package org.aimas.consert.eventmodel;
/*
 * Class for modeling a walking  low level activity
*/
public class WalkingLLA extends LLA
{
    public WalkingLLA()
    {
        super(Type.WALKING);
    }

    public WalkingLLA(Person person, AnnotationInfo context)
    {
        super(person, Type.WALKING, context);
    }
}
