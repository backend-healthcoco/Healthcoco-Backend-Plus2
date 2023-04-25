package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.Discount;
import com.dpdocter.beans.InvoiceItem;
import com.dpdocter.beans.Tax;
import com.dpdocter.collections.DoctorPatientReceiptCollection;
import com.dpdocter.collections.GenericCollection;

public class DoctorPatientInvoiceResponse extends GenericCollection {

	private String id;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private String uniqueInvoiceId;

	private List<InvoiceItem> invoiceItems;

	private List<ObjectId> receiptIds;

	private Discount totalDiscount;

	private double totalCost = 0.0;

	private Tax totalTax;

	private Double grandTotal = 0.0;

	private Double usedAdvanceAmount = 0.0;

	private Double refundAmount = 0.0;

	private Double balanceAmount = 0.0;

	private Boolean discarded = false;

	private DoctorPatientReceiptCollection receiptCollection;

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

	public Discount getTotalDiscount() {
		return totalDiscount;
	}

	public void setTotalDiscount(Discount totalDiscount) {
		this.totalDiscount = totalDiscount;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	public Tax getTotalTax() {
		return totalTax;
	}

	public void setTotalTax(Tax totalTax) {
		this.totalTax = totalTax;
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

	public DoctorPatientReceiptCollection getReceiptCollection() {
		return receiptCollection;
	}

	public void setReceiptCollection(DoctorPatientReceiptCollection receiptCollection) {
		this.receiptCollection = receiptCollection;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public List<InvoiceItem> getInvoiceItems() {
		return invoiceItems;
	}

	public void setInvoiceItems(List<InvoiceItem> invoiceItems) {
		this.invoiceItems = invoiceItems;
	}

	public List<ObjectId> getReceiptIds() {
		return receiptIds;
	}

	public void setReceiptIds(List<ObjectId> receiptIds) {
		this.receiptIds = receiptIds;
	}

	@Override
	public String toString() {
		return "DoctorPatientInvoiceResponse [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", uniqueInvoiceId=" + uniqueInvoiceId
				+ ", invoiceItems=" + invoiceItems + ", receiptIds=" + receiptIds + ", totalDiscount=" + totalDiscount
				+ ", totalCost=" + totalCost + ", totalTax=" + totalTax + ", grandTotal=" + grandTotal
				+ ", usedAdvanceAmount=" + usedAdvanceAmount + ", refundAmount=" + refundAmount + ", balanceAmount="
				+ balanceAmount + ", discarded=" + discarded + ", receiptCollection=" + receiptCollection
				+ ", invoiceDate=" + invoiceDate + "]";
	}

}
