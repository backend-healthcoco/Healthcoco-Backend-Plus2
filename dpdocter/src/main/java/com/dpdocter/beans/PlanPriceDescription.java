package com.dpdocter.beans;

import java.util.List;

public class PlanPriceDescription {

	private PlanDescriptionPrices planDescriptionPrices;
	private List<PlanPrice> planPrices;

	public PlanDescriptionPrices getPlanDescriptionPrices() {
		return planDescriptionPrices;
	}

	public void setPlanDescriptionPrices(PlanDescriptionPrices planDescriptionPrices) {
		this.planDescriptionPrices = planDescriptionPrices;
	}

	public List<PlanPrice> getPlanPrices() {
		return planPrices;
	}

	public void setPlanPrices(List<PlanPrice> planPrices) {
		this.planPrices = planPrices;
	}

}
