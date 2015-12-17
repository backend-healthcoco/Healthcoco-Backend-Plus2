package com.dpdocter.tests;

import java.io.IOException;
import java.util.Arrays;

import com.dpdocter.beans.DoctorRegistrationDetail;
import com.dpdocter.request.DoctorRegistrationAddEditRequest;

public class GeneralTests {

    public static void main(String[] args) throws IOException {
	DoctorRegistrationAddEditRequest request = new DoctorRegistrationAddEditRequest();

	request.setDoctorId("");

	DoctorRegistrationDetail registrationDetail = new DoctorRegistrationDetail();
	registrationDetail.setMedicalCouncil("Munna Bhai Medical Council");
	registrationDetail.setRegistrationId("MBBSNO1");
	registrationDetail.setYearOfPassing(2015);

	request.setRegistrationDetails(Arrays.asList(registrationDetail));

	System.out.println(Converter.ObjectToJSON(request));
    }

}
