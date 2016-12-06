package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.ContactUs;
import com.dpdocter.beans.Resume;
import com.dpdocter.beans.SendAppLink;
import com.dpdocter.beans.SubscriptionDetail;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.SubscriptionDetailRepository;
import com.dpdocter.services.AdminServices;
import com.dpdocter.services.SubscriptionService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.ADMIN_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ADMIN_BASE_URL, description = "")
public class AdminAPI {

	private static Logger logger = Logger.getLogger(AdminAPI.class.getName());

	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	private AdminServices adminServices;

	@Value(value = "${image.path}")
	private String imagePath;

	@Path(value = PathProxy.AdminUrls.ADD_RESUMES)
	@POST
	@ApiOperation(value = PathProxy.AdminUrls.ADD_RESUMES, notes = PathProxy.AdminUrls.ADD_RESUMES)
	public Response<Resume> addResumes(Resume request) {
		if (request == null
				|| DPDoctorUtils.anyStringEmpty(request.getEmailAddress(), request.getName(), request.getMobileNumber())
				|| request.getFile() == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Resume resume = adminServices.addResumes(request);
		resume.setPath(getFinalImageURL(resume.getPath()));
		Response<Resume> response = new Response<Resume>();
		response.setData(resume);
		return response;
	}

	@Path(value = PathProxy.AdminUrls.ADD_CONTACT_US)
	@POST
	@ApiOperation(value = PathProxy.AdminUrls.ADD_CONTACT_US, notes = PathProxy.AdminUrls.ADD_CONTACT_US)
	public Response<ContactUs> addContactUs(ContactUs request) {
		if (request == null || DPDoctorUtils.anyStringEmpty(request.getEmailAddress(), request.getName(),
				request.getMobileNumber(), request.getMessage())) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		ContactUs contactUs = adminServices.addContactUs(request);
		Response<ContactUs> response = new Response<ContactUs>();
		response.setData(contactUs);
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null)
			return imagePath + imageURL;
		else
			return null;
	}

	@Path(value = PathProxy.AdminUrls.SEND_APP_LINK)
	@POST
	@ApiOperation(value = PathProxy.AdminUrls.SEND_APP_LINK, notes = PathProxy.AdminUrls.SEND_APP_LINK)
	public Response<Boolean> sendLink(SendAppLink request) {
		if (request == null || request.getAppType() == null || (DPDoctorUtils.anyStringEmpty(request.getEmailAddress())
				&& DPDoctorUtils.anyStringEmpty(request.getMobileNumber()))) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}

		Boolean sendLinkresponse = adminServices.sendLink(request);
		Response<Boolean> response = new Response<Boolean>();
		response.setData(sendLinkresponse);
		return response;
	}

	@Path(value = PathProxy.AdminUrls.Add_SUBCRIPTION_DETAIL)
	@GET
	@ApiOperation(value = PathProxy.AdminUrls.Add_SUBCRIPTION_DETAIL, notes = PathProxy.AdminUrls.Add_SUBCRIPTION_DETAIL)
	public Response<Object> addSubscriptionDetail() {
		List<SubscriptionDetail> reponseSubscriptionDetail = subscriptionService.addsubscriptionData();
		Response<Object> response = new Response<Object>();
		response.setDataList(reponseSubscriptionDetail);
		return response;
	}

}
