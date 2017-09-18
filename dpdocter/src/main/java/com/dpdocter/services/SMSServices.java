package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.SMSDeliveryReports;
import com.dpdocter.beans.SMSFormat;
import com.dpdocter.beans.SMSTrack;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.response.SMSResponse;
import com.twilio.sdk.TwilioRestException;

public interface SMSServices {
	Boolean sendSMS(SMSTrackDetail smsTrackDetail, Boolean save);

    SMSResponse getSMS(int page, int size, String doctorId, String locationId, String hospitalId);

    List<SMSTrack> getSMSDetails(int page, int size, String patientId, String doctorId, String locationId, String hospitalId);

    void updateDeliveryReports(List<SMSDeliveryReports> request);

    void addNumber(String mobileNumber);

    void deleteNumber(String mobileNumber);

    SMSTrackDetail createSMSTrackDetail(String doctorId, String locationId, String hospitalId, String patientId, String patientName, String message,
	    String mobileNumber, String type);

    SMSFormat addSmsFormat(SMSFormat request);

    List<SMSFormat> getSmsFormat(String doctorId, String locationId, String hospitalId, String type);

	Boolean sendOTPSMS(SMSTrackDetail smsTrackDetail, Boolean save) throws TwilioRestException;
	
	Boolean sendOTPSMS(SMSTrackDetail smsTrackDetail,String otp , Boolean save) throws TwilioRestException;

	String getBulkSMSResponse(List<String> mobileNumbers, String message);

	String getOTPSMSResponse(String mobileNumber, String message, String otp);

	Boolean sendAndSaveOTPSMS(String message, String mobileNumber, String otp);

}
