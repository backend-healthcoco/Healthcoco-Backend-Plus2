package com.dpdocter.elasticsearch.services;

import java.util.List;

import com.dpdocter.elasticsearch.beans.AppointmentSearchResponse;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESUserLocaleDocument;
import com.dpdocter.elasticsearch.response.ESDoctorCardResponse;
import com.dpdocter.elasticsearch.response.ESWEBResponse;
import com.dpdocter.elasticsearch.response.LabResponse;
import com.dpdocter.response.SearchLandmarkLocalityResponse;

public interface ESAppointmentService {

	List<ESDoctorDocument> getDoctors(long page, int size, String city, String location, String latitude,
			String longitude, String speciality, String symptom, Boolean booking, Boolean calling, int minFee,
			int maxFee, int minTime, int maxTime, List<String> days, String gender, int minExperience,
			int maxExperience, String service);

	List<AppointmentSearchResponse> search(String city, String location, String latitude, String longitude,
			String searchTerm);

	List<LabResponse> getLabs(long page, int size, String city, String location, String latitude, String longitude,
			String test, Boolean booking, Boolean calling, int minTime, int maxTime, List<String> days,
			Boolean onlineReports, Boolean homeService, Boolean nabl);

	List<ESUserLocaleDocument> getPharmacies(long page, int size, String city, String location, String latitude,
			String longitude, String paymentType, Boolean homeService, Boolean isTwentyFourSevenOpen, long minTime,
			long maxTime, List<String> days, List<String> pharmacyType, Boolean isGenericMedicineAvailable);

	Boolean sendSMSToDoctors();

	ESWEBResponse getDoctorForWeb(int page, int size, String city, String location, String latitude, String longitude,
			String speciality, String symptom, Boolean booking, Boolean calling, int minFee, int maxFee, int minTime,
			int maxTime, List<String> days, String gender, int minExperience, int maxExperience, String service,
			String locality);

	public ESWEBResponse getPharmacyForWeb(int page, int size, String city, String localeName, String latitude,
			String longitude, String paymentType, Boolean homeService, Boolean isTwentyFourSevenOpen, long minTime,
			long maxTime, List<String> days, List<String> pharmacyType, Boolean isGenericMedicineAvailable,
			String locality);

	ESWEBResponse getLabForWeb(long page, int size, String city, String location, String latitude, String longitude,
			String test, Boolean booking, Boolean calling, int minTime, int maxTime, List<String> days,
			Boolean onlineReports, Boolean homeService, Boolean nabl, String locality);

	List<ESDoctorCardResponse> getDoctorsShortCard(int page, int size, String city, String location, String latitude,
			String longitude, String speciality, String searchTerm);

	public Integer getDoctorCount(String city, String location, String latitude, String longitude, String speciality,
			String symptom, Boolean booking, Boolean calling, int minFee, int maxFee, int minTime, int maxTime,
			List<String> days, String gender, int minExperience, int maxExperience, String service);

	List<SearchLandmarkLocalityResponse> getLandmarksAndLocalitiesByCity(String city, int page, int size,
			String searchTerm);

}
