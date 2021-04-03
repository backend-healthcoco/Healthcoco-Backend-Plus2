package com.dpdocter.services.v2;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.v2.ClinicalNotes;

public interface ClinicalNotesService {

	List<ClinicalNotes> getClinicalNotes(String patientId, int page, int size, String updatedTime, Boolean discarded);

	List<ClinicalNotes> getClinicalNotes(int page, int size, String doctorId, String locationId, String hospitalId,
			String patientId, String updatedTime, Boolean isOTPVerified,String from,String to, Boolean discarded, Boolean inHistory);

	ClinicalNotes getNotesById(String id, ObjectId visitId);

	List<ClinicalNotes> getClinicalNotesNEWCODE(int page, int size, String doctorId, String locationId,
			String hospitalId, String patientId, String updatedTime, Boolean isOTPVerified, String from, String to,
			Boolean discarded, Boolean inHistory);

	
}
