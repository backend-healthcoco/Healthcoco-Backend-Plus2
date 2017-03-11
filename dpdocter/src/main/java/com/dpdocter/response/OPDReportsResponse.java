package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.OPDReports;

public class OPDReportsResponse {

	List<OPDReports> opdReports;
	Integer count;

	public List<OPDReports> getOpdReports() {
		return opdReports;
	}

	public void setOpdReports(List<OPDReports> opdReports) {
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
