package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.IPDReports;
import com.dpdocter.beans.OPDReports;
import com.dpdocter.beans.OTReports;

public interface ReportsService {

	public IPDReports submitIPDReport(IPDReports ipdReports);

	public OPDReports submitOPDReport(OPDReports opdReports);

	public OTReports submitOTReport(OTReports otReports);

	public List<OPDReports> getOPDReportsList(Long startDate, Long endDate, String doctorId, String LocationId,
			String hospitalId);

	public List<IPDReports> getIPDReportsList(Long startDate, Long endDate, String doctorId, String LocationId,
			String hospitalId);

	public List<OTReports> getOTReportsList(Long startDate, Long endDate, String doctorId, String LocationId,
			String hospitalId);

}
