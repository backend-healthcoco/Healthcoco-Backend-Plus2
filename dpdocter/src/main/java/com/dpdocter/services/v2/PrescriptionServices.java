package com.dpdocter.services.v2;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.EyePrescription;
/*import com.dpdocter.beans.v2.Drug;
import com.dpdocter.beans.v2.Prescription;*/

public interface PrescriptionServices {
	/*
	 * Drug addDrug(DrugAddEditRequest request);
	 * 
	 * Drug editDrug(DrugAddEditRequest request);
	 * 
	 * Drug deleteDrug(String drugId, String doctorId, String hospitalId, String
	 * locationIdString, Boolean discarded);
	 * 
	 * Drug getDrugById(String drugId);
	 * 
	 * TemplateAddEditResponse addTemplate(TemplateAddEditRequest request);
	 * 
	 * TemplateAddEditResponseDetails editTemplate(TemplateAddEditRequest request);
	 * 
	 * TemplateAddEditResponseDetails deleteTemplate(String templateId, String
	 * doctorId, String hospitalId, String locationId, Boolean discarded);
	 * 
	 * TemplateAddEditResponseDetails getTemplate(String templateId, String
	 * doctorId, String hospitalId, String locationId);
	 * 
	 * PrescriptionAddEditResponse addPrescription(PrescriptionAddEditRequest
	 * request, Boolean isAppointmentAdd, String createdBy, Appointment
	 * appointment);
	 * 
	 * PrescriptionAddEditResponseDetails
	 * editPrescription(PrescriptionAddEditRequest request);
	 * 
	 * Prescription deletePrescription(String prescriptionId, String doctorId,
	 * String hospitalId, String locationId, String patientId, Boolean discarded);
	 */

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
			String patientId, String updatedTime, boolean isOTPVerified, boolean discarded, boolean inHistory);

	List<Prescription> getPrescriptionsByIdsForEMR(List<ObjectId> prescriptionIds, ObjectId visitId);

	Drug getDrugByDrugCode(String drugCode);

	/*
	 * List<Prescription> getPrescriptionsByIds(List<ObjectId> prescriptionIds,
	 * ObjectId visitId);
	 * 
	 * Prescription getPrescriptionById(String prescriptionId);
	 * 
	 * List<TemplateAddEditResponseDetails> getTemplates(int page, int size, String
	 * doctorId, String hospitalId, String locationId, String updatedTime, boolean
	 * discarded);
	 * 
	 * Integer getPrescriptionCount(ObjectId doctorObjectId, ObjectId
	 * patientObjectId, ObjectId locationObjectId, ObjectId hospitalObjectId,
	 * boolean isOTPVerified);
	 * 
	 * TemplateAddEditResponseDetails addTemplateHandheld(TemplateAddEditRequest
	 * request);
	 * 
	 * PrescriptionAddEditResponseDetails
	 * addPrescriptionHandheld(PrescriptionAddEditRequest request);
	 * 
	 * DrugTypeAddEditResponse addDrugType(DrugTypeAddEditRequest request);
	 * 
	 * DrugDosageAddEditResponse addDrugDosage(DrugDosageAddEditRequest request);
	 * 
	 * DrugDirectionAddEditResponse addDrugDirection(DrugDirectionAddEditRequest
	 * request);
	 * 
	 * DrugTypeAddEditResponse editDrugType(DrugTypeAddEditRequest request);
	 * 
	 * DrugDosageAddEditResponse editDrugDosage(DrugDosageAddEditRequest request);
	 * 
	 * DrugDirectionAddEditResponse editDrugDirection(DrugDirectionAddEditRequest
	 * request);
	 * 
	 * DrugTypeAddEditResponse deleteDrugType(String drugTypeId, Boolean discarded);
	 * 
	 * DrugDosageAddEditResponse deleteDrugDosage(String drugDosageId, Boolean
	 * discarded);
	 * 
	 * DrugDirectionAddEditResponse deleteDrugDirection(String drugDirectionId,
	 * Boolean discarded);
	 * 
	 * DrugDurationUnitAddEditResponse
	 * addDrugDurationUnit(DrugDurationUnitAddEditRequest request);
	 * 
	 * DrugDurationUnitAddEditResponse
	 * editDrugDurationUnit(DrugDurationUnitAddEditRequest request);
	 * 
	 * DrugDurationUnitAddEditResponse deleteDrugDurationUnit(String
	 * drugDurationUnitId, Boolean discarded);
	 * 
	 * public List<?> getPrescriptionItems(String type, String range, int page, int
	 * size, String doctorId, String locationId, String hospitalId, String
	 * updatedTime, Boolean discarded, Boolean isAdmin, String disease, String
	 * searchTerm);
	 * 
	 * void emailPrescription(String prescriptionId, String doctorId, String
	 * locationId, String hospitalId, String emailAddress);
	 * 
	 * MailResponse getPrescriptionMailData(String prescriptionId, String doctorId,
	 * String locationId, String hospitalId);
	 * 
	 * Boolean smsPrescription(String prescriptionId, String doctorId, String
	 * locationId, String hospitalId, String mobileNumber, String type);
	 * 
	 * LabTest addLabTest(LabTest request);
	 * 
	 * LabTest editLabTest(LabTest request);
	 * 
	 * LabTest deleteLabTest(String labTestId, String hospitalId, String locationId,
	 * Boolean discarded);
	 * 
	 * LabTest deleteLabTest(String labTestId, Boolean discarded);
	 * 
	 * LabTest getLabTestById(String labTestId);
	 * 
	 * List<DiagnosticTestCollection> getDiagnosticTest();
	 * 
	 * Response<Object> getPrescriptions(String patientId, int page, int size,
	 * String updatedTime, Boolean discarded);
	 * 
	 * DiagnosticTest addEditDiagnosticTest(DiagnosticTest request);
	 * 
	 * DiagnosticTest getDiagnosticTest(String diagnosticTestId);
	 * 
	 * DiagnosticTest deleteDiagnosticTest(String diagnosticTestId, Boolean
	 * discarded);
	 * 
	 * DiagnosticTest deleteDiagnosticTest(String diagnosticTestId, String
	 * hospitalId, String locationId, Boolean discarded);
	 * 
	 * PrescriptionTestAndRecord checkPrescriptionExists(String uniqueEmrId, String
	 * patientId, String locationId, String hospitalId);
	 * 
	 * String getPrescriptionFile(String prescriptionId, Boolean showPH, Boolean
	 * showPLH, Boolean showFH, Boolean showDA, Boolean isLabPrint);
	 * 
	 * Drug makeDrugFavourite(String drugId, String doctorId, String locationId,
	 * String hospitalId);
	 * 
	 * public Advice addAdvice(Advice request);
	 * 
	 * public Advice deleteAdvice(String adviceId, String doctorId, String
	 * locationId, String hospitalId, Boolean discarded);
	 * 
	 * // Boolean makeCustomDrugFavourite();
	 * 
	 * 
	 * 
	 * Drug addFavouriteDrug(DrugAddEditRequest request, DrugCollection
	 * originalDrug, String createdBy);
	 * 
	 * //Boolean addGenericNameInDrugs();
	 * 
	 * List<DrugInteractionResposne> drugInteraction(List<Drug> request, String
	 * patientId);
	 * 
	 * Boolean addGenericsWithReaction();
	 * 
	 * Boolean addFavouritesToDrug();
	 * 
	 * List<GenericCodesAndReaction> getGenericCodeWithReactionForAdmin(int page,
	 * int size, String updatedTime,Boolean discarded, String searchTerm);
	 * 
	 * Boolean addGenericCodeWithReaction(GenericCodesAndReaction request);
	 * 
	 * Boolean deleteGenericCodeWithReaction(GenericCodesAndReaction request);
	 * 
	 * Boolean uploadGenericCodeWithReaction(FormDataBodyPart file);
	 * 
	 * EyePrescription addEditEyePrescription(EyePrescription request, Boolean
	 * isAppointmentAdd);
	 * 
	 * EyePrescription editEyePrescription(EyePrescription request);
	 * 
	 * EyePrescription getEyePrescription(String id);
	 * 
	 * List<EyePrescription> getEyePrescriptions(int page, int size, String
	 * doctorId, String locationId, String hospitalId, String patientId, String
	 * updatedTime, Boolean discarded, Boolean isOTPVerified);
	 * 
	 * String downloadEyePrescription(String prescriptionId);
	 * 
	 * void emailEyePrescription(String prescriptionId, String doctorId, String
	 * locationId, String hospitalId, String emailAddress);
	 * 
	 * int getEyePrescriptionCount(ObjectId doctorObjectId, ObjectId
	 * patientObjectId, ObjectId locationObjectId, ObjectId hospitalObjectId,
	 * boolean isOTPVerified);
	 * 
	 * void updateEyePrescriptionVisitId(String eyePrescriptionId, String visitId);
	 * 
	 * EyePrescription deleteEyePrescription(String prescriptionId, String doctorId,
	 * String hospitalId, String locationId, String patientId, Boolean discarded);
	 * 
	 * Boolean smsEyePrescription(String prescriptionId, String doctorId, String
	 * locationId, String hospitalId, String mobileNumber, String type);
	 * 
	 * List<Drug> getAllCustomDrug();
	 * 
	 * Instructions addEditInstructions(Instructions instruction);
	 * 
	 * List<Instructions> getInstructions(int page, int size, String doctorId,
	 * String locationId, String hospitalId, String updatedTime, Boolean discarded);
	 * 
	 * Instructions deleteInstructions(String id, String doctorId, String
	 * locationId, String hospitalId, Boolean discarded);
	 * 
	 * 
	 * Boolean updateGenericCodes();
	 * 
	 * List<Drug> getDrugSubstitutes(String drugId);
	 * 
	 * Boolean smsPrescriptionforWeb(String prescriptionId, String doctorId, String
	 * locationId, String hospitalId, String mobileNumber, String type);
	 * 
	 * Boolean smsEyePrescriptionForWeb(String prescriptionId, String doctorId,
	 * String locationId, String hospitalId, String mobileNumber, String type);
	 * 
	 * void emailEyePrescriptionForWeb(String prescriptionId, String doctorId,
	 * String locationId, String hospitalId, String emailAddress);
	 * 
	 * Prescription deletePrescriptionForWeb(String prescriptionId, String doctorId,
	 * String hospitalId, String locationId, String patientId, Boolean discarded);
	 * 
	 * Boolean updateDrugRankingOnBasisOfRanking();
	 * 
	 * Boolean uploadDrugs();
	 * 
	 * Boolean updateDrugInteraction();
	 * 
	 * NutritionReferral addNutritionReferral(NutritionReferralRequest request);
	 */
}
