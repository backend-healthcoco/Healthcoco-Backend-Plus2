package com.dpdocter.elasticsearch.response;

import com.dpdocter.beans.Age;

public class GrowthChartGraphResponse {

	private String patientId;
	private Integer height;
	private Double weight;
	private Double bmi;
	private Integer skullCircumference;
	private Age age;

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Double getBmi() {
		return bmi;
	}

	public void setBmi(Double bmi) {
		this.bmi = bmi;
	}

	public Integer getSkullCircumference() {
		return skullCircumference;
	}

	public void setSkullCircumference(Integer skullCircumference) {
		this.skullCircumference = skullCircumference;
	}

	public Age getAge() {
		return age;
	}

	public void setAge(Age age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "GrowthChartGraphResponse [patientId=" + patientId + ", height=" + height + ", weight=" + weight
				+ ", bmi=" + bmi + ", skullCircumference=" + skullCircumference + ", age=" + age + "]";
	}

}
