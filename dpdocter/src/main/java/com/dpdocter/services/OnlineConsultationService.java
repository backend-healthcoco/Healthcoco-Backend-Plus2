package com.dpdocter.services;

import java.util.Date;

import com.dpdocter.beans.OnlineConsultationAnalytics;

public interface OnlineConsultationService {

	OnlineConsultationAnalytics getConsultationAnalytics(String fromDate,String toDate,String doctorId,String locationId,String type);
		
}
