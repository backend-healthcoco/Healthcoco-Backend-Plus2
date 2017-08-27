package com.dpdocter.services;

import java.util.List;

import com.dpdocter.request.AdmitCardRequest;
import com.dpdocter.response.AdmitCardResponse;
import com.sun.jersey.multipart.FormDataBodyPart;

public interface AdmitCardService {

	AdmitCardResponse getAdmitCard(String cardId);

	AdmitCardResponse addEditAdmitcard(FormDataBodyPart file, AdmitCardRequest request);

	List<AdmitCardResponse> getAdmitCards(String doctorId, String locationId, String hospitalId, String patientId,
			int page, int size, long updatedTime);

	AdmitCardResponse deleteAdmitCard(String cardId, String doctorId, String hospitalId, String locationId,
			Boolean discarded);
	

}
