package com.dpdocter.services;

import java.util.List;

import org.bson.types.ObjectId;

import com.dpdocter.beans.DiagnosticTest;
import com.dpdocter.beans.Drug;
import com.dpdocter.beans.GenericCode;
import com.dpdocter.beans.LabTest;
import com.dpdocter.beans.Prescription;
import com.dpdocter.collections.DiagnosticTestCollection;
import com.dpdocter.request.DrugAddEditRequest;
import com.dpdocter.request.DrugDirectionAddEditRequest;
import com.dpdocter.request.DrugDosageAddEditRequest;
import com.dpdocter.request.DrugDurationUnitAddEditRequest;
import com.dpdocter.request.DrugTypeAddEditRequest;
import com.dpdocter.request.PrescriptionAddEditRequest;
import com.dpdocter.request.TemplateAddEditRequest;
import com.dpdocter.response.DrugAddEditResponse;
import com.dpdocter.response.DrugDirectionAddEditResponse;
import com.dpdocter.response.DrugDosageAddEditResponse;
import com.dpdocter.response.DrugDurationUnitAddEditResponse;
import com.dpdocter.response.DrugTypeAddEditResponse;
import com.dpdocter.response.MailResponse;
import com.dpdocter.response.PrescriptionAddEditResponse;
import com.dpdocter.response.PrescriptionAddEditResponseDetails;
import com.dpdocter.response.PrescriptionTestAndRecord;
import com.dpdocter.response.TemplateAddEditResponse;
import com.dpdocter.response.TemplateAddEditResponseDetails;

public interface PrescriptionServices {
    DrugAddEditResponse addDrug(DrugAddEditRequest request);

    DrugAddEditResponse editDrug(DrugAddEditRequest request);

    Drug deleteDrug(String drugId, String doctorId, String hospitalId, String locationIdString, Boolean discarded);

    DrugAddEditResponse getDrugById(String drugId);

    TemplateAddEditResponse addTemplate(TemplateAddEditRequest request);

    TemplateAddEditResponseDetails editTemplate(TemplateAddEditRequest request);

    TemplateAddEditResponseDetails deleteTemplate(String templateId, String doctorId, String hospitalId, String locationId, Boolean discarded);

    TemplateAddEditResponseDetails getTemplate(String templateId, String doctorId, String hospitalId, String locationId);

    PrescriptionAddEditResponse addPrescription(PrescriptionAddEditRequest request);

    PrescriptionAddEditResponseDetails editPrescription(PrescriptionAddEditRequest request);

    Prescription deletePrescription(String prescriptionId, String doctorId, String hospitalId, String locationId, String patientId, Boolean discarded);

    List<Prescription> getPrescriptions(int page, int size, String doctorId, String hospitalId, String locationId, String patientId, String updatedTime,
	    boolean isOTPVerified, boolean discarded, boolean inHistory);

    List<Prescription> getPrescriptionsByIds(List<ObjectId> prescriptionIds);

    Prescription getPrescriptionById(String prescriptionId);

    List<TemplateAddEditResponseDetails> getTemplates(int page, int size, String doctorId, String hospitalId, String locationId, String updatedTime,
	    boolean discarded);

    Integer getPrescriptionCount(String doctorId, String patientId, String locationId, String hospitalId, boolean isOTPVerified);

    TemplateAddEditResponseDetails addTemplateHandheld(TemplateAddEditRequest request);

    PrescriptionAddEditResponseDetails addPrescriptionHandheld(PrescriptionAddEditRequest request);

    DrugTypeAddEditResponse addDrugType(DrugTypeAddEditRequest request);

    DrugDosageAddEditResponse addDrugDosage(DrugDosageAddEditRequest request);

    DrugDirectionAddEditResponse addDrugDirection(DrugDirectionAddEditRequest request);

    DrugTypeAddEditResponse editDrugType(DrugTypeAddEditRequest request);

    DrugDosageAddEditResponse editDrugDosage(DrugDosageAddEditRequest request);

    DrugDirectionAddEditResponse editDrugDirection(DrugDirectionAddEditRequest request);

    DrugTypeAddEditResponse deleteDrugType(String drugTypeId, Boolean discarded);

    DrugDosageAddEditResponse deleteDrugDosage(String drugDosageId, Boolean discarded);

    DrugDirectionAddEditResponse deleteDrugDirection(String drugDirectionId, Boolean discarded);

    DrugDurationUnitAddEditResponse addDrugDurationUnit(DrugDurationUnitAddEditRequest request);

    DrugDurationUnitAddEditResponse editDrugDurationUnit(DrugDurationUnitAddEditRequest request);

    DrugDurationUnitAddEditResponse deleteDrugDurationUnit(String drugDurationUnitId, Boolean discarded);

    List<?> getPrescriptionItems(String type, String range, int page, int size, String doctorId, String locationId, String hospitalId, String updatedTime,
	    Boolean discarded, Boolean isAdmin, String searchTerm);

    void emailPrescription(String prescriptionId, String doctorId, String locationId, String hospitalId, String emailAddress);

    MailResponse getPrescriptionMailData(String prescriptionId, String doctorId, String locationId, String hospitalId);

    Boolean smsPrescription(String prescriptionId, String doctorId, String locationId, String hospitalId, String mobileNumber, String type);

    LabTest addLabTest(LabTest request);

    LabTest editLabTest(LabTest request);

    LabTest deleteLabTest(String labTestId, String hospitalId, String locationId, Boolean discarded);

    LabTest deleteLabTest(String labTestId, Boolean discarded);

    LabTest getLabTestById(String labTestId);

    List<DiagnosticTestCollection> getDiagnosticTest();

    List<Prescription> getPrescriptions(String patientId, int page, int size, String updatedTime, Boolean discarded);

    DiagnosticTest addEditDiagnosticTest(DiagnosticTest request);

    DiagnosticTest getDiagnosticTest(String diagnosticTestId);

    DiagnosticTest deleteDiagnosticTest(String diagnosticTestId, Boolean discarded);

    DiagnosticTest deleteDiagnosticTest(String diagnosticTestId, String hospitalId, String locationId, Boolean discarded);

    PrescriptionTestAndRecord checkPrescriptionExists(String uniqueEmrId, String patientId);

	Boolean addRemoveGenericCode(String action, String genericCode, String drugCode);

	GenericCode addEditGenericCode(GenericCode request);

	String getPrescriptionFile(String prescriptionId);

	Boolean makeDrugFavourite(String drugId, String doctorId, String locationId, String hospitalId);

}
