package com.dpdocter.tests;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import common.util.web.DPDoctorUtils;



public class GeneralTests {

	private Boolean checkToday(Date date, String timeZone) {
		Boolean status = false;
//		System.out.println(date);
//		DateTime inputDate = new DateTime(date, DateTimeZone.forTimeZone(TimeZone.getTimeZone("UTC")));
//		System.out.println(inputDate);
//		DateTime today = new DateTime(DateTimeZone.forTimeZone(TimeZone.getTimeZone("UTC")));
//		System.out.println(today);
//		if (inputDate.getYear() == today.getYear() && today.getDayOfYear() == inputDate.getDayOfYear()) {
//			status = true;
//		}

	    Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
	    int currentDay = localCalendar.get(Calendar.DAY_OF_YEAR);
	    int currentYear = localCalendar.get(Calendar.YEAR);
	    localCalendar.setTime(date);
	    if(localCalendar.get(Calendar.YEAR) == currentYear && localCalendar.get(Calendar.DAY_OF_YEAR) == currentDay)status = true;
	    
		return status;
	}

	public static void main(String[] args) throws IOException, ParseException{
		System.out.println(DPDoctorUtils.getSHA3SecurePassword("neha1234".toCharArray()));
	}
}  	
