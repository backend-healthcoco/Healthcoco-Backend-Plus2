package com.dpdocter.beans;

public class ClinicalNotesPresentComplaintHistory {

	private String id;

	private String presentComplaintHistory;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPresentComplaintHistory() {
		return presentComplaintHistory;
	}

	public void setPresentComplaintHistory(String presentComplaintHistory) {
		this.presentComplaintHistory = presentComplaintHistory;
	}

	@Override
	public String toString() {
		return "ClinicalNotesPresentComplaintHistory [id=" + id + ", presentComplaintHistory=" + presentComplaintHistory
				+ "]";
	}

}
