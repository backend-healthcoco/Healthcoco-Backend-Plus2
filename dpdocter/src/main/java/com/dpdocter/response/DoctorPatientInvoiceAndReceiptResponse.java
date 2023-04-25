package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.DoctorPatientReceipt;
import com.dpdocter.collections.GenericCollection;

public class DoctorPatientInvoiceAndReceiptResponse extends GenericCollection {

	private String id;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private String uniqueInvoiceId;

	private List<InvoiceItemResponse> invoiceItems;

	private Double grandTotal = 0.0;

	private Double usedAdvanceAmount = 0.0;

	private Double refundAmount = 0.0;

	private Double balanceAmount = 0.0;

	private Boolean discarded = false;

	DoctorPatientReceipt doctorPatientReceipt;

	private Date invoiceDate;

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

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getUniqueInvoiceId() {
		return uniqueInvoiceId;
	}

	public void setUniqueInvoiceId(String uniqueInvoiceId) {
		this.uniqueInvoiceId = uniqueInvoiceId;
	}

	public List<InvoiceItemResponse> getInvoiceItems() {
		return invoiceItems;
	}

	public void setInvoiceItems(List<InvoiceItemResponse> invoiceItems) {
		this.invoiceItems = invoiceItems;
	}

	public Double getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(Double grandTotal) {
		this.grandTotal = grandTotal;
	}

	public Double getUsedAdvanceAmount() {
		return usedAdvanceAmount;
	}

	public void setUsedAdvanceAmount(Double usedAdvanceAmount) {
		this.usedAdvanceAmount = usedAdvanceAmount;
	}

	public Double getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(Double refundAmount) {
		this.refundAmount = refundAmount;
	}

	public Double getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(Double balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public DoctorPatientReceipt getDoctorPatientReceipt() {
		return doctorPatientReceipt;
	}

	public void setDoctorPatientReceipt(DoctorPatientReceipt doctorPatientReceipt) {
		this.doctorPatientReceipt = doctorPatientReceipt;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	@Override
	public String toString() {
		return "DoctorPatientInvoiceAndReceiptResponse [id=" + id + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", uniqueInvoiceId="
				+ uniqueInvoiceId + ", invoiceItems=" + invoiceItems + ", grandTotal=" + grandTotal
				+ ", usedAdvanceAmount=" + usedAdvanceAmount + ", refundAmount=" + refundAmount + ", balanceAmount="
				+ balanceAmount + ", discarded=" + discarded + ", doctorPatientReceipt=" + doctorPatientReceipt
				+ ", invoiceDate=" + invoiceDate + "]";
	}
}
