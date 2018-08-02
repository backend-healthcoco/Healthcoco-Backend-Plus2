package com.dpdocter.test;


import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class DrugTypeTests {

	public static void main(String[] args) throws IOException {Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		cal.add(Calendar.YEAR, 1); // to get previous year add 1
		Date expiryDate = cal.getTime();

		System.out.println("today is --> " + today);
		System.out.println("expiryDate is --> " + expiryDate);
	}
}
