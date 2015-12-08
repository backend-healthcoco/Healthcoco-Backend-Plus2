package com.dpdocter.solr.services;

import java.util.List;

import com.dpdocter.solr.beans.SolrCityLandmarkLocalityResponse;
import com.dpdocter.solr.document.SolrCityDocument;
import com.dpdocter.solr.document.SolrLocalityLandmarkDocument;

public interface SolrCityService {

    boolean addCities(SolrCityDocument solrCities);

    boolean editCities(SolrCityDocument solrCities);

    boolean activateDeactivateCity(String cityId, boolean activate);

    boolean addLocalityLandmark(SolrLocalityLandmarkDocument solrLocalityLandmark);

    boolean editLocalityLandmark(SolrLocalityLandmarkDocument solrLocalityLandmark);

    List<SolrCityDocument> searchCity(String searchTerm);

    List<SolrLocalityLandmarkDocument> searchLandmarkLocality(String cityId, String type, String searchTerm);

	List<SolrCityLandmarkLocalityResponse> searchCityLandmarkLocality(String searchTerm);

}
