package com.dpdocter.tests;

import java.io.IOException;
import java.util.Arrays;

import com.dpdocter.beans.AppointmentSlot;
import com.dpdocter.beans.ConsultationFee;
import com.dpdocter.beans.DoctorGeneralInfo;
import com.dpdocter.enums.Currency;
import com.dpdocter.enums.TimeUnit;

public class GeneralTests {

    public static void main(String[] args) throws IOException {
	DoctorGeneralInfo generalInfo = new DoctorGeneralInfo();

	generalInfo.setAppointmentBookingNumber(Arrays.asList("1234567890"));

	AppointmentSlot appointmentSlot = new AppointmentSlot();

	appointmentSlot.setTime(30);
	appointmentSlot.setTimeUnit(TimeUnit.MINS);
	generalInfo.setAppointmentSlot(appointmentSlot);

	ConsultationFee consultationFee = new ConsultationFee();

	consultationFee.setAmount(300);
	consultationFee.setCurrency(Currency.INR);
	generalInfo.setConsultationFee(consultationFee);

	generalInfo.setDoctorId("566a6e06e4b0a60366e8f722");
	generalInfo.setId("566d175ae4b029f4f8435d56");
	generalInfo.setLocationId("566a6e2ce4b0a60366e8f728");

	System.out.println(Converter.ObjectToJSON(generalInfo));
    }

}
