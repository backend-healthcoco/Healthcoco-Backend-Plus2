package com.dpdocter.tests;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneralTests {

	public static void main(String args[]) throws ParseException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("mm");

		
		    Date dt = sdf.parse("100");
		    sdf = new SimpleDateFormat("hh:mm a");
		    System.out.println(sdf.format(dt));
		    
//		System.out.println(String.format("%02d:%02d", 1000 / 60, 1000 % 60));
		
	}
}
