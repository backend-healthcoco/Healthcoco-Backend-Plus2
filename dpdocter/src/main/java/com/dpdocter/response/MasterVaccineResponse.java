package com.dpdocter.response;

import org.bson.types.ObjectId;

public class MasterVaccineResponse {

	private String id;
	private String name;
	private String longName;
	private Integer periodTime;
	private String duration;
	private Boolean isChartVaccine = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public Integer getPeriodTime() {
		return periodTime;
	}

	public void setPeriodTime(Integer periodTime) {
		this.periodTime = periodTime;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public Boolean getIsChartVaccine() {
		return isChartVaccine;
	}

	public void setIsChartVaccine(Boolean isChartVaccine) {
		this.isChartVaccine = isChartVaccine;
	}

}
