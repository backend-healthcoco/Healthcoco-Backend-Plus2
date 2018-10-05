package com.dpdocter.services;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.CollectionBoy;
import com.dpdocter.beans.CollectionBoyLabAssociation;
import com.dpdocter.beans.DentalWork;
import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.LabTestPickup;
import com.dpdocter.beans.LabTestPickupLookupResponse;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.RateCard;
import com.dpdocter.beans.RateCardLabAssociation;
import com.dpdocter.beans.RateCardTestAssociation;
import com.dpdocter.beans.Specimen;
import com.dpdocter.request.AddEditCustomWorkRequest;
import com.dpdocter.request.AddEditLabTestPickupRequest;
import com.dpdocter.request.DynamicCollectionBoyAllocationRequest;
import com.dpdocter.response.CollectionBoyResponse;
import com.dpdocter.response.DynamicCollectionBoyAllocationResponse;
import com.dpdocter.response.LabTestGroupResponse;
import com.dpdocter.response.PatientLabTestSampleReportResponse;
import com.dpdocter.response.RateCardTestAssociationByLBResponse;
import com.dpdocter.response.RateCardTestAssociationLookupResponse;

public interface LocationServices {
	public List<GeocodedLocation> geocodeLocation(String address);

	List<GeocodedLocation> geocodeTimeZone(Double latitude, Double longitude);

	public Location addEditRecommedation(String locationId, String patientId);

	Boolean setDefaultLab(String locationId, String defaultLabId);

	LabTestPickup addEditLabTestPickupRequest(AddEditLabTestPickupRequest request);

	Boolean verifyCRN(String locationId, String crn, String requestId);

	// List<CollectionBoyResponse> getCollectionBoyList(int size, long page, String
	// locationId, String searchTerm);

	// List<Location> getAssociatedLabs(String locationId, Boolean isParent);

	RateCardTestAssociation addEditRateCardTestAssociation(RateCardTestAssociation request);

	List<Location> addCollectionBoyAssociatedLabs(List<CollectionBoyLabAssociation> collectionBoyLabAssociations);

	List<RateCardTestAssociationLookupResponse> getRateCardTests(long page, int size, String searchTerm,
			String rateCardId, String labId);

	RateCard addEditRateCard(RateCard request);

	List<RateCard> getRateCards(long page, int size, String searchTerm, String locationId);

	List<Location> getCBAssociatedLabs(String parentLabId, String daughterLabId, String collectionBoyId, int size,
			long page);

	// List<LabTestPickupLookupResponse> getRequestForCB(String collectionBoyId,
	// int size, long page);

	LabTestPickupLookupResponse getLabTestPickupByRequestId(String requestId);

	LabTestPickupLookupResponse getLabTestPickupById(String id);

	CollectionBoy discardCB(String collectionBoyId, Boolean discarded);

	CollectionBoy changeAvailability(String collectionBoyId, Boolean isAvailable);

	RateCardLabAssociation addEditRateCardAssociatedLab(RateCardLabAssociation rateCardLabAssociation);

	RateCardLabAssociation getRateCardAssociatedLab(String daughterLabId, String parentLabId);
	/*
	 * List<Location> getClinics(long page, int size, String hospitalId, Boolean
	 * isClinic, Boolean isLab, Boolean isParent, String searchTerm);
	 */

	// Integer getCBCount(int size, long page, String locationId, String
	// searchTerm);

	List<Specimen> getSpecimenList(long page, int size, String searchTerm);

	Integer getRateCardCount(String searchTerm, String locationId);

	// List<Location> getAssociatedLabs(String locationId, Boolean isParent,
	// String searchTerm);

	CollectionBoy editCollectionBoy(CollectionBoy collectionBoy);

	Boolean addEditRateCardTestAssociation(List<RateCardTestAssociation> request);

	/*
	 * List<RateCardTestAssociationLookupResponse> getRateCardTests(long page, int
	 * size, String searchTerm, String daughterLabId, String parentLabId, String
	 * labId);
	 */
	// List<LabTestPickupLookupResponse> getRequestForDL(String daughterLabId,
	// int size, long page);

	List<RateCardTestAssociationByLBResponse> getRateCardTests(long page, int size, String searchTerm,
			String daughterLabId, String parentLabId, String labId, String specimen);

	RateCard getDLRateCard(String daughterLabId, String parentLabId);

	// List<LabTestPickupLookupResponse> getRequestForPL(String parentLabId, int
	// size, long page);

	Integer getCBCount(String locationId, String searchTerm, String labType);

	List<Location> getAssociatedLabs(String locationId, Boolean isParent, String searchTerm, long page, int size);

	public List<PatientLabTestSampleReportResponse> getLabReports(String locationId, Boolean isParent, Long from,
			Long to, String searchTerm, long page, int size);

	Integer countLabReports(String locationId, Boolean isParent, Long from, Long to, String searchTerm);

	List<LabTestPickupLookupResponse> getRequestForDL(String daughterLabId, Long from, Long to, String searchTerm,
			int size, long page);

	List<LabTestPickupLookupResponse> getRequestForCB(String collectionBoyId, Long from, Long to, String searchTerm,
			int size, long page);

	Boolean updateRequestStatus(String id, String status);

	List<LabTestPickupLookupResponse> getRequestForPL(String parentLabId, String daughterLabId, Long from, Long to,
			String searchTerm, int size, long page);

	List<LabTestGroupResponse> getGroupedLabTests(long page, int size, String searchTerm, String daughterLabId,
			String parentLabId, String labId);

	List<DentalWork> getCustomWorks(long page, int size, String searchTerm);

	DentalWork addEditCustomWork(AddEditCustomWorkRequest request);

	DentalWork deleteCustomWork(String id, boolean discarded);

	List<CollectionBoyResponse> getCollectionBoyList(int size, long page, String locationId, String searchTerm,
			String labtype);

	List<Location> getClinics(long page, int size, String hospitalId, Boolean isClinic, Boolean isLab, Boolean isParent,
			Boolean isDentalWorksLab, Boolean isDentalImagingLab, String searchTerm);

	List<LabTestPickupLookupResponse> getLabTestPickupByIds(List<ObjectId> ids);

	DynamicCollectionBoyAllocationResponse allocateCBDynamically(DynamicCollectionBoyAllocationRequest request);

}
