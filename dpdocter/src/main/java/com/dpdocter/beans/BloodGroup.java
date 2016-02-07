package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class BloodGroup extends GenericCollection {

    private String id;

    private String bloodGroup;

    private String description;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getBloodGroup() {
	return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
	this.bloodGroup = bloodGroup;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    @Override
    public String toString() {
	return "BloodGroup [id=" + id + ", bloodGroup=" + bloodGroup + ", description=" + description + "]";
    }

}
