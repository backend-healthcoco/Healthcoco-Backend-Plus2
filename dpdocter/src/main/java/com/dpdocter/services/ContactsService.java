package com.dpdocter.services;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.Branch;
import com.dpdocter.beans.DoctorContactsResponse;
import com.dpdocter.beans.Group;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.request.BulkSMSRequest;
import com.dpdocter.request.ExportRequest;
import com.dpdocter.request.GetDoctorContactsRequest;
import com.dpdocter.request.ImportContactsRequest;
import com.dpdocter.request.PatientGroupAddEditRequest;

import common.util.web.Response;

public interface ContactsService {

	DoctorContactsResponse getDoctorContacts(GetDoctorContactsRequest request);

	void blockPatient(String patientId, String docterId);

	Group addEditGroup(Group group);

	Group deleteGroup(String groupId, Boolean discarded);

	int getContactsTotalSize(GetDoctorContactsRequest request);

	Boolean importContacts(ImportContactsRequest request);

	Boolean exportContacts(ExportRequest request);

	DoctorContactsResponse getDoctorContacts(String doctorId, String locationId, String hospitalId, String updatedTime,
			boolean discarded, long page, int size, String role);

	public Integer getDoctorContactsHandheldCount(String doctorId, String locationId, String hospitalId,
			boolean discarded, String role, String searchTerm);

	PatientGroupAddEditRequest addGroupToPatient(PatientGroupAddEditRequest request);

	Response<Object> getAllGroups(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, boolean discarded);

	DoctorContactsResponse getDoctorContactsSortedByName(String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, long page, int size, String role);

	DoctorContactsResponse getSpecifiedPatientCards(Collection<ObjectId> patientIds, String doctorId, String locationId,
			String hospitalId, long page, int size, String updatedTime, Boolean discarded, Boolean sortByFirstName,
			String role) throws Exception;

	Boolean sendSMSToGroup(BulkSMSRequest request);

	List<RegisteredPatientDetails> getDoctorContactsHandheld(String doctorId, String locationId, String hospitalId,
			String updatedTime, boolean discarded, String role, int page, int size, String searchTerm, String userId);

	Branch addEditBranch(Branch branch);

	Branch deleteBranch(String branchId, Boolean discarded);

	Branch getBranchById(String branchId);

	Response<Object> getBranches(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, String searchTerm);

}
