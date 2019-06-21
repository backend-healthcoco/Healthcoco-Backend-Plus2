package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Treatment extends GenericCollection {
    
	private ObjectId treatmentServiceId;

	private ObjectId doctorId;
	
	private Date treatmentDate;
	
	private String status;

	private double cost = 0.0;

	private String note;
	
	private Discount discount;

	private double finalCost=0.0;

	private Quantity quantity;

	private List<Fields> treatmentFields;
	
	public ObjectId getTreatmentServiceId() {
		return treatmentServiceId;
	}

	public void setTreatmentServiceId(ObjectId treatmentServiceId) {
		this.treatmentServiceId = treatmentServiceId;
	}

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

	public double getFinalCost() {
		return finalCost;
	}

	public void setFinalCost(double finalCost) {
		this.finalCost = finalCost;
	}

	public List<Fields> getTreatmentFields() {
		return treatmentFields;
	}

	public void setTreatmentFields(List<Fields> treatmentFields) {
		this.treatmentFields = treatmentFields;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public Date getTreatmentDate() {
		return treatmentDate;
	}

	public void setTreatmentDate(Date treatmentDate) {
		this.treatmentDate = treatmentDate;
	}

	@Override
	public String toString() {
		return "Treatment [treatmentServiceId=" + treatmentServiceId + ", doctorId=" + doctorId + ", treatmentDate="
				+ treatmentDate + ", status=" + status + ", cost=" + cost + ", note=" + note + ", discount=" + discount
				+ ", finalCost=" + finalCost + ", quantity=" + quantity + ", treatmentFields=" + treatmentFields + "]";
	}

}
