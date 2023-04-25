package com.dpdocter.services;

import java.util.List;

import com.dpdocter.request.OrderDrugsRequest;
import com.dpdocter.request.UserSearchRequest;
import com.dpdocter.response.OrderDrugsResponse;
import com.dpdocter.response.SearchRequestFromUserResponse;
import com.dpdocter.response.SearchRequestToPharmacyResponse;
import com.dpdocter.response.UserFakeRequestDetailResponse;

public interface PharmacyService {

	UserSearchRequest addSearchRequest(UserSearchRequest request);

	OrderDrugsRequest orderDrugs(OrderDrugsRequest request);

	List<SearchRequestFromUserResponse> getPatientOrderHistoryList(String userId, long page, int size);

	Integer getPharmacyListCountbyOrderHistory(String uniqueRequestId, String replyType);

	List<SearchRequestToPharmacyResponse> getPharmacyListbyOrderHistory(String userId, String uniqueRequestId,
			String replyType, long page, int size, Double latitude, Double longitude);

	UserFakeRequestDetailResponse getUserFakeRequestCount(String userId);

	List<OrderDrugsResponse> getPatientOrders(String userId, long page, int size, String updatedTime);

	List<SearchRequestFromUserResponse> getPatientRequests(String userId, long page, int size, String updatedTime);

	OrderDrugsRequest cancelOrderDrug(String orderId, String userId);
}
