package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DentalLabPickup;
import com.dpdocter.beans.DentalWork;
import com.dpdocter.enums.LabType;
import com.dpdocter.request.AddEditCustomWorkRequest;
import com.dpdocter.request.DentalLabPickupRequest;

public interface DentalLabService {

	DentalWork addEditCustomWork(AddEditCustomWorkRequest request);

	List<DentalWork> getCustomWorks(int page, int size, String searchTerm);

	DentalWork deleteCustomWork(String id, boolean discarded);

	Boolean changeLabType(String doctorId, String locationId, LabType labType);

	DentalLabPickup addEditDentalLabPickupRequest(DentalLabPickupRequest request);

	/*
	 * DentalLabDoctorAssociation
	 * addEditDentalLabDoctorAssociation(DentalLabDoctorAssociation request);
	 * 
	 * List<DentalLabDoctorAssociationLookupResponse>
	 * getDentalLabDoctorAssociations(String locationId, int page, int size, String
	 * searchTerm);
	 */

}
