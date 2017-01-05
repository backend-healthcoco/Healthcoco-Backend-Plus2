package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.dpdocter.enums.EyeSightednessUnit;
import com.dpdocter.enums.EyeType;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class EyeTest {

	private Float baseCurve;
	private Float diameter;
	private Float power;
	private EyeSightednessUnit powerUnit;
	private Float cylinder;
	private EyeSightednessUnit cylinderUnit;
	private Integer axis;
	private Float addition;
	private EyeSightednessUnit additionUnit;
	private Integer pupilaryDistance;
	private EyeType eyeType;

	public Float getBaseCurve() {
		return baseCurve;
	}

	public void setBaseCurve(Float baseCurve) {
		this.baseCurve = baseCurve;
	}

	public Float getDiameter() {
		return diameter;
	}

	public void setDiameter(Float diameter) {
		this.diameter = diameter;
	}

	public Float getPower() {
		return power;
	}

	public void setPower(Float power) {
		this.power = power;
	}

	public Float getCylinder() {
		return cylinder;
	}

	public void setCylinder(Float cylinder) {
		this.cylinder = cylinder;
	}

	public Integer getAxis() {
		return axis;
	}

	public void setAxis(Integer axis) {
		this.axis = axis;
	}

	public Float getAddition() {
		return addition;
	}

	public void setAddition(Float addition) {
		this.addition = addition;
	}

	public Integer getPupilaryDistance() {
		return pupilaryDistance;
	}

	public void setPupilaryDistance(Integer pupilaryDistance) {
		this.pupilaryDistance = pupilaryDistance;
	}

	public EyeSightednessUnit getPowerUnit() {
		return powerUnit;
	}

	public void setPowerUnit(EyeSightednessUnit powerUnit) {
		this.powerUnit = powerUnit;
	}

	public EyeSightednessUnit getCylinderUnit() {
		return cylinderUnit;
	}

	public void setCylinderUnit(EyeSightednessUnit cylinderUnit) {
		this.cylinderUnit = cylinderUnit;
	}

	public EyeSightednessUnit getAdditionUnit() {
		return additionUnit;
	}

	public void setAdditionUnit(EyeSightednessUnit additionUnit) {
		this.additionUnit = additionUnit;
	}

	public EyeType getEyeType() {
		return eyeType;
	}

	public void setEyeType(EyeType eyeType) {
		this.eyeType = eyeType;
	}

	@Override
	public String toString() {
		return "EyeTest [baseCurve=" + baseCurve + ", diameter=" + diameter + ", power=" + power + ", powerUnit="
				+ powerUnit + ", cylinder=" + cylinder + ", cylinderUnit=" + cylinderUnit + ", axis=" + axis
				+ ", addition=" + addition + ", additionUnit=" + additionUnit + ", pupilaryDistance=" + pupilaryDistance
				+ ", eyeType=" + eyeType + "]";
	}

}
