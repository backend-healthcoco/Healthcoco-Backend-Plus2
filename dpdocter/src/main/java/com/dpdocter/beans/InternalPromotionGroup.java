package com.dpdocter.beans;

public class InternalPromotionGroup {

	private String id;
	private String groupName;
	private String promoCode;
	private Boolean discarded = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getPromoCode() {
		return promoCode;
	}

	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "InternalPromotionGroup [id=" + id + ", groupName=" + groupName + ", promoCode=" + promoCode
				+ ", discarded=" + discarded + "]";
	}

}
