package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class VendorExpense extends GenericCollection{
	
	private String id;
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
	@Override
	public String toString() {
		return "VendorExpense [id=" + id + ", vendorName=" + vendorName + ", licenseNumber=" + licenseNumber + "]";
	}
	
	
	

}
