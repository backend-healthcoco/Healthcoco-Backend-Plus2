package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TreatmentService extends GenericCollection {
	private String id;

	private String name;

	private String speciality;

	private String locationId;

	private String hospitalId;

	private String doctorId;

	private Boolean discarded = false;

	private double cost = 0.0;

	private String treatmentCode;

	private long rankingCount = 0;

	private String category;

	private List<String> fieldsRequired;

	private Double count = 0.0;
	private String ratelistId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
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

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public String getTreatmentCode() {
		return treatmentCode;
	}

	public void setTreatmentCode(String treatmentCode) {
		this.treatmentCode = treatmentCode;
	}

	public long getRankingCount() {
		return rankingCount;
	}

	public void setRankingCount(long rankingCount) {
		this.rankingCount = rankingCount;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<String> getFieldsRequired() {
		return fieldsRequired;
	}

	public void setFieldsRequired(List<String> fieldsRequired) {
		this.fieldsRequired = fieldsRequired;
	}

	public Double getCount() {
		return count;
	}

	public void setCount(Double count) {
		this.count = count;
	}

	public String getRatelistId() {
		return ratelistId;
	}

	public void setRatelistId(String ratelistId) {
		this.ratelistId = ratelistId;
	}

	@Override
	public String toString() {
		return "TreatmentService [id=" + id + ", name=" + name + ", speciality=" + speciality + ", locationId="
				+ locationId + ", hospitalId=" + hospitalId + ", doctorId=" + doctorId + ", discarded=" + discarded
				+ ", cost=" + cost + ", treatmentCode=" + treatmentCode + ", rankingCount=" + rankingCount
				+ ", category=" + category + ", fieldsRequired=" + fieldsRequired + ", count=" + count + "]";
	}

}
