package com.dpdocter.services;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.FileDownloadResponse;
import com.dpdocter.beans.FlexibleCounts;
import com.dpdocter.beans.MultipartUploadFile;
import com.dpdocter.beans.Records;
import com.dpdocter.beans.RecordsFile;
import com.dpdocter.beans.Tags;
import com.dpdocter.beans.UserAllowanceDetails;
import com.dpdocter.beans.UserRecords;
import com.dpdocter.request.MyFiileRequest;
import com.dpdocter.request.RecordsAddRequest;
import com.dpdocter.request.RecordsAddRequestMultipart;
import com.dpdocter.request.RecordsEditRequest;
import com.dpdocter.request.RecordsSearchRequest;
import com.dpdocter.request.TagRecordRequest;
import com.dpdocter.response.MailResponse;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.Response;

public interface RecordsService {
	Records addRecord(RecordsAddRequest request, String createdBy);

	void tagRecord(TagRecordRequest request);

	List<Records> searchRecords(RecordsSearchRequest request);

	List<Records> getRecords(long page, int size, String doctorId, String hospitalId, String locationId,
			String patientId, String updatedTime, boolean isOTPVerified, boolean discarded, boolean inHistory);

	List<Records> getRecordsByIds(List<ObjectId> recordIds, ObjectId visitId);

	Records getRecordById(String recordId);

	public void emailRecordToPatient(String recordId, String doctorId, String locationId, String hospitalId,
			String emailAddress);

	public MailResponse getRecordMailData(String recordId, String doctorId, String locationId, String hospitalId);

	Tags addEditTag(Tags tags);

	Tags deleteTag(String tagId, Boolean discarded);

	List<Tags> getAllTags(String doctorId, String locationId, String hospitalId);

	String getPatientEmailAddress(String patientId);

	FileDownloadResponse getRecordFile(String recordId);

	Records deleteRecord(String recordId, Boolean discarded);

	Integer getRecordCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, boolean isOTPVerified);

	FlexibleCounts getFlexibleCounts(FlexibleCounts flexibleCounts);

	Records editRecord(RecordsEditRequest request);

	void changeLabelAndDescription(String recordId, String label, String explanation);

	Response<Object> getRecordsByPatientId(String patientId, int page, int size, String updatedTime, Boolean discarded,
			Boolean isDoctorApp, String sortBy);

	Records addRecordsMultipart(FormDataBodyPart file, RecordsAddRequestMultipart request);

	String saveRecordsImage(FormDataBodyPart file, String patientIdString);

	Records changeRecordState(String recordId, String recordsState);

	UserRecords addUserRecords(UserRecords request);

	UserRecords getUserRecordById(String recordId);

	Response<Object> getUserRecordsByuserId(String patientId, String doctorId, String locationId, String hospitalId,
			long page, int size, String updatedTime, Boolean discarded);

	UserAllowanceDetails getUserRecordAllowance(String userId, String mobileNumber);

	UserRecords deleteUserRecord(String recordId, Boolean discarded, Boolean isVisible);

	public UserRecords deleteUserRecordsFile(String recordId, List<String> fileIds);

	UserRecords shareUserRecordsFile(String recordId, String patientId);

	RecordsFile uploadUserRecord(FormDataBodyPart file, MyFiileRequest request);

	List<Records> getRecordsByDoctorId(String doctorId, long page, int size, String updatedTime, Boolean discarded);

	public Boolean updateShareWithPatient(String recordId);

	Integer getUserRecordsByuserIdCount(String patientId, String doctorId, String locationId, String hospitalId,
			long page, int size, String updatedTime, Boolean discarded);

	MultipartUploadFile uploadImage(FormDataBodyPart file);

}
