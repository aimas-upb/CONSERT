package org.aimas.consert.eventmodel;
/*
 * Class for modeling a discussing  high level activity 
*/
public class DiscussingHLA extends HLA
{
    public DiscussingHLA()
    {
        this.type = TYPE.DISCUSSING;
    }
    public DiscussingHLA(Person person, ContextInfo context)
    {
        this.person = person;
        this.context = context;
        this.type = TYPE.DISCUSSING;
    }
}
