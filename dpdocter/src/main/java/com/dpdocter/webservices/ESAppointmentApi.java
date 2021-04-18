package com.dpdocter.webservices;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Produces;
import org.springframework.http.MediaType;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

@RestController
(PathProxy.SOLR_APPOINTMENT_BASE_URL)
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
@Api(value = PathProxy.SOLR_APPOINTMENT_BASE_URL, description = "Endpoint for solr appointment")
public class ESAppointmentApi {

	@Autowired
	private ESAppointmentService solrAppointmentService;

	@Value(value = "${image.path}")
	private String imagePath;

	
	@GetMapping(value = PathProxy.SolrAppointmentUrls.SEARCH)
	@ApiOperation(value = PathProxy.SolrAppointmentUrls.SEARCH, notes = PathProxy.SolrAppointmentUrls.SEARCH)
	public Response<AppointmentSearchResponse> search(@RequestParam("city") String city,
			@RequestParam("location") String location, @RequestParam(value = "latitude") String latitude,
			@RequestParam(value = "longitude") String longitude, @RequestParam("searchTerm") String searchTerm) {

		List<AppointmentSearchResponse> appointmentSearchResponses = solrAppointmentService.search(city, location,
				latitude, longitude, searchTerm);

		Response<AppointmentSearchResponse> response = new Response<AppointmentSearchResponse>();
		response.setDataList(appointmentSearchResponses);
		return response;
	}

	
	@GetMapping(value = PathProxy.SolrAppointmentUrls.GET_DOCTORS)
	@ApiOperation(value = PathProxy.SolrAppointmentUrls.GET_DOCTORS, notes = PathProxy.SolrAppointmentUrls.GET_DOCTORS)
	public Response<ESDoctorDocument> getDoctors(@RequestParam("page") long page, @RequestParam("size") int size,
			@RequestParam("city") String city, @RequestParam("location") String location,
			@RequestParam(value = "latitude") String latitude, @RequestParam(value = "longitude") String longitude,
			@RequestParam("speciality") String speciality, @RequestParam("symptom") String symptom,
			@DefaultValue("false") @RequestParam("booking") Boolean booking,
			@DefaultValue("false") @RequestParam("calling") Boolean calling, @RequestParam("minFee") int minFee,
			@RequestParam("maxFee") int maxFee, @RequestParam("minTime") int minTime, @RequestParam("maxTime") int maxTime,
			@MatrixParam("days") List<String> days, @RequestParam("gender") String gender,
			@RequestParam("minExperience") int minExperience, @RequestParam("maxExperience") int maxExperience,
			@RequestParam("service") String service) {

		List<ESDoctorDocument> doctors = solrAppointmentService.getDoctors(page, size, city, location, latitude,
				longitude, speciality, symptom, booking, calling, minFee, maxFee, minTime, maxTime, days, gender,
				minExperience, maxExperience, service);

		Response<ESDoctorDocument> response = new Response<ESDoctorDocument>();
		response.setDataList(doctors);
		return response;
	}

	
	@GetMapping(value = PathProxy.SolrAppointmentUrls.GET_PHARMACIES)
	@ApiOperation(value = PathProxy.SolrAppointmentUrls.GET_PHARMACIES, notes = PathProxy.SolrAppointmentUrls.GET_PHARMACIES)
	public Response<ESUserLocaleDocument> getPharmacies(@RequestParam("page") long page, @RequestParam("size") int size,
			@RequestParam("city") String city, @RequestParam("localeName") String localeName,
			@RequestParam(value = "latitude") String latitude, @RequestParam(value = "longitude") String longitude,
			@RequestParam("paymentType") String paymentType, @RequestParam("homeService") Boolean homeService,
			@RequestParam("isTwentyFourSevenOpen") Boolean isTwentyFourSevenOpen, @RequestParam("minTime") long minTime,
			@RequestParam("maxTime") long maxTime, @MatrixParam("days") List<String> days,
			@MatrixParam("types") List<String> pharmacyType,
			@RequestParam("isGenericMedicineAvailable") Boolean isGenericMedicineAvailable) {

		List<ESUserLocaleDocument> pharmacies = solrAppointmentService.getPharmacies(page, size, city, localeName,
				latitude, longitude, paymentType, homeService, isTwentyFourSevenOpen, minTime, maxTime, days,
				pharmacyType, isGenericMedicineAvailable);

		Response<ESUserLocaleDocument> response = new Response<ESUserLocaleDocument>();
		response.setDataList(pharmacies);
		return response;
	}

	
	@GetMapping(value = PathProxy.SolrAppointmentUrls.SEND_SMS_TO_DOCTOR)
	@ApiOperation(value = PathProxy.SolrAppointmentUrls.SEND_SMS_TO_DOCTOR, notes = PathProxy.SolrAppointmentUrls.SEND_SMS_TO_DOCTOR)
	public Response<Boolean> sendSMStoDoctor() {

		Boolean status = solrAppointmentService.sendSMSToDoctors();
		Response<Boolean> response = new Response<Boolean>();
		response.setData(status);
		return response;
	}

	
	@GetMapping(value = PathProxy.SolrAppointmentUrls.GET_LABS)
	@ApiOperation(value = PathProxy.SolrAppointmentUrls.GET_LABS, notes = PathProxy.SolrAppointmentUrls.GET_LABS)
	public Response<LabResponse> getLabs(@RequestParam("page") long page, @RequestParam("size") int size,
			@RequestParam("city") String city, @RequestParam("location") String location,
			@RequestParam(value = "latitude") String latitude, @RequestParam(value = "longitude") String longitude,
			@RequestParam("test") String test, @RequestParam("booking") Boolean booking,
			@RequestParam("calling") Boolean calling, @RequestParam("minTime") int minTime,
			@RequestParam("maxTime") int maxTime, @MatrixParam("days") List<String> days,
			@RequestParam("onlineReports") Boolean onlineReports, @RequestParam("homeService") Boolean homeService,
			@RequestParam("nabl") Boolean nabl) {

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

	// (value = PathProxy.SolrAppointmentUrls.ADD_SPECIALITY)
	// @PostMapping
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

	
	@GetMapping(value = PathProxy.SolrAppointmentUrls.GET_PHARMACIES_WEB)
	@ApiOperation(value = PathProxy.SolrAppointmentUrls.GET_PHARMACIES_WEB, notes = PathProxy.SolrAppointmentUrls.GET_PHARMACIES_WEB)
	public Response<ESWEBResponse> getPharmaciesForWeb(@RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam("city") String city, @RequestParam("localeName") String localeName,
			@RequestParam(value = "latitude") String latitude, @RequestParam(value = "longitude") String longitude,
			@RequestParam("paymentType") String paymentType, @RequestParam("homeService") Boolean homeService,
			@RequestParam("isTwentyFourSevenOpen") Boolean isTwentyFourSevenOpen, @RequestParam("minTime") long minTime,
			@RequestParam("maxTime") long maxTime, @MatrixParam("days") List<String> days,
			@MatrixParam("types") List<String> pharmacyType,
			@RequestParam("isGenericMedicineAvailable") Boolean isGenericMedicineAvailable,
			@RequestParam("locality") String locality) {

		ESWEBResponse pharmacies = solrAppointmentService.getPharmacyForWeb(page, size, city, localeName, latitude,
				longitude, paymentType, homeService, isTwentyFourSevenOpen, minTime, maxTime, days, pharmacyType,
				isGenericMedicineAvailable, locality);
		Response<ESWEBResponse> response = new Response<ESWEBResponse>();
		response.setData(pharmacies);
		return response;
	}

	
	@GetMapping(value = PathProxy.SolrAppointmentUrls.GET_LABS_WEB)
	@ApiOperation(value = PathProxy.SolrAppointmentUrls.GET_LABS_WEB, notes = PathProxy.SolrAppointmentUrls.GET_LABS_WEB)
	public Response<ESWEBResponse> getLabsForWeb(@RequestParam("page") long page, @RequestParam("size") int size,
			@RequestParam("city") String city, @RequestParam("location") String location,
			@RequestParam(value = "latitude") String latitude, @RequestParam(value = "longitude") String longitude,
			@RequestParam("test") String test, @RequestParam("booking") Boolean booking,
			@RequestParam("calling") Boolean calling, @RequestParam("minTime") int minTime,
			@RequestParam("maxTime") int maxTime, @MatrixParam("days") List<String> days,
			@RequestParam("onlineReports") Boolean onlineReports, @RequestParam("homeService") Boolean homeService,
			@RequestParam("nabl") Boolean nabl, @RequestParam("locality") String locality) {

		ESWEBResponse labs = solrAppointmentService.getLabForWeb(page, size, city, location, latitude, longitude, test,
				booking, calling, minTime, maxTime, days, onlineReports, homeService, nabl, locality);

		Response<ESWEBResponse> response = new Response<ESWEBResponse>();
		response.setData(labs);
		return response;
	}
	
	
	@GetMapping(value = PathProxy.SolrAppointmentUrls.GET_LANDMARKS_AND_LOCALITIES)
	@ApiOperation(value = PathProxy.SolrAppointmentUrls.GET_LANDMARKS_AND_LOCALITIES, notes = PathProxy.SolrAppointmentUrls.GET_LANDMARKS_AND_LOCALITIES)
	public Response<SearchLandmarkLocalityResponse> getLandmarksAndLocalitiesByCity(@RequestParam("city") String city, @RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam("searchTerm") String searchTerm) {
		
		List<SearchLandmarkLocalityResponse> searchLandmarkLocalityResponses = solrAppointmentService.getLandmarksAndLocalitiesByCity(city, page, size, searchTerm);

		Response<SearchLandmarkLocalityResponse> response = new Response<SearchLandmarkLocalityResponse>();
		response.setDataList(searchLandmarkLocalityResponses);
		return response;
	}
	

	
	@GetMapping(value = PathProxy.SolrAppointmentUrls.GET_DOCTORS_CARD)
	@ApiOperation(value = PathProxy.SolrAppointmentUrls.GET_DOCTORS_CARD, notes = PathProxy.SolrAppointmentUrls.GET_DOCTORS_CARD)
	public Response<ESDoctorCardResponse> getDoctors(@RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam("city") String city, @RequestParam("location") String location,
			@RequestParam(value = "latitude") String latitude, @RequestParam(value = "longitude") String longitude,
			@RequestParam("speciality") String speciality, @RequestParam("searchTerm") String searchTerm) {

		List<ESDoctorCardResponse> doctors = solrAppointmentService.getDoctorsShortCard(page, size, city, location, latitude, longitude, speciality, searchTerm);

		Response<ESDoctorCardResponse> response = new Response<ESDoctorCardResponse>();
		response.setDataList(doctors);
		return response;
	}
}
