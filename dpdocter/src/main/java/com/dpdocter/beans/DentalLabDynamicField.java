package com.dpdocter.beans;

import java.util.ArrayList;
import java.util.List;

public class DentalLabDynamicField {

	private List<String> dentalLabRequestPermission = new ArrayList<>();
	private List<String> dentalWorkSamplePermission = new ArrayList<>();

	public List<String> getDentalLabRequestPermission() {
		return dentalLabRequestPermission;
	}

	public void setDentalLabRequestPermission(List<String> dentalLabRequestPermission) {
		this.dentalLabRequestPermission = dentalLabRequestPermission;
	}

	public List<String> getDentalWorkSamplePermission() {
		return dentalWorkSamplePermission;
	}

	public void setDentalWorkSamplePermission(List<String> dentalWorkSamplePermission) {
		this.dentalWorkSamplePermission = dentalWorkSamplePermission;
	}

	@Override
	public String toString() {
		return "DentalLabDynamicField [dentalLabRequestPermission=" + dentalLabRequestPermission
				+ ", dentalWorkSamplePermission=" + dentalWorkSamplePermission + "]";
	}

}
