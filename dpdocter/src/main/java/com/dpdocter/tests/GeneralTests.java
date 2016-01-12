package com.dpdocter.tests;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GeneralTests {

    public static void main(String[] args) throws ParseException {

//    	interval2(1*60, 5*60, 20);
//    	System.out.println("Events");
//    	interval2((int)Math.round(3*60 + 30), (int)Math.round(5*60 + 30), 20);
    	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date dateObj = new Date();
//    	
    	System.out.println(new SimpleDateFormat("YYYY").format(dateObj));
    	System.out.println(new SimpleDateFormat("dd").format(dateObj));
        Calendar calendar = Calendar.getInstance();
		calendar.setTime(dateObj);
		
		System.out.println(dateObj.getTime());
		System.out.println(calendar.get(Calendar.WEEK_OF_MONTH));
    	System.out.println(calendar.get(Calendar.MONTH));
    	System.out.println(calendar.get(Calendar.DAY_OF_MONTH));
    	System.out.println(calendar.get(Calendar.YEAR));
//    	String.format("%02d:%02d", "10.30");
    	Double d = new Double(10.23);
    	int time = 24*60*60;
    	
    	System.out.println(time);
    	
//    	System.out.println(Float.parseFloat(String.format("%02:%02", 11.0000*100.0 / 100.0, 100 % 60)));
    }
    
    
    public static void interval2(int begin, int end, int interval) {
    	  for (int time = begin; time <= end; time += interval) {
    	    System.out.println(String.format("%02d:%02d", time / 60, time % 60));
    	  }
    	}
}
