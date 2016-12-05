package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Vaccination extends GenericCollection{

	private Age age;
	private List<Vaccine> vaccines;

	public Age getAge() {
		return age;
	}

	public void setAge(Age age) {
		this.age = age;
	}

	public List<Vaccine> getVaccines() {
		return vaccines;
	}

	public void setVaccines(List<Vaccine> vaccines) {
		this.vaccines = vaccines;
	}

	@Override
	public String toString() {
		return "Immunisation [age=" + age + ", vaccines=" + vaccines + "]";
	}

}
