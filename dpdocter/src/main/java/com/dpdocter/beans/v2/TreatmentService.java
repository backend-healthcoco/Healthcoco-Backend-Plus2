package com.dpdocter.beans.v2;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TreatmentService  {
	private String id;

	private String name;

	private String speciality;

	private String treatmentCode;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

	public String getTreatmentCode() {
		return treatmentCode;
	}

	public void setTreatmentCode(String treatmentCode) {
		this.treatmentCode = treatmentCode;
	}

	@Override
	public String toString() {
		return "TreatmentService [id=" + id + ", name=" + name + ", speciality=" + speciality + ", treatmentCode="
				+ treatmentCode + "]";
	}

}
