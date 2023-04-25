package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DoctorContactsResponse {

	private List<PatientCard> patientCards;

	private int totalSize;

	public List<PatientCard> getPatientCards() {
		return patientCards;
	}

	public void setPatientCards(List<PatientCard> patientCards) {
		this.patientCards = patientCards;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	@Override
	public String toString() {
		return "DoctorContactsResponse [patientCards=" + patientCards + ", totalSize=" + totalSize + "]";
	}

}
