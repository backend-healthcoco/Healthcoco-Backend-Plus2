package com.dpdocter.services.impl;

import java.util.Date;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.InvoiceAndReceiptInitials;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.services.BillingService;

@Service
public class BillingServiceImpl implements BillingService {

	private static Logger logger = Logger.getLogger(BillingServiceImpl.class.getName());
	
	@Autowired
	LocationRepository locationRepository;
	
	@Override
	public InvoiceAndReceiptInitials updateInitials(InvoiceAndReceiptInitials request) {
		InvoiceAndReceiptInitials response = null;
		try{
			LocationCollection locationCollection = locationRepository.findOne(new ObjectId(request.getLocationId()));
			if(locationCollection != null){
				locationCollection.setInvoiceInitial(request.getInvoiceInitial());
				locationCollection.setReceiptInitial(request.getReceiptInitial());
				locationCollection.setUpdatedTime(new Date());
				locationCollection = locationRepository.save(locationCollection);
				response = new InvoiceAndReceiptInitials();
				BeanUtil.map(locationCollection, response);
				response.setLocationId(locationCollection.getId().toString());
			}else{
				throw new BusinessException(ServiceError.InvalidInput, "Invalid location Id");
			}
		}catch(BusinessException be){
			logger.error(be);
			throw be;
		}catch(Exception e){
			logger.error("Error while updating billing initials"+e);
			throw new BusinessException(ServiceError.Unknown, "Error while updating billing initials"+e);
		}
		return response;
	}

}
