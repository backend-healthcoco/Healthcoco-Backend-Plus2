package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DoctorInfo;
import com.dpdocter.beans.Locale;
import com.dpdocter.elasticsearch.response.LabResponse;

public interface UserFavouriteService {

	List<DoctorInfo> getFavouriteDoctors(int page, int size, String userId);

	List<Locale> getFavouritePharmacies(int page, int size, String userId);

	List<LabResponse> getFavouriteLabs(int page, int size, String userId);

	Boolean addRemoveFavourites(String userId, String resourceId, String resourceType, String locationId,
			Boolean discarded);

}
