package com.dpdocter.solr.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.enums.CitySearchType;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.services.TransactionalManagementService;
import com.dpdocter.solr.beans.SolrCityLandmarkLocalityResponse;
import com.dpdocter.solr.document.SolrCityDocument;
import com.dpdocter.solr.document.SolrCountryDocument;
import com.dpdocter.solr.document.SolrLocalityLandmarkDocument;
import com.dpdocter.solr.document.SolrStateDocument;
import com.dpdocter.solr.repository.SolrCityRepository;
import com.dpdocter.solr.repository.SolrCountryRepository;
import com.dpdocter.solr.repository.SolrLocalityLandmarkRepository;
import com.dpdocter.solr.repository.SolrStateRepository;
import com.dpdocter.solr.services.SolrCityService;

import common.util.web.DPDoctorUtils;

@Service
public class SolrCityServiceImpl implements SolrCityService {

    @Autowired
    private SolrCityRepository solrCityRepository;

    @Autowired
    private SolrCountryRepository solrCountryRepository;

    @Autowired
    private SolrLocalityLandmarkRepository solrLocalityLandmarkRepository;

    @Autowired
    private TransactionalManagementService transnationalService;

    @Autowired
    private SolrStateRepository solrStateRepository;
    
    @Override
    public boolean addCountry(SolrCountryDocument solrCountry) {
	boolean response = false;
	try {
	    solrCountryRepository.save(solrCountry);
	    transnationalService.addResource(solrCountry.getId(), Resource.COUNTRY, true);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    // throw new BusinessException(ServiceError.Unknown,
	    // "Error Occurred While Saving Country");
	}
	return response;
    }

    @Override
    public boolean addCities(SolrCityDocument solrCities) {
	boolean response = false;
	try {
	    solrCityRepository.save(solrCities);
	    transnationalService.addResource(solrCities.getId(), Resource.CITY, false);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return response;
    }

    @Override
    public boolean activateDeactivateCity(String cityId, boolean activate) {
	boolean response = false;
	try {
	    SolrCityDocument solrCity = solrCityRepository.findOne(cityId);
	    solrCity.setIsActivated(activate);
	    solrCityRepository.save(solrCity);
	    transnationalService.addResource(cityId, Resource.CITY, true);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return response;
    }

    @Override
    public boolean addLocalityLandmark(SolrLocalityLandmarkDocument solrLocalityLandmark) {
	boolean response = false;
	try {
	    solrLocalityLandmarkRepository.save(solrLocalityLandmark);
	    transnationalService.addResource(solrLocalityLandmark.getId(), Resource.LANDMARKLOCALITY, true);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return response;
    }

    @Override
    public List<SolrCityDocument> searchCity(String searchTerm) {
	List<SolrCityDocument> response = null;
	try {
	    response = solrCityRepository.findByQueryAnnotation(searchTerm);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching City");
	}
	return response;

    }

    @Override
    public List<SolrLocalityLandmarkDocument> searchLandmarkLocality(String cityId, String type, String searchTerm) {
	List<SolrLocalityLandmarkDocument> response = null;
	try {
	    if (type == null)
		response = solrLocalityLandmarkRepository.findByCityId(cityId, searchTerm);
	    else {
		if (type.equalsIgnoreCase(CitySearchType.LANDMARK.getType())) {
		    response = solrLocalityLandmarkRepository.findByCityIdAndLandmark(cityId, searchTerm);
		}
		if (type.equalsIgnoreCase(CitySearchType.LOCALITY.getType())) {
		    response = solrLocalityLandmarkRepository.findByCityIdAndLocality(cityId, searchTerm);
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Landmark Locality");
	}
	return response;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SolrCityLandmarkLocalityResponse> searchCityLandmarkLocality(String searchTerm, String latitude, String longitude) {
	List<SolrCityLandmarkLocalityResponse> response = new ArrayList<SolrCityLandmarkLocalityResponse>();
	try {
	    List<SolrLocalityLandmarkDocument> landmarks = null;
	    List<SolrLocalityLandmarkDocument> localities = null;
	    List<SolrCityDocument> cities = null;
	    if (DPDoctorUtils.anyStringEmpty(latitude, longitude)) {
		if (searchTerm != null) {
		    landmarks = solrLocalityLandmarkRepository.findByLandmark(searchTerm);
		    localities = solrLocalityLandmarkRepository.findByLocality(searchTerm);
		    cities = solrCityRepository.findByQueryAnnotation(searchTerm);
		} else {
		    landmarks = IteratorUtils.toList(solrLocalityLandmarkRepository.findAll().iterator());
		    cities = IteratorUtils.toList(solrCityRepository.findAll().iterator());
		}
	    } else {
		if (searchTerm != null) {
		    landmarks = solrLocalityLandmarkRepository.findByLandmark(searchTerm, Double.parseDouble(latitude), Double.parseDouble(longitude));
		    localities = solrLocalityLandmarkRepository.findByLocality(searchTerm, Double.parseDouble(latitude), Double.parseDouble(longitude));
		    cities = solrCityRepository.findByQueryAnnotation(searchTerm, Double.parseDouble(latitude), Double.parseDouble(longitude));
		} else {
		    landmarks = solrLocalityLandmarkRepository.findByLandmarkANDLocality(Double.parseDouble(latitude), Double.parseDouble(longitude));
		    cities = solrCityRepository.findByQueryAnnotation(Double.parseDouble(latitude), Double.parseDouble(longitude));
		}
	    }
	    if (landmarks != null && !landmarks.isEmpty()) {
		for (SolrLocalityLandmarkDocument document : landmarks) {
		    SolrCityDocument city = solrCityRepository.findOne(document.getCityId());
		    SolrCityLandmarkLocalityResponse landmark = new SolrCityLandmarkLocalityResponse();
		    BeanUtil.map(document, landmark);
		    if (city != null){
		    	landmark.setCity(city.getCity());
		    	SolrStateDocument state = solrStateRepository.findOne(city.getStateId());
			    if(state != null){
			    	landmark.setState(state.getState());
			    	SolrCountryDocument country = solrCountryRepository.findOne(state.getCountryId());
			    	if(country != null) landmark.setCountry(country.getCountry());
			    }
		    }
		    response.add(landmark);
		}
	    }
	    if (localities != null && !localities.isEmpty()) {
		for (SolrLocalityLandmarkDocument document : localities) {
		    SolrCityDocument city = solrCityRepository.findOne(document.getCityId());
		    SolrCityLandmarkLocalityResponse locality = new SolrCityLandmarkLocalityResponse();
		    BeanUtil.map(document, locality);
		    if (city != null){
		    	locality.setCity(city.getCity());
		    	SolrStateDocument state = solrStateRepository.findOne(city.getStateId());
			    if(state != null){
			    	locality.setState(state.getState());
			    	SolrCountryDocument country = solrCountryRepository.findOne(state.getCountryId());
			    	if(country != null) locality.setCountry(country.getCountry());
			    }
		    }
		    response.add(locality);
		}
	    }
	    if (cities != null && !cities.isEmpty()) {
		for (SolrCityDocument document : cities) {
			SolrCityLandmarkLocalityResponse city = new SolrCityLandmarkLocalityResponse();
		    BeanUtil.map(document, city);
		    if (city != null){
		    	SolrStateDocument state = solrStateRepository.findOne(document.getStateId());
			    if(state != null){
			    	city.setState(state.getState());
			    	SolrCountryDocument country = solrCountryRepository.findOne(state.getCountryId());
			    	if(country != null) city.setCountry(country.getCountry());
			    }
		    }
		    response.add(city);
		}
	    }
	    if (response != null && !response.isEmpty() && response.size() > 30)
		response = response.subList(0, 29);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Landmark Locality");
	}
	return response;
    }

	@Override
	public boolean addState(SolrStateDocument solrState) {
		boolean response = false;
		try {
		    solrStateRepository.save(solrState);
		    transnationalService.addResource(solrState.getId(), Resource.STATE, false);
		    response = true;
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return response;
	}
}
