package com.dpdocter.response;

import java.util.List;

public class PatientVaccineGroupedResponse {

	private String duration;
	private Integer periodTime;
	private List<VaccineResponse> vaccines;

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public Integer getPeriodTime() {
		return periodTime;
	}

	public void setPeriodTime(Integer periodTime) {
		this.periodTime = periodTime;
	}

	public List<VaccineResponse> getVaccines() {
		return vaccines;
	}

	public void setVaccines(List<VaccineResponse> vaccines) {
		this.vaccines = vaccines;
	}

}
