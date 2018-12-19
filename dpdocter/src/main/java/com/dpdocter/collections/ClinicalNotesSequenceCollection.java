package com.dpdocter.collections;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.Fields;

public class ClinicalNotesSequenceCollection extends GenericCollection {

	private ObjectId id;
	private ObjectId doctorId;
	private ObjectId locationId;
	private ObjectId hospitalId;
	private List<Fields> fields;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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

	public List<Fields> getFields() {
		return fields;
	}

	public void setFields(List<Fields> fields) {
		this.fields = fields;
	}

	@Override
	public String toString() {
		return "ClinicalNotesSequenceCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", fields=" + fields + "]";
	}

}
