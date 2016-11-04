package com.dpdocter.beans;

public class ClinicalNotesGeneralExam {

	private String id;

	private String generalExam;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGeneralExam() {
		return generalExam;
	}

	public void setGeneralExam(String generalExam) {
		this.generalExam = generalExam;
	}

	@Override
	public String toString() {
		return "ClinicalNotesGeneralExam [id=" + id + ", generalExam=" + generalExam + "]";
	}

}
