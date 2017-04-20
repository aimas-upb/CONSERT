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
    public int hashCode() {
	    return name.hashCode();
    }

	@Override
    public boolean equals(Object obj) {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    Person other = (Person) obj;
	    if (name == null) {
		    if (other.name != null)
			    return false;
	    }
	    else if (!name.equals(other.name))
		    return false;
	    return true;
    }

	@Override
    public String toString() {
        return "Person [name=" + name + "]";
    }
}
