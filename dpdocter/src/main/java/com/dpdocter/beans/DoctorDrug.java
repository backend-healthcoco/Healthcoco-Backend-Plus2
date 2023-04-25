package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DoctorDrug {

	private String id;

	private String drugId;

	private String doctorId;

	private String hospitalId;

	private String locationId;

	private long rankingCount = 0;

	private Boolean discarded = false;

	private List<String> genericCodes;

	private List<Drug> drugs;

	Drug drug;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDrugId() {
		return drugId;
	}

	public void setDrugId(String drugId) {
		this.drugId = drugId;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public long getRankingCount() {
		return rankingCount;
	}

	public void setRankingCount(long rankingCount) {
		this.rankingCount = rankingCount;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public List<String> getGenericCodes() {
		return genericCodes;
	}

	public void setGenericCodes(List<String> genericCodes) {
		this.genericCodes = genericCodes;
	}

	public List<Drug> getDrugs() {
		if (drugs != null && !drugs.isEmpty()) {
			drug = drugs.get(0);
		}
		return drugs;
	}

	public void setDrugs(List<Drug> drugs) {
		this.drugs = drugs;
	}

	public Drug getDrug() {
		return drug;
	}

	public void setDrug(Drug drug) {
		this.drug = drug;
	}

	@Override
	public String toString() {
		return "DoctorDrug [id=" + id + ", drugId=" + drugId + ", doctorId=" + doctorId + ", hospitalId=" + hospitalId
				+ ", locationId=" + locationId + ", rankingCount=" + rankingCount + ", discarded=" + discarded
				+ ", genericCodes=" + genericCodes + ", drugs=" + drugs + ", drug=" + drug + "]";
	}
}
