package com.dpdocter.response;

public class EyeTestJasperResponse {

	private String distanceSPH = "--";
	
	private String nearSPH = "--";
	
	private String distanceCylinder = "--";
	
	private String nearCylinder = "--";
	
	private String distanceAxis = "--";
	
	private String nearAxis = "--";
	
	private String nearVA = "--";
	
	private String distanceVA = "--";

	public String getDistanceSPH() {
		return distanceSPH;
	}

	public void setDistanceSPH(String distanceSPH) {
		if(distanceSPH == null)this.distanceSPH = "--";
		else{
			if(!distanceSPH.startsWith("-"))this.distanceSPH = "+"+distanceSPH;
			else this.distanceSPH = distanceSPH;
		}
	}

	public String getNearSPH() {
		return nearSPH;
	}

	public void setNearSPH(String nearSPH) {
		if(nearSPH == null)this.nearSPH = "--";
		else{
			if(!nearSPH.startsWith("-"))this.nearSPH = "+"+nearSPH;
			else this.nearSPH = nearSPH;	
		}
	}

	public String getDistanceCylinder() {
		return distanceCylinder;
	}

	public void setDistanceCylinder(String distanceCylinder) {
		if(distanceCylinder == null)this.distanceCylinder = "--";
		else{
			if(!distanceCylinder.startsWith("-"))this.distanceCylinder = "+"+distanceCylinder;
			else this.distanceCylinder = distanceCylinder;	
		}
	}

	public String getNearCylinder() {
		return nearCylinder;
	}

	public void setNearCylinder(String nearCylinder) {
		if(nearCylinder == null)this.nearCylinder = "--";
		else{
			if(!nearCylinder.startsWith("-"))this.nearCylinder = "+"+nearCylinder;
			else this.nearCylinder = nearCylinder;
		}
	}

	public String getDistanceAxis() {
		return distanceAxis;
	}

	public void setDistanceAxis(String distanceAxis) {
		if(distanceAxis == null || distanceAxis.equalsIgnoreCase("0"))this.distanceAxis = "--";
		else this.distanceAxis = distanceAxis;
	}

	public String getNearAxis() {
		return nearAxis;
	}

	public void setNearAxis(String nearAxis) {
		if(nearAxis == null || nearAxis.equalsIgnoreCase("0"))this.nearAxis = "--";
		else this.nearAxis = nearAxis;
	}

	public String getNearVA() {
		return nearVA;
	}

	public void setNearVA(String nearVA) {
		if(nearVA == null)this.nearVA = "--";
		else this.nearVA = nearVA;
	}

	public String getDistanceVA() {
		return distanceVA;
	}

	public void setDistanceVA(String distanceVA) {
		if(distanceVA == null)this.distanceVA = "--";
		else this.distanceVA = distanceVA;
	}

	@Override
	public String toString() {
		return "EyeTestJasperResponse [distanceSPH=" + distanceSPH + ", nearSPH=" + nearSPH + ", distanceCylinder="
				+ distanceCylinder + ", nearCylinder=" + nearCylinder + ", distanceAxis=" + distanceAxis + ", nearAxis="
				+ nearAxis + ", nearVA=" + nearVA + ", distanceVA=" + distanceVA + "]";
	}
}
