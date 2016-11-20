package com.dpdocter.beans;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class UIPermissions {

	private List<String> tabPermissions;
	private List<String> patientVisitPermissions;
	private List<String> clinicalNotesPermissions;
	private List<String> prescriptionPermissions;
	private List<String> profilePermissions;

	public List<String> getTabPermissions() {
		return tabPermissions;
	}

	public void setTabPermissions(List<String> tabPermissions) {
		this.tabPermissions = tabPermissions;
	}

	public List<String> getPatientVisitPermissions() {
		return patientVisitPermissions;
	}

	public void setPatientVisitPermissions(List<String> patientVisitPermissions) {
		this.patientVisitPermissions = patientVisitPermissions;
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

	public List<String> getProfilePermissions() {
		return profilePermissions;
	}

	public void setProfilePermissions(List<String> profilePermissions) {
		this.profilePermissions = profilePermissions;
	}

	@Override
	public String toString() {
		return "UIPermissions [tabPermissions=" + tabPermissions + ", patientVisitPermissions="
				+ patientVisitPermissions + ", clinicalNotesPermissions=" + clinicalNotesPermissions
				+ ", prescriptionPermissions=" + prescriptionPermissions + ", profilePermissions=" + profilePermissions
				+ "]";
	}
}
