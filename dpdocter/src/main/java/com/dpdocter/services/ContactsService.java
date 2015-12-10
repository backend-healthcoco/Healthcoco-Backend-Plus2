package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DoctorContactsResponse;
import com.dpdocter.beans.Group;
import com.dpdocter.beans.PatientCard;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.request.ExportContactsRequest;
import com.dpdocter.request.GetDoctorContactsRequest;
import com.dpdocter.request.ImportContactsRequest;
import com.dpdocter.request.PatientGroupAddEditRequest;

public interface ContactsService {

    List<PatientCard> getDoctorContacts(GetDoctorContactsRequest request);

    void blockPatient(String patientId, String docterId);

    Group addEditGroup(Group group);

    Boolean deleteGroup(String groupId, Boolean discarded);

    List<PatientCard> getDoctorsRecentlyVisitedContacts(String doctorId, int size, int page);

    int getContactsTotalSize(GetDoctorContactsRequest request);

    Boolean importContacts(ImportContactsRequest request);

    Boolean exportContacts(ExportContactsRequest request);

    DoctorContactsResponse getDoctorContacts(String doctorId,  String locationId, String hospitalId, String updatedTime, boolean discarded, int page, int size);

    List<RegisteredPatientDetails> getDoctorContactsHandheld(String doctorId, String locationId, String hospitalId, String updatedTime, boolean discarded);

    PatientGroupAddEditRequest addGroupToPatient(PatientGroupAddEditRequest request);

    List<Group> getAllGroups(int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime, boolean discarded);

	DoctorContactsResponse getDoctorContactsSortedByName(String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded, int page, int size);

}
