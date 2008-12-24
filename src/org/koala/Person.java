package org.koala;
/*
 * Created on May 29, 2005
 *
 */

/**
 * @author tom
 *
 */

public class Person {
	private int id; //non-neg if valid
	protected String firstName, lastName;

	public Person() {
	    id = -1;
	    lastName = "";
	    firstName = "";
	}

	public Person(int id, String lastname, String firstname) {
		this.id = id;
		this.lastName = lastname;
		this.firstName = firstname;
	}

	protected void setId(int id) {
	    this.id = id;
	}

	protected void setLastName(String lastName) {
	    this.lastName = lastName;
	}

	protected void setFirstName(String firstName) {
	    this.firstName = firstName;
	}

	public int getId() {
		return id;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String toString() {
	    return new String(getLastName() + ", " + getFirstName());
	}
}
