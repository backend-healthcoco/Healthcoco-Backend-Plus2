package com.dpdocter.beans;

public class ClinicalNotesComplaint {
	private String id;

	private String complaint;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getComplaint() {
		return complaint;
	}

	public void setComplaint(String complaint) {
		this.complaint = complaint;
	}

	@Override
	public String toString() {
		return "ClinicalNotesComplaint [id=" + id + ", complaint=" + complaint + "]";
	}

}
