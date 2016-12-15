package com.dpdocter.tests;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class GeneralTests {

	private Boolean checkToday(Date date, String timeZone) {
		Boolean status = false;
		// System.out.println(date);
		// DateTime inputDate = new DateTime(date,
		// DateTimeZone.forTimeZone(TimeZone.getTimeZone("UTC")));
		// System.out.println(inputDate);
		// DateTime today = new
		// DateTime(DateTimeZone.forTimeZone(TimeZone.getTimeZone("UTC")));
		// System.out.println(today);
		// if (inputDate.getYear() == today.getYear() && today.getDayOfYear() ==
		// inputDate.getDayOfYear()) {
		// status = true;
		// }

		Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
		int currentDay = localCalendar.get(Calendar.DAY_OF_YEAR);
		int currentYear = localCalendar.get(Calendar.YEAR);
		localCalendar.setTime(date);
		if (localCalendar.get(Calendar.YEAR) == currentYear && localCalendar.get(Calendar.DAY_OF_YEAR) == currentDay)
			status = true;

		return status;
	}

	public static void main(String[] args) {

		CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();
		Double count;
		int div;
		String massage = " ";
		boolean status = asciiEncoder.canEncode(massage);
		double length = (double) massage.length();

		if (status)
			div = 160;
		else
			div = 70;
		count = (length / 160) - Math.floor(length / 160);
		if (count > 0) {
			count = Math.floor(length / div) + 1;
		} else {
			count = Math.floor(length / div);

		}

		System.out.println(count.intValue());
	}

}
