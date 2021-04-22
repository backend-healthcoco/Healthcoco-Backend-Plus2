package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.Offer;
import com.dpdocter.elasticsearch.services.ESTrendingServices;
import com.dpdocter.response.TrendingResponse;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.SOLR_TRENDING_BASE_URL,consumes =MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.SOLR_TRENDING_BASE_URL, description = "Endpoint for Trending")
public class ESTrendingAPI {

	private static Logger logger = LogManager.getLogger(ESTrendingAPI.class.getName());

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private ESTrendingServices estrendingService;

	
	@GetMapping(value = PathProxy.ESTrendingUrl.SEARCH_OFFERS)
	@ApiOperation(value = PathProxy.ESTrendingUrl.SEARCH_OFFERS, notes = PathProxy.ESTrendingUrl.SEARCH_OFFERS)
	public Response<Offer> searchOffers(@RequestParam("size") int size, @RequestParam("page") int page,
			@RequestParam("discarded") @DefaultValue("false") Boolean discarded,
			@RequestParam("searchTerm") String searchTerm, @RequestParam("productId") String productId,
			@RequestParam("offerType") String offerType, @RequestParam("productType") String productType) {

		Response<Offer> response = new Response<Offer>();
		List<Offer> offers = estrendingService.searchOffer(size, page, discarded, searchTerm, productId, offerType,
				productType);
		if (offers != null && !offers.isEmpty())
			for (Offer offer : offers) {
				if (offer != null) {
					if (offer.getTitleImage() != null) {
						if (!DPDoctorUtils.anyStringEmpty(offer.getTitleImage().getImageUrl()))
							offer.getTitleImage().setImageUrl(getFinalImageURL(offer.getTitleImage().getImageUrl()));

						if (!DPDoctorUtils.anyStringEmpty(offer.getTitleImage().getThumbnailUrl()))
							offer.getTitleImage()
									.setThumbnailUrl(getFinalImageURL(offer.getTitleImage().getThumbnailUrl()));

					}
				}
			}
		response.setDataList(offers);
		return response;
	}

	
	@GetMapping(value = PathProxy.ESTrendingUrl.SEARCH_TRENDINGS)
	@ApiOperation(value = PathProxy.ESTrendingUrl.SEARCH_TRENDINGS, notes = PathProxy.ESTrendingUrl.SEARCH_TRENDINGS)
	public Response<TrendingResponse> searchTrendings(@RequestParam("size") int size, @RequestParam("page") int page,
			@RequestParam("discarded") @DefaultValue("false") Boolean discarded,
			@RequestParam("searchTerm") String searchTerm, @RequestParam("trendingType") String trendingType,
			@RequestParam("resourceType") String resourceType) {

		Response<TrendingResponse> response = new Response<TrendingResponse>();
		List<TrendingResponse> trendings = estrendingService.searchTrendings(size, page, discarded, searchTerm,
				trendingType, resourceType);
		response.setDataList(trendings);
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null)
			return imagePath + imageURL;
		else
			return null;
	}

}
