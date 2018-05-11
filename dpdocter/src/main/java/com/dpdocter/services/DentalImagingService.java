package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DentalDiagnosticService;
import com.dpdocter.beans.DentalImaging;
import com.dpdocter.beans.DentalImagingLocationServiceAssociation;
import com.dpdocter.beans.DentalImagingReports;
import com.dpdocter.beans.DentalImagingRequest;
import com.dpdocter.beans.DoctorHospitalDentalImagingAssociation;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.Hospital;
import com.dpdocter.request.DentalImagingReportsAddRequest;
import com.dpdocter.request.DoctorLabReportsAddRequest;
import com.dpdocter.response.DentalImagingLocationResponse;
import com.dpdocter.response.DentalImagingLocationServiceAssociationLookupResponse;
import com.dpdocter.response.DentalImagingResponse;
import com.dpdocter.response.ServiceLocationResponse;

import common.util.web.Response;

public interface DentalImagingService {

	DentalImaging addEditDentalImagingRequest(DentalImagingRequest request);

	List<DentalImagingResponse> getRequests(String locationId, String hospitalId, String doctorId, Long from, Long to,
			String searchTerm, int size, int page);

	List<DentalDiagnosticService> getServices(String searchTerm, String type, int page, int size);

	Boolean addEditDentalImagingLocationServiceAssociation(List<DentalImagingLocationServiceAssociation> request);

	List<Hospital> getHospitalList(String doctorId, String hospitalId);

	List<DentalImagingLocationResponse> getServiceLocations(List<String> dentalImagingServiceId, String hospitalId,
			String searchTerm, int size, int page);

	List<DentalImagingLocationServiceAssociationLookupResponse> getLocationAssociatedServices(String locationId,
			String hospitalId, String searchTerm, String type, int page, int size, Boolean discarded);

	DentalImagingReports addDentalImagingReportBase64(FileDetails fileDetails, DentalImagingReportsAddRequest request);

	Boolean addEditDoctorHospitalDentalImagingAssociation(List<DoctorHospitalDentalImagingAssociation> request);

	List<DoctorHospitalDentalImagingAssociation> getDoctorHospitalAssociation(String doctorId);

}
