package com.dpdocter.beans;

public class StageInteractor {

	/*
	 * A) Pediatric B) Geriatric C) Pregnant woman - Pregnancy Catevategory (drop
	 * down list containing 5 fields; A,B,C,D and X) D) Lactating mothers
	 */

	private Boolean pediatric;
	private Boolean geriatric;
	private Boolean lactatingMother;

	public Boolean getPediatric() {
		return pediatric;
	}

	public void setPediatric(Boolean pediatric) {
		this.pediatric = pediatric;
	}

	public Boolean getGeriatric() {
		return geriatric;
	}

	public void setGeriatric(Boolean geriatric) {
		this.geriatric = geriatric;
	}

	public Boolean getLactatingMother() {
		return lactatingMother;
	}

	public void setLactatingMother(Boolean lactatingMother) {
		this.lactatingMother = lactatingMother;
	}

	@Override
	public String toString() {
		return "StageInteractor [pediatric=" + pediatric + ", geriatric=" + geriatric + ", lactatingMother="
				+ lactatingMother + "]";
	}

}
