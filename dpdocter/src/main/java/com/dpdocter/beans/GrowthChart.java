package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class GrowthChart extends GenericCollection {

	private Integer height;
	private Double weight;
	private Double bmi;
	private Integer skullCircumference;
	private String progress;

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

	public String getProgress() {
		return progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	@Override
	public String toString() {
		return "GrowthChart [height=" + height + ", weight=" + weight + ", bmi=" + bmi + ", skullCircumference="
				+ skullCircumference + ", progress=" + progress + "]";
	}

}
