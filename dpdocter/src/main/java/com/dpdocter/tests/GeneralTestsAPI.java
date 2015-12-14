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
import com.dpdocter.webservices.PathProxy;
import common.util.web.Response;

@Component
@Path(PathProxy.GENERAL_TESTS_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GeneralTestsAPI {
    @Autowired
    private LocationServices locationServices;

    @Path(value = "/geocodeLocation/{address}")
    @GET
    public Response<GeocodedLocation> getAccessControls(@PathParam(value = "address") String address) {
	List<GeocodedLocation> geocodedLocations = locationServices.geocodeLocation(address);

	Response<GeocodedLocation> response = new Response<GeocodedLocation>();
	response.setDataList(geocodedLocations);
	return response;
    }
}
