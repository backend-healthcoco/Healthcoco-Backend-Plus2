package com.dpdocter.beans.v2;

import org.codehaus.jackson.map.annotate.JsonSerialize;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class GenericCode {
	
    private String id;

    private String code;
    
    private String name;

    private String strength;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

	public String getStrength() {
		return strength;
	}

	public void setStrength(String strength) {
		this.strength = strength;
	}

	@Override
	public String toString() {
		return "GenericCode [id=" + id + ", code=" + code + ", name=" + name + ", strength=" + strength + "]";
	}

}
