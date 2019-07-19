package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class PatientMeasurementInfo extends GenericCollection {

	private String id;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private String assessmentId;

	private Double weightInKG = 0.0;

	private Double heightInCM = 0.0;

	private Double bmi = 0.0;

	private Integer bodyAge = 0;

	private Ratio waistHipRatio;

	private Double bodyFat = 0.0;

	private Integer bmr = 0;

	private Double vfat = 0.0;;

	private BodyContent wholeBody;

	private BodyContent armBody;

	private BodyContent trunkBody;

	private BodyContent legBody;

	public Double getWeightInKG() {
		return weightInKG;
	}

	public void setWeightInKG(Double weightInKG) {
		this.weightInKG = weightInKG;
	}

	public Double getHeightInCM() {
		return heightInCM;
	}

	public void setHeightInCM(Double heightInCM) {
		this.heightInCM = heightInCM;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Double getBmi() {
		return bmi;
	}

	public void setBmi(Double bmi) {
		this.bmi = bmi;
	}

	public Integer getBodyAge() {
		return bodyAge;
	}

	public void setBodyAge(Integer bodyAge) {
		this.bodyAge = bodyAge;
	}

	public Ratio getWaistHipRatio() {
		return waistHipRatio;
	}

	public void setWaistHipRatio(Ratio waistHipRatio) {
		this.waistHipRatio = waistHipRatio;
	}

	public Double getBodyFat() {
		return bodyFat;
	}

	public void setBodyFat(Double bodyFat) {
		this.bodyFat = bodyFat;
	}

	public Integer getBmr() {
		return bmr;
	}

	public void setBmr(Integer bmr) {
		this.bmr = bmr;
	}

	public Double getVfat() {
		return vfat;
	}

	public void setVfat(Double vfat) {
		this.vfat = vfat;
	}

	public BodyContent getWholeBody() {
		return wholeBody;
	}

	public void setWholeBody(BodyContent wholeBody) {
		this.wholeBody = wholeBody;
	}

	public BodyContent getArmBody() {
		return armBody;
	}

	public void setArmBody(BodyContent armBody) {
		this.armBody = armBody;
	}

	public BodyContent getTrunkBody() {
		return trunkBody;
	}

	public void setTrunkBody(BodyContent trunkBody) {
		this.trunkBody = trunkBody;
	}

	public BodyContent getLegBody() {
		return legBody;
	}

	public void setLegBody(BodyContent legBody) {
		this.legBody = legBody;
	}

	public String getAssessmentId() {
		return assessmentId;
	}

	public void setAssessmentId(String assessmentId) {
		this.assessmentId = assessmentId;
	}

}
