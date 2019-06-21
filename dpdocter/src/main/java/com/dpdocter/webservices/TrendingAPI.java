package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.OfferResponse;
import com.dpdocter.response.TrendingResponse;
import com.dpdocter.services.TrendingService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.TRENDING_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.TRENDING_URL, description = "Endpoint for Trending")
public class TrendingAPI {
	private static Logger logger = Logger.getLogger(TrendingAPI.class.getName());

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private TrendingService trendingService;

	@Path(value = PathProxy.TrendingUrls.GET_TRENDING)
	@GET
	@ApiOperation(value = PathProxy.TrendingUrls.GET_TRENDING, notes = PathProxy.TrendingUrls.GET_TRENDING)
	public Response<TrendingResponse> getTrending(@PathParam("id") String id, @QueryParam("userId") String userId) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<TrendingResponse> response = new Response<TrendingResponse>();

		TrendingResponse trending = trendingService.getTrending(id, userId);
		response.setData(trending);
		return response;
	}

	@Path(value = PathProxy.TrendingUrls.GET_OFFER)
	@GET
	@ApiOperation(value = PathProxy.TrendingUrls.GET_OFFER, notes = PathProxy.TrendingUrls.GET_OFFER)
	public Response<OfferResponse> getOffer(@PathParam("id") String id) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<OfferResponse> response = new Response<OfferResponse>();

		OfferResponse offer = trendingService.getOffer(id);

		response.setData(offer);
		return response;
	}

}
