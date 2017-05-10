package com.dpdocter.services;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.FileDownloadResponse;
import com.dpdocter.beans.FlexibleCounts;
import com.dpdocter.beans.Records;
import com.dpdocter.beans.Tags;
import com.dpdocter.beans.UserAllowanceDetails;
import com.dpdocter.beans.UserRecords;
import com.dpdocter.request.RecordsAddRequest;
import com.dpdocter.request.RecordsAddRequestMultipart;
import com.dpdocter.request.RecordsEditRequest;
import com.dpdocter.request.RecordsSearchRequest;
import com.dpdocter.request.TagRecordRequest;
import com.dpdocter.response.MailResponse;
import com.sun.jersey.multipart.FormDataBodyPart;

public interface RecordsService {
	Records addRecord(RecordsAddRequest request);

	void tagRecord(TagRecordRequest request);

	List<Records> searchRecords(RecordsSearchRequest request);

	List<Records> getRecords(int page, int size, String doctorId, String hospitalId, String locationId,
			String patientId, String updatedTime, boolean isOTPVerified, boolean discarded, boolean inHistory);

	List<Records> getRecordsByIds(List<ObjectId> recordIds, ObjectId visitId);

	Records getRecordById(String recordId);

	void emailRecordToPatient(String recordId, String doctorId, String locationId, String hospitalId,
			String emailAddress, List<String> fileIds);

	MailResponse getRecordMailData(String recordId, String doctorId, String locationId, String hospitalId,
			List<String> fileIds);

	Tags addEditTag(Tags tags);

	Tags deleteTag(String tagId, Boolean discarded);

	List<Tags> getAllTags(String doctorId, String locationId, String hospitalId);

	String getPatientEmailAddress(String patientId);

	FileDownloadResponse getRecordFile(String recordId, String fileId);

	Records deleteRecord(String recordId, Boolean discarded);

	Integer getRecordCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, boolean isOTPVerified);

	FlexibleCounts getFlexibleCounts(FlexibleCounts flexibleCounts);

	Records editRecord(RecordsEditRequest request);

	void changeLabelAndDescription(String recordId, String label, String explanation);

	List<Records> getRecordsByPatientId(String patientId, int page, int size, String updatedTime, Boolean discarded,
			Boolean isDoctorApp);

	Records addRecordsMultipart(FormDataBodyPart file, RecordsAddRequestMultipart request);

	String saveRecordsImage(FormDataBodyPart file, String patientIdString);

	Records changeRecordState(String recordId, String recordsState);

	UserRecords addUserRecordsMultipart(FormDataBodyPart file, UserRecords request);

	UserRecords getUserRecordById(String recordId);

	List<UserRecords> getUserRecordsByuserId(String userId, int page, int size, String updatedTime, Boolean discarded,
			Boolean isDoctor);

	UserAllowanceDetails getUserRecordAllowance(String userId, String mobileNumber);

	UserRecords deleteUserRecord(String recordId, Boolean discarded, Boolean isVisible);

	public Integer updateRecords();

	public Records deleteRecordsFile(String recordId, List<String> fileIds);

	public UserRecords deleteUserRecordsFile(String recordId, List<String> fileIds);
}
