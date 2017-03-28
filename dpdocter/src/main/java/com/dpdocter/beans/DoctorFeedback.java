package com.dpdocter.beans;

import com.dpdocter.collections.GenericCollection;
import com.dpdocter.enums.FeedbackEnum;
import com.dpdocter.enums.PricingEnum;

public class DoctorFeedback extends GenericCollection {

	private String id;
	private FeedbackEnum interconnectednessOfPlatform;
	private FeedbackEnum usefulnessOFPrescription;;
	private FeedbackEnum usefulOfListing;
	private FeedbackEnum usefulOfAppointment;
	private FeedbackEnum valueAdditionToPractice;
	private FeedbackEnum timeSavingInPrescription;
	private FeedbackEnum timeSavingInAppointments;
	private FeedbackEnum timeSavingInReports;
	private FeedbackEnum legalSafetyInPrescription;
	private FeedbackEnum legalSafetyInDrugsInformation;
	private FeedbackEnum legalSafetyInDrugsInteraction;
	private FeedbackEnum legalSafetyInProvisionalDiagnosis;
	private FeedbackEnum legalSafetyInEMR;
	private FeedbackEnum legalSafetyInMCICompliance;
	private FeedbackEnum recordKeeping;
	private FeedbackEnum presentationSkillOverall;
	private FeedbackEnum presentationSkillExample;
	private PricingEnum pricing;
	private String customizationNeeded;
	private String commentOnVirtualReality;
	private String suggesstions;
	private String doctorName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public FeedbackEnum getInterconnedtednessOfPlatform() {
		return interconnectednessOfPlatform;
	}

	public void setInterconnedtednessOfPlatform(FeedbackEnum interconnectednessOfPlatform) {
		this.interconnectednessOfPlatform = interconnectednessOfPlatform;
	}

	public FeedbackEnum getUsefulnessOFPrescription() {
		return usefulnessOFPrescription;
	}

	public void setUsefulnessOFPrescription(FeedbackEnum usefulnessOFPrescription) {
		this.usefulnessOFPrescription = usefulnessOFPrescription;
	}

	public FeedbackEnum getUsefulOfListing() {
		return usefulOfListing;
	}

	public void setUsefulOfListing(FeedbackEnum usefulOfListing) {
		this.usefulOfListing = usefulOfListing;
	}

	public FeedbackEnum getUsefulOfAppointment() {
		return usefulOfAppointment;
	}

	public void setUsefulOfAppointment(FeedbackEnum usefulOfAppointment) {
		this.usefulOfAppointment = usefulOfAppointment;
	}

	public FeedbackEnum getValueAdditionToPractice() {
		return valueAdditionToPractice;
	}

	public void setValueAdditionToPractice(FeedbackEnum valueAdditionToPractice) {
		this.valueAdditionToPractice = valueAdditionToPractice;
	}

	public FeedbackEnum getTimeSavingInPrescription() {
		return timeSavingInPrescription;
	}

	public void setTimeSavingInPrescription(FeedbackEnum timeSavingInPrescription) {
		this.timeSavingInPrescription = timeSavingInPrescription;
	}

	public FeedbackEnum getTimeSavingInAppointments() {
		return timeSavingInAppointments;
	}

	public void setTimeSavingInAppointments(FeedbackEnum timeSavingInAppointments) {
		this.timeSavingInAppointments = timeSavingInAppointments;
	}

	public FeedbackEnum getTimeSavingInReports() {
		return timeSavingInReports;
	}

	public void setTimeSavingInReports(FeedbackEnum timeSavingInReports) {
		this.timeSavingInReports = timeSavingInReports;
	}

	public FeedbackEnum getLegalSafetyInPrescription() {
		return legalSafetyInPrescription;
	}

	public void setLegalSafetyInPrescription(FeedbackEnum legalSafetyInPrescription) {
		this.legalSafetyInPrescription = legalSafetyInPrescription;
	}

	public FeedbackEnum getLegalSafetyInDrugsInformation() {
		return legalSafetyInDrugsInformation;
	}

	public void setLegalSafetyInDrugsInformation(FeedbackEnum legalSafetyInDrugsInformation) {
		this.legalSafetyInDrugsInformation = legalSafetyInDrugsInformation;
	}

	public FeedbackEnum getLegalSafetyInDrugsInteraction() {
		return legalSafetyInDrugsInteraction;
	}

	public void setLegalSafetyInDrugsInteraction(FeedbackEnum legalSafetyInDrugsInteraction) {
		this.legalSafetyInDrugsInteraction = legalSafetyInDrugsInteraction;
	}

	public FeedbackEnum getLegalSafetyInProvisionalDiagnosis() {
		return legalSafetyInProvisionalDiagnosis;
	}

	public void setLegalSafetyInProvisionalDiagnosis(FeedbackEnum legalSafetyInProvisionalDiagnosis) {
		this.legalSafetyInProvisionalDiagnosis = legalSafetyInProvisionalDiagnosis;
	}

	public FeedbackEnum getLegalSafetyInEMR() {
		return legalSafetyInEMR;
	}

	public void setLegalSafetyInEMR(FeedbackEnum legalSafetyInEMR) {
		this.legalSafetyInEMR = legalSafetyInEMR;
	}

	public FeedbackEnum getLegalSafetyInMCICompliance() {
		return legalSafetyInMCICompliance;
	}

	public void setLegalSafetyInMCICompliance(FeedbackEnum legalSafetyInMCICompliance) {
		this.legalSafetyInMCICompliance = legalSafetyInMCICompliance;
	}

	public FeedbackEnum getRecordKeeping() {
		return recordKeeping;
	}

	public void setRecordKeeping(FeedbackEnum recordKeeping) {
		this.recordKeeping = recordKeeping;
	}

	public FeedbackEnum getPresentationSkillOverall() {
		return presentationSkillOverall;
	}

	public void setPresentationSkillOverall(FeedbackEnum presentationSkillOverall) {
		this.presentationSkillOverall = presentationSkillOverall;
	}

	public FeedbackEnum getPresentationSkillExample() {
		return presentationSkillExample;
	}

	public void setPresentationSkillExample(FeedbackEnum presentationSkillExample) {
		this.presentationSkillExample = presentationSkillExample;
	}

	public PricingEnum getPricing() {
		return pricing;
	}

	public void setPricing(PricingEnum pricing) {
		this.pricing = pricing;
	}

	public String getCustomizationNeeded() {
		return customizationNeeded;
	}

	public void setCustomizationNeeded(String customizationNeeded) {
		this.customizationNeeded = customizationNeeded;
	}

	public String getCommentOnVirtualReality() {
		return commentOnVirtualReality;
	}

	public void setCommentOnVirtualReality(String commentOnVirtualReality) {
		this.commentOnVirtualReality = commentOnVirtualReality;
	}

	public String getSuggesstions() {
		return suggesstions;
	}

	public void setSuggesstions(String suggesstions) {
		this.suggesstions = suggesstions;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	@Override
	public String toString() {
		return "DoctorFeedback [id=" + id + ", interconnedtednessOfPlatform=" + interconnectednessOfPlatform
				+ ", usefulnessOFPrescription=" + usefulnessOFPrescription + ", usefulOfListing=" + usefulOfListing
				+ ", usefulOfAppointment=" + usefulOfAppointment + ", valueAdditionToPractice="
				+ valueAdditionToPractice + ", timeSavingInPrescription=" + timeSavingInPrescription
				+ ", timeSavingInAppointments=" + timeSavingInAppointments + ", timeSavingInReports="
				+ timeSavingInReports + ", legalSafetyInPrescription=" + legalSafetyInPrescription
				+ ", legalSafetyInDrugsInformation=" + legalSafetyInDrugsInformation
				+ ", legalSafetyInDrugsInteraction=" + legalSafetyInDrugsInteraction
				+ ", legalSafetyInProvisionalDiagnosis=" + legalSafetyInProvisionalDiagnosis + ", legalSafetyInEMR="
				+ legalSafetyInEMR + ", legalSafetyInMCICompliance=" + legalSafetyInMCICompliance + ", recordKeeping="
				+ recordKeeping + ", presentationSkillOverall=" + presentationSkillOverall
				+ ", presentationSkillExample=" + presentationSkillExample + ", pricing=" + pricing
				+ ", customizationNeeded=" + customizationNeeded + ", commentOnVirtualReality="
				+ commentOnVirtualReality + ", suggesstions=" + suggesstions + ", doctorName=" + doctorName + "]";
	}

}
