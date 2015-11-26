package com.dpdocter.sms.services;

import java.util.List;

import com.dpdocter.beans.SMSDeliveryReports;
import com.dpdocter.beans.SMSTrack;
import com.dpdocter.beans.SMSTrackDetail;
import com.dpdocter.response.SMSResponse;

public interface SMSServices {
    void sendSMS(SMSTrackDetail smsTrackDetail, Boolean save);

    SMSResponse getSMS(int page, int size, String doctorId, String locationId, String hospitalId);

    List<SMSTrack> getSMSDetails(int page, int size, String patientId, String doctorId, String locationId, String hospitalId);

    void updateDeliveryReports(List<SMSDeliveryReports> request);

    void addNumber(String mobileNumber);

    void deleteNumber(String mobileNumber);
    
    SMSTrackDetail createSMSTrackDetail(String doctorId, String locationId, String hospitalId, String patientId, String message, String mobileNumber);
    
}
