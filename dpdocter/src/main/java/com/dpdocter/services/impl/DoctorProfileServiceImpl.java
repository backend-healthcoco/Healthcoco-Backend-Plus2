package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.dpdocter.beans.DoctorExperience;
import com.dpdocter.beans.MedicalCouncil;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.MedicalCouncilCollection;
import com.dpdocter.collections.SpecialityCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.DoctorExperienceUnit;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.MedicalCouncilRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DoctorAchievementAddEditRequest;
import com.dpdocter.request.DoctorContactAddEditRequest;
import com.dpdocter.request.DoctorEducationAddEditRequest;
import com.dpdocter.request.DoctorExperienceAddEditRequest;
import com.dpdocter.request.DoctorProfilePictureAddEditRequest;
import com.dpdocter.request.DoctorRegistrationAddEditRequest;
import com.dpdocter.request.DoctorSpecialityAddEditRequest;
import com.dpdocter.services.DoctorProfileService;
import com.dpdocter.services.FileManager;

public class DoctorProfileServiceImpl implements DoctorProfileService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private MedicalCouncilRepository medicalCouncilRepository;

	@Autowired
	private SpecialityRepository specialityRepository;

	@Autowired
	private FileManager fileManager;

	@Override
	public Boolean addEditName(String doctorId, String title, String fname, String mname, String lname) {
		UserCollection userCollection = null;
		Boolean response = false;
		try {
			userCollection = userRepository.findOne(doctorId);
			userCollection.setTitle(title);
			userCollection.setFirstName(fname);
			userCollection.setMiddleName(mname);
			userCollection.setLastName(lname);
			userRepository.save(userCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	public Boolean addEditExperience(String doctorId, String experience) {
		DoctorCollection doctorCollection = null;
		Boolean response = false;
		try {
			doctorCollection = doctorRepository.findByUserId(doctorId);
			DoctorExperience doctorExperience = new DoctorExperience();
			doctorExperience.setExperience(Float.parseFloat(experience));
			doctorExperience.setPeriod(DoctorExperienceUnit.YEAR);
			doctorCollection.setExperience(doctorExperience);
			doctorRepository.save(doctorCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	public Boolean addEditContact(DoctorContactAddEditRequest request) {
		UserCollection userCollection = null;
		DoctorCollection doctorCollection = null;
		Boolean response = false;
		try {
			userCollection = userRepository.findOne(request.getDoctorId());
			doctorCollection = doctorRepository.findByUserId(request.getDoctorId());
			BeanUtil.map(request, userCollection);
			BeanUtil.map(request, doctorCollection);
			userRepository.save(userCollection);
			doctorRepository.save(doctorCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	public Boolean addEditEducation(DoctorEducationAddEditRequest request) {
		DoctorCollection doctorCollection = null;
		Boolean response = false;
		try {
			doctorCollection = doctorRepository.findByUserId(request.getDoctorId());
			BeanUtil.map(request, doctorCollection);
			doctorRepository.save(doctorCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	public Boolean addEditMedicalCouncil(List<MedicalCouncil> medicalCouncils) {
		List<MedicalCouncilCollection> medicalCouncilCollections = null;
		Boolean response = false;
		try {
			medicalCouncilCollections = new ArrayList<MedicalCouncilCollection>();
			BeanUtil.map(medicalCouncils, medicalCouncilCollections);
			medicalCouncilRepository.save(medicalCouncilCollections);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Editing Medical Councils");
		}
		return response;
	}

	@Override
	public Boolean addEditSpeciality(DoctorSpecialityAddEditRequest request) {
		DoctorCollection doctorCollection = null;
		List<SpecialityCollection> specialityCollections = null;
		List<String> specialities = null;
		Boolean response = false;
		try {
			specialityCollections = specialityRepository.findAll();
			specialities = new ArrayList<String>();
			for (String speciality : request.getSpeciality()) {
				Boolean specialityFound = false;
				for (SpecialityCollection specialityCollection : specialityCollections) {
					if (speciality.trim().equalsIgnoreCase(specialityCollection.getSpeciality())) {
						specialities.add(specialityCollection.getId());
						specialityFound = true;
						break;
					}
				}
				if (!specialityFound) {
					SpecialityCollection specialityCollection = new SpecialityCollection();
					specialityCollection.setSpeciality(speciality);
					specialityCollection = specialityRepository.save(specialityCollection);
					specialities.add(specialityCollection.getId());
				}
			}
			doctorCollection = doctorRepository.findByUserId(request.getDoctorId());
			doctorCollection.setSpecialities(specialities);
			doctorRepository.save(doctorCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	public Boolean addEditAchievement(DoctorAchievementAddEditRequest request) {
		DoctorCollection doctorCollection = null;
		Boolean response = false;
		try {
			doctorCollection = doctorRepository.findByUserId(request.getDoctorId());
			doctorCollection.setAchievements(request.getAchievements());
			doctorRepository.save(doctorCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	public Boolean addEditProfessionalStatement(String doctorId, String professionalStatement) {
		DoctorCollection doctorCollection = null;
		Boolean response = false;
		try {
			doctorCollection = doctorRepository.findByUserId(doctorId);
			doctorCollection.setProfessionalStatement(professionalStatement);
			doctorRepository.save(doctorCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	public Boolean addEditRegistrationDetail(DoctorRegistrationAddEditRequest request) {
		DoctorCollection doctorCollection = null;
		Boolean response = false;
		try {
			doctorCollection = doctorRepository.findByUserId(request.getDoctorId());
			doctorCollection.setRegistrationDetails(request.getRegistrationDetails());
			doctorRepository.save(doctorCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	public Boolean addEditExperienceDetail(DoctorExperienceAddEditRequest request) {
		DoctorCollection doctorCollection = null;
		Boolean response = false;
		try {
			doctorCollection = doctorRepository.findByUserId(request.getDoctorId());
			doctorCollection.setExperienceDetails(request.getExperienceDetails());
			doctorRepository.save(doctorCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

	@Override
	public String addEditProfilePicture(DoctorProfilePictureAddEditRequest request) {
		UserCollection userCollection = null;
		String response = "";
		try {
			userCollection = userRepository.findOne(request.getDoctorId());
			if (request.getImage() != null) {
				String path = "profile-image";
				// save image
				String imageurl = fileManager.saveImageAndReturnImageUrl(request.getImage(), path);
				userCollection.setImageUrl(imageurl);
				userCollection = userRepository.save(userCollection);
				response = userCollection.getImageUrl();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error Editing Doctor Profile");
		}
		return response;
	}

}
