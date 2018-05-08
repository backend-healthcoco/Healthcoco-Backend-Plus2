package com.dpdocter.beans;

import java.util.Date;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.PackageType;

public class DoctorClinicSubscription extends GenericCollection {

	private String id;

	private Date fromDate = new Date();

	private Integer durationInMonths;

	private String doctorId;

	private String locationId;

	private String packageType = PackageType.FREE.getType();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Integer getDurationInMonths() {
		return durationInMonths;
	}

	public void setDurationInMonths(Integer durationInMonths) {
		this.durationInMonths = durationInMonths;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getPackageType() {
		return packageType;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	@Override
	public String toString() {
		return "DoctorClinicSubscription [id=" + id + ", fromDate=" + fromDate + ", durationInMonths="
				+ durationInMonths + ", doctorId=" + doctorId + ", locationId=" + locationId + ", packageType="
				+ packageType + "]";
	}

}
