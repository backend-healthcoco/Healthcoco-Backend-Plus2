package com.dpdocter.beans;

public class Drug {
    private String id;

    private DrugType drugType;

    private String drugName;

    private String description;

    private Strength strength;

    private String genericId;

    private String drugCode;

    private Boolean discarded;

    private String companyName;

    private String packSize;

    private String MRP;

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

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public Strength getStrength() {
	return strength;
    }

    public void setStrength(Strength strength) {
	this.strength = strength;
    }

    public String getGenericId() {
	return genericId;
    }

    public void setGenericId(String genericId) {
	this.genericId = genericId;
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

    @Override
    public String toString() {
	return "Drug [id=" + id + ", drugType=" + drugType + ", drugName=" + drugName + ", description=" + description + ", strength=" + strength
		+ ", genericId=" + genericId + ", drugCode=" + drugCode + ", discarded=" + discarded + ", companyName=" + companyName + ", packSize=" + packSize
		+ ", MRP=" + MRP + "]";
    }
}
