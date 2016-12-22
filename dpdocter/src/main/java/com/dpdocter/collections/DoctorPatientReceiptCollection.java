package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.ModeOfPayment;
import com.dpdocter.enums.ReceiptType;

@Document(collection = "doctor_patient_receipt_cl")
public class DoctorPatientReceiptCollection extends GenericCollection{

	@Id
	private ObjectId id;
	
	@Field
	private String uniqueReceiptId;
	
	@Field
	private ReceiptType receiptType; 
	
	@Indexed
	private ObjectId doctorId;
	
	@Field
	private ObjectId locationId;
	
	@Field
	private ObjectId hospitalId;
	
	@Field
	private ObjectId patientId;
	
	@Field
	private ModeOfPayment modeOfPayment;
	
	@Field
	private List<ObjectId> invoiceIds;   
	
	@Field
	private Double amountPaid;
	
	@Field
	private Double remainingAdvanceAmount;
	
	@Field
	private Double balanceAmount;
	
	@Field
	private Date receivedDate;

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

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public ModeOfPayment getModeOfPayment() {
		return modeOfPayment;
	}

	public void setModeOfPayment(ModeOfPayment modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}

	public List<ObjectId> getInvoiceIds() {
		return invoiceIds;
	}

	public void setInvoiceIds(List<ObjectId> invoiceIds) {
		this.invoiceIds = invoiceIds;
	}

	public Double getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(Double amountPaid) {
		this.amountPaid = amountPaid;
	}

	public Double getRemainingAdvanceAmount() {
		return remainingAdvanceAmount;
	}

	public void setRemainingAdvanceAmount(Double remainingAdvanceAmount) {
		this.remainingAdvanceAmount = remainingAdvanceAmount;
	}

	public Double getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(Double balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	@Override
	public String toString() {
		return "DoctorPatientReceiptCollection [id=" + id + ", uniqueReceiptId=" + uniqueReceiptId + ", receiptType="
				+ receiptType + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", patientId=" + patientId + ", modeOfPayment=" + modeOfPayment + ", invoiceIds=" + invoiceIds
				+ ", amountPaid=" + amountPaid + ", remainingAdvanceAmount=" + remainingAdvanceAmount
				+ ", balanceAmount=" + balanceAmount + ", receivedDate=" + receivedDate + "]";
	}	
}
