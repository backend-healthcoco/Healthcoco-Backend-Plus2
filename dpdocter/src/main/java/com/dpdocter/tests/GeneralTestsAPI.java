package com.dpdocter.tests;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.services.LocationServices;
import com.dpdocter.solr.document.SolrDoctorDocument;
import com.dpdocter.webservices.PathProxy;

import common.util.web.Response;

@Component
@Path(PathProxy.GENERAL_TESTS_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GeneralTestsAPI {
    @Autowired
    private LocationServices locationServices;

    @Autowired
    private SolrTemplate solrTemplate;

    @Path(value = "/geocodeLocation/{address}")
    @GET
    public Response<GeocodedLocation> getAccessControls(@PathParam(value = "address") String address) {
	List<GeocodedLocation> geocodedLocations = locationServices.geocodeLocation(address);

	Response<GeocodedLocation> response = new Response<GeocodedLocation>();
	response.setDataList(geocodedLocations);
	return response;
    }

    @Path(value = "getDoctorsWithSpecialities")
    @GET
    public Response<SolrDoctorDocument> getDoctorsWithSpecialities() {
	Criteria doctorSearchCriteria = Criteria.where("specialities")
		.in(Arrays.asList("55ede9b0a136d953eb160f6c", "56cded552d7f84eee654686b", "56cded672d7f84eee654686c"));

	SimpleQuery query = new SimpleQuery(doctorSearchCriteria);
	solrTemplate.setSolrCore("doctors");

	List<SolrDoctorDocument> solrDoctorDocuments = solrTemplate.queryForPage(query, SolrDoctorDocument.class).getContent();

	Response<SolrDoctorDocument> response = new Response<SolrDoctorDocument>();
	response.setDataList(solrDoctorDocuments);
	return response;
    }
}
