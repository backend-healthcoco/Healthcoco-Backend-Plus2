package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class EyeTest {

	private Double distanceSPH;

	private Double nearSPH;

	private Double distanceCylinder;

	private Double nearCylinder;

	private Integer distanceAxis;

	private Integer nearAxis;

	private String nearVA;

	private String distanceVA;

	private Double nearBaseCurve;

	private Double distanceBaseCurve;

	private Double nearDiameter;

	private Double distanceDiameter;

	public Double getDistanceSPH() {
		return distanceSPH;
	}

	public void setDistanceSPH(Double distanceSPH) {
		this.distanceSPH = distanceSPH;
	}

	public Double getNearSPH() {
		return nearSPH;
	}

	public void setNearSPH(Double nearSPH) {
		this.nearSPH = nearSPH;
	}

	public Double getDistanceCylinder() {
		return distanceCylinder;
	}

	public void setDistanceCylinder(Double distanceCylinder) {
		this.distanceCylinder = distanceCylinder;
	}

	public Double getNearCylinder() {
		return nearCylinder;
	}

	public void setNearCylinder(Double nearCylinder) {
		this.nearCylinder = nearCylinder;
	}

	public Integer getDistanceAxis() {
		return distanceAxis;
	}

	public void setDistanceAxis(Integer distanceAxis) {
		this.distanceAxis = distanceAxis;
	}

	public Integer getNearAxis() {
		return nearAxis;
	}

	public void setNearAxis(Integer nearAxis) {
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

	public Double getNearBaseCurve() {
		return nearBaseCurve;
	}

	public void setNearBaseCurve(Double nearBaseCurve) {
		this.nearBaseCurve = nearBaseCurve;
	}

	public Double getDistanceBaseCurve() {
		return distanceBaseCurve;
	}

	public void setDistanceBaseCurve(Double distanceBaseCurve) {
		this.distanceBaseCurve = distanceBaseCurve;
	}

	public Double getNearDiameter() {
		return nearDiameter;
	}

	public void setNearDiameter(Double nearDiameter) {
		this.nearDiameter = nearDiameter;
	}

	public Double getDistanceDiameter() {
		return distanceDiameter;
	}

	public void setDistanceDiameter(Double distanceDiameter) {
		this.distanceDiameter = distanceDiameter;
	}

	@Override
	public String toString() {
		return "EyeTest [distanceSPH=" + distanceSPH + ", nearSPH=" + nearSPH + ", distanceCylinder=" + distanceCylinder
				+ ", nearCylinder=" + nearCylinder + ", distanceAxis=" + distanceAxis + ", nearAxis=" + nearAxis
				+ ", nearVA=" + nearVA + ", distanceVA=" + distanceVA + "]";
	}
}
