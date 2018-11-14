package com.dpdocter.collections;

import org.bson.types.ObjectId;

public class MasterBabyImmunizationCollection {

	private ObjectId id;
	private String name;
	private String vaccine;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVaccine() {
		return vaccine;
	}

	public void setVaccine(String vaccine) {
		this.vaccine = vaccine;
	}

	@Override
	public String toString() {
		return "MasterBabyImmunizationCollection [id=" + id + ", name=" + name + ", vaccine=" + vaccine + "]";
	}

}
