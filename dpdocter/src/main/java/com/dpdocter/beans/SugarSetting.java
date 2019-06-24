package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class SugarSetting extends GenericCollection {

	private String id;
	private String patientId;
	private String type;
	private String insulinTherapy;
	private Boolean pills = false;
	private String bloodGlucoseUnit;
	private String carbsUnit;
	private String targetRanges;
	private String meter;

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

	@Override
	public String toString() {
		return "SugarSetting [id=" + id + ", patientId=" + patientId + ", type=" + type + ", insulinTherapy="
				+ insulinTherapy + ", pills=" + pills + ", bloodGlucoseUnit=" + bloodGlucoseUnit + ", carbsUnit="
				+ carbsUnit + ", targetRanges=" + targetRanges + ", meter=" + meter + "]";
	}

}
