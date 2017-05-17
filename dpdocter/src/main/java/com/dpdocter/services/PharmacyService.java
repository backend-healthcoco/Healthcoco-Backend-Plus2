package com.dpdocter.services;

import java.util.List;

import com.dpdocter.request.OrderDrugsRequest;
import com.dpdocter.request.UserSearchRequest;
import com.dpdocter.response.PharmacyResponse;
import com.dpdocter.response.SearchRequestFromUserResponse;
import com.dpdocter.response.SearchRequestToPharmacyResponse;

public interface PharmacyService {

	UserSearchRequest addSearchRequest(UserSearchRequest request);

	/*Boolean addResponseInQueue(PharmacyResponse request);*/

	OrderDrugsRequest orderDrugs(OrderDrugsRequest request);

	List<SearchRequestFromUserResponse> getPatientOrderHistoryList(String userId, int page, int size);

	/*List<SearchRequestToPharmacyResponse> getPharmacyListbyOrderHistory(String userId, String uniqueRequestId, String replyType, int page,
			int size);*/

	Integer getPharmacyListCountbyOrderHistory(String uniqueRequestId, String replyType);

	List<SearchRequestToPharmacyResponse> getPharmacyListbyOrderHistory(String userId, String uniqueRequestId,
			String replyType, int page, int size, Double latitude, Double longitude);

}
