package com.dpdocter.tests;

<<<<<<< HEAD
import org.springframework.security.crypto.bcrypt.BCrypt;

public class GeneralTests {

	public static void main(String args[]) {
//		Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
//		localCalendar.setTime(new Date());
=======
import java.io.IOException;

//		Scanner scanner = new Scanner(new File("/Users/nehakariya/Healthcoco Projects/PractoExport-Bhutada/Patients.csv"));
>>>>>>> f99235a90... HAPPY-4577 Rugwani - Discharge summary & Admit Card new fields(Nayan
//		
//		int currentDay = localCalendar.get(Calendar.DATE);
//		int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
//		int currentYear = localCalendar.get(Calendar.YEAR);
//		DateTime fromDateTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
//				DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
//		
//		DateTime toDateTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
//				DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
//		
//		
//		int currentHour = localCalendar.get(Calendar.HOUR_OF_DAY)-1;
//		System.out.println(currentHour);
//		if(currentHour == -1) {
//			fromDateTime = fromDateTime.minusDays(1);
//			toDateTime = toDateTime.minusDays(1);
//		}
<<<<<<< HEAD
		System.out.println(BCrypt.gensalt(12));
//		System.out.println(toDateTime);
=======
		
//		System.out.println(Pattern.compile(slugUrl).matcher(slugUrl).replaceAll("-*-"));
		
		List<String> ne = new ArrayList<String>();
		ne.add("a");
		ne.add("b");
		ne.add("c");
		ne.add("d");
//		ne.add("e");
//		ne.add("f");
//		ne.add("g");
		
		if(ne.size()>3) {
			System.out.println("yes");
			List<String> e = ne.subList(0, 3);
			System.out.println(e);
		}
=======
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.parsers.ParserConfigurationException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
//import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import com.lowagie.text.DocumentException;

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
