package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.Medication;
import com.dpdocter.beans.RiskScore;
import com.dpdocter.beans.VitalSigns;
import com.dpdocter.collections.GenericCollection;

public class InitialAdmissionResponse extends GenericCollection {

	private String id;

	private String doctorId;

	private String nurseName;

	private String locationId;

	private String hospitalId;

	private String patientId;

	private Date admissionDate;
	private String timeOfAdmission;

	private VitalSigns vitalSigns;

	private List<Medication> oldMedication;

	private String nursingCare;

	private String coMorbidities;// more than one illness or disease

	private String advice;
	private List<RiskScore> riskFactor;
	private int totalRiskScore;

	private boolean discarded = false;

	private String ipdNumber;

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

	public String getNurseName() {
		return nurseName;
	}

	public void setNurseName(String nurseName) {
		this.nurseName = nurseName;
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

	public Date getAdmissionDate() {
		return admissionDate;
	}

	public void setAdmissionDate(Date admissionDate) {
		this.admissionDate = admissionDate;
	}

	public VitalSigns getVitalSigns() {
		return vitalSigns;
	}

	public void setVitalSigns(VitalSigns vitalSigns) {
		this.vitalSigns = vitalSigns;
	}

	public List<Medication> getOldMedication() {
		return oldMedication;
	}

	public void setOldMedication(List<Medication> oldMedication) {
		this.oldMedication = oldMedication;
	}

	public String getNursingCare() {
		return nursingCare;
	}

	public void setNursingCare(String nursingCare) {
		this.nursingCare = nursingCare;
	}

	public String getCoMorbidities() {
		return coMorbidities;
	}

	public void setCoMorbidities(String coMorbidities) {
		this.coMorbidities = coMorbidities;
	}

	public String getAdvice() {
		return advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
	}

	public List<RiskScore> getRiskFactor() {
		return riskFactor;
	}

	public void setRiskFactor(List<RiskScore> riskFactor) {
		this.riskFactor = riskFactor;
	}

	public int getTotalRiskScore() {
		return totalRiskScore;
	}

	public void setTotalRiskScore(int totalRiskScore) {
		this.totalRiskScore = totalRiskScore;
	}

	public String getTimeOfAdmission() {
		return timeOfAdmission;
	}

	public void setTimeOfAdmission(String timeOfAdmission) {
		this.timeOfAdmission = timeOfAdmission;
	}

	public boolean isDiscarded() {
		return discarded;
	}

	public void setDiscarded(boolean discarded) {
		this.discarded = discarded;
	}

	public String getIpdNumber() {
		return ipdNumber;
	}

	public void setIpdNumber(String ipdNumber) {
		this.ipdNumber = ipdNumber;
	}

}
