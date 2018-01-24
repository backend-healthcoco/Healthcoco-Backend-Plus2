package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DoctorLabReport;
import com.dpdocter.beans.RecordsFile;
import com.dpdocter.request.DoctorLabDoctorReferenceRequest;
import com.dpdocter.request.DoctorLabFavouriteDoctorRequest;
import com.dpdocter.request.DoctorLabReportUploadRequest;
import com.dpdocter.request.MyFiileRequest;
import com.dpdocter.response.DoctorLabFavouriteDoctorResponse;
import com.dpdocter.response.DoctorLabReportResponse;
import com.sun.jersey.multipart.FormDataBodyPart;

public interface DoctorLabService {

	public DoctorLabReport addDoctorLabReport(DoctorLabReport request);

	public RecordsFile uploadDoctorLabReport(DoctorLabReportUploadRequest request);

	public RecordsFile uploadDoctorLabReportMultipart(FormDataBodyPart file, MyFiileRequest request);

	public List<DoctorLabReportResponse> getDoctorLabReport(int page, int size, String patientId, String doctorId,
			String locationId, String hospitalId, String searchTerm, Boolean discarded, Boolean doctorLab);

	public Boolean addDoctorToFavouriteList(DoctorLabFavouriteDoctorRequest request);

	public List<DoctorLabFavouriteDoctorResponse> getFavouriteList(int size, int page, String searchTerm,
			String doctorId, String locationId, String hospitalId, String speciality);

	public List<DoctorLabFavouriteDoctorRequest> searchDoctor(int size, int page, String searchTerm, String doctorId,
			String locationId, String hospitalId, String speciality, String city);

	public Boolean addDoctorReference(DoctorLabDoctorReferenceRequest request);

	public DoctorLabReportResponse getDoctorLabReportById(String id, Boolean doctorLab);
}
