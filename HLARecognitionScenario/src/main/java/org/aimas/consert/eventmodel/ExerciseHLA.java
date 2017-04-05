package org.aimas.consert.eventmodel;
/*
 *   Class for modeling an exercise  high level activity
*/
public class ExerciseHLA extends HLA
{
    public ExerciseHLA()
    {
        this.type = TYPE.EXERCISE;
    }
    public ExerciseHLA(Person person, ContextInfo context)
    {
        this.person = person;
        this.context = context;
        this.type = TYPE.EXERCISE;
    }
}
