package com.dpdocter.services.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.dpdocter.services.AnalyticService;

public class AnalyticServiceImpl implements AnalyticService {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	Logger logger = Logger.getLogger(AnalyticServiceImpl.class);
	
	

}
