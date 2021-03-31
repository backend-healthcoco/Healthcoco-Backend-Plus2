package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.ContactUs;
import com.dpdocter.beans.Resume;
import com.dpdocter.beans.SendAppLink;
import com.dpdocter.beans.SubscriptionDetail;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.AdminServices;
import com.dpdocter.services.BirthdaySMSServices;
import com.dpdocter.services.SubscriptionService;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value=PathProxy.ADMIN_BASE_URL,produces = MediaType.APPLICATION_JSON ,consumes = MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ADMIN_BASE_URL, description = "",produces = MediaType.APPLICATION_JSON ,consumes = MediaType.APPLICATION_JSON)
public class AdminAPI {

	private static Logger logger = LogManager.getLogger(AdminAPI.class.getName());

	@Autowired
	private BirthdaySMSServices birthdaySMSServices;

	@Autowired
	private SubscriptionService subscriptionService;

	@Autowired
	private AdminServices adminServices;

	@Autowired
	private TransactionalManagementService transactionalManagementService;
	
	@Value(value = "${image.path}")
	private String imagePath;

	@PostMapping(value = PathProxy.AdminUrls.ADD_RESUMES)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_RESUMES, notes = PathProxy.AdminUrls.ADD_RESUMES)
	public Response<Resume> addResumes(@RequestBody Resume request) {
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

	@PostMapping(value = PathProxy.AdminUrls.ADD_CONTACT_US)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_CONTACT_US, notes = PathProxy.AdminUrls.ADD_CONTACT_US)
	public Response<ContactUs> addContactUs(@RequestBody ContactUs request) {
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

	@PostMapping(value = PathProxy.AdminUrls.SEND_APP_LINK)
	@ApiOperation(value = PathProxy.AdminUrls.SEND_APP_LINK, notes = PathProxy.AdminUrls.SEND_APP_LINK)
	public Response<Boolean> sendLink(@RequestBody SendAppLink request) {
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

	@GetMapping(value = PathProxy.AdminUrls.Add_SUBCRIPTION_DETAIL)
	@ApiOperation(value = PathProxy.AdminUrls.Add_SUBCRIPTION_DETAIL, notes = PathProxy.AdminUrls.Add_SUBCRIPTION_DETAIL)
	public Response<Object> addSubscriptionDetail() {
		List<SubscriptionDetail> reponseSubscriptionDetail = subscriptionService.addsubscriptionData();
		Response<Object> response = new Response<Object>();
		response.setDataList(reponseSubscriptionDetail);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.SEND_BIRTHDAY_WISH)
	
	@ApiOperation(value = PathProxy.AdminUrls.SEND_BIRTHDAY_WISH, notes = PathProxy.AdminUrls.SEND_BIRTHDAY_WISH)
	public Response<Boolean> sendBirthdayWish() {
		birthdaySMSServices.sendBirthdaySMSToPatients();
		Response<Boolean> response = new Response<Boolean>();
		response.setData(true);
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.DISCARD_DUPLICATE_CLINICAL_ITEMS)
	@ApiOperation(value = PathProxy.AdminUrls.DISCARD_DUPLICATE_CLINICAL_ITEMS, notes = PathProxy.AdminUrls.DISCARD_DUPLICATE_CLINICAL_ITEMS)
	public Response<Boolean> discardDuplicateClinicalItems(@PathVariable("doctorId") String doctorId) {
		
		Response<Boolean> response = new Response<Boolean>();
		response.setData(adminServices.discardDuplicateClinicalItems(doctorId));
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.COPY_CLINICAL_ITEMS)
	@ApiOperation(value = PathProxy.AdminUrls.COPY_CLINICAL_ITEMS, notes = PathProxy.AdminUrls.COPY_CLINICAL_ITEMS)
	public Response<Boolean> copyClinicalItems(@PathVariable("doctorId") String doctorId, @PathVariable("locationId") String locationId,
			@MatrixParam("drIds") List<String> drIds) {
		
		Response<Boolean> response = new Response<Boolean>();
		response.setData(adminServices.copyClinicalItems(doctorId, locationId, drIds));
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.UPDATE_LOCATION_IN_ROLE)
	@ApiOperation(value = PathProxy.AdminUrls.UPDATE_LOCATION_IN_ROLE, notes = PathProxy.AdminUrls.UPDATE_LOCATION_IN_ROLE)
	public Response<Boolean> updateLocationIdInRole() {
		
		Response<Boolean> response = new Response<Boolean>();
		response.setData(adminServices.updateLocationIdInRole());
		return response;
	}

	@GetMapping(value = PathProxy.AdminUrls.ADD_SERVICES)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_SERVICES, notes = PathProxy.AdminUrls.ADD_SERVICES)
	public Response<Boolean> addServices() {
		
		Response<Boolean> response = new Response<Boolean>();
		response.setData(adminServices.addServices());
		return response;
	}
	
	@GetMapping(value = PathProxy.AdminUrls.UPDATE_SERVICES_AND_SPECIALITIES_IN_DOCTORS)
	@ApiOperation(value = PathProxy.AdminUrls.UPDATE_SERVICES_AND_SPECIALITIES_IN_DOCTORS, notes = PathProxy.AdminUrls.UPDATE_SERVICES_AND_SPECIALITIES_IN_DOCTORS)
	public Response<Boolean> updateServicesAndSpecialities() {
		
		Response<Boolean> response = new Response<Boolean>();
		response.setData(adminServices.updateServicesAndSpecialities());
		return response;
	}
	
	@GetMapping(value = PathProxy.AdminUrls.ADD_SERVICES_OF_SPECIALITIES_IN_DOCTORS)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_SERVICES_OF_SPECIALITIES_IN_DOCTORS, notes = PathProxy.AdminUrls.ADD_SERVICES_OF_SPECIALITIES_IN_DOCTORS)
	public Response<Boolean> addServicesOfSpecialities() {
		
		Response<Boolean> response = new Response<Boolean>();
		response.setData(adminServices.addServicesOfSpecialities());
		return response;
	}
	
	@GetMapping(value = PathProxy.AdminUrls.ADD_SPECIALITIES)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_SPECIALITIES, notes = PathProxy.AdminUrls.ADD_SPECIALITIES)
	public Response<Boolean> addSpecialities() {
		
		Response<Boolean> response = new Response<Boolean>();
		response.setData(adminServices.addSpecialities());
		return response;
	}
	
	@GetMapping(value = PathProxy.AdminUrls.ADD_SYMPTOMS_DISEASES_CONDITION)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_SYMPTOMS_DISEASES_CONDITION, notes = PathProxy.AdminUrls.ADD_SYMPTOMS_DISEASES_CONDITION)
	public Response<Boolean> addSymptomsDiseasesCondition() {
		
		Response<Boolean> response = new Response<Boolean>();
		response.setData(adminServices.addSymptomsDiseasesCondition());
		return response;
	}
	
	@GetMapping(value = PathProxy.AdminUrls.ADD_ALL_TO_ELASTICSEARCH)
	@ApiOperation(value = PathProxy.AdminUrls.ADD_ALL_TO_ELASTICSEARCH, notes = PathProxy.AdminUrls.ADD_ALL_TO_ELASTICSEARCH)
	public Response<Boolean> addDataFromMongoToElasticSearch() {
		
		Response<Boolean> response = new Response<Boolean>();
		response.setData(transactionalManagementService.addDataFromMongoToElasticSearch());
		return response;
	}
}
