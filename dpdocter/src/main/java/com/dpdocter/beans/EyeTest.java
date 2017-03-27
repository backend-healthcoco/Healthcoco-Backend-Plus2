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
	
	private String va;

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

	public String getVa() {
		return va;
	}

	public void setVa(String va) {
		this.va = va;
	}

	@Override
	public String toString() {
		return "EyeTest [distanceSPH=" + distanceSPH + ", nearSPH=" + nearSPH + ", distanceCylinder=" + distanceCylinder
				+ ", nearCylinder=" + nearCylinder + ", distanceAxis=" + distanceAxis + ", nearAxis=" + nearAxis
				+ ", va=" + va + "]";
	}
}
