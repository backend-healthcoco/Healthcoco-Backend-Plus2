package com.dpdocter.beans;

public class Specimen {
	private String id;
	private String specimen;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSpecimen() {
		return specimen;
	}

	public void setSpecimen(String specimen) {
		this.specimen = specimen;
	}

	@Override
	public String toString() {
		return "Specimen [id=" + id + ", specimen=" + specimen + "]";
	}

}
