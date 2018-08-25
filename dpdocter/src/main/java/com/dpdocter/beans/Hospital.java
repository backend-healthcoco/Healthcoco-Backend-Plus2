package com.dpdocter.beans;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @author veeraj
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Hospital {
	private String id;

	private String hospitalName;

	private String hospitalPhoneNumber;

	private String hospitalImageUrl;

	private String hospitalDescription;

	private List<LocationAndAccessControl> locationsAndAccessControl = new ArrayList<LocationAndAccessControl>();

	private String hospitalUId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	public String getHospitalPhoneNumber() {
		return hospitalPhoneNumber;
	}

	public void setHospitalPhoneNumber(String hospitalPhoneNumber) {
		this.hospitalPhoneNumber = hospitalPhoneNumber;
	}

	public String getHospitalImageUrl() {
		return hospitalImageUrl;
	}

	public void setHospitalImageUrl(String hospitalImageUrl) {
		this.hospitalImageUrl = hospitalImageUrl;
	}

	public String getHospitalDescription() {
		return hospitalDescription;
	}

	public void setHospitalDescription(String hospitalDescription) {
		this.hospitalDescription = hospitalDescription;
	}

	public List<LocationAndAccessControl> getLocationsAndAccessControl() {
		return locationsAndAccessControl;
	}

	public void setLocationsAndAccessControl(List<LocationAndAccessControl> locationsAndAccessControl) {
		this.locationsAndAccessControl = locationsAndAccessControl;
	}

	public String getHospitalUId() {
		return hospitalUId;
	}

	public void setHospitalUId(String hospitalUId) {
		this.hospitalUId = hospitalUId;
	}

	@Override
	public String toString() {
		return "Hospital [id=" + id + ", hospitalName=" + hospitalName + ", hospitalPhoneNumber=" + hospitalPhoneNumber
				+ ", hospitalImageUrl=" + hospitalImageUrl + ", hospitalDescription=" + hospitalDescription
				+ ", locationsAndAccessControl=" + locationsAndAccessControl + ", hospitalUId=" + hospitalUId + "]";
	}
}
