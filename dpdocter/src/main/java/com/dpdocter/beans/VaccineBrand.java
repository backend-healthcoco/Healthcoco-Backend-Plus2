package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class VaccineBrand extends GenericCollection {

	private String id;
	private String name;
	private String groupFrom;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroupFrom() {
		return groupFrom;
	}

	public void setGroupFrom(String groupFrom) {
		this.groupFrom = groupFrom;
	}

}
