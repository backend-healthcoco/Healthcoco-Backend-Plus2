package com.dpdocter.response;

import java.util.List;

public class DailyReportAnalyticResponse {

	private List<DailyReportAnalyticItem> dailyReportAnalyticItem;
	private double advancedAmount = 0.0;
	private double totalServiceFees = 0;
	private double totalDiscount = 0;
	private double totalAmountPaid = 0;
	private double totalAmountPending = 0;

	private double totalAmountByCash = 0;

	private double totalAmountByCard = 0;

	public List<DailyReportAnalyticItem> getDailyReportAnalyticItem() {
		return dailyReportAnalyticItem;
	}

	public void setDailyReportAnalyticItem(List<DailyReportAnalyticItem> dailyReportAnalyticItem) {
		this.dailyReportAnalyticItem = dailyReportAnalyticItem;
	}

	public double getTotalServiceFees() {
		return totalServiceFees;
	}

	public void setTotalServiceFees(double totalServiceFees) {
		this.totalServiceFees = totalServiceFees;
	}

	public double getTotalDiscount() {
		return totalDiscount;
	}

	public void setTotalDiscount(double totalDiscount) {
		this.totalDiscount = totalDiscount;
	}

	public double getAdvancedAmount() {
		return advancedAmount;
	}

	public void setAdvancedAmount(double advancedAmount) {
		this.advancedAmount = advancedAmount;
	}

	public double getTotalAmountPaid() {
		return totalAmountPaid;
	}

	public void setTotalAmountPaid(double totalAmountPaid) {
		this.totalAmountPaid = totalAmountPaid;
	}

	public double getTotalAmountPending() {
		return totalAmountPending;
	}

	public void setTotalAmountPending(double totalAmountPending) {
		this.totalAmountPending = totalAmountPending;
	}

	public double getTotalAmountByCash() {
		return totalAmountByCash;
	}

	public void setTotalAmountByCash(double totalAmountByCash) {
		this.totalAmountByCash = totalAmountByCash;
	}

	public double getTotalAmountByCard() {
		return totalAmountByCard;
	}

	public void setTotalAmountByCard(double totalAmountByCard) {
		this.totalAmountByCard = totalAmountByCard;
	}

}
