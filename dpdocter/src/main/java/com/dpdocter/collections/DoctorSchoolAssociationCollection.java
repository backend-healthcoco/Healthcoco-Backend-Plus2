package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.AssesmentDepartment;
import com.dpdocter.beans.Doctor;
import com.dpdocter.beans.SchoolBranch;

@Document(collection = "doctor_school_association_cl")
public class DoctorSchoolAssociationCollection extends GenericCollection{

	@Field
	private ObjectId id;
	@Field
	private List<ObjectId> doctorId;
	@Field
	private ObjectId branchId;
	@Field
	private SchoolBranch branch;
	@Field
	private List<Doctor> doctors;
	@Field
	private List<AssesmentDepartment> departments;
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public List<ObjectId> getDoctorId() {
		return doctorId;
	}
	public void setDoctorId(List<ObjectId> doctorId) {
		this.doctorId = doctorId;
	}
	public ObjectId getBranchId() {
		return branchId;
	}
	public void setBranchId(ObjectId branchId) {
		this.branchId = branchId;
	}
	public SchoolBranch getBranch() {
		return branch;
	}
	public void setBranch(SchoolBranch branch) {
		this.branch = branch;
	}
	public List<Doctor> getDoctors() {
		return doctors;
	}
	public void setDoctors(List<Doctor> doctors) {
		this.doctors = doctors;
	}
	public List<AssesmentDepartment> getDepartments() {
		return departments;
	}
	public void setDepartments(List<AssesmentDepartment> departments) {
		this.departments = departments;
	}
	@Override
	public String toString() {
		return "DoctorSchoolAssociationCollection [id=" + id + ", doctorId=" + doctorId + ", branchId=" + branchId
				+ ", branch=" + branch + ", doctors=" + doctors + ", departments=" + departments + "]";
	}
}
