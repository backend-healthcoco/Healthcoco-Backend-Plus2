package com.dpdocter.tests;

import java.util.Arrays;

import com.dpdocter.beans.ClinicTiming;
import com.dpdocter.beans.Timing;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.beans.WorkingSchedule;
import com.dpdocter.enums.Day;
import com.dpdocter.enums.Period;

public class GeneralTests {
	public static void main(String args[]) {
		ClinicTiming clinicTiming = new ClinicTiming();
		
		clinicTiming.setTwentyFourSevenOpen(false);
	
		WorkingSchedule monday = new WorkingSchedule();
		WorkingSchedule tuesday = new WorkingSchedule();
		WorkingSchedule wednesday = new WorkingSchedule();
		
		WorkingHours workingHours = new WorkingHours();
		
		Timing from = new Timing();
		from.setHour("11");
		from.setPeriod(Period.AM);
		
		Timing to = new Timing();
		to.setHour("01");
		to.setPeriod(Period.PM);
		
		workingHours.setFrom(from);
		workingHours.setTo(to);
		
		monday.setWorkingDay(Day.MONDAY);
		monday.setWorkingHours(Arrays.asList(workingHours));
		
		tuesday.setWorkingDay(Day.TUESDAY);
		tuesday.setWorkingHours(Arrays.asList(workingHours));
		
		wednesday.setWorkingDay(Day.WEDNESDAY);
		wednesday.setWorkingHours(Arrays.asList(workingHours));
		
		clinicTiming.setWorkingSchedules(Arrays.asList(monday, tuesday, wednesday));
		
		System.out.println(Converter.ObjectToJSON(clinicTiming));
	}
}
