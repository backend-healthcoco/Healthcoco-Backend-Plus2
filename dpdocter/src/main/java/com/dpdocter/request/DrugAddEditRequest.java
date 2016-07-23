package com.dpdocter.request;

import java.util.List;
import java.util.UUID;

import com.dpdocter.beans.DrugType;
import com.dpdocter.beans.Strength;

public class DrugAddEditRequest {
    private String id;

    private DrugType drugType;

    private String drugName;

    private String explanation;

    private Strength strength;

    private List<String> genericCodes;

    private String doctorId;

    private String hospitalId;

    private String locationId;

    private UUID drugCode;

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

    public UUID getDrugCode() {
	return drugCode;
    }

    public void setDrugCode(UUID drugCode) {
	this.drugCode = drugCode;
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
		return "DrugAddEditRequest [id=" + id + ", drugType=" + drugType + ", drugName=" + drugName + ", explanation="
				+ explanation + ", strength=" + strength + ", genericCodes=" + genericCodes + ", doctorId=" + doctorId
				+ ", hospitalId=" + hospitalId + ", locationId=" + locationId + ", drugCode=" + drugCode
				+ ", companyName=" + companyName + ", packSize=" + packSize + ", MRP=" + MRP + "]";
	}

}
