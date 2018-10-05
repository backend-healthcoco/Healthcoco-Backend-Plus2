package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

@Component
@Path(PathProxy.USER_FAVOURITES_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.USER_FAVOURITES_BASE_URL, description = "Endpoint for user favourites")
public class UserFavouritesApi {

	private static Logger logger = Logger.getLogger(UserFavouritesApi.class.getName());
	
	@Autowired
	private UserFavouriteService userFavouriteService;
	
	@Path(value = PathProxy.UserFavouritesUrls.ADD_REMOVE_FROM_FAVOURITES)
	@GET
	@ApiOperation(value = PathProxy.UserFavouritesUrls.ADD_REMOVE_FROM_FAVOURITES, notes = PathProxy.UserFavouritesUrls.ADD_REMOVE_FROM_FAVOURITES)
	public Response<Boolean> addRemoveFavourites(@PathParam("resourceType") String resourceType, @PathParam("userId") String userId, @PathParam("resourceId") String resourceId,
			@QueryParam("locationId") String locationId, @DefaultValue(value = "false") @QueryParam("discarded") Boolean discarded) {
		if (DPDoctorUtils.anyStringEmpty(userId, resourceId, resourceType)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		Boolean addRemoveResponse = userFavouriteService.addRemoveFavourites(userId, resourceId, resourceType, locationId, discarded);

		Response<Boolean> response = new Response<Boolean>();
		response.setData(addRemoveResponse);
		return response;
	}
	
	@Path(value = PathProxy.UserFavouritesUrls.GET_FAVOURITE_DOCTORS)
	@GET
	@ApiOperation(value = PathProxy.UserFavouritesUrls.GET_FAVOURITE_DOCTORS, notes = PathProxy.UserFavouritesUrls.GET_FAVOURITE_DOCTORS)
	public Response<ESDoctorDocument> getFavouriteDoctors(@QueryParam("page") long page, @QueryParam("size") int size,
			@PathParam("userId") String userId) {
		if (DPDoctorUtils.anyStringEmpty(userId)) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ESDoctorDocument> doctors = userFavouriteService.getFavouriteDoctors(page, size, userId);

		Response<ESDoctorDocument> response = new Response<ESDoctorDocument>();
		response.setDataList(doctors);
		return response;
	}

	@Path(value = PathProxy.UserFavouritesUrls.GET_FAVOURITE_PHARMACIES)
	@GET
	@ApiOperation(value = PathProxy.UserFavouritesUrls.GET_FAVOURITE_PHARMACIES, notes = PathProxy.UserFavouritesUrls.GET_FAVOURITE_PHARMACIES)
	public Response<ESUserLocaleDocument> getFavouritePharmacies(@QueryParam("page") long page, @QueryParam("size") int size,
			@PathParam("userId") String userId) {

		List<ESUserLocaleDocument> pharmacies = userFavouriteService.getFavouritePharmacies(page, size, userId);

		Response<ESUserLocaleDocument> response = new Response<ESUserLocaleDocument>();
		response.setDataList(pharmacies);
		return response;
	}

	@Path(value = PathProxy.UserFavouritesUrls.GET_FAVOURITE_LABS)
	@GET
	@ApiOperation(value = PathProxy.UserFavouritesUrls.GET_FAVOURITE_LABS, notes = PathProxy.UserFavouritesUrls.GET_FAVOURITE_LABS)
	public Response<LabResponse> getFavouriteLabs(@QueryParam("page") long page, @QueryParam("size") int size,
			@PathParam("userId") String userId) {

		List<LabResponse> doctors = userFavouriteService.getFavouriteLabs(page, size, userId);

		Response<LabResponse> response = new Response<LabResponse>();
		response.setDataList(doctors);
		return response;
	}
}
