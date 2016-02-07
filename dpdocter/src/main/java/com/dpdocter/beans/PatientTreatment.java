package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.PatientTreatmentStatus;

public class PatientTreatment extends GenericCollection {
    private String productAndServiceId;

    private PatientTreatmentStatus status;

    private double cost = 0.0;

    private String note;

    public String getProductAndServiceId() {
	return productAndServiceId;
    }

    public void setProductAndServiceId(String productAndServiceId) {
	this.productAndServiceId = productAndServiceId;
    }

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

    @Override
    public String toString() {
	return "PatientTreatment [productAndServiceId=" + productAndServiceId + ", status=" + status + ", cost=" + cost + ", note=" + note + "]";
    }

}
