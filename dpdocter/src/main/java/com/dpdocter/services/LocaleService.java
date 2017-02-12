package com.dpdocter.services;

import com.dpdocter.beans.Locale;
import com.dpdocter.enums.RecommendationType;

public interface LocaleService {

	public Locale getLocaleDetails(String id);

	public Locale getLocaleDetailsByContactDetails(String contactNumber);

	Locale addEditRecommedation(String localeId, String patientId, RecommendationType type);
}
