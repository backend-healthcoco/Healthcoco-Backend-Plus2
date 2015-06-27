package com.dpdocter.tests;

import com.dpdocter.request.GetDoctorContactsRequest;

public class GeneralTests {
	public static void main(String args[]) {
		GetDoctorContactsRequest request = new GetDoctorContactsRequest();

		request.setBlocked(false);
		request.setDoctorId("5525ef96e4b077dfc168369b");
		request.setLocationId("5525ef96e4b077dfc16836a1");
		request.setHospitalId("5525ef96e4b077dfc16836a0");
		request.setPage(0);
		request.setSize(9);

		System.out.println(Converter.ObjectToJSON(request));
	}
}
