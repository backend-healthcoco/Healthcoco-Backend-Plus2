package com.dpdocter.tests;

import java.util.Arrays;

import com.dpdocter.enums.ExportRequestData;
import com.dpdocter.request.ExportContactsRequest;

public class GeneralTests {
	public static void main(String args[]) {
		ExportContactsRequest request = new ExportContactsRequest();
		request.setDataType(Arrays.asList(ExportRequestData.CONTACTS, ExportRequestData.CLINICAL_NOTES));
		request.setDoctorId("D12345");
		request.setEmailAddress("abc@example.com");
		request.setSpecialComments("Test Special Comments");
		
		System.out.println(Converter.ObjectToJSON(request));
	}
}
