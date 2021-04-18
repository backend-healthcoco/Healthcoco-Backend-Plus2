package com.dpdocter.services;

import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.Locale;
import com.dpdocter.enums.RecommendationType;
import com.dpdocter.response.ImageURLResponse;

public interface LocaleService {

	public Locale getLocaleDetails(String id, String userId);

	public Locale getLocaleDetailsByContactDetails(String contactNumber, String userId);

	Locale addEditRecommedation(String localeId, String patientId, RecommendationType type);

	ImageURLResponse addRXImageMultipart(MultipartFile file);

	public Locale getLocaleDetailBySlugUrl(String slugUrl);
}
