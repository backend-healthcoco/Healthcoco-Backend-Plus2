package com.dpdocter.services;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.request.AdmitCardRequest;
import com.dpdocter.response.AdmitCardResponse;

public interface AdmitCardService {

	AdmitCardResponse getAdmitCard(String cardId);

	List<AdmitCardResponse> getAdmitCards(String doctorId, String locationId, String hospitalId, String patientId,
			long page, int size, long updatedTime, Boolean discarded);

	Boolean deleteAdmitCard(String cardId, String doctorId, String hospitalId, String locationId,
			Boolean discarded);

	AdmitCardResponse addEditAdmitcard(AdmitCardRequest request);

	int getAdmitCardCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, boolean isOTPVerified);

	String downloadDischargeSummary(String admitCardId);

	void emailAdmitCard(String admitcardId, String doctorId, String locationId, String hospitalId,
			String emailAddress);

	void emailAdmitCardForWeb(String admitcardId, String doctorId, String locationId, String hospitalId,
			String emailAddress);

}
