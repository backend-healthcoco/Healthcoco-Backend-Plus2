package com.dpdocter.response;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.Drug;
import com.dpdocter.beans.TemplateItem;

public class TemplateLookUpResponse {

    private ObjectId id;

    private String name;

    private ObjectId doctorId;

    private ObjectId locationId;

    private ObjectId hospitalId;

    private Boolean discarded = false;

    private List<TemplateItem> items;
    
    private List<Drug> drugs;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public ObjectId getLocationId() {
		return locationId;
	}

	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}

	public ObjectId getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public List<TemplateItem> getItems() {
		return items;
	}

	public void setItems(List<TemplateItem> items) {
		this.items = items;
	}

	public List<Drug> getDrugs() {
		return drugs;
	}

	public void setDrugs(List<Drug> drugs) {
		this.drugs = drugs;
	}

	@Override
	public String toString() {
		return "TemplateLookUpResponse [id=" + id + ", name=" + name + ", doctorId=" + doctorId + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", discarded=" + discarded + ", items=" + items + "]";
	}
    
    

	
}
