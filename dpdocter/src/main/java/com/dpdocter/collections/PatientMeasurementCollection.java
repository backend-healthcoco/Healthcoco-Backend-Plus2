package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.BodyContent;
import com.dpdocter.beans.Ratio;

@Document(collection = "patient_measurement_cl")
public class PatientMeasurementCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId patientId;
	@Field
	private ObjectId assessmentId;
	@Field
	private Double weightInKG = 0.0;
	@Field
	private Double heightInCM = 0.0;
	@Field
	private Double bmi = 0.0;
	@Field
	private Integer bodyAge = 0;
	@Field
	private Ratio waistHipRatio;
	@Field
	private Double bodyFat = 0.0;
	@Field
	private Integer bmr = 0;
	@Field
	private Double vfat = 0.0;
	@Field
	private BodyContent wholeBody;
	@Field
	private BodyContent armBody;
	@Field
	private BodyContent trunkBody;
	@Field
	private BodyContent legBody;

	@Field
	private Boolean isPatientDiscarded = false;
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

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

	public ObjectId getAssessmentId() {
		return assessmentId;
	}

	public void setAssessmentId(ObjectId assessmentId) {
		this.assessmentId = assessmentId;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "PatientMeasurementCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", assessmentId=" + assessmentId
				+ ", weightInKG=" + weightInKG + ", heightInCM=" + heightInCM + ", bmi=" + bmi + ", bodyAge=" + bodyAge
				+ ", waistHipRatio=" + waistHipRatio + ", bodyFat=" + bodyFat + ", bmr=" + bmr + ", vfat=" + vfat
				+ ", wholeBody=" + wholeBody + ", armBody=" + armBody + ", trunkBody=" + trunkBody + ", legBody="
				+ legBody + ", isPatientDiscarded=" + isPatientDiscarded + "]";
	}
}
