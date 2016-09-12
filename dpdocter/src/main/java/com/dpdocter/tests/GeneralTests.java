package com.dpdocter.tests;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.dpdocter.beans.DOB;
import com.dpdocter.enums.RecordsState;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import common.util.web.DPDoctorUtils;



public class GeneralTests {

	public static void main(String[] args) throws NoSuchAlgorithmException, JsonGenerationException, JsonMappingException, IOException, ParseException  {
	
		int ageInYears = 25;
		Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
	    int currentDay = localCalendar.get(Calendar.DATE);
	    int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
	    int currentYear = localCalendar.get(Calendar.YEAR);
	    

		System.out.println(RecordsState.APPROVED_BY_DOCTOR.toString());
	   
	}
	    		 
}  	
