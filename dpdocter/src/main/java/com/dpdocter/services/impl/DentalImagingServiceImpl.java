package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.DentalDiagnosticService;
import com.dpdocter.beans.DentalImaging;
import com.dpdocter.beans.DentalImagingLocationServiceAssociation;
import com.dpdocter.beans.DentalImagingRequest;
import com.dpdocter.collections.DentalDiagnosticServiceCollection;
import com.dpdocter.collections.DentalImagingCollection;
import com.dpdocter.collections.DentalImagingLocationServiceAssociationCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DentalImagingLocationServiceAssociationRepository;
import com.dpdocter.repository.DentalImagingRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.response.DentalImagingLocationServiceAssociationLookupResponse;
import com.dpdocter.response.DentalLabPickupLookupResponse;
import com.dpdocter.services.DentalImagingService;

import common.util.web.DPDoctorUtils;

@Service
public class DentalImagingServiceImpl implements DentalImagingService {

	@Autowired
	LocationRepository locationRepository;

	@Autowired
	DentalImagingRepository dentalImagingRepository;

	@Autowired
	DentalImagingLocationServiceAssociationRepository dentalImagingLocationServiceAssociationRepository;

	@Autowired
	MongoTemplate mongoTemplate;
	private static Logger logger = Logger.getLogger(DentalImagingServiceImpl.class.getName());

	@Override
	@Transactional
	public DentalImaging addEditDentalImagingRequest(DentalImagingRequest request) {
		DentalImaging response = null;
		DentalImagingCollection dentalImagingCollection = null;
		String requestId = null;

		try {
			LocationCollection locationCollection = locationRepository.findOne(new ObjectId(request.getLocationId()));
			
			if (request.getId() != null) {
				dentalImagingCollection = dentalImagingRepository.findOne(new ObjectId(request.getId()));
				if (dentalImagingCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "Record not found");
				}
				BeanUtil.map(request, dentalImagingCollection);
				

				dentalImagingCollection.setUpdatedTime(new Date());
				dentalImagingCollection = dentalImagingRepository.save(dentalImagingCollection);
			} else {
				requestId = UniqueIdInitial.DENTAL_IMAGING.getInitial() + DPDoctorUtils.generateRandomId();
				dentalImagingCollection = new DentalImagingCollection();
				BeanUtil.map(request, dentalImagingCollection);
				dentalImagingCollection.setRequestId(requestId);
				dentalImagingCollection.setCreatedTime(new Date());
				dentalImagingCollection.setUpdatedTime(new Date());
			

			
			dentalImagingCollection = dentalImagingRepository.save(dentalImagingCollection);
			}
			response = new DentalImaging();
			BeanUtil.map(dentalImagingCollection, response);
		} catch (Exception e) {
			logger.warn(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional

	public List<DentalImaging> getRequests(String locationId, String hospitalId, String doctorId, Long from, Long to,
			String searchTerm, int size, int page) {

		List<DentalImaging> response = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (to != null) {
				criteria.and("updatedTime").gte(new Date(from)).lte(DPDoctorUtils.getEndTime(new Date(to)));
			} else {
				criteria.and("updatedTime").gte(new Date(from));
			}
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<DentalImaging> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalImagingCollection.class, DentalImaging.class);
			response = aggregationResults.getMappedResults();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public List<DentalDiagnosticService> getServices(String searchTerm, String type, int page, int size) {
		List<DentalDiagnosticService> response = null;

		try {
			Criteria criteria = new Criteria();
			Aggregation aggregation = null;

			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria.and("type").is(type);
			}

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm), new Criteria("name").regex(searchTerm + ".*"));
			}
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<DentalDiagnosticService> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalDiagnosticServiceCollection.class, DentalDiagnosticService.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean addEditDentalImagingLocationServiceAssociation(
			List<DentalImagingLocationServiceAssociation> request) {
		Boolean response = false;
		ObjectId oldId = null;
		DentalImagingLocationServiceAssociationCollection dentalImagingLocationServiceAssociationCollection = null;
		try {
			for (DentalImagingLocationServiceAssociation dentalImagingLocationServiceAssociation : request) {
				dentalImagingLocationServiceAssociationCollection = dentalImagingLocationServiceAssociationRepository
						.findbyServiceLocationHospital(new ObjectId(dentalImagingLocationServiceAssociation.getDentalDiagnosticServiceId()),
								new ObjectId(dentalImagingLocationServiceAssociation.getLocationId()), new ObjectId(dentalImagingLocationServiceAssociation.getHospitalId()));
				if (dentalImagingLocationServiceAssociationCollection == null) {
					dentalImagingLocationServiceAssociationCollection = new DentalImagingLocationServiceAssociationCollection();
				} else {
					oldId = dentalImagingLocationServiceAssociationCollection.getId();
				}
				BeanUtil.map(dentalImagingLocationServiceAssociation,
						dentalImagingLocationServiceAssociationCollection);
				dentalImagingLocationServiceAssociationCollection.setId(oldId);
				dentalImagingLocationServiceAssociationCollection = dentalImagingLocationServiceAssociationRepository
						.save(dentalImagingLocationServiceAssociationCollection);
			}
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.warn(e);
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input" + e);
		}
		return response;
	}
	
	@Override
	@Transactional
	public List<DentalImagingLocationServiceAssociationLookupResponse> getLocationAssociatedServices(String locationId , String hospitalId , String searchTerm, String type, int page, int size) {
		List<DentalImagingLocationServiceAssociationLookupResponse> response = null;

		try {
			Criteria criteria = new Criteria();
			Aggregation aggregation = null;

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}

			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria.and("service.type").is(type);
			}

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria = criteria.orOperator(new Criteria("service.name").regex("^" + searchTerm, "i"),
						new Criteria("service.name").regex("^" + searchTerm), new Criteria("service.name").regex(searchTerm + ".*"));
			}
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.lookup("dental_diagnostic_service_cl", "dentalDiagnosticServiceId", "_id", "service"),
						Aggregation.unwind("service"),Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.lookup("dental_diagnostic_service_cl", "dentalDiagnosticServiceId", "_id", "service"),
						Aggregation.unwind("service"),Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<DentalImagingLocationServiceAssociationLookupResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					DentalImagingLocationServiceAssociationCollection.class, DentalImagingLocationServiceAssociationLookupResponse.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return response;
	}

}
