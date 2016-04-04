package com.dpdocter.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.GeocodedLocation;
import com.dpdocter.collections.DiagnosticTestCollection;
import com.dpdocter.services.LocationServices;
import com.dpdocter.solr.document.SolrDoctorDocument;
import com.dpdocter.solr.document.SolrLabTestDocument;
import com.dpdocter.solr.repository.SolrLabTestRepository;
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

    @Autowired
    private SolrLabTestRepository solrLabTestRepository;

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
	Criteria doctorSearchCriteria = Criteria.where("specialities").in(Arrays.asList("5698a19c87bde8031cdb0ac5"));

	SimpleQuery query = new SimpleQuery(doctorSearchCriteria);
	solrTemplate.setSolrCore("doctors");

	List<SolrDoctorDocument> solrDoctorDocuments = solrTemplate.queryForPage(query, SolrDoctorDocument.class).getContent();

	Response<SolrDoctorDocument> response = new Response<SolrDoctorDocument>();
	response.setDataList(solrDoctorDocuments);
	return response;
    }

    @Path(value = "getTests")
    @GET
    public Response<SolrLabTestDocument> getTests() {
    	    
    	    List<DiagnosticTestCollection> diagnosticTests = new ArrayList<DiagnosticTestCollection>();
			 DiagnosticTestCollection diagnosticTestCollection = new DiagnosticTestCollection();
			 diagnosticTestCollection.setId("56b47551e4b0f5980d1404f5");diagnosticTests.add(diagnosticTestCollection);
			 diagnosticTestCollection.setId("56b47551e4b0f5980d14057e");diagnosticTests.add(diagnosticTestCollection);
			 diagnosticTestCollection.setId("56b47551e4b0f5980d14057b");diagnosticTests.add(diagnosticTestCollection);
			 diagnosticTestCollection.setId("56b47551e4b0f5980d140580");diagnosticTests.add(diagnosticTestCollection);
			 diagnosticTestCollection.setId("56b47551e4b0f5980d14057d");diagnosticTests.add(diagnosticTestCollection);
			 diagnosticTestCollection.setId("56b47551e4b0f5980d140623");diagnosticTests.add(diagnosticTestCollection);
			 @SuppressWarnings("unchecked")
			 Collection<String> testIds = CollectionUtils.collect(diagnosticTests, new BeanToPropertyValueTransformer("id"));
		     List<SolrLabTestDocument> solrLabTestDocuments = solrLabTestRepository.findByTestIds(testIds.toString().replace("[", "(").replace("]", ")"));
		
	Response<SolrLabTestDocument> response = new Response<SolrLabTestDocument>();
	response.setDataList(solrLabTestDocuments);
	return response;
    }


}
