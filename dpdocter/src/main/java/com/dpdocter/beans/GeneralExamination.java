package com.dpdocter.beans;

public class GeneralExamination {
	
private String InspectionOfPart_Posture;
private Palpation palpation;
private RangeOfMotion rangeOfMotion;


public String getInspectionOfPart_Posture() {
	return InspectionOfPart_Posture;
}
public void setInspectionOfPart_Posture(String inspectionOfPart_Posture) {
	InspectionOfPart_Posture = inspectionOfPart_Posture;
}

public Palpation getPalpation() {
	return palpation;
}
public void setPalpation(Palpation palpation) {
	this.palpation = palpation;
}
public RangeOfMotion getRangeOfMotion() {
	return rangeOfMotion;
}
public void setRangeOfMotion(RangeOfMotion rangeOfMotion) {
	this.rangeOfMotion = rangeOfMotion;
}





}
