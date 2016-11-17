package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ClinicalNotesObservation {
    private String id;

    private String observation;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getObservation() {
	return observation;
    }

    public void setObservation(String observation) {
	this.observation = observation;
    }

    @Override
    public String toString() {
	return "ClinicalNotesObservation [id=" + id + ", observation=" + observation + "]";
    }

}
