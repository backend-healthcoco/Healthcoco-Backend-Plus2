package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.ModeOfPayment;
import com.dpdocter.enums.ReceiptType;

@Document(collection = "dental_works_receipt_cl")
public class DentalWorksReceiptCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String uniqueReceiptId;

	@Field
	private ReceiptType receiptType;

	@Field
	private ObjectId doctorId;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Field
	private ObjectId dentalLabLocationId;

	@Field
	private ObjectId dentalLabHospitalId;

	@Field
	private ModeOfPayment modeOfPayment;

	@Field
	private Double amountPaid = 0.0;

	@Field
	private Double remainingAmount = 0.0;

	@Field
	private Long receivedDate;

	@Field
	private Boolean discarded = false;

	@Field
	private String chequeNumber;

	@Field
	private String note;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getUniqueReceiptId() {
		return uniqueReceiptId;
	}

	public void setUniqueReceiptId(String uniqueReceiptId) {
		this.uniqueReceiptId = uniqueReceiptId;
	}

	public ReceiptType getReceiptType() {
		return receiptType;
	}

	public void setReceiptType(ReceiptType receiptType) {
		this.receiptType = receiptType;
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

	public ObjectId getDentalLabLocationId() {
		return dentalLabLocationId;
	}

	public void setDentalLabLocationId(ObjectId dentalLabLocationId) {
		this.dentalLabLocationId = dentalLabLocationId;
	}

	public ObjectId getDentalLabHospitalId() {
		return dentalLabHospitalId;
	}

	public void setDentalLabHospitalId(ObjectId dentalLabHospitalId) {
		this.dentalLabHospitalId = dentalLabHospitalId;
	}

	public ModeOfPayment getModeOfPayment() {
		return modeOfPayment;
	}

	public void setModeOfPayment(ModeOfPayment modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}

	public Double getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(Double amountPaid) {
		this.amountPaid = amountPaid;
	}

	public Double getRemainingAmount() {
		return remainingAmount;
	}

	public void setRemainingAmount(Double remainingAmount) {
		this.remainingAmount = remainingAmount;
	}

	public Long getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Long receivedDate) {
		this.receivedDate = receivedDate;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getChequeNumber() {
		return chequeNumber;
	}

	public void setChequeNumber(String chequeNumber) {
		this.chequeNumber = chequeNumber;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
