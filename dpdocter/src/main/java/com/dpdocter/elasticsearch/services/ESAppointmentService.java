package com.dpdocter.elasticsearch.services;

import java.util.List;

import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.solr.beans.AppointmentSearchResponse;
import com.dpdocter.solr.response.LabResponse;

public interface ESAppointmentService {

    List<ESDoctorDocument> getDoctors(int page, int size, String city, String location, String latitude, String longitude, String speciality, String symptom,
	    Boolean booking, Boolean calling, String minFee, String maxFee, String minTime, String maxTime, List<String> days, String gender,
	    String minExperience, String maxExperience);

    List<AppointmentSearchResponse> search(String city, String location, String latitude, String longitude, String searchTerm);

    List<LabResponse> getLabs(int page, int size, String city, String location, String latitude, String longitude, String test, Boolean booking, Boolean calling);

}
