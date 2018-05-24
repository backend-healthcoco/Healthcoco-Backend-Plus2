package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;

public class LabPrintSetting extends GenericCollection {

	private String id;

	private String locationId;

	private String hospitalId;

	private String doctorId;

	private LabPrintContentSetup headerSetup;

	private LabPrintContentSetup footerSetup;

	private Boolean discarded = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	@Override
	public String toString() {
		return "LabPrintSetting [id=" + id + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", headerSetup=" + headerSetup + ", footerSetup=" + footerSetup + ", discarded=" + discarded + "]";
	}

}
