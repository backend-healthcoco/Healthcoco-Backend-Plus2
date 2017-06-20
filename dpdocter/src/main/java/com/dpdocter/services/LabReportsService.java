package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.LabReports;
import com.dpdocter.request.LabReportsAddRequest;
import com.sun.jersey.multipart.FormDataBodyPart;

public interface LabReportsService {

	public LabReports addLabReports(FormDataBodyPart file, LabReportsAddRequest request);
	
	public List<LabReports> getLabReports(String labTestSampleId, String requestId,
			String searchTerm, int page, int size);
	
}
