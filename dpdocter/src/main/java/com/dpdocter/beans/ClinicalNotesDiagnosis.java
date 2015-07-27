package com.dpdocter.beans;

public class ClinicalNotesDiagnosis {
    private String id;

    private String diagnosis;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getDiagnosis() {
	return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
	this.diagnosis = diagnosis;
    }

    @Override
    public String toString() {
	return "ClinicalNotesDiagnosis [id=" + id + ", diagnosis=" + diagnosis + "]";
    }

}
