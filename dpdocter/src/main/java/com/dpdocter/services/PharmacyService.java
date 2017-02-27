package com.dpdocter.services;

import com.dpdocter.request.OrderDrugsRequest;
import com.dpdocter.request.UserSearchRequest;
import com.dpdocter.response.PharmacyResponse;

public interface PharmacyService {

	Boolean addSearchRequest(UserSearchRequest request);

	/*Boolean addResponseInQueue(PharmacyResponse request);*/

	Boolean orderDrugs(OrderDrugsRequest request);

}
