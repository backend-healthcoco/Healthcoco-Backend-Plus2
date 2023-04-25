package com.dpdocter.beans;

import java.util.Date;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.ModeOfPayment;

public class DoctorExpense extends GenericCollection {
	public String id;
	public String doctorId;
	public String locationId;
	public String hospitalId;
	public String expenseType;
	public ModeOfPayment modeOfPayment = ModeOfPayment.CASH;
	public String chequeNo;
	public Double cost = 0.0;
	public Date toDate = new Date();
	public String notes;

	public VendorExpense vendor;
	public Boolean discarded = false;
	public String uniqueExpenseId;

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

	public VendorExpense getVendor() {
		return vendor;
	}

	public void setVendor(VendorExpense vendor) {
		this.vendor = vendor;
	}

}
