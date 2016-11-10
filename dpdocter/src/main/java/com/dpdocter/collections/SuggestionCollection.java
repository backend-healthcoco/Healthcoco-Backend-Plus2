package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.enums.SuggestionState;
import com.dpdocter.enums.Type;

@Document(collection = "suggestion_cl")
public class SuggestionCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private Type suggetionType;
	@Field
	private String name;
	@Field
	private String locality;
	@Field
	private String city;
	@Field
	private String additionalInfo;
	@Field
	private SuggestionState state = SuggestionState.PENDING;
	@Field
	private ObjectId userId;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Type getSuggetionType() {
		return suggetionType;
	}

	public void setSuggetionType(Type suggetionType) {
		this.suggetionType = suggetionType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public SuggestionState getState() {
		return state;
	}

	public void setState(SuggestionState state) {
		this.state = state;
	}

	public ObjectId getUserId() {
		return userId;
	}

	public void setUserId(ObjectId userId) {
		this.userId = userId;
	}

}
