package com.dpdocter.response;

import java.util.Date;
import java.util.List;

import com.dpdocter.beans.Blog;
import com.dpdocter.beans.Offer;
import com.dpdocter.beans.OfferSchedule;
import com.dpdocter.collections.GenericCollection;

public class TrendingResponse extends GenericCollection {
	private String id;

	private List<String> type;

	private String resourceType = "BLOG";

	private String offerId;

	private String blogId;

	private Integer rank = 0;

	private Date fromDate;

	private Date toDate;

	private List<OfferSchedule> time;

	private Offer offer;

	private Blog blog;

	private Boolean discarded = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getType() {
		return type;
	}

	public void setType(List<String> type) {
		this.type = type;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
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

	public Offer getOffer() {
		return offer;
	}

	public void setOffer(Offer offer) {
		this.offer = offer;
	}

	public Blog getBlog() {
		return blog;
	}

	public void setBlog(Blog blog) {
		this.blog = blog;
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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "TrendingResponse [id=" + id + ", type=" + type + ", rank=" + rank + ", fromDate=" + fromDate
				+ ", toDate=" + toDate + ", time=" + time + ", offer=" + offer + ", blog=" + blog + "]";
	}

}
