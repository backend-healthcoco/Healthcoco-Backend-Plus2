package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.DoctorContactUs;
import com.dpdocter.collections.DoctorContactUsCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorContactUsRepository;
import com.dpdocter.services.DoctorContactUsService;

@Service
public class DoctorContactUSServiceImpl implements DoctorContactUsService {
	
	private static Logger logger = Logger.getLogger(DoctorContactUSServiceImpl.class.getName());
	
	@Autowired
	DoctorContactUsRepository doctorContactUsRepository;

	@Override
	@Transactional
	public DoctorContactUs submitDoctorContactUSInfo(DoctorContactUs doctorContactUs) {
		DoctorContactUs response = null;
		DoctorContactUsCollection doctorContactUsCollection = new DoctorContactUsCollection();
		if(doctorContactUs != null)
		{
			BeanUtil.map(doctorContactUs, doctorContactUsCollection);
			try {
				doctorContactUsCollection.setUserName(doctorContactUs.getEmail());
				doctorContactUsCollection = doctorContactUsRepository.save(doctorContactUsCollection);
				if(doctorContactUsCollection != null)
				{
					response = new DoctorContactUs();
					BeanUtil.map(doctorContactUsCollection, response);
				}
			} catch (DuplicateKeyException de) {
			    logger.error(de);
			    throw new BusinessException(ServiceError.Unknown, "An account already exists with this email address.Please use another email address to register.");
			} catch (BusinessException be) {
			    logger.error(be);
			    throw be;
			} catch (Exception e) {
			    e.printStackTrace();
			    logger.error(e + " Error occured while creating doctor");
			    throw new BusinessException(ServiceError.Unknown, "Error occured while creating doctor");
			}
		}
		return response;
	}
	
	@Override
	@Transactional
	public List<DoctorContactUs> getDoctorContactList(int page, int size) {
		List<DoctorContactUs> response = null;
		try{
			List<DoctorContactUsCollection> doctorContactUsCollections = null;
			if(size > 0)
			{
				doctorContactUsCollections = doctorContactUsRepository.findAll(new PageRequest(page, size, Direction.DESC, "createdTime")).getContent();
			}
			else 
			{
				doctorContactUsCollections = doctorContactUsRepository.findAll(new Sort(Direction.DESC, "createdTime"));
			}
			if(doctorContactUsCollections != null){
				response = new ArrayList<DoctorContactUs>();
				BeanUtil.map(doctorContactUsCollections, response);
			}
		}catch(Exception e){
			logger.error("Error while getting hospitals "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while getting doctor contact List "+ e.getMessage());
		}
		return response;
	}

}
