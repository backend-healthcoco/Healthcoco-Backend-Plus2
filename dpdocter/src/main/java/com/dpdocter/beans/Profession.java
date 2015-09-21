package com.dpdocter.beans;

public class Profession {

    private String id;

    private String profession;

    private String description;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getProfession() {
	return profession;
    }

    public void setProfession(String profession) {
	this.profession = profession;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    @Override
    public String toString() {
	return "Profession [id=" + id + ", profession=" + profession + ", description=" + description + "]";
    }
}
