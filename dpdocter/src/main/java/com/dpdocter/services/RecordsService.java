package com.dpdocter.services;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.FileDownloadResponse;
import com.dpdocter.beans.FlexibleCounts;
import com.dpdocter.beans.Records;
import com.dpdocter.beans.Tags;
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

    List<Records> getRecords(int page, int size, String doctorId, String hospitalId, String locationId, String patientId, String updatedTime,
	    boolean isOTPVerified, boolean discarded, boolean inHistory);

    List<Records> getRecordsByIds(List<ObjectId> recordIds);

    Records getRecordById(String recordId);

    void emailRecordToPatient(String recordId, String doctorId, String locationId, String hospitalId, String emailAddress);

    MailResponse getRecordMailData(String recordId, String doctorId, String locationId, String hospitalId);

    Tags addEditTag(Tags tags);

    Tags deleteTag(String tagId, Boolean discarded);

    List<Tags> getAllTags(String doctorId, String locationId, String hospitalId);

    String getPatientEmailAddress(String patientId);

    FileDownloadResponse getRecordFile(String recordId);

    Records deleteRecord(String recordId, Boolean discarded);

    Integer getRecordCount(String doctorId, String patientId, String locationId, String hospitalId, boolean isOTPVerified);

    FlexibleCounts getFlexibleCounts(FlexibleCounts flexibleCounts);

    Records editRecord(RecordsEditRequest request);

    void changeLabelAndDescription(String recordId, String label, String explanation);

    List<Records> getRecordsByPatientId(String patientId, int page, int size, String updatedTime, Boolean discarded);

    Records addRecordsMultipart(FormDataBodyPart file, RecordsAddRequestMultipart request);

	String saveRecordsImage(FormDataBodyPart file, String patientIdString);

}
