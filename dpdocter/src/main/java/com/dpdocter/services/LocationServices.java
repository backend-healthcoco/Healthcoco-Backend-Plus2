package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.CollectionBoy;
import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.beans.LabTestPickup;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.RateCard;
import com.dpdocter.beans.RateCardTestAssociation;
import com.dpdocter.request.AddEditLabTestPickupRequest;
import com.dpdocter.beans.CollectionBoyLabAssociation;

public interface LocationServices {
	public List<GeocodedLocation> geocodeLocation(String address);

	List<GeocodedLocation> geocodeTimeZone(Double latitude, Double longitude);

	public Location addEditRecommedation(String locationId, String patientId);

	Boolean setDefaultLab(String locationId, String defaultLabId);

	LabTestPickup addEditLabTestPickupRequest(AddEditLabTestPickupRequest request);

	Boolean verifyCRN(String locationId, String crn, String requestId);

	List<CollectionBoy> getCollectionBoyList(int size, int page, String locationId, String searchTerm);

	List<Location> getAssociatedLabs(String locationId, Boolean isParent);

	RateCardTestAssociation addEditRateCardTestAssociation(RateCardTestAssociation request);

	Location addCollectionBoyAssociatedLabs(List<CollectionBoyLabAssociation> collectionBoyLabAssociations);

	List<RateCardTestAssociation> getRateCardTests(int page, int size, String searchTerm, String rateCardId,
			String labId);

	RateCard addEditRateCard(RateCard request);

	List<RateCard> getRateCards(int page, int size, String searchTerm, String locationId);

	List<Location> getCBAssociatedLabs(String parentLabId, String daughterLabId, String collectionBoyId, int size, int page);

}
