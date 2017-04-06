package org.aimas.consert.eventmodel;
/*
 * Class for modeling a sitting  low level activity
*/
public class SittingLLA extends LLA
{
    public SittingLLA()
    {
        super(Type.SITTING);
    }

    public SittingLLA(Person person, AnnotationInfo context)
    {
        super(person, Type.SITTING, context);
    }
}
