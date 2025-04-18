package com.dpdocter.request;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.DecimalMin;

import com.dpdocter.enums.ModeOfPayment;
import com.dpdocter.enums.ReceiptType;

public class DoctorPatientReceiptRequest {

	private String id;

	private String uniqueReceiptId;

	private ReceiptType receiptType;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private ModeOfPayment modeOfPayment;

	private String paymentInfo;

	private PaymentDetails paymentDetails;

	private String transactionId;

	private List<String> invoiceIds;
	
    @DecimalMin(value = "0.0", inclusive = true, message = "Amount paid must be zero or positive")
	private Double amountPaid = 0.0;

	private Double usedAdvanceAmount = 0.0;

	private Date receivedDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public ModeOfPayment getModeOfPayment() {
		return modeOfPayment;
	}

	public void setModeOfPayment(ModeOfPayment modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}

	public List<String> getInvoiceIds() {
		return invoiceIds;
	}

	public void setInvoiceIds(List<String> invoiceIds) {
		this.invoiceIds = invoiceIds;
	}

	public Double getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(Double amountPaid) {
		this.amountPaid = amountPaid;
	}

	public Double getUsedAdvanceAmount() {
		return usedAdvanceAmount;
	}

	public void setUsedAdvanceAmount(Double usedAdvanceAmount) {
		this.usedAdvanceAmount = usedAdvanceAmount;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
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

	public String getPaymentInfo() {
		return paymentInfo;
	}

	public void setPaymentInfo(String paymentInfo) {
		this.paymentInfo = paymentInfo;
	}

	@Override
	public String toString() {
		return "DoctorPatientReceiptRequest [uniqueReceiptId=" + uniqueReceiptId + ", receiptType=" + receiptType
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", patientId=" + patientId + ", modeOfPayment=" + modeOfPayment + ", invoiceIds=" + invoiceIds
				+ ", amountPaid=" + amountPaid + ", usedAdvanceAmount=" + usedAdvanceAmount + ", receivedDate="
				+ receivedDate + "]";
	}
}
