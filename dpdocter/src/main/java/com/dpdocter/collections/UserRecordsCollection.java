package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.RecordsFile;

@Document(collection = "user_records_cl")
public class UserRecordsCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String uniqueEmrId;
	@Field
	private List<RecordsFile> recordsFiles;

	@Field
	private String recordsLabel;

	@Field
	private String explanation;

	@Indexed
	private ObjectId userId;

	@Field
	private Double fileSizeInMB = 0.0;

	@Field
	private Boolean isVisible = true;

	@Field
	private Boolean discarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
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

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
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

	public List<RecordsFile> getRecordsFiles() {
		return recordsFiles;
	}

	public void setRecordsFiles(List<RecordsFile> recordsFiles) {
		this.recordsFiles = recordsFiles;
	}

	

}
