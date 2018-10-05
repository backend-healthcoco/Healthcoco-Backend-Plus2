package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "pharma_license_cl")
public class PharmaLicenseCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private Long date;
	@Field
	private Long quantity;
	@Field
	private Integer duration;
	@Field
	private ObjectId companyId;
	@Field
	private Long consumed;
	@Field
	private Long available;
	@Field
	private Boolean discarded;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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

	public ObjectId getCompanyId() {
		return companyId;
	}

	public void setCompanyId(ObjectId companyId) {
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
		return "PharmaLicenseCollection [id=" + id + ", date=" + date + ", quantity=" + quantity + ", duration="
				+ duration + ", companyId=" + companyId + ", consumed=" + consumed + ", available=" + available
				+ ", discarded=" + discarded + "]";
	}

}
