package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class VendorExpense extends GenericCollection{
	
	private String id;
	public String doctorId;
	public String locationId;
	public String hospitalId;
	private String vendorName;
	private String licenseNumber;
	private Boolean discarded=false;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
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
	
	
	public String getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}
	public String getLocationId() {
		return locationId;
	}
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	public String getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}
	@Override
	public String toString() {
		return "VendorExpense [id=" + id + ", vendorName=" + vendorName + ", licenseNumber=" + licenseNumber + "]";
	}
	
	
	

}
