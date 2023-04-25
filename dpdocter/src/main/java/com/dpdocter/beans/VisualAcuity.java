package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.enums.EyeSightednessUnit;
import com.dpdocter.enums.EyeType;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class VisualAcuity {

	private String unaided;
	private String presentLens;
	private EyeSightednessUnit presentLensUnit;
	private EyeType eyeType;
	private String pinHole;

	public String getUnaided() {
		return unaided;
	}

	public void setUnaided(String unaided) {
		this.unaided = unaided;
	}

	public String getPresentLens() {
		return presentLens;
	}

	public void setPresentLens(String presentLens) {
		this.presentLens = presentLens;
	}

	public EyeType getEyeType() {
		return eyeType;
	}

	public void setEyeType(EyeType eyeType) {
		this.eyeType = eyeType;
	}

	public EyeSightednessUnit getPresentLensUnit() {
		return presentLensUnit;
	}

	public void setPresentLensUnit(EyeSightednessUnit presentLensUnit) {
		this.presentLensUnit = presentLensUnit;
	}

	public String getPinHole() {
		return pinHole;
	}

	public void setPinHole(String pinHole) {
		this.pinHole = pinHole;
	}

	@Override
	public String toString() {
		return "VisualAcuity [unaided=" + unaided + ", presentLens=" + presentLens + ", presentLensUnit="
				+ presentLensUnit + ", eyeType=" + eyeType + "]";
	}

}
