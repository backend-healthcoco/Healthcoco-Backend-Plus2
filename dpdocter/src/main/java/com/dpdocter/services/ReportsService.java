package com.dpdocter.services;

import com.dpdocter.beans.DeliveryReports;
import com.dpdocter.beans.IPDReports;
import com.dpdocter.beans.OPDReports;
import com.dpdocter.beans.OTReports;
import com.dpdocter.response.DeliveryReportsResponse;
import com.dpdocter.response.IPDReportsResponse;
import com.dpdocter.response.OPDReportsResponse;
import com.dpdocter.response.OTReportsResponse;

public interface ReportsService {

	public IPDReports submitIPDReport(IPDReports ipdReports);

	public OPDReports submitOPDReport(OPDReports opdReports);

	public OTReports submitOTReport(OTReports otReports);

	public DeliveryReports submitDeliveryReport(DeliveryReports deliveryReports);

	IPDReportsResponse getIPDReportsList(String locationId, String doctorId, String patientId, String from, String to,
			int page, int size, String updatedTime);

	OPDReportsResponse getOPDReportsList(String locationId, String doctorId, String patientId, String from, String to,
			int page, int size, String updatedTime);

	OTReportsResponse getOTReportsList(String locationId, String doctorId, String patientId, String from, String to,
			int page, int size, String updatedTime);

	DeliveryReportsResponse getDeliveryReportsList(String locationId, String doctorId, String patientId, String from,
			String to, int page, int size, String updatedTime);

	public Boolean addPrescriptionOPDReports();

	OPDReports getOPDReportByVisitId(String visitId);

	public String getOTReportsFile(String otId);

	public String getDeliveryReportsFile(String reportId);
}
