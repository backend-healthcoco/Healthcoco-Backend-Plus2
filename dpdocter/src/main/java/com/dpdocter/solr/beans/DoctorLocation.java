package com.dpdocter.solr.beans;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Transient;
import org.springframework.data.solr.core.geo.GeoLocation;

import com.dpdocter.beans.WorkingHours;
import com.dpdocter.beans.WorkingSchedule;
import com.dpdocter.enums.Day;

public class DoctorLocation {
    @Field
    private String locationId;

    @Field
    private String hospitalId;

    @Field
    private String locationName;

    @Field
    private String country;

    @Field
    private String state;

    @Field
    private String city;

    @Field
    private String postalCode;

    @Field
    private String websiteUrl;

    @Field
    private GeoLocation geoLocation;

    @Field
    private Double latitude;

    @Field
    private Double longitude;

    @Field
    private String landmarkDetails;

    @Field
    private String locationEmailAddress;

    @Field
    private String streetAddress;

    @Field
    private String locality;

    @Field
    private String clinicNumber;

    @Field
    private List<String> alternateClinicNumbers;

    @Field
    private List<String> specialization;

    @Field
    private Boolean isClinic = true;

    @Field
    private Boolean isLab = false;

    @Field
    private Boolean isOnlineReportsAvailable = false;

    @Field
    private Boolean isNABLAccredited = false;

    @Field
    private Boolean isHomeServiceAvailable = false;

    @Field
    private List<String> images;

    @Field
    private String logoUrl;

    @Field
    private Integer noOfReviews = 0;

    @Field
    private Integer noOfRecommenations = 0;

    @Field
    private String locationUId;
    
//    @Field
//    private List<WorkingSchedule> clinicWorkingSchedules;
//
//    @Field
//    private List<Integer> clinicMondayWorkingHoursFromTime;
//    
//    @Field
//    private List<Integer> clinicMondayWorkingHoursToTime;
//    
//    @Field
//    private List<Integer> clinicTuesdayWorkingHoursFromTime;
//    
//    @Field
//    private List<Integer> clinicTuesdayWorkingHoursToTime;
//    
//    @Field
//    private List<Integer> clinicWednessdayWorkingHoursFromTime;
//    
//    @Field
//    private List<Integer> clinicWednessdayWorkingHoursToTime;
//    
//    @Field
//    private List<Integer> clinicThursdayWorkingHoursFromTime;
//    
//    @Field
//    private List<Integer> clinicThursdayWorkingHoursToTime;
//    
//    @Field
//    private List<Integer> clinicFridayWorkingHoursFromTime;
//    
//    @Field
//    private List<Integer> clinicFridayWorkingHoursToTime;
//    
//    @Field
//    private List<Integer> clinicSaturdayWorkingHoursFromTime;
//    
//    @Field
//    private List<Integer> clinicSaturdayWorkingHoursToTime;
//    
//    @Field
//    private List<Integer> clinicSundayWorkingHoursFromTime;
//    
//    @Field
//    private List<Integer> clinicSundayWorkingHoursToTime;

    public String getLocationName() {
	return locationName;
    }

    public void setLocationName(String locationName) {
	this.locationName = locationName;
    }

    public String getCountry() {
	return country;
    }

    public void setCountry(String country) {
	this.country = country;
    }

    public String getState() {
	return state;
    }

    public void setState(String state) {
	this.state = state;
    }

    public String getCity() {
	return city;
    }

    public void setCity(String city) {
	this.city = city;
    }

    public String getPostalCode() {
	return postalCode;
    }

    public void setPostalCode(String postalCode) {
	this.postalCode = postalCode;
    }

    public String getWebsiteUrl() {
	return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
	this.websiteUrl = websiteUrl;
    }

    public Double getLatitude() {
	return latitude;
    }

    public void setLatitude(Double latitude) {
	this.latitude = latitude;
    }

    public Double getLongitude() {
	return longitude;
    }

    public void setLongitude(Double longitude) {
	this.longitude = longitude;
    }

    public String getLandmarkDetails() {
	return landmarkDetails;
    }

    public void setLandmarkDetails(String landmarkDetails) {
	this.landmarkDetails = landmarkDetails;
    }

    public String getLocationEmailAddress() {
	return locationEmailAddress;
    }

    public void setLocationEmailAddress(String locationEmailAddress) {
	this.locationEmailAddress = locationEmailAddress;
    }

    public String getStreetAddress() {
	return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
	this.streetAddress = streetAddress;
    }

    public String getLocality() {
	return locality;
    }

    public void setLocality(String locality) {
	this.locality = locality;
    }

	public String getClinicNumber() {
		return clinicNumber;
	}

	public void setClinicNumber(String clinicNumber) {
		this.clinicNumber = clinicNumber;
	}

	public List<String> getAlternateClinicNumbers() {
		return alternateClinicNumbers;
	}

	public void setAlternateClinicNumbers(List<String> alternateClinicNumbers) {
		this.alternateClinicNumbers = alternateClinicNumbers;
	}

	public String getLocationId() {
	return locationId;
    }

    public void setLocationId(String locationId) {
	this.locationId = locationId;
    }

    public List<String> getSpecialization() {
	return specialization;
    }

    public void setSpecialization(List<String> specialization) {
	this.specialization = specialization;
    }

    public GeoLocation getGeoLocation() {
	return geoLocation;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
	this.geoLocation = geoLocation;
    }

    public Boolean getIsLab() {
	return isLab;
    }

    public void setIsLab(Boolean isLab) {
	this.isLab = isLab;
    }

    public Boolean getIsOnlineReportsAvailable() {
	return isOnlineReportsAvailable;
    }

    public void setIsOnlineReportsAvailable(Boolean isOnlineReportsAvailable) {
	this.isOnlineReportsAvailable = isOnlineReportsAvailable;
    }

    public Boolean getIsNABLAccredited() {
	return isNABLAccredited;
    }

    public void setIsNABLAccredited(Boolean isNABLAccredited) {
	this.isNABLAccredited = isNABLAccredited;
    }

    public Boolean getIsHomeServiceAvailable() {
	return isHomeServiceAvailable;
    }

    public void setIsHomeServiceAvailable(Boolean isHomeServiceAvailable) {
	this.isHomeServiceAvailable = isHomeServiceAvailable;
    }

    public String getHospitalId() {
	return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
	this.hospitalId = hospitalId;
    }

    public List<String> getImages() {
	return images;
    }

    public void setImages(List<String> images) {
	this.images = images;
    }

    public String getLogoUrl() {
	return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
	this.logoUrl = logoUrl;
    }

    public Integer getNoOfReviews() {
	return noOfReviews;
    }

    public void setNoOfReviews(Integer noOfReviews) {
	this.noOfReviews = noOfReviews;
    }

    public Integer getNoOfRecommenations() {
	return noOfRecommenations;
    }

    public void setNoOfRecommenations(Integer noOfRecommenations) {
	this.noOfRecommenations = noOfRecommenations;
    }

    public Boolean getIsClinic() {
	return isClinic;
    }

    public void setIsClinic(Boolean isClinic) {
	this.isClinic = isClinic;
    }

	public String getLocationUId() {
		return locationUId;
	}

	public void setLocationUId(String locationUId) {
		this.locationUId = locationUId;
	}

//	public List<WorkingSchedule> getClinicWorkingSchedules() {
//		return clinicWorkingSchedules;
//	}
//
//	public void setClinicWorkingSchedules(List<WorkingSchedule> clinicWorkingSchedules) {
//		this.clinicWorkingSchedules = clinicWorkingSchedules;
//		if(this.clinicWorkingSchedules != null && !this.clinicWorkingSchedules.isEmpty()){
//			for(WorkingSchedule solrWorkingSchedule : this.clinicWorkingSchedules){
//				List<Integer> fromTime = new  ArrayList<Integer>(), toTime = new ArrayList<Integer>();
//				if(solrWorkingSchedule.getWorkingHours() != null && !solrWorkingSchedule.getWorkingHours().isEmpty())
//				for(WorkingHours workingHours: solrWorkingSchedule.getWorkingHours()){
//						fromTime.add(workingHours.getFromTime());
//						toTime.add(workingHours.getToTime());
//				}
//				if(solrWorkingSchedule.getWorkingDay().getDay().equalsIgnoreCase(Day.MONDAY.getDay())){
//					this.clinicMondayWorkingHoursFromTime = fromTime;
//					this.clinicMondayWorkingHoursToTime = toTime;
//				}
//				else if(solrWorkingSchedule.getWorkingDay().getDay().equalsIgnoreCase(Day.TUESDAY.getDay())){
//					this.clinicTuesdayWorkingHoursFromTime = fromTime;
//					this.clinicTuesdayWorkingHoursToTime = toTime;
//				}
//				else if(solrWorkingSchedule.getWorkingDay().getDay().equalsIgnoreCase(Day.WEDNESDAY.getDay())){
//					this.clinicWednessdayWorkingHoursFromTime = fromTime;
//					this.clinicWednessdayWorkingHoursToTime = toTime;
//				}
//				else if(solrWorkingSchedule.getWorkingDay().getDay().equalsIgnoreCase(Day.THURSDAY.getDay())){
//					this.clinicThursdayWorkingHoursFromTime = fromTime;
//					this.clinicThursdayWorkingHoursToTime = toTime;
//				}
//				else if(solrWorkingSchedule.getWorkingDay().getDay().equalsIgnoreCase(Day.FRIDAY.getDay())){
//					this.clinicFridayWorkingHoursFromTime = fromTime;
//					this.clinicFridayWorkingHoursToTime = toTime;
//				}
//				else if(solrWorkingSchedule.getWorkingDay().getDay().equalsIgnoreCase(Day.SATURDAY.getDay())){
//					this.clinicSaturdayWorkingHoursFromTime = fromTime;
//					this.clinicSaturdayWorkingHoursToTime = toTime;
//				}
//				else if(solrWorkingSchedule.getWorkingDay().getDay().equalsIgnoreCase(Day.SUNDAY.getDay())){
//					this.clinicSundayWorkingHoursFromTime = fromTime;
//					this.clinicSundayWorkingHoursToTime = toTime;
//				}
//			}
//		}
//
//	}
//
//	public List<Integer> getClinicMondayWorkingHoursFromTime() {
//		return clinicMondayWorkingHoursFromTime;
//	}
//
//	public void setClinicMondayWorkingHoursFromTime(List<Integer> clinicMondayWorkingHoursFromTime) {
//		this.clinicMondayWorkingHoursFromTime = clinicMondayWorkingHoursFromTime;
//	}
//
//	public List<Integer> getClinicMondayWorkingHoursToTime() {
//		return clinicMondayWorkingHoursToTime;
//	}
//
//	public void setClinicMondayWorkingHoursToTime(List<Integer> clinicMondayWorkingHoursToTime) {
//		this.clinicMondayWorkingHoursToTime = clinicMondayWorkingHoursToTime;
//	}
//
//	public List<Integer> getClinicTuesdayWorkingHoursFromTime() {
//		return clinicTuesdayWorkingHoursFromTime;
//	}
//
//	public void setClinicTuesdayWorkingHoursFromTime(List<Integer> clinicTuesdayWorkingHoursFromTime) {
//		this.clinicTuesdayWorkingHoursFromTime = clinicTuesdayWorkingHoursFromTime;
//	}
//
//	public List<Integer> getClinicTuesdayWorkingHoursToTime() {
//		return clinicTuesdayWorkingHoursToTime;
//	}
//
//	public void setClinicTuesdayWorkingHoursToTime(List<Integer> clinicTuesdayWorkingHoursToTime) {
//		this.clinicTuesdayWorkingHoursToTime = clinicTuesdayWorkingHoursToTime;
//	}
//
//	public List<Integer> getClinicWednessdayWorkingHoursFromTime() {
//		return clinicWednessdayWorkingHoursFromTime;
//	}
//
//	public void setClinicWednessdayWorkingHoursFromTime(List<Integer> clinicWednessdayWorkingHoursFromTime) {
//		this.clinicWednessdayWorkingHoursFromTime = clinicWednessdayWorkingHoursFromTime;
//	}
//
//	public List<Integer> getClinicWednessdayWorkingHoursToTime() {
//		return clinicWednessdayWorkingHoursToTime;
//	}
//
//	public void setClinicWednessdayWorkingHoursToTime(List<Integer> clinicWednessdayWorkingHoursToTime) {
//		this.clinicWednessdayWorkingHoursToTime = clinicWednessdayWorkingHoursToTime;
//	}
//
//	public List<Integer> getClinicThursdayWorkingHoursFromTime() {
//		return clinicThursdayWorkingHoursFromTime;
//	}
//
//	public void setClinicThursdayWorkingHoursFromTime(List<Integer> clinicThursdayWorkingHoursFromTime) {
//		this.clinicThursdayWorkingHoursFromTime = clinicThursdayWorkingHoursFromTime;
//	}
//
//	public List<Integer> getClinicThursdayWorkingHoursToTime() {
//		return clinicThursdayWorkingHoursToTime;
//	}
//
//	public void setClinicThursdayWorkingHoursToTime(List<Integer> clinicThursdayWorkingHoursToTime) {
//		this.clinicThursdayWorkingHoursToTime = clinicThursdayWorkingHoursToTime;
//	}
//
//	public List<Integer> getClinicFridayWorkingHoursFromTime() {
//		return clinicFridayWorkingHoursFromTime;
//	}
//
//	public void setClinicFridayWorkingHoursFromTime(List<Integer> clinicFridayWorkingHoursFromTime) {
//		this.clinicFridayWorkingHoursFromTime = clinicFridayWorkingHoursFromTime;
//	}
//
//	public List<Integer> getClinicFridayWorkingHoursToTime() {
//		return clinicFridayWorkingHoursToTime;
//	}
//
//	public void setClinicFridayWorkingHoursToTime(List<Integer> clinicFridayWorkingHoursToTime) {
//		this.clinicFridayWorkingHoursToTime = clinicFridayWorkingHoursToTime;
//	}
//
//	public List<Integer> getClinicSaturdayWorkingHoursFromTime() {
//		return clinicSaturdayWorkingHoursFromTime;
//	}
//
//	public void setClinicSaturdayWorkingHoursFromTime(List<Integer> clinicSaturdayWorkingHoursFromTime) {
//		this.clinicSaturdayWorkingHoursFromTime = clinicSaturdayWorkingHoursFromTime;
//	}
//
//	public List<Integer> getClinicSaturdayWorkingHoursToTime() {
//		return clinicSaturdayWorkingHoursToTime;
//	}
//
//	public void setClinicSaturdayWorkingHoursToTime(List<Integer> clinicSaturdayWorkingHoursToTime) {
//		this.clinicSaturdayWorkingHoursToTime = clinicSaturdayWorkingHoursToTime;
//	}
//
//	public List<Integer> getClinicSundayWorkingHoursFromTime() {
//		return clinicSundayWorkingHoursFromTime;
//	}
//
//	public void setClinicSundayWorkingHoursFromTime(List<Integer> clinicSundayWorkingHoursFromTime) {
//		this.clinicSundayWorkingHoursFromTime = clinicSundayWorkingHoursFromTime;
//	}
//
//	public List<Integer> getClinicSundayWorkingHoursToTime() {
//		return clinicSundayWorkingHoursToTime;
//	}
//
//	public void setClinicSundayWorkingHoursToTime(List<Integer> clinicSundayWorkingHoursToTime) {
//		this.clinicSundayWorkingHoursToTime = clinicSundayWorkingHoursToTime;
//	}

	@Override
	public String toString() {
		return "DoctorLocation [locationId=" + locationId + ", hospitalId=" + hospitalId + ", locationName="
				+ locationName + ", country=" + country + ", state=" + state + ", city=" + city + ", postalCode="
				+ postalCode + ", websiteUrl=" + websiteUrl + ", geoLocation=" + geoLocation + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", landmarkDetails=" + landmarkDetails + ", locationEmailAddress="
				+ locationEmailAddress + ", streetAddress=" + streetAddress + ", locality=" + locality
				+ ", clinicNumber=" + clinicNumber + ", alternateClinicNumbers=" + alternateClinicNumbers
				+ ", specialization=" + specialization + ", isClinic=" + isClinic + ", isLab=" + isLab
				+ ", isOnlineReportsAvailable=" + isOnlineReportsAvailable + ", isNABLAccredited=" + isNABLAccredited
				+ ", isHomeServiceAvailable=" + isHomeServiceAvailable + ", images=" + images + ", logoUrl=" + logoUrl
				+ ", noOfReviews=" + noOfReviews + ", noOfRecommenations=" + noOfRecommenations + ", locationUId="
				+ locationUId + "]";
	}

}
