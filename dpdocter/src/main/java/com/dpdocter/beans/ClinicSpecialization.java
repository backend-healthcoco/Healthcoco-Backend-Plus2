package com.dpdocter.beans;

import java.util.List;

public class ClinicSpecialization {

    private String id;

    private List<String> specialization;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public List<String> getSpecialization() {
	return specialization;
    }

    public void setSpecialization(List<String> specialization) {
	this.specialization = specialization;
    }

    @Override
    public String toString() {
	return "ClinicSpecialization [id=" + id + ", specialization=" + specialization + "]";
    }
}
