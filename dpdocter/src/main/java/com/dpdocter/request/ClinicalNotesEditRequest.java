package com.dpdocter.request;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.ClinicalNotesComplaint;
import com.dpdocter.beans.ClinicalNotesDiagnosis;
import com.dpdocter.beans.ClinicalNotesInvestigation;
import com.dpdocter.beans.ClinicalNotesNote;
import com.dpdocter.beans.ClinicalNotesObservation;
import com.dpdocter.beans.VitalSigns;
import com.dpdocter.beans.WorkingHours;

public class ClinicalNotesEditRequest {
    private String id;

    private String patientId;

    private List<ClinicalNotesComplaint> complaints;

    private List<ClinicalNotesObservation> observations;

    private List<ClinicalNotesInvestigation> investigations;

    private List<ClinicalNotesDiagnosis> diagnoses;

    private List<ClinicalNotesNote> notes;

    private List<String> diagrams;

    private String doctorId;

    private String locationId;

    private String hospitalId;

    private String visitId;

    private VitalSigns vitalSigns;
    
    private AppointmentRequest appointmentRequest;

	private String appointmentId;

	private WorkingHours time;

	private Date fromDate;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getPatientId() {
	return patientId;
    }

    public void setPatientId(String patientId) {
	this.patientId = patientId;
    }

    public List<ClinicalNotesComplaint> getComplaints() {
	return complaints;
    }

    public void setComplaints(List<ClinicalNotesComplaint> complaints) {
	this.complaints = complaints;
    }

    public List<ClinicalNotesObservation> getObservations() {
	return observations;
    }

    public void setObservations(List<ClinicalNotesObservation> observations) {
	this.observations = observations;
    }

    public List<ClinicalNotesInvestigation> getInvestigations() {
	return investigations;
    }

    public void setInvestigations(List<ClinicalNotesInvestigation> investigations) {
	this.investigations = investigations;
    }

    public List<ClinicalNotesDiagnosis> getDiagnoses() {
	return diagnoses;
    }

    public void setDiagnoses(List<ClinicalNotesDiagnosis> diagnoses) {
	this.diagnoses = diagnoses;
    }

    public List<ClinicalNotesNote> getNotes() {
	return notes;
    }

    public void setNotes(List<ClinicalNotesNote> notes) {
	this.notes = notes;
    }

    public List<String> getDiagrams() {
	return diagrams;
    }

    public void setDiagrams(List<String> diagrams) {
	this.diagrams = diagrams;
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

    public String getVisitId() {
	return visitId;
    }

    public void setVisitId(String visitId) {
	this.visitId = visitId;
    }

    public VitalSigns getVitalSigns() {
	return vitalSigns;
    }

    public void setVitalSigns(VitalSigns vitalSigns) {
	this.vitalSigns = vitalSigns;
    }

	public AppointmentRequest getAppointmentRequest() {
		return appointmentRequest;
	}

	public void setAppointmentRequest(AppointmentRequest appointmentRequest) {
		this.appointmentRequest = appointmentRequest;
	}

	public String getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}

	public WorkingHours getTime() {
		return time;
	}

	public void setTime(WorkingHours time) {
		this.time = time;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	@Override
	public String toString() {
		return "ClinicalNotesEditRequest [id=" + id + ", patientId=" + patientId + ", complaints=" + complaints
				+ ", observations=" + observations + ", investigations=" + investigations + ", diagnoses=" + diagnoses
				+ ", notes=" + notes + ", diagrams=" + diagrams + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", visitId=" + visitId + ", vitalSigns=" + vitalSigns
				+ ", appointmentRequest=" + appointmentRequest + ", appointmentId=" + appointmentId + ", time=" + time
				+ ", fromDate=" + fromDate + "]";
	}
	
	
}
