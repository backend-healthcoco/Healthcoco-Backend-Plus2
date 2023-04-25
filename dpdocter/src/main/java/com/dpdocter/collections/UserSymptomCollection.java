package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.Symptom;

@Document(collection = "user_symptom_cl")
public class UserSymptomCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private String name;

	@Field
	private String mobile;

	@Field
	private String gender;

	@Field
	private Integer age;

	@Field
	private Symptom symtoms;

	@Field
	private Boolean discarded = false;

	@Field
	private String covid19Possibilities;

	@Field
	private String commonColdPossibilities;

	@Field
	private String fluPossibilities;

	@Field
	private String allergiesPossibilities;

	@Field
	private Address address;

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

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Symptom getSymtoms() {
		return symtoms;
	}

	public void setSymtoms(Symptom symtoms) {
		this.symtoms = symtoms;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getCovid19Possibilities() {
		return covid19Possibilities;
	}

	public void setCovid19Possibilities(String covid19Possibilities) {
		this.covid19Possibilities = covid19Possibilities;
	}

	public String getCommonColdPossibilities() {
		return commonColdPossibilities;
	}

	public void setCommonColdPossibilities(String commonColdPossibilities) {
		this.commonColdPossibilities = commonColdPossibilities;
	}

	public String getFluPossibilities() {
		return fluPossibilities;
	}

	public void setFluPossibilities(String fluPossibilities) {
		this.fluPossibilities = fluPossibilities;
	}

	public String getAllergiesPossibilities() {
		return allergiesPossibilities;
	}

	public void setAllergiesPossibilities(String allergiesPossibilities) {
		this.allergiesPossibilities = allergiesPossibilities;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "UserSymptomCollection [id=" + id + ", name=" + name + ", mobile=" + mobile + ", gender=" + gender
				+ ", age=" + age + ", symtoms=" + symtoms + "]";
	}

}
