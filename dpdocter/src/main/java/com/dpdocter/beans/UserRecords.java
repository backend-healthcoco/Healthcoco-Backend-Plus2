package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class UserRecords extends GenericCollection {

	private String id;

	private String uniqueEmrId;

	private List<RecordsFile> recordsFiles;

	private String recordsLabel;

	private String recordsType;

	private String explanation;

	private String userId;

	private Boolean isVisible = true;

	private Boolean discarded = false;

	public List<RecordsFile> getRecordsFiles() {
		return recordsFiles;
	}

	public void setRecordsFiles(List<RecordsFile> recordsFiles) {
		this.recordsFiles = recordsFiles;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUniqueEmrId() {
		return uniqueEmrId;
	}

	public void setUniqueEmrId(String uniqueEmrId) {
		this.uniqueEmrId = uniqueEmrId;
	}

	public String getRecordsLabel() {
		return recordsLabel;
	}

	public void setRecordsLabel(String recordsLabel) {
		this.recordsLabel = recordsLabel;
	}

	public String getRecordsType() {
		return recordsType;
	}

	public void setRecordsType(String recordsType) {
		this.recordsType = recordsType;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Double getFileSizeInMB() {
		return fileSizeInMB;
	}

	public void setFileSizeInMB(Double fileSizeInMB) {
		this.fileSizeInMB = fileSizeInMB;
	}

	public Boolean getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(Boolean isVisible) {
		this.isVisible = isVisible;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	@Override
	public String toString() {
		return "UserRecords [id=" + id + ", uniqueEmrId=" + uniqueEmrId + ", recordsUrl=" + recordsUrl
				+ ", recordsLabel=" + recordsLabel + ", recordsType=" + recordsType + ", explanation=" + explanation
				+ ", userId=" + userId + ", fileSizeInMB=" + fileSizeInMB + ", isVisible=" + isVisible + ", discarded="
				+ discarded + "]";
	}
}
