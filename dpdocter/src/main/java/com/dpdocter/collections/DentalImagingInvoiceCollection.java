package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.DentalImagingInvoiceItem;
import com.dpdocter.beans.Discount;
import com.dpdocter.beans.InvoiceTax;
import com.dpdocter.beans.Tax;
@Document(collection = "dental_imaging_invoice_cl")
public class DentalImagingInvoiceCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private ObjectId dentalImagingId;

	@Field
	private ObjectId doctorId;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Field
	private ObjectId patientId;

	@Field
	private String patientName;

	@Field
	private String mobileNumber;
	
	@Field
	private ObjectId dentalImagingDoctorId;	

	@Field
	private ObjectId dentalImagingLocationId;

	@Field
	private ObjectId dentalImagingHospitalId;

	@Field
	private String uniqueInvoiceId;

	@Field
	private Discount totalDiscount;

	@Field
	private Double totalCost = 0.0;

	@Field
	private Tax totalTax;

	@Field
	private List<InvoiceTax> invoiceTaxes;

	@Field
	private Double grandTotal = 0.0;

	@Field
	private Double oldGrantTotal = this.grandTotal;

	@Field
	private Double usedAdvanceAmount = 0.0;

	@Field
	private Double refundAmount = 0.0;

	@Field
	private Double balanceAmount = 0.0;

	@Field
	private Boolean discarded = false;

	@Field
	private Boolean isTaxNotApplicable = false;

	@Field
	private Date invoiceDate;

	@Field
	private List<DentalImagingInvoiceItem> invoiceItems;

	@Field
	private Boolean isPaid = false;
	
	@Field
	private String referringDoctor;

	@Field
	private Boolean isPatientDiscarded = false;
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getDentalImagingId() {
		return dentalImagingId;
	}

	public void setDentalImagingId(ObjectId dentalImagingId) {
		this.dentalImagingId = dentalImagingId;
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

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public ObjectId getDentalImagingLocationId() {
		return dentalImagingLocationId;
	}

	public void setDentalImagingLocationId(ObjectId dentalImagingLocationId) {
		this.dentalImagingLocationId = dentalImagingLocationId;
	}

	public ObjectId getDentalImagingHospitalId() {
		return dentalImagingHospitalId;
	}

	public void setDentalImagingHospitalId(ObjectId dentalImagingHospitalId) {
		this.dentalImagingHospitalId = dentalImagingHospitalId;
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

	public List<InvoiceTax> getInvoiceTaxes() {
		return invoiceTaxes;
	}

	public void setInvoiceTaxes(List<InvoiceTax> invoiceTaxes) {
		this.invoiceTaxes = invoiceTaxes;
	}

	public Double getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(Double grandTotal) {
		this.grandTotal = grandTotal;
	}

	public Double getOldGrantTotal() {
		return oldGrantTotal;
	}

	public void setOldGrantTotal(Double oldGrantTotal) {
		this.oldGrantTotal = oldGrantTotal;
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

	public Boolean getIsTaxNotApplicable() {
		return isTaxNotApplicable;
	}

	public void setIsTaxNotApplicable(Boolean isTaxNotApplicable) {
		this.isTaxNotApplicable = isTaxNotApplicable;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public List<DentalImagingInvoiceItem> getInvoiceItems() {
		return invoiceItems;
	}

	public void setInvoiceItems(List<DentalImagingInvoiceItem> invoiceItems) {
		this.invoiceItems = invoiceItems;
	}

	public Boolean getIsPaid() {
		return isPaid;
	}

	public ObjectId getDentalImagingDoctorId() {
		return dentalImagingDoctorId;
	}

	public void setDentalImagingDoctorId(ObjectId dentalImagingDoctorId) {
		this.dentalImagingDoctorId = dentalImagingDoctorId;
	}

	public String getReferringDoctor() {
		return referringDoctor;
	}

	public void setReferringDoctor(String referringDoctor) {
		this.referringDoctor = referringDoctor;
	}

	public void setIsPaid(Boolean isPaid) {
		this.isPaid = isPaid;
	}

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
	}

	@Override
	public String toString() {
		return "DentalImagingInvoiceCollection [id=" + id + ", dentalImagingId=" + dentalImagingId + ", doctorId="
				+ doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", patientId=" + patientId
				+ ", patientName=" + patientName + ", mobileNumber=" + mobileNumber + ", dentalImagingDoctorId="
				+ dentalImagingDoctorId + ", dentalImagingLocationId=" + dentalImagingLocationId
				+ ", dentalImagingHospitalId=" + dentalImagingHospitalId + ", uniqueInvoiceId=" + uniqueInvoiceId
				+ ", totalDiscount=" + totalDiscount + ", totalCost=" + totalCost + ", totalTax=" + totalTax
				+ ", invoiceTaxes=" + invoiceTaxes + ", grandTotal=" + grandTotal + ", oldGrantTotal=" + oldGrantTotal
				+ ", usedAdvanceAmount=" + usedAdvanceAmount + ", refundAmount=" + refundAmount + ", balanceAmount="
				+ balanceAmount + ", discarded=" + discarded + ", isTaxNotApplicable=" + isTaxNotApplicable
				+ ", invoiceDate=" + invoiceDate + ", invoiceItems=" + invoiceItems + ", isPaid=" + isPaid
				+ ", referringDoctor=" + referringDoctor + ", isPatientDiscarded=" + isPatientDiscarded + "]";
	}
}
