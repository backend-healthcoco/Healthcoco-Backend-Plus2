package com.dpdocter.solr.response;

import java.util.List;

public class SolrPatientResponseDetails {

	private List<SolrPatientResponse> patients;
	
	Long totalSize ;

	public List<SolrPatientResponse> getPatients() {
		return patients;
	}

	public void setPatients(List<SolrPatientResponse> patients) {
		this.patients = patients;
	}

	public Long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(Long totalSize) {
		this.totalSize = totalSize;
	}

	@Override
	public String toString() {
		return "SolrPatientResponseDetails [patients=" + patients + ", totalSize=" + totalSize + "]";
	}
}
