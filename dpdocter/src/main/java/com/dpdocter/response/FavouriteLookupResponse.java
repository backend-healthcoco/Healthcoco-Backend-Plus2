package com.dpdocter.response;

import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.LocaleCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.UserCollection;

public class FavouriteLookupResponse {

	private String resourceId;

	private LocaleCollection pharmacy;

	private LocationCollection lab;

	private UserCollection user;

	private DoctorCollection doctor;

	private DoctorClinicProfileCollection clinicProfileCollection;

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public LocaleCollection getPharmacy() {
		return pharmacy;
	}

	public void setPharmacy(LocaleCollection pharmacy) {
		this.pharmacy = pharmacy;
	}

	public LocationCollection getLab() {
		return lab;
	}

	public void setLab(LocationCollection lab) {
		this.lab = lab;
	}

	public UserCollection getUser() {
		return user;
	}

	public void setUser(UserCollection user) {
		this.user = user;
	}

	public DoctorCollection getDoctor() {
		return doctor;
	}

	public void setDoctor(DoctorCollection doctor) {
		this.doctor = doctor;
	}

	public DoctorClinicProfileCollection getClinicProfileCollection() {
		return clinicProfileCollection;
	}

	public void setClinicProfileCollection(DoctorClinicProfileCollection clinicProfileCollection) {
		this.clinicProfileCollection = clinicProfileCollection;
	}

	@Override
	public String toString() {
		return "FavouriteLookupResponse [resourceId=" + resourceId + ", pharmacy=" + pharmacy + ", lab=" + lab
				+ ", user=" + user + ", doctor=" + doctor + ", clinicProfileCollection=" + clinicProfileCollection
				+ "]";
	}

}
