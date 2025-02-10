package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Discount;
import com.dpdocter.beans.InvoiceItem;
import com.dpdocter.beans.Tax;

@Document(collection = "doctor_patient_invoice_cl")
@CompoundIndexes({ @CompoundIndex(def = "{'locationId' : 1, 'hospitalId': 1}") })
public class DoctorPatientInvoiceCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Indexed
	private ObjectId doctorId;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Field
	private ObjectId patientId;

	@Field
	private String uniqueInvoiceId;

	@Field
	private List<ObjectId> receiptIds;

	@Field
	private List<InvoiceItem> invoiceItems;

	@Field
	private Discount totalDiscount;

	@Field
	private Double totalCost = 0.0;

	@Field
	private Tax totalTax;

	@Field
	private Double grandTotal = 0.0;

	@Field
	private Double usedAdvanceAmount = 0.0;

	@Field
	private Double refundAmount = 0.0;

	@Field
	private Double balanceAmount = 0.0;

	@Field
	private Boolean discarded = false;

	@Field
	private Date invoiceDate;

	@Field
	private Boolean isPatientDiscarded = false;

	@Field
	private String timeOfAdmission;
	@Field
	private String timeOfDischarge;
	@Field
	private Date admissionDate;
	@Field
	private Date dischargeDate;
	@Field
	private Boolean isCghsInvoice = false;
	@Field
	private Discount discount;

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

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
	}

	public String getUniqueInvoiceId() {
		return uniqueInvoiceId;
	}

	public void setUniqueInvoiceId(String uniqueInvoiceId) {
		this.uniqueInvoiceId = uniqueInvoiceId;
	}

	public List<InvoiceItem> getInvoiceItems() {
		return invoiceItems;
	}

	public void setInvoiceItems(List<InvoiceItem> invoiceItems) {
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

	public List<ObjectId> getReceiptIds() {
		return receiptIds;
	}

	public void setReceiptIds(List<ObjectId> receiptIds) {
		this.receiptIds = receiptIds;
	}

	public Discount getTotalDiscount() {
		return totalDiscount;
	}

	public void setTotalDiscount(Discount totalDiscount) {
		this.totalDiscount = totalDiscount;
	}

	public Double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}

	public Tax getTotalTax() {
		return totalTax;
	}

	public void setTotalTax(Tax totalTax) {
		this.totalTax = totalTax;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	public String getTimeOfAdmission() {
		return timeOfAdmission;
	}

	public void setTimeOfAdmission(String timeOfAdmission) {
		this.timeOfAdmission = timeOfAdmission;
	}

	public String getTimeOfDischarge() {
		return timeOfDischarge;
	}

	public void setTimeOfDischarge(String timeOfDischarge) {
		this.timeOfDischarge = timeOfDischarge;
	}

	public Date getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}

	public Date getDischargeDate() {
		return dischargeDate;
	}

	public void setDischargeDate(Date dischargeDate) {
		this.dischargeDate = dischargeDate;
	}

	public Boolean getIsCghsInvoice() {
		return isCghsInvoice;
	}

	public void setIsCghsInvoice(Boolean isCghsInvoice) {
		this.isCghsInvoice = isCghsInvoice;
	}

	public Discount getDiscount() {
		return discount;
	}

	public void setDiscount(Discount discount) {
		this.discount = discount;
	}

	@Override
	public String toString() {
		return "DoctorPatientInvoiceCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", uniqueInvoiceId=" + uniqueInvoiceId
				+ ", receiptIds=" + receiptIds + ", invoiceItems=" + invoiceItems + ", totalDiscount=" + totalDiscount
				+ ", totalCost=" + totalCost + ", totalTax=" + totalTax + ", grandTotal=" + grandTotal
				+ ", usedAdvanceAmount=" + usedAdvanceAmount + ", refundAmount=" + refundAmount + ", balanceAmount="
				+ balanceAmount + ", discarded=" + discarded + ", invoiceDate=" + invoiceDate + ", isPatientDiscarded="
				+ isPatientDiscarded + "]";
	}

}
