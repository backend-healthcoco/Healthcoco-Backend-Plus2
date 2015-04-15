package com.dpdocter.response;

import java.util.List;
import java.util.UUID;

import com.dpdocter.beans.Strength;
import com.dpdocter.enums.DrugTypeEnum;

public class DrugAddEditResponse {
	private String id;

	private DrugTypeEnum drugType;

	private String drugName;

	private String description;

	private Strength strength;

	private List<String> genericNames;

	private String doctorId;

	private String hospitalId;

	private String locationId;

	private UUID drugCode;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DrugTypeEnum getDrugType() {
		return drugType;
	}

	public void setDrugType(DrugTypeEnum drugType) {
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

	public List<String> getGenericNames() {
		return genericNames;
	}

	public void setGenericNames(List<String> genericNames) {
		this.genericNames = genericNames;
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

	@Override
	public String toString() {
		return "DrugAddEditResponse [id=" + id + ", drugType=" + drugType + ", drugName=" + drugName + ", description=" + description + ", strength="
				+ strength + ", genericNames=" + genericNames + ", doctorId=" + doctorId + ", hospitalId=" + hospitalId + ", locationId=" + locationId + "]";
	}

}
