package com.dpdocter.response;

public class AmountResponse {

	private Double balanceAmount;
	
	private Double advanceAmount;

	public Double getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(Double balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public Double getAdvanceAmount() {
		return advanceAmount;
	}

	public void setAdvanceAmount(Double advanceAmount) {
		this.advanceAmount = advanceAmount;
	}

	@Override
	public String toString() {
		return "AmountResponse [balanceAmount=" + balanceAmount + ", advanceAmount=" + advanceAmount + "]";
	}
}
