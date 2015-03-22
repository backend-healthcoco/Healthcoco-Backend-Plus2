package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.Records;
import com.dpdocter.beans.Tags;
import com.dpdocter.request.RecordsAddRequest;
import com.dpdocter.request.RecordsSearchRequest;
import com.dpdocter.request.TagRecordRequest;

public interface RecordsService {
	Records addRecord(RecordsAddRequest request);
	 void tagRecord(TagRecordRequest request);
	 void changeReportLabel(String recordId,String label);
	 List<Records> searchRecords(RecordsSearchRequest request);
	 
	 Tags addEditTag(Tags tags);
	 List<Tags> getAllTags(String doctorId,String locationId,String hospitalId);
	  String getPatientEmailAddress(String patientId);
	 
	 
}
