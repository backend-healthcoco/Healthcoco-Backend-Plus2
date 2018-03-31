package com.dpdocter.response;

public class DoctorPrescriptionItemAnalyticResponse {
	private String name;
	private Integer totalCount = 0;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

}
