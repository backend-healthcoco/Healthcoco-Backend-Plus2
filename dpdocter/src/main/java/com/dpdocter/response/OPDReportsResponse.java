package com.dpdocter.response;

import java.util.List;

public class OPDReportsResponse {

	private List<OPDReportCustomResponse> opdReports;
	private Integer count;

	public List<OPDReportCustomResponse> getOpdReports() {
		return opdReports;
	}

	public void setOpdReports(List<OPDReportCustomResponse> opdReports) {
		this.opdReports = opdReports;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "OPDReportsResponse [opdReports=" + opdReports + ", count=" + count + "]";
	}

}
