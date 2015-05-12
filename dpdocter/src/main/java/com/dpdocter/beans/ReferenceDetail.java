package com.dpdocter.beans;

public class ReferenceDetail {
	private String id;
	private String reference;
	private String description;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "ReferenceDetail [id=" + id + ", reference=" + reference + ", description=" + description + "]";
	}

}
