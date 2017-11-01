package com.dpdocter.request;

import java.util.Date;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.beans.PrescriptionItemAndAdviceAdd;
import com.dpdocter.beans.VitalSigns;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.collections.GenericCollection;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DischargeSummaryRequest extends GenericCollection {
	private String id;
	private String patientId;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private PrescriptionItemAndAdviceAdd prescriptions;
	private String diagnosis;
	private String pastHistory;
	private String familyHistory;
	private String personalHistory;
	private String complaint;
	private String presentComplaint;
	private String presentComplaintHistory;
	private String menstrualHistory;
	private String obstetricHistory;
	private String generalExam;
	private String systemExam;
	private String observation;
	private String investigation;
	private String pa;
	private String ps;
	private String pv;
	private String echo;
	private String xRayDetails;
	private String operationNotes;
	private String treatmentsGiven;
	private AppointmentRequest appointmentRequest;
	private String uniqueEmrId;
	private Date admissionDate;
	private Date dischargeDate;
	private String labourNotes;
	private String babyWeight;
	private String babyNotes;
	private String conditionsAtDischarge;
	private String summary;
	private Boolean discarded = false;
	private String indicationOfUSG;
	private String ecgDetails;
	private String holter;
	private WorkingHours time;
	private Date fromDate;
	private String procedureNote;
	private String doctorIncharge;
	private VitalSigns vitalSigns;

	public VitalSigns getVitalSigns() {
		return vitalSigns;
	}

	public void setVitalSigns(VitalSigns vitalSigns) {
		this.vitalSigns = vitalSigns;
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

	public PrescriptionItemAndAdviceAdd getPrescriptions() {
		return prescriptions;
	}

	public void setPrescriptions(PrescriptionItemAndAdviceAdd prescriptions) {
		this.prescriptions = prescriptions;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public String getPastHistory() {
		return pastHistory;
	}

	public void setPastHistory(String pastHistory) {
		this.pastHistory = pastHistory;
	}

	public String getFamilyHistory() {
		return familyHistory;
	}

	public void setFamilyHistory(String familyHistory) {
		this.familyHistory = familyHistory;
	}

	public String getPersonalHistory() {
		return personalHistory;
	}

	public void setPersonalHistory(String personalHistory) {
		this.personalHistory = personalHistory;
	}

	public String getComplaint() {
		return complaint;
	}

	public void setComplaint(String complaint) {
		this.complaint = complaint;
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

	public String getPv() {
		return pv;
	}

	public void setPv(String pv) {
		this.pv = pv;
	}

	public String getEcho() {
		return echo;
	}

	public void setEcho(String echo) {
		this.echo = echo;
	}

	public String getxRayDetails() {
		return xRayDetails;
	}

	public void setxRayDetails(String xRayDetails) {
		this.xRayDetails = xRayDetails;
	}

	public String getOperationNotes() {
		return operationNotes;
	}

	public void setOperationNotes(String operationNotes) {
		this.operationNotes = operationNotes;
	}

	public String getTreatmentsGiven() {
		return treatmentsGiven;
	}

	public void setTreatmentsGiven(String treatmentsGiven) {
		this.treatmentsGiven = treatmentsGiven;
	}

	public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
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

	public String getLabourNotes() {
		return labourNotes;
	}

	public void setLabourNotes(String labourNotes) {
		this.labourNotes = labourNotes;
	}

	public String getBabyWeight() {
		return babyWeight;
	}

	public void setBabyWeight(String babyWeight) {
		this.babyWeight = babyWeight;
	}

	public String getBabyNotes() {
		return babyNotes;
	}

	public void setBabyNotes(String babyNotes) {
		this.babyNotes = babyNotes;
	}

	public String getConditionsAtDischarge() {
		return conditionsAtDischarge;
	}

	public void setConditionsAtDischarge(String conditionsAtDischarge) {
		this.conditionsAtDischarge = conditionsAtDischarge;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getIndicationOfUSG() {
		return indicationOfUSG;
	}

	public void setIndicationOfUSG(String indicationOfUSG) {
		this.indicationOfUSG = indicationOfUSG;
	}

	public String getEcgDetails() {
		return ecgDetails;
	}

	public void setEcgDetails(String ecgDetails) {
		this.ecgDetails = ecgDetails;
	}

	public String getHolter() {
		return holter;
	}

	public void setHolter(String holter) {
		this.holter = holter;
	}

	public AppointmentRequest getAppointmentRequest() {
		return appointmentRequest;
	}

	public void setAppointmentRequest(AppointmentRequest appointmentRequest) {
		this.appointmentRequest = appointmentRequest;
	}

	@Override
	public String toString() {
		return "DischargeSummaryRequest [id=" + id + ", patientId=" + patientId + ", doctorId=" + doctorId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", prescriptions=" + prescriptions
				+ ", diagnosis=" + diagnosis + ", pastHistory=" + pastHistory + ", familyHistory=" + familyHistory
				+ ", personalHistory=" + personalHistory + ", complaint=" + complaint + ", presentComplaint="
				+ presentComplaint + ", presentComplaintHistory=" + presentComplaintHistory + ", menstrualHistory="
				+ menstrualHistory + ", obstetricHistory=" + obstetricHistory + ", generalExam=" + generalExam
				+ ", systemExam=" + systemExam + ", observation=" + observation + ", investigation=" + investigation
				+ ", pa=" + pa + ", ps=" + ps + ", pv=" + pv + ", echo=" + echo + ", xRayDetails=" + xRayDetails
				+ ", operationNotes=" + operationNotes + ", treatmentsGiven=" + treatmentsGiven
				+ ", appointmentRequest=" + appointmentRequest + ", uniqueEmrId=" + uniqueEmrId + ", admissionDate="
				+ admissionDate + ", dischargeDate=" + dischargeDate + ", labourNotes=" + labourNotes + ", babyWeight="
				+ babyWeight + ", babyNotes=" + babyNotes + ", conditionsAtDischarge=" + conditionsAtDischarge
				+ ", summary=" + summary + ", discarded=" + discarded + ", indicationOfUSG=" + indicationOfUSG
				+ ", ecgDetails=" + ecgDetails + ", holter=" + holter + "]";
	}

	public String getProcedureNote() {
		return procedureNote;
	}

	public void setProcedureNote(String procedureNote) {
		this.procedureNote = procedureNote;
	}

	public String getDoctorIncharge() {
		return doctorIncharge;
	}

	public void setDoctorIncharge(String doctorIncharge) {
		this.doctorIncharge = doctorIncharge;
	}

}
