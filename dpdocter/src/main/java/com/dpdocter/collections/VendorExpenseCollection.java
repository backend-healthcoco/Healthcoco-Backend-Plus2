package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "vendor_expense_cl")
public class VendorExpenseCollection extends GenericCollection{

	@Id
	private ObjectId id;
	@Field
	public ObjectId doctorId;
	@Field
	public ObjectId locationId;
	@Field
	public ObjectId hospitalId;
	@Field
	private String vendorName;
	@Field
	private String licenseNumber;
	@Field
	private Boolean discarded=false;
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getVendorName() {
		return vendorName;
	}
	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}
	public String getLicenseNumber() {
		return licenseNumber;
	}
	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}
	public Boolean getDiscarded() {
		return discarded;
	}
	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}
	
	public ObjectId getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}
	public ObjectId getLocationId() {
		return locationId;
	}
	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}
	public ObjectId getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}
	@Override
	public String toString() {
		return "VendorExpenseCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", vendorName=" + vendorName + ", licenseNumber=" + licenseNumber
				+ ", discarded=" + discarded + "]";
	}
	
	
	
	
}
