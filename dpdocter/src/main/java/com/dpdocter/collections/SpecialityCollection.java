package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "speciality_cl")
public class SpecialityCollection extends GenericCollection {
    @Id
    private ObjectId id;

    @Field
    private String speciality;

    @Field
    private String superSpeciality;

    @Field
    private String formattedSpeciality;

    @Field
    private String formattedSuperSpeciality;
    
    @Field
    private Boolean toShow = true;

    @Field
    private String metaTitle;

    public ObjectId getId() {
	return id;
    }

    public void setId(ObjectId id) {
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

	public String getFormattedSpeciality() {
		return formattedSpeciality;
	}

	public void setFormattedSpeciality(String formattedSpeciality) {
		this.formattedSpeciality = formattedSpeciality;
	}

	public String getFormattedSuperSpeciality() {
		return formattedSuperSpeciality;
	}

	public void setFormattedSuperSpeciality(String formattedSuperSpeciality) {
		this.formattedSuperSpeciality = formattedSuperSpeciality;
	}

	public String getMetaTitle() {
		return metaTitle;
	}

	public void setMetaTitle(String metaTitle) {
		this.metaTitle = metaTitle;
	}

	@Override
	public String toString() {
		return "SpecialityCollection [id=" + id + ", speciality=" + speciality + ", superSpeciality=" + superSpeciality
				+ ", formattedSpeciality=" + formattedSpeciality + ", formattedSuperSpeciality="
				+ formattedSuperSpeciality + ", toShow=" + toShow + ", metaTitle=" + metaTitle + "]";
	}
}
