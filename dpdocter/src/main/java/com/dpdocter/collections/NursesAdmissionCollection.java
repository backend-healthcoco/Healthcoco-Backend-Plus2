package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Medication;
import com.dpdocter.beans.RiskScore;
import com.dpdocter.beans.VitalSigns;

@Document(collection ="nurses_admission_form_cl")
public class NursesAdmissionCollection extends GenericCollection{
	@Id
	private ObjectId id;

	@Field
	private ObjectId doctorId;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Field
	private ObjectId patientId;
	@Field
	private String nurseName;
	@Field
	private VitalSigns vitalSigns;
	@Field
	private List<Medication> oldMedication;
	@Field
	private String nursingCare;
	@Field
	private String coMorbidities;//more than one illness or disease
	@Field
    private String advice;
	@Field
	private List<RiskScore> riskFactor;
	@Field
	private int totalRiskScore;
	
	@Field
	private boolean discarded = false;

	@Field
	private String uniqueEmrId;
	
	@Field
	private String ipdNumber;
	
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

	

	public String getNurseName() {
		return nurseName;
	}

	public void setNurseName(String nurseName) {
		this.nurseName = nurseName;
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

	public boolean isDiscarded() {
		return discarded;
	}

	public void setDiscarded(boolean discarded) {
		this.discarded = discarded;
	}

	public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
	}

	public String getIpdNumber() {
		return ipdNumber;
	}

	public void setIpdNumber(String ipdNumber) {
		this.ipdNumber = ipdNumber;
	}

	
	
	

}
