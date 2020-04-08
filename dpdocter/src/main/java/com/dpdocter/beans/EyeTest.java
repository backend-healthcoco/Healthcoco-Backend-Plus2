package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class EyeTest {

	private String distanceSPH;

	private String nearSPH;

	private String distanceCylinder;

	private String nearCylinder;

	private String distanceAxis;

	private String nearAxis;

	private String nearVA;

	private String distanceVA;

	private String nearBaseCurve;

	private String distanceBaseCurve;

	private String nearDiameter;

	private String distanceDiameter;

	public String getDistanceSPH() {
		return distanceSPH;
	}

	public void setDistanceSPH(String distanceSPH) {
		this.distanceSPH = distanceSPH;
	}

	public String getNearSPH() {
		return nearSPH;
	}

	public void setNearSPH(String nearSPH) {
		this.nearSPH = nearSPH;
	}

	public String getDistanceCylinder() {
		return distanceCylinder;
	}

	public void setDistanceCylinder(String distanceCylinder) {
		this.distanceCylinder = distanceCylinder;
	}

	public String getNearCylinder() {
		return nearCylinder;
	}

	public void setNearCylinder(String nearCylinder) {
		this.nearCylinder = nearCylinder;
	}

	public String getDistanceAxis() {
		return distanceAxis;
	}

	public void setDistanceAxis(String distanceAxis) {
		this.distanceAxis = distanceAxis;
	}

	public String getNearAxis() {
		return nearAxis;
	}

	public void setNearAxis(String nearAxis) {
		this.nearAxis = nearAxis;
	}

	public String getNearVA() {
		return nearVA;
	}

	public void setNearVA(String nearVA) {
		this.nearVA = nearVA;
	}

	public String getDistanceVA() {
		return distanceVA;
	}

	public void setDistanceVA(String distanceVA) {
		this.distanceVA = distanceVA;
	}

	public String getNearBaseCurve() {
		return nearBaseCurve;
	}

	public void setNearBaseCurve(String nearBaseCurve) {
		this.nearBaseCurve = nearBaseCurve;
	}

	public String getDistanceBaseCurve() {
		return distanceBaseCurve;
	}

	public void setDistanceBaseCurve(String distanceBaseCurve) {
		this.distanceBaseCurve = distanceBaseCurve;
	}

	public String getNearDiameter() {
		return nearDiameter;
	}

	public void setNearDiameter(String nearDiameter) {
		this.nearDiameter = nearDiameter;
	}

	public String getDistanceDiameter() {
		return distanceDiameter;
	}

	public void setDistanceDiameter(String distanceDiameter) {
		this.distanceDiameter = distanceDiameter;
	}

	@Override
	public String toString() {
		return "EyeTest [distanceSPH=" + distanceSPH + ", nearSPH=" + nearSPH + ", distanceCylinder=" + distanceCylinder
				+ ", nearCylinder=" + nearCylinder + ", distanceAxis=" + distanceAxis + ", nearAxis=" + nearAxis
				+ ", nearVA=" + nearVA + ", distanceVA=" + distanceVA + "]";
	}
}
