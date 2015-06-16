package com.dpdocter.services;

import java.io.File;
import java.util.List;

import com.dpdocter.beans.Records;
import com.dpdocter.beans.Tags;
import com.dpdocter.request.RecordsAddRequest;
import com.dpdocter.request.RecordsSearchRequest;
import com.dpdocter.request.TagRecordRequest;

public interface RecordsService {
	Records addRecord(RecordsAddRequest request);

	void tagRecord(TagRecordRequest request);

	void changeReportLabel(String recordId, String label);

	List<Records> searchRecords(RecordsSearchRequest request);

	List<Records> getRecordsByIds(List<String> recordIds);

	void emailRecordToPatient(String recordId, String emailAddr);

	Tags addEditTag(Tags tags);

	void deleteTag(String tagId);

	List<Tags> getAllTags(String doctorId, String locationId, String hospitalId);

	String getPatientEmailAddress(String patientId);

	File getRecordFile(String recordId);

	void deleteRecord(String recordId);

	List<Records> searchRecords(String doctorId, String locationId, String hospitalId, String createdTime);

	Integer getRecordCount(String doctorId, String locationId, String hospitalId);

	boolean editDescription(String recordId, String description);

}
