package com.dpdocter.webservices;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.request.MailSubsciptionRequest;
import com.dpdocter.services.MailService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.EMAIL_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.EMAIL_BASE_URL, description = "Endpoint for  mail ")
public class EmailAPI {

	@Autowired
	private MailService mailService;

	@POST
	@ApiOperation(value = PathProxy.EmailUrls.UNSUBSCRIBE_MAIL, notes = PathProxy.EmailUrls.UNSUBSCRIBE_MAIL)
	public Response<Boolean> unsubscribeMail(MailSubsciptionRequest request) {
		if (DPDoctorUtils.anyStringEmpty(request.getSubscriberId())) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");

		}
		Response<Boolean> response = new Response<Boolean>();
		response.setData(mailService.subscribeMail(request));
		return response;
	}

}
