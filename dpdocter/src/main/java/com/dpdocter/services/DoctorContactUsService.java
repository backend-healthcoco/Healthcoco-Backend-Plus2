package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DoctorContactUs;
import com.dpdocter.enums.DoctorContactStateType;

public interface DoctorContactUsService {

	public String submitDoctorContactUSInfo(DoctorContactUs doctorContactUs);

	//public List<DoctorContactUs> getDoctorContactList(int page, int size);

	public DoctorContactUs updateDoctorContactState(String contactId, DoctorContactStateType contactState);

	public List<DoctorContactUs> getDoctorContactList(int page, int size, String searchTerm);

	Boolean resendWelcomeMessage(String emailAddress);
	
}
