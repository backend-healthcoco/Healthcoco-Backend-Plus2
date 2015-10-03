package com.dpdocter.tests;

import java.util.Arrays;

import com.dpdocter.beans.MailData;
import com.dpdocter.beans.MedicalData;
import com.dpdocter.enums.MailType;

public class GeneralTests {

    public static void main(String args[]) {
	MedicalData medicalData = new MedicalData();

	medicalData.setDoctorId("55edf013426eb4845f2e0f9b");
	medicalData.setEmailAddress("isankagarwal@gmail.com");
	medicalData.setHospitalId("55edf013426eb4845f2e0fa0");
	medicalData.setLocationId("55edf013426eb4845f2e0fa1");

	MailData mailData = new MailData();
	mailData.setId("55ef38cc426e994a8590fce8");
	mailData.setMailType(MailType.PRESCRIPTION);
	medicalData.setMailDataList(Arrays.asList(mailData));

	System.out.println(Converter.ObjectToJSON(medicalData));
    }
}