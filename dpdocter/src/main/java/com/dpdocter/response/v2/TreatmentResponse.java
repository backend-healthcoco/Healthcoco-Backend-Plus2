package com.dpdocter.response.v2;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.Discount;
import com.dpdocter.beans.Fields;
import com.dpdocter.beans.Quantity;
import com.dpdocter.beans.v2.TreatmentService;

public class TreatmentResponse {

	private String doctorId;
	
	private String doctorName;
	
	private Date treatmentDate;
	
	private TreatmentService treatmentService;

	private String treatmentServiceId;
	
	private String status;

	private double cost = 0.0;

	private String note;

	private Discount discount;

	private double finalCost = 0.0;

	private Quantity quantity;

	private List<TreatmentService> treatmentServices;

	private List<Fields> treatmentFields;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
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

	public double getFinalCost() {
		return finalCost;
	}

	public void setFinalCost(double finalCost) {
		this.finalCost = finalCost;
	}

	public Quantity getQuantity() {
		return quantity;
	}

	public void setQuantity(Quantity quantity) {
		this.quantity = quantity;
	}

	public Discount getDiscount() {
		return discount;
	}

	public void setDiscount(Discount discount) {
		this.discount = discount;
	}

	public List<TreatmentService> getTreatmentServices() {
		return treatmentServices;
	}

	public void setTreatmentServices(List<TreatmentService> treatmentServices) {
		this.treatmentServices = treatmentServices;
	}

	public String getTreatmentServiceId() {
		return treatmentServiceId;
	}

	public void setTreatmentServiceId(String treatmentServiceId) {
		this.treatmentServiceId = treatmentServiceId;
	}

	public List<Fields> getTreatmentFields() {
		return treatmentFields;
	}

	public void setTreatmentFields(List<Fields> treatmentFields) {
		this.treatmentFields = treatmentFields;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public Date getTreatmentDate() {
		return treatmentDate;
	}

	public void setTreatmentDate(Date treatmentDate) {
		this.treatmentDate = treatmentDate;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	@Override
	public String toString() {
		return "TreatmentResponse [doctorId=" + doctorId + ", doctorName=" + doctorName + ", treatmentDate="
				+ treatmentDate + ", treatmentService=" + treatmentService + ", treatmentServiceId="
				+ treatmentServiceId + ", status=" + status + ", cost=" + cost + ", note=" + note + ", discount="
				+ discount + ", finalCost=" + finalCost + ", quantity=" + quantity + ", treatmentServices="
				+ treatmentServices + ", treatmentFields=" + treatmentFields + "]";
	}

}
