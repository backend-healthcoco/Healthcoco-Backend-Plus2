package com.dpdocter.services;

import com.dpdocter.response.OfferResponse;
import com.dpdocter.response.TrendingResponse;

public interface TrendingService {

	public TrendingResponse getTrending(String id, String userId);

	public OfferResponse getOffer(String id);
}
