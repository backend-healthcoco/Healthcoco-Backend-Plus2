package com.dpdocter.request;

import com.dpdocter.collections.GenericCollection;

import common.util.web.JacksonUtil;

public class LabReportsAddRequest extends GenericCollection {

	private String locationId;

	private String hospitalId;

	private Boolean discarded = false;

	private String uploadedByLocationId;

	private String uploadedByHospitalId;

	private String labTestSampleId;

	private String recordsState;

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getUploadedByLocationId() {
		return uploadedByLocationId;
	}

	public void setUploadedByLocationId(String uploadedByLocationId) {
		this.uploadedByLocationId = uploadedByLocationId;
	}

	public String getUploadedByHospitalId() {
		return uploadedByHospitalId;
	}

	public void setUploadedByHospitalId(String uploadedByHospitalId) {
		this.uploadedByHospitalId = uploadedByHospitalId;
	}

	public String getLabTestSampleId() {
		return labTestSampleId;
	}

	public void setLabTestSampleId(String labTestSampleId) {
		this.labTestSampleId = labTestSampleId;
	}

	public String getRecordsState() {
		return recordsState;
	}

	public void setRecordsState(String recordsState) {
		this.recordsState = recordsState;
	}

}
