package com.dpdocter.beans;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class UIPermissions {

	private List<String> tabPermissions = new ArrayList<String>();
	private List<String> patientVisitPermissions = new ArrayList<String>();
	private List<String> clinicalNotesPermissions = new ArrayList<String>();
	private List<String> prescriptionPermissions = new ArrayList<String>();
	private List<String> profilePermissions = new ArrayList<String>();
	private List<String> vitalSignPermissions = new ArrayList<String>();
	private List<String> dischargeSummaryPermissions = new ArrayList<String>();
	private List<String> admitCardPermissions = new ArrayList<String>();
	private String landingPagePermissions = "CONTACTS";
	private List<String> patientCertificatePermissions = new ArrayList<String>();
	private Boolean showSidePanels = true;
	private Boolean isIPDIvoice=false;
	
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

	public List<String> getVitalSignPermissions() {
		return vitalSignPermissions;
	}

	public void setVitalSignPermissions(List<String> vitalSignPermissions) {
		this.vitalSignPermissions = vitalSignPermissions;
	}

	public List<String> getDischargeSummaryPermissions() {
		return dischargeSummaryPermissions;
	}

	public void setDischargeSummaryPermissions(List<String> dischargeSummaryPermissions) {
		this.dischargeSummaryPermissions = dischargeSummaryPermissions;
	}

	public List<String> getAdmitCardPermissions() {
		return admitCardPermissions;
	}

	public void setAdmitCardPermissions(List<String> admitCardPermissions) {
		this.admitCardPermissions = admitCardPermissions;
	}

	public String getLandingPagePermissions() {
		return landingPagePermissions;
	}

	public void setLandingPagePermissions(String landingPagePermissions) {
		this.landingPagePermissions = landingPagePermissions;
	}

	public List<String> getPatientCertificatePermissions() {
		return patientCertificatePermissions;
	}

	public void setPatientCertificatePermissions(List<String> patientCertificatePermissions) {
		this.patientCertificatePermissions = patientCertificatePermissions;
	}

	public Boolean getShowSidePanels() {
		return showSidePanels;
	}

	public void setShowSidePanels(Boolean showSidePanels) {
		this.showSidePanels = showSidePanels;
	}
	
	

	public Boolean getIsIPDIvoice() {
		return isIPDIvoice;
	}

	public void setIsIPDIvoice(Boolean isIPDIvoice) {
		this.isIPDIvoice = isIPDIvoice;
	}

	@Override
	public String toString() {
		return "UIPermissions [tabPermissions=" + tabPermissions + ", patientVisitPermissions="
				+ patientVisitPermissions + ", clinicalNotesPermissions=" + clinicalNotesPermissions
				+ ", prescriptionPermissions=" + prescriptionPermissions + ", profilePermissions=" + profilePermissions
				+ ", vitalSignPermissions=" + vitalSignPermissions + ", dischargeSummaryPermissions="
				+ dischargeSummaryPermissions + ", admitCardPermissions=" + admitCardPermissions
				+ ", landingPagePermissions=" + landingPagePermissions + ", patientCertificatePermissions="
				+ patientCertificatePermissions + ", showSidePanels=" + showSidePanels + "]";
	}
}
