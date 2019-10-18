package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class NutritionSchoolAssociation extends GenericCollection {

	private String id;
	private List<String> doctorId;
	private String branchId;
	private SchoolBranch branch;
	private List<Doctor> doctors;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(List<String> doctorId) {
		this.doctorId = doctorId;
	}

	public String getBranchId() {
		return branchId;
	}

	public void setBranchId(String branchId) {
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
