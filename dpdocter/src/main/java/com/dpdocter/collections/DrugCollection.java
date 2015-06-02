package com.dpdocter.collections;

import java.util.List;

import org.codehaus.jackson.map.deser.CreatorContainer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Strength;
import com.dpdocter.enums.DrugTypeEnum;

@Document(collection = "drug_cl")
public class DrugCollection extends GenericCollection {
	@Id
	private String id;

	@Field
	private DrugTypeEnum drugType;

	@Field
	private String drugName;

	@Field
	private String description;

	@Field
	private Strength strength;

	@Field
	private List<String> genericNames;

	@Field
	@Indexed(sparse = true)
	private String doctorId;

	@Field
	@Indexed(sparse = true)
	private String hospitalId;

	@Field
	@Indexed(sparse = true)
	private String locationId;

	@Field
	private boolean isDeleted = false;

	@Field
	private String drugCode;

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

	public boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getDrugCode() {
		return drugCode;
	}

	public void setDrugCode(String drugCode) {
		this.drugCode = drugCode;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	@Override
	public String toString() {
		return "DrugCollection [id=" + id + ", drugType=" + drugType + ", drugName=" + drugName + ", description=" + description + ", strength=" + strength
				+ ", genericNames=" + genericNames + ", doctorId=" + doctorId + ", hospitalId=" + hospitalId + ", locationId=" + locationId + ", isDeleted="
				+ isDeleted + ", drugCode=" + drugCode + "]";
	}

}
