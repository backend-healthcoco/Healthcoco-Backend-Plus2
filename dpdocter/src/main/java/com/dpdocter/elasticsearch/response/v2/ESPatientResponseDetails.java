package com.dpdocter.elasticsearch.response.v2;

import java.util.List;

public class ESPatientResponseDetails {

	private List<ESPatientResponse> patients;

	private long totalSize;

	public List<ESPatientResponse> getPatients() {
		return patients;
	}

	public void setPatients(List<ESPatientResponse> patients) {
		this.patients = patients;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	@Override
	public String toString() {
		return "ESPatientResponseDetails [patients=" + patients + ", totalSize=" + totalSize + "]";
	}
}
