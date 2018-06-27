package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "dental_works_ledger_cl")
public class DentalWorksLedgerCollection extends GenericCollection{

	private String id;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String dentalLabLocationId;

	private String dentalLabHospitalId;

	private String invoiceId;

	private Double dueAmount;

	private Double paidAmount;

	private List<ObjectId> requestIds;

	private Boolean discarded = false;

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

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public Double getDueAmount() {
		return dueAmount;
	}

	public void setDueAmount(Double dueAmount) {
		this.dueAmount = dueAmount;
	}

	public Double getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(Double paidAmount) {
		this.paidAmount = paidAmount;
	}

	public List<ObjectId> getRequestIds() {
		return requestIds;
	}

	public void setRequestIds(List<ObjectId> requestIds) {
		this.requestIds = requestIds;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}
	
}
