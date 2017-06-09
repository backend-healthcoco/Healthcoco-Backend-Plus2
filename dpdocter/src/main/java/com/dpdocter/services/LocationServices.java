package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.CollectionBoy;
import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.LabTestPickup;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.RateCard;
import com.dpdocter.beans.RateCardLabAssociation;
import com.dpdocter.beans.RateCardTestAssociation;
import com.dpdocter.beans.Specimen;
import com.dpdocter.request.AddEditLabTestPickupRequest;
import com.dpdocter.response.RateCardTestAssociationLookupResponse;
import com.dpdocter.beans.CollectionBoyLabAssociation;

public interface LocationServices {
	public List<GeocodedLocation> geocodeLocation(String address);

	List<GeocodedLocation> geocodeTimeZone(Double latitude, Double longitude);

	public Location addEditRecommedation(String locationId, String patientId);

	Boolean setDefaultLab(String locationId, String defaultLabId);

	LabTestPickup addEditLabTestPickupRequest(AddEditLabTestPickupRequest request);

	Boolean verifyCRN(String locationId, String crn, String requestId);

	List<CollectionBoy> getCollectionBoyList(int size, int page, String locationId, String searchTerm);

	//List<Location> getAssociatedLabs(String locationId, Boolean isParent);

	RateCardTestAssociation addEditRateCardTestAssociation(RateCardTestAssociation request);

	List<Location> addCollectionBoyAssociatedLabs(List<CollectionBoyLabAssociation> collectionBoyLabAssociations);

	List<RateCardTestAssociationLookupResponse> getRateCardTests(int page, int size, String searchTerm, String rateCardId,
			String labId);

	RateCard addEditRateCard(RateCard request);

	List<RateCard> getRateCards(int page, int size, String searchTerm, String locationId);

	List<Location> getCBAssociatedLabs(String parentLabId, String daughterLabId, String collectionBoyId, int size, int page);

	List<LabTestPickup> getRequestForCB(String collectionBoyId, int size, int page);

	LabTestPickup getLabTestPickupByRequestId(String requestId);

	LabTestPickup getLabTestPickupById(String id);

	CollectionBoy discardCB(String collectionBoyId, Boolean discarded);

	CollectionBoy changeAvailability(String collectionBoyId, Boolean isAvailable);

	RateCardLabAssociation addEditRateCardAssociatedLab(RateCardLabAssociation rateCardLabAssociation);

	RateCardLabAssociation getRateCardAssociatedLab(String daughterLabId, String parentLabId);

	List<Location> getClinics(int page, int size, String hospitalId, Boolean isClinic, Boolean isLab, Boolean isParent,
			String searchTerm);

	Integer getCBCount(int size, int page, String locationId, String searchTerm);

	List<Specimen> getSpecimenList(int page, int size, String searchTerm);

	Integer getRateCardCount(int page, int size, String searchTerm, String locationId);

	List<Location> getAssociatedLabs(String locationId, Boolean isParent, String searchTerm);

	CollectionBoy editCollectionBoy(CollectionBoy collectionBoy);

	Boolean addEditRateCardTestAssociation(List<RateCardTestAssociation> request);

/*	List<RateCardTestAssociationLookupResponse> getRateCardTests(int page, int size, String searchTerm,
			String daughterLabId, String parentLabId, String labId);
*/
	List<LabTestPickup> getRequestForDL(String daughterLabId, int size, int page);

	List<RateCardTestAssociationLookupResponse> getRateCardTests(int page, int size, String searchTerm,
			String daughterLabId, String parentLabId, String labId, String specimen);

	RateCard getDLRateCard(String daughterLabId, String parentLabId);

}
