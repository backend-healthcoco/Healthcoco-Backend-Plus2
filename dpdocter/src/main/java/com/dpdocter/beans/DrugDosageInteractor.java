package com.dpdocter.beans;

public class DrugDosageInteractor {

	private String adultDose;

	private String pediatricDose;

	private String geriatricDose;

	private String missedDose;

	private String overdose;

	public String getAdultDose() {
		return adultDose;
	}

	public void setAdultDose(String adultDose) {
		this.adultDose = adultDose;
	}

	public String getPediatricDose() {
		return pediatricDose;
	}

	public void setPediatricDose(String pediatricDose) {
		this.pediatricDose = pediatricDose;
	}

	public String getGeriatricDose() {
		return geriatricDose;
	}

	public void setGeriatricDose(String geriatricDose) {
		this.geriatricDose = geriatricDose;
	}

	public String getMissedDose() {
		return missedDose;
	}

	public void setMissedDose(String missedDose) {
		this.missedDose = missedDose;
	}

	public String getOverdose() {
		return overdose;
	}

	public void setOverdose(String overdose) {
		this.overdose = overdose;
	}

	@Override
	public String toString() {
		return "DrugDosageInteractor [adultDose=" + adultDose + ", pediatricDose=" + pediatricDose + ", geriatricDose="
				+ geriatricDose + ", missedDose=" + missedDose + ", overdose=" + overdose + "]";
	}

}
