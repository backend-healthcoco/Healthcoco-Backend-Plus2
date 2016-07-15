package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
				doctorContactUsCollection = doctorContactUsRepository.save(doctorContactUsCollection);
				if(doctorContactUsCollection != null)
				{
					response = new DoctorContactUs();
					BeanUtil.map(doctorContactUsCollection, response);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.warn(e);
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
