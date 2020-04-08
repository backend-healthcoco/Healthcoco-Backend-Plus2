package com.dpdocter.beans;

public class Symptom {

	private Boolean isFever=false;
	
	private Boolean isDryCough=false;
	
	private Boolean isShortnessOfBreadth=false;
	
	private Boolean isHeadaches=false;
	
	private Boolean isPain=false;
	
	private Boolean isSoreThroat=false;
	
	private Boolean isFatigue=false;
	
	private Boolean isDiarrhoea=false;
	
	private Boolean isRunnynose=false;
	
	private Boolean isSneezing=false;

	public Boolean getIsFever() {
		return isFever;
	}

	public void setIsFever(Boolean isFever) {
		this.isFever = isFever;
	}

	public Boolean getIsDryCough() {
		return isDryCough;
	}

	public void setIsDryCough(Boolean isDryCough) {
		this.isDryCough = isDryCough;
	}

	public Boolean getIsShortnessOfBreadth() {
		return isShortnessOfBreadth;
	}

	public void setIsShortnessOfBreadth(Boolean isShortnessOfBreadth) {
		this.isShortnessOfBreadth = isShortnessOfBreadth;
	}

	public Boolean getIsHeadaches() {
		return isHeadaches;
	}

	public void setIsHeadaches(Boolean isHeadaches) {
		this.isHeadaches = isHeadaches;
	}

	public Boolean getIsPain() {
		return isPain;
	}

	public void setIsPain(Boolean isPain) {
		this.isPain = isPain;
	}

	public Boolean getIsSoreThroat() {
		return isSoreThroat;
	}

	public void setIsSoreThroat(Boolean isSoreThroat) {
		this.isSoreThroat = isSoreThroat;
	}

	public Boolean getIsFatigue() {
		return isFatigue;
	}

	public void setIsFatigue(Boolean isFatigue) {
		this.isFatigue = isFatigue;
	}

	public Boolean getIsDiarrhoea() {
		return isDiarrhoea;
	}

	public void setIsDiarrhoea(Boolean isDiarrhoea) {
		this.isDiarrhoea = isDiarrhoea;
	}

	public Boolean getIsRunnynose() {
		return isRunnynose;
	}

	public void setIsRunnynose(Boolean isRunnynose) {
		this.isRunnynose = isRunnynose;
	}

	public Boolean getIsSneezing() {
		return isSneezing;
	}

	public void setIsSneezing(Boolean isSneezing) {
		this.isSneezing = isSneezing;
	}

	@Override
	public String toString() {
		return "Symptom [isFever=" + isFever + ", isDryCough=" + isDryCough + ", isShortnessOfBreadth="
				+ isShortnessOfBreadth + ", isHeadaches=" + isHeadaches + ", isPain=" + isPain + ", isSoreThroat="
				+ isSoreThroat + ", isFatigue=" + isFatigue + ", isDiarrhoea=" + isDiarrhoea + ", isRunnynose="
				+ isRunnynose + ", isSneezing=" + isSneezing + "]";
	}
	
	
	
}
