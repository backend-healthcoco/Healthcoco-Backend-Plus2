package com.dpdocter.beans;

import java.util.List;

public class DrugsAndAllergies {

	private String id;
	private List<Drug> drugs;
	private String allergies;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Drug> getDrugs() {
		return drugs;
	}

	public void setDrugs(List<Drug> drugs) {
		this.drugs = drugs;
	}

	public String getAllergies() {
		return allergies;
	}

	public void setAllergies(String allergies) {
		this.allergies = allergies;
	}

	@Override
	public String toString() {
		return "DrugsAndAllergies [id=" + id + ", drugs=" + drugs + ", allergies=" + allergies + "]";
	}

}
