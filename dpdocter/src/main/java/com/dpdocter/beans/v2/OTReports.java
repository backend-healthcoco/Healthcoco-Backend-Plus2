package com.dpdocter.beans.v2;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.beans.DoctorAndCost;
import com.dpdocter.beans.Surgery;
import com.dpdocter.beans.TimeDuration;
import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.AnaesthesiaTypeEnum;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class OTReports extends GenericCollection {

	private String id;
	private String uniqueOTId;
	private String patientId;
	private PatientCard patient;
	private Date operationDate;
	private AnaesthesiaTypeEnum anaesthesiaType;
	private String provisionalDiagnosis;
	private Surgery surgery;
	private String finalDiagnosis;
	private String operatingSurgeon;
	private String anaesthetist;
	private List<String> assitingDoctors;
	private List<String> assitingNurses;
	private Boolean materialForHPE = false;
	private String remarks;
	private String doctorId;
	private String doctorName;
	private String locationId;
	private String locationName;
	private String hospitalId;
	private String hospitalName;
	private TimeDuration timeDuration;
	private Boolean discarded = false;
	private String operationalNotes;
	private DoctorAndCost operatingSurgeonAndCost;
	private DoctorAndCost anaesthetistAndCost;
	private List<DoctorAndCost> assitingDoctorsAndCost;
	private List<DoctorAndCost> assitingNursesAndCost;
	private List<PrescriptionItemDetail> postOperativeOrder;

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

	public PatientCard getPatient() {
		return patient;
	}

	public void setPatient(PatientCard patient) {
		this.patient = patient;
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

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
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

	public String getUniqueOTId() {
		return uniqueOTId;
	}

	public void setUniqueOTId(String uniqueOTId) {
		this.uniqueOTId = uniqueOTId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
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
		return "OTReports [id=" + id + ", uniqueOTId=" + uniqueOTId + ", patientId=" + patientId + ", patient="
				+ patient + ", operationDate=" + operationDate + ", anaesthesiaType=" + anaesthesiaType
				+ ", provisionalDiagnosis=" + provisionalDiagnosis + ", surgery=" + surgery + ", finalDiagnosis="
				+ finalDiagnosis + ", operatingSurgeon=" + operatingSurgeon + ", anaesthetist=" + anaesthetist
				+ ", assitingDoctors=" + assitingDoctors + ", assitingNurses=" + assitingNurses + ", materialForHPE="
				+ materialForHPE + ", remarks=" + remarks + ", doctorId=" + doctorId + ", doctorName=" + doctorName
				+ ", locationId=" + locationId + ", locationName=" + locationName + ", hospitalId=" + hospitalId
				+ ", hospitalName=" + hospitalName + ", timeDuration=" + timeDuration + ", discarded=" + discarded
				+ ", operationalNotes=" + operationalNotes + ", operatingSurgeonAndCost=" + operatingSurgeonAndCost
				+ ", anaesthetistAndCost=" + anaesthetistAndCost + ", assitingDoctorsAndCost=" + assitingDoctorsAndCost
				+ ", assitingNursesAndCost=" + assitingNursesAndCost + ", postOperativeOrder=" + postOperativeOrder
				+ "]";
	}

}
