package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class DoctorSchoolAssociation extends GenericCollection {

	private String id;
	private List<String> doctorId;
	private String branchId;
	private SchoolBranch branch;
	private List<Doctor> doctors;
	private List<AssesmentDepartment> departments;

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

	public List<AssesmentDepartment> getDepartments() {
		return departments;
	}

	public void setDepartments(List<AssesmentDepartment> departments) {
		this.departments = departments;
	}

	@Override
	public String toString() {
		return "DoctorSchoolAssociation [id=" + id + ", doctorId=" + doctorId + ", branchId=" + branchId + ", branch="
				+ branch + ", doctors=" + doctors + ", departments=" + departments + "]";
	}
}
