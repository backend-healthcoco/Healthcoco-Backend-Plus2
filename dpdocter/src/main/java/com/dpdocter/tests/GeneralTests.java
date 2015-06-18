package com.dpdocter.tests;

import java.util.Arrays;

import com.dpdocter.beans.Count;
import com.dpdocter.beans.FlexibleCounts;
import com.dpdocter.enums.CountFor;

public class GeneralTests {
	public static void main(String args[]) {
		FlexibleCounts flexibleCounts = new FlexibleCounts();
		flexibleCounts.setDoctorId("5525ef96e4b077dfc168369b");
		flexibleCounts.setHospitalId("5525ef96e4b077dfc16836a0");
		flexibleCounts.setLocationId("5525ef96e4b077dfc16836a1");
		Count count1 = new Count();
		count1.setCountFor(CountFor.PRESCRIPTIONS);
		Count count2 = new Count();
		count2.setCountFor(CountFor.NOTES);
		flexibleCounts.setCounts(Arrays.asList(count1, count2));

		System.out.println(Converter.ObjectToJSON(flexibleCounts));
	}
}
