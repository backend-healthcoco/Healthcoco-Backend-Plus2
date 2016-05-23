package com.dpdocter.solr.document;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.solr.core.geo.Point;
import org.springframework.data.solr.core.mapping.SolrDocument;

import com.dpdocter.beans.WorkingHours;
import com.dpdocter.beans.WorkingSchedule;
import com.dpdocter.enums.Day;

@SolrDocument(solrCoreName = "locations")
public class SolrLocationDocument {
    @Id
    @Field
    private String id;

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
    private Point geoLocation;

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

    @Transient
    private List<WorkingSchedule> workingSchedules;

    @Field
    private List<Integer> mondayWorkingHoursFromTime;
    
    @Field
    private List<Integer> mondayWorkingHoursToTime;
    
    @Field
    private List<Integer> tuesdayWorkingHoursFromTime;
    
    @Field
    private List<Integer> tuesdayWorkingHoursToTime;
    
    @Field
    private List<Integer> wednessdayWorkingHoursFromTime;
    
    @Field
    private List<Integer> wednessdayWorkingHoursToTime;
    
    @Field
    private List<Integer> thursdayWorkingHoursFromTime;
    
    @Field
    private List<Integer> thursdayWorkingHoursToTime;
    
    @Field
    private List<Integer> fridayWorkingHoursFromTime;
    
    @Field
    private List<Integer> fridayWorkingHoursToTime;
    
    @Field
    private List<Integer> saturdayWorkingHoursFromTime;
    
    @Field
    private List<Integer> saturdayWorkingHoursToTime;
    
    @Field
    private List<Integer> sundayWorkingHoursFromTime;
    
    @Field
    private List<Integer> sundayWorkingHoursToTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

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

	public Point getGeoLocation() {
		return geoLocation;
	}

	public void setGeoLocation(Point geoLocation) {
		this.geoLocation = geoLocation;
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

	public List<String> getSpecialization() {
		return specialization;
	}

	public void setSpecialization(List<String> specialization) {
		this.specialization = specialization;
	}

	public Boolean getIsClinic() {
		return isClinic;
	}

	public void setIsClinic(Boolean isClinic) {
		this.isClinic = isClinic;
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

	public String getLocationUId() {
		return locationUId;
	}

	public void setLocationUId(String locationUId) {
		this.locationUId = locationUId;
	}

	public List<WorkingSchedule> getWorkingSchedules() {
		return workingSchedules;
	    }

	    public void setWorkingSchedules(List<WorkingSchedule> workingSchedules) {
		this.workingSchedules = workingSchedules;
		if(this.workingSchedules != null && !this.workingSchedules.isEmpty()){
			for(WorkingSchedule solrWorkingSchedule : this.workingSchedules){
				List<Integer> fromTime = new  ArrayList<Integer>(), toTime = new ArrayList<Integer>();
				if(solrWorkingSchedule.getWorkingHours() != null && !solrWorkingSchedule.getWorkingHours().isEmpty())
				for(WorkingHours workingHours: solrWorkingSchedule.getWorkingHours()){
						fromTime.add(workingHours.getFromTime());
						toTime.add(workingHours.getToTime());
				}
				if(solrWorkingSchedule.getWorkingDay().getDay().equalsIgnoreCase(Day.MONDAY.getDay())){
					this.mondayWorkingHoursFromTime = fromTime;
					this.mondayWorkingHoursToTime = toTime;
				}
				
			}
		}
	    }

		public List<Integer> getMondayWorkingHoursFromTime() {
			return mondayWorkingHoursFromTime;
		}

		public void setMondayWorkingHoursFromTime(List<Integer> mondayWorkingHoursFromTime) {
			this.mondayWorkingHoursFromTime = mondayWorkingHoursFromTime;
		}

		public List<Integer> getMondayWorkingHoursToTime() {
			return mondayWorkingHoursToTime;
		}

		public void setMondayWorkingHoursToTime(List<Integer> mondayWorkingHoursToTime) {
			this.mondayWorkingHoursToTime = mondayWorkingHoursToTime;
		}

		public List<Integer> getTuesdayWorkingHoursFromTime() {
			return tuesdayWorkingHoursFromTime;
		}

		public void setTuesdayWorkingHoursFromTime(List<Integer> tuesdayWorkingHoursFromTime) {
			this.tuesdayWorkingHoursFromTime = tuesdayWorkingHoursFromTime;
		}

		public List<Integer> getTuesdayWorkingHoursToTime() {
			return tuesdayWorkingHoursToTime;
		}

		public void setTuesdayWorkingHoursToTime(List<Integer> tuesdayWorkingHoursToTime) {
			this.tuesdayWorkingHoursToTime = tuesdayWorkingHoursToTime;
		}

		public List<Integer> getWednessdayWorkingHoursFromTime() {
			return wednessdayWorkingHoursFromTime;
		}

		public void setWednessdayWorkingHoursFromTime(List<Integer> wednessdayWorkingHoursFromTime) {
			this.wednessdayWorkingHoursFromTime = wednessdayWorkingHoursFromTime;
		}

		public List<Integer> getWednessdayWorkingHoursToTime() {
			return wednessdayWorkingHoursToTime;
		}

		public void setWednessdayWorkingHoursToTime(List<Integer> wednessdayWorkingHoursToTime) {
			this.wednessdayWorkingHoursToTime = wednessdayWorkingHoursToTime;
		}

		public List<Integer> getThursdayWorkingHoursFromTime() {
			return thursdayWorkingHoursFromTime;
		}

		public void setThursdayWorkingHoursFromTime(List<Integer> thursdayWorkingHoursFromTime) {
			this.thursdayWorkingHoursFromTime = thursdayWorkingHoursFromTime;
		}

		public List<Integer> getThursdayWorkingHoursToTime() {
			return thursdayWorkingHoursToTime;
		}

		public void setThursdayWorkingHoursToTime(List<Integer> thursdayWorkingHoursToTime) {
			this.thursdayWorkingHoursToTime = thursdayWorkingHoursToTime;
		}

		public List<Integer> getFridayWorkingHoursFromTime() {
			return fridayWorkingHoursFromTime;
		}

		public void setFridayWorkingHoursFromTime(List<Integer> fridayWorkingHoursFromTime) {
			this.fridayWorkingHoursFromTime = fridayWorkingHoursFromTime;
		}

		public List<Integer> getFridayWorkingHoursToTime() {
			return fridayWorkingHoursToTime;
		}

		public void setFridayWorkingHoursToTime(List<Integer> fridayWorkingHoursToTime) {
			this.fridayWorkingHoursToTime = fridayWorkingHoursToTime;
		}

		public List<Integer> getSaturdayWorkingHoursFromTime() {
			return saturdayWorkingHoursFromTime;
		}

		public void setSaturdayWorkingHoursFromTime(List<Integer> saturdayWorkingHoursFromTime) {
			this.saturdayWorkingHoursFromTime = saturdayWorkingHoursFromTime;
		}

		public List<Integer> getSaturdayWorkingHoursToTime() {
			return saturdayWorkingHoursToTime;
		}

		public void setSaturdayWorkingHoursToTime(List<Integer> saturdayWorkingHoursToTime) {
			this.saturdayWorkingHoursToTime = saturdayWorkingHoursToTime;
		}

		public List<Integer> getSundayWorkingHoursFromTime() {
			return sundayWorkingHoursFromTime;
		}

		public void setSundayWorkingHoursFromTime(List<Integer> sundayWorkingHoursFromTime) {
			this.sundayWorkingHoursFromTime = sundayWorkingHoursFromTime;
		}

		public List<Integer> getSundayWorkingHoursToTime() {
			return sundayWorkingHoursToTime;
		}

		public void setSundayWorkingHoursToTime(List<Integer> sundayWorkingHoursToTime) {
			this.sundayWorkingHoursToTime = sundayWorkingHoursToTime;
		}

		@Override
		public String toString() {
			return "SolrLocationDocument [id=" + id + ", hospitalId=" + hospitalId + ", locationName=" + locationName
					+ ", country=" + country + ", state=" + state + ", city=" + city + ", postalCode=" + postalCode
					+ ", websiteUrl=" + websiteUrl + ", geoLocation=" + geoLocation + ", latitude=" + latitude
					+ ", longitude=" + longitude + ", landmarkDetails=" + landmarkDetails + ", locationEmailAddress="
					+ locationEmailAddress + ", streetAddress=" + streetAddress + ", locality=" + locality
					+ ", clinicNumber=" + clinicNumber + ", alternateClinicNumbers=" + alternateClinicNumbers
					+ ", specialization=" + specialization + ", isClinic=" + isClinic + ", isLab=" + isLab
					+ ", isOnlineReportsAvailable=" + isOnlineReportsAvailable + ", isNABLAccredited="
					+ isNABLAccredited + ", isHomeServiceAvailable=" + isHomeServiceAvailable + ", images=" + images
					+ ", logoUrl=" + logoUrl + ", noOfReviews=" + noOfReviews + ", noOfRecommenations="
					+ noOfRecommenations + ", locationUId=" + locationUId + ", workingSchedules=" + workingSchedules
					+ ", mondayWorkingHoursFromTime=" + mondayWorkingHoursFromTime + ", mondayWorkingHoursToTime="
					+ mondayWorkingHoursToTime + ", tuesdayWorkingHoursFromTime=" + tuesdayWorkingHoursFromTime
					+ ", tuesdayWorkingHoursToTime=" + tuesdayWorkingHoursToTime + ", wednessdayWorkingHoursFromTime="
					+ wednessdayWorkingHoursFromTime + ", wednessdayWorkingHoursToTime=" + wednessdayWorkingHoursToTime
					+ ", thursdayWorkingHoursFromTime=" + thursdayWorkingHoursFromTime + ", thursdayWorkingHoursToTime="
					+ thursdayWorkingHoursToTime + ", fridayWorkingHoursFromTime=" + fridayWorkingHoursFromTime
					+ ", fridayWorkingHoursToTime=" + fridayWorkingHoursToTime + ", saturdayWorkingHoursFromTime="
					+ saturdayWorkingHoursFromTime + ", saturdayWorkingHoursToTime=" + saturdayWorkingHoursToTime
					+ ", sundayWorkingHoursFromTime=" + sundayWorkingHoursFromTime + ", sundayWorkingHoursToTime="
					+ sundayWorkingHoursToTime + "]";
		}

}
