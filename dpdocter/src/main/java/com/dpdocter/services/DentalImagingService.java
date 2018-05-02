package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DentalDiagnosticService;
import com.dpdocter.beans.DentalImaging;
import com.dpdocter.beans.DentalImagingLocationServiceAssociation;
import com.dpdocter.beans.DentalImagingRequest;
import com.dpdocter.beans.Hospital;
import com.dpdocter.response.DentalImagingLocationServiceAssociationLookupResponse;

public interface DentalImagingService {

	DentalImaging addEditDentalImagingRequest(DentalImagingRequest request);

	List<DentalImaging> getRequests(String locationId, String hospitalId, String doctorId, Long from, Long to,
			String searchTerm, int size, int page);

	List<DentalDiagnosticService> getServices(String searchTerm, String type, int page, int size);

	Boolean addEditDentalImagingLocationServiceAssociation(List<DentalImagingLocationServiceAssociation> request);

	List<DentalImagingLocationServiceAssociationLookupResponse> getLocationAssociatedServices(String locationId,
			String hospitalId, String searchTerm, String type, int page, int size);

	List<Hospital> getHospitalList(String doctorId, String hospitalId);

}
