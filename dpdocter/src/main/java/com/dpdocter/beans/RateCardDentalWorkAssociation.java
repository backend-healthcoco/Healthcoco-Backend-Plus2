package com.dpdocter.beans;


public class RateCardDentalWorkAssociation {

	private String id;
	private String locationId;
	private String hospitalId;
	private String dentalWorkId;
	private String rateCardId;
	private Integer turnaroundTime;
	private Double cost;
	private Boolean isAvailable = true;
	private DentalWork dentalWork;
	
	
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
	public String getDentalWorkId() {
		return dentalWorkId;
	}
	public void setDentalWorkId(String dentalWorkId) {
		this.dentalWorkId = dentalWorkId;
	}
	public String getRateCardId() {
		return rateCardId;
	}
	public void setRateCardId(String rateCardId) {
		this.rateCardId = rateCardId;
	}
	public Integer getTurnaroundTime() {
		return turnaroundTime;
	}
	public void setTurnaroundTime(Integer turnaroundTime) {
		this.turnaroundTime = turnaroundTime;
	}
	public Double getCost() {
		return cost;
	}
	public void setCost(Double cost) {
		this.cost = cost;
	}
	public Boolean getIsAvailable() {
		return isAvailable;
	}
	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}
	public DentalWork getDentalWork() {
		return dentalWork;
	}
	public void setDentalWork(DentalWork dentalWork) {
		this.dentalWork = dentalWork;
	}
	@Override
	public String toString() {
		return "RateCardDentalWorkAssociation [id=" + id + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", dentalWorkId=" + dentalWorkId + ", rateCardId=" + rateCardId + ", turnaroundTime=" + turnaroundTime
				+ ", cost=" + cost + ", isAvailable=" + isAvailable + ", dentalWork=" + dentalWork + "]";
	}
	
}
