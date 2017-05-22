package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "collection_boy_lab_association_cl")
public class CollectionBoyLabAssociationCollection extends GenericCollection {

	@Id
	private String id;
	@Field
	private String collectionBoyId;
	@Field
	private String parentLabId;
	@Field
	private String daughterLabId;
	@Field
	private Boolean isActive;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCollectionBoyId() {
		return collectionBoyId;
	}

	public void setCollectionBoyId(String collectionBoyId) {
		this.collectionBoyId = collectionBoyId;
	}

	public String getParentLabId() {
		return parentLabId;
	}

	public void setParentLabId(String parentLabId) {
		this.parentLabId = parentLabId;
	}

	public String getDaughterLabId() {
		return daughterLabId;
	}

	public void setDaughterLabId(String daughterLabId) {
		this.daughterLabId = daughterLabId;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

}
