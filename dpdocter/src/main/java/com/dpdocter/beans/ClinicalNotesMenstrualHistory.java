package com.dpdocter.beans;

public class ClinicalNotesMenstrualHistory {

	private String id;

	private String menstrualHistory;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMenstrualHistory() {
		return menstrualHistory;
	}

	public void setMenstrualHistory(String menstrualHistory) {
		this.menstrualHistory = menstrualHistory;
	}

	@Override
	public String toString() {
		return "ClinicalNotesMenstrualHistory [id=" + id + ", menstrualHistory=" + menstrualHistory + "]";
	}

}
