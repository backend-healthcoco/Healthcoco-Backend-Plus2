package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.Discount;
import com.dpdocter.beans.Treatment;

public class PatientTreatmentAddEditRequest {

	private String id;
	
	private String appoinmentId;
	
	private List<TreatmentRequest> treatments;

	private String patientId;

	private String locationId;

	private String hospitalId;

	private String doctorId;

	private double totalCost = 0.0;

	private Discount totalDiscount;

	private double grandTotal = 0.0;

	public Discount getTotalDiscount() {
		return totalDiscount;
	}

	public void setTotalDiscount(Discount totalDiscount) {
		this.totalDiscount = totalDiscount;
	}

	public double getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(double grandTotal) {
		this.grandTotal = grandTotal;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<TreatmentRequest> getTreatments() {
		return treatments;
	}

	public void setTreatments(List<TreatmentRequest> treatments) {
		this.treatments = treatments;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
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

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}
	

	public String getAppoinmentId() {
		return appoinmentId;
	}

	public void setAppoinmentId(String appoinmentId) {
		this.appoinmentId = appoinmentId;
	}

	@Override
	public String toString() {
		return "PatientTreatmentAddEditRequest [id=" + id + ", treatments=" + treatments + ", patientId=" + patientId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", doctorId=" + doctorId
				+ ", totalCost=" + totalCost + "]";
	}
}
