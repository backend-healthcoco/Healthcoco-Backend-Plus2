package com.dpdocter.services.v2;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.EyePrescription;
import com.dpdocter.beans.v2.Drug;
import com.dpdocter.beans.v2.Prescription;

public interface PrescriptionServices {

	List<Prescription> getPrescriptions(int page, int size, String doctorId, String hospitalId, String locationId,
			String patientId, String updatedTime, boolean isOTPVerified, boolean discarded, boolean inHistory);

	List<Prescription> getPrescriptionsByIds(List<ObjectId> prescriptionIds, ObjectId visitId);

	EyePrescription getEyePrescription(String id);

	Prescription getPrescriptionById(String prescriptionId);

	List<Drug> getCustomGlobalDrugs(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, boolean discarded, String searchTerm);

	Long countCustomGlobalDrugs(String doctorId, String locationId, String hospitalId, String updatedTime,
			boolean discarded, String searchTerm);

	List<Prescription> getPrescriptionsForEMR(int page, int size, String doctorId, String hospitalId, String locationId,
			String patientId, String updatedTime, boolean isOTPVerified,String from,String to, Boolean discarded, boolean inHistory);

	List<Prescription> getPrescriptionsByIdsForEMR(List<ObjectId> prescriptionIds, ObjectId visitId);

	Drug getDrugByDrugCode(String drugCode);
}
