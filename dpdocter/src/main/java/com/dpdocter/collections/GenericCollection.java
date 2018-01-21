package com.dpdocter.collections;

import java.util.Date;

import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;

public class GenericCollection {
	/*
	 * Note:-This date added after date edit change for doctor. this changes
	 * only in EMR Module
	 */
	// adminCreatedTime use for actual date of collection only for EMR MODULE
	@Indexed(direction = IndexDirection.DESCENDING)
	private Date adminCreatedTime;

	// createdTime use for date collection EMR MODULE
	@Indexed(direction = IndexDirection.DESCENDING)
	private Date createdTime;

	@Indexed(direction = IndexDirection.DESCENDING)
	private Date updatedTime = new Date();

	private String createdBy;

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getAdminCreatedTime() {
		return adminCreatedTime;
	}

	public void setAdminCreatedTime(Date adminCreatedTime) {
		this.adminCreatedTime = adminCreatedTime;
	}

}
