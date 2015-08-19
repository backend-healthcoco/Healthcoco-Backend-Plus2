package com.dpdocter.solr.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.enums.CitySearchType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.solr.document.SolrCityDocument;
import com.dpdocter.solr.document.SolrComplaintsDocument;
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
			if(type == null)   response = solrLocalityLandmarkRepository.findByCityId(cityId,searchTerm);
			else{
				if(type.equalsIgnoreCase(CitySearchType.LANDMARK.getType())){
					response = solrLocalityLandmarkRepository.findByCityIdAndLandmark(cityId,searchTerm);
				}
				if(type.equalsIgnoreCase(CitySearchType.LOCALITY.getType())){
					response = solrLocalityLandmarkRepository.findByCityIdAndLocality(cityId,searchTerm);
				}
			}
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Landmark Locality");
		}
		return response;
	    
	}

}
