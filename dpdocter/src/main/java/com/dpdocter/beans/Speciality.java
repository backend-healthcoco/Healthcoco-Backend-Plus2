package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Speciality extends GenericCollection {
	private String id;

	private String speciality;

	private String superSpeciality;

	private Boolean toShow = true;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

	public String getSuperSpeciality() {
		return superSpeciality;
	}

	public void setSuperSpeciality(String superSpeciality) {
		this.superSpeciality = superSpeciality;
	}

	public Boolean getToShow() {
		return toShow;
	}

	public void setToShow(Boolean toShow) {
		this.toShow = toShow;
	}

	@Override
	public String toString() {
		return "Speciality [id=" + id + ", speciality=" + speciality + ", superSpeciality=" + superSpeciality
				+ ", toShow=" + toShow + "]";
	}
}
