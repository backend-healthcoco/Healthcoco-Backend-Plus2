package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.Prescription;
import com.dpdocter.collections.GenericCollection;

public class PrescriptionGetResponse extends GenericCollection {

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private List<Prescription> prescriptions;

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

	public List<Prescription> getPrescriptions() {
		return prescriptions;
	}

	public void setPrescriptions(List<Prescription> prescriptions) {
		this.prescriptions = prescriptions;
	}

	@Override
	public String toString() {
		return "PrescriptionGetResponse [doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", patientId=" + patientId + ", prescriptions=" + prescriptions + "]";
	}

}
