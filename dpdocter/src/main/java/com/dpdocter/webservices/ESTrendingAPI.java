package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.Offer;
import com.dpdocter.elasticsearch.services.ESTrendingServices;
import com.dpdocter.response.TrendingResponse;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.SOLR_TRENDING_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SOLR_TRENDING_BASE_URL, description = "Endpoint for Trending")
public class ESTrendingAPI {

	private static Logger logger = Logger.getLogger(ESTrendingAPI.class.getName());

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private ESTrendingServices estrendingService;

	@Path(value = PathProxy.ESTrendingUrl.SEARCH_OFFERS)
	@GET
	@ApiOperation(value = PathProxy.ESTrendingUrl.SEARCH_OFFERS, notes = PathProxy.ESTrendingUrl.SEARCH_OFFERS)
	public Response<Offer> searchOffers(@QueryParam("size") int size, @QueryParam("page") int page,
			@QueryParam("discarded") @DefaultValue("false") Boolean discarded,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("productId") String productId,
			@QueryParam("offerType") String offerType, @QueryParam("productType") String productType) {

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

	@Path(value = PathProxy.ESTrendingUrl.SEARCH_TRENDINGS)
	@GET
	@ApiOperation(value = PathProxy.ESTrendingUrl.SEARCH_TRENDINGS, notes = PathProxy.ESTrendingUrl.SEARCH_TRENDINGS)
	public Response<TrendingResponse> searchTrendings(@QueryParam("size") int size, @QueryParam("page") int page,
			@QueryParam("discarded") @DefaultValue("false") Boolean discarded,
			@QueryParam("searchTerm") String searchTerm, @QueryParam("trendingType") String trendingType,
			@QueryParam("resourceType") String resourceType) {

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
