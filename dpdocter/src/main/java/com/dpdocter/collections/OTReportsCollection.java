package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Surgery;
import com.dpdocter.enums.AnaesthesiaTypeEnum;
@Document(collection = "ot_report_cl")
public class OTReportsCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String serialNo;
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

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
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

	@Override
	public String toString() {
		return "OTReportsCollection [id=" + id + ", serialNo=" + serialNo + ", patientId=" + patientId
				+ ", operationDate=" + operationDate + ", anaesthesiaType=" + anaesthesiaType
				+ ", provisionalDiagnosis=" + provisionalDiagnosis + ", surgery=" + surgery + ", finalDiagnosis="
				+ finalDiagnosis + ", operatingSurgeon=" + operatingSurgeon + ", anaesthetist=" + anaesthetist
				+ ", assitingDoctors=" + assitingDoctors + ", assitingNurses=" + assitingNurses + ", materialForHPE="
				+ materialForHPE + ", remarks=" + remarks + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + "]";
	}

}
