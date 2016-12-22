package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.InvoiceItems;

@Document(collection = "doctor_patient_invoice_cl")
@CompoundIndexes({ @CompoundIndex(def = "{'locationId' : 1, 'hospitalId': 1}") })
public class DoctorPatientInvoiceCollection extends GenericCollection{

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
	private List<InvoiceItems> invoiceItems;   
	
	@Field
	private Double grandTotal;
	
	@Field
	private Double usedAdvanceAmount;
	
	@Field
	private Double refundAmount;
	
	@Field
	private Double balanceAmount;

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

	public List<InvoiceItems> getInvoiceItems() {
		return invoiceItems;
	}

	public void setInvoiceItems(List<InvoiceItems> invoiceItems) {
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

	@Override
	public String toString() {
		return "DoctorPatientInvoiceCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", uniqueInvoiceId=" + uniqueInvoiceId
				+ ", invoiceItems=" + invoiceItems + ", grandTotal=" + grandTotal + ", usedAdvanceAmount="
				+ usedAdvanceAmount + ", refundAmount=" + refundAmount + ", balanceAmount=" + balanceAmount + "]";
	}
}
