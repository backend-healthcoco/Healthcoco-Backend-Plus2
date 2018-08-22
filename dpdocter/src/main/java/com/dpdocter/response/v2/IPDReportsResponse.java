package com.dpdocter.response.v2;

import java.util.List;

import com.dpdocter.beans.v2.IPDReports;

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
