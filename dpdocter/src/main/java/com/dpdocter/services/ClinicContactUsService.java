package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.ClinicContactUs;
import com.dpdocter.enums.DoctorContactStateType;

public interface ClinicContactUsService {
	
	public String submitClinicContactUSInfo(ClinicContactUs clinicContactUs);

	//public List<DoctorContactUs> getDoctorContactList(int page, int size);

	public ClinicContactUs updateClinicContactState(String contactId, DoctorContactStateType contactState);

	public List<ClinicContactUs> getDoctorContactList(int page, int size, String searchTerm);

}
