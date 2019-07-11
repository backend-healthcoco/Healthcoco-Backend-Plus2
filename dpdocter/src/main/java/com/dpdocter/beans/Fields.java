package com.dpdocter.beans;

public class Fields {

	private String key;
	
	private String value;

	private String type;
	
	private String name;

	private String id;
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Fields [key=" + key + ", value=" + value + ", type=" + type + ", name=" + name + ", id=" + id + "]";
	}
}
