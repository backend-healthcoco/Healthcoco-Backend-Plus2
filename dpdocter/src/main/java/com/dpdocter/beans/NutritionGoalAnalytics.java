package com.dpdocter.beans;

public class NutritionGoalAnalytics {

	private Long referredCount;
	private Long acceptedCount;
	private Long onHoldCount;
	private Long rejectedCount;
	private Long completedCount;
	private Long metGoalCount;

	public Long getReferredCount() {
		return referredCount;
	}

	public void setReferredCount(Long referredCount) {
		this.referredCount = referredCount;
	}

	public Long getAcceptedCount() {
		return acceptedCount;
	}

	public void setAcceptedCount(Long acceptedCount) {
		this.acceptedCount = acceptedCount;
	}

	public Long getOnHoldCount() {
		return onHoldCount;
	}

	public void setOnHoldCount(Long onHoldCount) {
		this.onHoldCount = onHoldCount;
	}

	public Long getRejectedCount() {
		return rejectedCount;
	}

	public void setRejectedCount(Long rejectedCount) {
		this.rejectedCount = rejectedCount;
	}

	public Long getCompletedCount() {
		return completedCount;
	}

	public void setCompletedCount(Long completedCount) {
		this.completedCount = completedCount;
	}

	public Long getMetGoalCount() {
		return metGoalCount;
	}

	public void setMetGoalCount(Long metGoalCount) {
		this.metGoalCount = metGoalCount;
	}

	@Override
	public String toString() {
		return "NutritionGoalAnalytics [referredCount=" + referredCount + ", acceptedCount=" + acceptedCount
				+ ", onHoldCount=" + onHoldCount + ", rejectedCount=" + rejectedCount + ", completedCount="
				+ completedCount + ", metGoalCount=" + metGoalCount + "]";
	}

}
