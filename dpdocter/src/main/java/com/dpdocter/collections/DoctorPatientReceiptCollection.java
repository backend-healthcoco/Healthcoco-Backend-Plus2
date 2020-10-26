package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.AdvanceReceiptIdWithAmount;
import com.dpdocter.enums.ModeOfPayment;
import com.dpdocter.enums.ReceiptType;
import com.dpdocter.request.PaymentDetails;

@Document(collection = "doctor_patient_receipt_cl")
public class DoctorPatientReceiptCollection extends GenericCollection {

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
	private PaymentDetails paymentDetails;
	
	@Field
	private String transactionId;

	@Field
	private List<AdvanceReceiptIdWithAmount> advanceReceiptIdWithAmounts; // For
																			// ReceiptType=ADVANCE
	@Field
	private ObjectId invoiceId; // For ReceiptType=INVOICE

	@Field
	private String uniqueInvoiceId;

	@Field
	private Double amountPaid = 0.0;

	@Field
	private Double remainingAdvanceAmount = 0.0;

	@Field
	private Double balanceAmount = 0.0;

	@Field
	private Date receivedDate = new Date();

	@Field
	private Boolean discarded = false;

	@Field
	private Double usedAdvanceAmount = 0.0;

	@Field
	private Boolean isPatientDiscarded = false;
	
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

	public List<AdvanceReceiptIdWithAmount> getAdvanceReceiptIdWithAmounts() {
		return advanceReceiptIdWithAmounts;
	}

	public void setAdvanceReceiptIdWithAmounts(List<AdvanceReceiptIdWithAmount> advanceReceiptIdWithAmounts) {
		this.advanceReceiptIdWithAmounts = advanceReceiptIdWithAmounts;
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public ObjectId getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(ObjectId invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getUniqueInvoiceId() {
		return uniqueInvoiceId;
	}

	public Double getUsedAdvanceAmount() {
		return usedAdvanceAmount;
	}

	public void setUsedAdvanceAmount(Double usedAdvanceAmount) {
		this.usedAdvanceAmount = usedAdvanceAmount;
	}

	public void setUniqueInvoiceId(String uniqueInvoiceId) {
		this.uniqueInvoiceId = uniqueInvoiceId;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}
	
	

	public PaymentDetails getPaymentDetails() {
		return paymentDetails;
	}

	public void setPaymentDetails(PaymentDetails paymentDetails) {
		this.paymentDetails = paymentDetails;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	@Override
	public String toString() {
		return "DoctorPatientReceiptCollection [id=" + id + ", uniqueReceiptId=" + uniqueReceiptId + ", receiptType="
				+ receiptType + ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", patientId=" + patientId + ", modeOfPayment=" + modeOfPayment + ", advanceReceiptIdWithAmounts="
				+ advanceReceiptIdWithAmounts + ", invoiceId=" + invoiceId + ", uniqueInvoiceId=" + uniqueInvoiceId
				+ ", amountPaid=" + amountPaid + ", remainingAdvanceAmount=" + remainingAdvanceAmount
				+ ", balanceAmount=" + balanceAmount + ", receivedDate=" + receivedDate + ", discarded=" + discarded
				+ ", usedAdvanceAmount=" + usedAdvanceAmount + ", isPatientDiscarded=" + isPatientDiscarded + "]";
	}
}
