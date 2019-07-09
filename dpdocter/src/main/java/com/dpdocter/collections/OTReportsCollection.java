package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.DoctorAndCost;
import com.dpdocter.beans.Surgery;
import com.dpdocter.beans.v2.PrescriptionItemDetail;
import com.dpdocter.enums.AnaesthesiaTypeEnum;

@Document(collection = "ot_report_cl")
public class OTReportsCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String uniqueOTId;
	@Field
	private ObjectId patientId;
	@Field
	private Date operationDate;
	@Field
	private AnaesthesiaTypeEnum anaesthesiaType;
	@Field
	private String provisionalDiagnosis;
	@Field
	private Surgery surgery;
	@Field
	private String finalDiagnosis;
	@Field
	private String operatingSurgeon;
	@Field
	private String anaesthetist;
	@Field
	private List<String> assitingDoctors;
	@Field
	private List<String> assitingNurses;
	@Field
	private Boolean materialForHPE;
	@Field
	private String remarks;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private String operationalNotes;
	@Field
	private Boolean isPatientDiscarded = false;
	@Field
	private Boolean discarded = false;
	@Field
	private DoctorAndCost operatingSurgeonAndCost;
	@Field
	private DoctorAndCost anaesthetistAndCost;
	@Field
	private List<DoctorAndCost> assitingDoctorsAndCost;
	@Field
	private List<DoctorAndCost> assitingNursesAndCost;
	@Field
	private List<PrescriptionItemDetail> postOperativeOrder;

	public ObjectId getId() {
		return id;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getPatientId() {
		return patientId;
	}

	public void setPatientId(ObjectId patientId) {
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

	public Boolean getIsPatientDiscarded() {
		return isPatientDiscarded;
	}

	public void setIsPatientDiscarded(Boolean isPatientDiscarded) {
		this.isPatientDiscarded = isPatientDiscarded;
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
		return "OTReportsCollection [id=" + id + ", uniqueOTId=" + uniqueOTId + ", patientId=" + patientId
				+ ", operationDate=" + operationDate + ", anaesthesiaType=" + anaesthesiaType
				+ ", provisionalDiagnosis=" + provisionalDiagnosis + ", surgery=" + surgery + ", finalDiagnosis="
				+ finalDiagnosis + ", operatingSurgeon=" + operatingSurgeon + ", anaesthetist=" + anaesthetist
				+ ", assitingDoctors=" + assitingDoctors + ", assitingNurses=" + assitingNurses + ", materialForHPE="
				+ materialForHPE + ", remarks=" + remarks + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", operationalNotes=" + operationalNotes + ", isPatientDiscarded="
				+ isPatientDiscarded + ", discarded=" + discarded + ", operatingSurgeonAndCost="
				+ operatingSurgeonAndCost + ", anaesthetistAndCost=" + anaesthetistAndCost + ", assitingDoctorsAndCost="
				+ assitingDoctorsAndCost + ", assitingNursesAndCost=" + assitingNursesAndCost + ", postOperativeOrder="
				+ postOperativeOrder + "]";
	}
}