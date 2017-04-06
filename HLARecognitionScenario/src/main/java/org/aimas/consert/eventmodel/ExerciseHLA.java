package org.aimas.consert.eventmodel;
/*
 *   Class for modeling an exercise  high level activity
*/
public class ExerciseHLA extends HLA
{
    public ExerciseHLA()
    {
        this.type = Type.EXERCISE;
    }
    public ExerciseHLA(Person person, AnnotationInfo context)
    {
        this.person = person;
        this.annotations = context;
        this.type = Type.EXERCISE;
    }
}
