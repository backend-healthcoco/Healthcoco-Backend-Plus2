package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.FlowSheet;
import com.dpdocter.beans.MonitoringChart;

public class AddEditFlowSheetRequest {

	private String id;
	private String dischargeSummaryId;
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private String patientId;
	private Boolean discarded = false;
	private List<FlowSheet> flowSheets;
	private List<MonitoringChart> monitoringChart;

	private String uniqueId;
	

	

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDischargeSummaryId() {
		return dischargeSummaryId;
	}

	public void setDischargeSummaryId(String dischargeSummaryId) {
		this.dischargeSummaryId = dischargeSummaryId;
	}

	public List<FlowSheet> getFlowSheets() {
		return flowSheets;
	}

	public void setFlowSheets(List<FlowSheet> flowSheets) {
		this.flowSheets = flowSheets;
	}

	public String getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
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

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	
	public List<MonitoringChart> getMonitoringChart() {
		return monitoringChart;
	}

	public void setMonitoringChart(List<MonitoringChart> monitoringChart) {
		this.monitoringChart = monitoringChart;
	}

	@Override
	public String toString() {
		return "AddEditFlowSheetRequest [id=" + id + ", dischargeSummaryId=" + dischargeSummaryId + ", doctorId="
				+ doctorId + ", locationId=" + locationId + ", hospitalId=" + hospitalId + ", patientId=" + patientId
				+ ", discarded=" + discarded + ", flowSheets=" + flowSheets + "]";
	}

}
