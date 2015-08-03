package com.dpdocter.tests;

import com.dpdocter.request.PrescriptionAddEditRequest;

public class GeneralTests {
    public static void main(String args[]) {
	PrescriptionAddEditRequest request = new PrescriptionAddEditRequest();

	request.setCreatedBy("Isank");
	request.setDoctorId("D12345");
	request.setHospitalId("H12345");
	request.setLocationId("L12345");
	request.setName("Test Prescription");
	request.setPatientId("55661e9ab732a94e37e2d0ab");
	request.setPrescriptionCode("PRES12345");

	System.out.println(Converter.ObjectToJSON(request));
    }
}
