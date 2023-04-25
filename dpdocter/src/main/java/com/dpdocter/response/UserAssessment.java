package com.dpdocter.response;

import com.dpdocter.beans.DentalAssessment;
import com.dpdocter.beans.ENTAssessment;
import com.dpdocter.beans.EyeAssessment;
import com.dpdocter.beans.GrowthAssessmentAndGeneralBioMetrics;
import com.dpdocter.beans.NutritionAssessment;
import com.dpdocter.beans.PhysicalAssessment;
import com.dpdocter.beans.RegistrationDetails;

public class UserAssessment {

	private GrowthAssessmentAndGeneralBioMetrics growthAssessmentAndGeneralBioMetrics;
	private PhysicalAssessment physicalAssessment;
	private ENTAssessment entAssessment;
	private DentalAssessment dentalAssessment;
	private EyeAssessment eyeAssessment;
	private NutritionAssessment nutritionAssessment;
	private RegistrationDetails registrationDetails;

	public GrowthAssessmentAndGeneralBioMetrics getGrowthAssessmentAndGeneralBioMetrics() {
		return growthAssessmentAndGeneralBioMetrics;
	}

	public void setGrowthAssessmentAndGeneralBioMetrics(
			GrowthAssessmentAndGeneralBioMetrics growthAssessmentAndGeneralBioMetrics) {
		this.growthAssessmentAndGeneralBioMetrics = growthAssessmentAndGeneralBioMetrics;
	}

	public PhysicalAssessment getPhysicalAssessment() {
		return physicalAssessment;
	}

	public void setPhysicalAssessment(PhysicalAssessment physicalAssessment) {
		this.physicalAssessment = physicalAssessment;
	}

	public ENTAssessment getEntAssessment() {
		return entAssessment;
	}

	public void setEntAssessment(ENTAssessment entAssessment) {
		this.entAssessment = entAssessment;
	}

	public DentalAssessment getDentalAssessment() {
		return dentalAssessment;
	}

	public void setDentalAssessment(DentalAssessment dentalAssessment) {
		this.dentalAssessment = dentalAssessment;
	}

	public EyeAssessment getEyeAssessment() {
		return eyeAssessment;
	}

	public void setEyeAssessment(EyeAssessment eyeAssessment) {
		this.eyeAssessment = eyeAssessment;
	}

	public NutritionAssessment getNutritionAssessment() {
		return nutritionAssessment;
	}

	public void setNutritionAssessment(NutritionAssessment nutritionAssessment) {
		this.nutritionAssessment = nutritionAssessment;
	}

	public RegistrationDetails getRegistrationDetails() {
		return registrationDetails;
	}

	public void setRegistrationDetails(RegistrationDetails registrationDetails) {
		this.registrationDetails = registrationDetails;
	}

	@Override
	public String toString() {
		return "UserAssessment [growthAssessmentAndGeneralBioMetrics=" + growthAssessmentAndGeneralBioMetrics
				+ ", physicalAssessment=" + physicalAssessment + ", entAssessment=" + entAssessment
				+ ", dentalAssessment=" + dentalAssessment + ", eyeAssessment=" + eyeAssessment
				+ ", nutritionAssessment=" + nutritionAssessment + ", registrationDetails=" + registrationDetails + "]";
	}
}
