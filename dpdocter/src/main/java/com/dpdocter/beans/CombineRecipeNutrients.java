package com.dpdocter.beans;

import java.util.Map;

public class CombineRecipeNutrients {

	private Map<String, Double> generalNutrients;

	private Map<String, Double> carbNutrients;

	private Map<String, Double> lipidNutrients;

	private Map<String, Double> proteinAminoAcidNutrients;

	private Map<String, Double> vitaminNutrients;

	private Map<String, Double> mineralNutrients;

	private Map<String, Double> otherNutrients;

	public Map<String, Double> getGeneralNutrients() {
		return generalNutrients;
	}

	public void setGeneralNutrients(Map<String, Double> generalNutrients) {
		this.generalNutrients = generalNutrients;
	}

	public Map<String, Double> getCarbNutrients() {
		return carbNutrients;
	}

	public void setCarbNutrients(Map<String, Double> carbNutrients) {
		this.carbNutrients = carbNutrients;
	}

	public Map<String, Double> getLipidNutrients() {
		return lipidNutrients;
	}

	public void setLipidNutrients(Map<String, Double> lipidNutrients) {
		this.lipidNutrients = lipidNutrients;
	}

	public Map<String, Double> getProteinAminoAcidNutrients() {
		return proteinAminoAcidNutrients;
	}

	public void setProteinAminoAcidNutrients(Map<String, Double> proteinAminoAcidNutrients) {
		this.proteinAminoAcidNutrients = proteinAminoAcidNutrients;
	}

	public Map<String, Double> getVitaminNutrients() {
		return vitaminNutrients;
	}

	public void setVitaminNutrients(Map<String, Double> vitaminNutrients) {
		this.vitaminNutrients = vitaminNutrients;
	}

	public Map<String, Double> getMineralNutrients() {
		return mineralNutrients;
	}

	public void setMineralNutrients(Map<String, Double> mineralNutrients) {
		this.mineralNutrients = mineralNutrients;
	}

	public Map<String, Double> getOtherNutrients() {
		return otherNutrients;
	}

	public void setOtherNutrients(Map<String, Double> otherNutrients) {
		this.otherNutrients = otherNutrients;
	}
}
