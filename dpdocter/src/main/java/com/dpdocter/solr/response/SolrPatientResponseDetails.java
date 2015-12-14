package com.dpdocter.solr.response;

import java.util.List;

public class SolrPatientResponseDetails {

    private List<SolrPatientResponse> patients;

    private long totalSize;

    public List<SolrPatientResponse> getPatients() {
	return patients;
    }

    public void setPatients(List<SolrPatientResponse> patients) {
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
	return "SolrPatientResponseDetails [patients=" + patients + ", totalSize=" + totalSize + "]";
    }
}
