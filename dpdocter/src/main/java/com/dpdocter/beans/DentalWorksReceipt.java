package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

import com.dpdocter.enums.ModeOfPayment;
import com.dpdocter.enums.ReceiptType;
import com.dpdocter.response.AdvanceReceiptIdWithAmountResponse;

public class DentalWorksReceipt {

	private String id;

	private String uniqueReceiptId;

	private ReceiptType receiptType;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String dentalLabLocationId;

	private String dentalLabHospitalId;

	private ModeOfPayment modeOfPayment;

	private List<AdvanceReceiptIdWithAmountResponse> advanceReceiptIdWithAmounts;

	private String invoiceId;

	private String uniqueInvoiceId;

	private Double amountPaid = 0.0;

	private Double remainingAdvanceAmount = 0.0;

	private Double balanceAmount = 0.0;

	private Date receivedDate;

	private Boolean discarded = false;

	private Double usedAdvanceAmount = 0.0;

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

	public String getDentalLabLocationId() {
		return dentalLabLocationId;
	}

	public void setDentalLabLocationId(String dentalLabLocationId) {
		this.dentalLabLocationId = dentalLabLocationId;
	}

	public String getDentalLabHospitalId() {
		return dentalLabHospitalId;
	}

	public void setDentalLabHospitalId(String dentalLabHospitalId) {
		this.dentalLabHospitalId = dentalLabHospitalId;
	}

	public ModeOfPayment getModeOfPayment() {
		return modeOfPayment;
	}

	public void setModeOfPayment(ModeOfPayment modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}

	public List<AdvanceReceiptIdWithAmountResponse> getAdvanceReceiptIdWithAmounts() {
		return advanceReceiptIdWithAmounts;
	}

	public void setAdvanceReceiptIdWithAmounts(List<AdvanceReceiptIdWithAmountResponse> advanceReceiptIdWithAmounts) {
		this.advanceReceiptIdWithAmounts = advanceReceiptIdWithAmounts;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getUniqueInvoiceId() {
		return uniqueInvoiceId;
	}

	public void setUniqueInvoiceId(String uniqueInvoiceId) {
		this.uniqueInvoiceId = uniqueInvoiceId;
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

	public Double getUsedAdvanceAmount() {
		return usedAdvanceAmount;
	}

	public void setUsedAdvanceAmount(Double usedAdvanceAmount) {
		this.usedAdvanceAmount = usedAdvanceAmount;
	}

	@Override
	public String toString() {
		return "DentalWorksReceipts [id=" + id + ", uniqueReceiptId=" + uniqueReceiptId + ", receiptType=" + receiptType
				+ ", doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", dentalLabLocationId=" + dentalLabLocationId + ", dentalLabHospitalId=" + dentalLabHospitalId
				+ ", modeOfPayment=" + modeOfPayment + ", advanceReceiptIdWithAmounts=" + advanceReceiptIdWithAmounts
				+ ", invoiceId=" + invoiceId + ", uniqueInvoiceId=" + uniqueInvoiceId + ", amountPaid=" + amountPaid
				+ ", remainingAdvanceAmount=" + remainingAdvanceAmount + ", balanceAmount=" + balanceAmount
				+ ", receivedDate=" + receivedDate + ", discarded=" + discarded + ", usedAdvanceAmount="
				+ usedAdvanceAmount + "]";
	}

}
