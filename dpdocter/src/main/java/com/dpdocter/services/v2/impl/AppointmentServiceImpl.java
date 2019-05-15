 package com.dpdocter.services.v2.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;

import javax.mail.MessagingException;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.CustomAppointment;
import com.dpdocter.beans.Event;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.Slot;
import com.dpdocter.beans.User;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.beans.WorkingSchedule;
import com.dpdocter.beans.v2.Appointment;
import com.dpdocter.beans.v2.PatientCard;
import com.dpdocter.collections.AppointmentBookedSlotCollection;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.AppointmentWorkFlowCollection;
import com.dpdocter.collections.CustomAppointmentCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.RoleCollection;
import com.dpdocter.collections.SMSFormatCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserRoleCollection;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.AppointmentCreatedBy;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.AppointmentType;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.DoctorFacility;
import com.dpdocter.enums.QueueStatus;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.RoleEnum;
import com.dpdocter.enums.SMSContent;
import com.dpdocter.enums.SMSFormatType;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AppointmentBookedSlotRepository;
import com.dpdocter.repository.AppointmentRepository;
import com.dpdocter.repository.AppointmentWorkFlowRepository;
import com.dpdocter.repository.CityRepository;
import com.dpdocter.repository.ClinicalNotesRepository;
import com.dpdocter.repository.CustomAppointmentRepository;
import com.dpdocter.repository.DoctorClinicProfileRepository;
import com.dpdocter.repository.LandmarkLocalityRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientQueueRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.RecommendationsRepository;
import com.dpdocter.repository.RecordsRepository;
import com.dpdocter.repository.ReferenceRepository;
import com.dpdocter.repository.RoleRepository;
import com.dpdocter.repository.SMSFormatRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserResourceFavouriteRepository;
import com.dpdocter.repository.UserRoleRepository;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.request.EventRequest;
import com.dpdocter.request.PatientRegistrationRequest;
import com.dpdocter.response.SlotDataResponse;
import com.dpdocter.response.v2.AppointmentLookupResponse;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.LocationServices;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.RegistrationService;
import com.dpdocter.services.SMSServices;
import com.dpdocter.services.TransactionalManagementService;
import com.dpdocter.services.UserFavouriteService;
import com.dpdocter.services.v2.AppointmentService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;
import common.util.web.DateAndTimeUtility;
import common.util.web.Response;

@Service(value = "AppointmentServiceImplV2")
public class AppointmentServiceImpl implements AppointmentService {

	private static Logger logger = Logger.getLogger(AppointmentServiceImpl.class.getName());

	@Value(value = "${pdf.footer.text}")
	private String footerText;

	@Autowired
	private CityRepository cityRepository;

	@Autowired
	private LandmarkLocalityRepository landmarkLocalityRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private JasperReportService jasperReportService;

	@Autowired
	private DoctorClinicProfileRepository doctorClinicProfileRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private LocationServices locationServices;

	@Autowired
	private AppointmentBookedSlotRepository appointmentBookedSlotRepository;

	@Autowired
	private AppointmentWorkFlowRepository appointmentWorkFlowRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private SMSFormatRepository sMSFormatRepository;

	@Autowired
	private SMSServices sMSServices;

	@Autowired
	private PatientQueueRepository patientQueueRepository;

	@Autowired
	private SpecialityRepository specialityRepository;

	@Autowired
	private CustomAppointmentRepository customAppointmentRepository;

	@Autowired
	private PrintSettingsRepository printSettingsRepository;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private PrescriptionRepository prescriptionRepository;

	@Autowired
	private ClinicalNotesRepository clinicalNotesRepository;

	@Autowired
	private RecordsRepository recordsRepository;

	@Autowired
	private OTPService otpService;

	@Autowired
	private ReferenceRepository referenceRepository;

	@Value(value = "${Appointment.timeSlotIsBooked}")
	private String timeSlotIsBooked;

	@Value(value = "${Appointment.incorrectAppointmentId}")
	private String incorrectAppointmentId;

	@Value(value = "${Appoinment.appointmentDoesNotExist}")
	private String appointmentDoesNotExist;

	@Autowired
	private MailService mailService;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	PushNotificationServices pushNotificationServices;

	@Value(value = "${mail.appointment.cancel.subject}")
	private String appointmentCancelMailSubject;

	@Value(value = "${mail.appointment.confirm.to.doctor.subject}")
	private String appointmentConfirmToDoctorMailSubject;

	@Value(value = "${mail.appointment.request.to.doctor.subject}")
	private String appointmentRequestToDoctorMailSubject;

	@Value(value = "${mail.appointment.confirm.to.patient.subject}")
	private String appointmentConfirmToPatientMailSubject;

	@Value(value = "${mail.appointment.reschedule.to.doctor.subject}")
	private String appointmentRescheduleToDoctorMailSubject;

	@Value(value = "${mail.appointment.reschedule.to.patient.subject}")
	private String appointmentRescheduleToPatientMailSubject;

	@Autowired
	UserRoleRepository UserRoleRepository;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${patient.app.bit.link}")
	private String patientAppBitLink;

	@Value(value = "${jasper.print.dental.works.reports.fileName}")
	private String dentalWorksFormA4FileName;

	@Value(value = "${jasper.print.calender.a4.fileName}")
	private String calenderA4FileName;

	@Autowired
	private PatientVisitService patientVisitService;

	@Autowired
	private RecommendationsRepository recommendationsRepository;

	@Autowired
	private ESRegistrationService esRegistrationService;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	private UserResourceFavouriteRepository userResourceFavouriteRepository;

	@Autowired
	private UserFavouriteService userFavouriteService;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Override
	@Transactional
	public Appointment updateAppointment(final AppointmentRequest request, Boolean updateVisit,
			Boolean isStatusChange) {
		Appointment response = null;
		try {
			AppointmentLookupResponse appointmentLookupResponse = mongoTemplate.aggregate(Aggregation.newAggregation(
					Aggregation.match(new Criteria("appointmentId").is(request.getAppointmentId())),
					Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
					Aggregation.lookup("location_cl", "locationId", "_id", "location"), Aggregation.unwind("location")),
					AppointmentCollection.class, AppointmentLookupResponse.class).getUniqueMappedResult();
			if (appointmentLookupResponse != null) {
				AppointmentCollection appointmentCollection = new AppointmentCollection();
				BeanUtil.map(appointmentLookupResponse, appointmentCollection);
				PatientCard patientCard = null;
				List<PatientCard> patientCards = null;
				if (!DPDoctorUtils.allStringsEmpty(request.getPatientId())) {
					patientCards = mongoTemplate.aggregate(
							Aggregation.newAggregation(
									Aggregation.match(new Criteria("userId").is(new ObjectId(request.getPatientId()))
											.and("locationId").is(new ObjectId(request.getLocationId()))
											.and("hospitalId").is(new ObjectId(request.getHospitalId()))),
									Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user")),
							PatientCollection.class, PatientCard.class).getMappedResults();
					if (patientCards != null && !patientCards.isEmpty())
						patientCard = patientCards.get(0);
					appointmentCollection.setLocalPatientName(patientCard.getLocalPatientName());
				} else {
					appointmentCollection.setLocalPatientName(request.getLocalPatientName());
				}

				final String doctorName = appointmentLookupResponse.getDoctor().getTitle() + " "
						+ appointmentLookupResponse.getDoctor().getFirstName();

				if (!isStatusChange) {
					AppointmentWorkFlowCollection appointmentWorkFlowCollection = new AppointmentWorkFlowCollection();
					BeanUtil.map(appointmentLookupResponse, appointmentWorkFlowCollection);
					appointmentWorkFlowRepository.save(appointmentWorkFlowCollection);

					appointmentCollection.setState(request.getState());

					if (request.getState().getState().equals(AppointmentState.CANCEL.getState())) {
						if (request.getCancelledBy() != null) {
							if (request.getCancelledBy().equalsIgnoreCase(AppointmentCreatedBy.DOCTOR.getType())) {
								appointmentCollection.setCancelledBy(appointmentLookupResponse.getDoctor().getTitle()
										+ " " + appointmentLookupResponse.getDoctor().getFirstName());
								appointmentCollection.setCancelledByProfile(AppointmentCreatedBy.DOCTOR.getType());
							} else {
								appointmentCollection.setCancelledBy(patientCard.getLocalPatientName());
								appointmentCollection.setCancelledByProfile(AppointmentCreatedBy.PATIENT.getType());
							}
						}
						AppointmentBookedSlotCollection bookedSlotCollection = appointmentBookedSlotRepository
								.findByAppointmentId(request.getAppointmentId());
						if (bookedSlotCollection != null)
							appointmentBookedSlotRepository.delete(bookedSlotCollection);
					} else {
						if (request.getState().getState().equals(AppointmentState.RESCHEDULE.getState())) {
							appointmentCollection.setFromDate(request.getFromDate());
							appointmentCollection.setToDate(request.getToDate());
							appointmentCollection.setTime(request.getTime());
							appointmentCollection.setIsRescheduled(true);
							appointmentCollection.setState(AppointmentState.CONFIRM);
							AppointmentBookedSlotCollection bookedSlotCollection = appointmentBookedSlotRepository
									.findByAppointmentId(request.getAppointmentId());
							if (bookedSlotCollection != null) {
								bookedSlotCollection.setDoctorId(new ObjectId(request.getDoctorId()));
								bookedSlotCollection.setFromDate(appointmentCollection.getFromDate());
								bookedSlotCollection.setToDate(appointmentCollection.getToDate());
								bookedSlotCollection.setTime(request.getTime());
								bookedSlotCollection.setUpdatedTime(new Date());
								appointmentBookedSlotRepository.save(bookedSlotCollection);
							}
						}

						if (!request.getDoctorId().equalsIgnoreCase(appointmentLookupResponse.getDoctorId())) {
							appointmentCollection.setDoctorId(new ObjectId(request.getDoctorId()));
							User drCollection = mongoTemplate.aggregate(
									Aggregation.newAggregation(Aggregation
											.match(new Criteria("id").is(appointmentCollection.getDoctorId()))),
									UserCollection.class, User.class).getUniqueMappedResult();
							appointmentLookupResponse.setDoctor(drCollection);
						}
					}

					DoctorClinicProfileCollection clinicProfileCollection = doctorClinicProfileRepository
							.findByDoctorIdLocationId(appointmentCollection.getDoctorId(),
									appointmentCollection.getLocationId());

					appointmentCollection.setCategory(request.getCategory());
					appointmentCollection.setExplanation(request.getExplanation());
					appointmentCollection.setNotifyDoctorByEmail(request.getNotifyDoctorByEmail());
					appointmentCollection.setNotifyDoctorBySms(request.getNotifyDoctorBySms());
					appointmentCollection.setNotifyPatientByEmail(request.getNotifyPatientByEmail());
					appointmentCollection.setNotifyPatientBySms(request.getNotifyPatientByEmail());
					appointmentCollection.setUpdatedTime(new Date());
					appointmentCollection = appointmentRepository.save(appointmentCollection);

					if (updateVisit && !DPDoctorUtils.anyStringEmpty(appointmentCollection.getVisitId())) {
						if (appointmentCollection.getState().getState().equals("CANCEL"))
							patientVisitService.updateAppointmentTime(appointmentCollection.getVisitId(), null, null,
									null);
						else
							patientVisitService.updateAppointmentTime(appointmentCollection.getVisitId(),
									appointmentCollection.getAppointmentId(), appointmentCollection.getTime(),
									appointmentCollection.getFromDate());
					}
					SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
					String _24HourTime = String.format("%02d:%02d", appointmentCollection.getTime().getFromTime() / 60,
							appointmentCollection.getTime().getFromTime() % 60);
					SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
					SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
					if (clinicProfileCollection != null) {
						sdf.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
						_24HourSDF.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
						_12HourSDF.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
					} else {
						sdf.setTimeZone(TimeZone.getTimeZone("IST"));
						_24HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
						_12HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
					}

					Date _24HourDt = _24HourSDF.parse(_24HourTime);

					final String patientName = (patientCard != null && patientCard.getLocalPatientName() != null)
							? patientCard.getLocalPatientName().split(" ")[0]
							: (request.getLocalPatientName() != null ? request.getLocalPatientName().split(" ")[0]
									: "");
					final String appointmentId = appointmentCollection.getAppointmentId();
					final String dateTime = _12HourSDF.format(_24HourDt) + ", "
							+ sdf.format(appointmentCollection.getFromDate());

					final String clinicName = appointmentLookupResponse.getLocation().getLocationName();
					final String clinicContactNum = appointmentLookupResponse.getLocation().getClinicNumber() != null
							? appointmentLookupResponse.getLocation().getClinicNumber()
							: "";

					// sendSMS after appointment is saved
					final String id = appointmentCollection.getId().toString(),
							patientEmailAddress = patientCard != null ? patientCard.getEmailAddress() : null,
							patientMobileNumber = patientCard != null ? patientCard.getUser().getMobileNumber() : null,
							doctorEmailAddress = appointmentLookupResponse.getDoctor().getEmailAddress(),
							doctorMobileNumber = appointmentLookupResponse.getDoctor().getMobileNumber();
					final DoctorFacility facility = (clinicProfileCollection != null)
							? clinicProfileCollection.getFacility()
							: null;

					Executors.newSingleThreadExecutor().execute(new Runnable() {
						@Override
						public void run() {
							try {
								sendAppointmentEmailSmsNotification(false, request, id, appointmentId, doctorName,
										patientName, dateTime, clinicName, clinicContactNum, patientEmailAddress,
										patientMobileNumber, doctorEmailAddress, doctorMobileNumber, facility);
							} catch (MessagingException e) {
								e.printStackTrace();
							}
						}
					});
				} else if (request.getStatus() != null) {

					appointmentCollection.setCheckedInAt(request.getCheckedInAt());
					appointmentCollection.setEngagedAt(request.getEngagedAt());
					appointmentCollection.setCheckedOutAt(request.getCheckedOutAt());

					if (request.getStatus().name().equalsIgnoreCase(QueueStatus.SCHEDULED.name())) {
						appointmentCollection.setCheckedInAt(0);
						appointmentCollection.setEngagedAt(0);
						appointmentCollection.setWaitedFor(0);
						appointmentCollection.setCheckedOutAt(0);
						appointmentCollection.setEngagedFor(0);
					} else if (request.getStatus().name().equalsIgnoreCase(QueueStatus.ENGAGED.name())) {
						appointmentCollection.setWaitedFor(
								appointmentCollection.getEngagedAt() - appointmentCollection.getCheckedInAt());
					} else if (request.getStatus().name().equalsIgnoreCase(QueueStatus.CHECKED_OUT.name())) {
						appointmentCollection.setEngagedFor(
								appointmentCollection.getCheckedOutAt() - appointmentCollection.getEngagedAt());
					}
					appointmentCollection.setStatus(request.getStatus());
					appointmentCollection.setUpdatedTime(new Date());
					appointmentRepository.save(appointmentCollection);
				}

				response = new Appointment();
				BeanUtil.map(appointmentCollection, response);
				if (patientCard != null) {
					patientCard.getUser().setLocalPatientName(patientCard.getLocalPatientName());
					//patientCard.getUser().setLocationId(patientCard.getLocationId());
					//patientCard.getUser().setHospitalId(patientCard.getHospitalId());
					BeanUtil.map(patientCard.getUser(), patientCard);
					patientCard.setUserId(patientCard.getUserId());
					patientCard.setId(patientCard.getUserId());
					patientCard.setColorCode(patientCard.getUser().getColorCode());
					//patientCard.setImageUrl(getFinalImageURL(patientCard.getImageUrl()));
					patientCard.setThumbnailUrl(getFinalImageURL(patientCard.getThumbnailUrl()));
				} else {
					patientCard = new PatientCard();
					patientCard.setLocalPatientName(request.getLocalPatientName());
				}
				response.setPatient(patientCard);

				response.setDoctorName(doctorName);
				if (appointmentLookupResponse.getLocation() != null) {
					response.setLocationName(appointmentLookupResponse.getLocation().getLocationName());
					response.setClinicNumber(appointmentLookupResponse.getLocation().getClinicNumber());

					String address = (!DPDoctorUtils
							.anyStringEmpty(appointmentLookupResponse.getLocation().getStreetAddress())
									? appointmentLookupResponse.getLocation().getStreetAddress() + ", "
									: "")
							+ (!DPDoctorUtils
									.anyStringEmpty(appointmentLookupResponse.getLocation().getLandmarkDetails())
											? appointmentLookupResponse.getLocation().getLandmarkDetails() + ", "
											: "")
							+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getLocality())
									? appointmentLookupResponse.getLocation().getLocality() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getCity())
									? appointmentLookupResponse.getLocation().getCity() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getState())
									? appointmentLookupResponse.getLocation().getState() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getCountry())
									? appointmentLookupResponse.getLocation().getCountry() + ", "
									: "")
							+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getPostalCode())
									? appointmentLookupResponse.getLocation().getPostalCode()
									: "");

					if (address.charAt(address.length() - 2) == ',') {
						address = address.substring(0, address.length() - 2);
					}
					response.setClinicAddress(address);
					response.setLatitude(appointmentLookupResponse.getLocation().getLatitude());
					response.setLongitude(appointmentLookupResponse.getLocation().getLongitude());
				}
				List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
						.findByLocationId(new ObjectId(request.getLocationId()));
				for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
					pushNotificationServices.notifyUser(doctorClinicProfileCollection.getDoctorId().toString(),
							"Appointment updated.", ComponentType.APPOINTMENT_REFRESH.getType(), null, null);
				}
			} else {
				logger.error(incorrectAppointmentId);
				throw new BusinessException(ServiceError.InvalidInput, incorrectAppointmentId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Appointment addAppointment(final AppointmentRequest request, Boolean isFormattedResponseRequired) {
		Appointment response = null;
		DoctorClinicProfileCollection clinicProfileCollection = null;
		try {

			ObjectId doctorId = new ObjectId(request.getDoctorId()), locationId = new ObjectId(request.getLocationId()),
					hospitalId = new ObjectId(request.getHospitalId()), patientId = null;

			patientId = registerPatientIfNotRegistered(request, doctorId, locationId, hospitalId);

			UserCollection userCollection = userRepository.findOne(doctorId);
			LocationCollection locationCollection = locationRepository.findOne(locationId);
			PatientCard patientCard = null;
			List<PatientCard> patientCards = null;

			if (patientId != null) {
				patientCards = mongoTemplate.aggregate(
						Aggregation.newAggregation(
								Aggregation.match(new Criteria("userId").is(patientId).and("locationId").is(locationId)
										.and("hospitalId").is(hospitalId)),
								Aggregation.lookup("user_cl", "userId", "_id", "user"), Aggregation.unwind("user")),
						PatientCollection.class, PatientCard.class).getMappedResults();
				if (patientCards != null && !patientCards.isEmpty())
					patientCard = patientCards.get(0);
				request.setLocalPatientName(patientCard.getLocalPatientName());
			}
			AppointmentCollection appointmentCollection = null;

			if (request.getCreatedBy().equals(AppointmentCreatedBy.PATIENT)) {
				List<AppointmentCollection> appointmentCollections = mongoTemplate
						.aggregate(
								Aggregation
										.newAggregation(
												Aggregation
														.match(new Criteria("locationId")
																.is(new ObjectId(request.getLocationId()))
																.andOperator(
																		new Criteria().orOperator(
																				new Criteria("doctorId")
																						.is(new ObjectId(
																								request.getDoctorId())),
																				new Criteria("doctorIds")
																						.is(new ObjectId(
																								request.getDoctorId()))
																						.and("isCalenderBlocked")
																						.is(true)),
																		new Criteria().orOperator(
																				new Criteria("time.fromTime")
																						.lte(request.getTime()
																								.getFromTime())
																						.and("time.toTime")
																						.gt(request.getTime()
																								.getToTime()),
																				new Criteria("time.fromTime")
																						.lt(request.getTime()
																								.getFromTime())
																						.and("time.toTime")
																						.gte(request.getTime()
																								.getToTime())))
																.and("fromDate").is(request.getFromDate()).and("toDate")
																.is(request.getToDate()).and("state")
																.ne(AppointmentState.CANCEL.getState()))),

								AppointmentCollection.class, AppointmentCollection.class)
						.getMappedResults();
				if (appointmentCollections != null && !appointmentCollections.isEmpty()) {
					logger.error(timeSlotIsBooked);
					throw new BusinessException(ServiceError.NotAcceptable, timeSlotIsBooked);
				}
			}

			if (userCollection != null && locationCollection != null) {

				clinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdLocationId(doctorId, locationId);

				appointmentCollection = new AppointmentCollection();
				BeanUtil.map(request, appointmentCollection);
				appointmentCollection.setCreatedTime(new Date());
				appointmentCollection
						.setAppointmentId(UniqueIdInitial.APPOINTMENT.getInitial() + DPDoctorUtils.generateRandomId());

				SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");

				String _24HourTime = String.format("%02d:%02d", appointmentCollection.getTime().getFromTime() / 60,
						appointmentCollection.getTime().getFromTime() % 60);
				SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
				SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
				if (clinicProfileCollection != null) {
					sdf.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
					_24HourSDF.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
					_12HourSDF.setTimeZone(TimeZone.getTimeZone(clinicProfileCollection.getTimeZone()));
				} else {
					sdf.setTimeZone(TimeZone.getTimeZone("IST"));
					_24HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
					_12HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
				}

				Date _24HourDt = _24HourSDF.parse(_24HourTime);

				final String patientName = (patientCard != null && patientCard.getLocalPatientName() != null)
						? patientCard.getLocalPatientName().split(" ")[0]
						: (request.getLocalPatientName() != null ? request.getLocalPatientName().split(" ")[0] : "");
				final String appointmentId = appointmentCollection.getAppointmentId();
				final String dateTime = _12HourSDF.format(_24HourDt) + ", "
						+ sdf.format(appointmentCollection.getFromDate());
				final String doctorName = userCollection.getTitle() + " " + userCollection.getFirstName();
				final String clinicName = locationCollection.getLocationName(),
						clinicContactNum = locationCollection.getClinicNumber() != null
								? locationCollection.getClinicNumber()
								: "";

				if (request.getCreatedBy().equals(AppointmentCreatedBy.DOCTOR)) {
					appointmentCollection.setState(AppointmentState.CONFIRM);
					appointmentCollection.setCreatedBy(userCollection.getTitle() + " " + userCollection.getFirstName());
				} else {
					if (patientCard != null)
						appointmentCollection.setCreatedBy(patientCard.getLocalPatientName());
					else
						appointmentCollection.setCreatedBy(request.getLocalPatientName());

					if (clinicProfileCollection != null && clinicProfileCollection.getFacility() != null
							&& (clinicProfileCollection.getFacility().getType()
									.equalsIgnoreCase(DoctorFacility.IBS.getType()))) {
						appointmentCollection.setState(AppointmentState.CONFIRM);
					} else {
						appointmentCollection.setState(AppointmentState.NEW);
					}

					if (patientId != null)
						userFavouriteService.addRemoveFavourites(request.getPatientId(), request.getDoctorId(),
								Resource.DOCTOR.getType(), request.getLocationId(), false);
				}
				appointmentCollection = appointmentRepository.save(appointmentCollection);

				AppointmentBookedSlotCollection bookedSlotCollection = new AppointmentBookedSlotCollection();
				BeanUtil.map(appointmentCollection, bookedSlotCollection);
				bookedSlotCollection.setDoctorId(appointmentCollection.getDoctorId());
				bookedSlotCollection.setLocationId(appointmentCollection.getLocationId());
				bookedSlotCollection.setHospitalId(appointmentCollection.getHospitalId());
				bookedSlotCollection.setId(null);
				appointmentBookedSlotRepository.save(bookedSlotCollection);

				// sendSMS after appointment is saved

				final String id = appointmentCollection.getId().toString(),
						patientEmailAddress = patientCard != null ? patientCard.getEmailAddress() : null,
						patientMobileNumber = patientCard != null ? patientCard.getUser().getMobileNumber() : null,
						doctorEmailAddress = userCollection.getEmailAddress(),
						doctorMobileNumber = userCollection.getMobileNumber();
				final DoctorFacility facility = (clinicProfileCollection != null)
						? clinicProfileCollection.getFacility()
						: null;

				Executors.newSingleThreadExecutor().execute(new Runnable() {
					@Override
					public void run() {
						try {
							sendAppointmentEmailSmsNotification(true, request, id, appointmentId, doctorName,
									patientName, dateTime, clinicName, clinicContactNum, patientEmailAddress,
									patientMobileNumber, doctorEmailAddress, doctorMobileNumber, facility);
						} catch (MessagingException e) {
							e.printStackTrace();
						}

					}
				});

				if (appointmentCollection != null) {
					response = new Appointment();
					BeanUtil.map(appointmentCollection, response);

					if (isFormattedResponseRequired) {
						if (patientCard != null) {
							patientCard.getUser().setLocalPatientName(patientCard.getLocalPatientName());
							//patientCard.getUser().setLocationId(patientCard.getLocationId());
							//patientCard.getUser().setHospitalId(patientCard.getHospitalId());
							BeanUtil.map(patientCard.getUser(), patientCard);
							patientCard.setUserId(patientCard.getUserId());
							patientCard.setId(patientCard.getUserId());
							patientCard.setColorCode(patientCard.getUser().getColorCode());
							//patientCard.setImageUrl(getFinalImageURL(patientCard.getImageUrl()));
							patientCard.setThumbnailUrl(getFinalImageURL(patientCard.getThumbnailUrl()));
						} else {
							patientCard = new PatientCard();
							patientCard.setLocalPatientName(request.getLocalPatientName());
						}
						response.setPatient(patientCard);
						if (userCollection != null)
							response.setDoctorName(userCollection.getTitle() + " " + userCollection.getFirstName());
						if (locationCollection != null) {
							response.setLocationName(locationCollection.getLocationName());
							response.setClinicNumber(locationCollection.getClinicNumber());

							String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
									? locationCollection.getStreetAddress() + ", "
									: "")
									+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
											? locationCollection.getLandmarkDetails() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
											? locationCollection.getLocality() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
											? locationCollection.getCity() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
											? locationCollection.getState() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
											? locationCollection.getCountry() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
											? locationCollection.getPostalCode()
											: "");

							if (address.charAt(address.length() - 2) == ',') {
								address = address.substring(0, address.length() - 2);
							}

							response.setClinicAddress(address);
							response.setLatitude(locationCollection.getLatitude());
							response.setLongitude(locationCollection.getLongitude());
						}
					}
					List<DoctorClinicProfileCollection> doctorClinicProfileCollections = doctorClinicProfileRepository
							.findByLocationId(locationId);
					for (DoctorClinicProfileCollection doctorClinicProfileCollection : doctorClinicProfileCollections) {
						pushNotificationServices.notifyUser(doctorClinicProfileCollection.getDoctorId().toString(),
								"New appointment created.", ComponentType.APPOINTMENT_REFRESH.getType(), null, null);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private ObjectId registerPatientIfNotRegistered(AppointmentRequest request, ObjectId doctorId, ObjectId locationId,
			ObjectId hospitalId) {
		ObjectId patientId = null;
		if (request.getPatientId() == null || request.getPatientId().isEmpty()) {

			if (DPDoctorUtils.anyStringEmpty(request.getLocalPatientName())) {
				throw new BusinessException(ServiceError.InvalidInput, "Patient not selected");
			}
			PatientRegistrationRequest patientRegistrationRequest = new PatientRegistrationRequest();
			patientRegistrationRequest.setFirstName(request.getLocalPatientName());
			patientRegistrationRequest.setLocalPatientName(request.getLocalPatientName());
			patientRegistrationRequest.setMobileNumber(request.getMobileNumber());
			patientRegistrationRequest.setDoctorId(request.getDoctorId());
			patientRegistrationRequest.setLocationId(request.getLocationId());
			patientRegistrationRequest.setHospitalId(request.getHospitalId());
			RegisteredPatientDetails patientDetails = null;
			patientDetails = registrationService.registerNewPatient(patientRegistrationRequest);
			if (patientDetails != null) {
				request.setPatientId(patientDetails.getUserId());
			}
			transnationalService.addResource(new ObjectId(patientDetails.getUserId()), Resource.PATIENT, false);
			esRegistrationService.addPatient(registrationService.getESPatientDocument(patientDetails));
			patientId = new ObjectId(request.getPatientId());
		} else if (!DPDoctorUtils.anyStringEmpty(request.getPatientId())) {

			patientId = new ObjectId(request.getPatientId());
			PatientCollection patient = patientRepository.findByUserIdLocationIdAndHospitalId(patientId, locationId,
					hospitalId);
			if (patient == null) {
				PatientRegistrationRequest patientRegistrationRequest = new PatientRegistrationRequest();
				patientRegistrationRequest.setDoctorId(request.getDoctorId());
				patientRegistrationRequest.setLocalPatientName(request.getLocalPatientName());
				patientRegistrationRequest.setFirstName(request.getLocalPatientName());
				patientRegistrationRequest.setUserId(request.getPatientId());
				patientRegistrationRequest.setLocationId(request.getLocationId());
				patientRegistrationRequest.setHospitalId(request.getHospitalId());
				RegisteredPatientDetails patientDetails = registrationService
						.registerExistingPatient(patientRegistrationRequest, null);
				transnationalService.addResource(new ObjectId(patientDetails.getUserId()), Resource.PATIENT, false);
				esRegistrationService.addPatient(registrationService.getESPatientDocument(patientDetails));
			} else {
				List<ObjectId> consultantDoctorIds = patient.getConsultantDoctorIds();
				if (consultantDoctorIds == null)
					consultantDoctorIds = new ArrayList<ObjectId>();
				if (!consultantDoctorIds.contains(doctorId))
					consultantDoctorIds.add(doctorId);
				patient.setConsultantDoctorIds(consultantDoctorIds);
				patient.setUpdatedTime(new Date());
				patientRepository.save(patient);
			}
		}

		return patientId;
	}

	private void sendAppointmentEmailSmsNotification(Boolean isAddAppointment, AppointmentRequest request,
			String appointmentCollectionId, String appointmentId, String doctorName, String patientName,
			String dateTime, String clinicName, String clinicContactNum, String patientEmailAddress,
			String patientMobileNumber, String doctorEmailAddress, String doctorMobileNumber,
			DoctorFacility doctorFacility) throws MessagingException {

		/*
		 * sendAppointmentEmailSmsNotification(true, request,
		 * appointmentCollection.getId().toString(), appointmentId, doctorName,
		 * patientName, dateTime, clinicName, clinicContactNum,
		 * patientCard.getEmailAddress(), patientCard.getUser().getMobileNumber(),
		 * userCollection.getEmailAddress(), userCollection.getMobileNumber(),
		 * (clinicProfileCollection != null) ? clinicProfileCollection.getFacility() :
		 * null);
		 */

		if (isAddAppointment) {
			if (request.getCreatedBy().equals(AppointmentCreatedBy.DOCTOR)) {
				if (request.getNotifyDoctorByEmail() != null && request.getNotifyDoctorByEmail())
					sendEmail(doctorName, patientName, dateTime, clinicName,
							"CONFIRMED_APPOINTMENT_TO_DOCTOR_BY_PATIENT", doctorEmailAddress);
				if (request.getNotifyDoctorBySms() != null && request.getNotifyDoctorBySms()) {
					sendMsg(null, "CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(), request.getLocationId(),
							request.getHospitalId(), request.getDoctorId(), doctorMobileNumber, patientName,
							appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
				}
				sendPushNotification("CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(), doctorMobileNumber,
						patientName, appointmentCollectionId, appointmentId, dateTime, doctorName, clinicName,
						clinicContactNum);

				if (request.getNotifyPatientByEmail() != null && request.getNotifyPatientByEmail()
						&& patientEmailAddress != null)
					sendEmail(doctorName, patientName, dateTime, clinicName, "CONFIRMED_APPOINTMENT_TO_PATIENT",
							patientEmailAddress);
				if (request.getNotifyPatientBySms() != null && request.getNotifyPatientBySms()
						&& !DPDoctorUtils.anyStringEmpty(patientMobileNumber)) {
					sendMsg(SMSFormatType.CONFIRMED_APPOINTMENT.getType(), "CONFIRMED_APPOINTMENT_TO_PATIENT",
							request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
							request.getPatientId(), patientMobileNumber, patientName, appointmentId, dateTime,
							doctorName, clinicName, clinicContactNum);
				}
				if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
					sendPushNotification("CONFIRMED_APPOINTMENT_TO_PATIENT", request.getPatientId(),
							patientMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
							doctorName, clinicName, clinicContactNum);
			} else {
				if (doctorFacility != null
						&& (doctorFacility.getType().equalsIgnoreCase(DoctorFacility.IBS.getType()))) {
					sendEmail(doctorName, patientName, dateTime, clinicName,
							"CONFIRMED_APPOINTMENT_TO_DOCTOR_BY_PATIENT", doctorEmailAddress);
					sendMsg(null, "CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(), request.getLocationId(),
							request.getHospitalId(), request.getDoctorId(), doctorMobileNumber, patientName,
							appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
					if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
						sendMsg(SMSFormatType.CONFIRMED_APPOINTMENT.getType(), "CONFIRMED_APPOINTMENT_TO_PATIENT",
								request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
								request.getPatientId(), patientMobileNumber, patientName, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum);
					sendPushNotification("CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(), doctorMobileNumber,
							patientName, appointmentCollectionId, appointmentId, dateTime, doctorName, clinicName,
							clinicContactNum);
					if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
						sendPushNotification("CONFIRMED_APPOINTMENT_TO_PATIENT", request.getPatientId(),
								patientMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum);
				} else {
					sendEmail(doctorName, patientName, dateTime, clinicName, "CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR",
							doctorEmailAddress);
					sendMsg(null, "CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR", request.getDoctorId(),
							request.getLocationId(), request.getHospitalId(), request.getDoctorId(), doctorMobileNumber,
							patientName, appointmentId, dateTime, doctorName, clinicName, clinicContactNum);
					if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
						sendMsg(SMSFormatType.APPOINTMENT_SCHEDULE.getType(), "TENTATIVE_APPOINTMENT_TO_PATIENT",
								request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
								request.getPatientId(), patientMobileNumber, patientName, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum);
					sendPushNotification("CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR", request.getDoctorId(),
							doctorMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
							doctorName, clinicName, clinicContactNum);
					if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
						sendPushNotification("TENTATIVE_APPOINTMENT_TO_PATIENT", request.getPatientId(),
								patientMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum);
				}
			}
		} else {
			if (request.getState().getState().equals(AppointmentState.CANCEL.getState())) {
				if (request.getCancelledBy().equals(AppointmentCreatedBy.DOCTOR.getType())) {
					if (request.getNotifyDoctorByEmail() != null && request.getNotifyDoctorByEmail())
						sendEmail(doctorName, patientName, dateTime, clinicName,
								"CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR", doctorEmailAddress);

					if (request.getNotifyDoctorBySms() != null && request.getNotifyDoctorBySms()) {
						sendMsg(null, "CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR", request.getDoctorId(),
								request.getLocationId(), request.getHospitalId(), request.getDoctorId(),
								doctorMobileNumber, patientName, appointmentId, dateTime, doctorName, clinicName,
								clinicContactNum);
					}

					sendPushNotification("CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR", request.getDoctorId(),
							doctorMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
							doctorName, clinicName, clinicContactNum);

					if (request.getNotifyPatientByEmail() != null && request.getNotifyPatientByEmail()
							&& patientEmailAddress != null)
						sendEmail(doctorName, patientName, dateTime, clinicName,
								"CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR", patientEmailAddress);

					if (request.getNotifyPatientBySms() != null && request.getNotifyPatientBySms()
							&& !DPDoctorUtils.anyStringEmpty(patientMobileNumber)) {
						sendMsg(SMSFormatType.CANCEL_APPOINTMENT.getType(), "CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR",
								request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
								request.getPatientId(), patientMobileNumber, patientName, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum);
					}

					if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
						sendPushNotification("CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR", request.getPatientId(),
								patientMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum);
				} else {
					if (request.getState().getState().equals(AppointmentState.CANCEL.getState())) {
						sendMsg(null, "CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT", request.getDoctorId(),
								request.getLocationId(), request.getHospitalId(), request.getDoctorId(),
								doctorMobileNumber, patientName, appointmentId, dateTime, doctorName, clinicName,
								clinicContactNum);
						if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
							sendMsg(SMSFormatType.CANCEL_APPOINTMENT.getType(),
									"CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT", request.getDoctorId(),
									request.getLocationId(), request.getHospitalId(), request.getPatientId(),
									patientMobileNumber, patientName, appointmentId, dateTime, doctorName, clinicName,
									clinicContactNum);

						sendPushNotification("CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT", request.getDoctorId(),
								doctorMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum);
						if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
							sendPushNotification("CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT", request.getPatientId(),
									patientMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
									doctorName, clinicName, clinicContactNum);
						if (!DPDoctorUtils.anyStringEmpty(patientEmailAddress))
							sendEmail(doctorName, patientName, dateTime, clinicName,
									"CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT", patientEmailAddress);
						sendEmail(doctorName, patientName, dateTime, clinicName,
								"CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT", doctorEmailAddress);
					}
				}
			} else {
				if (request.getCreatedBy().getType().equals(AppointmentCreatedBy.DOCTOR.getType())) {
					if (request.getNotifyDoctorByEmail() != null && request.getNotifyDoctorByEmail())
						sendEmail(doctorName, patientName, dateTime, clinicName,
								"CONFIRMED_APPOINTMENT_TO_DOCTOR_BY_PATIENT", doctorEmailAddress);

					if (request.getNotifyDoctorBySms() != null && request.getNotifyDoctorBySms()) {
						if (request.getState().getState().equals(AppointmentState.CONFIRM.getState()))
							sendMsg(null, "CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),
									request.getLocationId(), request.getHospitalId(), request.getDoctorId(),
									doctorMobileNumber, patientName, appointmentId, dateTime, doctorName, clinicName,
									clinicContactNum);
						else
							sendMsg(null, "RESCHEDULE_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),
									request.getLocationId(), request.getHospitalId(), request.getDoctorId(),
									doctorMobileNumber, patientName, appointmentId, dateTime, doctorName, clinicName,
									clinicContactNum);
					}

					if (request.getState().getState().equals(AppointmentState.CONFIRM.getState()))
						sendPushNotification("CONFIRMED_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),
								doctorMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum);
					else
						sendPushNotification("RESCHEDULE_APPOINTMENT_TO_DOCTOR", request.getDoctorId(),
								doctorMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
								doctorName, clinicName, clinicContactNum);
				}
				if (request.getNotifyPatientByEmail() != null && request.getNotifyPatientByEmail()
						&& !DPDoctorUtils.allStringsEmpty(patientEmailAddress)) {

					sendEmail(doctorName, patientName, dateTime, clinicName, "CONFIRMED_APPOINTMENT_TO_PATIENT",
							patientEmailAddress);
				}
				if (request.getNotifyPatientBySms() != null && request.getNotifyPatientBySms()) {
					if (request.getState().getState().equals(AppointmentState.CONFIRM.getState()))
						if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
							sendMsg(SMSFormatType.CONFIRMED_APPOINTMENT.getType(), "CONFIRMED_APPOINTMENT_TO_PATIENT",
									request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
									request.getPatientId(), patientMobileNumber, patientName, appointmentId, dateTime,
									doctorName, clinicName, clinicContactNum);
						else if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
							sendMsg(SMSFormatType.APPOINTMENT_SCHEDULE.getType(), "RESCHEDULE_APPOINTMENT_TO_PATIENT",
									request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
									request.getPatientId(), patientMobileNumber, patientName, appointmentId, dateTime,
									doctorName, clinicName, clinicContactNum);
				}

				if (request.getState().getState().equals(AppointmentState.CONFIRM.getState())
						&& !DPDoctorUtils.anyStringEmpty(patientMobileNumber))
					sendPushNotification("CONFIRMED_APPOINTMENT_TO_PATIENT", request.getPatientId(),
							patientMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
							doctorName, clinicName, clinicContactNum);
				else if (!DPDoctorUtils.anyStringEmpty(patientMobileNumber))
					sendPushNotification("RESCHEDULE_APPOINTMENT_TO_PATIENT", request.getPatientId(),
							patientMobileNumber, patientName, appointmentCollectionId, appointmentId, dateTime,
							doctorName, clinicName, clinicContactNum);
			}
		}
	}

	private void sendPushNotification(String type, String userId, String mobileNumber, String patientName,
			String appointmentCollectionId, String appointmentId, String dateTime, String doctorName, String clinicName,
			String clinicContactNum) {

		if (DPDoctorUtils.anyStringEmpty(patientName))
			patientName = "";
		if (DPDoctorUtils.anyStringEmpty(appointmentId))
			appointmentId = "";
		if (DPDoctorUtils.anyStringEmpty(dateTime))
			dateTime = "";
		if (DPDoctorUtils.anyStringEmpty(doctorName))
			doctorName = "";
		if (DPDoctorUtils.anyStringEmpty(clinicName))
			clinicName = "";
		if (DPDoctorUtils.anyStringEmpty(clinicContactNum))
			clinicContactNum = "";

		String text = "";
		switch (type) {
		case "CONFIRMED_APPOINTMENT_TO_PATIENT": {
			text = "Your appointment with " + doctorName + (clinicName != "" ? ", " + clinicName : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "") + " has been confirmed @ " + dateTime
					+ ".";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "CONFIRMED_APPOINTMENT_TO_DOCTOR": {
			text = "Healthcoco! Your appointment with " + patientName + " has been scheduled @ " + dateTime
					+ (clinicName != "" ? " at " + clinicName : "") + ".";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR": {
			text = "Healthcoco! You have an appointment request from " + patientName + " for " + dateTime + " at "
					+ clinicName + ".";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "TENTATIVE_APPOINTMENT_TO_PATIENT": {
			text = "Your appointment @ " + dateTime + " with " + doctorName
					+ (clinicName != "" ? ", " + clinicName : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "") + " has been sent for confirmation.";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR": {
			text = "Your appointment" + " with " + patientName + " for " + dateTime + " at " + clinicName
					+ " has been cancelled as per your request.";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR": {
			text = "Your appointment @ " + dateTime + " has been cancelled by " + doctorName
					+ (clinicName != "" ? ", " + clinicName : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "") + ".";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT": {
			text = "Healthcoco! Your appointment" + " with " + patientName + " @ " + dateTime + " at " + clinicName
					+ ", has been cancelled by patient.";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT": {
			text = "Your appointment for " + dateTime + " with " + doctorName
					+ " has been cancelled as per your request.";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "APPOINTMENT_REMINDER_TO_PATIENT": {
			text = "You have an appointment @ " + dateTime + " with " + doctorName
					+ (clinicName != "" ? ", " + clinicName : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "") + ".";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "RESCHEDULE_APPOINTMENT_TO_PATIENT": {
			text = "Your appointment with " + doctorName + (clinicName != "" ? ", " + clinicName : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "") + " has been rescheduled @ " + dateTime
					+ ".";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		case "RESCHEDULE_APPOINTMENT_TO_DOCTOR": {
			text = "Your appointment with " + patientName + " has been rescheduled to " + dateTime + " at " + clinicName
					+ ".";
			pushNotificationServices.notifyUser(userId, text, ComponentType.APPOINTMENT.getType(),
					appointmentCollectionId, null);
		}
			break;

		default:
			break;
		}
	}

	private void sendEmail(String doctorName, String patientName, String dateTime, String clinicName, String type,
			String emailAddress) throws MessagingException {
		switch (type) {
		case "CONFIRMED_APPOINTMENT_TO_PATIENT": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"confirmAppointmentToPatient.vm");
			mailService.sendEmail(emailAddress, appointmentConfirmToPatientMailSubject + " " + dateTime, body, null);
		}
			break;

		case "CONFIRMED_APPOINTMENT_TO_DOCTOR_BY_PATIENT": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"confirmAppointmentToDoctorByPatient.vm");
			mailService.sendEmail(emailAddress, appointmentConfirmToDoctorMailSubject + " " + dateTime, body, null);
		}
			break;

		case "CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentRequestToDoctorByPatient.vm");
			mailService.sendEmail(emailAddress, appointmentRequestToDoctorMailSubject + " " + dateTime, body, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentCancelByDoctorToDoctor.vm");
			mailService.sendEmail(emailAddress, appointmentCancelMailSubject, body, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentCancelToPatientByDoctor.vm");
			mailService.sendEmail(emailAddress, appointmentCancelMailSubject, body, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentCancelByPatientToDoctor.vm");
			mailService.sendEmail(emailAddress, appointmentCancelMailSubject, body, null);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentCancelToPatientByPatient.vm");
			mailService.sendEmail(emailAddress, appointmentCancelMailSubject, body, null);
		}
			break;

		case "RESCHEDULE_APPOINTMENT_TO_PATIENT": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentCancelToPatientByDoctor.vm");
			mailService.sendEmail(emailAddress, appointmentRescheduleToPatientMailSubject + " " + dateTime, body, null);
		}
			break;

		case "RESCHEDULE_APPOINTMENT_TO_DOCTOR": {
			String body = mailBodyGenerator.generateAppointmentEmailBody(doctorName, patientName, dateTime, clinicName,
					"appointmentRescheduleByDoctorToDoctor.vm");
			mailService.sendEmail(emailAddress, appointmentRescheduleToDoctorMailSubject + " " + dateTime, body, null);
		}
			break;

		default:
			break;
		}

	}

	private void sendMsg(String formatType, String type, String doctorId, String locationId, String hospitalId,
			String userId, String mobileNumber, String patientName, String appointmentId, String dateTime,
			String doctorName, String clinicName, String clinicContactNum) {
		SMSFormatCollection smsFormatCollection = null;
		if (formatType != null) {
			smsFormatCollection = sMSFormatRepository.find(new ObjectId(doctorId), new ObjectId(locationId),
					new ObjectId(hospitalId), formatType);
		}

		SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
		smsTrackDetail.setDoctorId(new ObjectId(doctorId));
		smsTrackDetail.setHospitalId(new ObjectId(hospitalId));
		smsTrackDetail.setLocationId(new ObjectId(locationId));
		smsTrackDetail.setType("APPOINTMENT");
		SMSDetail smsDetail = new SMSDetail();
		smsDetail.setUserId(new ObjectId(userId));
		SMS sms = new SMS();

		if (DPDoctorUtils.anyStringEmpty(patientName))
			patientName = "";
		if (DPDoctorUtils.anyStringEmpty(appointmentId))
			appointmentId = "";
		if (DPDoctorUtils.anyStringEmpty(dateTime))
			dateTime = "";
		if (DPDoctorUtils.anyStringEmpty(doctorName))
			doctorName = "";
		if (DPDoctorUtils.anyStringEmpty(clinicName))
			clinicName = "";
		if (DPDoctorUtils.anyStringEmpty(clinicContactNum))
			clinicContactNum = "";
		if (smsFormatCollection != null) {
			if (type.equalsIgnoreCase("CONFIRMED_APPOINTMENT_TO_PATIENT")
					|| type.equalsIgnoreCase("TENTATIVE_APPOINTMENT_TO_PATIENT")
					|| type.equalsIgnoreCase("CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR")
					|| type.equalsIgnoreCase("APPOINTMENT_REMINDER_TO_PATIENT")
					|| type.equalsIgnoreCase("RESCHEDULE_APPOINTMENT_TO_PATIENT")) {
				if (!smsFormatCollection.getContent().contains(SMSContent.CLINIC_NAME.getContent())
						|| clinicName == null)
					clinicName = "";
				if (!smsFormatCollection.getContent().equals(SMSContent.CLINIC_CONTACT_NUMBER.getContent())
						|| clinicContactNum == null)
					clinicContactNum = "";
			}
		}
		String text = "";
		switch (type) {
		case "CONFIRMED_APPOINTMENT_TO_PATIENT": {
			text = "Your appointment with " + doctorName + (clinicName != "" ? ", " + clinicName : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "") + " has been confirmed @ " + dateTime
					+ ". Download Healthcoco App- " + patientAppBitLink;
			smsDetail.setUserName(patientName);
		}
			break;

		case "CONFIRMED_APPOINTMENT_TO_DOCTOR": {
			text = "Healthcoco! Your appointment with " + patientName + " has been scheduled @ " + dateTime
					+ (clinicName != "" ? " at " + clinicName : "") + ".";
			smsDetail.setUserName(doctorName);
		}
			break;

		case "CONFIRMED_APPOINTMENT_REQUEST_TO_DOCTOR": {
			text = "Healthcoco! You have an appointment request from " + patientName + " for " + dateTime + " at "
					+ clinicName + ".";
			smsDetail.setUserName(doctorName);
		}
			break;

		case "TENTATIVE_APPOINTMENT_TO_PATIENT": {
			text = "Your appointmen @ " + dateTime + " with " + doctorName + (clinicName != "" ? ", " + clinicName : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "")
					+ " has been sent for confirmation. Download Healthcoco App- " + patientAppBitLink;
			smsDetail.setUserName(patientName);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_DOCTOR": {
			text = "Your appointment" + " with " + patientName + " for " + dateTime + " at " + clinicName
					+ " has been cancelled as per your request.";
			smsDetail.setUserName(doctorName);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_DOCTOR": {
			text = "Your appointment @ " + dateTime + " has been cancelled by " + doctorName
					+ (clinicName != "" ? ", " + clinicName : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "")
					+ ". Request you to book again. Download Healthcoco App- " + patientAppBitLink;
			smsDetail.setUserName(patientName);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_DOCTOR_BY_PATIENT": {
			text = "Healthcoco! Your appointment" + " with " + patientName + " @ " + dateTime + " at " + clinicName
					+ ", has been cancelled by patient.";
			smsDetail.setUserName(doctorName);
		}
			break;

		case "CANCEL_APPOINTMENT_TO_PATIENT_BY_PATIENT": {
			text = "Your appointment for " + dateTime + " with " + doctorName
					+ " has been cancelled as per your request. Download Healthcoco App- " + patientAppBitLink;
			smsDetail.setUserName(patientName);
		}
			break;

		case "APPOINTMENT_REMINDER_TO_PATIENT": {
			text = "You have an appointment @ " + dateTime + " with " + doctorName
					+ (clinicName != "" ? ", " + clinicName : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "") + ". Download Healthcoco App- "
					+ patientAppBitLink;
			smsDetail.setUserName(patientName);
		}
			break;

		case "RESCHEDULE_APPOINTMENT_TO_PATIENT": {
			text = "Your appointment with " + doctorName + (clinicName != "" ? ", " + clinicName : "")
					+ (clinicContactNum != "" ? ", " + clinicContactNum : "") + " has been rescheduled @ " + dateTime
					+ ". Download Healthcoco App- " + patientAppBitLink;
			smsDetail.setUserName(patientName);
		}
			break;

		case "RESCHEDULE_APPOINTMENT_TO_DOCTOR": {
			text = "Your appointment with " + patientName + " has been rescheduled to " + dateTime + " at " + clinicName
					+ ".";
			smsDetail.setUserName(doctorName);
		}
			break;

		default:
			break;
		}

		sms.setSmsText(text);

		SMSAddress smsAddress = new SMSAddress();
		smsAddress.setRecipient(mobileNumber);
		sms.setSmsAddress(smsAddress);

		smsDetail.setSms(sms);
		smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
		List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
		smsDetails.add(smsDetail);
		smsTrackDetail.setSmsDetails(smsDetails);
		sMSServices.sendSMS(smsTrackDetail, true);
	}

	@Override
	@Transactional
	public Response<Appointment> getAppointments(String locationId, List<String> doctorId, String patientId, String from,
			String to, int page, int size, String updatedTime, String status, String sortBy, String fromTime,
			String toTime, Boolean isRegisteredPatientRequired, Boolean isWeb) {
		Response<Appointment> response = null;
		List<Appointment> appointments = null;
		try {
			long updatedTimeStamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("type").is(AppointmentType.APPOINTMENT.getType()).and("updatedTime")
					.gte(new Date(updatedTimeStamp)).and("isPatientDiscarded").ne(true);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));

			if (doctorId != null && !doctorId.isEmpty()) {
				List<ObjectId> doctorObjectIds = new ArrayList<ObjectId>();
				for (String id : doctorId)
					doctorObjectIds.add(new ObjectId(id));
				criteria.and("doctorId").in(doctorObjectIds);
			}

			if (!DPDoctorUtils.anyStringEmpty(patientId))
				criteria.and("patientId").is(new ObjectId(patientId));

			if (!DPDoctorUtils.anyStringEmpty(status))
				criteria.and("status").is(status.toUpperCase()).and("state").ne(AppointmentState.CANCEL.getState());

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));

			if (!DPDoctorUtils.anyStringEmpty(from)) {
				localCalendar.setTime(new Date(Long.parseLong(from)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime fromDateTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				criteria.and("fromDate").gte(fromDateTime);
			}
			if (!DPDoctorUtils.anyStringEmpty(to)) {
				localCalendar.setTime(new Date(Long.parseLong(to)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime toDateTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				criteria.and("toDate").lte(toDateTime);
			}

			if (!DPDoctorUtils.anyStringEmpty(fromTime))
				criteria.and("time.fromTime").is(Integer.parseInt(fromTime));

			if (!DPDoctorUtils.anyStringEmpty(toTime))
				criteria.and("time.toTime").is(Integer.parseInt(toTime));
			List<AppointmentLookupResponse> appointmentLookupResponses = null;

			SortOperation sortOperation = Aggregation.sort(new Sort(Direction.ASC, "fromDate", "time.fromTime"));

			if (!DPDoctorUtils.anyStringEmpty(status)) {
				if (status.equalsIgnoreCase(QueueStatus.SCHEDULED.toString())) {
					sortOperation = Aggregation.sort(new Sort(Direction.ASC, "time.fromTime"));
				}else if (status.equalsIgnoreCase(QueueStatus.WAITING.toString())) {
					sortOperation = Aggregation.sort(new Sort(Direction.ASC, "checkedInAt"));
				} else if (status.equalsIgnoreCase(QueueStatus.ENGAGED.toString())) {
					sortOperation = Aggregation.sort(new Sort(Direction.ASC, "engagedAt"));
				} else if (status.equalsIgnoreCase(QueueStatus.CHECKED_OUT.toString())) {
					sortOperation = Aggregation.sort(new Sort(Direction.ASC, "checkedOutAt"));
				}
			} else if (!DPDoctorUtils.anyStringEmpty(sortBy) && sortBy.equalsIgnoreCase("updatedTime")) {
				sortOperation = Aggregation.sort(new Sort(Direction.DESC, "updatedTime"));
			}

			Integer count = (int) mongoTemplate.count(new Query(criteria), AppointmentCollection.class);
			if(count != null && count > 0) {
				response = new Response<>();
				response.setCount(count);
				
				if (isWeb)
					appointments = getAppointmentsForWeb(criteria, sortOperation, page, size, appointments,
							appointmentLookupResponses);
				else {
					if (size > 0) {
						appointmentLookupResponses = mongoTemplate.aggregate(
								Aggregation
										.newAggregation(Aggregation.match(criteria),
												Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
												Aggregation.unwind("doctor"),
												Aggregation.lookup("location_cl", "locationId", "_id", "location"),
												Aggregation.unwind("location"),

												Aggregation.lookup("patient_cl", "patientId", "userId", "patientCard"),
												new CustomAggregationOperation(new BasicDBObject(
														"$unwind",
														new BasicDBObject(
																"path", "$patientCard").append("preserveNullAndEmptyArrays",
																		true))),
												new CustomAggregationOperation(
														new BasicDBObject("$redact",
																new BasicDBObject("$cond",
																		new BasicDBObject("if", new BasicDBObject("$eq",
																				Arrays.asList("$patientCard.locationId",
																						"$locationId")))
																								.append("then", "$$KEEP")
																								.append("else",
																										"$$PRUNE")))),

												Aggregation.lookup("user_cl", "patientId", "_id", "patientCard.user"),
												Aggregation.unwind("patientCard.user"), sortOperation,
												Aggregation.skip((page) * size), Aggregation.limit(size))
										.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build()),
								AppointmentCollection.class, AppointmentLookupResponse.class).getMappedResults();
					} else {
						appointmentLookupResponses = mongoTemplate.aggregate(
								Aggregation
										.newAggregation(Aggregation.match(criteria),
												Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
												Aggregation.unwind("doctor"),
												Aggregation.lookup("location_cl", "locationId", "_id", "location"),
												Aggregation.unwind("location"),
												Aggregation.lookup("patient_cl", "patientId", "userId", "patientCard"),
												new CustomAggregationOperation(new BasicDBObject(
														"$unwind",
														new BasicDBObject(
																"path", "$patientCard").append("preserveNullAndEmptyArrays",
																		true))),
												new CustomAggregationOperation(
														new BasicDBObject("$redact",
																new BasicDBObject("$cond",
																		new BasicDBObject("if", new BasicDBObject("$eq",
																				Arrays.asList("$patientCard.locationId",
																						"$locationId")))
																								.append("then", "$$KEEP")
																								.append("else",
																										"$$PRUNE")))),

												Aggregation.lookup("user_cl", "patientId", "_id", "patientCard.user"),
												Aggregation.unwind("patientCard.user"), sortOperation)
										.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build()),
								AppointmentCollection.class, AppointmentLookupResponse.class).getMappedResults();
					}

					if (appointmentLookupResponses != null && !appointmentLookupResponses.isEmpty()) {
						appointments = new ArrayList<Appointment>();

					for (AppointmentLookupResponse collection : appointmentLookupResponses) {
						Appointment appointment = new Appointment();
						PatientCard patientCard = null;
						if (collection.getType().getType().equals(AppointmentType.APPOINTMENT.getType())) {
							patientCard = collection.getPatientCard();
							if (patientCard != null) {
								patientCard.setId(patientCard.getUserId());

								if (patientCard.getUser() != null) {
									patientCard.setColorCode(patientCard.getUser().getColorCode());
									patientCard.setMobileNumber(patientCard.getUser().getMobileNumber());
								}
								//patientCard.setImageUrl(getFinalImageURL(patientCard.getImageUrl()));
								patientCard.setThumbnailUrl(getFinalImageURL(patientCard.getThumbnailUrl()));

							}
						}
						BeanUtil.map(collection, appointment);
						appointment.setPatient(patientCard);
						appointment.setLocalPatientName(patientCard.getLocalPatientName());
						if (collection.getDoctor() != null) {
							appointment.setDoctorName(
									collection.getDoctor().getTitle() + " " + collection.getDoctor().getFirstName());
						}
							if (collection.getLocation() != null) {
								appointment.setLocationName(collection.getLocation().getLocationName());
								appointment.setClinicNumber(collection.getLocation().getClinicNumber());

								String address = (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getStreetAddress())
										? collection.getLocation().getStreetAddress() + ", "
										: "")
										+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getLandmarkDetails())
												? collection.getLocation().getLandmarkDetails() + ", "
												: "")
										+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getLocality())
												? collection.getLocation().getLocality() + ", "
												: "")
										+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getCity())
												? collection.getLocation().getCity() + ", "
												: "")
										+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getState())
												? collection.getLocation().getState() + ", "
												: "")
										+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getCountry())
												? collection.getLocation().getCountry() + ", "
												: "")
										+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getPostalCode())
												? collection.getLocation().getPostalCode()
												: "");

								if (address.charAt(address.length() - 2) == ',') {
									address = address.substring(0, address.length() - 2);
								}

								appointment.setClinicAddress(address);
								appointment.setLatitude(collection.getLocation().getLatitude());
								appointment.setLongitude(collection.getLocation().getLongitude());
							}
							appointments.add(appointment);
						}
					}
				}
				response.setDataList(appointments);
			}				
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	private List<Appointment> getAppointmentsForWeb(Criteria criteria, SortOperation sortOperation, int page, int size,
			List<Appointment> response, List<AppointmentLookupResponse> appointmentLookupResponses) {

		CustomAggregationOperation projectOperation = new CustomAggregationOperation(new BasicDBObject("$project",
				new BasicDBObject("_id", "$_id").append("doctorId", "$doctorId").append("locationId", "$locationId")
						.append("hospitalId", "$hospitalId").append("patientId", "$patientId").append("time", "$time")
						.append("state", "$state").append("isRescheduled", "$isRescheduled")
						.append("fromDate", "$fromDate").append("toDate", "$toDate")
						.append("appointmentId", "$appointmentId").append("subject", "$subject")
						.append("explanation", "$explanation").append("type", "$type")
						.append("isCalenderBlocked", "$isCalenderBlocked")
						.append("isFeedbackAvailable", "$isFeedbackAvailable").append("isAllDayEvent", "$isAllDayEvent")
						.append("doctorName",
								new BasicDBObject("$concat", Arrays.asList("$doctor.title", " ", "$doctor.firstName")))
						.append("cancelledBy", "$cancelledBy").append("notifyPatientBySms", "$notifyPatientBySms")
						.append("notifyPatientByEmail", "$notifyPatientByEmail")
						.append("notifyDoctorBySms", "$notifyDoctorBySms")
						.append("notifyDoctorByEmail", "$notifyDoctorByEmail").append("visitId", "$visitId")
						.append("status", "$status").append("waitedFor", "$waitedFor")
						.append("engagedFor", "$engagedFor").append("engagedAt", "$engagedAt")
						.append("checkedInAt", "$checkedInAt").append("checkedOutAt", "$checkedOutAt")
						.append("count", "$count").append("category", "$category")
						.append("cancelledByProfile", "$cancelledByProfile")
						.append("adminCreatedTime", "$adminCreatedTime").append("createdTime", "$createdTime")
						.append("updatedTime", "$updatedTime").append("createdBy", "$createdBy")
						.append("isCreatedByPatient", "$isCreatedByPatient")
						.append("patient._id", "$patientCard.userId").append("patient.userId", "$patientCard.userId")
						.append("patient.localPatientName", "$patientCard.localPatientName")
						.append("patient.PID", "$patientCard.PID")
						.append("patient.PNUM", "$patientCard.PNUM")
						.append("patient.imageUrl", new BasicDBObject("$cond",
								new BasicDBObject(
										"if", new BasicDBObject("eq", Arrays.asList("$patientCard.imageUrl", null)))
												.append("then",
														new BasicDBObject("$concat",
																Arrays.asList(imagePath, "$patientCard.imageUrl")))
												.append("else", null)))
						.append("patient.thumbnailUrl", new BasicDBObject("$cond",
								new BasicDBObject("if",
										new BasicDBObject("eq", Arrays.asList("$patientCard.thumbnailUrl", null)))
												.append("then",
														new BasicDBObject("$concat",
																Arrays.asList(imagePath, "$patientCard.thumbnailUrl")))
												.append("else", null)))
						.append("patient.mobileNumber", "$patientUser.mobileNumber")
						.append("patient.colorCode", "$patientUser.colorCode")));

		CustomAggregationOperation groupOperation = new CustomAggregationOperation(new BasicDBObject("$group",
				new BasicDBObject("_id", "$_id").append("doctorId", new BasicDBObject("$first", "$doctorId"))
						.append("locationId", new BasicDBObject("$first", "$locationId"))
						.append("hospitalId", new BasicDBObject("$first", "$hospitalId"))
						.append("patientId", new BasicDBObject("$first", "$patientId"))
						.append("time", new BasicDBObject("$first", "$time"))
						.append("state", new BasicDBObject("$first", "$state"))
						.append("isRescheduled", new BasicDBObject("$first", "$isRescheduled"))
						.append("fromDate", new BasicDBObject("$first", "$fromDate"))
						.append("toDate", new BasicDBObject("$first", "$toDate"))
						.append("appointmentId", new BasicDBObject("$first", "$appointmentId"))
						.append("subject", new BasicDBObject("$first", "$subject"))
						.append("explanation", new BasicDBObject("$first", "$explanation"))
						.append("type", new BasicDBObject("$first", "$type"))
						.append("isCalenderBlocked", new BasicDBObject("$first", "$isCalenderBlocked"))
						.append("isFeedbackAvailable", new BasicDBObject("$first", "$isFeedbackAvailable"))
						.append("isAllDayEvent", new BasicDBObject("$first", "$isAllDayEvent"))
						.append("doctorName", new BasicDBObject("$first", "$doctorName"))
						.append("cancelledBy", new BasicDBObject("$first", "$cancelledBy"))
						.append("notifyPatientBySms", new BasicDBObject("$first", "$notifyPatientBySms"))
						.append("notifyPatientByEmail", new BasicDBObject("$first", "$notifyPatientByEmail"))
						.append("notifyDoctorBySms", new BasicDBObject("$first", "$notifyDoctorBySms"))
						.append("notifyDoctorByEmail", new BasicDBObject("$first", "$notifyDoctorByEmail"))
						.append("visitId", new BasicDBObject("$first", "$visitId"))
						.append("status", new BasicDBObject("$first", "$status"))
						.append("waitedFor", new BasicDBObject("$first", "$waitedFor"))
						.append("engagedFor", new BasicDBObject("$first", "$engagedFor"))
						.append("engagedAt", new BasicDBObject("$first", "$engagedAt"))
						.append("checkedInAt", new BasicDBObject("$first", "$checkedInAt"))
						.append("checkedOutAt", new BasicDBObject("$first", "$checkedOutAt"))
						.append("count", new BasicDBObject("$first", "$count"))
						.append("category", new BasicDBObject("$first", "$category"))
						.append("cancelledByProfile", new BasicDBObject("$first", "$cancelledByProfile"))
						.append("adminCreatedTime", new BasicDBObject("$first", "$adminCreatedTime"))
						.append("createdTime", new BasicDBObject("$first", "$createdTime"))
						.append("updatedTime", new BasicDBObject("$first", "$updatedTime"))
						.append("isCreatedByPatient", new BasicDBObject("$first", "$isCreatedByPatient"))
						.append("createdBy", new BasicDBObject("$first", "$createdBy"))
						.append("patient", new BasicDBObject("$first", "$patient"))));

		if (size > 0) {
			response = mongoTemplate
					.aggregate(
							Aggregation
									.newAggregation(Aggregation.match(criteria),
											Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
											Aggregation.unwind("doctor"),
											Aggregation.lookup("patient_cl", "patientId", "userId", "patientCard"),
											new CustomAggregationOperation(new BasicDBObject(
													"$unwind",
													new BasicDBObject(
															"path", "$patientCard").append("preserveNullAndEmptyArrays",
																	true))),
											new CustomAggregationOperation(
													new BasicDBObject("$redact",
															new BasicDBObject("$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$patientCard.locationId",
																					"$locationId")))
																							.append("then", "$$KEEP")
																							.append("else",
																									"$$PRUNE")))),

											Aggregation.lookup("user_cl", "patientId", "_id", "patientUser"),
											Aggregation.unwind("patientUser"), projectOperation, groupOperation,
											sortOperation, Aggregation.skip((page) * size), Aggregation.limit(size))
									.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build()),
							AppointmentCollection.class, Appointment.class)
					.getMappedResults();
		} else {
			response = mongoTemplate
					.aggregate(
							Aggregation
									.newAggregation(Aggregation.match(criteria),
											Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
											Aggregation.unwind("doctor"),
											Aggregation.lookup("patient_cl", "patientId", "userId", "patientCard"),
											new CustomAggregationOperation(new BasicDBObject(
													"$unwind",
													new BasicDBObject(
															"path", "$patientCard").append("preserveNullAndEmptyArrays",
																	true))),
											new CustomAggregationOperation(
													new BasicDBObject("$redact",
															new BasicDBObject("$cond",
																	new BasicDBObject("if", new BasicDBObject("$eq",
																			Arrays.asList("$patientCard.locationId",
																					"$locationId")))
																							.append("then", "$$KEEP")
																							.append("else",
																									"$$PRUNE")))),

											Aggregation.lookup("user_cl", "patientId", "_id", "patientUser"),
											Aggregation.unwind("patientUser"), projectOperation, groupOperation,
											sortOperation)
									.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build()),
							AppointmentCollection.class, Appointment.class)
					.getMappedResults();
		}
		return response;
	}

	@Override
	@Transactional
	public Response<Object> getPatientAppointments(String locationId, String doctorId, String patientId, String from,
			String to, int page, int size, String updatedTime) {
		Response<Object> response = new Response<Object>();
		List<Appointment> appointments = null;
		List<AppointmentLookupResponse> appointmentLookupResponses = null;
		try {
			long updatedTimeStamp = Long.parseLong(updatedTime);
			Criteria criteria = new Criteria("type").is(AppointmentType.APPOINTMENT.getType()).and("updatedTime")
					.gte(new Date(updatedTimeStamp)).and("isPatientDiscarded").is(false);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));

			if (doctorId != null)
				criteria.and("doctorId").is(new ObjectId(doctorId));

			if (!DPDoctorUtils.anyStringEmpty(patientId))
				criteria.and("patientId").is(new ObjectId(patientId));

			Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone("IST"));

			if (!DPDoctorUtils.anyStringEmpty(from)) {
				localCalendar.setTime(new Date(Long.parseLong(from)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime fromDateTime = new DateTime(currentYear, currentMonth, currentDay, 0, 0, 0,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				criteria.and("fromDate").gte(fromDateTime);
			}
			if (!DPDoctorUtils.anyStringEmpty(to)) {
				localCalendar.setTime(new Date(Long.parseLong(to)));
				int currentDay = localCalendar.get(Calendar.DATE);
				int currentMonth = localCalendar.get(Calendar.MONTH) + 1;
				int currentYear = localCalendar.get(Calendar.YEAR);

				DateTime toDateTime = new DateTime(currentYear, currentMonth, currentDay, 23, 59, 59,
						DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

				criteria.and("toDate").lte(toDateTime);
			}

			long count = mongoTemplate.count(new Query(criteria), AppointmentCollection.class);
			if (count > 0) {
				response.setData(count);
				if (size > 0) {
					appointmentLookupResponses = mongoTemplate.aggregate(
							Aggregation.newAggregation(Aggregation.match(criteria),
									Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
									Aggregation.unwind("doctor"),
									Aggregation.lookup("location_cl", "locationId", "_id", "location"),
									Aggregation.unwind("location"),
									Aggregation.sort(new Sort(Direction.ASC, "fromDate", "time.fromTime")),
									Aggregation.skip((page) * size), Aggregation.limit(size)),
							AppointmentCollection.class, AppointmentLookupResponse.class).getMappedResults();
				} else {
					appointmentLookupResponses = mongoTemplate.aggregate(
							Aggregation.newAggregation(Aggregation.match(criteria),
									Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"),
									Aggregation.unwind("doctor"),
									Aggregation.lookup("location_cl", "locationId", "_id", "location"),
									Aggregation.unwind("location"),
									Aggregation.sort(new Sort(Direction.ASC, "fromDate", "time.fromTime"))),
							AppointmentCollection.class, AppointmentLookupResponse.class).getMappedResults();
				}

				if (appointmentLookupResponses != null) {
					appointments = new ArrayList<Appointment>();
					for (AppointmentLookupResponse collection : appointmentLookupResponses) {
						Appointment appointment = new Appointment();
						BeanUtil.map(collection, appointment);
						if (collection.getDoctor() != null) {
							appointment.setDoctorName(
									collection.getDoctor().getTitle() + " " + collection.getDoctor().getFirstName());
						}
						if (collection.getLocation() != null) {
							appointment.setLocationName(collection.getLocation().getLocationName());
							appointment.setClinicNumber(collection.getLocation().getClinicNumber());
							String address = (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getStreetAddress())
									? collection.getLocation().getStreetAddress() + ", "
									: "")
									+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getLandmarkDetails())
											? collection.getLocation().getLandmarkDetails() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getLocality())
											? collection.getLocation().getLocality() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getCity())
											? collection.getLocation().getCity() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getState())
											? collection.getLocation().getState() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getCountry())
											? collection.getLocation().getCountry() + ", "
											: "")
									+ (!DPDoctorUtils.anyStringEmpty(collection.getLocation().getPostalCode())
											? collection.getLocation().getPostalCode()
											: "");

							if (address.charAt(address.length() - 2) == ',') {
								address = address.substring(0, address.length() - 2);
							}
							appointment.setClinicAddress(address);
							appointment.setLatitude(collection.getLocation().getLatitude());
							appointment.setLongitude(collection.getLocation().getLongitude());
						}
						appointments.add(appointment);
					}
				}
				response.setDataList(appointments);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	
	/*
	 * slots are divided in such a way that we slice available time from 'fromTime
	 * of working hours' to 'start time of booked slots' & not available 'start Time
	 * of booked slotss' to 'end time of booked slots' & assign startTime = end time
	 * of booked slots. After booked slots completed again slicing available time
	 * from 'endTime of last booked slots i.e. now start time' to 'to Time of
	 * working hours
	 */
	@Override
	@Transactional
	public SlotDataResponse getTimeSlots(String doctorId, String locationId, Date date, Boolean isPatient) {
		DoctorClinicProfileCollection doctorClinicProfileCollection = null;
		List<Slot> slotResponse = null;
		SlotDataResponse response = null;
		try {
			ObjectId doctorObjectId = null, locationObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);

			doctorClinicProfileCollection = doctorClinicProfileRepository.findByDoctorIdLocationId(doctorObjectId,
					locationObjectId);
			if (doctorClinicProfileCollection != null) {

				if (!isPatient) {
					UserRoleCollection userRoleCollection = userRoleRepository.findByUserIdLocationId(doctorObjectId,
							locationObjectId);
					if (userRoleCollection != null) {
						RoleCollection roleCollection = roleRepository.findOne(userRoleCollection.getId());
						if (roleCollection != null)
							if (roleCollection.getRole().equalsIgnoreCase(RoleEnum.RECEPTIONIST_NURSE.getRole())
									|| roleCollection.getRole().equalsIgnoreCase("RECEPTIONIST")) {
								throw new BusinessException(ServiceError.NotAuthorized,
										"You are not authorized to have slots.");
							}
					}
				}
				Integer startTime = 0, endTime = 0;
				float slotTime = 0;
				SimpleDateFormat sdf = new SimpleDateFormat("EEEEE");
				sdf.setTimeZone(TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone()));
				String day = sdf.format(date);
				if (doctorClinicProfileCollection.getWorkingSchedules() != null
						&& doctorClinicProfileCollection.getAppointmentSlot() != null) {
					slotTime = doctorClinicProfileCollection.getAppointmentSlot().getTime();
					response = new SlotDataResponse();
					response.setAppointmentSlot(doctorClinicProfileCollection.getAppointmentSlot());
					slotResponse = new ArrayList<Slot>();
					List<WorkingHours> workingHours = null;
					for (WorkingSchedule workingSchedule : doctorClinicProfileCollection.getWorkingSchedules()) {
						if (workingSchedule.getWorkingDay().getDay().equalsIgnoreCase(day)) {
							workingHours = workingSchedule.getWorkingHours();
						}
					}
					if (workingHours != null && !workingHours.isEmpty()) {

						Calendar localCalendar = Calendar
								.getInstance(TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone()));
						localCalendar.setTime(date);
						int dayOfDate = localCalendar.get(Calendar.DATE);
						int monthOfDate = localCalendar.get(Calendar.MONTH) + 1;
						int yearOfDate = localCalendar.get(Calendar.YEAR);

						DateTime start = new DateTime(yearOfDate, monthOfDate, dayOfDate, 0, 0, 0, DateTimeZone
								.forTimeZone(TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone())));
						DateTime end = new DateTime(yearOfDate, monthOfDate, dayOfDate, 23, 59, 59, DateTimeZone
								.forTimeZone(TimeZone.getTimeZone(doctorClinicProfileCollection.getTimeZone())));
						List<AppointmentBookedSlotCollection> bookedSlots = appointmentBookedSlotRepository
								.findByDoctorLocationId(doctorObjectId, locationObjectId, start, end,
										new Sort(Direction.ASC, "time.fromTime"));
						int i = 0;
						for (WorkingHours hours : workingHours) {
							startTime = hours.getFromTime();
							endTime = hours.getToTime();

							if (bookedSlots != null && !bookedSlots.isEmpty()) {
								while (i < bookedSlots.size()) {
									AppointmentBookedSlotCollection bookedSlot = bookedSlots.get(i);
									if (endTime > startTime) {
										if (bookedSlot.getTime().getFromTime() >= startTime
												|| bookedSlot.getTime().getToTime() >= endTime) {
											if (!bookedSlot.getFromDate().equals(bookedSlot.getToDate())) {
												if (bookedSlot.getIsAllDayEvent()) {
													if (bookedSlot.getFromDate().equals(date))
														bookedSlot.getTime().setToTime(719);
													if (bookedSlot.getToDate().equals(date))
														bookedSlot.getTime().setFromTime(0);
												}
											}
											List<Slot> slots = DateAndTimeUtility.sliceTime(startTime,
													bookedSlot.getTime().getFromTime(), Math.round(slotTime), true);
											if (slots != null)
												slotResponse.addAll(slots);

											slots = DateAndTimeUtility.sliceTime(bookedSlot.getTime().getFromTime(),
													bookedSlot.getTime().getToTime(), Math.round(slotTime), false);
											if (slots != null)
												slotResponse.addAll(slots);
											startTime = bookedSlot.getTime().getToTime();
											i++;
										} else {
											i++;
											break;
										}
									} else {
										i++;
										break;
									}
								}
							}

							if (endTime > startTime) {
								List<Slot> slots = DateAndTimeUtility.sliceTime(startTime, endTime,
										Math.round(slotTime), true);
								if (slots != null)
									slotResponse.addAll(slots);
							}
						}

						if (checkToday(localCalendar.get(Calendar.DAY_OF_YEAR), yearOfDate,
								doctorClinicProfileCollection.getTimeZone()))
							for (Slot slot : slotResponse) {
								if (slot.getMinutesOfDay() < getMinutesOfDay(date)) {
									slot.setIsAvailable(false);
									slotResponse.set(slotResponse.indexOf(slot), slot);
								}
							}
					}
					response.setSlots(slotResponse);
				}
			}
		} catch (

		Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting time slots");
		}
		return response;
	}

	@Override
	@Transactional
	public Event addEvent(final EventRequest request) {
		Event response = null;
		try {
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId()))
				doctorObjectId = new ObjectId(request.getDoctorId());
			if (!DPDoctorUtils.anyStringEmpty(request.getLocationId()))
				locationObjectId = new ObjectId(request.getLocationId());
			if (!DPDoctorUtils.anyStringEmpty(request.getLocationId()))
				hospitalObjectId = new ObjectId(request.getHospitalId());
			UserCollection userCollection = userRepository.findOne(new ObjectId(request.getDoctorId()));

			AppointmentCollection appointmentCollection = null;

			List<ObjectId> doctorIds = new ArrayList<ObjectId>();
			if (request.getDoctorIds() != null && !request.getDoctorIds().isEmpty()) {
				for (String doctorId : request.getDoctorIds())
					doctorIds.add(new ObjectId(doctorId));

			}
			List<AppointmentCollection> appointmentCollections = mongoTemplate
					.aggregate(
							Aggregation
									.newAggregation(
											Aggregation
													.match(new Criteria(
															"locationId")
																	.is(new ObjectId(request.getLocationId()))
																	.orOperator(
																			new Criteria("doctorId").in(doctorIds)
																					.and("time.fromTime")
																					.lte(request.getTime()
																							.getFromTime())
																					.and("time.toTime")
																					.gt(request.getTime().getToTime()),
																			new Criteria("doctorIds").is(doctorIds)
																					.and("isCalenderBlocked").is(true)
																					.and("time.fromTime")
																					.lte(request.getTime()
																							.getFromTime())
																					.and("time.toTime")
																					.gt(request.getTime().getToTime()),
																			new Criteria("doctorId")
																					.in(doctorIds).and("time.fromTime")
																					.lt(request.getTime().getFromTime())
																					.and("time.toTime")
																					.gte(request.getTime().getToTime()),
																			new Criteria("doctorIds").is(doctorIds)
																					.and("isCalenderBlocked").is(true)
																					.and("time.fromTime")
																					.lt(request.getTime().getFromTime())
																					.and("time.toTime")
																					.gte(request.getTime().getToTime()))

																	.and("fromDate").is(request.getFromDate())
																	.and("toDate").is(request.getToDate()).and("state")
																	.ne(AppointmentState.CANCEL.getState()))),
							AppointmentCollection.class, AppointmentCollection.class)
					.getMappedResults();
			if (userCollection != null) {
				if (appointmentCollections == null || appointmentCollections.isEmpty()
						|| !request.getIsCalenderBlocked()) {
					ObjectId patientId = null;
					if (request.getIsPatientRequired() != null && request.getIsPatientRequired()) {
						AppointmentRequest appointmentRequest = new AppointmentRequest();
						BeanUtil.map(request, appointmentRequest);
						patientId = registerPatientIfNotRegistered(appointmentRequest, doctorObjectId, locationObjectId,
								hospitalObjectId);
					}

					appointmentCollection = new AppointmentCollection();
					BeanUtil.map(request, appointmentCollection);
					appointmentCollection
							.setAppointmentId(UniqueIdInitial.EVENT.getInitial() + DPDoctorUtils.generateRandomId());
					appointmentCollection.setDoctorId(doctorObjectId);
					appointmentCollection.setLocationId(locationObjectId);
					appointmentCollection.setState(AppointmentState.CONFIRM);
					appointmentCollection.setType(AppointmentType.EVENT);
					appointmentCollection.setCreatedTime(new Date());
					appointmentCollection.setAdminCreatedTime(new Date());
					appointmentCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());
					appointmentCollection.setId(null);
					appointmentCollection.setDoctorIds(doctorIds);
					appointmentCollection.setPatientId(patientId);
					appointmentCollection.setHospitalId(hospitalObjectId);
					appointmentCollection = appointmentRepository.save(appointmentCollection);

					if (request.getIsCalenderBlocked()) {

						AppointmentBookedSlotCollection bookedSlotCollection = new AppointmentBookedSlotCollection();
						BeanUtil.map(appointmentCollection, bookedSlotCollection);
						bookedSlotCollection.setId(null);
						bookedSlotCollection.setAppointmentId(appointmentCollection.getAppointmentId());
						bookedSlotCollection.setDoctorId(doctorObjectId);
						bookedSlotCollection.setLocationId(locationObjectId);
						bookedSlotCollection.setHospitalId(hospitalObjectId);
						bookedSlotCollection.setDoctorIds(appointmentCollection.getDoctorIds());
						appointmentBookedSlotRepository.save(bookedSlotCollection);
					}

					final String createdBy = appointmentCollection.getCreatedBy(),
							id = appointmentCollection.getId().toString();
					if (appointmentCollection != null) {
						Executors.newSingleThreadExecutor().execute(new Runnable() {
							@Override
							public void run() {

								if (request.getDoctorIds() != null && !request.getDoctorIds().isEmpty()) {
									for (String doctorId : request.getDoctorIds()) {
										pushNotificationServices.notifyUser(doctorId,
												"Event created by " + createdBy + " is here - Tap to view it!",
												ComponentType.EVENT.getType(), id, null);
									}

								}
							}
						});
						response = new Event();
						BeanUtil.map(appointmentCollection, response);
					}
				} else {
					logger.error(timeSlotIsBooked);
					throw new BusinessException(ServiceError.NotAcceptable, timeSlotIsBooked);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Event updateEvent(EventRequest request) {
		Event response = null;
		try {
			AppointmentCollection appointmentCollection = appointmentRepository.findOne(new ObjectId(request.getId()));
			if (appointmentCollection != null) {
				AppointmentCollection appointmentCollectionToCheck = null;
				if (request.getState().equals(AppointmentState.RESCHEDULE)) {
					appointmentCollectionToCheck = appointmentRepository.findAppointmentbyUserLocationIdTimeDate(
							appointmentCollection.getDoctorId(), appointmentCollection.getLocationId(),
							request.getTime().getFromTime(), request.getTime().getToTime(), request.getFromDate(),
							request.getToDate(), AppointmentState.CANCEL.getState());
					if (appointmentCollectionToCheck != null)
						if (!request.getIsCalenderBlocked())
							appointmentCollectionToCheck = null;
				}

				if (appointmentCollectionToCheck == null) {
					AppointmentWorkFlowCollection appointmentWorkFlowCollection = new AppointmentWorkFlowCollection();
					BeanUtil.map(appointmentCollection, appointmentWorkFlowCollection);
					appointmentWorkFlowRepository.save(appointmentWorkFlowCollection);

					appointmentCollection.setState(request.getState());

					if (request.getState().equals(AppointmentState.CANCEL)) {
						AppointmentBookedSlotCollection bookedSlotCollection = appointmentBookedSlotRepository
								.findByAppointmentId(appointmentCollection.getAppointmentId());
						if (bookedSlotCollection != null)
							appointmentBookedSlotRepository.delete(bookedSlotCollection);
					} else {
						appointmentCollection.setFromDate(request.getFromDate());
						appointmentCollection.setToDate(request.getToDate());
						appointmentCollection.setTime(request.getTime());
						appointmentCollection.setIsCalenderBlocked(request.getIsCalenderBlocked());
						appointmentCollection.setExplanation(request.getExplanation());
						if (request.getState().equals(AppointmentState.RESCHEDULE)) {
							appointmentCollection.setIsRescheduled(true);
							appointmentCollection.setState(AppointmentState.CONFIRM);
							AppointmentBookedSlotCollection bookedSlotCollection = appointmentBookedSlotRepository
									.findByAppointmentId(appointmentCollection.getAppointmentId());

							if (request.getIsCalenderBlocked()) {
								if (bookedSlotCollection != null) {
									bookedSlotCollection.setFromDate(appointmentCollection.getFromDate());
									bookedSlotCollection.setToDate(appointmentCollection.getToDate());
									bookedSlotCollection.setTime(appointmentCollection.getTime());
									bookedSlotCollection.setUpdatedTime(new Date());
									appointmentBookedSlotRepository.save(bookedSlotCollection);
								}
							} else {
								if (bookedSlotCollection != null)
									appointmentBookedSlotRepository.delete(bookedSlotCollection);
							}
						}
					}
					appointmentCollection.setUpdatedTime(new Date());
					appointmentCollection = appointmentRepository.save(appointmentCollection);
					response = new Event();
					BeanUtil.map(appointmentCollection, response);
				} else {
					logger.error(timeSlotIsBooked);
					throw new BusinessException(ServiceError.NotAcceptable, timeSlotIsBooked);
				}
			} else {
				logger.error("Incorrect Id");
				throw new BusinessException(ServiceError.InvalidInput, "Incorrect Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	
	/*private List<PatientQueue> updateQueue(String appointmentId, String doctorId, String locationId, String hospitalId,
			String patientId, Date date, Integer startTime, Integer sequenceNo, Boolean isPatientDetailRequire) {
		List<PatientQueue> response = null;
		List<PatientQueueCollection> patientQueueCollections = null;
		try {
			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);
			DateTime start = DPDoctorUtils.getStartTime(date);
			DateTime end = DPDoctorUtils.getEndTime(date);
			PatientQueueCollection patientQueueCollection = null;

			if (!DPDoctorUtils.anyStringEmpty(appointmentId))
				patientQueueCollection = patientQueueRepository.find(appointmentId);
			else
				patientQueueCollection = patientQueueRepository.find(doctorObjectId, locationObjectId, hospitalObjectId,
						patientObjectId, start, end);

			if (patientQueueCollection == null)
				patientQueueCollection = new PatientQueueCollection();

			if (!DPDoctorUtils.anyStringEmpty(appointmentId))
				patientQueueCollection.setAppointmentId(appointmentId);
			patientQueueCollection.setDoctorId(doctorObjectId);
			patientQueueCollection.setLocationId(locationObjectId);
			patientQueueCollection.setHospitalId(hospitalObjectId);
			patientQueueCollection.setPatientId(patientObjectId);
			patientQueueCollection.setDate(date);
			patientQueueCollection.setStartTime(startTime);
			patientQueueCollection.setDiscarded(false);

			patientQueueCollections = patientQueueRepository.find(doctorObjectId, locationObjectId, hospitalObjectId,
					start, end, false, new Sort(Direction.DESC, "sequenceNo"));
			if (startTime != null) {
				if (patientQueueCollections == null || patientQueueCollections.isEmpty()) {
					patientQueueCollection.setSequenceNo(1);
					patientQueueRepository.save(patientQueueCollection);
				} else {
					for (PatientQueueCollection queueCollection : patientQueueCollections) {
						int seq = queueCollection.getSequenceNo();
						if (queueCollection.getStartTime() > startTime) {
							queueCollection.setSequenceNo(seq + 1);
							patientQueueRepository.save(queueCollection);
						} else {
							patientQueueCollection.setSequenceNo(seq + 1);
							patientQueueRepository.save(patientQueueCollection);
							break;
						}
					}
				}
			} else if (sequenceNo != null) {
				if (sequenceNo == 0) {
					for (PatientQueueCollection queueCollection : patientQueueCollections) {
						int seq = queueCollection.getSequenceNo();
						if (appointmentId.equalsIgnoreCase(queueCollection.getAppointmentId())) {
							// queueCollection.setDiscarded(true);
							patientQueueRepository.delete(queueCollection);
							break;
						} else {
							queueCollection.setSequenceNo(seq - 1);
							patientQueueRepository.save(queueCollection);
						}

					}
				} else {
					Integer toCheck = patientQueueRepository.find(appointmentId, doctorObjectId, locationObjectId,
							hospitalObjectId, patientObjectId, start, end, sequenceNo, false);
					if (toCheck == null || toCheck == 0) {
						PatientQueueCollection temp = null;
						int oldSeqNum = 0;
						int newStartTime = 0;
						for (PatientQueueCollection queueCollection : patientQueueCollections) {
							if (appointmentId.equalsIgnoreCase(queueCollection.getAppointmentId())) {
								oldSeqNum = queueCollection.getSequenceNo();
							}
							if (oldSeqNum > 0)
								break;
						}
						for (PatientQueueCollection queueCollection : patientQueueCollections) {
							if (oldSeqNum < sequenceNo) {
								if (queueCollection.getSequenceNo() >= oldSeqNum
										&& queueCollection.getSequenceNo() <= sequenceNo) {
									if (oldSeqNum == queueCollection.getSequenceNo()) {
										queueCollection.setStartTime(newStartTime + 1);
										queueCollection.setSequenceNo(sequenceNo);
										patientQueueRepository.save(queueCollection);

									} else {
										queueCollection.setSequenceNo(queueCollection.getSequenceNo() - 1);
										patientQueueRepository.save(queueCollection);
									}
								}
								newStartTime = queueCollection.getStartTime();
							} else if (oldSeqNum > sequenceNo) {
								if (queueCollection.getSequenceNo() <= oldSeqNum
										&& queueCollection.getSequenceNo() >= sequenceNo) {
									if (oldSeqNum == queueCollection.getSequenceNo()) {
										queueCollection.setSequenceNo(sequenceNo);
										temp = new PatientQueueCollection();
										BeanUtil.map(queueCollection, temp);
									} else {
										queueCollection.setSequenceNo(queueCollection.getSequenceNo() + 1);
										patientQueueRepository.save(queueCollection);
									}
								}
								newStartTime = queueCollection.getStartTime();
							}
						}
						if (temp != null) {
							temp.setStartTime(newStartTime + 1);
							patientQueueRepository.save(temp);
						}
					}

				}
			} else {
				patientQueueCollection
						.setAppointmentId(UniqueIdInitial.APPOINTMENT.getInitial() + DPDoctorUtils.generateRandomId());
				if (patientQueueCollections == null || patientQueueCollections.isEmpty()) {
					patientQueueCollection.setSequenceNo(1);
					patientQueueCollection.setStartTime(0);
				} else {
					for (PatientQueueCollection queueCollection : patientQueueCollections) {
						int seq = queueCollection.getSequenceNo();
						patientQueueCollection.setSequenceNo(seq + 1);
						patientQueueCollection.setStartTime(queueCollection.getStartTime() + 1);
						break;
					}
				}
				patientQueueRepository.save(patientQueueCollection);
			}

			if (isPatientDetailRequire) {
				response = mongoTemplate.aggregate(
						Aggregation.newAggregation(
								Aggregation.match(new Criteria("doctorId").is(doctorObjectId).and("locationId")
										.is(locationObjectId).and("hospitalId").is(hospitalObjectId).and("date")
										.gt(start).lte(end).and("discarded").is(false)),
								Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
								Aggregation.unwind("patient"),
								Aggregation.lookup("user_cl", "patientId", "_id", "patient.user"),
								Aggregation.unwind("patient.user"),
								// Aggregation.match(new
								// Criteria("patient.locationId").is(locationObjectId).and("patient.hospitalId").is(hospitalObjectId)),
								Aggregation.sort(new Sort(Direction.ASC, "sequenceNo"))),
						PatientQueueCollection.class, PatientQueue.class).getMappedResults();
				if (response != null && !response.isEmpty())
					for (PatientQueue collection : response) {
						if (collection.getPatient().getUser() != null) {
							collection.getPatient()
									.setMobileNumber(collection.getPatient().getUser().getMobileNumber());
							collection.getPatient().setColorCode(collection.getPatient().getUser().getColorCode());
						}
						collection.getPatient().setId(collection.getPatient().getUserId());
						collection.getPatient().setImageUrl(getFinalImageURL(collection.getPatient().getImageUrl()));
						collection.getPatient()
								.setThumbnailUrl(getFinalImageURL(collection.getPatient().getThumbnailUrl()));
					}
			} else {
				patientQueueCollections = patientQueueRepository.find(doctorObjectId, locationObjectId,
						hospitalObjectId, start, end, false, new Sort(Direction.ASC, "sequenceNo"));
				if (patientQueueCollections != null && !patientQueueCollections.isEmpty()) {
					response = new ArrayList<PatientQueue>();
					BeanUtil.map(patientQueueCollections, response);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}
*/
	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	private Integer getMinutesOfDay(Date date) {
		DateTime dateTime = new DateTime(new Date(), DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));

		Integer currentMinute = dateTime.getMinuteOfDay();
		return currentMinute;
	}

	// private Boolean checkToday(Date date) {
	// Boolean status = false;
	// DateTime inputDate = new DateTime(date,
	// DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
	// DateTime today = new
	// DateTime(DateTimeZone.forTimeZone(TimeZone.getTimeZone("IST")));
	// if (inputDate.getYear() == today.getYear() && today.getDayOfYear() ==
	// inputDate.getDayOfYear()) {
	// status = true;
	// }
	//
	// return status;
	// }

	private Boolean checkToday(int dayOfDate, int yearOfDate, String timeZone) {
		Boolean status = false;
		Calendar localCalendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
		if (yearOfDate == localCalendar.get(Calendar.YEAR) && dayOfDate == localCalendar.get(Calendar.DAY_OF_YEAR)) {
			status = true;
		}
		return status;
	}

	@Override
	@Transactional
	public Appointment getAppointmentById(ObjectId appointmentId) {
		Appointment appointment = null;
		AppointmentLookupResponse appointmentLookupResponse = mongoTemplate
				.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria("id").is(appointmentId)),
						Aggregation.lookup("user_cl", "doctorId", "_id", "doctor"), Aggregation.unwind("doctor"),
						Aggregation.lookup("location_cl", "locationId", "_id", "location"),
						Aggregation.unwind("location"), Aggregation.lookup("user_cl", "patientId", "_id", "patient"),
						Aggregation.unwind("patient")), AppointmentCollection.class, AppointmentLookupResponse.class)
				.getUniqueMappedResult();

		if (appointmentLookupResponse != null) {
			appointment = new Appointment();
			BeanUtil.map(appointmentLookupResponse, appointment);
			PatientCollection patientCollection = patientRepository.findByUserIdLocationIdAndHospitalId(
					new ObjectId(appointmentLookupResponse.getPatientId()),
					new ObjectId(appointmentLookupResponse.getLocationId()),
					new ObjectId(appointmentLookupResponse.getHospitalId()));

			PatientCard patient = new PatientCard();
			BeanUtil.map(patientCollection, patient);
			patient.setUserId(patient.getUserId());
			patient.setId(patient.getUserId());
			if (patient.getUser() != null)
				patient.setColorCode(patient.getUser().getColorCode());
			//patient.setImageUrl(getFinalImageURL(patient.getImageUrl()));
			patient.setThumbnailUrl(getFinalImageURL(patient.getThumbnailUrl()));
			appointment.setPatient(patient);
			if (appointmentLookupResponse.getDoctor() != null) {
				appointment.setDoctorName(appointmentLookupResponse.getDoctor().getTitle() + " "
						+ appointmentLookupResponse.getDoctor().getFirstName());
			}
			if (appointmentLookupResponse.getLocation() != null) {
				appointment.setLocationName(appointmentLookupResponse.getLocation().getLocationName());
				appointment.setClinicNumber(appointmentLookupResponse.getLocation().getClinicNumber());

				String address = (!DPDoctorUtils
						.anyStringEmpty(appointmentLookupResponse.getLocation().getStreetAddress())
								? appointmentLookupResponse.getLocation().getStreetAddress() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getLandmarkDetails())
								? appointmentLookupResponse.getLocation().getLandmarkDetails() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getLocality())
								? appointmentLookupResponse.getLocation().getLocality() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getCity())
								? appointmentLookupResponse.getLocation().getCity() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getState())
								? appointmentLookupResponse.getLocation().getState() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getCountry())
								? appointmentLookupResponse.getLocation().getCountry() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(appointmentLookupResponse.getLocation().getPostalCode())
								? appointmentLookupResponse.getLocation().getPostalCode()
								: "");

				if (address.charAt(address.length() - 2) == ',') {
					address = address.substring(0, address.length() - 2);
				}

				appointment.setClinicAddress(address);
				appointment.setLatitude(appointmentLookupResponse.getLocation().getLatitude());
				appointment.setLongitude(appointmentLookupResponse.getLocation().getLongitude());
			}
		}
		return appointment;
	}

	// @Scheduled(cron = "0 13 12 * * ?", zone = "IST")
	// @Transactional
	// public void updateQueue() {
	//
	// List<AppointmentCollection> appointmentList =
	// appointmentRepository.findByAppointment(
	// DPDoctorUtils.getFormTime(new Date()), DPDoctorUtils.getToTime(new
	// Date()),
	// new Sort(Sort.Direction.ASC, "time.fromTime"));
	// if (appointmentList != null) {
	//
	// while (!appointmentList.isEmpty()) {
	//
	// List<PatientQueueCollection> sortedList = new
	// ArrayList<PatientQueueCollection>();
	// PatientQueueCollection patientQueueCollectionPrev = null;
	// PatientQueueCollection patientQueueCollection = null;
	// AppointmentCollection appointmentCollection = null;
	// int indexi = 0;
	// while (indexi < appointmentList.size()) {
	// appointmentCollection = appointmentList.get(indexi);
	// if (sortedList.isEmpty()) {
	//
	// patientQueueCollectionPrev = new PatientQueueCollection();
	// patientQueueCollectionPrev.setAppointmentId(appointmentCollection.getAppointmentId());
	// patientQueueCollectionPrev.setDoctorId(appointmentCollection.getDoctorId());
	// patientQueueCollectionPrev.setLocationId(appointmentCollection.getLocationId());
	// patientQueueCollectionPrev.setHospitalId(appointmentCollection.getHospitalId());
	// patientQueueCollectionPrev.setPatientId(appointmentCollection.getPatientId());
	// patientQueueCollectionPrev.setDate(new Date());
	// patientQueueCollectionPrev.setStartTime(appointmentCollection.getTime().getFromTime());
	// patientQueueCollectionPrev.setDiscarded(false);
	// sortedList.add(patientQueueCollectionPrev);
	// appointmentList.remove(appointmentCollection);
	//
	// } else if
	// (appointmentCollection.getDoctorId().equals(patientQueueCollectionPrev.getDoctorId())
	// && appointmentCollection.getLocationId()
	// .equals(patientQueueCollectionPrev.getLocationId())) {
	// patientQueueCollection = new PatientQueueCollection();
	// patientQueueCollection.setAppointmentId(appointmentCollection.getAppointmentId());
	// patientQueueCollection.setDoctorId(appointmentCollection.getDoctorId());
	// patientQueueCollection.setLocationId(appointmentCollection.getLocationId());
	// patientQueueCollection.setHospitalId(appointmentCollection.getHospitalId());
	// patientQueueCollection.setPatientId(appointmentCollection.getPatientId());
	// patientQueueCollection.setDate(new Date());
	// patientQueueCollection.setStartTime(appointmentCollection.getTime().getFromTime());
	// patientQueueCollection.setDiscarded(false);
	// sortedList.add(patientQueueCollection);
	// appointmentList.remove(appointmentCollection);
	//
	// } else
	// indexi++;
	// }
	//
	// for (indexi = 0; indexi < sortedList.size(); indexi++) {
	// sortedList.get(indexi).setSequenceNo(indexi + 1);
	//
	// }
	//
	// patientQueueRepository.save(sortedList);
	// }
	// }
	// }

	/*@Scheduled(cron = "0 30 0 * * ?", zone = "IST")
	@Transactional
	public void updateQueue() {

		List<AppointmentCollection> appointmentList = appointmentRepository.findConfirmAppointments(
				DPDoctorUtils.getStartTime(new Date()), DPDoctorUtils.getEndTime(new Date()),
				new Sort(Sort.Direction.ASC, "time.fromTime"));
		Map<String, List<PatientQueueCollection>> doctorsPatientQueue = new HashMap<String, List<PatientQueueCollection>>();

		for (AppointmentCollection appointmentCollection : appointmentList) {
			PatientQueueCollection patientQueueCollection = new PatientQueueCollection();
			patientQueueCollection.setAppointmentId(appointmentCollection.getAppointmentId());
			patientQueueCollection.setDoctorId(appointmentCollection.getDoctorId());
			patientQueueCollection.setLocationId(appointmentCollection.getLocationId());
			patientQueueCollection.setHospitalId(appointmentCollection.getHospitalId());
			patientQueueCollection.setPatientId(appointmentCollection.getPatientId());
			patientQueueCollection.setDate(appointmentCollection.getFromDate());
			patientQueueCollection.setStartTime(appointmentCollection.getTime().getFromTime());
			patientQueueCollection.setCreatedTime(appointmentCollection.getCreatedTime());
			patientQueueCollection.setUpdatedTime(appointmentCollection.getUpdatedTime());
			patientQueueCollection.setDiscarded(false);

			List<PatientQueueCollection> patientQueueCollections = doctorsPatientQueue
					.get(appointmentCollection.getDoctorId().toString() + ""
							+ appointmentCollection.getLocationId().toString());
			if (patientQueueCollections == null) {
				patientQueueCollection.setSequenceNo(1);
				patientQueueCollections = new ArrayList<PatientQueueCollection>();
			} else
				patientQueueCollection.setSequenceNo(patientQueueCollections.size() + 1);

			patientQueueCollections.add(patientQueueCollection);
			doctorsPatientQueue.put(appointmentCollection.getDoctorId().toString() + ""
					+ appointmentCollection.getLocationId().toString(), patientQueueCollections);
		}

		for (Entry<String, List<PatientQueueCollection>> entry : doctorsPatientQueue.entrySet()) {
			patientQueueRepository.save(entry.getValue());
		}

	}*/

	public CustomAppointment addCustomAppointment(CustomAppointment request) {
		CustomAppointment response = null;
		try {

			CustomAppointmentCollection appointmentCollection = null;
			UserCollection doctor = userRepository.findOne(new ObjectId(request.getDoctorId()));
			if (DPDoctorUtils.anyStringEmpty(request.getPatientName())) {
				throw new BusinessException(ServiceError.InvalidInput, "Patient Name should not Empty ");
			}
			if (doctor == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid doctor Id");
			}
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				appointmentCollection = customAppointmentRepository.findOne(new ObjectId(request.getId()));
				request.setUpdatedTime(new Date());
				request.setCreatedBy(appointmentCollection.getCreatedBy());
				request.setCreatedTime(appointmentCollection.getCreatedTime());
			} else {
				appointmentCollection = new CustomAppointmentCollection();
				request.setCreatedBy(doctor.getTitle() + " " + doctor.getFirstName());
				request.setCreatedTime(new Date());
			}
			BeanUtil.map(request, appointmentCollection);
			customAppointmentRepository.save(appointmentCollection);
			response = new CustomAppointment();
			BeanUtil.map(appointmentCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

}
