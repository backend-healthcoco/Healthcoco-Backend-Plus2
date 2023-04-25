package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Education {
	private String qualification;

	private String collegeUniversity;

	private int yearOfPassing;

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public String getCollegeUniversity() {
		return collegeUniversity;
	}

	public void setCollegeUniversity(String collegeUniversity) {
		this.collegeUniversity = collegeUniversity;
	}

	public int getYearOfPassing() {
		return yearOfPassing;
	}

	public void setYearOfPassing(int yearOfPassing) {
		this.yearOfPassing = yearOfPassing;
	}

	@Override
	public String toString() {
		return "Education [qualification=" + qualification + ", collegeUniversity=" + collegeUniversity
				+ ", yearOfPassing=" + yearOfPassing + "]";
	}

}
