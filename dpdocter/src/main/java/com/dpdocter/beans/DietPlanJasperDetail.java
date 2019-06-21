package com.dpdocter.beans;

public class DietPlanJasperDetail {

	private String timing;

	private String recipe;

	private String quantity;

	public String getTiming() {
		return timing;
	}

	public void setTiming(String timing) {
		this.timing = timing;
	}

	public String getRecipe() {
		return recipe;
	}

	public void setRecipe(String recipe) {
		this.recipe = recipe;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "DietPlanJasperDetail [timing=" + timing + ", recipe=" + recipe + ", quantity=" + quantity + "]";
	}

}
