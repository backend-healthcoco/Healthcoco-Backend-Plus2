package com.dpdocter.services.v2;

import java.util.List;

import com.dpdocter.response.v2.AdmitCardResponse;

public interface AdmitCardService {

	List<AdmitCardResponse> getAdmitCards(String doctorId, String locationId, String hospitalId, String patientId,
			int page, int size, long updatedTime, Boolean discarded);

}
