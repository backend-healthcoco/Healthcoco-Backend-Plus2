package com.dpdocter.test;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import common.util.web.DPDoctorUtils;

public class DrugTypeTests {

	public static void main(String[] args) throws IOException, ParseException {Calendar cal = Calendar.getInstance();
	// String shortUrl = DPDoctorUtils.urlShortner("https://d1ayyk8caoqrgh.cloudfront.net/rtpcr/finalcopy1617795732224.png");
	//System.out.println(shortUrl);
	  String s1="01-JAN-2014";
      SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
				Date d2=sdf.parse(s1);
				System.out.println("date1: "+d2);
	
	}
}
