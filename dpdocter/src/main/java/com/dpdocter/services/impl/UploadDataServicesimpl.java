package com.dpdocter.services.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Address;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DOB;
import com.dpdocter.beans.Discount;
import com.dpdocter.beans.Drug;
import com.dpdocter.beans.DrugDirection;
import com.dpdocter.beans.DrugDurationUnit;
import com.dpdocter.beans.DrugType;
import com.dpdocter.beans.Duration;
import com.dpdocter.beans.Fields;
import com.dpdocter.beans.InvoiceItem;
import com.dpdocter.beans.OPDReports;
import com.dpdocter.beans.PrescriptionItem;
import com.dpdocter.beans.Quantity;
import com.dpdocter.beans.Reference;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.Treatment;
import com.dpdocter.beans.TreatmentService;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.collections.AppointmentBookedSlotCollection;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.ClinicalNotesCollection;
import com.dpdocter.collections.DiseasesCollection;
import com.dpdocter.collections.DoctorClinicProfileCollection;
import com.dpdocter.collections.DoctorPatientDueAmountCollection;
import com.dpdocter.collections.DoctorPatientInvoiceCollection;
import com.dpdocter.collections.DoctorPatientLedgerCollection;
import com.dpdocter.collections.DoctorPatientReceiptCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.DrugDirectionCollection;
import com.dpdocter.collections.DrugDurationUnitCollection;
import com.dpdocter.collections.DrugTypeCollection;
import com.dpdocter.collections.GroupCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.OPDReportsCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientTreatmentCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.ReferencesCollection;
import com.dpdocter.collections.TreatmentServicesCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.elasticsearch.document.ESPatientDocument;
import com.dpdocter.elasticsearch.repository.ESPatientRepository;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.enums.AppointmentState;
import com.dpdocter.enums.InvoiceItemType;
import com.dpdocter.enums.ModeOfPayment;
import com.dpdocter.enums.QuantityEnum;
import com.dpdocter.enums.ReceiptType;
import com.dpdocter.enums.Resource;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.enums.UnitType;
import com.dpdocter.enums.VisitedFor;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AppointmentBookedSlotRepository;
import com.dpdocter.repository.AppointmentRepository;
import com.dpdocter.repository.ClinicalNotesRepository;
import com.dpdocter.repository.DiseasesRepository;
import com.dpdocter.repository.DoctorPatientDueAmountRepository;
import com.dpdocter.repository.DoctorPatientInvoiceRepository;
import com.dpdocter.repository.DoctorPatientLedgerRepository;
import com.dpdocter.repository.DoctorPatientReceiptRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.DrugTypeRepository;
import com.dpdocter.repository.GroupRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.OPDReportsRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PatientTreamentRepository;
import com.dpdocter.repository.PatientVisitRepository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.ReferenceRepository;
import com.dpdocter.repository.TreatmentServicesRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DrugAddEditRequest;
import com.dpdocter.request.PatientRegistrationRequest;
import com.dpdocter.response.DoctorClinicProfileLookupResponse;
import com.dpdocter.services.HistoryServices;
import com.dpdocter.services.PatientTreatmentServices;
import com.dpdocter.services.PrescriptionServices;
import com.dpdocter.services.RegistrationService;
import com.dpdocter.services.ReportsService;
import com.dpdocter.services.TransactionalManagementService;
import com.dpdocter.services.UploadDateService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;
import common.util.web.PrescriptionUtils;

@Service
public class UploadDataServicesimpl implements UploadDateService {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LocationRepository locationRepository;
	
	@Autowired
	private ReferenceRepository referenceRepository;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private DiseasesRepository diseasesRepository;

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private PatientVisitRepository patientVisitRepository;

	@Autowired
	private ReportsService reportsService;

	@Autowired
	private TransactionalManagementService transactionalManagementService;

	@Autowired
	private ESRegistrationService esRegistrationService;

	@Autowired
	private HistoryServices historyServices;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private PrescriptionRepository prescriptionRepository;

	@Autowired
	private PatientTreamentRepository patientTreamentRepository;

	@Autowired
	private DrugRepository drugRepository;

	@Autowired
	private TreatmentServicesRepository treatmentServicesRepository;

	@Autowired
	private PatientTreatmentServices patientTreatmentServices;

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Autowired
	private AppointmentBookedSlotRepository appointmentBookedSlotRepository;

	@Autowired
	private DrugTypeRepository drugTypeRepository;

	@Autowired
	private ClinicalNotesRepository clinicalNotesRepository;

	@Autowired
	private DoctorPatientInvoiceRepository doctorPatientInvoiceRepository;

	@Autowired
	private DoctorPatientReceiptRepository doctorPatientReceiptRepository;

//	@Autowired
//	private DischargeSummaryRepository dischargeSummaryRepository;
//
//	@Autowired
//	private AdmitCardRepository admitCardRepository;
//
//	@Autowired
//	private DeliveryReportsRepository deliveryReportsRepository;
//
//	@Autowired
//	private IPDReportsRepository ipdReportsRepository;

	@Autowired
	private OPDReportsRepository opdReportsRepository;

	@Autowired
	private PrescriptionServices prescriptionServices;

	@Autowired
	DoctorPatientLedgerRepository doctorPatientLedgerRepository;

	@Autowired
	DoctorPatientDueAmountRepository doctorPatientDueAmountRepository;

	@Value(value = "${patient.count}")
	private String patientCount;

	private static final String COMMA_DELIMITER = ",";

	private static final String NEW_LINE_SEPARATOR = "\n";

	private static final String FILE_HEADER = "PatientNumber,PatientName";

	@Value(value = "${upload.patients.data.file}")
	private String UPLOAD_PATIENTS_DATA_FILE;

	@Value(value = "${upload.appointments.data.file}")
	private String UPLOAD_APPOINTMENTS_DATA_FILE;

	@Value(value = "${upload.prescriptions.data.file}")
	private String UPLOAD_PRESCRIPTIONS_DATA_FILE;

	@Value(value = "${list.patients.not.registered.file}")
	private String LIST_PATIENTS_NOT_REGISTERED_FILE;

	@Value(value = "${upload.treatments.data.file}")
	private String UPLOAD_TREATMENTS_DATA_FILE;

	@Value(value = "${upload.treatment.services.data.file}")
	private String UPLOAD_TREATMENT_SERVICES_DATA_FILE;
	
	@Value(value = "${upload.clinical.notes.data.file}")
	private String UPLOAD_CLINICAL_NOTES_DATA_FILE;

	@Value(value = "${upload.payments.data.file}")
	private String UPLOAD_PAYMENTS_DATA_FILE;
	
	@Value(value = "${upload.invoices.data.file}")
	private String UPLOAD_INVOICES_DATA_FILE;
	
	@Value(value = "${upload.treatments.plan.data.file}")
	private String UPLOAD_TREATMENTS_PLAN_DATA_FILE;

	private String patientInitial = "";

	@Autowired
	private ESPatientRepository esPatientRepository;

//	@Autowired
//	private ESDrugRepository esDrugRepository;
//	
//	@Autowired
//	private ElasticsearchTemplate elasticsearchTemplate;
	
	@Override
	public Boolean deletePatients(String doctorId, String locationId, String hospitalId) {
		try {
			ObjectId doctorObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);

			// List<DrugCollection> drugCollections =
			// drugRepository.findByLocationId(locationObjectId);
			// if(drugCollections != null)
			// for(DrugCollection drugCollection : drugCollections) {
			//
			// ESDrugDocument document =
			// esDrugRepository.findOne(drugCollection.getId().toString());
			// if(document != null)esDrugRepository.delete(document);
			// drugRepository.delete(drugCollection);
			// }
			//
			// BoolQueryBuilder booleanQueryBuilder = new
			// BoolQueryBuilder().must(QueryBuilders.termQuery("locationId",
			// locationId));
			//
			//
			// SearchQuery searchQuery = new
			// NativeSearchQueryBuilder().withQuery(booleanQueryBuilder).withPageable(new
			// PageRequest(0, 3267)).build();
			// List<ESDrugDocument> drugDocuments =
			// elasticsearchTemplate.queryForList(searchQuery,
			// ESDrugDocument.class);
			// if(drugDocuments != null) {
			// System.out.println(drugDocuments.size());
			// for(ESDrugDocument esDrugDocument : drugDocuments) {
			// esDrugRepository.delete(esDrugDocument.getId());
			// }
			// }
			//
			// List<TreatmentServicesCollection> treatmentServicesCollections =
			// treatmentServicesRepository.findByLocationId(locationObjectId);
			// if(treatmentServicesCollections != null)
			// for(TreatmentServicesCollection treatmentServicesCollection :
			// treatmentServicesCollections) {
			//
			// ESTreatmentServiceDocument document =
			// esTreatmentServiceRepository.findOne(treatmentServicesCollection.getId().toString());
			// if(document !=
			// null)esTreatmentServiceRepository.delete(document.getId());
			// treatmentServicesRepository.delete(treatmentServicesCollection);
			// }
			//
			//
			// searchQuery = new
			// NativeSearchQueryBuilder().withQuery(booleanQueryBuilder).build();
			// List<ESTreatmentServiceDocument> esTreatmentServiceDocuments =
			// elasticsearchTemplate.queryForList(searchQuery,
			// ESTreatmentServiceDocument.class);
			// if(esTreatmentServiceDocuments != null) {
			// System.out.println(esTreatmentServiceDocuments.size());
			// for(ESTreatmentServiceDocument esTreatmentServiceDocument :
			// esTreatmentServiceDocuments) {
			// esTreatmentServiceRepository.delete(esTreatmentServiceDocument.getId());
			// }
			// }
			//
			//

			List<PatientCollection> patientCollections = patientRepository.findByDoctorId(doctorObjectId,
					new Date(Long.parseLong("0")), new Sort(Direction.ASC, "createdTime"));
			for (PatientCollection patientCollection : patientCollections) {

				ESPatientDocument document = esPatientRepository.findOne(patientCollection.getId().toString());
				if (document != null)
					esPatientRepository.delete(document);
				userRepository.delete(patientCollection.getUserId());
				patientRepository.delete(patientCollection);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public Boolean uploadPatientData(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		FileWriter fileWriter = null;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "%";
		int lineCount = 0;

		try {
			fileWriter = new FileWriter(LIST_PATIENTS_NOT_REGISTERED_FILE);
			fileWriter.append(FILE_HEADER.toString());
			fileWriter.append(NEW_LINE_SEPARATOR);

			br = new BufferedReader(new FileReader(UPLOAD_PATIENTS_DATA_FILE));

			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			UserCollection drCollection = userRepository.findOne(doctorObjectId);

			PatientRegistrationRequest request = null;

			while ((line = br.readLine()) != null) {

				if (lineCount > 0) {
					int count = 0;
					String[] fields = line.split(cvsSplitBy);
					request = new PatientRegistrationRequest();
					if (fields.length > 2 &&!DPDoctorUtils.anyStringEmpty(fields[2]) && !fields[2].equalsIgnoreCase("NONE'")) {

						
						String mobileNumber = fields[2].replace("'", "");
						if (mobileNumber.startsWith("+91"))
							mobileNumber = mobileNumber.replace("+91", "");
						request.setMobileNumber(mobileNumber);
						

						List<UserCollection> userCollections = userRepository.findByMobileNumber(mobileNumber);
						if (userCollections != null && !userCollections.isEmpty()) {
							for (UserCollection userCollection : userCollections) {
								if (!userCollection.getUserName().equalsIgnoreCase(userCollection.getEmailAddress()))
									count++;
							}
						}
					}
						if (count < Integer.parseInt(patientCount)) {
							if (!DPDoctorUtils.anyStringEmpty(fields[1]) && !fields[1].equalsIgnoreCase("NONE'")) {
								request.setFirstName(fields[1].replace("'", ""));
								request.setLocalPatientName(fields[1].replace("'", ""));
							}
							if (fields.length > 6 && !DPDoctorUtils.anyStringEmpty(fields[6]) && !fields[6].equalsIgnoreCase("NONE'"))
								request.setGender(fields[6].replace("'", ""));

							if (fields.length > 12 && !DPDoctorUtils.anyStringEmpty(fields[12]) && !fields[12].equalsIgnoreCase("NONE'")) {
								String[] dob = fields[12].replace("'", "").split("-");
								DOB dobObject = new DOB(Integer.parseInt(dob[2]), Integer.parseInt(dob[1]),
										Integer.parseInt(dob[0]));
								request.setDob(dobObject);
							}

							if (fields.length > 13 && !DPDoctorUtils.anyStringEmpty(fields[13]) && !fields[13].equalsIgnoreCase("NONE'"))
								request.setAge(Integer.parseInt(fields[13].replace("'", "")));

							if (fields.length > 4 && !DPDoctorUtils.anyStringEmpty(fields[4]) && !fields[4].equalsIgnoreCase("NONE'"))
								request.setEmailAddress(fields[4].replace("'", ""));

							if (fields.length > 15 && !DPDoctorUtils.anyStringEmpty(fields[15]) && !fields[15].equalsIgnoreCase("NONE'"))
								request.setBloodGroup(fields[15].replace("'", ""));

							if (fields.length > 3 && !DPDoctorUtils.anyStringEmpty(fields[3]) && !fields[3].equalsIgnoreCase("NONE'"))
								request.setSecMobile(fields[3].replace("'", ""));

							if (fields.length > 5 && !DPDoctorUtils.anyStringEmpty(fields[5]) && !fields[5].equalsIgnoreCase("NONE'"))
								request.setSecMobile(fields[5].replace("'", ""));

							String country = null, city = null, state = null, postalCode = null, locality = null,
									streetAddress = null;

							if (fields.length > 7 && !DPDoctorUtils.anyStringEmpty(fields[7]) && !fields[7].equalsIgnoreCase("NONE'"))
								streetAddress = fields[7].replace("'", "");

							if (fields.length > 8 && !DPDoctorUtils.anyStringEmpty(fields[8]) && !fields[8].equalsIgnoreCase("NONE'"))
								locality = fields[8].replace("'", "");

							if (fields.length > 9 && !DPDoctorUtils.anyStringEmpty(fields[9]) && !fields[9].equalsIgnoreCase("NONE'"))
								city = fields[9].replace("'", "");

							// if (!DPDoctorUtils.anyStringEmpty(fields[16]) &&
							// !fields[16].equalsIgnoreCase("NULL"))
							// state = fields[16];
							// if (!DPDoctorUtils.anyStringEmpty(fields[17]) &&
							// !fields[17].equalsIgnoreCase("NULL"))
							country = "India";

							if (fields.length > 10 && !DPDoctorUtils.anyStringEmpty(fields[10]) && !fields[10].equalsIgnoreCase("NONE'"))
								postalCode = fields[10].replace("'", "");

							if (!DPDoctorUtils.allStringsEmpty(country, city, state, postalCode, locality,
									streetAddress)) {
								Address address = new Address(country, city, state, postalCode, locality, null, null,
										null, streetAddress);
								request.setAddress(address);
							}

							if (fields.length > 18 && !DPDoctorUtils.anyStringEmpty(fields[18])
									&& !fields[18].equalsIgnoreCase("NONE'")) {
								String referredBy = fields[18].replace("'", "");
								Reference reference = new Reference();
								reference.setReference(referredBy);

								ReferencesCollection referencesCollection = referenceRepository.find(referredBy,
										doctorObjectId, locationObjectId, hospitalObjectId);
								if (referencesCollection != null)
									reference.setId(referencesCollection.getId().toString());

								request.setReferredBy(reference);
							}

							if (fields.length > 19 && !DPDoctorUtils.anyStringEmpty(fields[19])
									&& !fields[19].equalsIgnoreCase("NONE'")) {
								String groupName = fields[19].replace("'", "");
								GroupCollection groupCollection = groupRepository.findByName(groupName, doctorObjectId,
										locationObjectId, hospitalObjectId, false);
								if (groupCollection == null) {
									groupCollection = new GroupCollection();
									groupCollection.setDoctorId(doctorObjectId);
									groupCollection.setLocationId(locationObjectId);
									groupCollection.setHospitalId(hospitalObjectId);
									groupCollection.setName(groupName);
									groupCollection.setCreatedTime(new Date());
									if (drCollection != null) {
										groupCollection.setCreatedBy(
												(drCollection.getTitle() != null ? drCollection.getTitle() + " " : "")
														+ drCollection.getFirstName());
									}
									groupCollection = groupRepository.save(groupCollection);
								}
								request.setGroups(Arrays.asList(groupCollection.getId().toString()));
							}
							request.setDoctorId(doctorId);
							request.setLocationId(locationId);
							request.setHospitalId(hospitalId);

							if (!DPDoctorUtils.anyStringEmpty(fields[0])) {
								request.setPNUM(fields[0].replace("'", ""));

								patientInitial = request.getPNUM().replaceAll("[0-9]", "");
								BufferedReader br1 = new BufferedReader(new FileReader(UPLOAD_APPOINTMENTS_DATA_FILE));
								String appointmentDataLine = null;
								while ((appointmentDataLine = br1.readLine()) != null) {
									String[] splittedAppointmentData = appointmentDataLine.split("\\|");
									if (splittedAppointmentData[1].equalsIgnoreCase(fields[0])) {
										SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d hh:mm:ss");

										String dateSTri = splittedAppointmentData[0].replace("'", "");
										dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
										Date date = dateFormat.parse(dateSTri);
										request.setRegistrationDate(date.getTime());
										br1.close();
										break;
									}
								}
							} else {
								request.setRegistrationDate(new Date().getTime());
							}

							RegisteredPatientDetails registeredPatientDetails = registrationService
									.registerNewPatient(request);

							transactionalManagementService.addResource(
									new ObjectId(registeredPatientDetails.getUserId()), Resource.PATIENT, false);
							esRegistrationService
									.addPatient(registrationService.getESPatientDocument(registeredPatientDetails));

							if (fields.length > 17 && !DPDoctorUtils.anyStringEmpty(fields[17]) && !fields[17].equalsIgnoreCase("NONE'")) {
								fields[17] = fields[17].replace("'", "");
								String diseases[] = fields[17].split(",");
								for (String disease : diseases) {
									DiseasesCollection diseasesCollection = diseasesRepository.find(
											disease.replace("?", "\\\\?"), doctorObjectId, locationObjectId,
											hospitalObjectId, false);
									if (diseasesCollection == null) {
										diseasesCollection = new DiseasesCollection();
										diseasesCollection.setCreatedTime(new Date());
										diseasesCollection.setDoctorId(doctorObjectId);
										diseasesCollection.setLocationId(locationObjectId);
										diseasesCollection.setHospitalId(hospitalObjectId);
										diseasesCollection.setDisease(disease);

										if (drCollection != null) {
											diseasesCollection.setCreatedBy(
													(drCollection.getTitle() != null ? drCollection.getTitle() + " "
															: "") + drCollection.getFirstName());
										}
										diseasesCollection = diseasesRepository.save(diseasesCollection);
									}
									historyServices.assignMedicalHistory(diseasesCollection.getId().toString(),
											registeredPatientDetails.getUserId(), doctorId, hospitalId, locationId);
								}
								System.out.println(patientCount + registeredPatientDetails.getMobileNumber());
							}

						} else {
							System.out.println(patientCount + " patients already exist with mobile number "
									+ request.getMobileNumber());
						}
				}
				lineCount++;
				response = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
					if (fileWriter != null) {
						fileWriter.flush();
						fileWriter.close();
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return response;
	}

	@Override
	public Boolean uploadPrescriptionData(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\\|";
		int lineCount = 0;
		int dataCountNotUploaded = 0;
		try {

			br = new BufferedReader(new FileReader(UPLOAD_PRESCRIPTIONS_DATA_FILE));

			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			
			UserCollection drCollection = userRepository.findOne(doctorObjectId);
			PrescriptionCollection prescriptionCollection = null;

			Map<String, DrugType> drugTypesMap = new HashMap<String, DrugType>();
			List<DrugType> drugTypes = mongoTemplate
					.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria())), DrugTypeCollection.class,
							DrugType.class)
					.getMappedResults();
			if (drugTypes != null && !drugTypes.isEmpty()) {
				for (DrugType drugType : drugTypes)
					drugTypesMap.put(drugType.getType(), drugType);
			}

			Map<String, DrugDurationUnit> drugDurationMap = new HashMap<String, DrugDurationUnit>();
			List<DrugDurationUnit> drugDurationUnits = mongoTemplate
					.aggregate(Aggregation.newAggregation(Aggregation.match(new Criteria())),
							DrugDurationUnitCollection.class, DrugDurationUnit.class)
					.getMappedResults();
			if (drugDurationUnits != null && !drugDurationUnits.isEmpty()) {
				for (DrugDurationUnit durationUnit : drugDurationUnits)
					drugDurationMap.put(durationUnit.getUnit(), durationUnit);
			}

			DrugDirection beforeMealDirection = mongoTemplate.aggregate(
					Aggregation.newAggregation(
							Aggregation.match(new Criteria("direction").is("Before meal").and("doctorId").is(null))),
					DrugDirectionCollection.class, DrugDirection.class).getUniqueMappedResult();
			DrugDirection afterMealDirection = mongoTemplate.aggregate(
					Aggregation.newAggregation(
							Aggregation.match(new Criteria("direction").is("After meal").and("doctorId").is(null))),
					DrugDirectionCollection.class, DrugDirection.class).getUniqueMappedResult();

			while ((line = br.readLine()) != null) {

				if (lineCount > 0) {
					Boolean createVisit = false;
					String[] fields = line.split(cvsSplitBy);
System.out.println(lineCount +".."+ fields[2]);
					if (!DPDoctorUtils.anyStringEmpty(fields[2], fields[3])) {
						PatientCollection patientCollection = patientRepository.findByLocationIDHospitalIDAndPNUM(
								locationObjectId, hospitalObjectId, fields[2].replace("'", ""));
						if (patientCollection != null) {

							Date createdTime = new Date();
							if (!DPDoctorUtils.anyStringEmpty(fields[0])) {
								SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d hh:mm:ss");

								String dateSTri = fields[0].replace("'", "")+ " 13:00:00";
								System.out.println(dateSTri);
								dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
								createdTime = dateFormat.parse(dateSTri);
							}

							prescriptionCollection = prescriptionRepository.find(doctorObjectId, locationObjectId,
									hospitalObjectId, patientCollection.getUserId(), createdTime);
							if (prescriptionCollection == null) {
								createVisit = true;
								prescriptionCollection = new PrescriptionCollection();

								prescriptionCollection.setDoctorId(doctorObjectId);
								prescriptionCollection.setLocationId(locationObjectId);
								prescriptionCollection.setHospitalId(hospitalObjectId);
								prescriptionCollection.setPatientId(patientCollection.getUserId());
								prescriptionCollection.setCreatedTime(createdTime);
								prescriptionCollection.setUpdatedTime(createdTime);
								prescriptionCollection
										.setPrescriptionCode(PrescriptionUtils.generatePrescriptionCode());
								prescriptionCollection.setUniqueEmrId(
										UniqueIdInitial.PRESCRIPTION.getInitial() + DPDoctorUtils.generateRandomId());
								prescriptionCollection.setCreatedBy(
										(drCollection.getTitle() != null ? drCollection.getTitle() + " " : "")
												+ drCollection.getFirstName());

							}

							List<PrescriptionItem> items = prescriptionCollection.getItems();

							String drugName = fields[3].replace("'", "")
									+ ((checkIfNotNullOrNone(fields[5]) && !DPDoctorUtils.anyStringEmpty(fields[5]))
											? " " + fields[5].replace("'", "") : "")
									+ ((checkIfNotNullOrNone(fields[5]) && checkIfNotNullOrNone(fields[6])
											&& !DPDoctorUtils.anyStringEmpty(fields[6]))
													? " " + fields[6].replace("'", "") : "");

							String drugType = (!DPDoctorUtils.anyStringEmpty(fields[4])) ? fields[4].replace("'", "")
									: null;
							// DrugType
							DrugType drugTypeObj = null;
							if (!DPDoctorUtils.anyStringEmpty(drugType)) {

								if (drugType.equalsIgnoreCase("TABLET"))
									drugType = "TAB";
								if (drugType.equalsIgnoreCase("CAPSULE"))
									drugType = "CAP";
								if (drugType.equalsIgnoreCase("OINTMENT"))
									drugType = "OINT";
								if (drugType.equalsIgnoreCase("SYRUP"))
									drugType = "SYP";

								drugTypeObj = drugTypesMap.get(drugType);
								if (drugTypeObj == null) {
									List<DrugType> types = mongoTemplate
											.aggregate(
													Aggregation
															.newAggregation(
																	Aggregation
																			.match(new Criteria("type")
																					.regex("^" + drugType, "i")
																					.orOperator(
																							new Criteria("doctorId")
																									.is(new ObjectId(
																											doctorId))
																									.and("locationId")
																									.is(new ObjectId(
																											locationId))
																									.and("hospitalId")
																									.is(new ObjectId(
																											hospitalId)),
																							new Criteria("doctorId")
																									.is(null)
																									.and("locationId")
																									.is(null)
																									.and("hospitalId")
																									.is(null)))),
													DrugTypeCollection.class, DrugType.class)
											.getMappedResults();
									if (types != null && !types.isEmpty()) {
										drugTypeObj = types.get(0);
										drugTypesMap.put(drugType, drugTypeObj);
									} else {
										DrugTypeCollection drugTypeCollection = new DrugTypeCollection();
										drugTypeCollection.setCreatedBy(
												(drCollection.getTitle() != null ? drCollection.getTitle() + " " : "")
														+ drCollection.getFirstName());
										drugTypeCollection.setCreatedTime(new Date());
										drugTypeCollection.setType(drugType);
										drugTypeCollection = drugTypeRepository.save(drugTypeCollection);
										drugTypeObj = new DrugType();
										BeanUtil.map(drugTypeCollection, drugTypeObj);
										drugTypesMap.put(drugType, drugTypeObj);
									}
								}

							}

							List<DrugCollection> drugCollections = drugRepository.findByNameAndDoctorLocationHospital(
									drugName, drugType, doctorObjectId, locationObjectId, hospitalObjectId);

							DrugCollection drugCollection = null;
							if (drugCollections != null && !drugCollections.isEmpty()) {
								drugCollection = drugCollections.get(0);

								for (DrugCollection drug : drugCollections) {
									if (!DPDoctorUtils.anyStringEmpty(drugCollection.getDoctorId())) {
										drugCollection = drug;
										break;
									}
								}
							}

							if (drugCollection == null) {
								drugCollection = new DrugCollection();
							}

							DrugAddEditRequest drugAddEditRequest = new DrugAddEditRequest();
							if (drugCollection != null) {
								BeanUtil.map(drugCollection, drugAddEditRequest);
							}
							drugAddEditRequest.setDoctorId(doctorId);
							drugAddEditRequest.setHospitalId(hospitalId);
							drugAddEditRequest.setLocationId(locationId);
							if (!DPDoctorUtils.allStringsEmpty(drugName)) {
								drugAddEditRequest.setDrugName(drugName);
							}

							drugAddEditRequest.setDrugType(drugTypeObj);

							// DrugDirection
							List<DrugDirection> drugDirections = null;
							if (fields.length > 7 && checkIfNotNullOrNone(fields[7]) && fields[7].contains("1")) {
								drugDirections = new ArrayList<DrugDirection>();
								drugDirections.add(beforeMealDirection);
							}
							if (fields.length > 8 && checkIfNotNullOrNone(fields[8]) && fields[8].contains("1")) {
								if (drugDirections == null)
									drugDirections = new ArrayList<DrugDirection>();
								drugDirections.add(afterMealDirection);
							}
							drugAddEditRequest.setDirection(drugDirections);

							// DrugDuration13 14 
							Duration duration = null;
							if (fields.length > 13 && checkIfNotNullOrNone(fields[13]) && checkIfNotNullOrNone(fields[14])){
								duration = new Duration();
								duration.setValue(checkIfNotNullOrNone(fields[13]) ? fields[13].replace("'", "") : "");
								duration.setDurationUnit(drugDurationMap.get(fields[14].replace("'", "")));
								drugAddEditRequest.setDuration(duration);
							}
							

							// DrugDosage

							// Instructions12
							String instruction = (fields.length > 12) ? fields[12].replace("'", "") : null;

							Drug drug = prescriptionServices.addFavouriteDrug(drugAddEditRequest, drugCollection,
									prescriptionCollection.getCreatedBy());

							PrescriptionItem prescriptionItem = new PrescriptionItem(new ObjectId(drug.getId()),
									duration, null, drugTypeObj, drugName, null, null, drugDirections, instruction);

							if (items == null)
								items = new ArrayList<PrescriptionItem>();
							items.add(prescriptionItem);

							prescriptionCollection.setItems(items);

							prescriptionCollection = prescriptionRepository.save(prescriptionCollection);

							if (createVisit)
								addRecord(prescriptionCollection, VisitedFor.PRESCRIPTION, null,
										prescriptionCollection.getPatientId(), prescriptionCollection.getDoctorId(),
										prescriptionCollection.getLocationId(), prescriptionCollection.getHospitalId(),
										prescriptionCollection.getId());

							if (prescriptionCollection != null) {
								OPDReports opdReports = new OPDReports(
										String.valueOf(prescriptionCollection.getPatientId()),
										String.valueOf(prescriptionCollection.getId()),
										String.valueOf(prescriptionCollection.getDoctorId()),
										String.valueOf(prescriptionCollection.getLocationId()),
										String.valueOf(prescriptionCollection.getHospitalId()),
										prescriptionCollection.getCreatedTime());

								opdReports = reportsService.submitOPDReport(opdReports);
							}
						} else {
							dataCountNotUploaded++;
						}
					}
				}
				lineCount++;
				response = true;
			}
			System.out.println("Rx Done. dataCountNotUploaded: " + dataCountNotUploaded);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return response;
	}

	@Override
	public Boolean uploadAppointmentData(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\\|";
		int dataCountNotUploaded = 0;
		Map<String, UserCollection> doctors = new HashMap<String, UserCollection>();
		int lineCount = 0;
		try {

			br = new BufferedReader(new FileReader(UPLOAD_APPOINTMENTS_DATA_FILE));

			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			UserCollection drCollection = userRepository.findOne(doctorObjectId);
			doctors.put(drCollection.getFirstName().toLowerCase(), drCollection);

			List<DoctorClinicProfileLookupResponse> doctorClinicProfileLookupResponses = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(new Criteria("locationId").is(locationObjectId)),
							Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
							Aggregation.unwind("user")),
					DoctorClinicProfileCollection.class, DoctorClinicProfileLookupResponse.class).getMappedResults();
			if(doctorClinicProfileLookupResponses != null && !doctorClinicProfileLookupResponses.isEmpty()) {
				for(DoctorClinicProfileLookupResponse clinicProfileLookupResponse : doctorClinicProfileLookupResponses) {
					doctors.put(clinicProfileLookupResponse.getUser().getFirstName().toLowerCase(), clinicProfileLookupResponse.getUser());
				}
			}
			AppointmentCollection appointmentCollection = null;

			while ((line = br.readLine()) != null) {

				if (lineCount > 0) {
					String[] fields = line.split(cvsSplitBy);
System.out.println(fields[0] +""+ fields[1]);
					if (!DPDoctorUtils.anyStringEmpty(fields[0], fields[1])) {
						PatientCollection patientCollection = patientRepository.findByLocationIDHospitalIDAndPNUM(
								locationObjectId, hospitalObjectId, fields[1].replace("'", ""));
						if (patientCollection != null) {

							SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d hh:mm:ss");
							String dateSTri = fields[0].replace("'", "");
							dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
							Date fromDate = dateFormat.parse(dateSTri);

							Calendar c = Calendar.getInstance();
							c.setTimeZone(TimeZone.getTimeZone("IST"));
							c.setTime(fromDate);
							int hour = c.get(Calendar.HOUR_OF_DAY);
							int fromTime = hour * 60 + c.get(Calendar.MINUTE);

							WorkingHours workingHours = new WorkingHours();
							workingHours.setFromTime(fromTime);
							workingHours.setToTime(fromTime + 15);

							appointmentCollection = new AppointmentCollection();
							appointmentCollection.setTime(workingHours);
							appointmentCollection.setCreatedTime(fromDate);
							appointmentCollection.setUpdatedTime(fromDate);
							appointmentCollection.setFromDate(fromDate);
							appointmentCollection.setToDate(fromDate);
							appointmentCollection.setAppointmentId(
									UniqueIdInitial.APPOINTMENT.getInitial() + DPDoctorUtils.generateRandomId());

							if (fields.length > 5 && checkIfNotNullOrNone(fields[5])) {
								String state = fields[5].replace("'", "");
								if (state.equalsIgnoreCase("CANCEL") || state.equalsIgnoreCase("CANCELLED"))
									appointmentCollection.setState(AppointmentState.CANCEL);
								else
									appointmentCollection.setState(AppointmentState.CONFIRM);
							}
							if(fields.length > 3 && checkIfNotNullOrNone(fields[3]))
								appointmentCollection.setExplanation(fields[3].replace("'", ""));

							appointmentCollection.setLocationId(locationObjectId);
							appointmentCollection.setHospitalId(hospitalObjectId);
							appointmentCollection.setPatientId(patientCollection.getUserId());

							String drName = fields[4].replace("'", "").replace("Dr. ", "").replace("Dr ", "").replace("Dr.", "").replace("Dr", "");
							UserCollection userCollection = doctors.get(drName.toLowerCase());
							if (userCollection == null) {
								List<UserCollection> collections = mongoTemplate.aggregate(
										Aggregation.newAggregation(
												Aggregation.match(new Criteria("firstName").regex(drName,"i")),
												new CustomAggregationOperation(new BasicDBObject("$redact",
														new BasicDBObject("$cond",
																new BasicDBObject("if",
																		new BasicDBObject("$eq",
																				Arrays.asList("$emailAddress",
																						"$userName")))
																								.append("then",
																										"$$KEEP")
																								.append("else",
																										"$$PRUNE"))))),
										UserCollection.class, UserCollection.class).getMappedResults();
								if (collections != null && !collections.isEmpty()) {
									userCollection = collections.get(0);
									doctors.put(userCollection.getFirstName().toLowerCase(), userCollection);
								}
							}
							if (userCollection != null) {
								appointmentCollection
										.setCreatedBy(userCollection.getTitle() + " " + userCollection.getFirstName());
								appointmentCollection.setDoctorId(userCollection.getId());

								AppointmentCollection appointmentToCheck = appointmentRepository.find(appointmentCollection.getDoctorId(), locationObjectId, hospitalObjectId, 
																	appointmentCollection.getPatientId(), appointmentCollection.getTime().getFromTime(), appointmentCollection.getTime().getToTime(),
																	appointmentCollection.getFromDate(), appointmentCollection.getToDate());
								if(appointmentToCheck == null) {
									appointmentCollection = appointmentRepository.save(appointmentCollection);

									AppointmentBookedSlotCollection bookedSlotCollection = new AppointmentBookedSlotCollection();
									BeanUtil.map(appointmentCollection, bookedSlotCollection);
									bookedSlotCollection.setDoctorId(appointmentCollection.getDoctorId());
									bookedSlotCollection.setLocationId(appointmentCollection.getLocationId());
									bookedSlotCollection.setHospitalId(appointmentCollection.getHospitalId());
									bookedSlotCollection.setId(null);
									appointmentBookedSlotRepository.save(bookedSlotCollection);
									
									System.out.println(fields[0] +"..." +fields[1]+"..." +appointmentCollection.getCreatedBy());
								}else {
									System.out.println("Already present:" +fields[0] +"..." +fields[1]+"..." +appointmentCollection.getCreatedBy());
								}
								
							} else
								dataCountNotUploaded++;
						} else
							dataCountNotUploaded++;
					}
				}
				lineCount++;
				response = true;
			}
			System.out.println("Appointments Done. dataCountNotUploaded: " + dataCountNotUploaded);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return response;
	}

	private Boolean checkIfNotNullOrNone(String value) {
		if (value.equalsIgnoreCase("NONE'") || value.equalsIgnoreCase("NONE") || DPDoctorUtils.allStringsEmpty(value))
			return false;
		else
			return true;

	}

	@Override
	public Boolean uploadTreatmentPlansData(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\\|";
		int dataCountNotUploaded = 0;
		Map<String, UserCollection> doctors = new HashMap<String, UserCollection>();
		int lineCount = 0;
		try {

			br = new BufferedReader(new FileReader(UPLOAD_TREATMENTS_PLAN_DATA_FILE));

			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			UserCollection drCollection = userRepository.findOne(doctorObjectId);
			doctors.put(drCollection.getFirstName(), drCollection);
			List<DoctorClinicProfileLookupResponse> doctorClinicProfileLookupResponses = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(new Criteria("locationId").is(locationObjectId)),
							Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
							Aggregation.unwind("user")),
					DoctorClinicProfileCollection.class, DoctorClinicProfileLookupResponse.class).getMappedResults();
			if(doctorClinicProfileLookupResponses != null && !doctorClinicProfileLookupResponses.isEmpty()) {
				for(DoctorClinicProfileLookupResponse clinicProfileLookupResponse : doctorClinicProfileLookupResponses) {
					doctors.put(clinicProfileLookupResponse.getUser().getFirstName().toLowerCase(), clinicProfileLookupResponse.getUser());
				}
			}
			PatientTreatmentCollection patientTreatmentCollection = null;

			while ((line = br.readLine()) != null) {

				if (lineCount > 0) {
					String[] fields = line.split(cvsSplitBy);
					Boolean createVisit = false;
					if (!DPDoctorUtils.anyStringEmpty(fields[0], fields[1])) {
						PatientCollection patientCollection = patientRepository.findByLocationIDHospitalIDAndPNUM(
								locationObjectId, hospitalObjectId, fields[1].replace("'", ""));
						if (patientCollection != null) {

							SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d hh:mm:ss");
							String dateSTri = fields[0].replace("'", "") + " 13:00:00";
							dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
							Date fromDate = dateFormat.parse(dateSTri);

							String drName = fields[3].replace("'", "").replace("Dr. ", "").replace("Dr ", "");
							UserCollection userCollection = doctors.get(drName.toLowerCase());
							if (userCollection == null) {
								List<UserCollection> collections = mongoTemplate.aggregate(
										Aggregation.newAggregation(
												Aggregation.match(new Criteria("firstName").regex(drName, "i")),
												new CustomAggregationOperation(new BasicDBObject("$redact",
														new BasicDBObject("$cond",
																new BasicDBObject("if",
																		new BasicDBObject("$eq",
																				Arrays.asList("$emailAddress",
																						"$userName")))
																								.append("then",
																										"$$KEEP")
																								.append("else",
																										"$$PRUNE"))))),
										UserCollection.class, UserCollection.class).getMappedResults();
								if (collections != null && !collections.isEmpty()) {
									userCollection = collections.get(0);
									doctors.put(userCollection.getFirstName().toLowerCase(), userCollection);
								}
							}
							if (userCollection != null) {
								Discount totalDiscount = null;
								double totalCost = 0.0;
								double grandTotal = 0.0;

								patientTreatmentCollection = patientTreamentRepository.find(userCollection.getId(),
										locationObjectId, hospitalObjectId, patientCollection.getUserId(), fromDate);

								if (patientTreatmentCollection == null) {
									createVisit = true;
									patientTreatmentCollection = new PatientTreatmentCollection();

									patientTreatmentCollection.setCreatedTime(fromDate);
									patientTreatmentCollection.setUpdatedTime(fromDate);
									patientTreatmentCollection.setFromDate(fromDate);
									patientTreatmentCollection.setUniqueEmrId(
											UniqueIdInitial.TREATMENT.getInitial() + DPDoctorUtils.generateRandomId());
									patientTreatmentCollection.setLocationId(locationObjectId);
									patientTreatmentCollection.setHospitalId(hospitalObjectId);
									patientTreatmentCollection.setPatientId(patientCollection.getUserId());

									if (userCollection != null) {
										patientTreatmentCollection.setCreatedBy(
												userCollection.getTitle() + " " + userCollection.getFirstName());
										patientTreatmentCollection.setDoctorId(userCollection.getId());
									}
								} else {
									totalDiscount = patientTreatmentCollection.getTotalDiscount();
									totalCost = patientTreatmentCollection.getTotalCost();
									grandTotal = patientTreatmentCollection.getGrandTotal();
								}
								List<Treatment> treatments = patientTreatmentCollection.getTreatments();

								String treatmentName = fields[4].replace("'", "");
								List<TreatmentServicesCollection> treatmentServicesCollections = treatmentServicesRepository
										.findByNameAndDoctorLocationHospital(treatmentName, doctorObjectId,
												locationObjectId, hospitalObjectId);

								TreatmentServicesCollection treatmentServicesCollection = null;
								TreatmentService treatmentService = new TreatmentService();
								if (treatmentServicesCollections != null && !treatmentServicesCollections.isEmpty()) {
									treatmentServicesCollection = treatmentServicesCollections.get(0);
									BeanUtil.map(treatmentServicesCollection, treatmentService);
								} else {
									treatmentService.setName(treatmentName);
								}

								if (checkIfNotNullOrNone(fields[5]))
									treatmentService.setCost(Double.parseDouble(fields[5].replace("'", "")));

								treatmentService.setDoctorId(patientTreatmentCollection.getDoctorId().toString());
								treatmentService.setLocationId(patientTreatmentCollection.getLocationId().toString());
								treatmentService.setHospitalId(patientTreatmentCollection.getHospitalId().toString());
								treatmentService = patientTreatmentServices.addFavouritesToService(treatmentService,
										patientTreatmentCollection.getCreatedBy());

								Treatment treatment = new Treatment();
								BeanUtil.map(treatmentService, treatment);
								treatment.setTreatmentServiceId(new ObjectId(treatmentService.getId()));

								if (checkIfNotNullOrNone(fields[6])) {
									Quantity quantity = new Quantity();
									quantity.setType(QuantityEnum.QTY);
									quantity.setValue(Integer.parseInt(fields[6].replace("'", "")));
									treatment.setQuantity(quantity);
								}

								treatment.setFinalCost(treatment.getCost());

								if (checkIfNotNullOrNone(fields[7])) {
									Discount discount = new Discount();
									if (!checkIfNotNullOrNone(fields[8])) {
										discount.setUnit(UnitType.INR);
									} else if ((fields[8].replace("'", "")).equalsIgnoreCase("NUMBER")) {
										discount.setUnit(UnitType.INR);
									} else {
										discount.setUnit(UnitType.valueOf(fields[8].replace("'", "")));
									}
									discount.setValue(Double.parseDouble(fields[7].replace("'", "")));
									treatment.setDiscount(discount);

									if (discount.getUnit().name().equalsIgnoreCase(UnitType.PERCENT.name())) {
										treatment.setFinalCost(treatment.getCost()
												- (treatment.getCost() * (discount.getValue() / 100)));
									} else {
										treatment.setFinalCost(treatment.getCost() - (discount.getValue() / 100));
									}

									if (totalDiscount == null) {
										if (discount.getUnit().name().equalsIgnoreCase(UnitType.PERCENT.name())) {
											totalDiscount = new Discount();
											totalDiscount.setUnit(UnitType.INR);
											totalDiscount.setValue(treatment.getCost() * (discount.getValue() / 100));
										} else {
											totalDiscount = discount;
										}
									} else {
										totalDiscount.setValue(totalDiscount.getValue() + discount.getValue());
									}
								}

								if (checkIfNotNullOrNone(fields[9]))
									treatment.setFinalCost(Double.parseDouble(fields[9].replace("'", "")));

								if (fields.length > 10 && checkIfNotNullOrNone(fields[10]))
									treatment.setNote(fields[10].replace("'", ""));

								if (treatments == null)
									treatments = new ArrayList<Treatment>();
								treatments.add(treatment);

								patientTreatmentCollection.setTreatments(treatments);

								patientTreatmentCollection.setTotalCost(totalCost + treatment.getCost());
								patientTreatmentCollection.setGrandTotal(grandTotal + treatment.getFinalCost());
								patientTreatmentCollection.setTotalDiscount(totalDiscount);

								patientTreatmentCollection = patientTreamentRepository.save(patientTreatmentCollection);

								if (createVisit)
									addRecord(patientTreatmentCollection, VisitedFor.TREATMENT, null,
											patientTreatmentCollection.getPatientId(),
											patientTreatmentCollection.getDoctorId(),
											patientTreatmentCollection.getLocationId(),
											patientTreatmentCollection.getHospitalId(),
											patientTreatmentCollection.getId());
							} else
								dataCountNotUploaded++;

						} else
							dataCountNotUploaded++;
						System.out.println(lineCount + ".." + fields[1]);
					}
				}
				lineCount++;
				response = true;
			}
			System.out.println("treatments Plan Done. dataCountNotUploaded: " + dataCountNotUploaded);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return response;
	}

	@Override
	public Boolean uploadTreatmentData(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\\|";
		int dataCountNotUploaded = 0;
		Map<String, UserCollection> doctors = new HashMap<String, UserCollection>();
		int lineCount = 0;
		try {

			br = new BufferedReader(new FileReader(UPLOAD_TREATMENTS_DATA_FILE));

			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			UserCollection drCollection = userRepository.findOne(doctorObjectId);
			doctors.put(drCollection.getFirstName().toLowerCase(), drCollection);
			List<DoctorClinicProfileLookupResponse> doctorClinicProfileLookupResponses = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(new Criteria("locationId").is(locationObjectId)),
							Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
							Aggregation.unwind("user")),
					DoctorClinicProfileCollection.class, DoctorClinicProfileLookupResponse.class).getMappedResults();
			if(doctorClinicProfileLookupResponses != null && !doctorClinicProfileLookupResponses.isEmpty()) {
				for(DoctorClinicProfileLookupResponse clinicProfileLookupResponse : doctorClinicProfileLookupResponses) {
					doctors.put(clinicProfileLookupResponse.getUser().getFirstName().toLowerCase(), clinicProfileLookupResponse.getUser());
				}
			}
			PatientTreatmentCollection patientTreatmentCollection = null;

			while ((line = br.readLine()) != null) {

				if (lineCount > 0) {
					String[] fields = line.split(cvsSplitBy);
					Boolean createVisit = false;
					if (!DPDoctorUtils.anyStringEmpty(fields[0], fields[1])) {
						PatientCollection patientCollection = patientRepository.findByLocationIDHospitalIDAndPNUM(
								locationObjectId, hospitalObjectId, fields[1].replace("'", ""));
						if (patientCollection != null) {

							SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d hh:mm:ss");
							String dateSTri = fields[0].replace("'", "") + " 13:00:00";
							dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
							Date fromDate = dateFormat.parse(dateSTri);

							String drName = fields[9].replace("'", "").replace("Dr. ", "").replace("Dr ", "");
							UserCollection userCollection = doctors.get(drName.toLowerCase());
							if (userCollection == null) {
								List<UserCollection> collections = mongoTemplate.aggregate(
										Aggregation.newAggregation(
												Aggregation.match(new Criteria("firstName").regex(drName, "i")),
												new CustomAggregationOperation(new BasicDBObject("$redact",
														new BasicDBObject("$cond",
																new BasicDBObject("if",
																		new BasicDBObject("$eq",
																				Arrays.asList("$emailAddress",
																						"$userName")))
																								.append("then",
																										"$$KEEP")
																								.append("else",
																										"$$PRUNE"))))),
										UserCollection.class, UserCollection.class).getMappedResults();
								if (collections != null && !collections.isEmpty()) {
									userCollection = collections.get(0);
									doctors.put(userCollection.getFirstName().toLowerCase(), userCollection);
								}
							}
							
							patientTreatmentCollection = patientTreamentRepository.find(userCollection.getId(),
									locationObjectId, hospitalObjectId, patientCollection.getUserId(), fromDate);

							Discount totalDiscount = null;
							double totalCost = 0.0;
							double grandTotal = 0.0;

							if (patientTreatmentCollection == null) {
								createVisit = true;
								patientTreatmentCollection = new PatientTreatmentCollection();

								patientTreatmentCollection.setCreatedTime(fromDate);
								patientTreatmentCollection.setUpdatedTime(fromDate);
								patientTreatmentCollection.setFromDate(fromDate);
								patientTreatmentCollection.setUniqueEmrId(
										UniqueIdInitial.TREATMENT.getInitial() + DPDoctorUtils.generateRandomId());
								patientTreatmentCollection.setLocationId(locationObjectId);
								patientTreatmentCollection.setHospitalId(hospitalObjectId);
								patientTreatmentCollection.setPatientId(patientCollection.getUserId());

								patientTreatmentCollection
										.setCreatedBy(userCollection.getTitle() + " " + userCollection.getFirstName());
								patientTreatmentCollection.setDoctorId(userCollection.getId());
							} else {
								totalDiscount = patientTreatmentCollection.getTotalDiscount();
								totalCost = patientTreatmentCollection.getTotalCost();
								grandTotal = patientTreatmentCollection.getGrandTotal();
							}
							List<Treatment> treatments = patientTreatmentCollection.getTreatments();

							String treatmentName = fields[3].replace("'", "");
							List<TreatmentServicesCollection> treatmentServicesCollections = treatmentServicesRepository
									.findByNameAndDoctorLocationHospital(treatmentName, doctorObjectId,
											locationObjectId, hospitalObjectId);

							TreatmentServicesCollection treatmentServicesCollection = null;
							TreatmentService treatmentService = new TreatmentService();
							if (treatmentServicesCollections != null && !treatmentServicesCollections.isEmpty()) {
								treatmentServicesCollection = treatmentServicesCollections.get(0);
								BeanUtil.map(treatmentServicesCollection, treatmentService);
							} else {
								treatmentService.setName(treatmentName);
							}

							if (checkIfNotNullOrNone(fields[6]))
								treatmentService.setCost(Double.parseDouble(fields[6].replace("'", "")));

							treatmentService.setDoctorId(patientTreatmentCollection.getDoctorId().toString());
							treatmentService.setLocationId(patientTreatmentCollection.getLocationId().toString());
							treatmentService.setHospitalId(patientTreatmentCollection.getHospitalId().toString());
							treatmentService = patientTreatmentServices.addFavouritesToService(treatmentService,
									patientTreatmentCollection.getCreatedBy());

							Treatment treatment = new Treatment();
							BeanUtil.map(treatmentService, treatment);
							treatment.setTreatmentServiceId(new ObjectId(treatmentService.getId()));
							if(checkIfNotNullOrNone(fields[4])) {
								List<Fields> treatmentFieldList = new ArrayList<>();
								Fields treatmentFields = new Fields();
								treatmentFields.setKey("toothNumber");treatmentFields.setValue(fields[4].replace("'", ""));
								treatmentFieldList.add(treatmentFields);
								treatment.setTreatmentFields(treatmentFieldList);
							}

							if (checkIfNotNullOrNone(fields[5]))
								treatment.setNote(fields[5].replace("'", ""));

							treatment.setFinalCost(treatment.getCost());

							if (checkIfNotNullOrNone(fields[7])) {
								Discount discount = new Discount();
								if (!checkIfNotNullOrNone(fields[8])) {
									discount.setUnit(UnitType.INR);
								} else if ((fields[8].replace("'", "")).equalsIgnoreCase("NUMBER")) {
									discount.setUnit(UnitType.INR);
								} else {
									discount.setUnit(UnitType.valueOf(fields[8].replace("'", "")));
								}
								discount.setValue(Double.parseDouble(fields[7].replace("'", "")));
								treatment.setDiscount(discount);

								if (discount.getUnit().name().equalsIgnoreCase(UnitType.PERCENT.name())) {
									treatment.setFinalCost(
											treatment.getCost() - (treatment.getCost() * (discount.getValue() / 100)));
								} else {
									treatment.setFinalCost(treatment.getCost() - (discount.getValue() / 100));
								}

								if (totalDiscount == null) {
									if (discount.getUnit().name().equalsIgnoreCase(UnitType.PERCENT.name())) {
										totalDiscount = new Discount();
										totalDiscount.setUnit(UnitType.INR);
										totalDiscount.setValue(treatment.getCost() * (discount.getValue() / 100));
									} else {
										totalDiscount = discount;
									}
								} else {
									totalDiscount.setValue(totalDiscount.getValue() + discount.getValue());
								}
							}

							if (treatments == null)
								treatments = new ArrayList<Treatment>();
							treatments.add(treatment);
							patientTreatmentCollection.setTreatments(treatments);

							patientTreatmentCollection.setTotalCost(totalCost + treatment.getCost());
							patientTreatmentCollection.setGrandTotal(grandTotal + treatment.getFinalCost());
							patientTreatmentCollection.setTotalDiscount(totalDiscount);

							patientTreatmentCollection = patientTreamentRepository.save(patientTreatmentCollection);
							if (createVisit)
								addRecord(patientTreatmentCollection, VisitedFor.TREATMENT, null,
										patientTreatmentCollection.getPatientId(),
										patientTreatmentCollection.getDoctorId(),
										patientTreatmentCollection.getLocationId(),
										patientTreatmentCollection.getHospitalId(), patientTreatmentCollection.getId());
						} else {
							dataCountNotUploaded++;
						}
					}
				}
				lineCount++;
				response = true;
			}
			System.out.println("Treatments Done. dataCountNotUploaded: " + dataCountNotUploaded);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return response;
	}

	@Override
	public Boolean assignPNUMToPatientsHavingPNUMAsNull(String doctorId, String locationId, String hospitalId) {
		try {
			System.out.println(patientInitial);
			if (DPDoctorUtils.anyStringEmpty(patientInitial))
				patientInitial = "P";

			ObjectId locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			List<PatientCollection> patientCollections = patientRepository
					.findByLocationIDHospitalIDAndNullPNUM(locationObjectId, hospitalObjectId);
			Integer patientCount = patientRepository.findCountByLocationIDHospitalIDAndNotPNUM(locationObjectId,
					hospitalObjectId, null);
			if (patientCollections != null && !patientCollections.isEmpty()) {
				for (PatientCollection patientCollection : patientCollections) {
					patientCollection.setPNUM(patientInitial + patientCount);
					patientRepository.save(patientCollection);
					patientCount++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public String addRecord(Object details, VisitedFor visitedFor, String visitId, ObjectId patientId,
			ObjectId doctorId, ObjectId locationId, ObjectId hospitalId, ObjectId id) {
		PatientVisitCollection patientVisitCollection = new PatientVisitCollection();
		try {

			BeanUtil.map(details, patientVisitCollection);

			patientVisitCollection.setPatientId(patientId);
			patientVisitCollection.setDoctorId(doctorId);
			patientVisitCollection.setLocationId(locationId);
			patientVisitCollection.setHospitalId(hospitalId);

			patientVisitCollection
					.setUniqueEmrId(UniqueIdInitial.VISITS.getInitial() + DPDoctorUtils.generateRandomId());

			List<VisitedFor> visitedforList = new ArrayList<VisitedFor>();
			visitedforList.add(visitedFor);
			patientVisitCollection.setVisitedFor(visitedforList);

			patientVisitCollection.setVisitedTime(patientVisitCollection.getCreatedTime());
			if (visitedFor.equals(VisitedFor.PRESCRIPTION)) {
				if (patientVisitCollection.getPrescriptionId() == null) {
					List<ObjectId> prescriptionId = new ArrayList<ObjectId>();
					prescriptionId.add(id);
					patientVisitCollection.setPrescriptionId(prescriptionId);
				} else {
					if (!patientVisitCollection.getPrescriptionId().contains(id))
						patientVisitCollection.getPrescriptionId().add(id);
				}

			} else if (visitedFor.equals(VisitedFor.CLINICAL_NOTES)) {
				if (patientVisitCollection.getClinicalNotesId() == null) {
					List<ObjectId> clinicalNotes = new ArrayList<ObjectId>();
					clinicalNotes.add(id);
					patientVisitCollection.setClinicalNotesId(clinicalNotes);
				} else {
					if (!patientVisitCollection.getClinicalNotesId().contains(id))
						patientVisitCollection.getClinicalNotesId().add(id);
				}
			} else if (visitedFor.equals(VisitedFor.REPORTS)) {
				if (patientVisitCollection.getRecordId() == null) {
					List<ObjectId> recordId = new ArrayList<ObjectId>();
					recordId.add(id);
					patientVisitCollection.setRecordId(recordId);
				} else {
					if (!patientVisitCollection.getRecordId().contains(id))
						patientVisitCollection.getRecordId().add(id);
				}
			} else if (visitedFor.equals(VisitedFor.TREATMENT)) {
				if (patientVisitCollection.getTreatmentId() == null) {
					List<ObjectId> treatmentId = new ArrayList<ObjectId>();
					treatmentId.add(id);
					patientVisitCollection.setTreatmentId(treatmentId);
				} else {
					if (!patientVisitCollection.getTreatmentId().add(id))
						patientVisitCollection.getTreatmentId().add(id);
				}
			}

			else if (visitedFor.equals(VisitedFor.EYE_PRESCRIPTION)) {
				if (patientVisitCollection.getEyePrescriptionId() == null) {
					patientVisitCollection.setEyePrescriptionId(id);
				}
			}
			patientVisitCollection.setId(null);
			patientVisitCollection = patientVisitRepository.save(patientVisitCollection);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return patientVisitCollection.getId().toString();
	}

	@Override
	public Boolean updateEMR() {

		try {
			Aggregation aggregation = Aggregation.newAggregation(
					Aggregation.lookup("opd_report_cl", "_id", "prescriptionId", "opd"),
					new CustomAggregationOperation(new BasicDBObject("$unwind",
							new BasicDBObject("path", "$opd").append("preserveNullAndEmptyArrays", true))),
					Aggregation.match(new Criteria("opd.prescriptionId").exists(false)));
			List<PrescriptionCollection> prescriptionCollections = mongoTemplate
					.aggregate(aggregation, "prescription_cl", PrescriptionCollection.class).getMappedResults();
			OPDReportsCollection opdReportsCollection = null;
			List<OPDReportsCollection> opdReportsCollections = new ArrayList<OPDReportsCollection>();
			if (prescriptionCollections != null && !prescriptionCollections.isEmpty()) {
				for (PrescriptionCollection prescriptionCollection : prescriptionCollections) {
					OPDReports opdReports = new OPDReports(String.valueOf(prescriptionCollection.getPatientId()),
							String.valueOf(prescriptionCollection.getId()),
							String.valueOf(prescriptionCollection.getDoctorId()),
							String.valueOf(prescriptionCollection.getLocationId()),
							String.valueOf(prescriptionCollection.getHospitalId()),
							prescriptionCollection.getCreatedTime());
					opdReportsCollection = new OPDReportsCollection();
					UserCollection userCollection = null;
					if (!DPDoctorUtils.anyStringEmpty(opdReports.getDoctorId())) {
						userCollection = userRepository.findOne(new ObjectId(opdReports.getDoctorId()));
					}
					BeanUtil.map(opdReports, opdReportsCollection);
					if (userCollection != null) {
						opdReportsCollection.setCreatedBy((!DPDoctorUtils.anyStringEmpty(userCollection.getTitle())
								? userCollection.getTitle() : "DR.") + " " + userCollection.getFirstName());
					}
					opdReportsCollection.setAdminCreatedTime(prescriptionCollection.getAdminCreatedTime());
					if (prescriptionCollection.getAdminCreatedTime() == null) {
						opdReportsCollection.setAdminCreatedTime(new Date());
					}
					if (opdReports.getCreatedTime() == null) {
						opdReportsCollection.setCreatedTime(new Date());
					}
					opdReportsCollections.add(opdReportsCollection);
				}
			}
			opdReportsRepository.save(opdReportsCollections);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public Boolean uploadTreatmentServicesData(String doctorId, String locationId, String hospitalId) {
//		Boolean response = false;
//		BufferedReader br = null;
//		String line = "";
//		String cvsSplitBy = ",";
//		int dataCountNotUploaded = 0;
//		int lineCount = 0;
//		try {
//
//			br = new BufferedReader(new FileReader(UPLOAD_TREATMENT_SERVICES_DATA_FILE));
//
//			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
//			if (!DPDoctorUtils.anyStringEmpty(doctorId))
//				doctorObjectId = new ObjectId(doctorId);
//			if (!DPDoctorUtils.anyStringEmpty(locationId))
//				locationObjectId = new ObjectId(locationId);
//			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
//				hospitalObjectId = new ObjectId(hospitalId);
//
//			UserCollection drCollection = userRepository.findOne(doctorObjectId);
//			
//			TreatmentServicesCollection treatmentServicesCollection = null;
//
//			while ((line = br.readLine()) != null) {
//
//				if (lineCount > 0) {
//					String[] fields = line.split(cvsSplitBy);
//					
////					treatmentServicesCollection = treatmentServicesRepository.fin
//						
//					
//				}
//				lineCount++;
//				response = true;
//			}
//			System.out.println("Treatments Done. dataCountNotUploaded: " + dataCountNotUploaded);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (br != null) {
//				try {
//					br.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
		return null;
	}

	@Override
	public Boolean uploadClinicalNotesData(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\\|";
		int lineCount = 0;
		int dataCountNotUploaded = 0;
		Map<String, UserCollection> doctors = new HashMap<String, UserCollection>();
		try {

			br = new BufferedReader(new FileReader(UPLOAD_CLINICAL_NOTES_DATA_FILE));

			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			
			UserCollection drCollection = userRepository.findOne(doctorObjectId);
			doctors.put(drCollection.getFirstName().toLowerCase(), drCollection);
			List<DoctorClinicProfileLookupResponse> doctorClinicProfileLookupResponses = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(new Criteria("locationId").is(locationObjectId)),
							Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
							Aggregation.unwind("user")),
					DoctorClinicProfileCollection.class, DoctorClinicProfileLookupResponse.class).getMappedResults();
			if(doctorClinicProfileLookupResponses != null && !doctorClinicProfileLookupResponses.isEmpty()) {
				for(DoctorClinicProfileLookupResponse clinicProfileLookupResponse : doctorClinicProfileLookupResponses) {
					doctors.put(clinicProfileLookupResponse.getUser().getFirstName().toLowerCase(), clinicProfileLookupResponse.getUser());
				}
			}
			ClinicalNotesCollection clinicalNotesCollection = null;

			while ((line = br.readLine()) != null) {

				if (lineCount > 0) {
					Boolean createVisit = false;
					String[] fields = line.split(cvsSplitBy);
System.out.println(lineCount +".."+ fields[2]);
					if (!DPDoctorUtils.anyStringEmpty(fields[1], fields[4])) {
						PatientCollection patientCollection = patientRepository.findByLocationIDHospitalIDAndPNUM(
								locationObjectId, hospitalObjectId, fields[1].replace("'", ""));
						if (patientCollection != null) {

							Date createdTime = new Date();
							if (!DPDoctorUtils.anyStringEmpty(fields[0])) {
								SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d hh:mm:ss");
								String dateSTri = fields[0].replace("'", "")+ " 13:00:00";
								dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
								createdTime = dateFormat.parse(dateSTri);
							}

							String drName = fields[3].replace("'", "").replace("Dr. ", "").replace("Dr ", "");
							UserCollection userCollection = doctors.get(drName.toLowerCase());
							if (userCollection == null) {
								List<UserCollection> collections = mongoTemplate.aggregate(
										Aggregation.newAggregation(
												Aggregation.match(new Criteria("firstName").regex(drName,"i")),
												new CustomAggregationOperation(new BasicDBObject("$redact",
														new BasicDBObject("$cond",
																new BasicDBObject("if",
																		new BasicDBObject("$eq",
																				Arrays.asList("$emailAddress",
																						"$userName")))
																								.append("then",
																										"$$KEEP")
																								.append("else",
																										"$$PRUNE"))))),
										UserCollection.class, UserCollection.class).getMappedResults();
								if (collections != null && !collections.isEmpty()) {
									userCollection = collections.get(0);
									doctors.put(userCollection.getFirstName().toLowerCase(), userCollection);
								}
							}
								
							clinicalNotesCollection = clinicalNotesRepository.find(userCollection.getId(), locationObjectId,
									hospitalObjectId, patientCollection.getUserId(), createdTime);
							if (clinicalNotesCollection == null) {
								createVisit = true;
								clinicalNotesCollection = new ClinicalNotesCollection();

								clinicalNotesCollection.setDoctorId(doctorObjectId);
								clinicalNotesCollection.setLocationId(locationObjectId);
								clinicalNotesCollection.setHospitalId(hospitalObjectId);
								clinicalNotesCollection.setPatientId(patientCollection.getUserId());
								clinicalNotesCollection.setCreatedTime(createdTime);
								clinicalNotesCollection.setUpdatedTime(createdTime);
								clinicalNotesCollection.setUniqueEmrId(
										UniqueIdInitial.CLINICALNOTES.getInitial() + DPDoctorUtils.generateRandomId());
								clinicalNotesCollection.setCreatedBy(
										(userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
												+ userCollection.getFirstName());

							}
							
							String type = fields.length > 4 ? fields[4].replace("'", ""):"";
							
							String description = fields.length > 5 ? fields[5].replace("'", "") : "";
							
							
							if(type.equalsIgnoreCase("diagnoses")) {
								if(DPDoctorUtils.allStringsEmpty(clinicalNotesCollection.getDiagnosis())) {
									clinicalNotesCollection.setDiagnosis(description);
								}else {
									clinicalNotesCollection.setDiagnosis(clinicalNotesCollection.getDiagnosis() + ", " +description);
								}
							}else if(type.equalsIgnoreCase("complaints")) {
								if(DPDoctorUtils.allStringsEmpty(clinicalNotesCollection.getComplaint())) {
									clinicalNotesCollection.setComplaint(description);
								}else {
									clinicalNotesCollection.setComplaint(clinicalNotesCollection.getComplaint() + ", " +description);
								}
							}else if(type.equalsIgnoreCase("treatmentnotes")) {
								if(DPDoctorUtils.allStringsEmpty(clinicalNotesCollection.getProcedureNote())) {
									clinicalNotesCollection.setProcedureNote(description);
								}else {
									clinicalNotesCollection.setProcedureNote(clinicalNotesCollection.getProcedureNote() + ", " +description);
								}
							}else if(type.equalsIgnoreCase("observations")) {
								if(DPDoctorUtils.allStringsEmpty(clinicalNotesCollection.getObservation())) {
									clinicalNotesCollection.setObservation(description);
								}else {
									clinicalNotesCollection.setObservation(clinicalNotesCollection.getObservation() + ", " +description);
								}
							}else if(type.equalsIgnoreCase("investigations")) {
								if(DPDoctorUtils.allStringsEmpty(clinicalNotesCollection.getInvestigation())) {
									clinicalNotesCollection.setInvestigation(description);
								}else {
									clinicalNotesCollection.setInvestigation(clinicalNotesCollection.getInvestigation() + ", " +description);
								}
							}
								
								

							clinicalNotesCollection = clinicalNotesRepository.save(clinicalNotesCollection);

							if (createVisit)
								addRecord(clinicalNotesCollection, VisitedFor.CLINICAL_NOTES, null,
										clinicalNotesCollection.getPatientId(), clinicalNotesCollection.getDoctorId(),
										clinicalNotesCollection.getLocationId(), clinicalNotesCollection.getHospitalId(),
										clinicalNotesCollection.getId());

						} else {
							dataCountNotUploaded++;
						}
					}
				}
				lineCount++;
				response = true;
			}
			System.out.println("CN Done. dataCountNotUploaded: " + dataCountNotUploaded);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return response;
	}

	@Override
	public Boolean uploadInvoicesData(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\\|";
		int lineCount = 0;
		int dataCountNotUploaded = 0;
		Map<String, UserCollection> doctors = new HashMap<String, UserCollection>();
		try {

			br = new BufferedReader(new FileReader(UPLOAD_INVOICES_DATA_FILE));

			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			
			UserCollection drCollection = userRepository.findOne(doctorObjectId);
			doctors.put(drCollection.getFirstName().toLowerCase(), drCollection);
			List<DoctorClinicProfileLookupResponse> doctorClinicProfileLookupResponses = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(new Criteria("locationId").is(locationObjectId)),
							Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
							Aggregation.unwind("user")),
					DoctorClinicProfileCollection.class, DoctorClinicProfileLookupResponse.class).getMappedResults();
			if(doctorClinicProfileLookupResponses != null && !doctorClinicProfileLookupResponses.isEmpty()) {
				for(DoctorClinicProfileLookupResponse clinicProfileLookupResponse : doctorClinicProfileLookupResponses) {
					doctors.put(clinicProfileLookupResponse.getUser().getFirstName().toLowerCase(), clinicProfileLookupResponse.getUser());
				}
			}
			DoctorPatientInvoiceCollection doctorPatientInvoiceCollection = null;

			while ((line = br.readLine()) != null) {

				if (lineCount > 0) {
					String[] fields = line.split(cvsSplitBy);
System.out.println(lineCount +".."+ fields[1]+".."+ fields[5]);
					if (!DPDoctorUtils.anyStringEmpty(fields[1], fields[5])) {
						PatientCollection patientCollection = patientRepository.findByLocationIDHospitalIDAndPNUM(
								locationObjectId, hospitalObjectId, fields[1].replace("'", ""));
						if (patientCollection != null) {

							Date createdTime = new Date();
							if (!DPDoctorUtils.anyStringEmpty(fields[0])) {
								SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d hh:mm:ss");
								String dateSTri = fields[0].replace("'", "")+ " 13:00:00";
								dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
								createdTime = dateFormat.parse(dateSTri);
							}

							String drName = fields[3].replace("'", "").replace("Dr. ", "").replace("Dr ", "");
							UserCollection userCollection = doctors.get(drName.toLowerCase());
							if (userCollection == null) {
								List<UserCollection> collections = mongoTemplate.aggregate(
										Aggregation.newAggregation(
												Aggregation.match(new Criteria("firstName").regex(drName,"i")),
												new CustomAggregationOperation(new BasicDBObject("$redact",
														new BasicDBObject("$cond",
																new BasicDBObject("if",
																		new BasicDBObject("$eq",
																				Arrays.asList("$emailAddress",
																						"$userName")))
																								.append("then",
																										"$$KEEP")
																								.append("else",
																										"$$PRUNE"))))),
										UserCollection.class, UserCollection.class).getMappedResults();
								if (collections != null && !collections.isEmpty()) {
									userCollection = collections.get(0);
									doctors.put(userCollection.getFirstName().toLowerCase(), userCollection);
								}
							}
							
							Discount totalDiscount = null;
							double totalCost = 0.0;
							double grandTotal = 0.0;
							
							doctorPatientInvoiceCollection = doctorPatientInvoiceRepository.find(fields[4].replace("'", ""), userCollection.getId(), locationObjectId, hospitalObjectId);
							if (doctorPatientInvoiceCollection == null) {
								System.out.println("new invoice");
								doctorPatientInvoiceCollection = new DoctorPatientInvoiceCollection();

								doctorPatientInvoiceCollection.setDoctorId(userCollection.getId());
								doctorPatientInvoiceCollection.setLocationId(locationObjectId);
								doctorPatientInvoiceCollection.setHospitalId(hospitalObjectId);
								doctorPatientInvoiceCollection.setPatientId(patientCollection.getUserId());
								doctorPatientInvoiceCollection.setInvoiceDate(createdTime);
								doctorPatientInvoiceCollection.setCreatedTime(createdTime);
								doctorPatientInvoiceCollection.setUpdatedTime(createdTime);
								doctorPatientInvoiceCollection.setAdminCreatedTime(createdTime);
								doctorPatientInvoiceCollection.setUniqueInvoiceId(fields[4].replace("'", ""));
								doctorPatientInvoiceCollection.setCreatedBy(
										(userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
												+ userCollection.getFirstName());

							}else {System.out.println("not new invoice");
								totalDiscount = doctorPatientInvoiceCollection.getTotalDiscount();
								totalCost = doctorPatientInvoiceCollection.getTotalCost();
								grandTotal = doctorPatientInvoiceCollection.getGrandTotal();
							}
							
							List<InvoiceItem> invoiceItems = doctorPatientInvoiceCollection.getInvoiceItems();
							if(invoiceItems == null) invoiceItems = new ArrayList<InvoiceItem>();
							
							InvoiceItem invoiceItem = new InvoiceItem();
							invoiceItem.setDoctorId(userCollection.getId());
							invoiceItem.setDoctorName(doctorPatientInvoiceCollection.getCreatedBy());
							invoiceItem.setName(fields[5].replace("'", ""));invoiceItem.setType(InvoiceItemType.SERVICE);
							
							if (fields.length > 7 && checkIfNotNullOrNone(fields[7])) {
								Quantity quantity = new Quantity();
								quantity.setType(QuantityEnum.QTY);
								quantity.setValue(Integer.parseInt(fields[7].replace("'", "")));
								invoiceItem.setQuantity(quantity);
							}
							
							if (fields.length > 6 && checkIfNotNullOrNone(fields[6]))
								invoiceItem.setCost(Double.parseDouble(fields[6].replace("'", "")));
							
							
							if (fields.length > 8 && checkIfNotNullOrNone(fields[8])) {
								Discount discount = new Discount();
								if (fields.length > 9 && !checkIfNotNullOrNone(fields[9])) {
									discount.setUnit(UnitType.INR);
								} else if ((fields[9].replace("'", "")).equalsIgnoreCase("NUMBER")) {
									discount.setUnit(UnitType.INR);
								} else {
									discount.setUnit(UnitType.valueOf(fields[9].replace("'", "")));
								}
								discount.setValue(Double.parseDouble(fields[8].replace("'", "")));
								invoiceItem.setDiscount(discount);

								if (discount.getUnit().name().equalsIgnoreCase(UnitType.PERCENT.name())) {
									invoiceItem.setFinalCost(
											invoiceItem.getCost() - (invoiceItem.getCost() * (discount.getValue() / 100)));
								} else {
									invoiceItem.setFinalCost(invoiceItem.getCost() - (discount.getValue() / 100));
								}

								if (totalDiscount == null) {
									if (discount.getUnit().name().equalsIgnoreCase(UnitType.PERCENT.name())) {
										totalDiscount = new Discount();
										totalDiscount.setUnit(UnitType.INR);
										totalDiscount.setValue(invoiceItem.getCost() * (discount.getValue() / 100));
									} else {
										totalDiscount = discount;
									}
								} else {
									double value = 0;
									if (discount.getUnit().name().equalsIgnoreCase(UnitType.PERCENT.name())) {
										value = invoiceItem.getCost() * (discount.getValue() / 100);
									} 
									
									totalDiscount.setValue(totalDiscount.getValue() + value);
								}
							}
							
							if (fields.length > 16 && checkIfNotNullOrNone(fields[16]))
								invoiceItem.setNote(fields[16].replace("'", ""));
							
							invoiceItems.add(invoiceItem);
							totalCost = totalCost + invoiceItem.getCost();
							grandTotal = grandTotal + invoiceItem.getFinalCost();
							
							doctorPatientInvoiceCollection.setInvoiceItems(invoiceItems);
							doctorPatientInvoiceCollection.setGrandTotal(grandTotal);
							doctorPatientInvoiceCollection.setTotalCost(totalCost);
							doctorPatientInvoiceCollection.setTotalDiscount(totalDiscount);
							doctorPatientInvoiceCollection.setBalanceAmount(grandTotal);
							
							if (fields.length > 14 && fields[14].equalsIgnoreCase("'1'")) {
								doctorPatientInvoiceCollection.setDiscarded(true);
							}
							
							System.out.println(doctorPatientInvoiceCollection);
							doctorPatientInvoiceCollection = doctorPatientInvoiceRepository.save(doctorPatientInvoiceCollection);
							
							
							DoctorPatientLedgerCollection doctorPatientLedgerCollection = doctorPatientLedgerRepository
									.findByInvoiceId(doctorPatientInvoiceCollection.getId());
							if (doctorPatientLedgerCollection == null) {
								doctorPatientLedgerCollection = new DoctorPatientLedgerCollection();
								doctorPatientLedgerCollection.setPatientId(doctorPatientInvoiceCollection.getPatientId());
								doctorPatientLedgerCollection.setLocationId(doctorPatientInvoiceCollection.getLocationId());
								doctorPatientLedgerCollection.setHospitalId(doctorPatientInvoiceCollection.getHospitalId());
								doctorPatientLedgerCollection.setInvoiceId(doctorPatientInvoiceCollection.getId());
								doctorPatientLedgerCollection.setDebitAmount(doctorPatientInvoiceCollection.getBalanceAmount());
								doctorPatientLedgerCollection.setCreatedTime(new Date());
								doctorPatientLedgerCollection.setUpdatedTime(new Date());
							} else {
								doctorPatientLedgerCollection.setDebitAmount(doctorPatientInvoiceCollection.getBalanceAmount());
								doctorPatientLedgerCollection.setUpdatedTime(new Date());
							}
							doctorPatientLedgerCollection = doctorPatientLedgerRepository.save(doctorPatientLedgerCollection);

							DoctorPatientDueAmountCollection doctorPatientDueAmountCollection = doctorPatientDueAmountRepository
									.find(doctorPatientInvoiceCollection.getPatientId(),
											doctorPatientInvoiceCollection.getDoctorId(),
											doctorPatientInvoiceCollection.getLocationId(),
											doctorPatientInvoiceCollection.getHospitalId());

							if (doctorPatientDueAmountCollection == null) {
								doctorPatientDueAmountCollection = new DoctorPatientDueAmountCollection();
								doctorPatientDueAmountCollection.setDoctorId(doctorPatientInvoiceCollection.getDoctorId());
								doctorPatientDueAmountCollection.setHospitalId(doctorPatientInvoiceCollection.getHospitalId());
								doctorPatientDueAmountCollection.setLocationId(doctorPatientInvoiceCollection.getLocationId());
								doctorPatientDueAmountCollection.setPatientId(doctorPatientInvoiceCollection.getPatientId());
								doctorPatientDueAmountCollection.setDueAmount(0.0);
							}
							doctorPatientDueAmountCollection
									.setDueAmount(doctorPatientDueAmountCollection.getDueAmount() + invoiceItem.getFinalCost());
							doctorPatientDueAmountRepository.save(doctorPatientDueAmountCollection);
							System.out.println(fields[1]+".."+fields[5]);
						} else {
							dataCountNotUploaded++;
						}
					}else {
						dataCountNotUploaded++;
					}
				}
				lineCount++;
				response = true;
			}
			System.out.println("Invoices Done. dataCountNotUploaded: " + dataCountNotUploaded);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return response;
	}

	@Override
	public Boolean uploadPaymentsData(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\\|";
		int lineCount = 0;
		int dataCountNotUploaded = 0;
		try {

			br = new BufferedReader(new FileReader(UPLOAD_PAYMENTS_DATA_FILE));

			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			LocationCollection  locationCollection = locationRepository.findOne(locationObjectId);
			if(locationCollection!= null) {
				locationCollection.setInvoiceInitial("INV");
				locationCollection.setReceiptInitial("RCPT");
				locationCollection = locationRepository.save(locationCollection);
			}
			UserCollection drCollection = userRepository.findOne(doctorObjectId);
			DoctorPatientReceiptCollection doctorPatientReceiptCollection = null;

			while ((line = br.readLine()) != null) {

				if (lineCount > 0) {
					String[] fields = line.split(cvsSplitBy);
System.out.println(lineCount +".."+ fields[2]);
					if (!DPDoctorUtils.anyStringEmpty(fields[1])) {
						PatientCollection patientCollection = patientRepository.findByLocationIDHospitalIDAndPNUM(
								locationObjectId, hospitalObjectId, fields[1].replace("'", ""));
						if (patientCollection != null) {

							Date createdTime = new Date();
							if (!DPDoctorUtils.anyStringEmpty(fields[0])) {
								SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d hh:mm:ss");
								String dateSTri = fields[0].replace("'", "")+ " 13:00:00";
								dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
								createdTime = dateFormat.parse(dateSTri);
							}

							doctorPatientReceiptCollection = doctorPatientReceiptRepository.find(fields[3].replace("'", ""), locationObjectId, hospitalObjectId);
							if (doctorPatientReceiptCollection == null) {
								doctorPatientReceiptCollection = new DoctorPatientReceiptCollection();

								doctorPatientReceiptCollection.setDoctorId(doctorObjectId);
								doctorPatientReceiptCollection.setLocationId(locationObjectId);
								doctorPatientReceiptCollection.setHospitalId(hospitalObjectId);
								doctorPatientReceiptCollection.setPatientId(patientCollection.getUserId());
								doctorPatientReceiptCollection.setReceivedDate(createdTime);
								doctorPatientReceiptCollection.setCreatedTime(createdTime);
								doctorPatientReceiptCollection.setUpdatedTime(createdTime);
								doctorPatientReceiptCollection.setAdminCreatedTime(createdTime);
								doctorPatientReceiptCollection.setUniqueInvoiceId(fields[4].replace("'", ""));
								doctorPatientReceiptCollection.setCreatedBy(
										(drCollection.getTitle() != null ? drCollection.getTitle() + " " : "")
												+ drCollection.getFirstName());

							}
							
							DoctorPatientInvoiceCollection doctorPatientInvoiceCollection = null;
							if (fields.length > 14 && fields[14].equalsIgnoreCase("'1'")) {
								doctorPatientInvoiceCollection = doctorPatientInvoiceRepository.find(fields[6].replace("'", ""), doctorObjectId, locationObjectId, hospitalObjectId);
								doctorPatientReceiptCollection.setDiscarded(true);
								doctorPatientReceiptCollection.setAmountPaid(Double.parseDouble(fields[5].replace("'", "")));
								
								doctorPatientReceiptCollection.setBalanceAmount(doctorPatientInvoiceCollection.getBalanceAmount() - doctorPatientReceiptCollection.getAmountPaid());
								doctorPatientReceiptCollection.setInvoiceId(doctorPatientInvoiceCollection.getId());
								doctorPatientInvoiceCollection.setUniqueInvoiceId(doctorPatientInvoiceCollection.getUniqueInvoiceId());
								
							}else {
								
								if(fields.length > 4 && checkIfNotNullOrNone(fields[4])) {
									doctorPatientInvoiceCollection = doctorPatientInvoiceRepository.find(fields[6].replace("'", ""), doctorObjectId, locationObjectId, hospitalObjectId);
									if(doctorPatientInvoiceCollection == null)break;
									doctorPatientReceiptCollection.setReceiptType(ReceiptType.INVOICE);
									doctorPatientReceiptCollection.setAmountPaid(Double.parseDouble(fields[5].replace("'", "")));
									doctorPatientReceiptCollection.setBalanceAmount(doctorPatientInvoiceCollection.getBalanceAmount() - doctorPatientReceiptCollection.getAmountPaid());
									doctorPatientReceiptCollection.setInvoiceId(doctorPatientInvoiceCollection.getId());
									doctorPatientInvoiceCollection.setUniqueInvoiceId(doctorPatientInvoiceCollection.getUniqueInvoiceId());
									
									doctorPatientInvoiceCollection.setBalanceAmount(doctorPatientReceiptCollection.getBalanceAmount());
									
									doctorPatientReceiptCollection.setCreatedBy(doctorPatientInvoiceCollection.getCreatedBy());
									doctorPatientReceiptCollection.setDoctorId(doctorPatientInvoiceCollection.getDoctorId());
									doctorObjectId = doctorPatientInvoiceCollection.getDoctorId();
								}else {
									doctorPatientReceiptCollection.setReceiptType(ReceiptType.ADVANCE);
									doctorPatientReceiptCollection.setRemainingAdvanceAmount(Double.parseDouble(fields[5].replace("'", "")));
									doctorPatientReceiptCollection.setAmountPaid(Double.parseDouble(fields[5].replace("'", "")));
									doctorPatientReceiptCollection.setBalanceAmount(0.0);
								}
								
							}
							doctorPatientReceiptCollection.setModeOfPayment(ModeOfPayment.CASH);
							doctorPatientReceiptCollection = doctorPatientReceiptRepository.save(doctorPatientReceiptCollection);
							
							if(doctorPatientInvoiceCollection != null) {
								List<ObjectId> receiptIds = doctorPatientInvoiceCollection.getReceiptIds();
								if(receiptIds == null) receiptIds = new ArrayList<ObjectId>();
								receiptIds.add(doctorPatientReceiptCollection.getId());
								
								doctorPatientInvoiceCollection.setReceiptIds(receiptIds);
								doctorPatientInvoiceCollection.setUpdatedTime(doctorPatientReceiptCollection.getUpdatedTime());
								doctorPatientInvoiceCollection = doctorPatientInvoiceRepository.save(doctorPatientInvoiceCollection);
							}
							DoctorPatientLedgerCollection doctorPatientLedgerCollection = doctorPatientLedgerRepository
									.findByReceiptId(doctorPatientReceiptCollection.getId());
							if (doctorPatientLedgerCollection == null) {
								doctorPatientLedgerCollection = new DoctorPatientLedgerCollection();
								doctorPatientLedgerCollection.setPatientId(doctorPatientReceiptCollection.getPatientId());
								doctorPatientLedgerCollection.setLocationId(doctorPatientReceiptCollection.getLocationId());
								doctorPatientLedgerCollection.setHospitalId(doctorPatientReceiptCollection.getHospitalId());
								doctorPatientLedgerCollection.setReceiptId(doctorPatientReceiptCollection.getId());
								doctorPatientLedgerCollection.setCreditAmount(doctorPatientReceiptCollection.getAmountPaid());
								doctorPatientLedgerCollection.setCreatedTime(new Date());
								doctorPatientLedgerCollection.setUpdatedTime(new Date());
							} else {
								doctorPatientLedgerCollection.setCreditAmount(doctorPatientReceiptCollection.getAmountPaid());
								doctorPatientLedgerCollection.setUpdatedTime(new Date());
							}
							doctorPatientLedgerCollection.setDiscarded(doctorPatientReceiptCollection.getDiscarded());
							doctorPatientLedgerCollection = doctorPatientLedgerRepository.save(doctorPatientLedgerCollection);

							if(!doctorPatientReceiptCollection.getDiscarded()) {
								DoctorPatientDueAmountCollection doctorPatientDueAmountCollection = doctorPatientDueAmountRepository
										.find(doctorPatientReceiptCollection.getPatientId(),
												doctorPatientReceiptCollection.getDoctorId(),
												doctorPatientReceiptCollection.getLocationId(),
												doctorPatientReceiptCollection.getHospitalId());
								if (doctorPatientDueAmountCollection == null) {
									doctorPatientDueAmountCollection = new DoctorPatientDueAmountCollection();
									doctorPatientDueAmountCollection.setDoctorId(doctorPatientReceiptCollection.getDoctorId());
									doctorPatientDueAmountCollection.setHospitalId(doctorPatientReceiptCollection.getHospitalId());
									doctorPatientDueAmountCollection.setLocationId(doctorPatientReceiptCollection.getLocationId());
									doctorPatientDueAmountCollection.setPatientId(doctorPatientReceiptCollection.getPatientId());
								}
								doctorPatientDueAmountCollection.setDueAmount(doctorPatientDueAmountCollection.getDueAmount() - doctorPatientReceiptCollection.getAmountPaid());
								doctorPatientDueAmountRepository.save(doctorPatientDueAmountCollection);
							}
							System.out.println(fields[1]+".."+fields[4]);
						
						} else {
							dataCountNotUploaded++;
						}
					}
				}
				lineCount++;
				response = true;
			}
			System.out.println("Payments Done. dataCountNotUploaded: " + dataCountNotUploaded);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return response;
	}

}
