package org.aimas.consert.eventmodel;
/*
 * Class for modeling a discussing  high level activity 
*/
public class DiscussingHLA extends HLA
{
    public DiscussingHLA()
    {
        this.type = Type.DISCUSSING;
    }
    public DiscussingHLA(Person person, AnnotationInfo context)
    {
        this.person = person;
        this.annotations = context;
        this.type = Type.DISCUSSING;
    }
}
