package com.dpdocter.tests;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneralTests {

	public static void main(String args[]) throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("mm");

		Date dt = sdf.parse("100");
		sdf = new SimpleDateFormat("hh:mm a");

	}
}
