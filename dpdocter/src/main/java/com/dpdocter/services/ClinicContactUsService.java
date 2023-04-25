package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.ClinicContactUs;
import com.dpdocter.enums.DoctorContactStateType;

public interface ClinicContactUsService {
	
	public String submitClinicContactUSInfo(ClinicContactUs clinicContactUs);

	public ClinicContactUs updateClinicContactState(String contactId, DoctorContactStateType contactState);

	public List<ClinicContactUs> getDoctorContactList(long page, int size, String searchTerm);

}
