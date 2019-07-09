package com.dpdocter.services.v2;

import com.dpdocter.response.v2.DeliveryReportsResponse;
import com.dpdocter.response.v2.IPDReportsResponse;
import com.dpdocter.response.v2.OTReportsResponse;

public interface ReportsService {


	IPDReportsResponse getIPDReportsList(String locationId, String doctorId, String patientId, String from, String to,
			int page, int size, String updatedTime, Boolean discarded);


	OTReportsResponse getOTReportsList(String locationId, String doctorId, String patientId, String from, String to,
			int page, int size, String updatedTime, Boolean discarded);

	DeliveryReportsResponse getDeliveryReportsList(String locationId, String doctorId, String patientId, String from,
			String to, int page, int size, String updatedTime, Boolean discarded);

}
