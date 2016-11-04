package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.BirthType;
import com.dpdocter.enums.GestationType;

public class BirthHistory extends GenericCollection {
	private String id;
	private String birthPlace;
	private String obstetricianName;
	private GestationType gestationType;
	private Integer prematureWeeks;
	private BirthType birthType;
	private Integer height;
	private Double weight;
	private Integer birthOrder;
	private String birthProblem;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private String patientId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	@Override
	public String toString() {
		return "BirthHistory [id=" + id + ", birthPlace=" + birthPlace + ", obstetricianName=" + obstetricianName
				+ ", gestationType=" + gestationType + ", prematureWeeks=" + prematureWeeks + ", birthType=" + birthType
				+ ", height=" + height + ", weight=" + weight + ", birthOrder=" + birthOrder + ", birthProblem="
				+ birthProblem + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", patientId=" + patientId + "]";
	}

}
