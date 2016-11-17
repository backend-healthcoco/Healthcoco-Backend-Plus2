package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class GenericCode extends GenericCollection{
	
    private String id;

    private String code;
    
    private String name;

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

    @Override
    public String toString() {
	return "GenericCode [id=" + id + ", code=" + code + ", name=" + name + "]";
    }
}
