package com.dpdocter.beans;

import java.util.List;

public class InseptionReportJasperBean {
	private String dentalLab;
	private String doctor;
	private String patientName;
	private String shade;
	private String dentalWork;
	private String material;
	private String toothNumbers;
	private String status;
	private String requestId;
	private String bisqueStage = "BISQUE";
	private String finalStage = "FINAL";
	private String copingStage = "COPING";
	private String date;
	private List<DentalStagejasperBean> items;

	public String getDentalLab() {
		return dentalLab;
	}

	public void setDentalLab(String dentalLab) {
		this.dentalLab = dentalLab;
	}

	public String getDoctor() {
		return doctor;
	}

	public void setDoctor(String doctor) {
		this.doctor = doctor;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getShade() {
		return shade;
	}

	public void setShade(String shade) {
		this.shade = shade;
	}

	public String getDentalWork() {
		return dentalWork;
	}

	public void setDentalWork(String dentalWork) {
		this.dentalWork = dentalWork;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getToothNumbers() {
		return toothNumbers;
	}

	public void setToothNumbers(String toothNumbers) {
		this.toothNumbers = toothNumbers;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getBisqueStage() {
		return bisqueStage;
	}

	public void setBisqueStage(String bisqueStage) {
		this.bisqueStage = bisqueStage;
	}

	public String getFinalStage() {
		return finalStage;
	}

	public void setFinalStage(String finalStage) {
		this.finalStage = finalStage;
	}

	public String getCopingStage() {
		return copingStage;
	}

	public void setCopingStage(String copingStage) {
		this.copingStage = copingStage;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<DentalStagejasperBean> getItems() {
		return items;
	}

	public void setItems(List<DentalStagejasperBean> items) {
		this.items = items;
	}
	@Override
	public String toString() {
		return "s [dentalLab=" + dentalLab + ", doctor=" + doctor + ", patientName=" + patientName + ", shade=" + shade
				+ ", dentalWork=" + dentalWork + ", material=" + material + ", toothNumbers=" + toothNumbers
				+ ", status=" + status + ", requestId=" + requestId + ", bisqueStage=" + bisqueStage + ", finalStage="
				+ finalStage + ", copingStage=" + copingStage + ", date=" + date + ", items=" + items + "]";
	}
}
