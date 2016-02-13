package com.dpdocter.solr.services;

import java.util.List;

import javax.ws.rs.core.UriInfo;

import com.dpdocter.solr.beans.AppointmentSearchResponse;
import com.dpdocter.solr.beans.SolrDoctor;
import com.dpdocter.solr.document.SolrLocationDocument;
import com.dpdocter.solr.document.SolrSpecialityDocument;
import com.dpdocter.solr.response.LabResponse;

public interface SolrAppointmentService {

    boolean addLocation(List<SolrLocationDocument> request);

    boolean addSpeciality(List<SolrSpecialityDocument> request);

    List<SolrDoctor> getDoctors(String city, String location, String latitude, String longitude, String speciality, String symptom, Boolean booking, Boolean calling, String minFee,
	    String maxFee, String minTime, String maxTime, List<String> days, String gender, String minExperience, String maxExperience);

    List<AppointmentSearchResponse> search(String city, String location, String latitude, String longitude, String searchTerm);

    List<LabResponse> getLabs(String city, String location, String latitude, String longitude, String testId);

}
