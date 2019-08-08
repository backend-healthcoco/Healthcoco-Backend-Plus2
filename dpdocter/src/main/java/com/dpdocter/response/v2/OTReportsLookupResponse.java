package com.dpdocter.response.v2;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.DoctorAndCost;
import com.dpdocter.beans.Patient;
import com.dpdocter.beans.Surgery;
import com.dpdocter.beans.TimeDuration;
import com.dpdocter.beans.v2.PrescriptionItemDetail;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.AnaesthesiaTypeEnum;

public class OTReportsLookupResponse extends GenericCollection {

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
	private String operationalNotes;
	private PatientCollection patientCollection;
	private UserCollection patientUser;
	private String uniqueOTId;
	private Boolean discarded = false;
	private DoctorAndCost operatingSurgeonAndCost;
	private DoctorAndCost anaesthetistAndCost;
	private List<DoctorAndCost> assitingDoctorsAndCost;
	private List<DoctorAndCost> assitingNursesAndCost;
	private List<PrescriptionItemDetail> postOperativeOrder;

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
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

	public String getOperationalNotes() {
		return operationalNotes;
	}

	public void setOperationalNotes(String operationalNotes) {
		this.operationalNotes = operationalNotes;
	}

	public PatientCollection getPatientCollection() {
		return patientCollection;
	}

	public void setPatientCollection(PatientCollection patientCollection) {
		this.patientCollection = patientCollection;
	}

	public UserCollection getPatientUser() {
		return patientUser;
	}

	public void setPatientUser(UserCollection patientUser) {
		this.patientUser = patientUser;
	}

	public String getUniqueOTId() {
		return uniqueOTId;
	}

	public void setUniqueOTId(String uniqueOTId) {
		this.uniqueOTId = uniqueOTId;
	}

	public DoctorAndCost getOperatingSurgeonAndCost() {
		return operatingSurgeonAndCost;
	}

	public void setOperatingSurgeonAndCost(DoctorAndCost operatingSurgeonAndCost) {
		this.operatingSurgeonAndCost = operatingSurgeonAndCost;
	}

	public DoctorAndCost getAnaesthetistAndCost() {
		return anaesthetistAndCost;
	}

	public void setAnaesthetistAndCost(DoctorAndCost anaesthetistAndCost) {
		this.anaesthetistAndCost = anaesthetistAndCost;
	}

	public List<DoctorAndCost> getAssitingDoctorsAndCost() {
		return assitingDoctorsAndCost;
	}

	public void setAssitingDoctorsAndCost(List<DoctorAndCost> assitingDoctorsAndCost) {
		this.assitingDoctorsAndCost = assitingDoctorsAndCost;
	}

	public List<DoctorAndCost> getAssitingNursesAndCost() {
		return assitingNursesAndCost;
	}

	public void setAssitingNursesAndCost(List<DoctorAndCost> assitingNursesAndCost) {
		this.assitingNursesAndCost = assitingNursesAndCost;
	}

	public List<PrescriptionItemDetail> getPostOperativeOrder() {
		return postOperativeOrder;
	}

	public void setPostOperativeOrder(List<PrescriptionItemDetail> postOperativeOrder) {
		this.postOperativeOrder = postOperativeOrder;
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
				+ hospital + ", timeDuration=" + timeDuration + ", operationalNotes=" + operationalNotes
				+ ", patientCollection=" + patientCollection + ", patientUser=" + patientUser + ", uniqueOTId="
				+ uniqueOTId + ", discarded=" + discarded + ", operatingSurgeonAndCost=" + operatingSurgeonAndCost
				+ ", anaesthetistAndCost=" + anaesthetistAndCost + ", assitingDoctorsAndCost=" + assitingDoctorsAndCost
				+ ", assitingNursesAndCost=" + assitingNursesAndCost + ", postOperativeOrder=" + postOperativeOrder
				+ "]";
	}

}
