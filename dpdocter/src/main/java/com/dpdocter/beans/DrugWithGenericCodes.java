package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class DrugWithGenericCodes extends GenericCollection{
    private String id;

    private DrugType drugType;

    private String drugName;

    private String explanation;

    private Strength strength;

    private List<String> genericCodes;

    private String drugCode;

    private Boolean discarded;

    private String companyName;

    private String packSize;

    private String MRP;

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

	public List<String> getGenericCodes() {
		return genericCodes;
	}

	public void setGenericCodes(List<String> genericCodes) {
		this.genericCodes = genericCodes;
	}

	public String getDrugCode() {
	return drugCode;
    }

    public void setDrugCode(String drugCode) {
	this.drugCode = drugCode;
    }

    public Boolean getDiscarded() {
	return discarded;
    }

    public void setDiscarded(Boolean discarded) {
	this.discarded = discarded;
    }

    public String getCompanyName() {
	return companyName;
    }

    public void setCompanyName(String companyName) {
	this.companyName = companyName;
    }

    public String getPackSize() {
	return packSize;
    }

    public void setPackSize(String packSize) {
	this.packSize = packSize;
    }

    public String getMRP() {
	return MRP;
    }

    public void setMRP(String mRP) {
	MRP = mRP;
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
				+ ", strength=" + strength + ", genericCodes=" + genericCodes + ", drugCode=" + drugCode
				+ ", discarded=" + discarded + ", companyName=" + companyName + ", packSize=" + packSize + ", MRP="
				+ MRP + ", doctorId=" + doctorId + ", hospitalId=" + hospitalId + ", locationId=" + locationId + "]";
	}
}
