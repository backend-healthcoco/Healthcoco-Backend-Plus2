package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class Records extends GenericCollection {
    private String id;

    private String recordsUrl;

    private String recordsLable;

    private String recordsType;

    private String description;

    private Long createdDate;

    private boolean inHistory = false;

    private boolean deleted;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getRecordsUrl() {
	return recordsUrl;
    }

    public void setRecordsUrl(String recordsUrl) {
	this.recordsUrl = recordsUrl;
    }

    public String getRecordsLable() {
	return recordsLable;
    }

    public void setRecordsLable(String recordsLable) {
	this.recordsLable = recordsLable;
    }

    public String getRecordsType() {
	return recordsType;
    }

    public void setRecordsType(String recordsType) {
	this.recordsType = recordsType;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public Long getCreatedDate() {
	return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
	this.createdDate = createdDate;
    }

    public boolean isInHistory() {
	return inHistory;
    }

    public void setInHistory(boolean inHistory) {
	this.inHistory = inHistory;
    }

    public boolean isDeleted() {
	return deleted;
    }

    public void setDeleted(boolean deleted) {
	this.deleted = deleted;
    }

    @Override
    public String toString() {
	return "Records [id=" + id + ", recordsUrl=" + recordsUrl + ", recordsLable=" + recordsLable + ", recordsType=" + recordsType + ", description="
		+ description + ", createdDate=" + createdDate + ", inHistory=" + inHistory + ", deleted=" + deleted + "]";
    }

}
