package com.dpdocter.tests;

import java.io.IOException;
import java.util.Arrays;
import com.dpdocter.request.DoctorMultipleDataAddEditRequest;

public class GeneralTests {

    public static void main(String[] args) throws IOException {
	DoctorMultipleDataAddEditRequest request = new DoctorMultipleDataAddEditRequest();

	request.setDoctorId("55eabcd0e4b00c1a44ac6feb");
	request.setFirstName("Gulshan Saluja");
	request.setSpeciality(Arrays.asList("Urologist", "Orthopaedic Surgeon"));

	String requestJSON = Converter.ObjectToJSON(request);

	System.out.println(requestJSON);

    }

}
