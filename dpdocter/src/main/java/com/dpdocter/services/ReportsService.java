package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DeliveryReports;
import com.dpdocter.beans.IPDReports;
import com.dpdocter.beans.OPDReports;
import com.dpdocter.beans.OTReports;

public interface ReportsService {

	public IPDReports submitIPDReport(IPDReports ipdReports);

	public OPDReports submitOPDReport(OPDReports opdReports);

	public OTReports submitOTReport(OTReports otReports);

	public DeliveryReports submitDeliveryReport(DeliveryReports deliveryReports);

	List<IPDReports> getIPDReportsList(String locationId, String doctorId, String patientId, String from, String to,
			int page, int size, String updatedTime);

	List<OPDReports> getOPDReportsList(String locationId, String doctorId, String patientId, String from, String to,
			int page, int size, String updatedTime);

	List<OTReports> getOTReportsList(String locationId, String doctorId, String patientId, String from, String to,
			int page, int size, String updatedTime);

	List<DeliveryReports> getDeliveryReportsList(String locationId, String doctorId, String patientId, String from,
			String to, int page, int size, String updatedTime);

	public Boolean addPrescriptionOPDReports();

}
