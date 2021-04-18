package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESUserLocaleDocument;
import com.dpdocter.elasticsearch.response.LabResponse;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.UserFavouriteService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
(PathProxy.USER_FAVOURITES_BASE_URL)
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.USER_FAVOURITES_BASE_URL, description = "Endpoint for user favourites")
public class UserFavouritesApi {

	private static Logger logger = LogManager.getLogger(UserFavouritesApi.class.getName());
	
	@Autowired
	private UserFavouriteService userFavouriteService;
	
	
	@GetMapping(value = PathProxy.UserFavouritesUrls.ADD_REMOVE_FROM_FAVOURITES)
	@ApiOperation(value = PathProxy.UserFavouritesUrls.ADD_REMOVE_FROM_FAVOURITES, notes = PathProxy.UserFavouritesUrls.ADD_REMOVE_FROM_FAVOURITES)
	public Response<Boolean> addRemoveFavourites(@PathVariable("resourceType") String resourceType, @PathVariable("userId") String userId, @PathVariable("resourceId") String resourceId,
			@RequestParam("locationId") String locationId, @DefaultValue(value = "false") @RequestParam(required = false, value ="discarded", defaultValue="true")boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(userId, resourceId, resourceType)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean addRemoveResponse = userFavouriteService.addRemoveFavourites(userId, resourceId, resourceType, locationId, discarded);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(addRemoveResponse);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.UserFavouritesUrls.GET_FAVOURITE_DOCTORS)
	@ApiOperation(value = PathProxy.UserFavouritesUrls.GET_FAVOURITE_DOCTORS, notes = PathProxy.UserFavouritesUrls.GET_FAVOURITE_DOCTORS)
	public Response<ESDoctorDocument> getFavouriteDoctors(@RequestParam("page") long page, @RequestParam("size") int size,
			@PathVariable("userId") String userId) {
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESDoctorDocument> doctors = userFavouriteService.getFavouriteDoctors(page, size, userId);

		Response<ESDoctorDocument> response = new Response<ESDoctorDocument>();
		response.setDataList(doctors);
		return response;
	}

	
	@GetMapping(value = PathProxy.UserFavouritesUrls.GET_FAVOURITE_PHARMACIES)
	@ApiOperation(value = PathProxy.UserFavouritesUrls.GET_FAVOURITE_PHARMACIES, notes = PathProxy.UserFavouritesUrls.GET_FAVOURITE_PHARMACIES)
	public Response<ESUserLocaleDocument> getFavouritePharmacies(@RequestParam("page") long page, @RequestParam("size") int size,
			@PathVariable("userId") String userId) {

		List<ESUserLocaleDocument> pharmacies = userFavouriteService.getFavouritePharmacies(page, size, userId);

		Response<ESUserLocaleDocument> response = new Response<ESUserLocaleDocument>();
		response.setDataList(pharmacies);
		return response;
	}

	
	@GetMapping(value = PathProxy.UserFavouritesUrls.GET_FAVOURITE_LABS)
	@ApiOperation(value = PathProxy.UserFavouritesUrls.GET_FAVOURITE_LABS, notes = PathProxy.UserFavouritesUrls.GET_FAVOURITE_LABS)
	public Response<LabResponse> getFavouriteLabs(@RequestParam("page") long page, @RequestParam("size") int size,
			@PathVariable("userId") String userId) {

		List<LabResponse> doctors = userFavouriteService.getFavouriteLabs(page, size, userId);

		Response<LabResponse> response = new Response<LabResponse>();
		response.setDataList(doctors);
		return response;
	}
}
