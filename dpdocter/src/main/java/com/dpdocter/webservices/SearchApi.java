package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.beans.Blog;
import com.dpdocter.beans.ClinicImage;
import com.dpdocter.beans.DoctorClinicProfile;
import com.dpdocter.beans.DoctorProfile;
import com.dpdocter.elasticsearch.response.ESWEBResponse;
import com.dpdocter.elasticsearch.services.ESAppointmentService;
import com.dpdocter.enums.PackageType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.response.BlogResponse;
import com.dpdocter.response.DoctorClinicProfileBySlugUrlResponse;
import com.dpdocter.response.DoctorProfileBySlugUrlResponse;
import com.dpdocter.response.ResourcesCountResponse;
import com.dpdocter.response.SearchDoctorResponse;
import com.dpdocter.services.BlogService;
import com.dpdocter.services.DoctorProfileService;
import com.dpdocter.services.SearchService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Path(PathProxy.WEB_SEARCH_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.WEB_SEARCH_BASE_URL, description = "Endpoint for search")
public class SearchApi {

	private static Logger logger = Logger.getLogger(SearchApi.class.getName());

	@Autowired
	private SearchService searchService;

	@Autowired
	private ESAppointmentService solrAppointmentService;

	@Autowired
	private DoctorProfileService doctorProfileService;

	@Autowired
	private BlogService blogService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Path(value = PathProxy.SearchUrls.SEARCH_DOCTORS)
	@GET
	@ApiOperation(value = PathProxy.SearchUrls.SEARCH_DOCTORS, notes = PathProxy.SearchUrls.SEARCH_DOCTORS)
	public Response<SearchDoctorResponse> getDoctors(@QueryParam("page") int page, @QueryParam("size") int size,
			@QueryParam("city") String city, @QueryParam("location") String location,
			@QueryParam(value = "latitude") String latitude, @QueryParam(value = "longitude") String longitude,
			@QueryParam("speciality") String speciality, @QueryParam("symptom") String symptom,
			@DefaultValue("false") @QueryParam("booking") Boolean booking,
			@DefaultValue("false") @QueryParam("calling") Boolean calling, @QueryParam("minFee") int minFee,
			@QueryParam("maxFee") int maxFee, @QueryParam("minTime") int minTime, @QueryParam("maxTime") int maxTime,
			@MatrixParam("days") List<String> days, @QueryParam("gender") String gender,
			@QueryParam("minExperience") int minExperience, @QueryParam("maxExperience") int maxExperience,
			@QueryParam("service") String service, @QueryParam("locality") String locality,
			@DefaultValue(value = "false") @QueryParam("otherArea") Boolean otherArea,
			@QueryParam("expertIn") String expertIn, @QueryParam("symptomDiseaseCondition") String symptomDiseaseCondition) {
		SearchDoctorResponse doctors = searchService.searchDoctors(page, size, city, location, latitude, longitude,
				speciality, symptom, booking, calling, minFee, maxFee, minTime, maxTime, days, gender, minExperience,
				maxExperience, service, locality, otherArea, expertIn, symptomDiseaseCondition);

		Response<SearchDoctorResponse> response = new Response<SearchDoctorResponse>();
		response.setData(doctors);
		return response;
	}

	@Path(value = PathProxy.SearchUrls.GET_RESOURCES_COUNT_BY_CITY)
	@GET
	@ApiOperation(value = PathProxy.SearchUrls.GET_RESOURCES_COUNT_BY_CITY, notes = PathProxy.SearchUrls.GET_RESOURCES_COUNT_BY_CITY)
	public Response<ResourcesCountResponse> getResourcesCountByCity(@PathParam("city") String city,
			@MatrixParam("type") List<String> type) {

		if (city == null) {
			logger.warn("Invalid Input");
			throw new BusinessException(ServiceError.InvalidInput, "Invalid Input");
		}
		List<ResourcesCountResponse> resourcesCountResponses = searchService.getResourcesCountByCity(city, type);

		Response<ResourcesCountResponse> response = new Response<ResourcesCountResponse>();
		response.setDataList(resourcesCountResponses);
		return response;
	}

	@Path(value = PathProxy.SolrAppointmentUrls.GET_DOCTOR_WEB)
	@GET
	@ApiOperation(value = PathProxy.SolrAppointmentUrls.GET_DOCTOR_WEB, notes = PathProxy.SolrAppointmentUrls.GET_DOCTOR_WEB)
	public Response<ESWEBResponse> getDoctorForWeb(@QueryParam("page") int page, @QueryParam("size") int size,
			@QueryParam("city") String city, @QueryParam("location") String location,
			@QueryParam(value = "latitude") String latitude, @QueryParam(value = "longitude") String longitude,
			@QueryParam("speciality") String speciality, @QueryParam("symptom") String symptom,
			@DefaultValue("false") @QueryParam("booking") Boolean booking,
			@DefaultValue("false") @QueryParam("calling") Boolean calling, @QueryParam("minFee") int minFee,
			@QueryParam("maxFee") int maxFee, @QueryParam("minTime") int minTime, @QueryParam("maxTime") int maxTime,
			@MatrixParam("days") List<String> days, @QueryParam("gender") String gender,
			@QueryParam("minExperience") int minExperience, @QueryParam("maxExperience") int maxExperience,
			@QueryParam("service") String service, @QueryParam("locality") String locality) {

		Response<ESWEBResponse> response = new Response<ESWEBResponse>();
		response.setData(solrAppointmentService.getDoctorForWeb(page, size, city, location, latitude, longitude,
				speciality, symptom, booking, calling, minFee, maxFee, minTime, maxTime, days, gender, minExperience,
				maxExperience, service, locality));
		return response;
	}

	@Path(value = PathProxy.DoctorProfileUrls.GET_DOCTOR_PROFILE)
	@GET
	@ApiOperation(value = PathProxy.DoctorProfileUrls.GET_DOCTOR_PROFILE, notes = PathProxy.DoctorProfileUrls.GET_DOCTOR_PROFILE)
	public Response<DoctorProfile> getDoctorProfile(@PathParam("doctorId") String doctorId,
			@QueryParam("locationId") String locationId, @QueryParam("hospitalId") String hospitalId,
			@DefaultValue(value = "false") @QueryParam(value = "isMobileApp") Boolean isMobileApp,
			@QueryParam(value = "patientId") String patientId,
			@DefaultValue(value = "true") @QueryParam(value = "isSearched") Boolean isSearched) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Doctor Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Cannot Be Empty");
		}
		DoctorProfile doctorProfile = doctorProfileService.getDoctorProfile(doctorId, locationId, hospitalId, patientId,
				isMobileApp, isSearched);
		if (doctorProfile != null) {
			if (doctorProfile.getImageUrl() != null) {
				doctorProfile.setImageUrl(getFinalImageURL(doctorProfile.getImageUrl()));
			}
			if (doctorProfile.getThumbnailUrl() != null) {
				doctorProfile.setThumbnailUrl(getFinalImageURL(doctorProfile.getThumbnailUrl()));
			}
			if (doctorProfile.getCoverImageUrl() != null) {
				doctorProfile.setCoverImageUrl(getFinalImageURL(doctorProfile.getCoverImageUrl()));
			}
			if (doctorProfile.getCoverThumbnailImageUrl() != null) {
				doctorProfile.setCoverThumbnailImageUrl(getFinalImageURL(doctorProfile.getCoverThumbnailImageUrl()));
			}
			if (doctorProfile.getClinicProfile() != null & !doctorProfile.getClinicProfile().isEmpty()) {
				for (DoctorClinicProfile clinicProfile : doctorProfile.getClinicProfile()) {
					if (clinicProfile.getImages() != null) {
						for (ClinicImage clinicImage : clinicProfile.getImages()) {
							if (clinicImage.getImageUrl() != null)
								clinicImage.setImageUrl(getFinalImageURL(clinicImage.getImageUrl()));
							if (clinicImage.getThumbnailUrl() != null)
								clinicImage.setThumbnailUrl(getFinalImageURL(clinicImage.getThumbnailUrl()));
						}
					}
					if (clinicProfile.getLogoUrl() != null) {
						clinicProfile.setLogoUrl(getFinalImageURL(clinicProfile.getLogoUrl()));
					}
					if (clinicProfile.getPackageType() == null) {
						clinicProfile.setPackageType(PackageType.ADVANCE.getType());
					}

					if (clinicProfile.getLogoThumbnailUrl() != null) {
						clinicProfile.setLogoThumbnailUrl(getFinalImageURL(clinicProfile.getLogoThumbnailUrl()));
					}
				}
			}

			if (patientId != null || isSearched == true) {
				doctorProfileService.updateDoctorProfileViews(doctorId);
			}
		}
		Response<DoctorProfile> response = new Response<DoctorProfile>();
		response.setData(doctorProfile);
		return response;
	}

	@Path(value = PathProxy.BlogsUrls.GET_BLOGS)
	@GET
	@ApiOperation(value = PathProxy.BlogsUrls.GET_BLOGS, notes = PathProxy.BlogsUrls.GET_BLOGS)
	public Response<BlogResponse> getBlogs(@QueryParam(value = "size") int size, @QueryParam(value = "page") long page,
			@QueryParam(value = "userId") String userId, @QueryParam(value = "category") String category,
			@QueryParam(value = "title") String title) {
		BlogResponse blogresponse = blogService.getBlogs(size, page, category, userId, title);
		Response<BlogResponse> response = new Response<BlogResponse>();
		response.setData(blogresponse);
		return response;
	}

	@Path(value = PathProxy.BlogsUrls.GET_BLOG_BY_SLUG_URL)
	@GET
	@ApiOperation(value = PathProxy.BlogsUrls.GET_BLOG_BY_SLUG_URL, notes = PathProxy.BlogsUrls.GET_BLOG_BY_SLUG_URL)
	public Response<Blog> getBlogBySlugURL(@PathParam("slugURL") String slugURL, @QueryParam("userId") String userId) {
		Blog blogresponse = blogService.getBlog(null, slugURL, userId);
		if (DPDoctorUtils.anyStringEmpty(slugURL)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");

		}
		Response<Blog> response = new Response<Blog>();
		response.setData(blogresponse);
		return response;
	}

	@Path(value = PathProxy.DoctorProfileUrls.GET_DOCTOR_PROFILE_BY_SLUG_URL)
	@GET
	@ApiOperation(value = PathProxy.DoctorProfileUrls.GET_DOCTOR_PROFILE_BY_SLUG_URL, notes = PathProxy.DoctorProfileUrls.GET_DOCTOR_PROFILE_BY_SLUG_URL)
	public Response<DoctorProfileBySlugUrlResponse> getDoctorProfileBySlugUrl(@PathParam("slugURL") String slugURL,
			@PathParam("userUId") String userUId) {
		if (DPDoctorUtils.anyStringEmpty(userUId)) {
			logger.warn("Doctor Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Cannot Be Empty");
		}
		
		DoctorProfileBySlugUrlResponse doctorProfile = searchService.getDoctorProfileBySlugUrl(userUId, slugURL);
		if (doctorProfile != null) {
			if (doctorProfile.getImageUrl() != null) {
				doctorProfile.setImageUrl(getFinalImageURL(doctorProfile.getImageUrl()));
			}
			if (doctorProfile.getThumbnailUrl() != null) {
				doctorProfile.setThumbnailUrl(getFinalImageURL(doctorProfile.getThumbnailUrl()));
			}
			
			if (doctorProfile.getClinicProfile() != null & !doctorProfile.getClinicProfile().isEmpty()) {
				for (DoctorClinicProfileBySlugUrlResponse clinicProfile : doctorProfile.getClinicProfile()) {
					if (clinicProfile.getImages() != null) {
						for (String clinicImage : clinicProfile.getImages()) {
							if (clinicImage != null)
								clinicImage = getFinalImageURL(clinicImage);
						}
					}
				}
			}

			doctorProfile.setDoctorSlugURL(slugURL);
			doctorProfileService.updateDoctorProfileViews(doctorProfile.getDoctorId());

		}
		Response<DoctorProfileBySlugUrlResponse> response = new Response<DoctorProfileBySlugUrlResponse>();
		response.setData(doctorProfile);
		return response;
	}
	
	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}
}
