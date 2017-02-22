package com.dpdocter.response;

import com.dpdocter.beans.WorkingHours;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.UserCollection;

public class AppointmentDoctorReminderResponse {

	private String doctorId;
	
	private int total;

	private String patientId;
	
	private WorkingHours time;
	
	private UserCollection doctor;
	
	private PatientCollection patient;
	
	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public WorkingHours getTime() {
		return time;
	}

	public void setTime(WorkingHours time) {
		this.time = time;
	}

	public UserCollection getDoctor() {
		return doctor;
	}

	public void setDoctor(UserCollection doctor) {
		this.doctor = doctor;
	}

	public PatientCollection getPatient() {
		return patient;
	}

	public void setPatient(PatientCollection patient) {
		this.patient = patient;
	}

	@Override
	public String toString() {
		return "AppointmentDoctorReminderResponse [doctorId=" + doctorId + ", total=" + total + ", patientId="
				+ patientId + ", time=" + time + ", doctor=" + doctor + ", patient=" + patient + "]";
	}
}
