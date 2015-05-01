package com.dpdocter.beans;

public class Relations {

	private String name;
	private String relation;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	@Override
	public String toString() {
		return "Relations [name=" + name + ", relation=" + relation + "]";
	}

}
