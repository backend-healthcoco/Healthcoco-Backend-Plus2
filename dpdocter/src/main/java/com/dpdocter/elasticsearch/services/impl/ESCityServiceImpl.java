package com.dpdocter.elasticsearch.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import com.dpdocter.elasticsearch.document.ESCityDocument;
import com.dpdocter.elasticsearch.document.ESLandmarkLocalityDocument;
import com.dpdocter.elasticsearch.repository.ESCityRepository;
import com.dpdocter.elasticsearch.repository.ESLandmarkLocalityRepository;
import com.dpdocter.elasticsearch.services.ESCityService;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.services.TransactionalManagementService;
import com.dpdocter.solr.beans.SolrCityLandmarkLocalityResponse;

import common.util.web.DPDoctorUtils;

@Service
public class ESCityServiceImpl  implements ESCityService{

    @Autowired
    private ESCityRepository esCityRepository;

    @Autowired
    private ESLandmarkLocalityRepository esLocalityLandmarkRepository;
    
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private TransactionalManagementService transnationalService;

    @Override
    public boolean addCities(ESCityDocument esCityDocument) {
	boolean response = false;
	try {
		if(esCityDocument.getLatitude()!= null && esCityDocument.getLongitude() != null)esCityDocument.setGeoPoint(new GeoPoint(esCityDocument.getLatitude(), esCityDocument.getLongitude()));
		esCityRepository.save(esCityDocument);
	    transnationalService.addResource(esCityDocument.getId(), Resource.CITY, true);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return response;
    }

//    @Override
//    public boolean activateDeactivateCity(String cityId, boolean activate) {
//	boolean response = false;
//	try {
//	    ESCityDocument solrCity = esCityRepository.findOne(cityId);
//	    solrCity.setIsActivated(activate);
//	    esCityRepository.save(solrCity);
//	    transnationalService.addResource(cityId, Resource.CITY, true);
//	    response = true;
//	} catch (Exception e) {
//	    e.printStackTrace();
//	}
//	return response;
//    }
//
    @Override
    public boolean addLocalityLandmark(ESLandmarkLocalityDocument esLandmarkLocalityDocument) {
	boolean response = false;
	try {
		if(esLandmarkLocalityDocument.getLatitude()!= null && esLandmarkLocalityDocument.getLongitude() != null)esLandmarkLocalityDocument.setGeoPoint(new GeoPoint(esLandmarkLocalityDocument.getLatitude(), esLandmarkLocalityDocument.getLongitude()));
	    esLocalityLandmarkRepository.save(esLandmarkLocalityDocument);
	    transnationalService.addResource(esLandmarkLocalityDocument.getId(), Resource.LANDMARKLOCALITY, true);
	    response = true;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return response;
    }
//    @Override
//    public List<ESCityDocument> searchCity(String searchTerm) {
//	List<ESCityDocument> response = null;
//	try {
//	    response = esCityRepository.findByQueryAnnotation(searchTerm);
//	} catch (Exception e) {
//	    e.printStackTrace();
//	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching City");
//	}
//	return response;
//
//    }
//
//    @Override
//    public List<ESLocalityLandmarkDocument> searchLandmarkLocality(String cityId, String type, String searchTerm) {
//	List<ESLocalityLandmarkDocument> response = null;
//	try {
//	    if (type == null)
//		response = esLocalityLandmarkRepository.findByCityId(cityId, searchTerm);
//	    else {
//		if (type.equalsIgnoreCase(CitySearchType.LANDMARK.getType())) {
//		    response = esLocalityLandmarkRepository.findByCityIdAndLandmark(cityId, searchTerm);
//		}
//		if (type.equalsIgnoreCase(CitySearchType.LOCALITY.getType())) {
//		    response = esLocalityLandmarkRepository.findByCityIdAndLocality(cityId, searchTerm);
//		}
//	    }
//	} catch (Exception e) {
//	    e.printStackTrace();
//	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Searching Landmark Locality");
//	}
//	return response;
//    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SolrCityLandmarkLocalityResponse> searchCityLandmarkLocality(String searchTerm, String latitude, String longitude) {
	List<SolrCityLandmarkLocalityResponse> response = new ArrayList<SolrCityLandmarkLocalityResponse>();
	try {
	    List<ESLandmarkLocalityDocument> landmarks = null;
	    List<ESLandmarkLocalityDocument> localities = null;
	    List<ESCityDocument> cities = null;
	    if (DPDoctorUtils.anyStringEmpty(latitude, longitude)) {
		if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
		    landmarks = esLocalityLandmarkRepository.findByLandmark(searchTerm);
		    localities = esLocalityLandmarkRepository.findByLocality(searchTerm);
		    cities = esCityRepository.findByQueryAnnotation(searchTerm);
		} else {
		    landmarks = IteratorUtils.toList(esLocalityLandmarkRepository.findAll().iterator());
		    cities = IteratorUtils.toList(esCityRepository.findAll().iterator());
		}
	    } else {
	    	BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder().filter(QueryBuilders.geoDistanceQuery("geoPoint").lat(Double.parseDouble(latitude)).lon(Double.parseDouble(longitude)).distance("30km"));
		if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
			landmarks = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("landmark", searchTerm))).build(), ESLandmarkLocalityDocument.class);
			localities = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("locality", searchTerm))).build(), ESLandmarkLocalityDocument.class);
		    cities = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder.must(QueryBuilders.matchQuery("city", searchTerm))).build(), ESCityDocument.class);
		} else {
		    landmarks = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder.mustNot(QueryBuilders.existsQuery("locality"))).build(), ESLandmarkLocalityDocument.class);
		    localities = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder.mustNot(QueryBuilders.existsQuery("landmark"))).build(), ESLandmarkLocalityDocument.class);
		    cities = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(), ESCityDocument.class);
		}
	    }
	    if (landmarks != null && !landmarks.isEmpty()) {
		for (ESLandmarkLocalityDocument document : landmarks) {
		    ESCityDocument city = esCityRepository.findOne(document.getCityId());
		    SolrCityLandmarkLocalityResponse landmark = new SolrCityLandmarkLocalityResponse();
		    BeanUtil.map(document, landmark);
		    if (city != null) {
			landmark.setCity(city.getCity());
			landmark.setState(city.getState());
			landmark.setCountry(city.getCountry());
		    }
		    response.add(landmark);
		}
	    }
	    if (localities != null && !localities.isEmpty()) {
		for (ESLandmarkLocalityDocument document : localities) {
		    ESCityDocument city = esCityRepository.findOne(document.getCityId());
		    SolrCityLandmarkLocalityResponse locality = new SolrCityLandmarkLocalityResponse();
		    BeanUtil.map(document, locality);
		    if (city != null) {
			locality.setCity(city.getCity());
			locality.setState(city.getState());
			locality.setCountry(city.getCountry());
		    }
			response.add(locality);
		}
	    }
	    if (cities != null && !cities.isEmpty()) {
		for (ESCityDocument document : cities) {
		    SolrCityLandmarkLocalityResponse city = new SolrCityLandmarkLocalityResponse();
		    BeanUtil.map(document, city);
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

}
