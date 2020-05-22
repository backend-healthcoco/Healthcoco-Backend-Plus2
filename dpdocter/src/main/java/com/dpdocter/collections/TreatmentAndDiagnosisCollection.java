package com.dpdocter.collections;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dpdocter.enums.BloodPressureType;
import com.dpdocter.enums.DiabetesType;
import com.dpdocter.enums.ThyroidType;

@Document(collection = "treatment_diagnosis_cl")
public class TreatmentAndDiagnosisCollection extends GenericCollection {
	@Id
	private ObjectId id;
	@Field
	private ObjectId doctorId;
	@Field
	private ObjectId locationId;
	@Field
	private ObjectId hospitalId;
	@Field
	private ObjectId patientId;
	@Field
	private Boolean discarded = false;
	@Field
	private Map<String, Boolean> treatmentAndDiagnosisBoolen;
	@Field
	private Map<String, String> treatmentAndDiagnosisString;
	@Field
	private Map<String, List<String>> treatmentAndDiagnosisList;
	@Field
	private BloodPressureType bloodPressureType;
	@Field
	private DiabetesType diabetesType;
	@Field
	private ThyroidType thyroidType;
	@Field
	private Boolean isHistoryOfHeartProblems;
	@Field
	private String historyOfHeartProblems;
	@Field
	private Boolean isHistoryOfHeartProblemsInFamily;
	@Field
	private String historyOfHeartProblemsInFamily;
	@Field
	private Boolean isChronicIllnessOrCondition;
	@Field
	private String chronicIllnessOrCondition;
	@Field
	private Boolean isRecentSurgeryIn24Month;
	@Field
	private String recentSurgeryIn24Month;
	@Field
	private Boolean isHistoryOfBreathingProblem;
	@Field
	private String historyOfBreathingProblem;
	@Field
	private Boolean isChronicSleepProblem;
	@Field
	private String chronicSleepProblem;
	
	public ObjectId getId() {
		return id;
	}
	public ObjectId getDoctorId() {
		return doctorId;
	}
	public ObjectId getLocationId() {
		return locationId;
	}
	public ObjectId getHospitalId() {
		return hospitalId;
	}
	public ObjectId getPatientId() {
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
	public void setId(ObjectId id) {
		this.id = id;
	}
	public void setDoctorId(ObjectId doctorId) {
		this.doctorId = doctorId;
	}
	public void setLocationId(ObjectId locationId) {
		this.locationId = locationId;
	}
	public void setHospitalId(ObjectId hospitalId) {
		this.hospitalId = hospitalId;
	}
	public void setPatientId(ObjectId patientId) {
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
		return "TreatmentAndDiagnosisCollection [id=" + id + ", doctorId=" + doctorId + ", locationId=" + locationId
				+ ", hospitalId=" + hospitalId + ", patientId=" + patientId + ", discarded=" + discarded
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
