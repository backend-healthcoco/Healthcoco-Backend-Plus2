package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.DynamicUI;
import com.dpdocter.beans.UIPermissions;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.DynamicUICollection;
import com.dpdocter.collections.SpecialityCollection;
import com.dpdocter.enums.CardioPermissionEnum;
import com.dpdocter.enums.ClinicalNotesPermissionEnum;
import com.dpdocter.enums.GynacPermissionsEnum;
import com.dpdocter.enums.OpthoPermissionEnums;
import com.dpdocter.enums.PatientVisitPermissionEnum;
import com.dpdocter.enums.PrescriptionPermissionEnum;
import com.dpdocter.enums.ProfilePermissionEnum;
import com.dpdocter.enums.SpecialityTypeEnum;
import com.dpdocter.enums.TabPermissionsEnum;
import com.dpdocter.enums.VitalSignPermissions;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.DynamicUIRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DynamicUIRequest;
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
		DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
		if (doctorCollection != null) {
			uiPermissions = new UIPermissions();
			UIPermissions tempPermissions = null;
			String speciality = null;
			if (doctorCollection.getSpecialities() == null || doctorCollection.getSpecialities().isEmpty()) {

				tempPermissions = getAllPermissionBySpeciality(String.valueOf("EMPTY"));
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
					}
				}
				uiPermissions.setPatientVisitPermissions(new ArrayList<String>(patientVisitPermissionsSet));
				uiPermissions.setClinicalNotesPermissions(new ArrayList<String>(clinicalNotesPermissionsSet));
				uiPermissions.setPrescriptionPermissions(new ArrayList<String>(prescriptionPermissionsSet));
				uiPermissions.setProfilePermissions(new ArrayList<String>(profilePermissionsSet));
				uiPermissions.setTabPermissions(new ArrayList<String>(tabPermissionsSet));
				uiPermissions.setVitalSignPermissions(new ArrayList<String>(vitalSignPermissionSet));

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
		switch (speciality.toUpperCase().trim()) {
		case "OPHTHALMOLOGIST":
			uiPermissions = new UIPermissions();
			clinicalNotesPermission = new ArrayList<String>(Arrays.asList(clinicalNotesPermission()));
			clinicalNotesPermission.add(OpthoPermissionEnums.OPTHO_CLINICAL_NOTES.getPermissions());
			prescriptionPermission = new ArrayList<String>(Arrays.asList(prescriptionPermission()));
			prescriptionPermission.add(OpthoPermissionEnums.OPTHO_RX.getPermissions());
			profilePermission = new ArrayList<String>(Arrays.asList(historyPermission()));
			tabPermission = new ArrayList<String>(Arrays.asList(tabPermission()));
			patientVisitPermission = new ArrayList<String>(Arrays.asList(patientVisitPermission()));
			vitalSignPermission = new ArrayList<String>(Arrays.asList(vitalSignPermission()));
			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			uiPermissions.setTabPermissions(tabPermission);
			uiPermissions.setPatientVisitPermissions(patientVisitPermission);
			uiPermissions.setVitalSignPermissions(vitalSignPermission);
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
			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			uiPermissions.setTabPermissions(tabPermission);
			uiPermissions.setPatientVisitPermissions(patientVisitPermission);
			uiPermissions.setVitalSignPermissions(vitalSignPermission);
			break;
		case "GYNECOLOGIST/OBSTETRICIAN":
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
			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			uiPermissions.setTabPermissions(tabPermission);
			uiPermissions.setPatientVisitPermissions(patientVisitPermission);
			uiPermissions.setVitalSignPermissions(vitalSignPermission);
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
			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			uiPermissions.setTabPermissions(tabPermission);
			uiPermissions.setPatientVisitPermissions(patientVisitPermission);
			uiPermissions.setVitalSignPermissions(vitalSignPermission);
			break;
		case "EMPTY":
			uiPermissions = new UIPermissions();
			patientVisitPermission = new ArrayList<String>(Arrays.asList(patientVisitPermission()));
			clinicalNotesPermission = new ArrayList<String>(Arrays.asList(clinicalNotesPermission()));
			prescriptionPermission = new ArrayList<String>(Arrays.asList(prescriptionPermission()));
			profilePermission = new ArrayList<String>(Arrays.asList(historyPermission()));
			tabPermission = new ArrayList<String>(Arrays.asList(tabPermission()));
			vitalSignPermission = new ArrayList<String>(Arrays.asList(vitalSignPermission()));
			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			uiPermissions.setTabPermissions(tabPermission);
			uiPermissions.setPatientVisitPermissions(patientVisitPermission);
			uiPermissions.setVitalSignPermissions(vitalSignPermission);
			break;
		default:
			uiPermissions = new UIPermissions();
			patientVisitPermission = new ArrayList<String>(Arrays.asList(patientVisitPermission()));
			clinicalNotesPermission = new ArrayList<String>(Arrays.asList(clinicalNotesPermission()));
			prescriptionPermission = new ArrayList<String>(Arrays.asList(prescriptionPermission()));
			profilePermission = new ArrayList<String>(Arrays.asList(historyPermission()));
			tabPermission = new ArrayList<String>(Arrays.asList(tabPermission()));
			vitalSignPermission = new ArrayList<String>(Arrays.asList(vitalSignPermission()));
			uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
			uiPermissions.setPrescriptionPermissions(prescriptionPermission);
			uiPermissions.setProfilePermissions(profilePermission);
			uiPermissions.setTabPermissions(tabPermission);
			uiPermissions.setPatientVisitPermissions(patientVisitPermission);
			uiPermissions.setVitalSignPermissions(vitalSignPermission);
			break;
		}
		return uiPermissions;
	}

	private UIPermissions getDefaultPermissions() {
		UIPermissions uiPermissions = null;
		ArrayList<String> clinicalNotesPermission = null;
		ArrayList<String> prescriptionPermission = null;
		ArrayList<String> patientVisitPermission = null;
		ArrayList<String> profilePermission = null;
		ArrayList<String> tabPermission = null;
		ArrayList<String> vitalSignPermission = null;
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
		uiPermissions.setClinicalNotesPermissions(clinicalNotesPermission);
		uiPermissions.setPrescriptionPermissions(prescriptionPermission);
		uiPermissions.setProfilePermissions(profilePermission);
		uiPermissions.setTabPermissions(tabPermission);
		uiPermissions.setPatientVisitPermissions(patientVisitPermission);
		uiPermissions.setVitalSignPermissions(vitalSignPermission);
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
	
	private String[] vitalSignPermission()
	{
		return Arrays.toString(VitalSignPermissions.values()).replaceAll("^.|.$", "").split(", ");
	}

}
