package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Group;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.request.ExportContactsRequest;
import com.dpdocter.request.GetDoctorContactsRequest;
import com.dpdocter.request.ImportContactsRequest;
import com.dpdocter.request.SearchRequest;

public interface ContactsService {

	List<PatientCard> getDoctorContacts(GetDoctorContactsRequest request);

	void blockPatient(String patientId, String docterId);

	Group addEditGroup(Group group);

	Boolean deleteGroup(String groupId);

	List<PatientCard> searchPatients(SearchRequest request);

	List<PatientCard> getDoctorsRecentlyVisitedContacts(String doctorId, int size, int page);

	List<PatientCard> getDoctorsMostVisitedContacts(String doctorId, int size, int page);

	int getcontactsTotalSize(GetDoctorContactsRequest request);

	List<Group> getAllGroups(String doctorId, String locationId, String hospitalId);

	Boolean importContacts(ImportContactsRequest request);

	Boolean exportContacts(ExportContactsRequest request);

}
