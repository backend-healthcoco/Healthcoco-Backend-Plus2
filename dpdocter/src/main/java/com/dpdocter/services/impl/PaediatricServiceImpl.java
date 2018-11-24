package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.lucene.search.grouping.AbstractGroupFacetCollector.GroupedFacetResult;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.GrowthChart;
import com.dpdocter.collections.GrowthChartCollection;
import com.dpdocter.collections.VaccineBrandAssociationCollection;
import com.dpdocter.collections.VaccineCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.GrowthChartRepository;
import com.dpdocter.repository.VaccineRepository;
import com.dpdocter.request.MultipleVaccineEditRequest;
import com.dpdocter.request.VaccineRequest;
import com.dpdocter.response.GroupedVaccineBrandAssociationResponse;
import com.dpdocter.response.VaccineBrandAssociationResponse;
import com.dpdocter.response.VaccineResponse;
import com.dpdocter.services.PaediatricService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class PaediatricServiceImpl implements PaediatricService{
	
	@Autowired
	private GrowthChartRepository growthChartRepository;
	
	@Autowired
	private VaccineRepository vaccineRepository;

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	@Transactional
	public GrowthChart addEditGrowthChart(GrowthChart growthChart)
	{
		GrowthChart response = null;
		GrowthChartCollection growthChartCollection = null;
		try {
			if(growthChart.getId() != null)
			{
				growthChartCollection = growthChartRepository.findOne(new ObjectId(growthChart.getId()));
			}
			else
			{
				growthChartCollection = new GrowthChartCollection();
			}
			BeanUtil.map(growthChart, growthChartCollection);
			growthChartCollection = growthChartRepository.save(growthChartCollection);
			if(growthChartCollection != null){
				response = new GrowthChart();
				 BeanUtil.map(growthChartCollection, response);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
			
		}
		return response;
	}
	
	@Override
	@Transactional
	public GrowthChart getGrowthChartById(String id) {
		GrowthChart response = null;
		GrowthChartCollection growthChartCollection = null;
		try {
			growthChartCollection = growthChartRepository.findOne(new ObjectId(id));
			if (growthChartCollection != null) {
				response = new GrowthChart();
				BeanUtil.map(growthChartCollection, response);
			} else {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;

		}
		return response;
	}
	
	/*public List<GrowthChart> getGrowthChartById(String patientId, String doctorId, String locationId, String hospitalId, int page, int size) {
		List<GrowthChart> growthCharts = null;
		GrowthChartCollection growthChartCollection = null;
		try {
			growthChartCollection = growthChartRepository.findOne(new ObjectId(id));
			if (growthChartCollection != null) {
				response = new GrowthChart();
				BeanUtil.map(growthChartCollection, response);
			} else {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;

		}
		return response;
	}*/

	@Override
	@Transactional
	public Boolean discardGrowthChart(String id, Boolean discarded) {
		Boolean response = false;
		GrowthChartCollection growthChartCollection = null;
		try {
			growthChartCollection = growthChartRepository.findOne(new ObjectId(id));
			if (growthChartCollection != null) {
				growthChartCollection.setDiscarded(discarded);
				growthChartRepository.save(growthChartCollection);
				response = true;
			} else {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return response;
	}
	

	@Override
	@Transactional
	public VaccineResponse addEditVaccine(VaccineRequest request)
	{
		 VaccineResponse response = null;
		 VaccineCollection vaccineCollection = null;
		try {
			if(request.getId() != null)
			{
				vaccineCollection = vaccineRepository.findOne(new ObjectId(request.getId()));
			}
			else
			{
				vaccineCollection = new VaccineCollection();
			}
			BeanUtil.map(request, vaccineCollection);
			vaccineCollection = vaccineRepository.save(vaccineCollection);
			if(vaccineCollection != null){
				response = new VaccineResponse();
				 BeanUtil.map(vaccineCollection, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return response;
	}
	
	@Override
	@Transactional
	public Boolean addEditMultipleVaccine(List<VaccineRequest> requests) {
		Boolean response = false;
		VaccineCollection vaccineCollection = null;
		try {
			for (VaccineRequest request : requests) {
				if (request.getId() != null) {
					vaccineCollection = vaccineRepository.findOne(new ObjectId(request.getId()));
				} else {
					vaccineCollection = new VaccineCollection();
				}
				BeanUtil.map(request, vaccineCollection);
				vaccineCollection = vaccineRepository.save(vaccineCollection);
				response = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean addEditMultipleVaccineStatus(MultipleVaccineEditRequest request) {
		Boolean response = false;
		VaccineCollection vaccineCollection = null;
		try {
			for (String id : request.getIds()) {
				if (id != null) {
					vaccineCollection = vaccineRepository.findOne(new ObjectId(id));
					vaccineCollection.setStatus(request.getStatus());
					vaccineCollection = vaccineRepository.save(vaccineCollection);
					response = true;
				}

			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return response;
	}
	
	@Override
	@Transactional
	public VaccineResponse getVaccineById(String id) {
		VaccineResponse response = null;
		VaccineCollection vaccineCollection = null;
		try {
			vaccineCollection = vaccineRepository.findOne(new ObjectId(id));
			if (vaccineCollection != null) {
				response = new VaccineResponse();
				BeanUtil.map(vaccineCollection, response);
			} else {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;

		}
		return response;
	}
	
	@Override
	@Transactional
	public List<VaccineResponse> getVaccineList(String patientId , String doctorId, String locationId, String hospitalId , String updatedTime) {
		List<VaccineResponse> responses = null;
		try {
			//Criteria criteria = new Criteria();
			
			long createdTimestamp = Long.parseLong(updatedTime);
			
			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));
			
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}
			
			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria.and("patientId").is(new ObjectId(patientId));
			}
		/*	
			AggregationOperation aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id", new BasicDBObject("dueDate", "$vaccineResponses.dueDate"))
							.append("vaccineResponses", new BasicDBObject("$push", "$vaccineResponses")).append("dueDate",
									new BasicDBObject("$first", "$diagnosticTest.dueDate"))));
			
			*/
			responses = mongoTemplate.aggregate(
					Aggregation.newAggregation(
							Aggregation.lookup("vaccine_brand_cl", "vaccineBrandId", "_id", "vaccineBrand"),
							new CustomAggregationOperation(new BasicDBObject("$unwind",
									new BasicDBObject("path", "$vaccineBrand").append("preserveNullAndEmptyArrays",
											true))),
							Aggregation.match(criteria), Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
					VaccineCollection.class, VaccineResponse.class).getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return responses;
	}
	
	@Override
	@Transactional
	public List<VaccineBrandAssociationResponse> getVaccineBrandAssociation(String vaccineId, String vaccineBrandId) {

		List<VaccineBrandAssociationResponse> responses = null;
		try {
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(vaccineId)) {
				criteria.and("vaccineId").is(new ObjectId(vaccineId));
			}

			if (!DPDoctorUtils.anyStringEmpty(vaccineBrandId)) {
				criteria.and("vaccineBrandId").is(new ObjectId(vaccineBrandId));
			}

			responses = mongoTemplate
					.aggregate(
							Aggregation.newAggregation(
									Aggregation.lookup("vaccine_brand_cl", "vaccineBrandId", "_id", "vaccineBrand"),
									new CustomAggregationOperation(new BasicDBObject("$unwind",
											new BasicDBObject("path", "$vaccineBrand")
													.append("preserveNullAndEmptyArrays", true))),
									Aggregation.lookup("vaccine_cl", "vaccineId", "_id", "vaccine"),
									new CustomAggregationOperation(new BasicDBObject("$unwind",
											new BasicDBObject("path", "$vaccine").append("preserveNullAndEmptyArrays",
													true))),
									Aggregation.match(criteria),
									Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
							VaccineBrandAssociationCollection.class, VaccineBrandAssociationResponse.class)
					.getMappedResults();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return responses;
	}
	
	
	@Override
	@Transactional
	public List<GroupedVaccineBrandAssociationResponse> getGroupedVaccineBrandAssociation(List<String> vaccineIds)
	{
		List<ObjectId> vaccineObjectIds = null;
		List<GroupedVaccineBrandAssociationResponse> responses = null;
		try {
			Criteria criteria = new Criteria();
			
			
			if(vaccineIds != null)
			{
				vaccineObjectIds = new ArrayList<>();
				for (String id : vaccineIds) {
					vaccineObjectIds.add(new ObjectId(id));
				}
			}
			
			if (vaccineIds != null) {
				criteria.and("vaccineId").in(vaccineObjectIds);
			}
			
			AggregationOperation aggregationOperation = new CustomAggregationOperation(new BasicDBObject("$group",
					new BasicDBObject("_id", new BasicDBObject("name", "$vaccine.name"))
							.append("vaccine", new BasicDBObject("$push", "$vaccine")).append("name",
									new BasicDBObject("$first", "$vaccine.name"))
							.append("id",
									new BasicDBObject("$first", "$vaccine.id"))));
			
			responses = mongoTemplate.aggregate(
					Aggregation.newAggregation(
							Aggregation.lookup("vaccine_brand_cl", "vaccineBrandId", "_id", "vaccineBrand"),
							new CustomAggregationOperation(new BasicDBObject("$unwind",
									new BasicDBObject("path", "$vaccineBrand").append("preserveNullAndEmptyArrays",
											true))),
							Aggregation.match(criteria),aggregationOperation, Aggregation.sort(new Sort(Direction.DESC, "createdTime"))),
					VaccineBrandAssociationCollection.class, GroupedVaccineBrandAssociationResponse.class).getMappedResults();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return responses;
	}
	
	
	
	
}
