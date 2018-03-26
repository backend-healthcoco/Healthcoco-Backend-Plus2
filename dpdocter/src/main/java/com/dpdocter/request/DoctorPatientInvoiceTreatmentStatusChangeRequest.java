package com.dpdocter.request;

import java.util.List;

public class DoctorPatientInvoiceTreatmentStatusChangeRequest {

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private String uniqueInvoiceId;

	private List<InvoiceItemChangeStatusRequest> invoiceItemChangeStatusRequests;

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

	public List<InvoiceItemChangeStatusRequest> getInvoiceItemChangeStatusRequests() {
		return invoiceItemChangeStatusRequests;
	}

	public void setInvoiceItemChangeStatusRequests(
			List<InvoiceItemChangeStatusRequest> invoiceItemChangeStatusRequests) {
		this.invoiceItemChangeStatusRequests = invoiceItemChangeStatusRequests;
	}

	@Override
	public String toString() {
		return "DoctorPatientInvoiceTreatmentStatusChangeRequest [doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", uniqueInvoiceId=" + uniqueInvoiceId
				+ ", invoiceItemChangeStatusRequests=" + invoiceItemChangeStatusRequests + "]";
	}

}
