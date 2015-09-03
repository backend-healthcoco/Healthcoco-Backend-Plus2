package com.dpdocter.solr.services;

import java.util.List;

import com.dpdocter.solr.beans.AppointmentSearchResponse;
import com.dpdocter.solr.document.SolrDoctorDocument;
import com.dpdocter.solr.document.SolrLocationDocument;
import com.dpdocter.solr.document.SolrSpecialityDocument;

public interface SolrAppointmentService {
    boolean addDoctor(SolrDoctorDocument request);

    boolean addLocation(List<SolrLocationDocument> request);

    boolean addSpeciality(List<SolrSpecialityDocument> request);

    List<AppointmentSearchResponse> search(String city, String location, String keyword);

    List<SolrDoctorDocument> getDoctors(String city, String location, String keyword);

}
