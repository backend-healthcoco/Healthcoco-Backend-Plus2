package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.collections.GenericCollection;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TreatmentServiceCost extends GenericCollection {

	private String id;

	private String locationId;

	private String hospitalId;

	private String doctorId;

	private TreatmentService treatmentService;

	private List<TreatmentService> treatmentServicesList;

	private double cost = 0.0;

	private Boolean discarded = false;

	private int ranking = 0;

	private Boolean isFav = false;

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

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public TreatmentService getTreatmentService() {
		return treatmentService;
	}

	public void setTreatmentService(TreatmentService treatmentService) {
		this.treatmentService = treatmentService;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public List<TreatmentService> getTreatmentServicesList() {
		if (treatmentServicesList != null && !treatmentServicesList.isEmpty()) {
			this.treatmentService = treatmentServicesList.get(0);
			this.treatmentServicesList = null;
		}
		return treatmentServicesList;
	}

	public void setTreatmentServicesList(List<TreatmentService> treatmentServicesList) {
		this.treatmentServicesList = treatmentServicesList;
		if (treatmentServicesList != null && !treatmentServicesList.isEmpty()) {
			this.treatmentService = treatmentServicesList.get(0);
			this.treatmentServicesList = null;
		}
	}

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	public Boolean getIsFav() {
		return isFav;
	}

	public void setIsFav(Boolean isFav) {
		this.isFav = isFav;
	}

	@Override
	public String toString() {
		return "TreatmentServiceCost [id=" + id + ", locationId=" + locationId + ", hospitalId=" + hospitalId
				+ ", doctorId=" + doctorId + ", treatmentService=" + treatmentService + ", treatmentServicesList="
				+ treatmentServicesList + ", cost=" + cost + ", discarded=" + discarded + ", ranking=" + ranking
				+ ", isFav=" + isFav + "]";
	}
}
