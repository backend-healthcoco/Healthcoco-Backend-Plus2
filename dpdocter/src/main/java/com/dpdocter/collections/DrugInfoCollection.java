package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "drug_info_cl")
public class DrugInfoCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String drugCode;
	@Field
	private String brandName;
	@Field
	private String drugType;
	@Field
	private String packForm;
	@Field
	private String packSize;
	@Field
	private String companyName;
	@Field
	private String genericContent;
	@Field
	private String category;
	@Field
	private String composition;
	@Field
	private Double price;
	@Field
	private String pricePerDrug;
	@Field
	private Boolean isPrescriptionRequired;
	@Field
	private Boolean discarded;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getDrugCode() {
		return drugCode;
	}

	public void setDrugCode(String drugCode) {
		this.drugCode = drugCode;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getDrugType() {
		return drugType;
	}

	public void setDrugType(String drugType) {
		this.drugType = drugType;
	}

	public String getPackForm() {
		return packForm;
	}

	public void setPackForm(String packForm) {
		this.packForm = packForm;
	}

	public String getPackSize() {
		return packSize;
	}

	public void setPackSize(String packSize) {
		this.packSize = packSize;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getGenericContent() {
		return genericContent;
	}

	public void setGenericContent(String genericContent) {
		this.genericContent = genericContent;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getComposition() {
		return composition;
	}

	public void setComposition(String composition) {
		this.composition = composition;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getPricePerDrug() {
		return pricePerDrug;
	}

	public void setPricePerDrug(String pricePerDrug) {
		this.pricePerDrug = pricePerDrug;
	}

	public Boolean getIsPrescriptionRequired() {
		return isPrescriptionRequired;
	}

	public void setIsPrescriptionRequired(Boolean isPrescriptionRequired) {
		this.isPrescriptionRequired = isPrescriptionRequired;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

}
