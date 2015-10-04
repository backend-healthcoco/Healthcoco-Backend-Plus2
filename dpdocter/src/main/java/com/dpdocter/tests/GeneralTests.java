package com.dpdocter.tests;

import java.util.Arrays;
import java.util.Date;

import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.SMSTrackDetail;
import com.dpdocter.enums.SMSStatus;

public class GeneralTests {

    public static void main(String[] args) {
	SMS sms1 = new SMS();
	sms1.setSmsText("This is a test SMS");
	SMSAddress smsAddress1 = new SMSAddress();
	smsAddress1.setRecipient("919021703700");
	sms1.setSmsAddress(smsAddress1);

	SMS sms2 = new SMS();
	sms2.setSmsText("This is a second test SMS");
	SMSAddress smsAddress2 = new SMSAddress();
	smsAddress2.setRecipient("919371747404");
	sms2.setSmsAddress(smsAddress2);

	SMSDetail smsDetail1 = new SMSDetail();

	smsDetail1.setDeliveryStatus(SMSStatus.IN_PROGRESS);
	smsDetail1.setPatientId("55ef30f1426e2ee85c67fd10");
	smsDetail1.setSentTime(new Date());
	smsDetail1.setSms(sms1);

	SMSDetail smsDetail2 = new SMSDetail();

	smsDetail2.setDeliveryStatus(SMSStatus.IN_PROGRESS);
	smsDetail2.setPatientId("55ef30f1426e2ee85c67fd10");
	smsDetail2.setSentTime(new Date());
	smsDetail2.setSms(sms2);

	SMSTrackDetail smsTrackDetail = new SMSTrackDetail();

	smsTrackDetail.setDoctorId("55edf013426eb4845f2e0f9b");
	smsTrackDetail.setHospitalId("55edf013426eb4845f2e0fa0");
	smsTrackDetail.setLocationId("55edf013426eb4845f2e0fa1");
	smsTrackDetail.setSmsDetails(Arrays.asList(smsDetail1, smsDetail2));

	System.out.println(Converter.ObjectToJSON(smsTrackDetail));

    }
}