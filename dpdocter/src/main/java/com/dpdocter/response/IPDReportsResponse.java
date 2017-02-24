package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.IPDReports;

public class IPDReportsResponse {

	List<IPDReports> ipdReports;
	Integer count;

	public List<IPDReports> getIpdReports() {
		return ipdReports;
	}

	public void setIpdReports(List<IPDReports> ipdReports) {
		this.ipdReports = ipdReports;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "IPDReportsResponse [ipdReports=" + ipdReports + ", count=" + count + "]";
	}

}
