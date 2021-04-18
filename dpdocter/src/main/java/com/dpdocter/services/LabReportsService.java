package com.dpdocter.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.LabReports;
import com.dpdocter.request.DoctorLabReportsAddRequest;
import com.dpdocter.request.EditLabReportsRequest;
import com.dpdocter.request.LabReportsAddRequest;
import com.dpdocter.response.LabReportsResponse;

public interface LabReportsService {

	public LabReports addLabReports(MultipartFile file, LabReportsAddRequest request);

	List<LabReports> getLabReports(String labTestSampleId, String searchTerm, long page, int size);

	LabReports addLabReportBase64(MultipartFile file, DoctorLabReportsAddRequest doctorLabReportsAddRequest);

	LabReports editLabReports(EditLabReportsRequest request);

	/*
	 * List<LabReportsResponse> getLabReportsForDoctor(String doctorId, String
	 * locationId, String hospitalId, String searchTerm, long page, int size);
	 * 
	 * List<LabReportsResponse> getLabReportsForLab(String doctorId, String
	 * locationId, String hospitalId, String searchTerm, long page, int size);
	 */

	List<LabReportsResponse> getLabReportsForDoctor(String doctorId, String locationId, String hospitalId,
			String doctorId2, String searchTerm, long page, int size);

	List<LabReportsResponse> getLabReportsForLab(String doctorId, String locationId, String hospitalId,
			String patientId, String searchTerm, long page, int size);

	LabReportsResponse changePatientShareStatus(String id, Boolean status);

	public String downloadLabreportPrint(List<String> ids);

	LabReports addLabReportBase64(MultipartFile file, LabReportsAddRequest request);

}