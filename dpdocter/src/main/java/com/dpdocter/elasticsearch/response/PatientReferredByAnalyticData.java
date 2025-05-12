package com.dpdocter.elasticsearch.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.TreatmentService;

public class PatientReferredByAnalyticData {
	private String id;
	private String localPatientName;
	private String mobileNumber;
	private Date receiptDate;
	private String referredBy;
    private List<String> servicesArray; // updated field to hold array of service names
    private Double cost = 0.0;
	private String patientAnalyticType;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLocalPatientName() {
		return localPatientName;
	}

	public void setLocalPatientName(String localPatientName) {
		this.localPatientName = localPatientName;
	}

	public Date getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getReferredBy() {
		return referredBy;
	}

	public void setReferredBy(String referredBy) {
		this.referredBy = referredBy;
	}

	public List<String> getServicesArray() {
		return servicesArray;
	}

	public void setServicesArray(List<String> servicesArray) {
		this.servicesArray = servicesArray;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public String getPatientAnalyticType() {
		return patientAnalyticType;
	}

	public void setPatientAnalyticType(String patientAnalyticType) {
		this.patientAnalyticType = patientAnalyticType;
	}

}
