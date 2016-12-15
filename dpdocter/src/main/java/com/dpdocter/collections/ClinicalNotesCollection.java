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

import com.dpdocter.beans.VitalSigns;
import com.dpdocter.beans.WorkingHours;

@Document(collection = "clinical_notes_cl")
@CompoundIndexes({ @CompoundIndex(def = "{'locationId' : 1, 'hospitalId': 1}") })
public class ClinicalNotesCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String uniqueEmrId;

	@Field
	private List<ObjectId> notes;

	@Field
	private List<ObjectId> observations;

	@Field
	private List<ObjectId> investigations;

	@Field
	private List<ObjectId> diagnoses;

	@Field
	private List<ObjectId> complaints;

	@Field
	private List<ObjectId> diagrams;

 	@Field
	private String note;

	@Field
	private String observation;

	@Field
	private String investigation;

	@Field
	private String diagnosis;

	@Field
	private String provisionalDiagnosis;

	@Field
	private String generalExam;

	@Field
	private String systemExam;

	@Field
	private String complaint;
	
	@Field
	private String presentComplaint;

	@Field
	private String presentComplaintHistory;

	@Field
	private String menstrualHistory;
	
	@Field
	private String obstetricHistory;
	
	@Field
	private String indicationOfUSG;
	
	@Field
	private String pv;
	
	@Field
	private String pa;
	
	@Field
	private String ps;

	@Field
	private List<String> comments;

	@Indexed
	private ObjectId doctorId;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Indexed
	private ObjectId patientId;

	@Field
	private Boolean discarded = false;

	@Field
	private boolean inHistory = false;

	@Field
	private VitalSigns vitalSigns;

	@Field
	private String appointmentId;

	@Field
	private WorkingHours time;

	@Field
	private Date fromDate;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<ObjectId> getNotes() {
		return notes;
	}

	public void setNotes(List<ObjectId> notes) {
		this.notes = notes;
	}

	public List<ObjectId> getObservations() {
		return observations;
	}

	public void setObservations(List<ObjectId> observations) {
		this.observations = observations;
	}

	public List<ObjectId> getInvestigations() {
		return investigations;
	}

	public void setInvestigations(List<ObjectId> investigations) {
		this.investigations = investigations;
	}

	public List<ObjectId> getDiagnoses() {
		return diagnoses;
	}

	public void setDiagnoses(List<ObjectId> diagnoses) {
		this.diagnoses = diagnoses;
	}

	public List<ObjectId> getComplaints() {
		return complaints;
	}

	public void setComplaints(List<ObjectId> complaints) {
		this.complaints = complaints;
	}

	public List<ObjectId> getDiagrams() {
		return diagrams;
	}

	public void setDiagrams(List<ObjectId> diagrams) {
		this.diagrams = diagrams;
	}

	public List<String> getComments() {
		return comments;
	}

	public void setComments(List<String> comments) {
		this.comments = comments;
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public boolean isInHistory() {
		return inHistory;
	}

	public void setInHistory(boolean inHistory) {
		this.inHistory = inHistory;
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

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
		this.patientId = patientId;
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

	public String getIndicationOfUSG() {
		return indicationOfUSG;
	}

	public void setIndicationOfUSG(String indicationOfUSG) {
		this.indicationOfUSG = indicationOfUSG;
	}

	public String getPv() {
		return pv;
	}

	public void setPv(String pv) {
		this.pv = pv;
	}

	public String getPa() {
		return pa;
	}

	public void setPa(String pa) {
		this.pa = pa;
	}

	public String getPs() {
		return ps;
	}

	public void setPs(String ps) {
		this.ps = ps;
	}

	@Override
	public String toString() {
		return "ClinicalNotesCollection [id=" + id + ", uniqueEmrId=" + uniqueEmrId + ", notes=" + notes
				+ ", observations=" + observations + ", investigations=" + investigations + ", diagnoses=" + diagnoses
				+ ", complaints=" + complaints + ", diagrams=" + diagrams + ", note=" + note + ", observation="
				+ observation + ", investigation=" + investigation + ", diagnosis=" + diagnosis
				+ ", provisionalDiagnosis=" + provisionalDiagnosis + ", generalExam=" + generalExam + ", systemExam="
				+ systemExam + ", complaint=" + complaint + ", presentComplaint=" + presentComplaint
				+ ", presentComplaintHistory=" + presentComplaintHistory + ", menstrualHistory=" + menstrualHistory
				+ ", obstetricHistory=" + obstetricHistory + ", indicationOfUSG=" + indicationOfUSG + ", pv=" + pv
				+ ", pa=" + pa + ", ps=" + ps + ", comments=" + comments + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", discarded=" + discarded
				+ ", inHistory=" + inHistory + ", vitalSigns=" + vitalSigns + ", appointmentId=" + appointmentId
				+ ", time=" + time + ", fromDate=" + fromDate + "]";
	}
}
