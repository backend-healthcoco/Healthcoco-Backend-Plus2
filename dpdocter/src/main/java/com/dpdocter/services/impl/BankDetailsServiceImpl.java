package com.dpdocter.services.impl;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.BankDetails;
import com.dpdocter.collections.BankDetailsCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.BankDetailsRepository;
import com.dpdocter.security.AES;
import com.dpdocter.services.BankDetailsService;

import common.util.web.DPDoctorUtils;

@Service
public class BankDetailsServiceImpl implements BankDetailsService {
	
	private static Logger logger = LogManager.getLogger(BankDetailsServiceImpl.class.getName());

	@Autowired
	private BankDetailsRepository bankDetailsRepository;
	
	@Value(value = "${secret.key.account.details}")
	private String secretKeyAccountDetails;
	
	@Override
	public Boolean addEditBankDetails(BankDetails request) {
		Boolean response=false;
		
		try {
			BankDetailsCollection bankDetailsCollection=new BankDetailsCollection();
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
			request.setCreatedTime(new Date());
			request.setUpdatedTime(new Date());
			BeanUtil.map(request, bankDetailsCollection);
		}
		bankDetailsCollection.setAccountholderName(AES.encrypt(bankDetailsCollection.getAccountholderName(), secretKeyAccountDetails));
		bankDetailsCollection.setAccountNumber(AES.encrypt(bankDetailsCollection.getAccountNumber(), secretKeyAccountDetails));
		bankDetailsCollection.setIfscNumber(AES.encrypt(bankDetailsCollection.getIfscNumber(), secretKeyAccountDetails));
		bankDetailsCollection.setPanCardNumber(AES.encrypt(bankDetailsCollection.getPanCardNumber(), secretKeyAccountDetails));
		bankDetailsCollection.setBankName(AES.encrypt(bankDetailsCollection.getBankName(), secretKeyAccountDetails));
		bankDetailsCollection.setBranchCity(AES.encrypt(bankDetailsCollection.getBranchCity(), secretKeyAccountDetails));
		
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
		BankDetailsCollection bankDetailsCollection=bankDetailsRepository.findByDoctorId(new ObjectId(doctorId)).orElse(null);
		
		 if(bankDetailsCollection==null)
		    {
		    	throw new BusinessException(ServiceError.NotFound,"Error no such id");
		    }

		 bankDetailsCollection.setAccountholderName(AES.decrypt(bankDetailsCollection.getAccountholderName(), secretKeyAccountDetails));
			bankDetailsCollection.setAccountNumber(AES.decrypt(bankDetailsCollection.getAccountNumber(), secretKeyAccountDetails));
			bankDetailsCollection.setIfscNumber(AES.decrypt(bankDetailsCollection.getIfscNumber(), secretKeyAccountDetails));
			bankDetailsCollection.setPanCardNumber(AES.decrypt(bankDetailsCollection.getPanCardNumber(), secretKeyAccountDetails));
			bankDetailsCollection.setBankName(AES.decrypt(bankDetailsCollection.getBankName(), secretKeyAccountDetails));
			bankDetailsCollection.setBranchCity(AES.decrypt(bankDetailsCollection.getBranchCity(), secretKeyAccountDetails));

			
		 response=new BankDetails();

			BeanUtil.map(bankDetailsCollection, response);
		
		}
		catch (BusinessException e) {
			logger.error("Error while searching the id "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown,"Error while searching the id");
		}
		return response;
	}	

}
