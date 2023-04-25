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

	private String nearBaseCurve = "--";

	private String distanceBaseCurve = "--";

	private String nearDiameter = "--";

	private String distanceDiameter = "--";

	public String getDistanceSPH() {
		return distanceSPH;
	}

	public void setDistanceSPH(String distanceSPH) {
		if (distanceSPH == null)
			this.distanceSPH = "--";
		else {
			if (!distanceSPH.startsWith("-"))
				this.distanceSPH = "+" + distanceSPH;
			else
				this.distanceSPH = distanceSPH;
		}
	}

	public String getNearSPH() {
		return nearSPH;
	}

	public void setNearSPH(String nearSPH) {
		if (nearSPH == null)
			this.nearSPH = "--";
		else {
			if (!nearSPH.startsWith("-"))
				this.nearSPH = "+" + nearSPH;
			else
				this.nearSPH = nearSPH;
		}
	}

	public String getDistanceCylinder() {
		return distanceCylinder;
	}

	public void setDistanceCylinder(String distanceCylinder) {
		if (distanceCylinder == null)
			this.distanceCylinder = "--";
		else {
			if (!distanceCylinder.startsWith("-"))
				this.distanceCylinder = "+" + distanceCylinder;
			else
				this.distanceCylinder = distanceCylinder;
		}
	}

	public String getNearCylinder() {
		return nearCylinder;
	}

	public void setNearCylinder(String nearCylinder) {
		if (nearCylinder == null)
			this.nearCylinder = "--";
		else {
			if (!nearCylinder.startsWith("-"))
				this.nearCylinder = "+" + nearCylinder;
			else
				this.nearCylinder = nearCylinder;
		}
	}

	public String getDistanceAxis() {
		return distanceAxis;
	}

	public void setDistanceAxis(String distanceAxis) {
		if (distanceAxis == null || distanceAxis.equalsIgnoreCase("0"))
			this.distanceAxis = "--";
		else
			this.distanceAxis = distanceAxis;
	}

	public String getNearAxis() {
		return nearAxis;
	}

	public void setNearAxis(String nearAxis) {
		if (nearAxis == null || nearAxis.equalsIgnoreCase("0"))
			this.nearAxis = "--";
		else
			this.nearAxis = nearAxis;
	}

	public String getNearVA() {
		return nearVA;
	}

	public void setNearVA(String nearVA) {
		if (nearVA == null)
			this.nearVA = "--";
		else
			this.nearVA = nearVA;
	}

	public String getDistanceVA() {
		return distanceVA;
	}

	public void setDistanceVA(String distanceVA) {
		if (distanceVA == null)
			this.distanceVA = "--";
		else
			this.distanceVA = distanceVA;
	}

	public String getNearBaseCurve() {
		return nearBaseCurve;
	}

	public void setNearBaseCurve(String nearBaseCurve) {
		if (nearBaseCurve == null)
			this.nearBaseCurve = "--";
		else {
			if (!nearBaseCurve.startsWith("-"))
				this.nearBaseCurve = "+" + nearBaseCurve;
			else
				this.nearBaseCurve = nearBaseCurve;
		}
	}

	public String getDistanceBaseCurve() {
		return distanceBaseCurve;
	}

	public void setDistanceBaseCurve(String distanceBaseCurve) {
		if (distanceBaseCurve == null)
			this.distanceBaseCurve = "--";
		else {
			if (!distanceBaseCurve.startsWith("-"))
				this.distanceBaseCurve = "+" + distanceBaseCurve;
			else
				this.distanceBaseCurve = distanceBaseCurve;
		}
	}

	public String getNearDiameter() {
		return nearDiameter;
	}

	public void setNearDiameter(String nearDiameter) {
		if (nearDiameter == null)
			this.nearDiameter = "--";
		else {
			if (!nearDiameter.startsWith("-"))
				this.nearDiameter = "+" + nearDiameter;
			else
				this.nearDiameter = nearDiameter;
		}
	}

	public String getDistanceDiameter() {
		return distanceDiameter;
	}

	public void setDistanceDiameter(String distanceDiameter) {
		if (distanceDiameter == null)
			this.distanceDiameter = "--";
		else {
			if (!distanceDiameter.startsWith("-"))
				this.distanceDiameter = "+" + distanceDiameter;
			else
				this.distanceDiameter = distanceDiameter;
		}
	}

	@Override
	public String toString() {
		return "EyeTestJasperResponse [distanceSPH=" + distanceSPH + ", nearSPH=" + nearSPH + ", distanceCylinder="
				+ distanceCylinder + ", nearCylinder=" + nearCylinder + ", distanceAxis=" + distanceAxis + ", nearAxis="
				+ nearAxis + ", nearVA=" + nearVA + ", distanceVA=" + distanceVA + ", nearBaseCurve=" + nearBaseCurve
				+ ", distanceBaseCurve=" + distanceBaseCurve + ", nearDiameter=" + nearDiameter + ", distanceDiameter="
				+ distanceDiameter + "]";
	}
}
