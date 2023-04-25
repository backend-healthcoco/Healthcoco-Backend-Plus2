
package com.dpdocter.services.v2.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.AccessControl;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.Role;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.response.DoctorClinicProfileLookupResponse;
import com.dpdocter.response.UserRoleLookupResponse;
import com.dpdocter.response.v2.ClinicDoctorResponse;
import com.dpdocter.services.AccessControlServices;
import com.dpdocter.services.DynamicUIService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.v2.RegistrationService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service(value = "RegistrationServiceImplV2")
public class RegistrationServiceImpl implements RegistrationService {

	private static Logger logger = Logger.getLogger(RegistrationServiceImpl.class.getName());

	@Autowired
	private AccessControlServices accessControlServices;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	PushNotificationServices pushNotificationServices;

	@Autowired
	DynamicUIService dynamicUIService;

	@Value(value = "${jasper.print.consentForm.a4.fileName}")
	private String consentFormA4FileName;

	@Value(value = "${mail.signup.subject.activation}")
	private String signupSubject;

	@Value(value = "${mail.forgotPassword.subject}")
	private String forgotUsernamePasswordSub;

	@Value(value = "${mail.staffmember.account.verify.subject}")
	private String staffmemberAccountVerifySub;

	@Value(value = "${mail.add.existing.doctor.to.clinic.subject}")
	private String addExistingDoctorToClinicSub;

	@Value(value = "${mail.add.doctor.to.clinic.verify.subject}")
	private String addDoctorToClinicVerifySub;

	@Value(value = "${mail.add.feedback.subject}")
	private String addFeedbackSubject;

	@Value(value = "${mail.add.feedback.for.doctor.subject}")
	private String addFeedbackForDoctorSubject;

	@Value(value = "${patient.count}")
	private String patientCount;

	@Value(value = "${Register.checkPatientCount}")
	private String checkPatientCount;

	@Value(value = "${Signup.role}")
	private String role;

	@Value(value = "${Signup.DOB}")
	private String DOB;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${register.role.not.found}")
	private String roleNotFoundException;

	@Value(value = "${user.reminder.not.found}")
	private String reminderNotFoundException;

	@Value(value = "${patient.welcome.message}")
	private String patientWelcomeMessage;

	@Autowired
	DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Override
	@Transactional
	public List<ClinicDoctorResponse> getUsers(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, String role, Boolean active, String userState) {
		List<ClinicDoctorResponse> response = null;
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		try {
			List<DoctorClinicProfileLookupResponse> doctorClinicProfileLookupResponses = null;
			Criteria criteria = new Criteria();
			String defaultDoctorId = null;

			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}
			if (active) {
				criteria.and("isActivate").is(active);
			}
			if (!DPDoctorUtils.anyStringEmpty(userState)) {
				if (userState.equalsIgnoreCase("COMPLETED")) {
					criteria.and("user.userState").is("USERSTATECOMPLETE");
				} else {
					criteria.and("user.userState").is(userState);
				}
			}
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorClinicProfileCollection = doctorClinicProfileRepository
						.findByDoctorIdAndLocationId(new ObjectId(doctorId), new ObjectId(locationId));
				defaultDoctorId = (!DPDoctorUtils.anyStringEmpty(doctorClinicProfileCollection.getDefaultDoctorId())
						? doctorClinicProfileCollection.getDefaultDoctorId().toString()
						: doctorClinicProfileCollection.getDoctorId().toString());
				
			}
			CustomAggregationOperation projectionOperation = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id").append("doctorId", new BasicDBObject("$first", "$doctorId"))
							.append("locationId", new BasicDBObject("$first", "$locationId"))
							.append("isActivate", new BasicDBObject("$first", "$isActivate"))
							.append("isVerified", new BasicDBObject("$first", "$isVerified"))
							.append("discarded", new BasicDBObject("$first", "$discarded"))
							.append("patientInitial", new BasicDBObject("$first", "$patientInitial"))
							.append("patientCounter", new BasicDBObject("$first", "$patientCounter"))
							.append("appointmentBookingNumber",
									new BasicDBObject("$first", "$appointmentBookingNumber"))
							.append("consultationFee", new BasicDBObject("$first", "$consultationFee"))
							.append("revisitConsultationFee", new BasicDBObject("$first", "$revisitConsultationFee"))
							.append("appointmentSlot", new BasicDBObject("$first", "$appointmentSlot"))
							.append("workingSchedules", new BasicDBObject("$first", "$workingSchedules"))
							.append("facility", new BasicDBObject("$first", "$facility"))
							.append("noOfReviews", new BasicDBObject("$first", "$noOfReviews"))
							.append("noOfRecommenations", new BasicDBObject("$first", "$noOfRecommenations"))
							.append("timeZone", new BasicDBObject("$first", "$timeZone"))
							.append("rankingCount", new BasicDBObject("$first", "$rankingCount"))
							.append("location", new BasicDBObject("$first", "$location"))
							.append("hospital", new BasicDBObject("$first", "$hospital"))
							.append("doctor", new BasicDBObject("$first", "$doctor"))
							.append("user", new BasicDBObject("$first", "$user"))
							.append("packageType", new BasicDBObject("$first", "$packageType"))
							.append("createdTime", new BasicDBObject("$first", "$createdTime"))
							.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
							.append("createdBy", new BasicDBObject("$first", "$createdBy"))));

			if (size > 0) {
				doctorClinicProfileLookupResponses = mongoTemplate
						.aggregate(
								Aggregation.newAggregation(
										Aggregation.lookup("location_cl", "locationId", "_id", "location"),
										Aggregation.unwind("location"),
										Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
										Aggregation.unwind("user"),
										Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"),
										Aggregation.unwind("doctor"),
										Aggregation.lookup("user_role_cl", "doctorId", "userId", "userRoleCollection"),
										Aggregation.unwind("userRoleCollection"),
										Aggregation.match(criteria.and("userRoleCollection.locationId")
												.is(new ObjectId(locationId))),
										Aggregation.skip((long)(page) * size), Aggregation.limit(size)),
								DoctorClinicProfileCollection.class, DoctorClinicProfileLookupResponse.class)
						.getMappedResults();
			} else {
				doctorClinicProfileLookupResponses = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.lookup("location_cl", "locationId", "_id", "location"),
								Aggregation.unwind("location"),
								Aggregation.lookup("user_cl", "doctorId", "_id", "user"), Aggregation.unwind("user"),
								Aggregation.lookup("docter_cl", "doctorId", "userId", "doctor"),
								Aggregation.unwind("doctor"), Aggregation.match(criteria)),
						DoctorClinicProfileCollection.class, DoctorClinicProfileLookupResponse.class)
						.getMappedResults();
			}

			if (doctorClinicProfileLookupResponses != null) {
				response = new ArrayList<ClinicDoctorResponse>();
				for (DoctorClinicProfileLookupResponse doctorClinicProfileLookupResponse : doctorClinicProfileLookupResponses) {
					ClinicDoctorResponse clinicDoctorResponse = new ClinicDoctorResponse();
					if (doctorClinicProfileLookupResponse.getUser() != null) {
						BeanUtil.map(doctorClinicProfileLookupResponse.getUser(), clinicDoctorResponse);

						clinicDoctorResponse.setUserId(doctorClinicProfileLookupResponse.getUser().getId().toString());
						clinicDoctorResponse.setIsActivate(doctorClinicProfileLookupResponse.getIsActivate());
						// clinicDoctorResponse.setDiscarded(doctorClinicProfileLookupResponse.getDiscarded());
						clinicDoctorResponse.setIsShowDoctorInCalender(doctorClinicProfileLookupResponse.getIsShowDoctorInCalender());
						clinicDoctorResponse.setIsShowPatientNumber(doctorClinicProfileLookupResponse.getIsShowPatientNumber());

						Criteria roleCriteria = new Criteria();

						if (role.equalsIgnoreCase(RoleEnum.DOCTOR.getRole())) {
							roleCriteria = new Criteria("roleCollection.role")
									.in(Arrays.asList(RoleEnum.DOCTOR.getRole(), RoleEnum.CONSULTANT_DOCTOR.getRole()));
						} else if (role.equalsIgnoreCase(RoleEnum.STAFF.getRole())) {
							roleCriteria = new Criteria("roleCollection.role")
									.nin(Arrays.asList(RoleEnum.DOCTOR.getRole(), RoleEnum.CONSULTANT_DOCTOR.getRole(),
											RoleEnum.LOCATION_ADMIN.getRole(), RoleEnum.HOSPITAL_ADMIN.getRole(),
											RoleEnum.ADMIN.getRole(), RoleEnum.PATIENT.getRole(),
											RoleEnum.SUPER_ADMIN.getRole()));
						} else if (role.equalsIgnoreCase(RoleEnum.ADMIN.getRole())) {
							roleCriteria = new Criteria("roleCollection.role").in(Arrays
									.asList(RoleEnum.LOCATION_ADMIN.getRole(), RoleEnum.HOSPITAL_ADMIN.getRole()));
						} else if (role.equalsIgnoreCase("ALL")) {
							roleCriteria = new Criteria("roleCollection.role").nin(Arrays
									.asList(RoleEnum.LOCATION_ADMIN.getRole(), RoleEnum.HOSPITAL_ADMIN.getRole()));
						} else if (role.equalsIgnoreCase(RoleEnum.WEB_DOCTOR.getRole())) {

							roleCriteria = new Criteria("roleCollection.role")
									.in(Arrays.asList(RoleEnum.DOCTOR.getRole(), RoleEnum.CONSULTANT_DOCTOR.getRole(),
											RoleEnum.LOCATION_ADMIN.getRole(), RoleEnum.HOSPITAL_ADMIN.getRole(),
											RoleEnum.ADMIN.getRole()));
						}

						List<UserRoleLookupResponse> userRoleLookupResponses = mongoTemplate.aggregate(
								Aggregation.newAggregation(Aggregation.match(new Criteria("userId")
										.is(doctorClinicProfileLookupResponse.getDoctorId()).and("locationId")
										.is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId))),
										Aggregation.lookup("role_cl", "roleId", "_id", "roleCollection"),
										Aggregation.unwind("roleCollection"), Aggregation.match(roleCriteria)),
								UserRoleCollection.class, UserRoleLookupResponse.class).getMappedResults();

						if (userRoleLookupResponses != null && !userRoleLookupResponses.isEmpty()) {
							List<Role> roles = new ArrayList<>();
							for (UserRoleLookupResponse userRoleLookupResponse : userRoleLookupResponses) {
								Role roleObj = new Role();
								AccessControl accessControl = accessControlServices.getAccessControls(
										userRoleLookupResponse.getRoleCollection().getId(),
										userRoleLookupResponse.getRoleCollection().getLocationId(),
										userRoleLookupResponse.getRoleCollection().getHospitalId());
								BeanUtil.map(userRoleLookupResponse.getRoleCollection(), roleObj);
								roleObj.setAccessModules(accessControl.getAccessModules());
								roles.add(roleObj);
							}
							if (!DPDoctorUtils.anyStringEmpty(defaultDoctorId)) {
								if (doctorClinicProfileLookupResponse.getDoctorId().toString().equals(defaultDoctorId))
									clinicDoctorResponse.setIsDefault(true);

							}

							 clinicDoctorResponse.setRole(roles);
							response.add(clinicDoctorResponse);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}
}
