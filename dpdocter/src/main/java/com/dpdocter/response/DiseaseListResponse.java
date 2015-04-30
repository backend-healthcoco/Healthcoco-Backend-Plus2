package com.dpdocter.response;

public class DiseaseListResponse {
	private String id;
	private String disease;
	private String description;

	public DiseaseListResponse(String id, String disease, String description) {
		this.id = id;
		this.disease = disease;
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisease() {
		return disease;
	}

	public void setDisease(String disease) {
		this.disease = disease;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "DiseaseListResponse [id=" + id + ", disease=" + disease + ", description=" + description + "]";
	}

}
