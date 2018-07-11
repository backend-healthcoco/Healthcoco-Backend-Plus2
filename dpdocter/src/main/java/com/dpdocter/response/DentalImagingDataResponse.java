package com.dpdocter.response;

import java.util.List;

import com.dpdocter.beans.CBDTArch;
import com.dpdocter.beans.CBDTQuadrant;
import com.dpdocter.beans.FOV;

public class DentalImagingDataResponse {

	private List<CBDTQuadrant> cbdtQuadrants;
	private List<CBDTArch> cbdtArchs;
	private List<FOV> fovs;

	public List<CBDTQuadrant> getCbdtQuadrants() {
		return cbdtQuadrants;
	}

	public void setCbdtQuadrants(List<CBDTQuadrant> cbdtQuadrants) {
		this.cbdtQuadrants = cbdtQuadrants;
	}

	public List<CBDTArch> getCbdtArchs() {
		return cbdtArchs;
	}

	public void setCbdtArchs(List<CBDTArch> cbdtArchs) {
		this.cbdtArchs = cbdtArchs;
	}

	public List<FOV> getFovs() {
		return fovs;
	}

	public void setFovs(List<FOV> fovs) {
		this.fovs = fovs;
	}

	@Override
	public String toString() {
		return "DentalImagingData [cbdtQuadrants=" + cbdtQuadrants + ", cbdtArchs=" + cbdtArchs + ", fovs=" + fovs
				+ "]";
	}

}
