package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.Resume;
import com.dpdocter.beans.User;
import com.dpdocter.services.AdminServices;

import common.util.web.Response;

@Component
@Path(PathProxy.ADMIN_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AdminAPI {

	private static Logger logger = Logger.getLogger(AdminAPI.class.getName());
	
	@Autowired
	AdminServices adminServices;
	
    @Value(value = "${image.path}")
    private String imagePath;

	@Path(value = PathProxy.AdminUrls.GET_INACTIVE_USERS)
	@GET
	public Response<User> getInactiveUsers(@QueryParam(value = "page") int page, @QueryParam(value = "size") int size){
		
		List<User> users = adminServices.getInactiveUsers(page, size);
		Response<User> response = new Response<User>();
		response.setDataList(users);
		return response;
	}
	
	@Path(value = PathProxy.AdminUrls.GET_HOSPITALS)
	@GET
	public Response<Hospital> getHospitals(@QueryParam(value = "page") int page, @QueryParam(value = "size") int size){
		
		List<Hospital> hospitals = adminServices.getHospitals(page, size);
		Response<Hospital> response = new Response<Hospital>();
		response.setDataList(hospitals);
		return response;
	}
	
	@Path(value = PathProxy.AdminUrls.GET_CLINICS)
	@GET
	public Response<Location> getClinics(@QueryParam(value = "page") int page, @QueryParam(value = "size") int size, @QueryParam(value = "hospitalId") String hospitalId){
		
		List<Location> locations = adminServices.getClinics(page, size, hospitalId);
		if(locations != null && !locations.isEmpty()){
			for(Location location : locations){
				if (location.getImages() != null && !location.getImages().isEmpty()) {
					for (ClinicImage clinicImage : location.getImages()) {
					    if (clinicImage.getImageUrl() != null) {
						clinicImage.setImageUrl(getFinalImageURL(clinicImage.getImageUrl()));
					    }
					    if (clinicImage.getThumbnailUrl() != null) {
						clinicImage.setThumbnailUrl(getFinalImageURL(clinicImage.getThumbnailUrl()));
					    }
					}
				    }
				    if (location.getLogoUrl() != null)
				    	location.setLogoUrl(getFinalImageURL(location.getLogoUrl()));
				    if (location.getLogoThumbnailUrl() != null)
				    	location.setLogoThumbnailUrl(getFinalImageURL(location.getLogoThumbnailUrl()));
			}
		}
		Response<Location> response = new Response<Location>();
		response.setDataList(locations);
		return response;
	}
	
	@Path(value = PathProxy.AdminUrls.ADD_RESUMES)
	@POST
	public Response<Resume> addResumes(Resume request){
		
		Resume resume = adminServices.addResumes(request);
		resume.setPath(getFinalImageURL(resume.getPath()));
		Response<Resume> response = new Response<Resume>();
		response.setData(resume);
		return response;
	}
	
	@Path(value = PathProxy.AdminUrls.GET_RESUMES)
	@GET
	public Response<Resume> getResumes(@QueryParam(value = "page") int page, @QueryParam(value = "size") int size, @QueryParam(value = "type") String type){
		
		List<Resume> resumes = adminServices.getResumes(page, size, type);
		if(resumes != null && !resumes.isEmpty()){
			for(Resume resume : resumes){
				resume.setPath(getFinalImageURL(resume.getPath()));
			}
		}
		Response<Resume> response = new Response<Resume>();
		response.setDataList(resumes);
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

}
