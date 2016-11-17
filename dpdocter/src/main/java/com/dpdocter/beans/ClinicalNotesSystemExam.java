package com.dpdocter.beans;

public class ClinicalNotesSystemExam {

	private String id;

	private String systemExam;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSystemExam() {
		return systemExam;
	}

	public void setSystemExam(String systemExam) {
		this.systemExam = systemExam;
	}

	@Override
	public String toString() {
		return "ClinicalNotesSystemExam [id=" + id + ", systemExam=" + systemExam + "]";
	}

}
