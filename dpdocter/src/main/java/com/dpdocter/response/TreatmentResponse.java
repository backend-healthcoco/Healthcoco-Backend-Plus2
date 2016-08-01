package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.TreatmentService;
import com.dpdocter.enums.PatientTreatmentStatus;

public class TreatmentResponse {

    private TreatmentService treatmentService;

    private PatientTreatmentStatus status;

    private double cost = 0.0;

    private String note;

    private int quantity = 0;
    
    private List<TreatmentService> treatmentServices;
    
    public PatientTreatmentStatus getStatus() {
		return status;
	}

	public void setStatus(PatientTreatmentStatus status) {
		this.status = status;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public TreatmentService getTreatmentService() {
		return treatmentService;
	}

	public void setTreatmentService(TreatmentService treatmentService) {
		this.treatmentService = treatmentService;
	}

	public List<TreatmentService> getTreatmentServices() {
		return treatmentServices;
	}

	public void setTreatmentServices(List<TreatmentService> treatmentServices) {
		this.treatmentServices = treatmentServices;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "TreatmentResponse [treatmentService=" + treatmentService + ", status=" + status + ", cost=" + cost
				+ ", note=" + note + ", quantity=" + quantity + ", treatmentServices=" + treatmentServices + "]";
	}
}
