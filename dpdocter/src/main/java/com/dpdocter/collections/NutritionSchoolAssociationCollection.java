package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Doctor;
import com.dpdocter.beans.SchoolBranch;

@Document(collection = "nutritionist_school_association_cl")
public class NutritionSchoolAssociationCollection {

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

}
