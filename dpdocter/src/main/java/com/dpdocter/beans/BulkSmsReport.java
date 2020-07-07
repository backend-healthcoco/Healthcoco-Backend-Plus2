package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class BulkSmsReport extends GenericCollection {

	private String id;

	private String doctorId;

	private String locationId;
	private String hospitalId;

	private List<SMSDetail> smsDetails;

	private String type;

	private String responseId;
	
	private Long delivered;
	
	private Long undelivered;
	
	private Long totalCreditsSpent;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public List<SMSDetail> getSmsDetails() {
		return smsDetails;
	}

	public void setSmsDetails(List<SMSDetail> smsDetails) {
		this.smsDetails = smsDetails;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getResponseId() {
		return responseId;
	}

	public void setResponseId(String responseId) {
		this.responseId = responseId;
	}
	
	

	public Long getDelivered() {
		return delivered;
	}

	public void setDelivered(Long delivered) {
		this.delivered = delivered;
	}

	public Long getUndelivered() {
		return undelivered;
	}

	public void setUndelivered(Long undelivered) {
		this.undelivered = undelivered;
	}
	
	

	public Long getTotalCreditsSpent() {
		return totalCreditsSpent;
	}

	public void setTotalCreditsSpent(Long totalCreditsSpent) {
		this.totalCreditsSpent = totalCreditsSpent;
	}

	@Override
	public String toString() {
		return "BulkSmsReport [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", smsDetails=" + smsDetails + ", type=" + type + ", responseId=" + responseId + "]";
	}

}
