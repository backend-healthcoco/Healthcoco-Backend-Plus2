package com.dpdocter.beans.v2;

import java.util.List;

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
