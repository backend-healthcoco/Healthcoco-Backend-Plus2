package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.EyeObservation;

public interface OphthalmologyService {

	public EyeObservation addEditEyeObservation(EyeObservation eyeObservation);
	
	//public EyeObservation deleteEyeObservation(String id);
	
	public EyeObservation getEyeObservation(int page, int size, String doctorId, String locationId,
			String hospitalId, String patientId, String updatedTime, Boolean isOTPVerified, Boolean discarded,
			Boolean inHistory);
	
	/*public List<EyeObservation> getEyeObservations(int page, int size, String doctorId, String locationId,
			String hospitalId, String patientId, String updatedTime, Boolean isOTPVerified, Boolean discarded,
			Boolean inHistory);
*/
	EyeObservation deleteEyeObservation(String id, Boolean discarded);

	//List<EyeObservation> getEyeObservations(int page, int size, String doctorId, String locationId, String hospitalId,
	//		String patientId, String updatedTime, Boolean discarded);

	List<EyeObservation> getEyeObservations(int page, int size, String doctorId, String locationId, String hospitalId,
			String patientId, String updatedTime, Boolean discarded, Boolean isOTPVerified);
}
