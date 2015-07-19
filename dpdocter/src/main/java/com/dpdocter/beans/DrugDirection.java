package com.dpdocter.beans;

public class DrugDirection {
	private String id;

	private String direction;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	@Override
	public String toString() {
		return "DrugDirection [id=" + id + ", direction=" + direction + "]";
	}

}
