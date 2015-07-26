package com.dpdocter.services;

import java.util.List;

import org.springframework.data.mongodb.repository.Query;

import com.dpdocter.beans.DrugDirection;
import com.dpdocter.beans.DrugDosage;
import com.dpdocter.beans.DrugDurationUnit;
import com.dpdocter.beans.DrugStrengthUnit;
import com.dpdocter.beans.DrugType;
import com.dpdocter.beans.Prescription;
import com.dpdocter.request.DrugAddEditRequest;
import com.dpdocter.request.DrugDirectionAddEditRequest;
import com.dpdocter.request.DrugDosageAddEditRequest;
import com.dpdocter.request.DrugDurationUnitAddEditRequest;
import com.dpdocter.request.DrugStrengthAddEditRequest;
import com.dpdocter.request.DrugTypeAddEditRequest;
import com.dpdocter.request.PrescriptionAddEditRequest;
import com.dpdocter.request.TemplateAddEditRequest;
import com.dpdocter.response.DrugAddEditResponse;
import com.dpdocter.response.DrugDirectionAddEditResponse;
import com.dpdocter.response.DrugDosageAddEditResponse;
import com.dpdocter.response.DrugDurationUnitAddEditResponse;
import com.dpdocter.response.DrugStrengthAddEditResponse;
import com.dpdocter.response.DrugTypeAddEditResponse;
import com.dpdocter.response.PrescriptionAddEditResponse;
import com.dpdocter.response.PrescriptionAddEditResponseDetails;
import com.dpdocter.response.TemplateAddEditResponse;
import com.dpdocter.response.TemplateAddEditResponseDetails;

public interface PrescriptionServices {
	DrugAddEditResponse addDrug(DrugAddEditRequest request);

	DrugAddEditResponse editDrug(DrugAddEditRequest request);

	Boolean deleteDrug(String drugId, String doctorId, String hospitalId, String locationIdString);

	Boolean deleteDrug(String drugId);

	DrugAddEditResponse getDrugById(String drugId);

	TemplateAddEditResponse addTemplate(TemplateAddEditRequest request);

	TemplateAddEditResponse editTemplate(TemplateAddEditRequest request);

	Boolean deleteTemplate(String templateId, String doctorId, String hospitalId, String locationId);

	TemplateAddEditResponseDetails getTemplate(String templateId, String doctorId, String hospitalId, String locationId);

	PrescriptionAddEditResponse addPrescription(PrescriptionAddEditRequest request);

	PrescriptionAddEditResponse editPrescription(PrescriptionAddEditRequest request);

	Boolean deletePrescription(String prescriptionId, String doctorId, String hospitalId, String locationId, String patientId);

	List<Prescription> getPrescriptions(String doctorId, String hospitalId, String locationId, String patientId, String createdTime, boolean isOTPVarified);

	List<Prescription> getPrescriptionsByIds(List<String> prescriptionIds);

	List<TemplateAddEditResponseDetails> getTemplates(String doctorId, String hospitalId, String locationId, String createdTime);

	List<DrugAddEditResponse> getDrugs(String doctorId, String hospitalId, String locationId, String createdTime);

	@Query(value = "{'doctorId': ?0, 'patientId': ?1, 'locationId': ?2, 'hospitalId': ?3}", count = true)
	Integer getPrescriptionCount(String doctorId, String patientId, String locationId, String hospitalId);

	TemplateAddEditResponseDetails addTemplateHandheld(TemplateAddEditRequest request);

	PrescriptionAddEditResponseDetails addPrescriptionHandheld(PrescriptionAddEditRequest request);

	List<DrugType> getAllDrugType();

	List<DrugType> getCustomDrugType(String doctorId, String locationId, String hospitalId);

	List<DrugStrengthUnit> getAllDrugStrengthUnit();
	
	List<DrugStrengthUnit> getCustomDrugStrengthUnit(String doctorId, String locationId, String hospitalId);

	List<DrugDosage> getAllDrugDosage();

	List<DrugDosage> getCustomDrugDosage(String doctorId, String locationId, String hospitalId);

	List<DrugDurationUnit> getAllDrugDurationUnit();

	List<DrugDurationUnit> getCustomDrugDurationUnit(String doctorId, String locationId, String hospitalId);
	
	List<DrugDirection> getAllDrugDirection();

	List<DrugDirection> getCustomDrugDirection(String doctorId, String locationId, String hospitalId);
	
	DrugTypeAddEditResponse addDrugType(DrugTypeAddEditRequest request);

	DrugStrengthAddEditResponse addDrugStrength(DrugStrengthAddEditRequest request);

	DrugDosageAddEditResponse addDrugDosage(DrugDosageAddEditRequest request);

	DrugDirectionAddEditResponse addDrugDirection(DrugDirectionAddEditRequest request);

	DrugTypeAddEditResponse editDrugType(DrugTypeAddEditRequest request);

	DrugStrengthAddEditResponse editDrugStrength(DrugStrengthAddEditRequest request);

	DrugDosageAddEditResponse editDrugDosage(DrugDosageAddEditRequest request);

	DrugDirectionAddEditResponse editDrugDirection(DrugDirectionAddEditRequest request);

	Boolean deleteDrugType(String drugTypeId);

	Boolean deleteDrugStrength(String drugStrengthId);

	Boolean deleteDrugDosage(String drugDosageId);

	Boolean deleteDrugDirection(String drugDirectionId);

	DrugDurationUnitAddEditResponse addDrugDurationUnit(DrugDurationUnitAddEditRequest request);

	DrugDurationUnitAddEditResponse editDrugDurationUnit(DrugDurationUnitAddEditRequest request);

	Boolean deleteDrugDurationUnit(String drugDurationUnitId);
	
}
