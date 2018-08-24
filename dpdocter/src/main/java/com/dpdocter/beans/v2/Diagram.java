package com.dpdocter.beans.v2;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Diagram {
	private String id;

	private String diagramUrl;

	private String tags;

	private String speciality;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDiagramUrl() {
		return diagramUrl;
	}

	public void setDiagramUrl(String diagramUrl) {
		this.diagramUrl = diagramUrl;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

	@Override
	public String toString() {
		return "Diagram [id=" + id + ", diagramUrl=" + diagramUrl + ", tags=" + tags + ", speciality=" + speciality
				+ "]";
	}

}
