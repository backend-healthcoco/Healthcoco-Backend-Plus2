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
import com.dpdocter.beans.PrescriptionDynamicField;
import com.dpdocter.beans.TreatmentDynamicFields;
import com.dpdocter.beans.DataDynamicUI;
import com.dpdocter.beans.DynamicUI;
import com.dpdocter.beans.PrescriptionDynamicField;
import com.dpdocter.beans.UIPermissions;
import com.dpdocter.collections.DataDynamicUICollection;
import com.dpdocter.collections.DentalLabDynamicUICollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.DynamicUICollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.SpecialityCollection;
import com.dpdocter.enums.AdmitCardPermissionEnum;
import com.dpdocter.enums.CardioPermissionEnum;
import com.dpdocter.enums.ClinicalNotesPermissionEnum;
import com.dpdocter.enums.DentalLabRequestPermissions;
import com.dpdocter.enums.DentistPermissionEnum;
import com.dpdocter.enums.DischargeSummaryPermissions;
import com.dpdocter.enums.ENTPermissionType;
import com.dpdocter.enums.GynacPermissionsEnum;
import com.dpdocter.enums.OpthoPermissionEnums;
import com.dpdocter.enums.OrthoPermissionType;
import com.dpdocter.enums.PatientVisitPermissionEnum;
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
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DynamicUIRequest;
import com.dpdocter.response.DynamicUIResponse;
import com.dpdocter.services.DentalLabService;
import com.dpdocter.services.DynamicUIService;

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
		DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
		if (doctorCollection != null) {
			uiPermissions = new UIPermissions();
			UIPermissions tempPermissions = null;
			String speciality = null;
			if (doctorCollection.getSpecialities() == null || doctorCollection.getSpecialities().isEmpty()) {
				uiPermissions = getAllPermissionBySpeciality(String.valueOf("EMPTY"));
			} else {
				for (ObjectId specialityId : doctorCollection.getSpecialities()) {

					SpecialityCollection specialityCollection = specialityRepository.findOne(specialityId);
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
			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			uiPermissions.setTabPermissions(tabPermission);
			uiPermissions.setPatientVisitPermissions(patientVisitPermission);
			uiPermissions.setVitalSignPermissions(vitalSignPermission);
			uiPermissions.setDischargeSummaryPermissions(dischargeSummaryPermission);
			uiPermissions.setAdmitCardPermissions(admitCardPermission);
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
			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			uiPermissions.setTabPermissions(tabPermission);
			uiPermissions.setPatientVisitPermissions(patientVisitPermission);
			uiPermissions.setVitalSignPermissions(vitalSignPermission);
			uiPermissions.setDischargeSummaryPermissions(dischargeSummaryPermission);
			uiPermissions.setAdmitCardPermissions(admitCardPermission);
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
			clinicalNotesPermission.add(GynacPermissionsEnum.USG_GENDER_COUNT.getPermissions());
			prescriptionPermission = new ArrayList<String>(Arrays.asList(prescriptionPermission()));
			profilePermission = new ArrayList<String>(Arrays.asList(historyPermission()));
			profilePermission.add(GynacPermissionsEnum.BIRTH_HISTORY.getPermissions());
			tabPermission = new ArrayList<String>(Arrays.asList(tabPermission()));
			vitalSignPermission = new ArrayList<String>(Arrays.asList(vitalSignPermission()));
			patientVisitPermission = new ArrayList<String>(Arrays.asList(patientVisitPermission()));
			dischargeSummaryPermission = new ArrayList<String>(Arrays.asList(dischargeSummaryPermission()));
			admitCardPermission = new ArrayList<String>(Arrays.asList(admitcardPermission()));
			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			uiPermissions.setTabPermissions(tabPermission);
			uiPermissions.setPatientVisitPermissions(patientVisitPermission);
			uiPermissions.setVitalSignPermissions(vitalSignPermission);
			uiPermissions.setDischargeSummaryPermissions(dischargeSummaryPermission);
			uiPermissions.setAdmitCardPermissions(admitCardPermission);
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
			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			uiPermissions.setTabPermissions(tabPermission);
			uiPermissions.setPatientVisitPermissions(patientVisitPermission);
			uiPermissions.setVitalSignPermissions(vitalSignPermission);
			uiPermissions.setDischargeSummaryPermissions(dischargeSummaryPermission);
			uiPermissions.setAdmitCardPermissions(admitCardPermission);
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
			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			uiPermissions.setTabPermissions(tabPermission);
			uiPermissions.setPatientVisitPermissions(patientVisitPermission);
			uiPermissions.setVitalSignPermissions(vitalSignPermission);
			uiPermissions.setDischargeSummaryPermissions(dischargeSummaryPermission);
			uiPermissions.setAdmitCardPermissions(admitCardPermission);

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
			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			uiPermissions.setTabPermissions(tabPermission);
			uiPermissions.setPatientVisitPermissions(patientVisitPermission);
			uiPermissions.setVitalSignPermissions(vitalSignPermission);
			uiPermissions.setDischargeSummaryPermissions(dischargeSummaryPermission);
			uiPermissions.setAdmitCardPermissions(admitCardPermission);
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
			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			uiPermissions.setTabPermissions(tabPermission);
			uiPermissions.setPatientVisitPermissions(patientVisitPermission);
			uiPermissions.setVitalSignPermissions(vitalSignPermission);
			uiPermissions.setDischargeSummaryPermissions(dischargeSummaryPermission);

			uiPermissions.setAdmitCardPermissions(admitCardPermission);
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
			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			uiPermissions.setTabPermissions(tabPermission);
			uiPermissions.setPatientVisitPermissions(patientVisitPermission);
			uiPermissions.setVitalSignPermissions(vitalSignPermission);
			uiPermissions.setDischargeSummaryPermissions(dischargeSummaryPermission);
			uiPermissions.setAdmitCardPermissions(admitCardPermission);
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
			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			uiPermissions.setTabPermissions(tabPermission);
			uiPermissions.setPatientVisitPermissions(patientVisitPermission);
			uiPermissions.setVitalSignPermissions(vitalSignPermission);
			uiPermissions.setDischargeSummaryPermissions(dischargeSummaryPermission);
			uiPermissions.setAdmitCardPermissions(admitCardPermission);
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
		uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
		uiPermissions.setPrescriptionPermissions(prescriptionPermission);
		uiPermissions.setProfilePermissions(profilePermission);
		uiPermissions.setTabPermissions(tabPermission);
		uiPermissions.setPatientVisitPermissions(patientVisitPermission);
		uiPermissions.setVitalSignPermissions(vitalSignPermission);
		uiPermissions.setDischargeSummaryPermissions(dischargeSummaryPermission);
		uiPermissions.setAdmitCardPermissions(admitCardPermission);
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
				//ClinicalNotesDynamicField clinicalNotesDynamicField = new ClinicalNotesDynamicField();
				//System.out.println(clinicalNotesDynamicField);
				dataDynamicField.setClinicalNotesDynamicField(new ClinicalNotesDynamicField());
				//PrescriptionDynamicField prescriptionDynamicField = new PrescriptionDynamicField();
				//System.out.println(prescriptionDynamicField);
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
			// TODO: handle exception
		}
		return dentalLabDynamicField;
	}

	@Override
	@Transactional
	public DentalLabDynamicUi getPermissionForDentalLab(String dentalLabId) {
		DentalLabDynamicUi dentalLabDynamicUi = null;
		try {
			
			LocationCollection locationCollection = locationRepository.findOne(new ObjectId(dentalLabId));
			if(locationCollection == null)
			{
				throw new BusinessException(ServiceError.NoRecord , "Lab not found");
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
			// TODO: handle exception
			e.printStackTrace();
		}
		return dentalLabDynamicUi;
	}

}
