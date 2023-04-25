package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dpdocter.elasticsearch.beans.AppointmentSearchResponse;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESUserLocaleDocument;
import com.dpdocter.elasticsearch.response.ESDoctorCardResponse;
import com.dpdocter.elasticsearch.response.ESWEBResponse;
import com.dpdocter.elasticsearch.response.LabResponse;
import com.dpdocter.elasticsearch.services.ESAppointmentService;
import com.dpdocter.response.SearchLandmarkLocalityResponse;

import common.util.web.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Component
@Path(PathProxy.SOLR_APPOINTMENT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = PathProxy.SOLR_APPOINTMENT_BASE_URL, description = "Endpoint for solr appointment")
public class ESAppointmentApi {

	@Autowired
	private ESAppointmentService solrAppointmentService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Path(value = PathProxy.SolrAppointmentUrls.SEARCH)
	@GET
	@ApiOperation(value = PathProxy.SolrAppointmentUrls.SEARCH, notes = PathProxy.SolrAppointmentUrls.SEARCH)
	public Response<AppointmentSearchResponse> search(@QueryParam("city") String city,
			@QueryParam("location") String location, @QueryParam(value = "latitude") String latitude,
			@QueryParam(value = "longitude") String longitude, @QueryParam("searchTerm") String searchTerm) {

		List<AppointmentSearchResponse> appointmentSearchResponses = solrAppointmentService.search(city, location,
				latitude, longitude, searchTerm);

		Response<AppointmentSearchResponse> response = new Response<AppointmentSearchResponse>();
		response.setDataList(appointmentSearchResponses);
		return response;
	}

	@Path(value = PathProxy.SolrAppointmentUrls.GET_DOCTORS)
	@GET
	@ApiOperation(value = PathProxy.SolrAppointmentUrls.GET_DOCTORS, notes = PathProxy.SolrAppointmentUrls.GET_DOCTORS)
	public Response<ESDoctorDocument> getDoctors(@QueryParam("page") long page, @QueryParam("size") int size,
			@QueryParam("city") String city, @QueryParam("location") String location,
			@QueryParam(value = "latitude") String latitude, @QueryParam(value = "longitude") String longitude,
			@QueryParam("speciality") String speciality, @QueryParam("symptom") String symptom,
			@DefaultValue("false") @QueryParam("booking") Boolean booking,
			@DefaultValue("false") @QueryParam("calling") Boolean calling, @QueryParam("minFee") int minFee,
			@QueryParam("maxFee") int maxFee, @QueryParam("minTime") int minTime, @QueryParam("maxTime") int maxTime,
			@MatrixParam("days") List<String> days, @QueryParam("gender") String gender,
			@QueryParam("minExperience") int minExperience, @QueryParam("maxExperience") int maxExperience,
			@QueryParam("service") String service) {

		List<ESDoctorDocument> doctors = solrAppointmentService.getDoctors(page, size, city, location, latitude,
				longitude, speciality, symptom, booking, calling, minFee, maxFee, minTime, maxTime, days, gender,
				minExperience, maxExperience, service);

		Response<ESDoctorDocument> response = new Response<ESDoctorDocument>();
		response.setDataList(doctors);
		return response;
	}

	@Path(value = PathProxy.SolrAppointmentUrls.GET_PHARMACIES)
	@GET
	@ApiOperation(value = PathProxy.SolrAppointmentUrls.GET_PHARMACIES, notes = PathProxy.SolrAppointmentUrls.GET_PHARMACIES)
	public Response<ESUserLocaleDocument> getPharmacies(@QueryParam("page") long page, @QueryParam("size") int size,
			@QueryParam("city") String city, @QueryParam("localeName") String localeName,
			@QueryParam(value = "latitude") String latitude, @QueryParam(value = "longitude") String longitude,
			@QueryParam("paymentType") String paymentType, @QueryParam("homeService") Boolean homeService,
			@QueryParam("isTwentyFourSevenOpen") Boolean isTwentyFourSevenOpen, @QueryParam("minTime") long minTime,
			@QueryParam("maxTime") long maxTime, @MatrixParam("days") List<String> days,
			@MatrixParam("types") List<String> pharmacyType,
			@QueryParam("isGenericMedicineAvailable") Boolean isGenericMedicineAvailable) {

		List<ESUserLocaleDocument> pharmacies = solrAppointmentService.getPharmacies(page, size, city, localeName,
				latitude, longitude, paymentType, homeService, isTwentyFourSevenOpen, minTime, maxTime, days,
				pharmacyType, isGenericMedicineAvailable);

		Response<ESUserLocaleDocument> response = new Response<ESUserLocaleDocument>();
		response.setDataList(pharmacies);
		return response;
	}

	@Path(value = PathProxy.SolrAppointmentUrls.SEND_SMS_TO_DOCTOR)
	@GET
	@ApiOperation(value = PathProxy.SolrAppointmentUrls.SEND_SMS_TO_DOCTOR, notes = PathProxy.SolrAppointmentUrls.SEND_SMS_TO_DOCTOR)
	public Response<Boolean> sendSMStoDoctor() {

		Boolean status = solrAppointmentService.sendSMSToDoctors();
		Response<Boolean> response = new Response<Boolean>();
		response.setData(status);
		return response;
	}

	@Path(value = PathProxy.SolrAppointmentUrls.GET_LABS)
	@GET
	@ApiOperation(value = PathProxy.SolrAppointmentUrls.GET_LABS, notes = PathProxy.SolrAppointmentUrls.GET_LABS)
	public Response<LabResponse> getLabs(@QueryParam("page") long page, @QueryParam("size") int size,
			@QueryParam("city") String city, @QueryParam("location") String location,
			@QueryParam(value = "latitude") String latitude, @QueryParam(value = "longitude") String longitude,
			@QueryParam("test") String test, @QueryParam("booking") Boolean booking,
			@QueryParam("calling") Boolean calling, @QueryParam("minTime") int minTime,
			@QueryParam("maxTime") int maxTime, @MatrixParam("days") List<String> days,
			@QueryParam("onlineReports") Boolean onlineReports, @QueryParam("homeService") Boolean homeService,
			@QueryParam("nabl") Boolean nabl) {

		List<LabResponse> doctors = solrAppointmentService.getLabs(page, size, city, location, latitude, longitude,
				test, booking, calling, minTime, maxTime, days, onlineReports, homeService, nabl);

		// if (doctors != null && !doctors.isEmpty()) {
		// for (LabResponse doctorDocument : doctors) {
		// if (doctorDocument.getImages() != null &&
		// !doctorDocument.getImages().isEmpty()) {
		// List<String> images = new ArrayList<String>();
		// for (String clinicImage : doctorDocument.getImages()) {
		// images.add(clinicImage);
		// }
		// doctorDocument.setImages(images);
		// }
		// if (doctorDocument.getLogoUrl() != null)
		// doctorDocument.setLogoUrl(getFinalImageURL(doctorDocument.getLogoUrl()));
		// }
		// }
		Response<LabResponse> response = new Response<LabResponse>();
		response.setDataList(doctors);
		return response;
	}

	// @Path(value = PathProxy.SolrAppointmentUrls.ADD_SPECIALITY)
	// @POST
	// @ApiOperation(value = PathProxy.SolrAppointmentUrls.ADD_SPECIALITY, notes
	// = PathProxy.SolrAppointmentUrls.ADD_SPECIALITY)
	// public Response<Boolean> addSpeciality(List<SolrSpecialityDocument>
	// request) {
	// if (request == null || request.isEmpty()) {
	// throw new BusinessException(ServiceError.InvalidInput, "Specialities
	// Cannot Be Empty");
	// }
	//
	// boolean addSpecializationResponse =
	// solrAppointmentService.addSpeciality(request);
	//
	// Response<Boolean> response = new Response<Boolean>();
	// response.setData(addSpecializationResponse);
	// return response;
	// }

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

	@Path(value = PathProxy.SolrAppointmentUrls.GET_PHARMACIES_WEB)
	@GET
	@ApiOperation(value = PathProxy.SolrAppointmentUrls.GET_PHARMACIES_WEB, notes = PathProxy.SolrAppointmentUrls.GET_PHARMACIES_WEB)
	public Response<ESWEBResponse> getPharmaciesForWeb(@QueryParam("page") int page, @QueryParam("size") int size,
			@QueryParam("city") String city, @QueryParam("localeName") String localeName,
			@QueryParam(value = "latitude") String latitude, @QueryParam(value = "longitude") String longitude,
			@QueryParam("paymentType") String paymentType, @QueryParam("homeService") Boolean homeService,
			@QueryParam("isTwentyFourSevenOpen") Boolean isTwentyFourSevenOpen, @QueryParam("minTime") long minTime,
			@QueryParam("maxTime") long maxTime, @MatrixParam("days") List<String> days,
			@MatrixParam("types") List<String> pharmacyType,
			@QueryParam("isGenericMedicineAvailable") Boolean isGenericMedicineAvailable,
			@QueryParam("locality") String locality) {

		ESWEBResponse pharmacies = solrAppointmentService.getPharmacyForWeb(page, size, city, localeName, latitude,
				longitude, paymentType, homeService, isTwentyFourSevenOpen, minTime, maxTime, days, pharmacyType,
				isGenericMedicineAvailable, locality);
		Response<ESWEBResponse> response = new Response<ESWEBResponse>();
		response.setData(pharmacies);
		return response;
	}

	@Path(value = PathProxy.SolrAppointmentUrls.GET_LABS_WEB)
	@GET
	@ApiOperation(value = PathProxy.SolrAppointmentUrls.GET_LABS_WEB, notes = PathProxy.SolrAppointmentUrls.GET_LABS_WEB)
	public Response<ESWEBResponse> getLabsForWeb(@QueryParam("page") long page, @QueryParam("size") int size,
			@QueryParam("city") String city, @QueryParam("location") String location,
			@QueryParam(value = "latitude") String latitude, @QueryParam(value = "longitude") String longitude,
			@QueryParam("test") String test, @QueryParam("booking") Boolean booking,
			@QueryParam("calling") Boolean calling, @QueryParam("minTime") int minTime,
			@QueryParam("maxTime") int maxTime, @MatrixParam("days") List<String> days,
			@QueryParam("onlineReports") Boolean onlineReports, @QueryParam("homeService") Boolean homeService,
			@QueryParam("nabl") Boolean nabl, @QueryParam("locality") String locality) {

		ESWEBResponse labs = solrAppointmentService.getLabForWeb(page, size, city, location, latitude, longitude, test,
				booking, calling, minTime, maxTime, days, onlineReports, homeService, nabl, locality);

		Response<ESWEBResponse> response = new Response<ESWEBResponse>();
		response.setData(labs);
		return response;
	}

	@Path(value = PathProxy.SolrAppointmentUrls.GET_LANDMARKS_AND_LOCALITIES)
	@GET
	@ApiOperation(value = PathProxy.SolrAppointmentUrls.GET_LANDMARKS_AND_LOCALITIES, notes = PathProxy.SolrAppointmentUrls.GET_LANDMARKS_AND_LOCALITIES)
	public Response<SearchLandmarkLocalityResponse> getLandmarksAndLocalitiesByCity(@QueryParam("city") String city,
			@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam("searchTerm") String searchTerm) {

		List<SearchLandmarkLocalityResponse> searchLandmarkLocalityResponses = solrAppointmentService
				.getLandmarksAndLocalitiesByCity(city, page, size, searchTerm);

		Response<SearchLandmarkLocalityResponse> response = new Response<SearchLandmarkLocalityResponse>();
		response.setDataList(searchLandmarkLocalityResponses);
		return response;
	}

	@Path(value = PathProxy.SolrAppointmentUrls.GET_DOCTORS_CARD)
	@GET
	@ApiOperation(value = PathProxy.SolrAppointmentUrls.GET_DOCTORS_CARD, notes = PathProxy.SolrAppointmentUrls.GET_DOCTORS_CARD)
	public Response<ESDoctorCardResponse> getDoctors(@QueryParam("page") int page, @QueryParam("size") int size,
			@QueryParam("city") String city, @QueryParam("location") String location,
			@QueryParam(value = "latitude") String latitude, @QueryParam(value = "longitude") String longitude,
			@QueryParam("speciality") String speciality, @QueryParam("searchTerm") String searchTerm) {

		List<ESDoctorCardResponse> doctors = solrAppointmentService.getDoctorsShortCard(page, size, city, location,
				latitude, longitude, speciality, searchTerm);

		Response<ESDoctorCardResponse> response = new Response<ESDoctorCardResponse>();
		response.setDataList(doctors);
		return response;
	}
}
