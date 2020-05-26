package com.dpdocter.beans;

public class PhysioExamination {

	private String historyOfPresentIllness;
	private String otNotes;
	private HistoryOfPain historyOfPain;
	private PainRatingScale painRatingScale;
	private String PastHistory;
	private GeneralExamination generalExamination;
	private String manualMuscleTesting;
	private SpecialTest specialTest;
	private String treatment;
	
	public String getHistoryOfPresentIllness() {
		return historyOfPresentIllness;
	}
	public void setHistoryOfPresentIllness(String historyOfPresentIllness) {
		this.historyOfPresentIllness = historyOfPresentIllness;
	}
	public String getOtNotes() {
		return otNotes;
	}
	public void setOtNotes(String otNotes) {
		this.otNotes = otNotes;
	}
	public HistoryOfPain getHistoryOfPain() {
		return historyOfPain;
	}
	public void setHistoryOfPain(HistoryOfPain historyOfPain) {
		this.historyOfPain = historyOfPain;
	}
	public PainRatingScale getPainRatingScale() {
		return painRatingScale;
	}
	public void setPainRatingScale(PainRatingScale painRatingScale) {
		this.painRatingScale = painRatingScale;
	}
	public String getPastHistory() {
		return PastHistory;
	}
	public void setPastHistory(String pastHistory) {
		PastHistory = pastHistory;
	}
	public GeneralExamination getGeneralExamination() {
		return generalExamination;
	}
	public void setGeneralExamination(GeneralExamination generalExamination) {
		this.generalExamination = generalExamination;
	}
	public String getManualMuscleTesting() {
		return manualMuscleTesting;
	}
	public void setManualMuscleTesting(String manualMuscleTesting) {
		this.manualMuscleTesting = manualMuscleTesting;
	}
	public SpecialTest getSpecialTest() {
		return specialTest;
	}
	public void setSpecialTest(SpecialTest specialTest) {
		this.specialTest = specialTest;
	}
	public String getTreatment() {
		return treatment;
	}
	public void setTreatment(String treatment) {
		this.treatment = treatment;
	}
	@Override
	public String toString() {
		return "PhysioExamination [historyOfPresentIllness=" + historyOfPresentIllness + ", otNotes=" + otNotes
				+ ", historyOfPain=" + historyOfPain + ", painRatingScale=" + painRatingScale + ", PastHistory="
				+ PastHistory + ", generalExamination=" + generalExamination + ", manualMuscleTesting="
				+ manualMuscleTesting + ", specialTest=" + specialTest + ", treatment=" + treatment + "]";
	}
	
	
	
}
