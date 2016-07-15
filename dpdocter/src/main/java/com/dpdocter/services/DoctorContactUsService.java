package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DoctorContactUs;

public interface DoctorContactUsService {

	public DoctorContactUs submitDoctorContactUSInfo(DoctorContactUs doctorContactUs);

	public List<DoctorContactUs> getDoctorContactList(int page, int size);
	
}
