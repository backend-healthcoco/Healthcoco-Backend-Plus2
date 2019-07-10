package com.dpdocter.elasticsearch.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.Age;

@Document(indexName = "collection_boy_in" , type = "collection_boy")
public class ESCollectionBoyDocument {

	@Id
	private String id;
	@Field(type = FieldType.Text)
	private String name;
	@Field(type = FieldType.Nested)
	private Age age;
	@Field(type = FieldType.Text)
	private String gender;
	@Field(type = FieldType.Nested)
	private Address address;
	@Field(type = FieldType.Text)
	private String mobileNumber;

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

	public Age getAge() {
		return age;
	}

	public void setAge(Age age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	@Override
	public String toString() {
		return "ESCollectionBoyDocument [id=" + id + ", name=" + name + ", age=" + age + ", gender=" + gender
				+ ", address=" + address + ", mobileNumber=" + mobileNumber + "]";
	}

}
