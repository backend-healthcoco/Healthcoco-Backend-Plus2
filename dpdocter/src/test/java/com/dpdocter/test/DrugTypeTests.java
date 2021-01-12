package com.dpdocter.test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalUnit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DrugTypeTests {

	public static void main(String[] args) throws IOException {
//		Calendar cal = Calendar.getInstance();
//		Date today = cal.getTime();
//		cal.add(Calendar.YEAR, 1); // to get previous year add 1
//		Date expiryDate = cal.getTime();
//
//		System.out.println("today is --> " + today);
//		System.out.println("expiryDate is --> " + expiryDate);
		org.joda.time.LocalDateTime time= org.joda.time.LocalDateTime.now(DateTimeZone.UTC);
		
		
		String dateString = "2010-03-01T00:00:00+05:30";
		String pattern = "yyyy-MM-dd'T'H:mm:ssz";
		
		DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
	//	DateTime dateTime = dtf.parseDateTime(time.);
		System.out.println(time.toString(pattern));
	}
}
