package com.dpdocter.beans;

import java.util.List;

public class DrugsAndAllergies {

	private List<Drug> drugs;
	private String allergies;

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
		return "DrugsAndAllergies [drugs=" + drugs + ", allergies=" + allergies + "]";
	}

}
