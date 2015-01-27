package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Group;
import com.dpdocter.beans.PatientCard;

public interface ContactsService {
	
	List<PatientCard> getDoctorContacts(String doctorId,Boolean blocked,int page,int size);
	void blockPatient(String patientId,String docterId);
	Group addGroup(Group group);
	boolean addToGroup(String userId,String groupId);

}
