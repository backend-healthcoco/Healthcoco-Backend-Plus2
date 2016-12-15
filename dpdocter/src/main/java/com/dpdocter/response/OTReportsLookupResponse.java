package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.Patient;
import com.dpdocter.beans.Surgery;
import com.dpdocter.beans.TimeDuration;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.AnaesthesiaTypeEnum;

public class OTReportsLookupResponse {

	private String id;
	private String patientId;
	private Patient patient;
	private Date operationDate;
	private AnaesthesiaTypeEnum anaesthesiaType;
	private String provisionalDiagnosis;
	private Surgery surgery;
	private String finalDiagnosis;
	private String operatingSurgeon;
	private String anaesthetist;
	private List<String> assitingDoctors;
	private List<String> assitingNurses;
	private Boolean materialForHPE;
	private String remarks;
	private String doctorId;
	private UserCollection doctor;
	private String locationId;
	private LocationCollection location;
	private String hospitalId;
	private HospitalCollection hospital;
	private TimeDuration timeDuration;
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
	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	public Date getOperationDate() {
		return operationDate;
	}
	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}
	public AnaesthesiaTypeEnum getAnaesthesiaType() {
		return anaesthesiaType;
	}
	public void setAnaesthesiaType(AnaesthesiaTypeEnum anaesthesiaType) {
		this.anaesthesiaType = anaesthesiaType;
	}
	public String getProvisionalDiagnosis() {
		return provisionalDiagnosis;
	}
	public void setProvisionalDiagnosis(String provisionalDiagnosis) {
		this.provisionalDiagnosis = provisionalDiagnosis;
	}
	public Surgery getSurgery() {
		return surgery;
	}
	public void setSurgery(Surgery surgery) {
		this.surgery = surgery;
	}
	public String getFinalDiagnosis() {
		return finalDiagnosis;
	}
	public void setFinalDiagnosis(String finalDiagnosis) {
		this.finalDiagnosis = finalDiagnosis;
	}
	public String getOperatingSurgeon() {
		return operatingSurgeon;
	}
	public void setOperatingSurgeon(String operatingSurgeon) {
		this.operatingSurgeon = operatingSurgeon;
	}
	public String getAnaesthetist() {
		return anaesthetist;
	}
	public void setAnaesthetist(String anaesthetist) {
		this.anaesthetist = anaesthetist;
	}
	public List<String> getAssitingDoctors() {
		return assitingDoctors;
	}
	public void setAssitingDoctors(List<String> assitingDoctors) {
		this.assitingDoctors = assitingDoctors;
	}
	public List<String> getAssitingNurses() {
		return assitingNurses;
	}
	public void setAssitingNurses(List<String> assitingNurses) {
		this.assitingNurses = assitingNurses;
	}
	public Boolean getMaterialForHPE() {
		return materialForHPE;
	}
	public void setMaterialForHPE(Boolean materialForHPE) {
		this.materialForHPE = materialForHPE;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}
	public UserCollection getDoctor() {
		return doctor;
	}
	public void setDoctor(UserCollection doctor) {
		this.doctor = doctor;
	}
	public String getLocationId() {
		return locationId;
	}
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	public LocationCollection getLocation() {
		return location;
	}
	public void setLocation(LocationCollection location) {
		this.location = location;
	}
	public String getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}
	public HospitalCollection getHospital() {
		return hospital;
	}
	public void setHospital(HospitalCollection hospital) {
		this.hospital = hospital;
	}
	public TimeDuration getTimeDuration() {
		return timeDuration;
	}
	public void setTimeDuration(TimeDuration timeDuration) {
		this.timeDuration = timeDuration;
	}
	@Override
	public String toString() {
		return "OTReportsLookupResponse [id=" + id + ", patientId=" + patientId + ", patient=" + patient
				+ ", operationDate=" + operationDate + ", anaesthesiaType=" + anaesthesiaType
				+ ", provisionalDiagnosis=" + provisionalDiagnosis + ", surgery=" + surgery + ", finalDiagnosis="
				+ finalDiagnosis + ", operatingSurgeon=" + operatingSurgeon + ", anaesthetist=" + anaesthetist
				+ ", assitingDoctors=" + assitingDoctors + ", assitingNurses=" + assitingNurses + ", materialForHPE="
				+ materialForHPE + ", remarks=" + remarks + ", doctorId=" + doctorId + ", doctor=" + doctor
				+ ", locationId=" + locationId + ", location=" + location + ", hospitalId=" + hospitalId + ", hospital="
				+ hospital + ", timeDuration=" + timeDuration + "]";
	}
}
