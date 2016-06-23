package com.dpdocter.elasticsearch.services;

import java.util.List;

import com.dpdocter.elasticsearch.beans.ESCityLandmarkLocalityResponse;
import com.dpdocter.elasticsearch.document.ESCityDocument;
import com.dpdocter.elasticsearch.document.ESLandmarkLocalityDocument;

public interface ESCityService {

    boolean addCities(ESCityDocument solrCities);
//
//    boolean activateDeactivateCity(String cityId, boolean activate);
//
    boolean addLocalityLandmark(ESLandmarkLocalityDocument esLandmarkLocalityDocument);
//
//    List<SolrCityDocument> searchCity(String searchTerm);
//
//    List<SolrLocalityLandmarkDocument> searchLandmarkLocality(String cityId, String type, String searchTerm);
//
    List<ESCityLandmarkLocalityResponse> searchCityLandmarkLocality(String searchTerm, String latitude, String longitude);

}
