package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.ClinicalNotesDynamicField;
import com.dpdocter.beans.DataDynamicField;
import com.dpdocter.beans.DataDynamicUI;
import com.dpdocter.beans.DentalLabDynamicField;
import com.dpdocter.beans.DentalLabDynamicUi;
import com.dpdocter.beans.DischargeSummaryDynamicFields;
import com.dpdocter.beans.DynamicUI;
import com.dpdocter.beans.KioskDynamicUi;
import com.dpdocter.beans.NutritionUI;
import com.dpdocter.beans.NutritionUIPermission;
import com.dpdocter.beans.PrescriptionDynamicField;
import com.dpdocter.beans.TreatmentDynamicFields;
import com.dpdocter.beans.UIPermissions;
import com.dpdocter.collections.DataDynamicUICollection;
import com.dpdocter.collections.DentalLabDynamicUICollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.DynamicUICollection;
import com.dpdocter.collections.KioskDynamicUiCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.NutritionUICollection;
import com.dpdocter.collections.SpecialityCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.AccessPermissionType;
import com.dpdocter.enums.AdmitCardPermissionEnum;
import com.dpdocter.enums.CardioPermissionEnum;
import com.dpdocter.enums.ClinicalNotesPermissionEnum;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.DentalLabRequestPermissions;
import com.dpdocter.enums.DentistPermissionEnum;
import com.dpdocter.enums.DischargeSummaryPermissions;
import com.dpdocter.enums.ENTPermissionType;
import com.dpdocter.enums.GynacPermissionsEnum;
import com.dpdocter.enums.InitialAssessmentCardPermissionEnum;
import com.dpdocter.enums.KioskDynamicUiEnum;
import com.dpdocter.enums.NurssingAdmissionCardPermissionEnum;
import com.dpdocter.enums.NutritionUIPermissionEnum;
import com.dpdocter.enums.OpthoPermissionEnums;
import com.dpdocter.enums.OrthoPermissionType;
import com.dpdocter.enums.PatientCertificatePermissions;
import com.dpdocter.enums.PatientVisitPermissionEnum;
import com.dpdocter.enums.PreOperationCardPermissionEnum;
import com.dpdocter.enums.PrescriptionPermissionEnum;
import com.dpdocter.enums.ProfilePermissionEnum;
import com.dpdocter.enums.SpecialityTypeEnum;
import com.dpdocter.enums.TabPermissionsEnum;
import com.dpdocter.enums.VitalSignPermissions;
import com.dpdocter.enums.WorkSamplePermissions;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DataDynamicUIRepository;
import com.dpdocter.repository.DentalLabDynamicUIRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.DynamicUIRepository;
import com.dpdocter.repository.KioskDynamicUiRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.NutritionUIRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DynamicUIRequest;
import com.dpdocter.request.KioskDynamicUiResquest;
import com.dpdocter.request.NutrirtionUIRequest;
import com.dpdocter.response.DynamicUIResponse;
import com.dpdocter.services.DentalLabService;
import com.dpdocter.services.DynamicUIService;
import com.dpdocter.services.PushNotificationServices;

import common.util.web.DPDoctorUtils;

@Service
public class DynamicUIServiceImpl implements DynamicUIService {

	@Autowired
	DynamicUIRepository dynamicUIRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	DoctorRepository doctorRepository;

	@Autowired
	SpecialityRepository specialityRepository;

	@Autowired
	DataDynamicUIRepository dataDynamicUIRepository;

	@Autowired
	DentalLabDynamicUIRepository dentalLabDynamicUIRepository;

	@Autowired
	DentalLabService dentalLabService;

	@Autowired
	LocationRepository locationRepository;

	@Autowired
	private KioskDynamicUiRepository kioskDynamicUiRepository;

	@Autowired
	private PushNotificationServices pushNotificationServices;

	@Autowired
	private NutritionUIRepository nutritionUIRepository;

	@Override
	@Transactional
	public UIPermissions getAllPermissionForDoctor(String doctorId) {
		UIPermissions uiPermissions = null;
		Set<String> clinicalNotesPermissionsSet = new HashSet<String>();
		Set<String> patientVisitPermissionsSet = new HashSet<String>();
		Set<String> prescriptionPermissionsSet = new HashSet<String>();
		Set<String> profilePermissionsSet = new HashSet<String>();
		Set<String> tabPermissionsSet = new HashSet<String>();
		Set<String> vitalSignPermissionSet = new HashSet<String>();
		Set<String> dischargeSummaryPermissionSet = new HashSet<String>();
		Set<String> admitCardPermissionSet = new HashSet<String>();
		Set<String> patientCertificatePermissionSet = new HashSet<String>();

		Set<String> nursingAdmissionFormPermissions = new HashSet<String>();
		Set<String> preOperationAssessmentFormPerimissions = new HashSet<String>();
		Set<String> initialAssessmentFormPermissions = new HashSet<String>();

		DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
		if (doctorCollection != null) {
			uiPermissions = new UIPermissions();
			UIPermissions tempPermissions = null;
			String speciality = null;
			if (doctorCollection.getSpecialities() == null || doctorCollection.getSpecialities().isEmpty()) {
				uiPermissions = getAllPermissionBySpeciality(String.valueOf("EMPTY"));
			} else {
				for (ObjectId specialityId : doctorCollection.getSpecialities()) {

					SpecialityCollection specialityCollection = specialityRepository.findById(specialityId)
							.orElse(null);
					if (specialityCollection != null) {
						speciality = specialityCollection.getSpeciality();
					}
					tempPermissions = getAllPermissionBySpeciality(String.valueOf(speciality));
					if (tempPermissions != null) {
						patientVisitPermissionsSet.addAll(tempPermissions.getPatientVisitPermissions());
						clinicalNotesPermissionsSet.addAll(tempPermissions.getClinicalNotesPermissions());
						prescriptionPermissionsSet.addAll(tempPermissions.getPrescriptionPermissions());
						profilePermissionsSet.addAll(tempPermissions.getProfilePermissions());
						tabPermissionsSet.addAll(tempPermissions.getTabPermissions());
						vitalSignPermissionSet.addAll(tempPermissions.getVitalSignPermissions());
						dischargeSummaryPermissionSet.addAll(tempPermissions.getDischargeSummaryPermissions());
						admitCardPermissionSet.addAll(tempPermissions.getAdmitCardPermissions());
						patientCertificatePermissionSet.addAll(tempPermissions.getPatientCertificatePermissions());

						nursingAdmissionFormPermissions.addAll(tempPermissions.getNursingAdmissionFormPermissions());
						initialAssessmentFormPermissions.addAll(tempPermissions.getInitialAssessmentFormPermissions());
						preOperationAssessmentFormPerimissions
								.addAll(tempPermissions.getPreOperationAssessmentFormPerimissions());
					}
				}
				uiPermissions.setPatientVisitPermissions(new ArrayList<String>(patientVisitPermissionsSet));
				uiPermissions.setClinicalNotesPermissions(new ArrayList<String>(clinicalNotesPermissionsSet));
				uiPermissions.setPrescriptionPermissions(new ArrayList<String>(prescriptionPermissionsSet));
				uiPermissions.setProfilePermissions(new ArrayList<String>(profilePermissionsSet));
				uiPermissions.setTabPermissions(new ArrayList<String>(tabPermissionsSet));
				uiPermissions.setVitalSignPermissions(new ArrayList<String>(vitalSignPermissionSet));
				uiPermissions.setDischargeSummaryPermissions(new ArrayList<String>(dischargeSummaryPermissionSet));
				uiPermissions.setAdmitCardPermissions(new ArrayList<String>(admitCardPermissionSet));
				uiPermissions.setPatientCertificatePermissions(new ArrayList<String>(patientCertificatePermissionSet));
				uiPermissions
						.setNursingAdmissionFormPermissions(new ArrayList<String>(nursingAdmissionFormPermissions));
				uiPermissions.setPreOperationAssessmentFormPerimissions(
						new ArrayList<String>(preOperationAssessmentFormPerimissions));
				uiPermissions
						.setInitialAssessmentFormPermissions(new ArrayList<String>(initialAssessmentFormPermissions));
			}
		}
		return uiPermissions;
	}

	@Override
	@Transactional
	public DynamicUI getPermissionForDoctor(String doctorId) {
		DynamicUI dynamicUI = null;
		DynamicUICollection dynamicUICollection = dynamicUIRepository.findByDoctorId(new ObjectId(doctorId));
		if (dynamicUICollection != null) {
			dynamicUI = new DynamicUI();
			BeanUtil.map(dynamicUICollection, dynamicUI);
		} else if (dynamicUICollection == null || dynamicUICollection.getUiPermissions() == null) {
			dynamicUI = new DynamicUI();
			dynamicUI.setUiPermissions(getDefaultPermissions());
			dynamicUI.setDoctorId(doctorId);
		}
		return dynamicUI;
	}

	@Override
	@Transactional
	public DynamicUI postPermissions(DynamicUIRequest dynamicUIRequest) {
		DynamicUI dynamicUI = null;
		DynamicUICollection dynamicUICollection = dynamicUIRepository
				.findByDoctorId(new ObjectId(dynamicUIRequest.getDoctorId()));
		if (dynamicUICollection != null) {
			dynamicUICollection.setUiPermissions(dynamicUIRequest.getUiPermissions());
			dynamicUICollection = dynamicUIRepository.save(dynamicUICollection);
			dynamicUI = new DynamicUI();
			BeanUtil.map(dynamicUICollection, dynamicUI);
		} else {
			dynamicUICollection = new DynamicUICollection();
			BeanUtil.map(dynamicUIRequest, dynamicUICollection);
			dynamicUICollection = dynamicUIRepository.save(dynamicUICollection);
			dynamicUI = new DynamicUI();
			BeanUtil.map(dynamicUICollection, dynamicUI);
		}
		return dynamicUI;
	}

	private UIPermissions getAllPermissionBySpeciality(String speciality) {
		UIPermissions uiPermissions = null;
		ArrayList<String> clinicalNotesPermission = null;
		ArrayList<String> patientVisitPermission = null;
		ArrayList<String> prescriptionPermission = null;
		ArrayList<String> profilePermission = null;
		ArrayList<String> tabPermission = null;
		ArrayList<String> vitalSignPermission = null;
		ArrayList<String> dischargeSummaryPermission = null;
		ArrayList<String> admitCardPermission = null;
		ArrayList<String> dentalLabRequestPermission = null;
		ArrayList<String> dentalWorkSamplePermission = null;
		ArrayList<String> patientCertificatePermissions = null;

		ArrayList<String> nursingAdmissionFormPermissions = null;
		ArrayList<String> preOperationAssessmentFormPerimissions = null;
		ArrayList<String> initialAssessmentFormPermissions = null;

		switch (speciality.toUpperCase().trim()) {
		case "OPHTHALMOLOGIST":
			uiPermissions = new UIPermissions();
			clinicalNotesPermission = new ArrayList<String>(Arrays.asList(clinicalNotesPermission()));
			clinicalNotesPermission.add(OpthoPermissionEnums.OPTHO_CLINICAL_NOTES.getPermissions());
			prescriptionPermission = new ArrayList<String>(Arrays.asList(prescriptionPermission()));
			prescriptionPermission.add(OpthoPermissionEnums.OPTHO_RX.getPermissions());
			profilePermission = new ArrayList<String>(Arrays.asList(historyPermission()));
			tabPermission = new ArrayList<String>(Arrays.asList(tabPermission()));
			tabPermission.add(OpthoPermissionEnums.LENS_PRESCRIPTION.getPermissions());
			patientVisitPermission = new ArrayList<String>(Arrays.asList(patientVisitPermission()));
			vitalSignPermission = new ArrayList<String>(Arrays.asList(vitalSignPermission()));
			dischargeSummaryPermission = new ArrayList<String>(Arrays.asList(dischargeSummaryPermission()));
			admitCardPermission = new ArrayList<String>(Arrays.asList(admitcardPermission()));
			patientCertificatePermissions = new ArrayList<String>(Arrays.asList(patientCertificatePermission()));
			nursingAdmissionFormPermissions = new ArrayList<String>(Arrays.asList(nurssingAdmissionCardPermission()));
			initialAssessmentFormPermissions = new ArrayList<String>(Arrays.asList(initialAssessmentCardPermission()));
			preOperationAssessmentFormPerimissions = new ArrayList<String>(Arrays.asList(preOperationCardPermission()));

			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			uiPermissions.setTabPermissions(tabPermission);
			uiPermissions.setPatientVisitPermissions(patientVisitPermission);
			uiPermissions.setVitalSignPermissions(vitalSignPermission);
			uiPermissions.setDischargeSummaryPermissions(dischargeSummaryPermission);
			uiPermissions.setAdmitCardPermissions(admitCardPermission);
			uiPermissions.setPatientCertificatePermissions(patientCertificatePermissions);
			uiPermissions.setPreOperationAssessmentFormPerimissions(preOperationAssessmentFormPerimissions);
			uiPermissions.setNursingAdmissionFormPermissions(nursingAdmissionFormPermissions);
			uiPermissions.setInitialAssessmentFormPermissions(initialAssessmentFormPermissions);

			break;
		case "PEDIATRICIAN":
			uiPermissions = new UIPermissions();
			clinicalNotesPermission = new ArrayList<String>(Arrays.asList(clinicalNotesPermission()));
			prescriptionPermission = new ArrayList<String>(Arrays.asList(prescriptionPermission()));
			profilePermission = new ArrayList<String>(Arrays.asList(historyPermission()));
			profilePermission.add(GynacPermissionsEnum.BIRTH_HISTORY.getPermissions());
			tabPermission = new ArrayList<String>(Arrays.asList(tabPermission()));
			patientVisitPermission = new ArrayList<String>(Arrays.asList(patientVisitPermission()));
			vitalSignPermission = new ArrayList<String>(Arrays.asList(vitalSignPermission()));
			dischargeSummaryPermission = new ArrayList<String>(Arrays.asList(dischargeSummaryPermission()));
			admitCardPermission = new ArrayList<String>(Arrays.asList(admitcardPermission()));
			patientCertificatePermissions = new ArrayList<String>(Arrays.asList(patientCertificatePermission()));
			nursingAdmissionFormPermissions = new ArrayList<String>(Arrays.asList(nurssingAdmissionCardPermission()));
			initialAssessmentFormPermissions = new ArrayList<String>(Arrays.asList(initialAssessmentCardPermission()));
			preOperationAssessmentFormPerimissions = new ArrayList<String>(Arrays.asList(preOperationCardPermission()));

			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			uiPermissions.setTabPermissions(tabPermission);
			uiPermissions.setPatientVisitPermissions(patientVisitPermission);
			uiPermissions.setVitalSignPermissions(vitalSignPermission);
			uiPermissions.setDischargeSummaryPermissions(dischargeSummaryPermission);
			uiPermissions.setAdmitCardPermissions(admitCardPermission);
			uiPermissions.setPatientCertificatePermissions(patientCertificatePermissions);
			uiPermissions.setPreOperationAssessmentFormPerimissions(preOperationAssessmentFormPerimissions);
			uiPermissions.setNursingAdmissionFormPermissions(nursingAdmissionFormPermissions);
			uiPermissions.setInitialAssessmentFormPermissions(initialAssessmentFormPermissions);

			break;
		case "GYNAECOLOGIST/OBSTETRICIAN":
			uiPermissions = new UIPermissions();
			clinicalNotesPermission = new ArrayList<String>(Arrays.asList(clinicalNotesPermission()));
			clinicalNotesPermission.add(GynacPermissionsEnum.PA.getPermissions());
			clinicalNotesPermission.add(GynacPermissionsEnum.PV.getPermissions());
			clinicalNotesPermission.add(GynacPermissionsEnum.PS.getPermissions());
			clinicalNotesPermission.add(GynacPermissionsEnum.INDICATION_OF_USG.getPermissions());
			clinicalNotesPermission.add(GynacPermissionsEnum.LMP.getPermissions());
			clinicalNotesPermission.add(GynacPermissionsEnum.EDD.getPermissions());
			prescriptionPermission = new ArrayList<String>(Arrays.asList(prescriptionPermission()));
			profilePermission = new ArrayList<String>(Arrays.asList(historyPermission()));
			profilePermission.add(GynacPermissionsEnum.BIRTH_HISTORY.getPermissions());
			tabPermission = new ArrayList<String>(Arrays.asList(tabPermission()));
			vitalSignPermission = new ArrayList<String>(Arrays.asList(vitalSignPermission()));
			patientVisitPermission = new ArrayList<String>(Arrays.asList(patientVisitPermission()));
			dischargeSummaryPermission = new ArrayList<String>(Arrays.asList(dischargeSummaryPermission()));
			admitCardPermission = new ArrayList<String>(Arrays.asList(admitcardPermission()));
			patientCertificatePermissions = new ArrayList<String>(Arrays.asList(patientCertificatePermission()));
			nursingAdmissionFormPermissions = new ArrayList<String>(Arrays.asList(nurssingAdmissionCardPermission()));
			initialAssessmentFormPermissions = new ArrayList<String>(Arrays.asList(initialAssessmentCardPermission()));
			preOperationAssessmentFormPerimissions = new ArrayList<String>(Arrays.asList(preOperationCardPermission()));

			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			uiPermissions.setTabPermissions(tabPermission);
			uiPermissions.setPatientVisitPermissions(patientVisitPermission);
			uiPermissions.setVitalSignPermissions(vitalSignPermission);
			uiPermissions.setDischargeSummaryPermissions(dischargeSummaryPermission);
			uiPermissions.setAdmitCardPermissions(admitCardPermission);
			uiPermissions.setPatientCertificatePermissions(patientCertificatePermissions);
			uiPermissions.setPreOperationAssessmentFormPerimissions(preOperationAssessmentFormPerimissions);
			uiPermissions.setInitialAssessmentFormPermissions(initialAssessmentFormPermissions);
			uiPermissions.setNursingAdmissionFormPermissions(nursingAdmissionFormPermissions);

			break;
		case "CARDIOLOGIST":
			uiPermissions = new UIPermissions();
			clinicalNotesPermission = new ArrayList<String>(Arrays.asList(clinicalNotesPermission()));
			clinicalNotesPermission.add(CardioPermissionEnum.ECG.getPermissions());
			clinicalNotesPermission.add(CardioPermissionEnum.ECHO.getPermissions());
			clinicalNotesPermission.add(CardioPermissionEnum.XRAY.getPermissions());
			clinicalNotesPermission.add(CardioPermissionEnum.HOLTER.getPermissions());
			prescriptionPermission = new ArrayList<String>(Arrays.asList(prescriptionPermission()));
			profilePermission = new ArrayList<String>(Arrays.asList(historyPermission()));
			tabPermission = new ArrayList<String>(Arrays.asList(tabPermission()));
			vitalSignPermission = new ArrayList<String>(Arrays.asList(vitalSignPermission()));
			patientVisitPermission = new ArrayList<String>(Arrays.asList(patientVisitPermission()));
			dischargeSummaryPermission = new ArrayList<String>(Arrays.asList(dischargeSummaryPermission()));
			admitCardPermission = new ArrayList<String>(Arrays.asList(admitcardPermission()));
			patientCertificatePermissions = new ArrayList<String>(Arrays.asList(patientCertificatePermission()));
			nursingAdmissionFormPermissions = new ArrayList<String>(Arrays.asList(nurssingAdmissionCardPermission()));
			initialAssessmentFormPermissions = new ArrayList<String>(Arrays.asList(initialAssessmentCardPermission()));
			preOperationAssessmentFormPerimissions = new ArrayList<String>(Arrays.asList(preOperationCardPermission()));

			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			uiPermissions.setTabPermissions(tabPermission);
			uiPermissions.setPatientVisitPermissions(patientVisitPermission);
			uiPermissions.setVitalSignPermissions(vitalSignPermission);
			uiPermissions.setDischargeSummaryPermissions(dischargeSummaryPermission);
			uiPermissions.setAdmitCardPermissions(admitCardPermission);
			uiPermissions.setPatientCertificatePermissions(patientCertificatePermissions);
			uiPermissions.setPreOperationAssessmentFormPerimissions(preOperationAssessmentFormPerimissions);
			uiPermissions.setInitialAssessmentFormPermissions(initialAssessmentFormPermissions);
			uiPermissions.setNursingAdmissionFormPermissions(nursingAdmissionFormPermissions);

			break;

		case "DENTIST":
			uiPermissions = new UIPermissions();
			clinicalNotesPermission = new ArrayList<String>(Arrays.asList(clinicalNotesPermission()));
			// clinicalNotesPermission.add(DentistPermissionEnum.PROCEDURE_NOTE.getPermissions());
			clinicalNotesPermission.add(DentistPermissionEnum.PAIN_SCALE.getPermissions());
			prescriptionPermission = new ArrayList<String>(Arrays.asList(prescriptionPermission()));
			profilePermission = new ArrayList<String>(Arrays.asList(historyPermission()));
			tabPermission = new ArrayList<String>(Arrays.asList(tabPermission()));
			vitalSignPermission = new ArrayList<String>(Arrays.asList(vitalSignPermission()));
			patientVisitPermission = new ArrayList<String>(Arrays.asList(patientVisitPermission()));
			dischargeSummaryPermission = new ArrayList<String>(Arrays.asList(dischargeSummaryPermission()));
			admitCardPermission = new ArrayList<String>(Arrays.asList(admitcardPermission()));
			dentalLabRequestPermission = new ArrayList<String>(Arrays.asList(dentalLabRequestPermission()));
			dentalWorkSamplePermission = new ArrayList<String>(Arrays.asList(dentalWorkSamplePermission()));
			patientCertificatePermissions = new ArrayList<String>(Arrays.asList(patientCertificatePermission()));
			nursingAdmissionFormPermissions = new ArrayList<String>(Arrays.asList(nurssingAdmissionCardPermission()));
			initialAssessmentFormPermissions = new ArrayList<String>(Arrays.asList(initialAssessmentCardPermission()));
			preOperationAssessmentFormPerimissions = new ArrayList<String>(Arrays.asList(preOperationCardPermission()));

			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			uiPermissions.setTabPermissions(tabPermission);
			uiPermissions.setPatientVisitPermissions(patientVisitPermission);
			uiPermissions.setVitalSignPermissions(vitalSignPermission);
			uiPermissions.setDischargeSummaryPermissions(dischargeSummaryPermission);
			uiPermissions.setAdmitCardPermissions(admitCardPermission);
			uiPermissions.setPatientCertificatePermissions(patientCertificatePermissions);
			uiPermissions.setPreOperationAssessmentFormPerimissions(preOperationAssessmentFormPerimissions);
			uiPermissions.setInitialAssessmentFormPermissions(initialAssessmentFormPermissions);
			uiPermissions.setNursingAdmissionFormPermissions(nursingAdmissionFormPermissions);

			break;

		case "EAR-NOSE-THROAT (ENT) SPECIALIST":
			uiPermissions = new UIPermissions();
			clinicalNotesPermission = new ArrayList<String>(Arrays.asList(clinicalNotesPermission()));
			List<String> entPermissions = new ArrayList<String>(Arrays.asList(entPermission()));
			clinicalNotesPermission.addAll(entPermissions);

			prescriptionPermission = new ArrayList<String>(Arrays.asList(prescriptionPermission()));
			profilePermission = new ArrayList<String>(Arrays.asList(historyPermission()));
			tabPermission = new ArrayList<String>(Arrays.asList(tabPermission()));
			vitalSignPermission = new ArrayList<String>(Arrays.asList(vitalSignPermission()));
			patientVisitPermission = new ArrayList<String>(Arrays.asList(patientVisitPermission()));
			dischargeSummaryPermission = new ArrayList<String>(Arrays.asList(dischargeSummaryPermission()));
			admitCardPermission = new ArrayList<String>(Arrays.asList(admitcardPermission()));
			patientCertificatePermissions = new ArrayList<String>(Arrays.asList(patientCertificatePermission()));
			nursingAdmissionFormPermissions = new ArrayList<String>(Arrays.asList(nurssingAdmissionCardPermission()));
			initialAssessmentFormPermissions = new ArrayList<String>(Arrays.asList(initialAssessmentCardPermission()));

			initialAssessmentFormPermissions.add("NOSE_EXAM");
			initialAssessmentFormPermissions.add("ORAL_CAVITY_THROAT_EXAM");
			initialAssessmentFormPermissions.add("NECK_EXAM");
			initialAssessmentFormPermissions.add("EAR_EXAM");

			preOperationAssessmentFormPerimissions = new ArrayList<String>(Arrays.asList(preOperationCardPermission()));

			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			uiPermissions.setTabPermissions(tabPermission);
			uiPermissions.setPatientVisitPermissions(patientVisitPermission);
			uiPermissions.setVitalSignPermissions(vitalSignPermission);
			uiPermissions.setDischargeSummaryPermissions(dischargeSummaryPermission);
			uiPermissions.setAdmitCardPermissions(admitCardPermission);
			uiPermissions.setPatientCertificatePermissions(patientCertificatePermissions);
			uiPermissions.setPreOperationAssessmentFormPerimissions(preOperationAssessmentFormPerimissions);
			uiPermissions.setInitialAssessmentFormPermissions(initialAssessmentFormPermissions);
			uiPermissions.setNursingAdmissionFormPermissions(nursingAdmissionFormPermissions);

			break;

		case "ORTHOPEDIST":
			uiPermissions = new UIPermissions();
			clinicalNotesPermission = new ArrayList<String>(Arrays.asList(clinicalNotesPermission()));
			prescriptionPermission = new ArrayList<String>(Arrays.asList(prescriptionPermission()));
			profilePermission = new ArrayList<String>(Arrays.asList(historyPermission()));
			tabPermission = new ArrayList<String>(Arrays.asList(tabPermission()));
			vitalSignPermission = new ArrayList<String>(Arrays.asList(vitalSignPermission()));
			patientVisitPermission = new ArrayList<String>(Arrays.asList(patientVisitPermission()));
			dischargeSummaryPermission = new ArrayList<String>(Arrays.asList(dischargeSummaryPermission()));
			List<String> orthoPermission = new ArrayList<String>(Arrays.asList(orthoPermission()));
			dischargeSummaryPermission.addAll(orthoPermission);
			admitCardPermission = new ArrayList<String>(Arrays.asList(admitcardPermission()));
			patientCertificatePermissions = new ArrayList<String>(Arrays.asList(patientCertificatePermission()));
			nursingAdmissionFormPermissions = new ArrayList<String>(Arrays.asList(nurssingAdmissionCardPermission()));
			initialAssessmentFormPermissions = new ArrayList<String>(Arrays.asList(initialAssessmentCardPermission()));
			preOperationAssessmentFormPerimissions = new ArrayList<String>(Arrays.asList(preOperationCardPermission()));

			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			uiPermissions.setTabPermissions(tabPermission);
			uiPermissions.setPatientVisitPermissions(patientVisitPermission);
			uiPermissions.setVitalSignPermissions(vitalSignPermission);
			uiPermissions.setDischargeSummaryPermissions(dischargeSummaryPermission);
			uiPermissions.setAdmitCardPermissions(admitCardPermission);
			uiPermissions.setPatientCertificatePermissions(patientCertificatePermissions);
			uiPermissions.setPreOperationAssessmentFormPerimissions(preOperationAssessmentFormPerimissions);
			uiPermissions.setInitialAssessmentFormPermissions(initialAssessmentFormPermissions);
			uiPermissions.setNursingAdmissionFormPermissions(nursingAdmissionFormPermissions);

			break;

		case "EMPTY":
			uiPermissions = new UIPermissions();
			patientVisitPermission = new ArrayList<String>(Arrays.asList(patientVisitPermission()));
			clinicalNotesPermission = new ArrayList<String>(Arrays.asList(clinicalNotesPermission()));
			prescriptionPermission = new ArrayList<String>(Arrays.asList(prescriptionPermission()));
			profilePermission = new ArrayList<String>(Arrays.asList(historyPermission()));
			tabPermission = new ArrayList<String>(Arrays.asList(tabPermission()));
			vitalSignPermission = new ArrayList<String>(Arrays.asList(vitalSignPermission()));
			dischargeSummaryPermission = new ArrayList<String>(Arrays.asList(dischargeSummaryPermission()));
			admitCardPermission = new ArrayList<String>(Arrays.asList(admitcardPermission()));
			patientCertificatePermissions = new ArrayList<String>(Arrays.asList(patientCertificatePermission()));
			nursingAdmissionFormPermissions = new ArrayList<String>(Arrays.asList(nurssingAdmissionCardPermission()));
			initialAssessmentFormPermissions = new ArrayList<String>(Arrays.asList(initialAssessmentCardPermission()));
			preOperationAssessmentFormPerimissions = new ArrayList<String>(Arrays.asList(preOperationCardPermission()));

			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			uiPermissions.setTabPermissions(tabPermission);
			uiPermissions.setPatientVisitPermissions(patientVisitPermission);
			uiPermissions.setVitalSignPermissions(vitalSignPermission);
			uiPermissions.setDischargeSummaryPermissions(dischargeSummaryPermission);
			uiPermissions.setAdmitCardPermissions(admitCardPermission);
			uiPermissions.setPatientCertificatePermissions(patientCertificatePermissions);
			uiPermissions.setPreOperationAssessmentFormPerimissions(preOperationAssessmentFormPerimissions);
			uiPermissions.setInitialAssessmentFormPermissions(initialAssessmentFormPermissions);
			uiPermissions.setNursingAdmissionFormPermissions(nursingAdmissionFormPermissions);

			break;
		default:
			uiPermissions = new UIPermissions();
			patientVisitPermission = new ArrayList<String>(Arrays.asList(patientVisitPermission()));
			clinicalNotesPermission = new ArrayList<String>(Arrays.asList(clinicalNotesPermission()));
			prescriptionPermission = new ArrayList<String>(Arrays.asList(prescriptionPermission()));
			profilePermission = new ArrayList<String>(Arrays.asList(historyPermission()));
			tabPermission = new ArrayList<String>(Arrays.asList(tabPermission()));
			vitalSignPermission = new ArrayList<String>(Arrays.asList(vitalSignPermission()));
			dischargeSummaryPermission = new ArrayList<String>(Arrays.asList(dischargeSummaryPermission()));
			admitCardPermission = new ArrayList<String>(Arrays.asList(admitcardPermission()));
			patientCertificatePermissions = new ArrayList<String>(Arrays.asList(patientCertificatePermission()));

			initialAssessmentFormPermissions = new ArrayList<String>(Arrays.asList(initialAssessmentCardPermission()));
			nursingAdmissionFormPermissions = new ArrayList<String>(Arrays.asList(nurssingAdmissionCardPermission()));
			preOperationAssessmentFormPerimissions = new ArrayList<String>(Arrays.asList(preOperationCardPermission()));

			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			uiPermissions.setTabPermissions(tabPermission);
			uiPermissions.setPatientVisitPermissions(patientVisitPermission);
			uiPermissions.setVitalSignPermissions(vitalSignPermission);
			uiPermissions.setDischargeSummaryPermissions(dischargeSummaryPermission);
			uiPermissions.setAdmitCardPermissions(admitCardPermission);
			uiPermissions.setPatientCertificatePermissions(patientCertificatePermissions);

			uiPermissions.setNursingAdmissionFormPermissions(nursingAdmissionFormPermissions);
			uiPermissions.setInitialAssessmentFormPermissions(initialAssessmentFormPermissions);
			uiPermissions.setPreOperationAssessmentFormPerimissions(preOperationAssessmentFormPerimissions);
			break;
		}
		return uiPermissions;
	}

	@Override
	@Transactional
	public UIPermissions getDefaultPermissions() {
		UIPermissions uiPermissions = null;
		ArrayList<String> clinicalNotesPermission = null;
		ArrayList<String> prescriptionPermission = null;
		ArrayList<String> patientVisitPermission = null;
		ArrayList<String> profilePermission = null;
		ArrayList<String> tabPermission = null;
		ArrayList<String> vitalSignPermission = null;
		ArrayList<String> dischargeSummaryPermission = null;
		ArrayList<String> admitCardPermission = null;
		ArrayList<String> patientCertificatePermissions = null;

		uiPermissions = new UIPermissions();
		clinicalNotesPermission = new ArrayList<String>();
		clinicalNotesPermission.add(ClinicalNotesPermissionEnum.VITAL_SIGNS.getPermissions());
		clinicalNotesPermission.add(ClinicalNotesPermissionEnum.COMPLAINT.getPermissions());
		clinicalNotesPermission.add(ClinicalNotesPermissionEnum.OBSERVATION.getPermissions());
		clinicalNotesPermission.add(ClinicalNotesPermissionEnum.INVESTIGATIONS.getPermissions());
		clinicalNotesPermission.add(ClinicalNotesPermissionEnum.NOTES.getPermissions());
		clinicalNotesPermission.add(ClinicalNotesPermissionEnum.DIAGNOSIS.getPermissions());
		clinicalNotesPermission.add(ClinicalNotesPermissionEnum.DIAGRAM.getPermissions());
		prescriptionPermission = new ArrayList<String>(Arrays.asList(prescriptionPermission()));
		patientVisitPermission = new ArrayList<String>(Arrays.asList(patientVisitPermission()));
		profilePermission = new ArrayList<String>(Arrays.asList(historyPermission()));
		tabPermission = new ArrayList<String>(Arrays.asList(tabPermission()));
		vitalSignPermission = new ArrayList<String>(Arrays.asList(vitalSignPermission()));
		dischargeSummaryPermission = new ArrayList<String>(Arrays.asList(dischargeSummaryPermission()));
		admitCardPermission = new ArrayList<String>(Arrays.asList(admitcardPermission()));
		patientCertificatePermissions = new ArrayList<String>(Arrays.asList(patientCertificatePermission()));
		patientCertificatePermissions
				.remove(PatientCertificatePermissions.SPECICAL_INFORMATION_CONSENT.getPermission());
		patientCertificatePermissions
				.remove(PatientCertificatePermissions.CONSENT_FOR_BLOOD_TRANFUSION.getPermission());
		patientCertificatePermissions.remove(PatientCertificatePermissions.HIGH_RISK_CONSENT_FORM.getPermission());
		patientCertificatePermissions.remove(PatientCertificatePermissions.MLC_INFORMATION.getPermission());

		uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
		uiPermissions.setPrescriptionPermissions(prescriptionPermission);
		uiPermissions.setProfilePermissions(profilePermission);
		uiPermissions.setTabPermissions(tabPermission);
		uiPermissions.setPatientVisitPermissions(patientVisitPermission);
		uiPermissions.setVitalSignPermissions(vitalSignPermission);
		uiPermissions.setDischargeSummaryPermissions(dischargeSummaryPermission);
		uiPermissions.setAdmitCardPermissions(admitCardPermission);
		uiPermissions.setLandingPagePermissions("CONTACTS");
		uiPermissions.setPatientCertificatePermissions(patientCertificatePermissions);
		return uiPermissions;
	}

	/*
	 * private List<String> initailizeGeneralList() { SpecialityTypeEnum[]
	 * specialityTypeEnums = values();
	 * 
	 * return null; }
	 */

	private String[] clinicalNotesPermission() {
		return Arrays.toString(ClinicalNotesPermissionEnum.values()).replaceAll("^.|.$", "").split(", ");
	}

	private String[] prescriptionPermission() {
		return Arrays.toString(PrescriptionPermissionEnum.values()).replaceAll("^.|.$", "").split(", ");
	}

	private String[] gynaecPermission() {
		return Arrays.toString(GynacPermissionsEnum.values()).replaceAll("^.|.$", "").split(", ");
	}

	private String[] patientVisitPermission() {
		return Arrays.toString(PatientVisitPermissionEnum.values()).replaceAll("^.|.$", "").split(", ");
	}

	private String[] historyPermission() {
		return Arrays.toString(ProfilePermissionEnum.values()).replaceAll("^.|.$", "").split(", ");
	}

	private String[] tabPermission() {
		return Arrays.toString(TabPermissionsEnum.values()).replaceAll("^.|.$", "").split(", ");
	}

	private String[] specialityType() {
		return Arrays.toString(SpecialityTypeEnum.values()).replaceAll("^.|.$", "").split(", ");
	}

	private String[] vitalSignPermission() {
		return Arrays.toString(VitalSignPermissions.values()).replaceAll("^.|.$", "").split(", ");
	}

	private String[] dischargeSummaryPermission() {
		return Arrays.toString(DischargeSummaryPermissions.values()).replaceAll("^.|.$", "").split(", ");
	}

	private String[] admitcardPermission() {
		return Arrays.toString(AdmitCardPermissionEnum.values()).replaceAll("^.|.$", "").split(", ");
	}

	private String[] entPermission() {
		return Arrays.toString(ENTPermissionType.values()).replaceAll("^.|.$", "").split(", ");
	}

	private String[] orthoPermission() {
		return Arrays.toString(OrthoPermissionType.values()).replaceAll("^.|.$", "").split(", ");
	}

	private String[] dentalLabRequestPermission() {
		return Arrays.toString(DentalLabRequestPermissions.values()).replaceAll("^.|.$", "").split(", ");
	}

	private String[] dentalWorkSamplePermission() {
		return Arrays.toString(WorkSamplePermissions.values()).replaceAll("^.|.$", "").split(", ");
	}

	private String[] patientCertificatePermission() {
		return Arrays.toString(PatientCertificatePermissions.values()).replaceAll("^.|.$", "").split(", ");
	}

	private String[] nurssingAdmissionCardPermission() {
		return Arrays.toString(NurssingAdmissionCardPermissionEnum.values()).replaceAll("^.|.$", "").split(", ");
	}

	private String[] initialAssessmentCardPermission() {
		return Arrays.toString(InitialAssessmentCardPermissionEnum.values()).replaceAll("^.|.$", "").split(", ");
	}

	private String[] preOperationCardPermission() {
		return Arrays.toString(PreOperationCardPermissionEnum.values()).replaceAll("^.|.$", "").split(", ");
	}

	@Override
	@Transactional
	public DynamicUIResponse getBothPermissions(String doctorId) {
		DynamicUIResponse uiResponse = new DynamicUIResponse();
		uiResponse.setAllPermissions(getAllPermissionForDoctor(doctorId));
		DynamicUI dynamicUI = getPermissionForDoctor(doctorId);
		if (dynamicUI != null) {
			uiResponse.setDoctorPermissions(dynamicUI.getUiPermissions());
			uiResponse.setDoctorId(dynamicUI.getDoctorId());
		}
		return uiResponse;
	}

	@Override
	@Transactional
	public DataDynamicUI getDynamicDataPermissionForDoctor(String doctorId) {
		DataDynamicUI dataDynamicUI = null;
		DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
		if (doctorCollection != null) {
			dataDynamicUI = new DataDynamicUI();
			DataDynamicUICollection dataDynamicUICollection = dataDynamicUIRepository
					.findByDoctorId(new ObjectId(doctorId));
			if (dataDynamicUICollection != null) {
				BeanUtil.map(dataDynamicUICollection, dataDynamicUI);
			} else {
				dataDynamicUI = new DataDynamicUI();
				dataDynamicUI.setDoctorId(doctorId);
				DataDynamicField dataDynamicField = new DataDynamicField();
				dataDynamicField.setClinicalNotesDynamicField(new ClinicalNotesDynamicField());
				dataDynamicField.setPrescriptionDynamicField(new PrescriptionDynamicField());
				dataDynamicField.setDischargeSummaryDynamicFields(new DischargeSummaryDynamicFields());
				dataDynamicField.setTreatmentDynamicFields(new TreatmentDynamicFields());
				// ClinicalNotesDynamicField clinicalNotesDynamicField = new
				// ClinicalNotesDynamicField();
				dataDynamicField.setClinicalNotesDynamicField(new ClinicalNotesDynamicField());
				// PrescriptionDynamicField prescriptionDynamicField = new
				// PrescriptionDynamicField();
				dataDynamicField.setPrescriptionDynamicField(new PrescriptionDynamicField());
				dataDynamicUI.setDataDynamicField(dataDynamicField);
			}
		} else {
			throw new BusinessException(ServiceError.InvalidInput, "Doctor not present");
		}
		return dataDynamicUI;
	}

	@Override
	@Transactional
	public DataDynamicUI postDataPermissions(DataDynamicUI dynamicUIRequest) {
		DataDynamicUI dataDynamicUI = null;
		DataDynamicUICollection dataDynamicUICollection = dataDynamicUIRepository
				.findByDoctorId(new ObjectId(dynamicUIRequest.getDoctorId()));
		if (dataDynamicUICollection != null) {
			dataDynamicUICollection.setDataDynamicField(dynamicUIRequest.getDataDynamicField());
			dataDynamicUICollection = dataDynamicUIRepository.save(dataDynamicUICollection);
			dataDynamicUI = new DataDynamicUI();
			BeanUtil.map(dataDynamicUICollection, dataDynamicUI);
		} else {
			dataDynamicUICollection = new DataDynamicUICollection();
			BeanUtil.map(dynamicUIRequest, dataDynamicUICollection);
			dataDynamicUICollection.setCreatedTime(new Date());
			dataDynamicUICollection = dataDynamicUIRepository.save(dataDynamicUICollection);
			dataDynamicUI = new DataDynamicUI();
			BeanUtil.map(dataDynamicUICollection, dataDynamicUI);
		}

		pushNotificationServices.notifyUser(dynamicUIRequest.getDoctorId(), "",
				ComponentType.REFRESH_DATA_SETTING.getType(), null, null);

		return dataDynamicUI;
	}

	@Override
	@Transactional
	public DentalLabDynamicUi postDentalLabPermissions(DentalLabDynamicUi request) {
		DentalLabDynamicUi dentalLabDynamicUI = null;
		DentalLabDynamicUICollection dentalLabDynamicUICollection = dentalLabDynamicUIRepository
				.findByDentalLabId(new ObjectId(request.getDentalLabId()));
		if (dentalLabDynamicUICollection != null) {
			dentalLabDynamicUICollection.setDentalLabDynamicField(request.getDentalLabDynamicField());
			dentalLabDynamicUICollection = dentalLabDynamicUIRepository.save(dentalLabDynamicUICollection);
			dentalLabDynamicUI = new DentalLabDynamicUi();
			BeanUtil.map(dentalLabDynamicUICollection, dentalLabDynamicUI);
		} else {
			dentalLabDynamicUICollection = new DentalLabDynamicUICollection();
			BeanUtil.map(request, dentalLabDynamicUICollection);
			dentalLabDynamicUICollection.setCreatedTime(new Date());
			dentalLabDynamicUICollection = dentalLabDynamicUIRepository.save(dentalLabDynamicUICollection);
			dentalLabDynamicUI = new DentalLabDynamicUi();
			BeanUtil.map(dentalLabDynamicUICollection, dentalLabDynamicUI);
		}
		return dentalLabDynamicUI;
	}

	@Override
	@Transactional
	public DentalLabDynamicField getAllDentalLabPermissions() {
		DentalLabDynamicField dentalLabDynamicField = null;
		try {
			dentalLabDynamicField = new DentalLabDynamicField();
			dentalLabDynamicField
					.setDentalLabRequestPermission(new ArrayList<String>(Arrays.asList(dentalLabRequestPermission())));
			dentalLabDynamicField
					.setDentalWorkSamplePermission(new ArrayList<String>(Arrays.asList(dentalWorkSamplePermission())));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dentalLabDynamicField;
	}

	@Override
	@Transactional
	public DentalLabDynamicUi getPermissionForDentalLab(String dentalLabId) {
		DentalLabDynamicUi dentalLabDynamicUi = null;
		try {
			LocationCollection locationCollection = locationRepository.findById(new ObjectId(dentalLabId)).orElse(null);
			if (locationCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Lab not found");
			}
			DentalLabDynamicUICollection dentalLabDynamicUICollection = dentalLabDynamicUIRepository
					.findByDentalLabId(new ObjectId(dentalLabId));
			if (dentalLabDynamicUICollection != null) {
				dentalLabDynamicUi = new DentalLabDynamicUi();
				BeanUtil.map(dentalLabDynamicUICollection, dentalLabDynamicUi);
			} else {
				dentalLabDynamicUi = new DentalLabDynamicUi();
				dentalLabDynamicUi.setDentalLabId(dentalLabId);
				dentalLabDynamicUi.setDentalLabDynamicField(getAllDentalLabPermissions());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dentalLabDynamicUi;
	}

	@Override
	@Transactional
	public KioskDynamicUi addEditKioskUiPermission(KioskDynamicUiResquest request) {
		KioskDynamicUi response = null;
		try {
			KioskDynamicUiCollection dynamicUiCollection = null;
			UserCollection doctor = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
			if (doctor == null) {
				throw new BusinessException(ServiceError.NoRecord, "doctor not found By doctorId");
			}

			KioskDynamicUiCollection olddynamicUiCollection = kioskDynamicUiRepository
					.findByDoctorId(new ObjectId(request.getDoctorId()));
			dynamicUiCollection = new KioskDynamicUiCollection();
			if (olddynamicUiCollection == null) {

				BeanUtil.map(request, dynamicUiCollection);
				dynamicUiCollection
						.setCreatedBy((!DPDoctorUtils.anyStringEmpty(doctor.getTitle()) ? "Dr." : doctor.getTitle())
								+ doctor.getFirstName());
				dynamicUiCollection.setCreatedTime(new Date());

			} else {
				BeanUtil.map(request, dynamicUiCollection);
				dynamicUiCollection.setId(olddynamicUiCollection.getId());
				dynamicUiCollection.setCreatedBy(olddynamicUiCollection.getCreatedBy());
				dynamicUiCollection.setCreatedTime(olddynamicUiCollection.getCreatedTime());
			}
			dynamicUiCollection = kioskDynamicUiRepository.save(dynamicUiCollection);
			response = new KioskDynamicUi();
			BeanUtil.map(dynamicUiCollection, response);
			response.setAllkioskPermission(new ArrayList<String>(
					Arrays.asList((Arrays.toString(KioskDynamicUiEnum.values()).replaceAll("^.|.$", "").split(", ")))));

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error occuring while add edit Kiosk UI Permission ");
		}
		return response;
	}

	@Override
	@Transactional
	public KioskDynamicUi getKioskUiPermission(String doctorId) {
		KioskDynamicUi response = null;
		try {
			KioskDynamicUiCollection dynamicUiCollection = kioskDynamicUiRepository
					.findByDoctorId(new ObjectId(doctorId));
			response = new KioskDynamicUi();
			if (dynamicUiCollection != null) {
				BeanUtil.map(dynamicUiCollection, response);
			}
			response.setDoctorId(doctorId);
			response.setAllkioskPermission(new ArrayList<String>(
					Arrays.asList((Arrays.toString(KioskDynamicUiEnum.values()).replaceAll("^.|.$", "").split(", ")))));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error occuring while getting Kiosk UI Permission ");
		}
		return response;
	}

	@Override
	@Transactional
	public NutritionUI getAllNutritionUIPermission() {
		NutritionUI response = null;
		try {
			List<String> uiList = new ArrayList<String>(Arrays
					.asList(Arrays.toString(NutritionUIPermissionEnum.values()).replaceAll("^.|.$", "").split(", ")));
			response = new NutritionUI();
			List<NutritionUIPermission> uiPermissions = new ArrayList<NutritionUIPermission>();
			NutritionUIPermission uiPermission = null;
			for (String ui : uiList) {
				uiPermission = new NutritionUIPermission();
				uiPermission.setUi(ui);
				uiPermission.setAccessTypes(new ArrayList<String>(Arrays
						.asList(Arrays.toString(AccessPermissionType.values()).replaceAll("^.|.$", "").split(", "))));
				uiPermissions.add(uiPermission);
			}
			response.setUiPermission(uiPermissions);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown,
					"Error occuring while getting ALL Nutrition UI Permission ");
		}
		return response;
	}

	@Override
	@Transactional
	public NutritionUI addEditNutritionUIPermission(NutrirtionUIRequest request) {
		NutritionUI response = null;
		try {
			NutritionUICollection nutritionUICollection = null;
			UserCollection userCollection = userRepository.findById(new ObjectId(request.getAdminId())).orElse(null);
			if (userCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "admin not found By Id");
			}
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				nutritionUICollection = nutritionUIRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (nutritionUICollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "nutrition not found By Id");
				}
				nutritionUICollection.setUiPermission(new ArrayList<NutritionUIPermission>());
				nutritionUICollection.setUiPermission(request.getUiPermission());
				nutritionUICollection.setUserId(new ObjectId(request.getUserId()));
				nutritionUICollection.setCreatedBy(userCollection.getFirstName());
			} else {
				nutritionUICollection = new NutritionUICollection();
				BeanUtil.map(request, nutritionUICollection);
				nutritionUICollection.setCreatedTime(new Date());
				nutritionUICollection.setCreatedBy(userCollection.getFirstName());
			}
			nutritionUICollection = nutritionUIRepository.save(nutritionUICollection);
			response = new NutritionUI();
			BeanUtil.map(nutritionUICollection, response);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error occuring while add edit Nutrition UI Permission ");
		}
		return response;
	}

	@Override
	@Transactional
	public NutritionUI getNutritionUIPermission(String doctorId) {
		NutritionUI response = null;
		try {
			NutritionUICollection nutritionUICollection = nutritionUIRepository.findById(new ObjectId(doctorId))
					.orElse(null);
			response = new NutritionUI();
			BeanUtil.map(nutritionUICollection, response);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error occuring while getting Nutrition UI Permission ");
		}
		return response;
	}

}
