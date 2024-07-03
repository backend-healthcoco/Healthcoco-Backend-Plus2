package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.Age;
import com.dpdocter.beans.TestimonialMedia;

@Document(collection = "testimonial_cl")
public class TestimonialCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private String name;
	@Field
	private ObjectId planId;
	@Field
	private Age age;
	@Field
	private List<TestimonialMedia> media;
	@Field
	private String para;
	@Field
	private Boolean discarded = false;

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

	public ObjectId getPlanId() {
		return planId;
	}

	public void setPlanId(ObjectId planId) {
		this.planId = planId;
	}

	public Age getAge() {
		return age;
	}

	public void setAge(Age age) {
		this.age = age;
	}

	public List<TestimonialMedia> getMedia() {
		return media;
	}

	public void setMedia(List<TestimonialMedia> media) {
		this.media = media;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getPara() {
		return para;
	}

	public void setPara(String para) {
		this.para = para;
	}

	@Override
	public String toString() {
		return "TestimonialCollection [id=" + id + ", name=" + name + ", planId=" + planId + ", age=" + age + ", media="
				+ media + "]";
	}

}
