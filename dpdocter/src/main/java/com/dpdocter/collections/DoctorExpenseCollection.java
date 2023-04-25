package com.dpdocter.collections;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.VendorExpense;
import com.dpdocter.enums.ModeOfPayment;

@Document(collection = "doctor_expense_cl")
public class DoctorExpenseCollection extends GenericCollection {
	@Id
	public ObjectId id;
	@Field
	public ObjectId doctorId;
	@Field
	public ObjectId locationId;
	@Field
	public ObjectId hospitalId;
	@Field
	public String expenseType;
	@Field
	public ModeOfPayment modeOfPayment = ModeOfPayment.CASH;
	@Field
	public String chequeNo;
	@Field
	public Double cost = 0.0;
	@Field
	public Date toDate = new Date();
	@Field
	public String notes;
	@Field
	public Boolean discarded = false;
	@Field
	public VendorExpense vendor;
	@Field
	public String uniqueExpenseId;

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getUniqueExpenseId() {
		return uniqueExpenseId;
	}

	public void setUniqueExpenseId(String uniqueExpenseId) {
		this.uniqueExpenseId = uniqueExpenseId;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getExpenseType() {
		return expenseType;
	}

	public void setExpenseType(String expenseType) {
		this.expenseType = expenseType;
	}

	public ModeOfPayment getModeOfPayment() {
		return modeOfPayment;
	}

	public void setModeOfPayment(ModeOfPayment modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}

	public String getChequeNo() {
		return chequeNo;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public VendorExpense getVendor() {
		return vendor;
	}

	public void setVendor(VendorExpense vendor) {
		this.vendor = vendor;
	}

}
