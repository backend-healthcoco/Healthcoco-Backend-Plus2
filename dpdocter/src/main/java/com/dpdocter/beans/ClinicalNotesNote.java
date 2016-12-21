package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ClinicalNotesNote {
    private String id;

    private String note;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getNote() {
	return note;
    }

    public void setNote(String note) {
	this.note = note;
    }

    @Override
    public String toString() {
	return "ClinicalNotesNote [id=" + id + ", note=" + note + "]";
    }

}
