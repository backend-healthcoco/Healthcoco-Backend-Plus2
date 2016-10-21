package com.dpdocter.beans;

public class GrowthChart {

	private String id;
	private String patientId;
	private Integer height;
	private Double weight;
	private Integer skullCircumference;
	private String progress;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public Integer getSkullCircumference() {
		return skullCircumference;
	}

	public void setSkullCircumference(Integer skullCircumference) {
		this.skullCircumference = skullCircumference;
	}

	public String getProgress() {
		return progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	@Override
	public String toString() {
		return "GrowthChart [id=" + id + ", patientId=" + patientId + ", height=" + height + ", weight=" + weight
				+ ", skullCircumference=" + skullCircumference + ", progress=" + progress + "]";
	}

}
