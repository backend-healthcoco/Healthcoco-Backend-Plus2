package com.dpdocter.beans;

public class MedicalCouncil {
	private String id;
	private String medicalCouncil;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMedicalCouncil() {
		return medicalCouncil;
	}

	public void setMedicalCouncil(String medicalCouncil) {
		this.medicalCouncil = medicalCouncil;
	}

	@Override
	public String toString() {
		return "MedicalCouncil [id=" + id + ", medicalCouncil=" + medicalCouncil + "]";
	}

}
