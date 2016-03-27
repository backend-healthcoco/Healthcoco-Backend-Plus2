package com.dpdocter.services;

import java.util.List;

import com.dpdocter.beans.DiagnosticTest;
import com.dpdocter.beans.LabTest;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.Prescription;
import com.dpdocter.collections.DiagnosticTestCollection;
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
import com.dpdocter.response.PrescriptionTestAndRecord;
import com.dpdocter.response.TemplateAddEditResponse;
import com.dpdocter.response.TemplateAddEditResponseDetails;

public interface PrescriptionServices {
    DrugAddEditResponse addDrug(DrugAddEditRequest request);

    DrugAddEditResponse editDrug(DrugAddEditRequest request);

    Boolean deleteDrug(String drugId, String doctorId, String hospitalId, String locationIdString, Boolean discarded);

    Boolean deleteDrug(String drugId, Boolean discarded);

    DrugAddEditResponse getDrugById(String drugId);

    TemplateAddEditResponse addTemplate(TemplateAddEditRequest request);

    TemplateAddEditResponseDetails editTemplate(TemplateAddEditRequest request);

    Boolean deleteTemplate(String templateId, String doctorId, String hospitalId, String locationId, Boolean discarded);

    TemplateAddEditResponseDetails getTemplate(String templateId, String doctorId, String hospitalId, String locationId);

    PrescriptionAddEditResponse addPrescription(PrescriptionAddEditRequest request);

    PrescriptionAddEditResponseDetails editPrescription(PrescriptionAddEditRequest request);

    Boolean deletePrescription(String prescriptionId, String doctorId, String hospitalId, String locationId, String patientId, Boolean discarded);

    List<Prescription> getPrescriptions(int page, int size, String doctorId, String hospitalId, String locationId, String patientId, String updatedTime,
	    boolean isOTPVerified, boolean discarded, boolean inHistory);

    List<Prescription> getPrescriptionsByIds(List<String> prescriptionIds);

    Prescription getPrescriptionById(String prescriptionId);

    List<TemplateAddEditResponseDetails> getTemplates(int page, int size, String doctorId, String hospitalId, String locationId, String updatedTime,
	    boolean discarded);

    Integer getPrescriptionCount(String doctorId, String patientId, String locationId, String hospitalId, boolean isOTPVerified);

    TemplateAddEditResponseDetails addTemplateHandheld(TemplateAddEditRequest request);

    PrescriptionAddEditResponseDetails addPrescriptionHandheld(PrescriptionAddEditRequest request);

    DrugTypeAddEditResponse addDrugType(DrugTypeAddEditRequest request);

    DrugStrengthAddEditResponse addDrugStrength(DrugStrengthAddEditRequest request);

    DrugDosageAddEditResponse addDrugDosage(DrugDosageAddEditRequest request);

    DrugDirectionAddEditResponse addDrugDirection(DrugDirectionAddEditRequest request);

    DrugTypeAddEditResponse editDrugType(DrugTypeAddEditRequest request);

    DrugStrengthAddEditResponse editDrugStrength(DrugStrengthAddEditRequest request);

    DrugDosageAddEditResponse editDrugDosage(DrugDosageAddEditRequest request);

    DrugDirectionAddEditResponse editDrugDirection(DrugDirectionAddEditRequest request);

    Boolean deleteDrugType(String drugTypeId, Boolean discarded);

    Boolean deleteDrugStrength(String drugStrengthId, Boolean discarded);

    Boolean deleteDrugDosage(String drugDosageId, Boolean discarded);

    Boolean deleteDrugDirection(String drugDirectionId, Boolean discarded);

    DrugDurationUnitAddEditResponse addDrugDurationUnit(DrugDurationUnitAddEditRequest request);

    DrugDurationUnitAddEditResponse editDrugDurationUnit(DrugDurationUnitAddEditRequest request);

    Boolean deleteDrugDurationUnit(String drugDurationUnitId, Boolean discarded);

    List<Object> getPrescriptionItems(String type, String range, int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded);

    void emailPrescription(String prescriptionId, String doctorId, String locationId, String hospitalId, String emailAddress);

    MailAttachment getPrescriptionMailData(String prescriptionId, String doctorId, String locationId, String hospitalId);

    void smsPrescription(String prescriptionId, String doctorId, String locationId, String hospitalId, String mobileNumber);

    LabTest addLabTest(LabTest request);

    LabTest editLabTest(LabTest request);

    Boolean deleteLabTest(String labTestId, String hospitalId, String locationId, Boolean discarded);

    Boolean deleteLabTest(String labTestId, Boolean discarded);

    LabTest getLabTestById(String labTestId);

    void importDrug();

    List<DiagnosticTestCollection> getDiagnosticTest();

    List<Prescription> getPrescriptions(String patientId, int page, int size, String updatedTime, Boolean discarded);

    DiagnosticTest addEditDiagnosticTest(DiagnosticTest request);

    DiagnosticTest getDiagnosticTest(String diagnosticTestId);

    Boolean deleteDiagnosticTest(String diagnosticTestId, Boolean discarded);

    Boolean deleteDiagnosticTest(String diagnosticTestId, String hospitalId, String locationId, Boolean discarded);

    PrescriptionTestAndRecord checkPrescriptionExists(String uniqueEmrId, String patientId);

}
