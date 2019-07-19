package com.dpdocter.services.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.VersionControl;
import com.dpdocter.collections.VersionControlCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.VersionControlRepository;
import com.dpdocter.services.VersionControlService;

import common.util.web.DPDoctorUtils;

@Service
public class VersionControlServiceImpl implements VersionControlService{
	
	private static Logger logger = Logger.getLogger(VersionControlServiceImpl.class.getName());
	
	@Autowired
	private VersionControlRepository versionControlRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	@Transactional
	public Integer checkVersion(VersionControl versionControl)
	{
		Integer versionControlCode = 0; // default value for success - no change
		VersionControlCollection versionControlCollection = versionControlRepository.findByApplicationType(versionControl.getAppType().toString() , versionControl.getDeviceType().toString());
		if(versionControl != null || versionControlCollection != null)
		{
			if(versionControlCollection.getMajorVersion() > versionControl.getMajorVersion())
			{
				versionControlCode = 3; // major version change - forced update
			}
			else if(versionControlCollection.getMajorVersion() == versionControl.getMajorVersion() && versionControlCollection.getMinorVersion() > versionControl.getMinorVersion())
			{
				versionControlCode = 2; // minor version change - forced update
			}
			else if(versionControlCollection.getMajorVersion() == versionControl.getMajorVersion() && versionControlCollection.getMinorVersion() ==  versionControl.getMinorVersion() && versionControlCollection.getPatchVersion() > versionControl.getPatchVersion())
			{
				versionControlCode = 1; // minor version change - optional update
			}
			
		}
		return versionControlCode;
	}
	
	@Override
	@Transactional
	public List<VersionControl> getVersionsList(long page, int size ,String searchTerm)
	{
		List<VersionControl> response = null;
		Criteria criteria = null;
		try{
			if(!DPDoctorUtils.anyStringEmpty(searchTerm))criteria = new Criteria().orOperator(new Criteria("deviceType").regex("^"+searchTerm,"i"),(new Criteria("appType").regex("^"+searchTerm,"i")));
			Aggregation aggregation = null;
			if(criteria != null)
			{
				if(size > 0)aggregation = Aggregation.newAggregation(Aggregation.match(criteria),Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size), Aggregation.limit(size));
				else aggregation = Aggregation.newAggregation(Aggregation.match(criteria),Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			}
			else
			{
				if(size > 0)aggregation = Aggregation.newAggregation(Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size), Aggregation.limit(size));
				else aggregation = Aggregation.newAggregation(Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			}
			
			AggregationResults<VersionControl> aggregationResults = mongoTemplate.aggregate(aggregation, VersionControlCollection.class, VersionControl.class);
			response = aggregationResults.getMappedResults();
		}catch(Exception e){
			logger.error("Error while getting versions "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while getting version control List "+ e.getMessage());
		}
		return response;
	}
	
	@Override
	@Transactional
	public VersionControl changeVersion(VersionControl versionControl)
	{
		VersionControl response = null;
		VersionControlCollection versionControlCollection = versionControlRepository.findByApplicationType(versionControl.getAppType().toString() , versionControl.getDeviceType().toString());
		if(versionControl != null )
		{
			if(versionControlCollection == null)
			{
				versionControlCollection = new VersionControlCollection();
			}
			else
			{
				versionControl.setId(versionControlCollection.getId().toString());
			}
			BeanUtil.map(versionControl, versionControlCollection);
			try {
				versionControlCollection = versionControlRepository.save(versionControlCollection);
				if(versionControlCollection != null)
				{
					response = new VersionControl();
					BeanUtil.map(versionControlCollection, response);
				}
			} catch (Exception e) {
				logger.warn(e);
			}
		}
		return response;
		
	}

}
