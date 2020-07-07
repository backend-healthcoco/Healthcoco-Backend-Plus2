package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.BulkSmsCredits;
import com.dpdocter.beans.BulkSmsPackage;
import com.dpdocter.collections.BulkSmsCreditsCollection;
import com.dpdocter.collections.BulkSmsHistoryCollection;
import com.dpdocter.collections.BulkSmsPackageCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.BulkSmsCreditsRepository;
import com.dpdocter.repository.BulkSmsPackageRepository;
import com.dpdocter.services.BulkSmsServices;

import common.util.web.DPDoctorUtils;

@Service
public class BulkSmsServiceImpl implements BulkSmsServices{

	@Autowired
	private BulkSmsPackageRepository bulkSmsRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private BulkSmsCreditsRepository bulkSmsCreditRepository;
	
	@Override
	public BulkSmsPackage addEditBulkSmsPackage(BulkSmsPackage request) {
		BulkSmsPackage response=null;
		try {
			BulkSmsPackageCollection bulkSms=null;
			if(!DPDoctorUtils.anyStringEmpty(request.getId())) {
				bulkSms=bulkSmsRepository.findById(new ObjectId(request.getId())).orElse(null);
				if(bulkSms==null) {
					throw new BusinessException(ServiceError.Unknown,"Id not found");
				}
				BeanUtil.map(request, bulkSms);
				bulkSms.setUpdatedTime(new Date());
			}else {
				bulkSms=new BulkSmsPackageCollection();
				BeanUtil.map(request, bulkSms);
				bulkSms.setCreatedTime(new Date());
				bulkSms.setUpdatedTime(new Date());
				
			}
			bulkSmsRepository.save(bulkSms);
			response=new BulkSmsPackage();
			BeanUtil.map(bulkSms, response);
			
		}catch (BusinessException e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,"Error while addEdit Bulksms package"+ e.getMessage());
		}
		return response;
	}

	@Override
	public List<BulkSmsPackage> getBulkSmsPackage(int page, int size, String searchTerm, Boolean discarded) {
		List<BulkSmsPackage> response=null;
		try {
		
			
			Criteria criteria = new Criteria();
			
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("packageName").regex("^" + searchTerm, "i"),
						new Criteria("packageName").regex("^" + searchTerm));
			
			
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						
						Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));
				
				} else {
					aggregation = Aggregation.newAggregation( 
							Aggregation.match(criteria),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

				}
				response = mongoTemplate.aggregate(aggregation, BulkSmsPackageCollection.class, BulkSmsPackage.class).getMappedResults();
			
			}catch (BusinessException e) {
				e.printStackTrace();
				throw new BusinessException(ServiceError.Unknown,"Error while getting Bulksms package"+ e.getMessage());
			}
		
		return response;
	}


	@Override
	public Integer CountBulkSmsPackage(String searchTerm, Boolean discarded) {
		Integer count=null;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("packageName").regex("^" + searchTerm, "i"),
						new Criteria("packageName").regex("^" + searchTerm));
			
			
			
			count=(int) mongoTemplate.count(new Query(criteria), BulkSmsPackageCollection.class);
		
		}catch (BusinessException e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,"Error while getting Bulksms package"+ e.getMessage());
		}
		return count;
	}


	@Override
	public BulkSmsPackage getBulkSmsPackageByDoctorId(String doctorId) {
		BulkSmsPackage response=null;
		try {
			BulkSmsPackageCollection bulkSms=null;
			if(!DPDoctorUtils.anyStringEmpty(doctorId)) {
				bulkSms=bulkSmsRepository.findByDoctorId(new ObjectId(doctorId));
				if(bulkSms==null) {
					throw new BusinessException(ServiceError.Unknown,"Id not found");
				}
				response=new BulkSmsPackage();
				BeanUtil.map(bulkSms, response);
			}
	}
		catch (BusinessException e) {
		e.printStackTrace();
		throw new BusinessException(ServiceError.Unknown,"Error while getting Bulksms package"+ e.getMessage());
		}
	return response;
	}
	
	@Override
	public BulkSmsCredits getCreditsByDoctorId(String doctorId) {
		BulkSmsCredits response=null;
		try {
			BulkSmsCreditsCollection bulk=new BulkSmsCreditsCollection();
			bulk=bulkSmsCreditRepository.findByDoctorId(new ObjectId(doctorId));
			
			if(bulk==null)
			{
				throw new BusinessException(ServiceError.Unknown,"DoctorId not found for bulk sms");
			}
			response=new BulkSmsCredits();
			BeanUtil.map(bulk,response);
			
			
			
		}catch (BusinessException e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,"Error while getting Bulksms credits count"+ e.getMessage());
		}
		return response;
	}

	@Override
	public List<BulkSmsCredits> getBulkSmsHistory(int page, int size, String searchTerm, String doctorId) {
		List<BulkSmsCredits> response=null;
		try {
		
			
			Criteria criteria = new Criteria();
			
			if(!DPDoctorUtils.anyStringEmpty(doctorId))
			{
				ObjectId doctorObjectId=new ObjectId(doctorId);
				criteria.and("doctorId").is(doctorObjectId);
			}
			
			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("packageName").regex("^" + searchTerm, "i"),
						new Criteria("packageName").regex("^" + searchTerm));
			
			
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						
						Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
						Aggregation.skip((page) * size), Aggregation.limit(size));
				
				} else {
					aggregation = Aggregation.newAggregation( 
							Aggregation.match(criteria),
							Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

				}
				response = mongoTemplate.aggregate(aggregation, BulkSmsHistoryCollection.class, BulkSmsCredits.class).getMappedResults();
			
			}catch (BusinessException e) {
				e.printStackTrace();
				throw new BusinessException(ServiceError.Unknown,"Error while getting Bulksms package"+ e.getMessage());
			}
		
			return response;
		}

}
