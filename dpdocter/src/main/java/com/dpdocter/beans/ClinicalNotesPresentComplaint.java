package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ClinicalNotesPresentComplaint {

	private String id;

	private String presentComplaint;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPresentComplaint() {
		return presentComplaint;
	}

	public void setPresentComplaint(String presentComplaint) {
		this.presentComplaint = presentComplaint;
	}

	@Override
	public String toString() {
		return "ClinicalNotesPresentComplaint [id=" + id + ", presentComplaint=" + presentComplaint + "]";
	}

}
