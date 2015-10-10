package com.dpdocter.tests;

import java.util.Arrays;
import java.util.List;

class Test {
    private String firstName;

    private String lastName;

    public Test(String firstName, String lastName) {
	super();
	this.firstName = firstName;
	this.lastName = lastName;
    }

    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public String getLastName() {
	return lastName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    @Override
    public String toString() {
	return "Test [firstName=" + firstName + ", lastName=" + lastName + "]";
    }

}

public class GeneralTests {

    public static void main(String[] args) {
	List<Test> tests = Arrays.asList(new Test("Isank", "Agarwal"), new Test("Juby", "Agarwal"), new Test("Binny", "Agarwal"));
	System.out.println(tests);
	for (Test test : tests) {
	    test.setFirstName("Isanka");
	    test.setLastName("Agarwala");
	}
	System.out.println(tests);
    }
}