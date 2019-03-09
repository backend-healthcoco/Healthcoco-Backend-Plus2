package com.dpdocter.elasticsearch.document;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.dpdocter.beans.OfferSchedule;
import com.dpdocter.enums.OfferCategaryType;
import com.dpdocter.enums.TrendingEnum;

@Document(indexName = "trending_in", type = "trendings")
public class ESTrendingDocument {
	@Id
	private String id;

	@Field(type = FieldType.String)
	private List<OfferCategaryType> type;

	@Field(type = FieldType.String)
	private TrendingEnum resourceType = TrendingEnum.BLOG;

	@Field(type = FieldType.String)
	private String offerId;

	@Field(type = FieldType.String)
	private String blogId;

	@Field(type = FieldType.Integer)
	private Integer rank = 0;

	@Field(type = FieldType.Date)
	private Date fromDate;

	@Field(type = FieldType.Date)
	private Date toDate;

	@Field(type = FieldType.Nested)
	private List<OfferSchedule> time;

	@Field(type = FieldType.Date)
	private Date updatedTime = new Date();

	@Field(type = FieldType.Boolean)
	private Boolean discarded = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<OfferCategaryType> getType() {
		return type;
	}

	public void setType(List<OfferCategaryType> type) {
		this.type = type;
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

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getOfferId() {
		return offerId;
	}

	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}

	public String getBlogId() {
		return blogId;
	}

	public void setBlogId(String blogId) {
		this.blogId = blogId;
	}

	public TrendingEnum getResourceType() {
		return resourceType;
	}

	public void setResourceType(TrendingEnum resourceType) {
		this.resourceType = resourceType;
	}

}
