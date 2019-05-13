package com.dpdocter.elasticsearch.services;

import java.util.List;

import com.dpdocter.beans.Offer;
import com.dpdocter.elasticsearch.document.ESOfferDocument;
import com.dpdocter.elasticsearch.document.ESTrendingDocument;
import com.dpdocter.response.TrendingResponse;

public interface ESTrendingServices {
	public boolean addOffer(ESOfferDocument request);

	public boolean addTrending(ESTrendingDocument request);

	public List<Offer> searchOffer(int size, int page, Boolean discarded, String searchTerm, String productId,
			String offerType, String productType);

	public List<TrendingResponse> searchTrendings(int size, int page, Boolean discarded, String searchTerm,
			String trendingType, String resourceType);
}
