package com.dpdocter.webservices;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.SMSDeliveryReports;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.SMSServices;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.BULK_SMS_BASE_URL,produces = MediaType.APPLICATION_JSON_VALUE ,consumes = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.BULK_SMS_BASE_URL, description = "Endpoint for bulk sms")
public class BulkMessageApi {

	 private static Logger logger = LogManager.getLogger(BulkMessageApi.class);
	
	  @Autowired
	    private SMSServices smsServices;

	    
	
	
	
	@PostMapping(value = PathProxy.BulkSMSUrls.UPDATE_DELIVERY_REPORTS)
    @ApiOperation(value = PathProxy.BulkSMSUrls.UPDATE_DELIVERY_REPORTS, notes = PathProxy.BulkSMSUrls.UPDATE_DELIVERY_REPORTS)
    public String updateDeliveryReports(String request) {

	try {
	    request = request.replaceFirst("data=", "");
	    ObjectMapper mapper = new ObjectMapper();
	    @SuppressWarnings("deprecation")
		List<SMSDeliveryReports> list = mapper.readValue(request, TypeFactory.collectionType(List.class, SMSDeliveryReports.class));
	    smsServices.updateDeliveryReports(list);
	} catch (JsonParseException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.InvalidInput, e.getMessage());
	} catch (JsonMappingException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.InvalidInput, e.getMessage());
	} catch (IOException e) {
	    logger.error(e);
	    throw new BusinessException(ServiceError.InvalidInput, e.getMessage());
	}
	return "true";
    }

}
