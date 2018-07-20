package com.dpdocter.response;

import java.util.List;

public class DoctorLabReportResponseWithCount {

	private List<LabReportsResponse> labReportsResponses;
	private Integer count;

	public List<LabReportsResponse> getLabReportsResponses() {
		return labReportsResponses;
	}

	public void setLabReportsResponses(List<LabReportsResponse> labReportsResponses) {
		this.labReportsResponses = labReportsResponses;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "DoctorLabReportResponseWithCount [labReportsResponses=" + labReportsResponses + ", count=" + count
				+ "]";
	}
}