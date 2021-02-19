package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class HiuDataResponse extends GenericCollection{

	private String transactionId;
	
	private NdhmPatientDetails patient;
	
	private NdhmDoctorDetails doctor;
	
	private List<NdhmPrescriptionDetails>prescription;

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public NdhmPatientDetails getPatient() {
		return patient;
	}

	public void setPatient(NdhmPatientDetails patient) {
		this.patient = patient;
	}

	public NdhmDoctorDetails getDoctor() {
		return doctor;
	}

	public void setDoctor(NdhmDoctorDetails doctor) {
		this.doctor = doctor;
	}

	public List<NdhmPrescriptionDetails> getPrescription() {
		return prescription;
	}

	public void setPrescription(List<NdhmPrescriptionDetails> prescription) {
		this.prescription = prescription;
	}
	
	
	
	
}
