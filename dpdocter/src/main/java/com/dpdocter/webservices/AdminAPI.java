package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.ContactUs;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.Resume;
import com.dpdocter.beans.Speciality;
import com.dpdocter.beans.User;
import com.dpdocter.response.DoctorResponse;
import com.dpdocter.services.AdminServices;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.ADMIN_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.ADMIN_BASE_URL, description = "Endpoint for admin")
public class AdminAPI {

//	private static Logger logger = Logger.getLogger(AdminAPI.class.getName());
	
	@Autowired
	AdminServices adminServices;
	
    @Value(value = "${image.path}")
    private String imagePath;

	@Path(value = PathProxy.AdminUrls.GET_INACTIVE_USERS)
	@GET
	@ApiOperation(value = PathProxy.AdminUrls.GET_INACTIVE_USERS, notes = PathProxy.AdminUrls.GET_INACTIVE_USERS)
	public Response<User> getInactiveUsers(@QueryParam(value = "page") int page, @QueryParam(value = "size") int size){
		
		List<User> users = adminServices.getInactiveUsers(page, size);
		Response<User> response = new Response<User>();
		response.setDataList(users);
		return response;
	}
	
	@Path(value = PathProxy.AdminUrls.GET_HOSPITALS)
	@GET
	@ApiOperation(value = PathProxy.AdminUrls.GET_HOSPITALS, notes = PathProxy.AdminUrls.GET_HOSPITALS)
	public Response<Hospital> getHospitals(@QueryParam(value = "page") int page, @QueryParam(value = "size") int size){
		
		List<Hospital> hospitals = adminServices.getHospitals(page, size);
		Response<Hospital> response = new Response<Hospital>();
		response.setDataList(hospitals);
		return response;
	}
	
	@Path(value = PathProxy.AdminUrls.GET_CLINICS_AND_LABS)
	@GET
	@ApiOperation(value = PathProxy.AdminUrls.GET_CLINICS_AND_LABS, notes = PathProxy.AdminUrls.GET_CLINICS_AND_LABS)
	public Response<Location> getClinics(@QueryParam(value = "page") int page, @QueryParam(value = "size") int size, @QueryParam(value = "hospitalId") String hospitalId,
			@QueryParam(value = "isClinic") @DefaultValue("false") Boolean isClinic, @QueryParam(value = "isLab") @DefaultValue("false") Boolean isLab,
			@QueryParam(value = "searchTerm") String searchTerm){
		
		List<Location> locations = adminServices.getClinics(page, size, hospitalId, isClinic, isLab, searchTerm);
		
		Response<Location> response = new Response<Location>();
		response.setDataList(locations);
		return response;
	}
	
	@Path(value = PathProxy.AdminUrls.GET_DOCTORS)
	@GET
	@ApiOperation(value = PathProxy.AdminUrls.GET_DOCTORS, notes = PathProxy.AdminUrls.GET_DOCTORS)
	public Response<DoctorResponse> getDoctors(@QueryParam(value = "page") int page, @QueryParam(value = "size") int size, @QueryParam(value = "locationId") String locationId,
			@QueryParam(value = "state") String state, @QueryParam(value = "searchTerm") String searchTerm){
		
		List<DoctorResponse> doctorResponses = adminServices.getDoctors(page, size, locationId, state, searchTerm);
		
		Response<DoctorResponse> response = new Response<DoctorResponse>();
		response.setDataList(doctorResponses);
		return response;
	}
	
	@Path(value = PathProxy.AdminUrls.ADD_RESUMES)
	@POST
	@ApiOperation(value = PathProxy.AdminUrls.ADD_RESUMES, notes = PathProxy.AdminUrls.ADD_RESUMES)
	public Response<Resume> addResumes(Resume request){
		
		Resume resume = adminServices.addResumes(request);
		resume.setPath(getFinalImageURL(resume.getPath()));
		Response<Resume> response = new Response<Resume>();
		response.setData(resume);
		return response;
	}
	
	@Path(value = PathProxy.AdminUrls.GET_RESUMES)
	@GET
	@ApiOperation(value = PathProxy.AdminUrls.GET_RESUMES, notes = PathProxy.AdminUrls.GET_RESUMES)
	public Response<Resume> getResumes(@QueryParam(value = "page") int page, @QueryParam(value = "size") int size, @QueryParam(value = "type") String type){
		
		List<Resume> resumes = adminServices.getResumes(page, size, type);
		Response<Resume> response = new Response<Resume>();
		response.setDataList(resumes);
		return response;
	}
	
	@Path(value = PathProxy.AdminUrls.ADD_CONTACT_US)
	@POST
	@ApiOperation(value = PathProxy.AdminUrls.ADD_CONTACT_US, notes = PathProxy.AdminUrls.ADD_CONTACT_US)
	public Response<ContactUs> addContactUs(ContactUs request){
		
		ContactUs contactUs = adminServices.addContactUs(request);
		Response<ContactUs> response = new Response<ContactUs>();
		response.setData(contactUs);
		return response;
	}
	
	@Path(value = PathProxy.AdminUrls.GET_CONTACT_US)
	@GET
	@ApiOperation(value = PathProxy.AdminUrls.GET_CONTACT_US, notes = PathProxy.AdminUrls.GET_CONTACT_US)
	public Response<ContactUs> getContactUs(@QueryParam(value = "page") int page, @QueryParam(value = "size") int size){
		
		List<ContactUs> contactUs = adminServices.getContactUs(page, size);
		Response<ContactUs> response = new Response<ContactUs>();
		response.setDataList(contactUs);
		return response;
	}
	
//	@Path(value = PathProxy.AdminUrls.GET_PATIENT)
//	@GET
//	public Response<Resume> getResumes(@QueryParam(value = "page") int page, @QueryParam(value = "size") int size, @QueryParam(value = "type") String type){
//		
//		List<Resume> resumes = adminServices.getResumes(page, size, type);
//		Response<Resume> response = new Response<Resume>();
//		response.setDataList(resumes);
//		return response;
//	}
	
	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
		    return imagePath + imageURL;
		} else
		    return null;
	    }

	@Path(value = PathProxy.AdminUrls.IMPORT_DRUG)
    @GET
    public Response<Boolean> importDrug() {
	adminServices.importDrug();

	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

	@Path(value = PathProxy.AdminUrls.IMPORT_CITY)
    @GET
    public Response<Boolean> importCity() {
		adminServices.importCity();

	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

	@Path(value = PathProxy.AdminUrls.IMPORT_DIAGNOSTIC_TEST)
    @GET
    public Response<Boolean> importDiagnosticTest() {
	adminServices.importDiagnosticTest();

	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

	@Path(value = PathProxy.AdminUrls.IMPORT_EDUCATION_INSTITUTE)
    @GET
    public Response<Boolean> importEducationInstitute() {
		adminServices.importEducationInstitute();

	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

	@Path(value = PathProxy.AdminUrls.IMPORT_EDUCATION_QUALIFICATION)
    @GET
    public Response<Boolean> importEducationQualification() {
	adminServices.importEducationQualification();

	Response<Boolean> response = new Response<Boolean>();
	response.setData(true);
	return response;
    }

    @Path(value = PathProxy.AdminUrls.GET_UNIQUE_SPECIALITY)
    @GET
    @ApiOperation(value = PathProxy.AdminUrls.GET_UNIQUE_SPECIALITY, notes = PathProxy.AdminUrls.GET_UNIQUE_SPECIALITY)
    public Response<Speciality> getUniqueSpecialities(@QueryParam(value = "searchTerm") String searchTerm,
	    @DefaultValue("0") @QueryParam(value = "updatedTime") String updatedTime, @QueryParam("page") int page, @QueryParam("size") int size) {

	List<Speciality> searchResonse = adminServices.getUniqueSpecialities(searchTerm, updatedTime, page, size);
	Response<Speciality> response = new Response<Speciality>();
	response.setDataList(searchResonse);
	return response;
    }
}
