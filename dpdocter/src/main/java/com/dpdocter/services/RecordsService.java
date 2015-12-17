package com.dpdocter.services;

import java.io.File;
import java.util.List;

import javax.ws.rs.core.UriInfo;

import com.dpdocter.beans.FlexibleCounts;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.Records;
import com.dpdocter.beans.Tags;
import com.dpdocter.request.RecordsAddRequest;
import com.dpdocter.request.RecordsEditRequest;
import com.dpdocter.request.RecordsSearchRequest;
import com.dpdocter.request.TagRecordRequest;

public interface RecordsService {
    Records addRecord(RecordsAddRequest request);

    void tagRecord(TagRecordRequest request);

    List<Records> searchRecords(RecordsSearchRequest request);

    List<Records> getRecords(int page, int size, String doctorId, String hospitalId, String locationId, String patientId, String updatedTime,
	    boolean isOTPVerified, boolean discarded, boolean inHistory);

    List<Records> getRecordsByIds(List<String> recordIds);

    Records getRecordById(String recordId);

    void emailRecordToPatient(String recordId, String doctorId, String locationId, String hospitalId, String emailAddress, UriInfo uriInfo);

    MailAttachment getRecordMailData(String recordId, String doctorId, String locationId, String hospitalId, UriInfo uriInfo);

    Tags addEditTag(Tags tags);

    void deleteTag(String tagId);

    List<Tags> getAllTags(String doctorId, String locationId, String hospitalId);

    String getPatientEmailAddress(String patientId);

    File getRecordFile(String recordId);

    void deleteRecord(String recordId, Boolean discarded);

    Integer getRecordCount(String doctorId, String patientId, String locationId, String hospitalId);

    FlexibleCounts getFlexibleCounts(FlexibleCounts flexibleCounts);

    Records editRecord(RecordsEditRequest request);

    void changeLabelAndDescription(String recordId, String label, String description);

}
