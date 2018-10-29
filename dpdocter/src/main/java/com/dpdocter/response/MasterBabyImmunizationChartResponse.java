package com.dpdocter.response;

import com.dpdocter.beans.Age;
import com.dpdocter.beans.Vaccine;

public class MasterBabyImmunizationChartResponse {

	private Age age;
	private String name;
	private String periodTime;
	private Vaccine vaccine;

	public Age getAge() {
		return age;
	}

	public void setAge(Age age) {
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPeriodTime() {
		return periodTime;
	}

	public void setPeriodTime(String periodTime) {
		this.periodTime = periodTime;
	}

	public Vaccine getVaccine() {
		return vaccine;
	}

	public void setVaccine(Vaccine vaccine) {
		this.vaccine = vaccine;
	}

}
