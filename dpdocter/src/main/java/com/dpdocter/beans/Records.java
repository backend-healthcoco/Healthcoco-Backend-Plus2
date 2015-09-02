package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class Records extends GenericCollection {
    private String id;

    private String recordsUrl;

    private String recordsLable;

    private String recordsType;

    private String description;

    private boolean inHistory = false;

    private Boolean discarded = false;

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

    public boolean isInHistory() {
	return inHistory;
    }

    public void setInHistory(boolean inHistory) {
	this.inHistory = inHistory;
    }

    public Boolean getDiscarded() {
	return discarded;
    }

    public void setDiscarded(Boolean discarded) {
	this.discarded = discarded;
    }

    @Override
    public String toString() {
	return "Records [id=" + id + ", recordsUrl=" + recordsUrl + ", recordsLable=" + recordsLable + ", recordsType=" + recordsType + ", description="
		+ description + ", inHistory=" + inHistory + ", discarded=" + discarded + "]";
    }
}
