package com.dpdocter.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * @author veeraj
 */
public class Hospital {
	private String id;
	private String hospitalName;
	private String hospitalPhoneNumber;
	private String hospitalImageUrl;
	private String hospitalDescription;
	private List<Locations> locations = new ArrayList<Locations>();

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

	public List<Locations> getLocations() {
		return locations;
	}

	public void setLocations(List<Locations> locations) {
		this.locations = locations;
	}

	@Override
	public String toString() {
		return "Hospital [id=" + id + ", hospitalName=" + hospitalName + ", hospitalPhoneNumber=" + hospitalPhoneNumber + ", hospitalImageUrl="
				+ hospitalImageUrl + ", hospitalDescription=" + hospitalDescription + ", locations=" + locations + "]";
	}

}
