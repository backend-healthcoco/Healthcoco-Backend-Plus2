package com.dpdocter.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.Records;
import com.dpdocter.beans.Tags;
import com.dpdocter.request.RecordsAddRequest;
import com.dpdocter.request.RecordsSearchRequest;

public interface RecordsService {
	Records addRecord(RecordsAddRequest request,MultipartFile image);
	 void tagRecord(List<Tags>tags, String recordId);
	 void changeReportLabel(String recordId,String label);
	 List<Records> searchRecords(RecordsSearchRequest request);
	 
	 
}
