package com.dpdocter.tests;

import com.dpdocter.collections.DoctorCollection;

public class GeneralTests {
	public static void main(String args[]) {
		DoctorCollection doctor = new DoctorCollection();
		
		doctor.setSecMobile("9021703700");
		doctor.setSpecialization("Neurologist");
		doctor.setUserId("D12345");
		
		System.out.println(Converter.ObjectToJSON(doctor));
	}
}
