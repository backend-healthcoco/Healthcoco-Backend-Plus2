package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.OfferResponse;
import com.dpdocter.response.TrendingResponse;
import com.dpdocter.services.TrendingService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
(PathProxy.TRENDING_URL)
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.TRENDING_URL, description = "Endpoint for Trending")
public class TrendingAPI {
	private static Logger logger = LogManager.getLogger(TrendingAPI.class.getName());

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private TrendingService trendingService;

	
	@GetMapping(value = PathProxy.TrendingUrls.GET_TRENDING)
	@ApiOperation(value = PathProxy.TrendingUrls.GET_TRENDING, notes = PathProxy.TrendingUrls.GET_TRENDING)
	public Response<TrendingResponse> getTrending(@PathVariable("id") String id, @RequestParam("userId") String userId) {
		if (DPDoctorUtils.anyStringEmpty(id)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");

		}
		Response<TrendingResponse> response = new Response<TrendingResponse>();

		TrendingResponse trending = trendingService.getTrending(id, userId);
		response.setData(trending);
		return response;
	}

	
	@GetMapping(value = PathProxy.TrendingUrls.GET_OFFER)
	@ApiOperation(value = PathProxy.TrendingUrls.GET_OFFER, notes = PathProxy.TrendingUrls.GET_OFFER)
	public Response<OfferResponse> getOffer(@PathVariable("id") String id) {
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
