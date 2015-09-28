package com.dpdocter.tests;

import java.util.Arrays;

import com.dpdocter.beans.AppointmentSlot;
import com.dpdocter.beans.ConsultationFee;
import com.dpdocter.beans.DoctorGeneralInfo;
import com.dpdocter.enums.Currency;
import com.dpdocter.enums.TimeUnit;

public class GeneralTests {

    public static void main(String args[]) {
	DoctorGeneralInfo doctorGeneralInfo = new DoctorGeneralInfo();

	doctorGeneralInfo.setId("55eabdbde4b00c1a44ac7007");
	doctorGeneralInfo.setDoctorId("55eabdbde4b00c1a44ac7006");
	doctorGeneralInfo.setLocationId("55eabdbde4b00c1a44ac700c");

	doctorGeneralInfo.setAppointmentBookingNumber(Arrays.asList("0123456789", "9876543210"));

	AppointmentSlot appointmentSlot = new AppointmentSlot();
	appointmentSlot.setTime(10f);
	appointmentSlot.setTimeUnit(TimeUnit.MINS);
	doctorGeneralInfo.setAppointmentSlot(appointmentSlot);

	ConsultationFee consultationFee = new ConsultationFee();
	consultationFee.setAmount(500);
	consultationFee.setCurrency(Currency.INR);
	doctorGeneralInfo.setConsultationFee(consultationFee);

	System.out.println(Converter.ObjectToJSON(doctorGeneralInfo));

    }
}