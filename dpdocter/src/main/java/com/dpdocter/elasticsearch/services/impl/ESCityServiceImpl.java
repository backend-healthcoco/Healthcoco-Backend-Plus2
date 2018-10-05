package com.dpdocter.elasticsearch.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;
import org.bson.types.ObjectId;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.City;
import com.dpdocter.elasticsearch.beans.ESCityLandmarkLocalityResponse;
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

import common.util.web.DPDoctorUtils;

@Service
public class ESCityServiceImpl implements ESCityService {

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
			if (esCityDocument.getLatitude() != null && esCityDocument.getLongitude() != null)
				esCityDocument.setGeoPoint(new GeoPoint(esCityDocument.getLatitude(), esCityDocument.getLongitude()));
			esCityRepository.save(esCityDocument);
			transnationalService.addResource(new ObjectId(esCityDocument.getId()), Resource.CITY, true);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public boolean activateDeactivateCity(String cityId, Boolean activate) {
		boolean response = false;
		try {
			ESCityDocument solrCity = esCityRepository.findById(cityId).orElse(null);
			solrCity.setIsActivated(activate);
			esCityRepository.save(solrCity);
			transnationalService.addResource(new ObjectId(cityId), Resource.CITY, true);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public boolean addLocalityLandmark(ESLandmarkLocalityDocument esLandmarkLocalityDocument) {
		boolean response = false;
		try {
			if (esLandmarkLocalityDocument.getLatitude() != null && esLandmarkLocalityDocument.getLongitude() != null)
				esLandmarkLocalityDocument.setGeoPoint(new GeoPoint(esLandmarkLocalityDocument.getLatitude(),
						esLandmarkLocalityDocument.getLongitude()));
			esLocalityLandmarkRepository.save(esLandmarkLocalityDocument);
			transnationalService.addResource(new ObjectId(esLandmarkLocalityDocument.getId()),
					Resource.LANDMARKLOCALITY, true);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	// @Override
	// public List<ESCityDocument> searchCity(String searchTerm) {
	// List<ESCityDocument> response = null;
	// try {
	// response = esCityRepository.findByQueryAnnotation(searchTerm);
	// } catch (Exception e) {
	// e.printStackTrace();
	// throw new BusinessException(ServiceError.Unknown, "Error Occurred While
	// Searching City");
	// }
	// return response;
	//
	// }
	//
	// @Override
	// public List<ESLocalityLandmarkDocument> searchLandmarkLocality(String
	// cityId, String type, String searchTerm) {
	// List<ESLocalityLandmarkDocument> response = null;
	// try {
	// if (type == null)
	// response = esLocalityLandmarkRepository.findByCityId(cityId, searchTerm);
	// else {
	// if (type.equalsIgnoreCase(CitySearchType.LANDMARK.getType())) {
	// response = esLocalityLandmarkRepository.findByCityIdAndLandmark(cityId,
	// searchTerm);
	// }
	// if (type.equalsIgnoreCase(CitySearchType.LOCALITY.getType())) {
	// response = esLocalityLandmarkRepository.findByCityIdAndLocality(cityId,
	// searchTerm);
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// throw new BusinessException(ServiceError.Unknown, "Error Occurred While
	// Searching Landmark Locality");
	// }
	// return response;
	// }

	@SuppressWarnings("unchecked")
	@Override
	public List<ESCityLandmarkLocalityResponse> searchCityLandmarkLocality(String searchTerm, String latitude,
			String longitude) {
		List<ESCityLandmarkLocalityResponse> response = new ArrayList<ESCityLandmarkLocalityResponse>();
		try {
			List<ESLandmarkLocalityDocument> landmarks = null;
			List<ESLandmarkLocalityDocument> localities = null;
			List<ESCityDocument> cities = null;

			int localityLandmarkSize = (int) esLocalityLandmarkRepository.count();
			int citySize = (int) esCityRepository.count();
			if (DPDoctorUtils.anyStringEmpty(latitude, longitude)) {
				if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
					if (localityLandmarkSize > 0) {
						landmarks = elasticsearchTemplate
								.queryForList(
										new NativeSearchQueryBuilder()
												.withQuery(new BoolQueryBuilder().must(
														QueryBuilders.matchPhrasePrefixQuery("landmark", searchTerm)))
												.withPageable(PageRequest.of(0, localityLandmarkSize)).build(),
										ESLandmarkLocalityDocument.class);
						localities = elasticsearchTemplate
								.queryForList(
										new NativeSearchQueryBuilder()
												.withQuery(new BoolQueryBuilder().must(
														QueryBuilders.matchPhrasePrefixQuery("locality", searchTerm)))
												.withPageable(PageRequest.of(0, localityLandmarkSize)).build(),
										ESLandmarkLocalityDocument.class);
					}
					if (citySize > 0) {
						cities = elasticsearchTemplate
								.queryForList(
										new NativeSearchQueryBuilder()
												.withQuery(new BoolQueryBuilder()
														.must(QueryBuilders.matchPhrasePrefixQuery("city", searchTerm))
														.must(QueryBuilders.matchPhrasePrefixQuery("isActivated",
																true)))
												.withPageable(PageRequest.of(0, citySize)).build(),
										ESCityDocument.class);
					}
				} else {
					if (localityLandmarkSize > 0)
						landmarks = IteratorUtils.toList(esLocalityLandmarkRepository
								.findAll(PageRequest.of(0, localityLandmarkSize)).iterator());
					if (citySize > 0) {
						cities = elasticsearchTemplate
								.queryForList(
										new NativeSearchQueryBuilder()
												.withQuery(new BoolQueryBuilder().must(
														QueryBuilders.matchPhrasePrefixQuery("isActivated", true)))
												.withPageable(PageRequest.of(0, citySize)).build(),
										ESCityDocument.class);
					}
				}
			} else {
				BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
						.filter(QueryBuilders.geoDistanceQuery("geoPoint").point(Double.parseDouble(latitude),
								Double.parseDouble(longitude)).distance("30km"));
				if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
					if (localityLandmarkSize > 0) {
						landmarks = elasticsearchTemplate
								.queryForList(
										new NativeSearchQueryBuilder()
												.withQuery(boolQueryBuilder.must(
														QueryBuilders.matchPhrasePrefixQuery("landmark", searchTerm)))
												.withPageable(PageRequest.of(0, localityLandmarkSize)).build(),
										ESLandmarkLocalityDocument.class);
						boolQueryBuilder = new BoolQueryBuilder()
								.filter(QueryBuilders.geoDistanceQuery("geoPoint").point(Double.parseDouble(latitude),
								Double.parseDouble(longitude)).distance("30km"));
						localities = elasticsearchTemplate
								.queryForList(
										new NativeSearchQueryBuilder()
												.withQuery(boolQueryBuilder.must(
														QueryBuilders.matchPhrasePrefixQuery("locality", searchTerm)))
												.withPageable(PageRequest.of(0, localityLandmarkSize)).build(),
										ESLandmarkLocalityDocument.class);
					}
					if (citySize > 0) {
						boolQueryBuilder = new BoolQueryBuilder().filter(QueryBuilders.geoDistanceQuery("geoPoint")
								.point(Double.parseDouble(latitude), Double.parseDouble(longitude)).distance("30km"));
						cities = elasticsearchTemplate
								.queryForList(
										new NativeSearchQueryBuilder()
												.withQuery(boolQueryBuilder
														.must(QueryBuilders.matchPhrasePrefixQuery("city", searchTerm))
														.must(QueryBuilders.matchPhrasePrefixQuery("isActivated",
																true)))
												.withPageable(PageRequest.of(0, citySize)).build(),
										ESCityDocument.class);
					}
				} else {
					if (localityLandmarkSize > 0) {
						landmarks = elasticsearchTemplate
								.queryForList(
										new NativeSearchQueryBuilder()
												.withQuery(
														boolQueryBuilder.mustNot(QueryBuilders.existsQuery("locality")))
												.withPageable(PageRequest.of(0, citySize)).build(),
										ESLandmarkLocalityDocument.class);
						boolQueryBuilder = new BoolQueryBuilder().filter(QueryBuilders.geoDistanceQuery("geoPoint")
								.point(Double.parseDouble(latitude), Double.parseDouble(longitude)).distance("30km"));
						localities = elasticsearchTemplate
								.queryForList(
										new NativeSearchQueryBuilder()
												.withQuery(
														boolQueryBuilder.mustNot(QueryBuilders.existsQuery("landmark")))
												.withPageable(PageRequest.of(0, citySize)).build(),
										ESLandmarkLocalityDocument.class);
					}
					if (citySize > 0) {
						boolQueryBuilder = new BoolQueryBuilder()
								.must(QueryBuilders.matchPhrasePrefixQuery("isActivated", true))
								.filter(QueryBuilders.geoDistanceQuery("geoPoint")
										.point(Double.parseDouble(latitude), Double.parseDouble(longitude)).distance("30km"));
						cities = elasticsearchTemplate
								.queryForList(
										new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
												.withPageable(PageRequest.of(0, citySize)).build(),
										ESCityDocument.class);
					}
				}
			}
			if (landmarks != null && !landmarks.isEmpty()) {
				for (ESLandmarkLocalityDocument document : landmarks) {
					ESCityDocument city = esCityRepository.findById(document.getCityId()).orElse(null);
					ESCityLandmarkLocalityResponse landmark = new ESCityLandmarkLocalityResponse();
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
					ESCityDocument city = esCityRepository.findById(document.getCityId()).orElse(null);
					ESCityLandmarkLocalityResponse locality = new ESCityLandmarkLocalityResponse();
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
					ESCityLandmarkLocalityResponse city = new ESCityLandmarkLocalityResponse();
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

	@SuppressWarnings("unchecked")
	@Override
	public List<City> searchCity(String searchTerm) {
		List<City> response = new ArrayList<City>();
		try {
			List<ESCityDocument> cities = null;

			int citySize = (int) esCityRepository.count();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				if (citySize > 0) {
					cities = elasticsearchTemplate.queryForList(new NativeSearchQueryBuilder()
							.withQuery(new BoolQueryBuilder()
									.must(QueryBuilders.matchPhrasePrefixQuery("city", searchTerm)))
							.withPageable(PageRequest.of(0, citySize)).build(), ESCityDocument.class);
				}
			} else {
				if (citySize > 0)
					cities = IteratorUtils.toList(esCityRepository.findAll(PageRequest.of(0, citySize)).iterator());
			}

			if (cities != null && !cities.isEmpty()) {
				for (ESCityDocument document : cities) {
					City city = new City();
					BeanUtil.map(document, city);
					response.add(city);
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
	public List<ESCityLandmarkLocalityResponse> searchCityLandmarkLocalityForWeb(String searchTerm, String latitude,
			String longitude) {
		List<ESCityLandmarkLocalityResponse> response = new ArrayList<ESCityLandmarkLocalityResponse>();
		try {
			List<ESLandmarkLocalityDocument> landmarks = null;
			List<ESLandmarkLocalityDocument> localities = null;
			List<ESCityDocument> cities = null;

			int localityLandmarkSize = (int) esLocalityLandmarkRepository.count();
			int citySize = (int) esCityRepository.count();
			if (DPDoctorUtils.anyStringEmpty(latitude, longitude)) {
				if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
					if (localityLandmarkSize > 0) {
						landmarks = elasticsearchTemplate
								.queryForList(
										new NativeSearchQueryBuilder()
												.withQuery(new BoolQueryBuilder().must(
														QueryBuilders.matchPhrasePrefixQuery("landmark", searchTerm)))
												.withPageable(PageRequest.of(0, localityLandmarkSize)).build(),
										ESLandmarkLocalityDocument.class);
						localities = elasticsearchTemplate
								.queryForList(
										new NativeSearchQueryBuilder()
												.withQuery(new BoolQueryBuilder().must(
														QueryBuilders.matchPhrasePrefixQuery("locality", searchTerm)))
												.withPageable(PageRequest.of(0, localityLandmarkSize)).build(),
										ESLandmarkLocalityDocument.class);
					}
					if (citySize > 0) {
						cities = elasticsearchTemplate
								.queryForList(
										new NativeSearchQueryBuilder()
												.withQuery(new BoolQueryBuilder()
														.must(QueryBuilders.matchPhrasePrefixQuery("city", searchTerm))
														.must(QueryBuilders.matchPhrasePrefixQuery("isActivated",
																true)))
												.withPageable(PageRequest.of(0, citySize)).build(),
										ESCityDocument.class);
					}
				} else {
					if (localityLandmarkSize > 0)
						landmarks = IteratorUtils.toList(esLocalityLandmarkRepository
								.findAll(PageRequest.of(0, localityLandmarkSize)).iterator());
					if (citySize > 0) {
						cities = elasticsearchTemplate
								.queryForList(
										new NativeSearchQueryBuilder()
												.withQuery(new BoolQueryBuilder().must(
														QueryBuilders.matchPhrasePrefixQuery("isActivated", true)))
												.withPageable(PageRequest.of(0, citySize)).build(),
										ESCityDocument.class);
					}
				}
			} else {
				BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
						.filter(QueryBuilders.geoDistanceQuery("geoPoint")
				.point(Double.parseDouble(latitude), Double.parseDouble(longitude)).distance("30km"));
				if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
					if (localityLandmarkSize > 0) {
						landmarks = elasticsearchTemplate
								.queryForList(
										new NativeSearchQueryBuilder()
												.withQuery(boolQueryBuilder.must(
														QueryBuilders.matchPhrasePrefixQuery("landmark", searchTerm)))
												.withPageable(PageRequest.of(0, localityLandmarkSize)).build(),
										ESLandmarkLocalityDocument.class);
						boolQueryBuilder = new BoolQueryBuilder().filter(QueryBuilders.geoDistanceQuery("geoPoint")
								.point(Double.parseDouble(latitude), Double.parseDouble(longitude)).distance("30km"));
						localities = elasticsearchTemplate
								.queryForList(
										new NativeSearchQueryBuilder()
												.withQuery(boolQueryBuilder.must(
														QueryBuilders.matchPhrasePrefixQuery("locality", searchTerm)))
												.withPageable(PageRequest.of(0, localityLandmarkSize)).build(),
										ESLandmarkLocalityDocument.class);
					}
					if (citySize > 0) {
						boolQueryBuilder = new BoolQueryBuilder().filter(QueryBuilders.geoDistanceQuery("geoPoint")
								.point(Double.parseDouble(latitude), Double.parseDouble(longitude)).distance("30km"));
						cities = elasticsearchTemplate
								.queryForList(
										new NativeSearchQueryBuilder()
												.withQuery(boolQueryBuilder
														.must(QueryBuilders.matchPhrasePrefixQuery("city", searchTerm))
														.must(QueryBuilders.matchPhrasePrefixQuery("isActivated",
																true)))
												.withPageable(PageRequest.of(0, citySize)).build(),
										ESCityDocument.class);
					}
				} else {
					if (localityLandmarkSize > 0) {
						landmarks = elasticsearchTemplate
								.queryForList(
										new NativeSearchQueryBuilder()
												.withQuery(
														boolQueryBuilder.mustNot(QueryBuilders.existsQuery("locality")))
												.withPageable(PageRequest.of(0, citySize)).build(),
										ESLandmarkLocalityDocument.class);
						boolQueryBuilder = new BoolQueryBuilder().filter(QueryBuilders.geoDistanceQuery("geoPoint")
								.point(Double.parseDouble(latitude), Double.parseDouble(longitude)).distance("30km"));
						localities = elasticsearchTemplate
								.queryForList(
										new NativeSearchQueryBuilder()
												.withQuery(
														boolQueryBuilder.mustNot(QueryBuilders.existsQuery("landmark")))
												.withPageable(PageRequest.of(0, citySize)).build(),
										ESLandmarkLocalityDocument.class);
					}
					if (citySize > 0) {
						boolQueryBuilder = new BoolQueryBuilder()
								.must(QueryBuilders.matchPhrasePrefixQuery("isActivated", true))
						.filter(QueryBuilders.geoDistanceQuery("geoPoint")
								.point(Double.parseDouble(latitude), Double.parseDouble(longitude)).distance("30km"));
						cities = elasticsearchTemplate
								.queryForList(
										new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
												.withPageable(PageRequest.of(0, citySize)).build(),
										ESCityDocument.class);
					}
				}
			}
			if (landmarks != null && !landmarks.isEmpty()) {
				for (ESLandmarkLocalityDocument document : landmarks) {
					ESCityDocument city = esCityRepository.findById(document.getCityId()).orElse(null);
					ESCityLandmarkLocalityResponse landmark = new ESCityLandmarkLocalityResponse();
					BeanUtil.map(document, landmark);
					if (city != null) {
						landmark.setLocality(!DPDoctorUtils.anyStringEmpty(document.getLocality())
								? document.getLocality().trim().replace(" ", "-") : document.getLocality());
						landmark.setLandmark(!DPDoctorUtils.anyStringEmpty(document.getLandmark())
								? document.getLandmark().trim().replace(" ", "-") : document.getLandmark());
						landmark.setCity(!DPDoctorUtils.anyStringEmpty(city.getCity())
								? city.getCity().trim().replace(" ", "-") : city.getCity());
						landmark.setState(!DPDoctorUtils.anyStringEmpty(city.getState())
								? city.getState().trim().replace(" ", "-") : city.getState());
						landmark.setCountry(!DPDoctorUtils.anyStringEmpty(city.getCountry())
								? city.getCountry().trim().replace(" ", "-") : city.getCountry());
					}
					response.add(landmark);
				}
			}
			if (localities != null && !localities.isEmpty()) {
				for (ESLandmarkLocalityDocument document : localities) {
					ESCityDocument city = esCityRepository.findById(document.getCityId()).orElse(null);
					ESCityLandmarkLocalityResponse locality = new ESCityLandmarkLocalityResponse();
					BeanUtil.map(document, locality);
					if (city != null) {
						locality.setLocality(!DPDoctorUtils.anyStringEmpty(document.getLocality())
								? document.getLocality().trim().replace(" ", "-") : document.getLocality());
						locality.setLandmark(!DPDoctorUtils.anyStringEmpty(document.getLandmark())
								? document.getLandmark().trim().replace(" ", "-") : document.getLandmark());
						locality.setCity(!DPDoctorUtils.anyStringEmpty(city.getCity())
								? city.getCity().trim().replace(" ", "-") : city.getCity());
						locality.setState(!DPDoctorUtils.anyStringEmpty(city.getState())
								? city.getState().trim().replace(" ", "-") : city.getState());
						locality.setCountry(!DPDoctorUtils.anyStringEmpty(city.getCountry())
								? city.getCountry().trim().replace(" ", "-") : city.getCountry());
					}
					response.add(locality);
				}
			}
			if (cities != null && !cities.isEmpty()) {
				for (ESCityDocument document : cities) {
					ESCityLandmarkLocalityResponse city = new ESCityLandmarkLocalityResponse();
					BeanUtil.map(document, city);
					if (document != null) {

						city.setCity(!DPDoctorUtils.anyStringEmpty(city.getCity())
								? city.getCity().trim().replace(" ", "-") : city.getCity());
						city.setState(!DPDoctorUtils.anyStringEmpty(city.getState())
								? city.getState().trim().replace(" ", "-") : city.getState());
						city.setCountry(!DPDoctorUtils.anyStringEmpty(city.getCountry())
								? city.getCountry().trim().replace(" ", "-") : city.getCountry());
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
}