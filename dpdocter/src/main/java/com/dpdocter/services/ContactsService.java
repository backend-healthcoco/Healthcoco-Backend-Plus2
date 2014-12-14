package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.PatientCard;

public interface ContactsService {
	
	List<PatientCard> getDoctorContacts(String doctorId,Boolean blocked,int page,int size);
	void blockPatient(String patientId,String docterId);

}
