package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class FOV extends GenericCollection {

	private String id;
	private String name;

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

	@Override
	public String toString() {
		return "FOV [id=" + id + ", name=" + name + "]";
	}

}
