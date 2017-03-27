package com.dpdocter.beans;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class EyeTest {

	private Float distanceSPH;
	
	private Float nearSPH;
	
	private Float distanceCylinder;
	
	private Float nearCylinder;
	
	private Integer distanceAxis;
	
	private Integer nearAxis;
	
	private String nearVA;
	
	private String distanceVA;

	public Float getDistanceSPH() {
		return distanceSPH;
	}

	public void setDistanceSPH(Float distanceSPH) {
		this.distanceSPH = distanceSPH;
	}

	public Float getNearSPH() {
		return nearSPH;
	}

	public void setNearSPH(Float nearSPH) {
		this.nearSPH = nearSPH;
	}

	public Float getDistanceCylinder() {
		return distanceCylinder;
	}

	public void setDistanceCylinder(Float distanceCylinder) {
		this.distanceCylinder = distanceCylinder;
	}

	public Float getNearCylinder() {
		return nearCylinder;
	}

	public void setNearCylinder(Float nearCylinder) {
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

	@Override
	public String toString() {
		return "EyeTest [distanceSPH=" + distanceSPH + ", nearSPH=" + nearSPH + ", distanceCylinder=" + distanceCylinder
				+ ", nearCylinder=" + nearCylinder + ", distanceAxis=" + distanceAxis + ", nearAxis=" + nearAxis
				+ ", nearVA=" + nearVA + ", distanceVA=" + distanceVA + "]";
	}
}
