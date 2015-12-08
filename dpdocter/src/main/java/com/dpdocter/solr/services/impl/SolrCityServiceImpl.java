package com.dpdocter.solr.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.enums.CitySearchType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.solr.beans.SolrCityLandmarkLocalityResponse;
import com.dpdocter.solr.document.SolrCityDocument;
import com.dpdocter.solr.document.SolrLocalityLandmarkDocument;
import com.dpdocter.solr.repository.SolrCityRepository;
import com.dpdocter.solr.repository.SolrLocalityLandmarkRepository;
import com.dpdocter.solr.services.SolrCityService;

@Service
public class SolrCityServiceImpl implements SolrCityService {

    @Autowired
    private SolrCityRepository solrCityRepository;

    @Autowired
    private SolrLocalityLandmarkRepository solrLocalityLandmarkRepository;

    @Override
    public boolean addCities(SolrCityDocument solrCities) {
	boolean response = false;
	try {
	    solrCityRepository.save(solrCities);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving City");
	}
	return response;
    }

    @Override
    public boolean editCities(SolrCityDocument solrCities) {
	boolean response = false;
	try {
	    solrCityRepository.save(solrCities);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editig City");
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
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Activating Deactivating City");
	}
	return response;
    }

    @Override
    public boolean addLocalityLandmark(SolrLocalityLandmarkDocument solrLocalityLandmark) {
	boolean response = false;
	try {
	    solrLocalityLandmarkRepository.save(solrLocalityLandmark);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Saving Locality or Landmark City");
	}
	return response;
    }

    @Override
    public boolean editLocalityLandmark(SolrLocalityLandmarkDocument solrLocalityLandmark) {
	boolean response = false;
	try {
	    solrLocalityLandmarkRepository.save(solrLocalityLandmark);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Editing Locality or Landmark City");
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
	public List<SolrCityLandmarkLocalityResponse> searchCityLandmarkLocality(String searchTerm) {
		List<SolrCityLandmarkLocalityResponse> response = new ArrayList<SolrCityLandmarkLocalityResponse>();
		try {
			List<SolrLocalityLandmarkDocument> landmarks = null;
			List<SolrLocalityLandmarkDocument> localities = null;
			List<SolrCityDocument> cities = null;
			if (searchTerm != null){
		    	landmarks = solrLocalityLandmarkRepository.findByLandmark(searchTerm);
		    	localities = solrLocalityLandmarkRepository.findByLocality(searchTerm); 
		    	cities = solrCityRepository.findByQueryAnnotation(searchTerm);
		    }else{
		    	landmarks = IteratorUtils.toList(solrLocalityLandmarkRepository.findAll().iterator());
		    	localities = IteratorUtils.toList(solrLocalityLandmarkRepository.findAll().iterator()); 
		    	cities = IteratorUtils.toList(solrCityRepository.findAll().iterator());
		    }
			if(landmarks != null && !landmarks.isEmpty()){
				for(SolrLocalityLandmarkDocument document : landmarks){
					SolrCityDocument city = solrCityRepository.findOne(document.getCityId());
					SolrCityLandmarkLocalityResponse landmark = new SolrCityLandmarkLocalityResponse();
					BeanUtil.map(document, landmark);
					if(city!=null)landmark.setCity(city.getCity());
					response.add(landmark);
				}
			}
			if(localities != null && !localities.isEmpty()){
				for(SolrLocalityLandmarkDocument document : localities){
					SolrCityDocument city = solrCityRepository.findOne(document.getCityId());
					SolrCityLandmarkLocalityResponse locality = new SolrCityLandmarkLocalityResponse();
					BeanUtil.map(document, locality);
					if(city!=null)locality.setCity(city.getCity());
					response.add(locality);
				}
			}
			if(cities != null && !cities.isEmpty()){
				List<SolrCityLandmarkLocalityResponse> citiesResponse = new ArrayList<SolrCityLandmarkLocalityResponse>();
				BeanUtil.map(cities, citiesResponse);
				response.addAll(citiesResponse);
			}
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Landmark Locality");
		}
		return response;

	}

}
