package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DentalDiagnosticService;
import com.dpdocter.beans.DentalImaging;
import com.dpdocter.beans.DentalImagingInvoice;
import com.dpdocter.beans.DentalImagingLocationServiceAssociation;
import com.dpdocter.beans.DentalImagingReports;
import com.dpdocter.beans.DentalImagingRequest;
import com.dpdocter.beans.DoctorHospitalDentalImagingAssociation;
import com.dpdocter.beans.FileDetails;
import com.dpdocter.beans.Hospital;
import com.dpdocter.elasticsearch.response.DentalImagingInvoiceResponse;
import com.dpdocter.request.DentalImagingLabDoctorRegistrationRequest;
import com.dpdocter.request.DentalImagingReportsAddRequest;
import com.dpdocter.response.AnalyticResponse;
import com.dpdocter.response.DentalImagingDataResponse;
import com.dpdocter.response.DentalImagingLocationResponse;
import com.dpdocter.response.DentalImagingLocationServiceAssociationLookupResponse;
import com.dpdocter.response.DentalImagingResponse;
import com.dpdocter.response.DentalImagingVisitAnalyticsResponse;
import com.dpdocter.response.DoctorHospitalDentalImagingAssociationResponse;
import com.dpdocter.response.PatientDentalImagignVisitAnalyticsResponse;

public interface DentalImagingService {

	DentalImagingResponse addEditDentalImagingRequest(DentalImagingRequest request);

	List<DentalDiagnosticService> getServices(String searchTerm, String type, long page, int size);

	Boolean addEditDentalImagingLocationServiceAssociation(List<DentalImagingLocationServiceAssociation> request);

	List<Hospital> getHospitalList(String doctorId, String hospitalId);

	List<DentalImagingLocationResponse> getServiceLocations(List<String> dentalImagingServiceId, String hospitalId,
			String searchTerm, int size, long page);

	List<DentalImagingLocationServiceAssociationLookupResponse> getLocationAssociatedServices(String locationId,
			String hospitalId, String searchTerm, String type, long page, int size, Boolean discarded);

	DentalImagingReports addDentalImagingReportBase64(FileDetails fileDetails, DentalImagingReportsAddRequest request);

	Boolean addEditDoctorHospitalDentalImagingAssociation(List<DoctorHospitalDentalImagingAssociation> request);

	List<DoctorHospitalDentalImagingAssociation> getDoctorHospitalAssociation(String doctorId);

	List<DentalImagingResponse> getRequests(String locationId, String hospitalId, String doctorId, Long from, Long to,
			String searchTerm, int size, long page, String type);

	DentalImaging discardRequest(String id, boolean discarded);

	DentalImagingReports discardReport(String id, boolean discarded);

	List<DoctorHospitalDentalImagingAssociationResponse> getHospitalAssociatedDoctor(String hospitalId,
			String searchTerm, int size, long page);

	Boolean dentalLabDoctorRegistration(DentalImagingLabDoctorRegistrationRequest request);

	Double getInvoiceAmount(String doctorId, String locationId, String hospitalId, String fromDate, String toDate,
			String dentalImagingLocationId, String dentalImagingHospitalId, long page, int size);

	DentalImagingInvoice discardInvoice(String id, boolean discarded);

	DentalImagingInvoice changeInvoicePaymentStatus(String id, boolean isPaid);

	List<DentalImagingInvoiceResponse> getInvoices(String doctorId, String locationId, String hospitalId,
			String dentalImagingLocationId, String dentalImagingHospitalId, Long from, Long to, String searchTerm,
			int size, long page, Boolean isPaid);

	public DentalImagingInvoiceResponse getInvoice(String invoiceId);

	DentalImagingVisitAnalyticsResponse getVisitAnalytics(String fromDate, String toDate,
			String dentalImagingLocationId, String dentalImagingHospitalId);

	List<DentalImagingReports> getReports(String doctorId, String locationId, String hospitalId,
			String dentalImagingLocationId, String dentalImagingHospitalId, String patientId, Long from, Long to,
			String searchTerm, int size, long page);

	List<AnalyticResponse> getPatientVisitAnalytics(Long fromDate, Long toDate, String dentalImagingLocationId,
			String dentalImagingHospitalId, String searchType);

	public String downloadInvoice(String invoiceId);

	Boolean emailInvoice(String invoiceId, String emailAddress);

	Boolean emailReports(String id, String emailAddress);

	List<PatientDentalImagignVisitAnalyticsResponse> getDoctorVisitAnalytics(Long fromDate, Long toDate,
			String dentalImagingLocationId, String dentalImagingHospitalId, String doctorId, String searchType);

	DentalImaging changeVisitedStatus(String id, boolean isVisited);

	List<PatientDentalImagignVisitAnalyticsResponse> getDoctorVisitAnalyticsCount(Long fromDate, Long toDate,
			String dentalImagingLocationId, String dentalImagingHospitalId, String doctorId, String searchType);

	PatientDentalImagignVisitAnalyticsResponse getDetailedDoctorVisitAnalytics(Long fromDate, Long toDate,
			String dentalImagingLocationId, String dentalImagingHospitalId, String doctorId, String searchType,
			long page, int size);

	DentalImagingDataResponse getDentalImagingData();

	DentalImagingInvoice addEditInvoice(DentalImagingInvoice request, Boolean fromRequest);

	Integer countHospitalAssociatedDoctor(String hospitalId, String searchTerm);


}
