package com.dpdocter.beans;

import com.dpdocter.enums.BirthType;
import com.dpdocter.enums.GestationType;

public class BirthHistory {
	private String birthPlace;
	private String obstetricianName;
	private GestationType gestationType;
	private Integer prematureWeeks;
	private BirthType birthType;
	private Integer height;
	private Double weight;
	private Integer birthOrder;
	private String birthProblem;

	public String getBirthPlace() {
		return birthPlace;
	}

	public void setBirthPlace(String birthPlace) {
		this.birthPlace = birthPlace;
	}

	public String getObstetricianName() {
		return obstetricianName;
	}

	public void setObstetricianName(String obstetricianName) {
		this.obstetricianName = obstetricianName;
	}

	public GestationType getGestationType() {
		return gestationType;
	}

	public void setGestationType(GestationType gestationType) {
		this.gestationType = gestationType;
	}

	public Integer getPrematureWeeks() {
		return prematureWeeks;
	}

	public void setPrematureWeeks(Integer prematureWeeks) {
		this.prematureWeeks = prematureWeeks;
	}

	public BirthType getBirthType() {
		return birthType;
	}

	public void setBirthType(BirthType birthType) {
		this.birthType = birthType;
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

	public Integer getBirthOrder() {
		return birthOrder;
	}

	public void setBirthOrder(Integer birthOrder) {
		this.birthOrder = birthOrder;
	}

	public String getBirthProblem() {
		return birthProblem;
	}

	public void setBirthProblem(String birthProblem) {
		this.birthProblem = birthProblem;
	}

	@Override
	public String toString() {
		return "BirthHistory [birthPlace=" + birthPlace + ", obstetricianName=" + obstetricianName + ", gestationType="
				+ gestationType + ", prematureWeeks=" + prematureWeeks + ", birthType=" + birthType + ", height="
				+ height + ", weight=" + weight + ", birthOrder=" + birthOrder + ", birthProblem=" + birthProblem + "]";
	}

}
