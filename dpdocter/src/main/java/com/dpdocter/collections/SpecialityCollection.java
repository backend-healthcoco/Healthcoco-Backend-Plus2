package com.dpdocter.collections;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "speciality_cl")
public class SpecialityCollection extends GenericCollection {
    @Id
    private String id;

    @Field
    private String speciality;

    @Field
    private String superSpeciality;

    @Field
    private Boolean toShow = true;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public String getSpeciality() {
	return speciality;
    }

    public void setSpeciality(String speciality) {
	this.speciality = speciality;
    }

    public String getSuperSpeciality() {
	return superSpeciality;
    }

    public void setSuperSpeciality(String superSpeciality) {
	this.superSpeciality = superSpeciality;
    }

	public Boolean getToShow() {
		return toShow;
	}

	public void setToShow(Boolean toShow) {
		this.toShow = toShow;
	}

	@Override
	public String toString() {
		return "SpecialityCollection [id=" + id + ", speciality=" + speciality + ", superSpeciality=" + superSpeciality
				+ ", toShow=" + toShow + "]";
	}

}
