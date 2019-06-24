package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class Testimonial extends GenericCollection {

	private String id;
	private String name;
	private String planId;
	private Age age;
	private List<TestimonialMedia> media;
	private String para;

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

	public List<TestimonialMedia> getMedia() {
		return media;
	}

	public void setMedia(List<TestimonialMedia> media) {
		this.media = media;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getPara() {
		return para;
	}

	public void setPara(String para) {
		this.para = para;
	}

	@Override
	public String toString() {
		return "Testimonial [id=" + id + ", name=" + name + ", planId=" + planId + ", age=" + age + ", media=" + media
				+ "]";
	}

}
