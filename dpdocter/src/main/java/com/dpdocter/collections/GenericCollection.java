package com.dpdocter.collections;

import java.util.Date;

public class GenericCollection {
    private Date createdTime;

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

}
