package com.dpdocter.services;

import com.dpdocter.beans.Locale;
import com.dpdocter.enums.RecommendationType;
import com.dpdocter.response.ImageURLResponse;
import com.sun.jersey.multipart.FormDataBodyPart;

public interface LocaleService {

	public Locale getLocaleDetails(String id, String userId);

	public Locale getLocaleDetailsByContactDetails(String contactNumber, String userId);

	Locale addEditRecommedation(String localeId, String patientId, RecommendationType type);

	ImageURLResponse addRXImageMultipart(FormDataBodyPart file);

	public Locale getLocaleDetailBySlugUrl(String slugUrl);
}
