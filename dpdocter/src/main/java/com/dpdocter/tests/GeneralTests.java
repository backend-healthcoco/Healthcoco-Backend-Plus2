package com.dpdocter.tests;

import java.util.ArrayList;
import java.util.Arrays;

import com.dpdocter.request.DiseaseAddEditRequest;
import com.dpdocter.request.SpecialNotesAddRequest;

public class GeneralTests {
	public static void main(String args[]) {
		SpecialNotesAddRequest request = new SpecialNotesAddRequest();
		request.setDoctorId("D12345");
		request.setHospitalId("H12345");
		request.setLocationId("L12345");
		request.setPatientId("P12345");
		request.setSpecialNotes(Arrays.asList("Take rest", "Test Notes"));

		System.out.println(Converter.ObjectToJSON(request));
	}
}
