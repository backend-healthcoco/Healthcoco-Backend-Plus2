package com.dpdocter.beans;

public class Records {
	private String id;

	private String recordsUrl;

	private String recordsLable;

	private String description;

	private Long createdDate;

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

	@Override
	public String toString() {
		return "Records [id=" + id + ", recordsUrl=" + recordsUrl + ", recordsLable=" + recordsLable + ", description=" + description + ", createdDate="
				+ createdDate + "]";
	}

}
