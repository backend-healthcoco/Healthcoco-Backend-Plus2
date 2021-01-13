package com.dpdocter.test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


public class DrugTypeTests {

	public static void main(String[] args) throws IOException {
//		Calendar cal = Calendar.getInstance();
//		Date today = cal.getTime();
//		cal.add(Calendar.YEAR, 1); // to get previous year add 1
//		Date expiryDate = cal.getTime();
//
//		System.out.println("today is --> " + today);
//		System.out.println("expiryDate is --> " + expiryDate);
		String pattern = "yyyy-MM-dd'T'H:mm:ss.SSS";
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
		// Set Timestamp 
		LocalDateTime time= LocalDateTime.now(ZoneOffset.UTC);
		time.format(dtf);
	//	DateTime dateTime = dtf.parseDateTime(time.);
		System.out.println(time.format(dtf));
	}
}
