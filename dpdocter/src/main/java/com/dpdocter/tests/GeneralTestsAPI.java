package com.dpdocter.tests;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.webservices.PathProxy;

import common.util.web.Response;

@RestController(PathProxy.GENERAL_TESTS_URL)
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
class GeneralTestsAPI {
	@Autowired
	private LocationServices locationServices;

	// @Autowired
	// private SolrTemplate solrTemplate;

	// @Autowired
	// private SolrLabTestRepository solrLabTestRepository;

	@Autowired
	private PushNotificationServices pushNotificationServices;

	
	@GetMapping(value = "/geocodeLocation/{address}")
	public Response<GeocodedLocation> getAccessControls(@PathVariable(value = "address") String address) {
		List<GeocodedLocation> geocodedLocations = locationServices.geocodeLocation(address);

		Response<GeocodedLocation> response = new Response<GeocodedLocation>();
		response.setDataList(geocodedLocations);
		return response;
	}

	
	@GetMapping(value = "push")
	public Response<Boolean> reminder() {
		pushNotificationServices.notifyUser("570ca16fe4b07c04418b3568", "Hello", "Healthcoco", "1", null);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	// (value = "pushIOS")
	// @GetMapping
	// public Response<Boolean> pushIOS() {
	//
	// pushNotificationServices.pushNotificationOnIosDevices("bb", "Hello", "h",
	// "a");
	// Response<Boolean> response = new Response<Boolean>();
	// response.setData(true);
	// return response;
	// }
}
