package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.AuditTrailData;
import com.dpdocter.collections.AuditLogCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserDeviceCollection;
import com.dpdocter.enums.AuditActionType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.repository.AuditLogRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.UserDeviceRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.AuditService;

import common.util.web.DPDoctorUtils;

@Service(value = "AuditServiceImpl")
public class AuditServiceImpl implements AuditService {

	private static Logger logger = Logger.getLogger(AuditServiceImpl.class.getName());
	@Autowired
	private AuditLogRepository auditLogRepository;

	@Autowired
	private UserDeviceRepository userDeviceRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Override
	public void addAuditData(AuditActionType auditActionType, String dataViewId, String collectionId, String patientId, String doctorId,
			String locationId, String hospitalId) {
		String doctorName = null;
		String patientName = null;
		UserCollection userCollection = null;
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		List<UserDeviceCollection> userDeviceCollections = null;
		UserCollection doctorCollection = userRepository.findById(new ObjectId(doctorId)).orElse(null);

		if (!DPDoctorUtils.anyStringEmpty(patientId)) {
			userCollection = userRepository.findById(new ObjectId(patientId)).orElse(null);
			userDeviceCollections = userDeviceRepository.findByUserIds(new ObjectId(patientId));
		}
		if (doctorCollection != null && !DPDoctorUtils.anyStringEmpty(doctorCollection.getTitle())
				&& !DPDoctorUtils.anyStringEmpty(doctorCollection.getFirstName())) {
			doctorName = doctorCollection.getTitle() + " " + doctorCollection.getFirstName();
		}
		if (locationId == null) {
			List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
					.findByDoctorId(new ObjectId(doctorId));

			if (!DPDoctorUtils.isNullOrEmptyList(doctorClinicProfileCollections)) {
				doctorClinicProfileCollection = doctorClinicProfileCollections.get(0);
				locationId = doctorClinicProfileCollection.getLocationId().toString();

			}
		}
		if (userCollection != null)
			patientName = userCollection.getFirstName();
		AuditLogCollection auditLogCollection = new AuditLogCollection();
		auditLogCollection.setAction(auditActionType);
		auditLogCollection.setCreatedTime(new Date());
		auditLogCollection.setUsername(patientName);
		auditLogCollection.setDataModifiedId(collectionId);
		auditLogCollection.setDoctorId(new ObjectId(doctorId));
		auditLogCollection.setLocationId(new ObjectId(locationId));
		auditLogCollection.setHospitalId(new ObjectId(hospitalId));
		auditLogCollection.setDataViewId(dataViewId);
		UserDeviceCollection userDeviceCollection = null;
		if (userDeviceCollections != null && !DPDoctorUtils.isNullOrEmptyList(userDeviceCollections)) {
			userDeviceCollection = userDeviceCollections.get(0);
			auditLogCollection.setDeviceName(userDeviceCollection.getDeviceType() + userDeviceCollection.getDeviceId());
		}
		switch (auditActionType) {

		case CREATE_APPOINTMENT:
			auditLogCollection.setDetails(doctorName + " scheduled appointment for " + patientName);
			break;
		case UPDATE_APPOINTMENT:
			auditLogCollection.setDetails(doctorName + " rescheduled appointment for " + patientName);
			break;
		case CANCLE_APPOINTMENT:
			auditLogCollection.setDetails(doctorName + " cancelled appointment for " + patientName);
			break;
		case CREATE_VISIT:
			auditLogCollection.setDetails(doctorName + " created visit for " + patientName);
			break;
		case UPDATE_VISIT:
			auditLogCollection.setDetails(doctorName + " updated visit for " + patientName);
			break;
		case DELETE_VISIT:
			auditLogCollection.setDetails(doctorName + " discarded visit for " + patientName);
			break;
		case CREATE_PRESCRIPTION:
			auditLogCollection.setDetails(doctorName + " created prescription for " + patientName);
			break;
		case UPDATE_PRESCRIPTION:
			auditLogCollection.setDetails(doctorName + " updated prescription for " + patientName);
			break;
		case DELETE_PRESCRIPTION:
			auditLogCollection.setDetails(doctorName + " discarded prescription for " + patientName);
			break;
		case CREATE_CLINICAL_NOTES:
			auditLogCollection.setDetails(doctorName + " created clinical notes for " + patientName);
			break;
		case UPDATE_CLINICAL_NOTES:
			auditLogCollection.setDetails(doctorName + " updated clinical notes for " + patientName);
			break;
		case DELETE_CLINICAL_NOTES:
			auditLogCollection.setDetails(doctorName + " discarded clinical notes for " + patientName);
			break;
		case CREATE_TREATMENT:
			auditLogCollection.setDetails(doctorName + " created treatment for " + patientName);
			break;
		case UPDATE_TREATMENT:
			auditLogCollection.setDetails(doctorName + " updated treatment for " + patientName);
			break;
		case DELETE_TREATMENT:
			auditLogCollection.setDetails(doctorName + " discarded treatment for " + patientName);
			break;
		case CREATE_DS:
			auditLogCollection.setDetails(doctorName + " created discharge summary for " + patientName);
			break;
		case UPDATE_DS:
			auditLogCollection.setDetails(doctorName + " updated discharge summary for " + patientName);
			break;
		case DELETE_DS:
			auditLogCollection.setDetails(doctorName + " discarded discharge summary for " + patientName);
			break;
		case CREATE_FILES:
			auditLogCollection.setDetails(doctorName + " created files for " + patientName);
			break;
		case UPDATE_FILES:
			auditLogCollection.setDetails(doctorName + " updated files for " + patientName);
			break;
		case DELETE_FILES:
			auditLogCollection.setDetails(doctorName + " discarded files for " + patientName);
			break;
		case CREATE_INVOICE:
			auditLogCollection.setDetails(doctorName + " created invoice for " + patientName);
			break;
		case UPDATE_INVOICE:
			auditLogCollection.setDetails(doctorName + " updated invoice for " + patientName);
			break;
		case DELETE_INVOICE:
			auditLogCollection.setDetails(doctorName + " discarded invoice for " + patientName);
			break;
		case CREATE_RECEIPT:
			auditLogCollection.setDetails(doctorName + " created receipt for " + patientName);
			break;
		case UPDATE_RECEIPT:
			auditLogCollection.setDetails(doctorName + " updated receipt for " + patientName);
			break;
		case DELETE_RECEIPT:
			auditLogCollection.setDetails(doctorName + " discarded receipt for " + patientName);
			break;
		case CREATE_PATIENT:
			auditLogCollection.setDetails(doctorName + " created patient " + patientName);
			break;
		case UPDATE_PATIENT:
			auditLogCollection.setDetails(doctorName + " updated patient " + patientName);
			break;
		case DELETE_PATIENT:
			auditLogCollection.setDetails(doctorName + " discarded patient " + patientName);
			break;
		case UPDATE_CLINIC_PROFILE:
			auditLogCollection.setDetails(doctorName + " updated clinic profile");
			break;
		case UPDATE_DOCTOR_PROFILE:
			auditLogCollection.setDetails(doctorName + " updated doctor profile");
			break;
		case UPDATE_PASSWORD:
			auditLogCollection.setDetails(doctorName + " updated password");
			break;
		case UPDATE_SUBSCRIPTION_PLAN:
			auditLogCollection.setDetails(doctorName + " updated subscription plan ");
			break;
		case UPDATE_COUNSULTATION_SETTING:
			auditLogCollection.setDetails(doctorName + " updated counsultation setting ");
			break;
		case ADD_DOCTOR:
			auditLogCollection.setDetails(doctorName + " added new doctor");
			break;
		case BUY_SMS_CREDITS:
			auditLogCollection.setDetails(doctorName + " buys sms credits ");
			break;
		case LOGIN:
			auditLogCollection.setDetails(doctorName + " login");
			break;
		case LOGOUT:
			auditLogCollection.setDetails(doctorName + " logout");
			break;
		default:
			break;
		}
		auditLogRepository.save(auditLogCollection);
	}

	@Override
	public List<AuditTrailData> getAuditTrailAppointmentData(String locationId, String hospitalId, String from,
			String to, int page, int size) {
		List<AuditTrailData> auditTrailAppointmentDatas = null;

		Criteria criteria = new Criteria();

		if (!DPDoctorUtils.anyStringEmpty(locationId))
			criteria.and("locationId").is(new ObjectId(locationId));

//		if (!DPDoctorUtils.anyStringEmpty(hospitalId))
//			criteria.and("hospitalId").is(new ObjectId(hospitalId));

		Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));
		DateTime fromDateTime = null, toDateTime = null;

		if (!DPDoctorUtils.anyStringEmpty(from)) {
			localCalendar.setTime(new Date(Long.parseLong(from)));
			int currentDay = localCalendar.get(Calendar.DATE);
			int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			int currentYear = localCalendar.get(Calendar.YEAR);

			fromDateTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
					DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

		}
		if (!DPDoctorUtils.anyStringEmpty(to)) {
			localCalendar.setTime(new Date(Long.parseLong(to)));
			int currentDay = localCalendar.get(Calendar.DATE);
			int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
			int currentYear = localCalendar.get(Calendar.YEAR);

			toDateTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
					DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

		}
		
		if (fromDateTime != null && toDateTime != null) {
			criteria.and("createdTime").gte(fromDateTime).lte(toDateTime);
		} else if (fromDateTime != null) {
			criteria.and("createdTime").gte(fromDateTime);
		} else if (toDateTime != null) {
			criteria.and("createdTime").lte(toDateTime);
		}
		if (size > 0) {
			auditTrailAppointmentDatas = mongoTemplate
					.aggregate(
							Aggregation.newAggregation(Aggregation.match(criteria),
									Aggregation.skip((long) (page) * size), Aggregation.limit(size)),
							AuditLogCollection.class, AuditTrailData.class)
					.getMappedResults();
		} else {
			auditTrailAppointmentDatas = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(Sort.Direction.DESC, "createdTime")),
					AuditLogCollection.class, AuditTrailData.class).getMappedResults();
		}
		return auditTrailAppointmentDatas;
	}

}
