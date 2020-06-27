package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class BulkSmsCredits extends GenericCollection {

	private String id;

	private long creditBalance;
	
	private long creditSpent;
	
	private String doctorId;
	
	private String locationId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	

	public long getCreditBalance() {
		return creditBalance;
	}

	public void setCreditBalance(long creditBalance) {
		this.creditBalance = creditBalance;
	}

	public long getCreditSpent() {
		return creditSpent;
	}

	public void setCreditSpent(long creditSpent) {
		this.creditSpent = creditSpent;
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
	
	

}
