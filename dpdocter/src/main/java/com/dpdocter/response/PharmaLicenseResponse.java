package com.dpdocter.response;

import com.dpdocter.collections.GenericCollection;

public class PharmaLicenseResponse extends GenericCollection {

	private String id;
	private Long date;
	private Long quantity;
	private Integer duration;
	private String companyId;
	private Long consumed;
	private Long available;
	private Boolean discarded;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public Long getConsumed() {
		return consumed;
	}

	public void setConsumed(Long consumed) {
		this.consumed = consumed;
	}

	public Long getAvailable() {
		return available;
	}

	public void setAvailable(Long available) {
		this.available = available;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "PharmaLicenseResponse [id=" + id + ", date=" + date + ", quantity=" + quantity + ", duration="
				+ duration + ", companyId=" + companyId + ", consumed=" + consumed + ", available=" + available
				+ ", discarded=" + discarded + "]";
	}

}
