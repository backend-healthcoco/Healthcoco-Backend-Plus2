package com.dpdocter.collections;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Strength;
import com.dpdocter.enums.DrugTypeEnum;

@Document(collection="drug_cl")
public class DrugCollection {
	@Id
	private String id;
	
	@Field
	private DrugTypeEnum drugType;
	
	@Field
	private String drugName;
	
	@Field
	private Strength strength;
	
	@Field
	private List<String> genericNames;
	
	@Field
	private String doctorId;
	
	


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
	
	
	

	@Override
	public String toString() {
		return "DrugCollection [id=" + id + ", drugType=" + drugType
				+ ", drugName=" + drugName + ", strength=" + strength
				+ ", genericNames=" + genericNames + ", doctorId=" + doctorId
				+ "]";
	}
}
