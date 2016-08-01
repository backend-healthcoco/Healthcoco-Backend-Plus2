package com.dpdocter.response;

import java.util.List;

import com.dpdocter.collections.GenericCollection;

public class PatientTreatmentResponse extends GenericCollection {
    private String id;

    private List<TreatmentResponse> treatments;

    private String patientId;

    private String locationId;

    private String hospitalId;

    private String doctorId;

    private double totalCost = 0.0;

    private Boolean discarded = false;

    private Boolean inHistory = false;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public List<TreatmentResponse> getTreatments() {
		return treatments;
	}

	public void setTreatments(List<TreatmentResponse> treatments) {
		this.treatments = treatments;
	}

	public String getPatientId() {
	return patientId;
    }

    public void setPatientId(String patientId) {
	this.patientId = patientId;
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

    public double getTotalCost() {
	return totalCost;
    }

    public void setTotalCost(double totalCost) {
	this.totalCost = totalCost;
    }

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public Boolean getInHistory() {
		return inHistory;
	}

	public void setInHistory(Boolean inHistory) {
		this.inHistory = inHistory;
	}

	@Override
	public String toString() {
		return "PatientTreatmentResponse [id=" + id + ", treatments=" + treatments + ", patientId=" + patientId
				+ ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", doctorId=" + doctorId
				+ ", totalCost=" + totalCost + ", discarded=" + discarded + ", inHistory=" + inHistory + "]";
	}
}
