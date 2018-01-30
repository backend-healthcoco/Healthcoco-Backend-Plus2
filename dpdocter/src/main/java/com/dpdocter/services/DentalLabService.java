package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.CollectionBoyDoctorAssociation;
import com.dpdocter.beans.DentalLabDoctorAssociation;
import com.dpdocter.beans.DentalLabPickup;
import com.dpdocter.beans.DentalWork;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.RateCardDentalWorkAssociation;
import com.dpdocter.beans.RateCardDoctorAssociation;
import com.dpdocter.beans.User;
import com.dpdocter.enums.LabType;
import com.dpdocter.request.AddEditCustomWorkRequest;
import com.dpdocter.request.DentalLabPickupRequest;
import com.dpdocter.response.CBDoctorAssociationLookupResponse;
import com.dpdocter.response.DentalLabDoctorAssociationLookupResponse;
import com.dpdocter.response.DentalLabPickupResponse;

public interface DentalLabService {

	DentalWork addEditCustomWork(AddEditCustomWorkRequest request);

	List<DentalWork> getCustomWorks(int page, int size, String searchTerm);

	DentalWork deleteCustomWork(String id, boolean discarded);

	Boolean changeLabType(String doctorId, String locationId, LabType labType);

	DentalLabPickup addEditDentalLabPickupRequest(DentalLabPickupRequest request);

	Boolean addEditRateCardDentalWorkAssociation(List<RateCardDentalWorkAssociation> request);

	/*List<RateCardDentalWorkAssociation> getRateCardWorks(int page, int size, String searchTerm, String rateCardId,
			Boolean discarded);
*/
	//Boolean addEditRateCardDoctorAssociation(List<RateCardDoctorAssociation> request);

	DentalLabDoctorAssociation addEditDentalLabDoctorAssociation(DentalLabDoctorAssociation request);

	/*List<DentalLabDoctorAssociationLookupResponse> getDentalLabDoctorAssociations(String locationId, int page, int size,
			String searchTerm);*/

	/*List<RateCardDoctorAssociation> getRateCards(int page, int size, String searchTerm, String doctorId,
			Boolean discarded);*/

	Boolean addEditCollectionBoyDoctorAssociation(List<CollectionBoyDoctorAssociation> request);

	List<CBDoctorAssociationLookupResponse> getCBAssociatedDoctors(String doctorId, String dentalLabId, String collectionBoyId, int size, int page);

	Boolean addEditDentalLabDoctorAssociation(List<DentalLabDoctorAssociation> request);

	RateCardDoctorAssociation addEditRateCardDoctorAssociation(RateCardDoctorAssociation request);

	List<RateCardDoctorAssociation> getRateCards(int page, int size, String searchTerm, String doctorId,
			String dentalLabId, Boolean discarded);

	List<User> getDentalLabDoctorAssociations(String locationId, String doctorId,
			int page, int size, String searchTerm);

	List<DentalLabPickupResponse> getRequests(String dentalLabId, String doctorId, Long from, Long to,
			String searchTerm, String status, Boolean isAcceptedAtLab, Boolean isCompleted, int size, int page);

	List<Location> getDentalLabDoctorAssociationsForDoctor(String doctorId, int page, int size, String searchTerm);

	List<RateCardDentalWorkAssociation> getRateCardWorks(int page, int size, String searchTerm, String dentalLabId,
			String doctorId, Boolean discarded);

	Boolean changeStatus(String dentalLabPickupId, String status);

	/*
	 * DentalLabDoctorAssociation
	 * addEditDentalLabDoctorAssociation(DentalLabDoctorAssociation request);
	 * 
	 * List<DentalLabDoctorAssociationLookupResponse>
	 * getDentalLabDoctorAssociations(String locationId, int page, int size, String
	 * searchTerm);
	 */

}
