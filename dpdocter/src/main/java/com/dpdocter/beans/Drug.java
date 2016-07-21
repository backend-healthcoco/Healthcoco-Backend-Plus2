package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class Drug extends GenericCollection{
   
	private String id;

    private DrugType drugType;

    private String drugName;

    private String explanation;

    private Strength strength;

    private Boolean discarded;

    private String doctorId;

    private String hospitalId;

    private String locationId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DrugType getDrugType() {
		return drugType;
	}

	public void setDrugType(DrugType drugType) {
		this.drugType = drugType;
	}

	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public Strength getStrength() {
		return strength;
	}

	public void setStrength(Strength strength) {
		this.strength = strength;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	@Override
	public String toString() {
		return "Drug [id=" + id + ", drugType=" + drugType + ", drugName=" + drugName + ", explanation=" + explanation
				+ ", strength=" + strength + ", discarded=" + discarded + ", doctorId=" + doctorId + ", hospitalId="
				+ hospitalId + ", locationId=" + locationId + "]";
	}
}
