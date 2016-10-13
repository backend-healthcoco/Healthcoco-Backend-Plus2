package com.dpdocter.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.repository.DynamicUIRepository;
import com.dpdocter.services.DynamicUIService;
@Service
public class DynamicUIServiceImpl implements DynamicUIService{

	@Autowired
	DynamicUIRepository dynamicUIRepository;
	
}
