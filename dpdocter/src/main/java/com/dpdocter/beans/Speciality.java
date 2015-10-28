package com.dpdocter.beans;

public class Speciality {
    private String id;

    private String speciality;
    
    private String superSpeciality;

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

	@Override
	public String toString() {
		return "Speciality [id=" + id + ", speciality=" + speciality + ", superSpeciality=" + superSpeciality + "]";
	}
}
