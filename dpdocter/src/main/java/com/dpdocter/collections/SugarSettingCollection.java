package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "sugar_setting_cl")
public class SugarSettingCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private ObjectId patientId;
	@Field
	private String type;
	@Field
	private String insulinTherapy;
	@Field
	private Boolean pills = false;
	@Field
	private String bloodGlucoseUnit;
	@Field
	private String carbsUnit;
	@Field
	private String targetRanges;
	@Field
	private String meter;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInsulinTherapy() {
		return insulinTherapy;
	}

	public void setInsulinTherapy(String insulinTherapy) {
		this.insulinTherapy = insulinTherapy;
	}

	public Boolean getPills() {
		return pills;
	}

	public void setPills(Boolean pills) {
		this.pills = pills;
	}

	public String getBloodGlucoseUnit() {
		return bloodGlucoseUnit;
	}

	public void setBloodGlucoseUnit(String bloodGlucoseUnit) {
		this.bloodGlucoseUnit = bloodGlucoseUnit;
	}

	public String getCarbsUnit() {
		return carbsUnit;
	}

	public void setCarbsUnit(String carbsUnit) {
		this.carbsUnit = carbsUnit;
	}

	public String getTargetRanges() {
		return targetRanges;
	}

	public void setTargetRanges(String targetRanges) {
		this.targetRanges = targetRanges;
	}

	public String getMeter() {
		return meter;
	}

	public void setMeter(String meter) {
		this.meter = meter;
	}

}
