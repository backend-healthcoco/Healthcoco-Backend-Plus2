package com.dpdocter.services.impl;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.collections.BankDetailsCollection;
import com.dpdocter.collections.SettlementCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.BankDetailsRepository;
import com.dpdocter.repository.SettlementRepository;
import com.dpdocter.response.SettlementResponse;
import com.dpdocter.services.PaymentServices;


@Service
public class PaymentserviceImpl implements PaymentServices {

	 private static Logger logger = LogManager.getLogger(PaymentserviceImpl.class);
	 
	 @Autowired
		private BankDetailsRepository bankDetailsRepository;
	
	 @Autowired
	 private SettlementRepository settlementRepository;
	
	@Override
	public String updateSettlementReport(SettlementResponse request) {
		String response=null;
		try {
			SettlementCollection settlements=new SettlementCollection();
			BeanUtil.map(request, settlements);
			settlements.setCreatedTime(new Date());
			settlements.setUpdatedTime(new Date());
			
			BankDetailsCollection payment=null;
			payment = bankDetailsRepository.findByRazorPayAccountId(settlements.getAccount_id());
			System.out.println("payment"+payment);
			if(payment!=null)
				if(payment.getDoctorId()!=null)
			{
						settlements.setDoctorId(payment.getDoctorId());
			}
			settlementRepository.save(settlements);
			System.out.println("settlements"+settlements);
			
			response="settlement report added successfully";
		}catch (BusinessException e) {
			logger.error("Error while getting settlements " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting settlements " + e.getMessage());

		}
		return response;
	}

}
