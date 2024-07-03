package com.dpdocter.beans;

import java.util.List;
import java.util.Map;

import com.dpdocter.enums.BloodPressureType;
import com.dpdocter.enums.DiabetesType;
import com.dpdocter.enums.ThyroidType;

public class TreatmentAndDiagnosis {
	private String doctorId;
	private String locationId;
	private String hospitalId;
	private String patientId;
	private Boolean discarded = false;
	private Map<String, Boolean> treatmentAndDiagnosisBoolen;
	private Map<String, String> treatmentAndDiagnosisString;
	private Map<String, List<String>> treatmentAndDiagnosisList;
	private BloodPressureType bloodPressureType;
	private DiabetesType diabetesType;
	private ThyroidType thyroidType;
	private Boolean isHistoryOfHeartProblems;
	private String historyOfHeartProblems;
	private Boolean isHistoryOfHeartProblemsInFamily;
	private String historyOfHeartProblemsInFamily;
	private Boolean isChronicIllnessOrCondition;
	private String chronicIllnessOrCondition;
	private Boolean isRecentSurgeryIn24Month;
	private String recentSurgeryIn24Month;
	private Boolean isHistoryOfBreathingProblem;
	private String historyOfBreathingProblem;
	private Boolean isChronicSleepProblem;
	private String chronicSleepProblem;

	public String getDoctorId() {
		return doctorId;
	}

	public String getLocationId() {
		return locationId;
	}

	public String getHospitalId() {
		return hospitalId;
	}

	public String getPatientId() {
		return patientId;
	}

	public Boolean getDiscarded() {
		return discarded;
	}

	public Map<String, Boolean> getTreatmentAndDiagnosisBoolen() {
		return treatmentAndDiagnosisBoolen;
	}

	public Map<String, String> getTreatmentAndDiagnosisString() {
		return treatmentAndDiagnosisString;
	}

	public Map<String, List<String>> getTreatmentAndDiagnosisList() {
		return treatmentAndDiagnosisList;
	}

	public BloodPressureType getBloodPressureType() {
		return bloodPressureType;
	}

	public DiabetesType getDiabetesType() {
		return diabetesType;
	}

	public ThyroidType getThyroidType() {
		return thyroidType;
	}

	public void setDoctorId(String doctorId) {
		this.doctorId = doctorId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public void setHospitalId(String hospitalId) {
		this.hospitalId = hospitalId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public void setDiscarded(Boolean discarded) {
		this.discarded = discarded;
	}

	public void setTreatmentAndDiagnosisBoolen(Map<String, Boolean> treatmentAndDiagnosisBoolen) {
		this.treatmentAndDiagnosisBoolen = treatmentAndDiagnosisBoolen;
	}

	public void setTreatmentAndDiagnosisString(Map<String, String> treatmentAndDiagnosisString) {
		this.treatmentAndDiagnosisString = treatmentAndDiagnosisString;
	}

	public void setTreatmentAndDiagnosisList(Map<String, List<String>> treatmentAndDiagnosisList) {
		this.treatmentAndDiagnosisList = treatmentAndDiagnosisList;
	}

	public void setBloodPressureType(BloodPressureType bloodPressureType) {
		this.bloodPressureType = bloodPressureType;
	}

	public void setDiabetesType(DiabetesType diabetesType) {
		this.diabetesType = diabetesType;
	}

	public void setThyroidType(ThyroidType thyroidType) {
		this.thyroidType = thyroidType;
	}

	public Boolean getIsHistoryOfHeartProblems() {
		return isHistoryOfHeartProblems;
	}

	public String getHistoryOfHeartProblems() {
		return historyOfHeartProblems;
	}

	public Boolean getIsHistoryOfHeartProblemsInFamily() {
		return isHistoryOfHeartProblemsInFamily;
	}

	public String getHistoryOfHeartProblemsInFamily() {
		return historyOfHeartProblemsInFamily;
	}

	public Boolean getIsChronicIllnessOrCondition() {
		return isChronicIllnessOrCondition;
	}

	public String getChronicIllnessOrCondition() {
		return chronicIllnessOrCondition;
	}

	public Boolean getIsRecentSurgeryIn24Month() {
		return isRecentSurgeryIn24Month;
	}

	public String getRecentSurgeryIn24Month() {
		return recentSurgeryIn24Month;
	}

	public Boolean getIsHistoryOfBreathingProblem() {
		return isHistoryOfBreathingProblem;
	}

	public String getHistoryOfBreathingProblem() {
		return historyOfBreathingProblem;
	}

	public Boolean getIsChronicSleepProblem() {
		return isChronicSleepProblem;
	}

	public String getChronicSleepProblem() {
		return chronicSleepProblem;
	}

	public void setIsHistoryOfHeartProblems(Boolean isHistoryOfHeartProblems) {
		this.isHistoryOfHeartProblems = isHistoryOfHeartProblems;
	}

	public void setHistoryOfHeartProblems(String historyOfHeartProblems) {
		this.historyOfHeartProblems = historyOfHeartProblems;
	}

	public void setIsHistoryOfHeartProblemsInFamily(Boolean isHistoryOfHeartProblemsInFamily) {
		this.isHistoryOfHeartProblemsInFamily = isHistoryOfHeartProblemsInFamily;
	}

	public void setHistoryOfHeartProblemsInFamily(String historyOfHeartProblemsInFamily) {
		this.historyOfHeartProblemsInFamily = historyOfHeartProblemsInFamily;
	}

	public void setIsChronicIllnessOrCondition(Boolean isChronicIllnessOrCondition) {
		this.isChronicIllnessOrCondition = isChronicIllnessOrCondition;
	}

	public void setChronicIllnessOrCondition(String chronicIllnessOrCondition) {
		this.chronicIllnessOrCondition = chronicIllnessOrCondition;
	}

	public void setIsRecentSurgeryIn24Month(Boolean isRecentSurgeryIn24Month) {
		this.isRecentSurgeryIn24Month = isRecentSurgeryIn24Month;
	}

	public void setRecentSurgeryIn24Month(String recentSurgeryIn24Month) {
		this.recentSurgeryIn24Month = recentSurgeryIn24Month;
	}

	public void setIsHistoryOfBreathingProblem(Boolean isHistoryOfBreathingProblem) {
		this.isHistoryOfBreathingProblem = isHistoryOfBreathingProblem;
	}

	public void setHistoryOfBreathingProblem(String historyOfBreathingProblem) {
		this.historyOfBreathingProblem = historyOfBreathingProblem;
	}

	public void setIsChronicSleepProblem(Boolean isChronicSleepProblem) {
		this.isChronicSleepProblem = isChronicSleepProblem;
	}

	public void setChronicSleepProblem(String chronicSleepProblem) {
		this.chronicSleepProblem = chronicSleepProblem;
	}

	@Override
	public String toString() {
		return "TreatmentAndDiagnosis [doctorId=" + doctorId + ", locationId=" + locationId + ", hospitalId="
				+ hospitalId + ", patientId=" + patientId + ", discarded=" + discarded
				+ ", treatmentAndDiagnosisBoolen=" + treatmentAndDiagnosisBoolen + ", treatmentAndDiagnosisString="
				+ treatmentAndDiagnosisString + ", treatmentAndDiagnosisList=" + treatmentAndDiagnosisList
				+ ", bloodPressureType=" + bloodPressureType + ", diabetesType=" + diabetesType + ", thyroidType="
				+ thyroidType + ", isHistoryOfHeartProblems=" + isHistoryOfHeartProblems + ", historyOfHeartProblems="
				+ historyOfHeartProblems + ", isHistoryOfHeartProblemsInFamily=" + isHistoryOfHeartProblemsInFamily
				+ ", historyOfHeartProblemsInFamily=" + historyOfHeartProblemsInFamily
				+ ", isChronicIllnessOrCondition=" + isChronicIllnessOrCondition + ", chronicIllnessOrCondition="
				+ chronicIllnessOrCondition + ", isRecentSurgeryIn24Month=" + isRecentSurgeryIn24Month
				+ ", recentSurgeryIn24Month=" + recentSurgeryIn24Month + ", isHistoryOfBreathingProblem="
				+ isHistoryOfBreathingProblem + ", historyOfBreathingProblem=" + historyOfBreathingProblem
				+ ", isChronicSleepProblem=" + isChronicSleepProblem + ", chronicSleepProblem=" + chronicSleepProblem
				+ "]";
	}

}
