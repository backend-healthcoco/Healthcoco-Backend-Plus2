package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class UIPermissions {

	private List<String> tabPermissions;
	private List<String> clinicalNotesPermissions;
	private List<String> prescriptionPermissions;
	private List<String> historyPermissions;

	public List<String> getTabPermissions() {
		return tabPermissions;
	}

	public void setTabPermissions(List<String> tabPermissions) {
		this.tabPermissions = tabPermissions;
	}

	public List<String> getClinicalNotesPermissions() {
		return clinicalNotesPermissions;
	}

	public void setClinicalNotesPermissions(List<String> clinicalNotesPermissions) {
		this.clinicalNotesPermissions = clinicalNotesPermissions;
	}

	public List<String> getPrescriptionPermissions() {
		return prescriptionPermissions;
	}

	public void setPrescriptionPermissions(List<String> prescriptionPermissions) {
		this.prescriptionPermissions = prescriptionPermissions;
	}

	public List<String> getHistoryPermissions() {
		return historyPermissions;
	}

	public void setHistoryPermissions(List<String> historyPermissions) {
		this.historyPermissions = historyPermissions;
	}

	@Override
	public String toString() {
		return "UIPermissions [tabPermissions=" + tabPermissions + ", clinicalNotesPermissions="
				+ clinicalNotesPermissions + ", prescriptionPermissions=" + prescriptionPermissions
				+ ", historyPermissions=" + historyPermissions + "]";
	}

}
