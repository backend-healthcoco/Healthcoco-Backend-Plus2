package com.dpdocter.response;

public class AmountResponse {

	private Double totalDueAmount;

	private Double totalRemainingAdvanceAmount;

	public Double getTotalDueAmount() {
		return totalDueAmount;
	}

	public Double getTotalRemainingAdvanceAmount() {
		return totalRemainingAdvanceAmount;
	}

	public void setTotalRemainingAdvanceAmount(Double totalRemainingAdvanceAmount) {
		this.totalRemainingAdvanceAmount = totalRemainingAdvanceAmount;
	}

	public void setTotalDueAmount(Double totalDueAmount) {
		this.totalDueAmount = totalDueAmount;
	}

	@Override
	public String toString() {
		return "AmountResponse [totalDueAmount=" + totalDueAmount + ", totalRemainingAdvanceAmount="
				+ totalRemainingAdvanceAmount + "]";
	}
}
