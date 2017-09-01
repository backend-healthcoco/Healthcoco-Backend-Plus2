package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.LabReports;
import com.dpdocter.request.EditLabReportsRequest;
import com.dpdocter.request.LabReportsAddRequest;
import com.sun.jersey.multipart.FormDataBodyPart;

public interface LabReportsService {

	public LabReports addLabReports(FormDataBodyPart file, LabReportsAddRequest request);

	List<LabReports> getLabReports(String labTestSampleId, String searchTerm, int page, int size);

	LabReports addLabReportBase64(FileDetails fileDetails, LabReportsAddRequest request);

	LabReports editLabReports(EditLabReportsRequest request);
	
}
