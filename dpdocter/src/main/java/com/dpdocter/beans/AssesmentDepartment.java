package com.dpdocter.beans;

import java.util.List;

public class AssesmentDepartment {

	private String assesmentType;

	private List<String> departments;

	public String getAssesmentType() {
		return assesmentType;
	}

	public void setAssesmentType(String assesmentType) {
		this.assesmentType = assesmentType;
	}

	public List<String> getDepartments() {
		return departments;
	}

	public void setDepartments(List<String> departments) {
		this.departments = departments;
	}

	@Override
	public String toString() {
		return "AssesmentDepartment [assesmentType=" + assesmentType + ", departments=" + departments + "]";
	}
}
