package com.dpdocter.services;

import java.io.InputStream;
import java.util.List;

import com.dpdocter.beans.Records;
import com.dpdocter.beans.Tags;
import com.dpdocter.request.RecordsAddRequest;
import com.dpdocter.request.RecordsSearchRequest;

public interface RecordsService {
	Records addRecord(RecordsAddRequest request,InputStream image,String filename);
	 void tagRecord(List<Tags>tags, String recordId);
	 void changeReportLabel(String recordId,String label);
	 List<Records> searchRecords(RecordsSearchRequest request);
	 
	 
}
