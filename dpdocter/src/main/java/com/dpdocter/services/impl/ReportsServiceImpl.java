package com.dpdocter.services.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.DoctorContactUs;
import com.dpdocter.beans.IPDReports;
import com.dpdocter.beans.OPDReports;
import com.dpdocter.beans.OTReports;
import com.dpdocter.collections.DoctorContactUsCollection;
import com.dpdocter.collections.IPDReportsCollection;
import com.dpdocter.collections.OPDReportsCollection;
import com.dpdocter.collections.OTReportsCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.IPDReportsRepository;
import com.dpdocter.repository.OPDReportsRepository;
import com.dpdocter.repository.OTReportsRepository;
import com.dpdocter.services.ReportsService;

import common.util.web.DPDoctorUtils;

@Service
public class ReportsServiceImpl implements ReportsService {

	private static Logger logger = Logger.getLogger(ReportsServiceImpl.class.getName());

	@Autowired
	IPDReportsRepository ipdReportsRepository;

	@Autowired
	OPDReportsRepository opdReportsRepository;

	@Autowired
	OTReportsRepository otReportsRepository;
	
	@Override
	@Transactional
	public IPDReports submitIPDReport(IPDReports ipdReports) {
		IPDReports response = null;
		IPDReportsCollection ipdReportsCollection = new IPDReportsCollection();
		if(ipdReports != null)
		{
			BeanUtil.map(ipdReports, ipdReportsCollection);
			try {
				ipdReportsCollection = ipdReportsRepository.save(ipdReportsCollection);
				
				if(ipdReportsCollection != null)
				{
					BeanUtil.map(ipdReportsCollection, ipdReports);
					response = new IPDReports();
					response = ipdReports;
				}
			} catch (Exception e) {
			    e.printStackTrace();
			    logger.error(e + " Error occured while creating IPD Records");
			    throw new BusinessException(ServiceError.Unknown, "Error occured while creating IPD Records");
			}
		}
		return response;
	}
	
	@Override
	@Transactional
	public OPDReports submitOPDReport(OPDReports opdReports) {
		OPDReports response = null;
		OPDReportsCollection opdReportsCollection = new OPDReportsCollection();
		if(opdReports != null)
		{
			BeanUtil.map(opdReports, opdReportsCollection);
			try {
				opdReportsCollection = opdReportsRepository.save(opdReportsCollection);
				
				if(opdReportsCollection != null)
				{
					BeanUtil.map(opdReportsCollection, opdReports);
					response = new OPDReports();
					response = opdReports;
				}
			} catch (Exception e) {
			    e.printStackTrace();
			    logger.error(e + " Error occured while creating OPD Records");
			    throw new BusinessException(ServiceError.Unknown, "Error occured while OPD Records");
			}
		}
		return response;
	}
	
	@Override
	@Transactional
	public OTReports submitOTReport(OTReports otReports) {
		OTReports response = null;
		OTReportsCollection otReportsCollection = new OTReportsCollection();
		if(otReports != null)
		{
			BeanUtil.map(otReports, otReportsCollection);
			try {
				otReportsCollection = otReportsRepository.save(otReportsCollection);
				
				if(otReportsCollection != null)
				{
					BeanUtil.map(otReportsCollection, otReports);
					response = new OTReports();
					response = otReports;
				}
			} catch (Exception e) {
			    e.printStackTrace();
			    logger.error(e + " Error occured while creating OPD Records");
			    throw new BusinessException(ServiceError.Unknown, "Error occured while OPD Records");
			}
		}
		return response;
	}
	
	@Override
	@Transactional
	public List<OPDReports> getOPDReportsList(Long startDate, Long endDate , String doctorId, String LocationId , String hospitalId) {
		List<OPDReports> response = null;
		//String searchTerm = null;
		Criteria criteria = null;
		/*try{
			if(!DPDoctorUtils.anyStringEmpty(searchTerm))criteria = new Criteria().orOperator(new Criteria("firstName").regex("^"+searchTerm,"i"),(new Criteria("emailAddress").regex("^"+searchTerm,"i")));
			Aggregation aggregation = null;
			if(criteria != null)
			{
				if(size > 0)aggregation = Aggregation.newAggregation(Aggregation.match(criteria),Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size), Aggregation.limit(size));
				else aggregation = Aggregation.newAggregation(Aggregation.match(criteria),Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			}
			else
			{
				if(size > 0)aggregation = Aggregation.newAggregation(Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size), Aggregation.limit(size));
				else aggregation = Aggregation.newAggregation(Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			}
			
			AggregationResults<DoctorContactUs> aggregationResults = mongoTemplate.aggregate(aggregation, DoctorContactUsCollection.class, DoctorContactUs.class);
			response = aggregationResults.getMappedResults();
		}catch(Exception e){
			logger.error("Error while getting hospitals "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while getting doctor contact List "+ e.getMessage());
		}*/
		return response;
	}

	@Override
	@Transactional
	public List<IPDReports> getIPDReportsList(Long startDate, Long endDate , String doctorId, String LocationId , String hospitalId) {
		List<IPDReports> response = null;
		//String searchTerm = null;
		Criteria criteria = null;
		/*try{
			if(!DPDoctorUtils.anyStringEmpty(searchTerm))criteria = new Criteria().orOperator(new Criteria("firstName").regex("^"+searchTerm,"i"),(new Criteria("emailAddress").regex("^"+searchTerm,"i")));
			Aggregation aggregation = null;
			if(criteria != null)
			{
				if(size > 0)aggregation = Aggregation.newAggregation(Aggregation.match(criteria),Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size), Aggregation.limit(size));
				else aggregation = Aggregation.newAggregation(Aggregation.match(criteria),Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			}
			else
			{
				if(size > 0)aggregation = Aggregation.newAggregation(Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size), Aggregation.limit(size));
				else aggregation = Aggregation.newAggregation(Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			}
			
			AggregationResults<DoctorContactUs> aggregationResults = mongoTemplate.aggregate(aggregation, DoctorContactUsCollection.class, DoctorContactUs.class);
			response = aggregationResults.getMappedResults();
		}catch(Exception e){
			logger.error("Error while getting hospitals "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while getting doctor contact List "+ e.getMessage());
		}*/
		return response;
	}
	
	@Override
	@Transactional
	public List<OTReports> getOTReportsList(Long startDate, Long endDate , String doctorId, String LocationId , String hospitalId) {
		List<OTReports> response = null;
		//String searchTerm = null;
		Criteria criteria = null;
		/*try{
			if(!DPDoctorUtils.anyStringEmpty(searchTerm))criteria = new Criteria().orOperator(new Criteria("firstName").regex("^"+searchTerm,"i"),(new Criteria("emailAddress").regex("^"+searchTerm,"i")));
			Aggregation aggregation = null;
			if(criteria != null)
			{
				if(size > 0)aggregation = Aggregation.newAggregation(Aggregation.match(criteria),Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size), Aggregation.limit(size));
				else aggregation = Aggregation.newAggregation(Aggregation.match(criteria),Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			}
			else
			{
				if(size > 0)aggregation = Aggregation.newAggregation(Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size), Aggregation.limit(size));
				else aggregation = Aggregation.newAggregation(Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			}
			
			AggregationResults<DoctorContactUs> aggregationResults = mongoTemplate.aggregate(aggregation, DoctorContactUsCollection.class, DoctorContactUs.class);
			response = aggregationResults.getMappedResults();
		}catch(Exception e){
			logger.error("Error while getting hospitals "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while getting doctor contact List "+ e.getMessage());
		}*/
		return response;
	}

	

}
