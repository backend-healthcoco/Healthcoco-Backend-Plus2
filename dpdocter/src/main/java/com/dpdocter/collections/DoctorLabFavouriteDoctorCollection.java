package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "doctor_lab_fav_doctor_cl")
public class DoctorLabFavouriteDoctorCollection extends GenericCollection {

	@Id
	private ObjectId id;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId favouriteDoctorId;
	@Field
	private ObjectId favouriteLocationId;
	@Field
	private ObjectId favouriteHospitalId;
	@Field
	private Boolean discarded = false;

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

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

	public ObjectId getFavouriteDoctorId() {
		return favouriteDoctorId;
	}

	public void setFavouriteDoctorId(ObjectId favouriteDoctorId) {
		this.favouriteDoctorId = favouriteDoctorId;
	}

	public ObjectId getFavouriteLocationId() {
		return favouriteLocationId;
	}

	public void setFavouriteLocationId(ObjectId favouriteLocationId) {
		this.favouriteLocationId = favouriteLocationId;
	}

	public ObjectId getFavouriteHospitalId() {
		return favouriteHospitalId;
	}

	public void setFavouriteHospitalId(ObjectId favouriteHospitalId) {
		this.favouriteHospitalId = favouriteHospitalId;
	}

	@Override
	public String toString() {
		return "DoctorLabFavouriteDoctorCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", favouriteDoctorId=" + favouriteDoctorId + ", favouriteLocationId="
				+ favouriteLocationId + ", favouriteHospitalId=" + favouriteHospitalId + ", discarded=" + discarded
				+ "]";
	}

}
