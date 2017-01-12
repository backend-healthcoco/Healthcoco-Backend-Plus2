package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "doctor_patient_ledger_cl")
@CompoundIndexes({ @CompoundIndex(def = "{'locationId' : 1, 'hospitalId': 1}") })
public class DoctorPatientLedgerCollection extends GenericCollection{

	@Id
	private ObjectId id;
		
	@Field
	private ObjectId locationId;
	
	@Field
	private ObjectId hospitalId;
	
	@Field
	private ObjectId patientId;
	
	@Field
	private ObjectId receiptId;
	
	@Field
	private ObjectId invoiceId;   
	
	@Field
	private Double balanceAmount;

	@Field
	private Double creditAmount;
	
	@Field
	private Double debitAmount;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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

	public ObjectId getReceiptId() {
		return receiptId;
	}

	public void setReceiptId(ObjectId receiptId) {
		this.receiptId = receiptId;
	}

	public ObjectId getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(ObjectId invoiceId) {
		this.invoiceId = invoiceId;
	}

	public Double getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(Double balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public Double getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(Double creditAmount) {
		this.creditAmount = creditAmount;
	}

	public Double getDebitAmount() {
		return debitAmount;
	}

	public void setDebitAmount(Double debitAmount) {
		this.debitAmount = debitAmount;
	}

	@Override
	public String toString() {
		return "DoctorPatientLedgerCollection [id=" + id + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", patientId=" + patientId + ", receiptId=" + receiptId + ", invoiceId=" + invoiceId
				+ ", balanceAmount=" + balanceAmount + ", creditAmount=" + creditAmount + ", debitAmount=" + debitAmount
				+ "]";
	}
}
