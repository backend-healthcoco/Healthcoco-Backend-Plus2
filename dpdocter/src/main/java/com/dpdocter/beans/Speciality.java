package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class Speciality extends GenericCollection {
    private String id;

    private String speciality;

    private String superSpeciality;

    private String code;

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

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    @Override
    public String toString() {
	return "Speciality [id=" + id + ", speciality=" + speciality + ", superSpeciality=" + superSpeciality + ", code=" + code + "]";
    }

}
