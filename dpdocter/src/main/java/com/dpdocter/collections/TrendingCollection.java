package com.dpdocter.collections;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.OfferSchedule;
import com.dpdocter.enums.OfferCategaryType;
import com.dpdocter.enums.TrendingEnum;

@Document(collection = "trending_cl")
public class TrendingCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private List<OfferCategaryType> type;
	@Field
	private TrendingEnum resourceType = TrendingEnum.BLOG;
	@Field
	private ObjectId offerId;
	@Field
	private ObjectId blogId;
	@Field
	private Integer rank = 0;
	@Field
	private Date fromDate;
	@Field
	private Date toDate;
	@Field
	private List<OfferSchedule> time;

	private Boolean discarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<OfferCategaryType> getType() {
		return type;
	}

	public void setType(List<OfferCategaryType> type) {
		this.type = type;
	}

	public TrendingEnum getResourceType() {
		return resourceType;
	}

	public void setResourceType(TrendingEnum resourceType) {
		this.resourceType = resourceType;
	}

	public ObjectId getOfferId() {
		return offerId;
	}

	public void setOfferId(ObjectId offerId) {
		this.offerId = offerId;
	}

	public ObjectId getBlogId() {
		return blogId;
	}

	public void setBlogId(ObjectId blogId) {
		this.blogId = blogId;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public List<OfferSchedule> getTime() {
		return time;
	}

	public void setTime(List<OfferSchedule> time) {
		this.time = time;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

}
