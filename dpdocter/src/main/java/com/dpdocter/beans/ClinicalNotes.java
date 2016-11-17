package com.dpdocter.beans;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ClinicalNotes extends GenericCollection {

	private String id;

	private String uniqueEmrId;

	private List<Diagnoses> diagnoses;

	private List<Diagram> diagrams;

	private String note;

    private String observation;

    private String investigation;

	private String diagnosis;

	private String provisionalDiagnosis;

	private String generalExam;

	private String systemExam;

	private String complaint;

	private String presentComplaint;

	private String presentComplaintHistory;

	private String menstrualHistory;

	private String obstetricHistory;

	private String doctorId;

	private String locationId;

	private String hospitalId;

	private boolean inHistory = false;

	private Boolean discarded = false;

	private String visitId;

	private String patientId;

	private VitalSigns vitalSigns;

	private String appointmentId;

	private WorkingHours time;

	private Date fromDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Diagnoses> getDiagnoses() {
		return diagnoses;
	}

	public void setDiagnoses(List<Diagnoses> diagnoses) {
		this.diagnoses = diagnoses;
	}

	public List<Diagram> getDiagrams() {
		return diagrams;
	}

	public void setDiagrams(List<Diagram> diagrams) {
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

	public boolean isInHistory() {
		return inHistory;
	}

	public void setInHistory(boolean inHistory) {
		this.inHistory = inHistory;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getVisitId() {
		return visitId;
	}

	public void setVisitId(String visitId) {
		this.visitId = visitId;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public VitalSigns getVitalSigns() {
		return vitalSigns;
	}

	public void setVitalSigns(VitalSigns vitalSigns) {
		this.vitalSigns = vitalSigns;
	}

	public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getObservation() {
		return observation;
	}

	public void setObservation(String observation) {
		this.observation = observation;
	}

	public String getInvestigation() {
		return investigation;
	}

	public void setInvestigation(String investigation) {
		this.investigation = investigation;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public String getComplaint() {
		return complaint;
	}

	public void setComplaint(String complaint) {
		this.complaint = complaint;
	}

	public String getProvisionalDiagnosis() {
		return provisionalDiagnosis;
	}

	public void setProvisionalDiagnosis(String provisionalDiagnosis) {
		this.provisionalDiagnosis = provisionalDiagnosis;
	}

	public String getGeneralExam() {
		return generalExam;
	}

	public void setGeneralExam(String generalExam) {
		this.generalExam = generalExam;
	}

	public String getSystemExam() {
		return systemExam;
	}

	public void setSystemExam(String systemExam) {
		this.systemExam = systemExam;
	}

	public String getPresentComplaint() {
		return presentComplaint;
	}

	public void setPresentComplaint(String presentComplaint) {
		this.presentComplaint = presentComplaint;
	}

	public String getPresentComplaintHistory() {
		return presentComplaintHistory;
	}

	public void setPresentComplaintHistory(String presentComplaintHistory) {
		this.presentComplaintHistory = presentComplaintHistory;
	}

	public String getMenstrualHistory() {
		return menstrualHistory;
	}

	public void setMenstrualHistory(String menstrualHistory) {
		this.menstrualHistory = menstrualHistory;
	}

	public String getObstetricHistory() {
		return obstetricHistory;
	}

	public void setObstetricHistory(String obstetricHistory) {
		this.obstetricHistory = obstetricHistory;
	}

	@Override
	public String toString() {
		return "ClinicalNotes [id=" + id + ", uniqueEmrId=" + uniqueEmrId + ", diagnoses=" + diagnoses + ", diagrams="
				+ diagrams + ", note=" + note + ", observation=" + observation + ", investigation=" + investigation
				+ ", diagnosis=" + diagnosis + ", provisionalDiagnosis=" + provisionalDiagnosis + ", generalExam="
				+ generalExam + ", systemExam=" + systemExam + ", complaint=" + complaint + ", presentComplaint="
				+ presentComplaint + ", presentComplaintHistory=" + presentComplaintHistory + ", menstrualHistory="
				+ menstrualHistory + ", obstetricHistory=" + obstetricHistory + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", inHistory=" + inHistory
				+ ", discarded=" + discarded + ", visitId=" + visitId + ", patientId=" + patientId + ", vitalSigns="
				+ vitalSigns + ", appointmentId=" + appointmentId + ", time=" + time + ", fromDate=" + fromDate + "]";
	}
}
