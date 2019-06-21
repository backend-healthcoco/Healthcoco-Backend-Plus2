package com.dpdocter.collections;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.dpdocter.beans.LabPrintContentSetup;

@Document(collection = "lab_print_set_up_cl")
public class LabPrintSettingCollection extends GenericCollection {

	@Id
	private ObjectId id;

	@Field
	private ObjectId doctorId;

	@Field
	private ObjectId locationId;

	@Field
	private ObjectId hospitalId;

	@Field
	private LabPrintContentSetup headerSetup;

	@Field
	private LabPrintContentSetup footerSetup;

	public ObjectId getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}

	public LabPrintContentSetup getHeaderSetup() {
		return headerSetup;
	}

	public void setHeaderSetup(LabPrintContentSetup headerSetup) {
		this.headerSetup = headerSetup;
	}

	public LabPrintContentSetup getFooterSetup() {
		return footerSetup;
	}

	public void setFooterSetup(LabPrintContentSetup footerSetup) {
		this.footerSetup = footerSetup;
	}

	@Field
	private Boolean discarded = false;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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
}
