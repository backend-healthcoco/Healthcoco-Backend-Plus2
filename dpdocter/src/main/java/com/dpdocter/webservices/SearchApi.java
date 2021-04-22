package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@RequestMapping(value=PathProxy.WEB_SEARCH_BASE_URL,consumes =MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.WEB_SEARCH_BASE_URL, description = "Endpoint for search")
public class SearchApi {

	private static Logger logger = LogManager.getLogger(SearchApi.class.getName());

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

	
	@GetMapping(value = PathProxy.SearchUrls.SEARCH_DOCTORS)
	@ApiOperation(value = PathProxy.SearchUrls.SEARCH_DOCTORS, notes = PathProxy.SearchUrls.SEARCH_DOCTORS)
	public Response<SearchDoctorResponse> getDoctors(@RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam("city") String city, @RequestParam("location") String location,
			@RequestParam(value = "latitude") String latitude, @RequestParam(value = "longitude") String longitude,
			@RequestParam("speciality") String speciality, @RequestParam("symptom") String symptom,
			@DefaultValue("false") @RequestParam("booking") Boolean booking,
			@DefaultValue("false") @RequestParam("calling") Boolean calling, @RequestParam("minFee") int minFee,
			@RequestParam("maxFee") int maxFee, @RequestParam("minTime") int minTime, @RequestParam("maxTime") int maxTime,
			@MatrixParam("days") List<String> days, @RequestParam("gender") String gender,
			@RequestParam("minExperience") int minExperience, @RequestParam("maxExperience") int maxExperience,
			@RequestParam("service") String service, @RequestParam("locality") String locality,
			@DefaultValue(value = "false") @RequestParam("otherArea") Boolean otherArea,
			@RequestParam("expertIn") String expertIn, @RequestParam("symptomDiseaseCondition") String symptomDiseaseCondition) {
		SearchDoctorResponse doctors = searchService.searchDoctors(page, size, city, location, latitude, longitude,
				speciality, symptom, booking, calling, minFee, maxFee, minTime, maxTime, days, gender, minExperience,
				maxExperience, service, locality, otherArea, expertIn, symptomDiseaseCondition);

		Response<SearchDoctorResponse> response = new Response<SearchDoctorResponse>();
		response.setData(doctors);
		return response;
	}

	
	@GetMapping(value = PathProxy.SearchUrls.GET_RESOURCES_COUNT_BY_CITY)
	@ApiOperation(value = PathProxy.SearchUrls.GET_RESOURCES_COUNT_BY_CITY, notes = PathProxy.SearchUrls.GET_RESOURCES_COUNT_BY_CITY)
	public Response<ResourcesCountResponse> getResourcesCountByCity(@PathVariable("city") String city,
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

	
	@GetMapping(value = PathProxy.SolrAppointmentUrls.GET_DOCTOR_WEB)
	@ApiOperation(value = PathProxy.SolrAppointmentUrls.GET_DOCTOR_WEB, notes = PathProxy.SolrAppointmentUrls.GET_DOCTOR_WEB)
	public Response<ESWEBResponse> getDoctorForWeb(@RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam("city") String city, @RequestParam("location") String location,
			@RequestParam(value = "latitude") String latitude, @RequestParam(value = "longitude") String longitude,
			@RequestParam("speciality") String speciality, @RequestParam("symptom") String symptom,
			@DefaultValue("false") @RequestParam("booking") Boolean booking,
			@DefaultValue("false") @RequestParam("calling") Boolean calling, @RequestParam("minFee") int minFee,
			@RequestParam("maxFee") int maxFee, @RequestParam("minTime") int minTime, @RequestParam("maxTime") int maxTime,
			@MatrixParam("days") List<String> days, @RequestParam("gender") String gender,
			@RequestParam("minExperience") int minExperience, @RequestParam("maxExperience") int maxExperience,
			@RequestParam("service") String service, @RequestParam("locality") String locality) {

		Response<ESWEBResponse> response = new Response<ESWEBResponse>();
		response.setData(solrAppointmentService.getDoctorForWeb(page, size, city, location, latitude, longitude,
				speciality, symptom, booking, calling, minFee, maxFee, minTime, maxTime, days, gender, minExperience,
				maxExperience, service, locality));
		return response;
	}

	
	@GetMapping(value = PathProxy.DoctorProfileUrls.GET_DOCTOR_PROFILE)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.GET_DOCTOR_PROFILE, notes = PathProxy.DoctorProfileUrls.GET_DOCTOR_PROFILE)
	public Response<DoctorProfile> getDoctorProfile(@PathVariable("doctorId") String doctorId,
			@RequestParam("locationId") String locationId, @RequestParam("hospitalId") String hospitalId,
			@DefaultValue(value = "false") @RequestParam(value = "isMobileApp") Boolean isMobileApp,
			@RequestParam(value = "patientId") String patientId,
			@DefaultValue(value = "true") @RequestParam(value = "isSearched") Boolean isSearched,
			@RequestParam(value = "userState") String userState) {
		if (DPDoctorUtils.anyStringEmpty(doctorId)) {
			logger.warn("Doctor Id Cannot Be Empty");
			throw new BusinessException(ServiceError.InvalidInput, "Doctor Id Cannot Be Empty");
		}
		DoctorProfile doctorProfile = doctorProfileService.getDoctorProfile(doctorId, locationId, hospitalId, patientId,
				isMobileApp, isSearched, userState);
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

	
	@GetMapping(value = PathProxy.BlogsUrls.GET_BLOGS)
	@ApiOperation(value = PathProxy.BlogsUrls.GET_BLOGS, notes = PathProxy.BlogsUrls.GET_BLOGS)
	public Response<BlogResponse> getBlogs(@RequestParam(value = "size") int size, @RequestParam(value = "page") long page,
			@RequestParam(value = "userId") String userId, @RequestParam(value = "category") String category,
			@RequestParam(value = "title") String title) {
		BlogResponse blogresponse = blogService.getBlogs(size, page, category, userId, title);
		Response<BlogResponse> response = new Response<BlogResponse>();
		response.setData(blogresponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.BlogsUrls.GET_BLOG_BY_SLUG_URL)
	@ApiOperation(value = PathProxy.BlogsUrls.GET_BLOG_BY_SLUG_URL, notes = PathProxy.BlogsUrls.GET_BLOG_BY_SLUG_URL)
	public Response<Blog> getBlogBySlugURL(@PathVariable("slugURL") String slugURL, @RequestParam("userId") String userId) {
		Blog blogresponse = blogService.getBlog(null, slugURL, userId);
		if (DPDoctorUtils.anyStringEmpty(slugURL)) {
			throw new BusinessException(ServiceError.InvalidInput, "Invalid input");

		}
		Response<Blog> response = new Response<Blog>();
		response.setData(blogresponse);
		return response;
	}

	
	@GetMapping(value = PathProxy.DoctorProfileUrls.GET_DOCTOR_PROFILE_BY_SLUG_URL)
	@ApiOperation(value = PathProxy.DoctorProfileUrls.GET_DOCTOR_PROFILE_BY_SLUG_URL, notes = PathProxy.DoctorProfileUrls.GET_DOCTOR_PROFILE_BY_SLUG_URL)
	public Response<DoctorProfileBySlugUrlResponse> getDoctorProfileBySlugUrl(@PathVariable("slugURL") String slugURL,
			@PathVariable("userUId") String userUId) {
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
