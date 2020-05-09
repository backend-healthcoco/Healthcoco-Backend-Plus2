package com.dpdocter.services.impl;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.BankDetails;
import com.dpdocter.collections.BankDetailsCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.BankDetailsRepository;
import com.dpdocter.services.BankDetailsService;

import common.util.web.DPDoctorUtils;

@Service
public class BankDetailsServiceImpl implements BankDetailsService {
	
	private static Logger logger = LogManager.getLogger(BankDetailsServiceImpl.class.getName());

	@Autowired
	private BankDetailsRepository bankDetailsRepository;
	
	@Override
	public Boolean addEditBankDetails(BankDetails request) {
		Boolean response=false;
		BankDetailsCollection bankDetailsCollection=null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				bankDetailsCollection = bankDetailsRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (bankDetailsCollection == null) {
					throw new BusinessException(ServiceError.NotFound, "bankDetailsId Not found");
				}
			request.setUpdatedTime(new Date());
			BeanUtil.map(request, bankDetailsCollection);
			
			
		}
		else{
			bankDetailsCollection=new BankDetailsCollection();
			bankDetailsCollection.setCreatedTime(new Date());
			bankDetailsCollection.setUpdatedTime(new Date());
			BeanUtil.map(request, bankDetailsCollection);
		}
		bankDetailsRepository.save(bankDetailsCollection);
		response=true;
		} catch (BusinessException e) {
			logger.error("Error while add/edit bank Details " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while add/edit bank Details " + e.getMessage());

		}
		
		return response;
	}

	@Override
	public BankDetails getBankDetailsByDoctorId(String doctorId) {
		BankDetails response=null;
		try {
		BankDetailsCollection bankDetailsCollection=bankDetailsRepository.findById(new ObjectId(doctorId)).orElse(null);
		
		 if(bankDetailsCollection==null)
		    {
		    	throw new BusinessException(ServiceError.NotFound,"Error no such id");
		    }
			
			BeanUtil.map(bankDetailsCollection, response);
		
		}
		catch (BusinessException e) {
			logger.error("Error while searching the id "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while searching the id");
		}
		return response;
	}	

}
