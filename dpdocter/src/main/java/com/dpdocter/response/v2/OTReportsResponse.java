package com.dpdocter.response.v2;

import java.util.List;

import com.dpdocter.beans.v2.OTReports;

public class OTReportsResponse {

	List<OTReports> otReports;
	Integer count;

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
