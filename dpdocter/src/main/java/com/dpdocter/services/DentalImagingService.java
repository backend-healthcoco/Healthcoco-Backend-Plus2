package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DentalImaging;
import com.dpdocter.beans.DentalImagingRequest;

public interface DentalImagingService {

	DentalImaging addEditDentalImagingRequest(DentalImagingRequest request);

	List<DentalImaging> getRequests(String locationId, String hospitalId, String doctorId, Long from, Long to,
			String searchTerm, int size, int page);

}
