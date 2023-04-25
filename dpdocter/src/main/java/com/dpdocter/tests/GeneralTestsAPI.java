package com.dpdocter.tests;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.webservices.PathProxy;

import common.util.web.Response;

@Component
@Path(PathProxy.GENERAL_TESTS_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class GeneralTestsAPI {
	@Autowired
	private LocationServices locationServices;

	@Autowired
	private PushNotificationServices pushNotificationServices;

	@Path(value = "/geocodeLocation/{address}")
	@GET
	public Response<GeocodedLocation> getAccessControls(@PathParam(value = "address") String address) {
		List<GeocodedLocation> geocodedLocations = locationServices.geocodeLocation(address);

		Response<GeocodedLocation> response = new Response<GeocodedLocation>();
		response.setDataList(geocodedLocations);
		return response;
	}

	@Path(value = "push")
	@GET
	public Response<Boolean> reminder() {
		pushNotificationServices.notifyUser("570ca16fe4b07c04418b3568", "Hello", "Healthcoco", "1", null);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

}
