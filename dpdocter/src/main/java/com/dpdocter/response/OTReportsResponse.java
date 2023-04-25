package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.OTReports;

public class OTReportsResponse {

	private List<OTReports> otReports;
	private Integer count;

	public List<OTReports> getOtReports() {
		return otReports;
	}

	public void setOtReports(List<OTReports> otReports) {
		this.otReports = otReports;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "OTReportsResponse [otReports=" + otReports + ", count=" + count + "]";
	}

}
