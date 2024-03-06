package com.dpdocter.beans;

import java.util.List;

import com.dpdocter.enums.NdhmAuthMethods;
import com.dpdocter.enums.NdhmPurpose;

public class NdhmAuth {

	private NdhmPurpose purpose; 
	
	private List<NdhmAuthMethods> modes;

	public NdhmPurpose getPurpose() {
		return purpose;
	}

	public void setPurpose(NdhmPurpose purpose) {
		this.purpose = purpose;
	}

	public List<NdhmAuthMethods> getModes() {
		return modes;
	}

	public void setModes(List<NdhmAuthMethods> modes) {
		this.modes = modes;
	}
	
	
	
	
}
