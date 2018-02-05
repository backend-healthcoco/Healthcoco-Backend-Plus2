package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.LabReports;
import com.dpdocter.beans.PatientLabTestSample;
import com.dpdocter.request.DoctorLabReportsAddRequest;
import com.dpdocter.request.EditLabReportsRequest;
import com.dpdocter.request.LabReportsAddRequest;
import com.dpdocter.response.LabReportsResponse;
import com.sun.jersey.multipart.FormDataBodyPart;

public interface LabReportsService {

	public LabReports addLabReports(FormDataBodyPart file, LabReportsAddRequest request);

	List<LabReports> getLabReports(String labTestSampleId, String searchTerm, int page, int size);

	LabReports addLabReportBase64(FileDetails fileDetails, LabReportsAddRequest request);

	LabReports editLabReports(EditLabReportsRequest request);

	/*
	 * List<LabReportsResponse> getLabReportsForDoctor(String doctorId, String
	 * locationId, String hospitalId, String searchTerm, int page, int size);
	 * 
	 * List<LabReportsResponse> getLabReportsForLab(String doctorId, String
	 * locationId, String hospitalId, String searchTerm, int page, int size);
	 */

	LabReports addLabReportBase64(FileDetails fileDetails, DoctorLabReportsAddRequest request);

	List<LabReportsResponse> getLabReportsForDoctor(String doctorId, String locationId, String hospitalId,
			String doctorId2, String searchTerm, int page, int size);

	List<LabReportsResponse> getLabReportsForLab(String doctorId, String locationId, String hospitalId,
			String patientId, String searchTerm, int page, int size);

	LabReportsResponse changePatientShareStatus(String id, Boolean status);

	public String downloadLabreportPrintforParent(List<String> requestIds, boolean isParent);

}
