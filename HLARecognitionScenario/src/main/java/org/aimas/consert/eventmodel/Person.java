package org.aimas.consert.eventmodel;
/*
 * Class for modeling a Person.
 */
public class Person
{
    String name; /* name of the person*/

    public Person() {}

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person [name=" + name + "]";
    }
}
