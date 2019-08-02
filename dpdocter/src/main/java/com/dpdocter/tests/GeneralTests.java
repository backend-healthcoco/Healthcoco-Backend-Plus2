package com.dpdocter.tests;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class GeneralTests {

	public static void main(String args[]) {
		Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
		localCalendar.setTime(new Date());
		
		int currentDay = localCalendar.get(Calendar.DATE);
		int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
		int currentYear = localCalendar.get(Calendar.YEAR);
		DateTime fromDateTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
				DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
		
		DateTime toDateTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
				DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
		
		
		int currentHour = localCalendar.get(Calendar.HOUR_OF_DAY)-1;
		System.out.println(currentHour);
		if(currentHour == -1) {
			fromDateTime = fromDateTime.minusDays(1);
			toDateTime = toDateTime.minusDays(1);
		}
		System.out.println(fromDateTime);
		System.out.println(toDateTime);
		
	}
}
