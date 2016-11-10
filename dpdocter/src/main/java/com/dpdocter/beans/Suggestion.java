package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.SuggestionState;
import com.dpdocter.enums.Type;

public class Suggestion extends GenericCollection {
	private String id;
	private Type suggetionType;
	private String name;
	private String locality;
	private String city;
	private String additionalInfo;
	private SuggestionState state = SuggestionState.PENDING;
	private String userId;

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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
