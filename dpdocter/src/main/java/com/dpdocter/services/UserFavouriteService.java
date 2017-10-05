package com.dpdocter.services;

import java.util.List;

import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESUserLocaleDocument;
import com.dpdocter.elasticsearch.response.LabResponse;

public interface UserFavouriteService {

	List<ESDoctorDocument> getFavouriteDoctors(int page, int size, String userId);

	List<ESUserLocaleDocument> getFavouritePharmacies(int page, int size, String userId);

	List<LabResponse> getFavouriteLabs(int page, int size, String userId);

	Boolean addRemoveFavourites(String userId, String resourceId, String resourceType, String locationId,
			Boolean discarded);

}
