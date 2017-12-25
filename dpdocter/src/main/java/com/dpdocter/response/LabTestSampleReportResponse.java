package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.Location;
import com.dpdocter.beans.RateCardTestAssociation;
import com.dpdocter.collections.GenericCollection;

public class LabTestSampleReportResponse extends GenericCollection {
	private String id;
	private String sampleType;
	private String daughterLabLocationId;
	private String parentLabLocationId;
	private Location daughterLabLocation;
	private Location parentLabLocation;
	private String doctorId;
	private RateCardTestAssociation rateCardTestAssociation;
	private Boolean isUrgent;
	private Long urgentTime;
	private Boolean isCollected = false;
	private Boolean isCollectedAtLab = false;
	private Boolean isHardCopyRequired;
	private Boolean isHardCopyGiven;
	private String status;
	private String sampleId;
	private String serialNumber;
	private Boolean isCompleted = false;
	private List<ImageURLResponse> labReports;
	private String uploadedByDoctorId;
	private String uploadedByLocationId;
	private String uploadedByHospitalId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSampleType() {
		return sampleType;
	}

	public void setSampleType(String sampleType) {
		this.sampleType = sampleType;
	}

	public String getDaughterLabLocationId() {
		return daughterLabLocationId;
	}

	public void setDaughterLabLocationId(String daughterLabLocationId) {
		this.daughterLabLocationId = daughterLabLocationId;
	}

	public String getParentLabLocationId() {
		return parentLabLocationId;
	}

	public void setParentLabLocationId(String parentLabLocationId) {
		this.parentLabLocationId = parentLabLocationId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public Location getDaughterLabLocation() {
		return daughterLabLocation;
	}

	public void setDaughterLabLocation(Location daughterLabLocation) {
		this.daughterLabLocation = daughterLabLocation;
	}

	public Location getParentLabLocation() {
		return parentLabLocation;
	}

	public void setParentLabLocation(Location parentLabLocation) {
		this.parentLabLocation = parentLabLocation;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public RateCardTestAssociation getRateCardTestAssociation() {
		return rateCardTestAssociation;
	}

	public void setRateCardTestAssociation(RateCardTestAssociation rateCardTestAssociation) {
		this.rateCardTestAssociation = rateCardTestAssociation;
	}

	public Boolean getIsUrgent() {
		return isUrgent;
	}

	public void setIsUrgent(Boolean isUrgent) {
		this.isUrgent = isUrgent;
	}

	public Long getUrgentTime() {
		return urgentTime;
	}

	public void setUrgentTime(Long urgentTime) {
		this.urgentTime = urgentTime;
	}

	public Boolean getIsCollected() {
		return isCollected;
	}

	public void setIsCollected(Boolean isCollected) {
		this.isCollected = isCollected;
	}

	public Boolean getIsCollectedAtLab() {
		return isCollectedAtLab;
	}

	public void setIsCollectedAtLab(Boolean isCollectedAtLab) {
		this.isCollectedAtLab = isCollectedAtLab;
	}

	public Boolean getIsHardCopyRequired() {
		return isHardCopyRequired;
	}

	public void setIsHardCopyRequired(Boolean isHardCopyRequired) {
		this.isHardCopyRequired = isHardCopyRequired;
	}

	public Boolean getIsHardCopyGiven() {
		return isHardCopyGiven;
	}

	public void setIsHardCopyGiven(Boolean isHardCopyGiven) {
		this.isHardCopyGiven = isHardCopyGiven;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSampleId() {
		return sampleId;
	}

	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Boolean getIsCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public List<ImageURLResponse> getLabReports() {
		return labReports;
	}

	public void setLabReports(List<ImageURLResponse> labReports) {
		this.labReports = labReports;
	}

	public String getUploadedByDoctorId() {
		return uploadedByDoctorId;
	}

	public void setUploadedByDoctorId(String uploadedByDoctorId) {
		this.uploadedByDoctorId = uploadedByDoctorId;
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

}
