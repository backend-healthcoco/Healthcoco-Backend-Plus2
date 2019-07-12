package com.dpdocter.services.impl;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
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
import com.dpdocter.beans.Records;
import com.dpdocter.beans.Reference;
import com.dpdocter.beans.RegisteredPatientDetails;
import com.dpdocter.beans.Treatment;
import com.dpdocter.beans.TreatmentService;
import com.dpdocter.beans.WorkingHours;
import com.dpdocter.collections.AdmitCardCollection;
import com.dpdocter.collections.AppointmentBookedSlotCollection;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.ClinicalNotesCollection;
import com.dpdocter.collections.DeliveryReportsCollection;
import com.dpdocter.collections.DischargeSummaryCollection;
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
import com.dpdocter.collections.IPDReportsCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.OPDReportsCollection;
import com.dpdocter.collections.OTReportsCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientTreatmentCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.collections.RecordsCollection;
import com.dpdocter.collections.ReferencesCollection;
import com.dpdocter.collections.TreatmentServicesCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.elasticsearch.document.ESPatientDocument;
import com.dpdocter.elasticsearch.document.ESTreatmentServiceDocument;
import com.dpdocter.elasticsearch.repository.ESPatientRepository;
import com.dpdocter.elasticsearch.repository.ESTreatmentServiceRepository;
import com.dpdocter.elasticsearch.services.ESRegistrationService;
import com.dpdocter.elasticsearch.services.ESTreatmentService;
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
import com.dpdocter.repository.AdmitCardRepository;
import com.dpdocter.repository.AppointmentBookedSlotRepository;
import com.dpdocter.repository.AppointmentRepository;
import com.dpdocter.repository.ClinicalNotesRepository;
import com.dpdocter.repository.DeliveryReportsRepository;
import com.dpdocter.repository.DischargeSummaryRepository;
import com.dpdocter.repository.DiseasesRepository;
import com.dpdocter.repository.DoctorPatientDueAmountRepository;
import com.dpdocter.repository.DoctorPatientInvoiceRepository;
import com.dpdocter.repository.DoctorPatientLedgerRepository;
import com.dpdocter.repository.DoctorPatientReceiptRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.DrugTypeRepository;
import com.dpdocter.repository.GroupRepository;
import com.dpdocter.repository.IPDReportsRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.OPDReportsRepository;
import com.dpdocter.repository.OTReportsRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PatientTreamentRepository;
import com.dpdocter.repository.PatientVisitRepository;
import com.dpdocter.repository.PrescriptionRepository;
import com.dpdocter.repository.RecordsRepository;
import com.dpdocter.repository.ReferenceRepository;
import com.dpdocter.repository.TreatmentServicesRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DrugAddEditRequest;
import com.dpdocter.request.PatientRegistrationRequest;
import com.dpdocter.response.DoctorClinicProfileLookupResponse;
import com.dpdocter.response.TreatmentServiceUpdateResponse;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.HistoryServices;
import com.dpdocter.services.PatientTreatmentServices;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.PrescriptionServices;
import com.dpdocter.services.RegistrationService;
import com.dpdocter.services.ReportsService;
import com.dpdocter.services.TransactionalManagementService;
import com.dpdocter.services.UploadDateService;
import com.mongodb.BasicDBObject;

import common.util.web.CSVUtils;
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
	private ESTreatmentService esTreatmentService;
	
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

	@Autowired
	private DischargeSummaryRepository dischargeSummaryRepository;

	@Autowired
	private AdmitCardRepository admitCardRepository;

	@Autowired
	private DeliveryReportsRepository deliveryReportsRepository;

	@Autowired
	private IPDReportsRepository ipdReportsRepository;

	@Autowired
	private OPDReportsRepository opdReportsRepository;
	
	@Autowired
	private OTReportsRepository otReportsRepository;

	@Autowired
	private PrescriptionServices prescriptionServices;

	@Autowired
	DoctorPatientLedgerRepository doctorPatientLedgerRepository;

	@Autowired
	DoctorPatientDueAmountRepository doctorPatientDueAmountRepository;

	@Autowired
	RecordsRepository recordsRepository;
	
	@Autowired
	private PatientVisitService patientVisitService;
	
	@Autowired 
	private ESTreatmentServiceRepository eSTreatmentServiceRepository;
	
	@Value(value = "${patient.count}")
	private String patientCount;

//	private static final String COMMA_DELIMITER = ",";

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

	@Value(value = "${list.prescriptions.not.uploaded.file}")
	private String LIST_PRESCRIPTIONS_NOT_UPLOADED_FILE;
	
	@Value(value = "${list.clinicalnotes.not.uploaded.file}")
	private String LIST_CLINICAL_NOTES_NOT_UPLOADED_FILE;
	
	@Value(value = "${list.appointments.not.uploaded.file}")
	private String LIST_APPOINTMENTS_NOT_UPLOADED_FILE;
	
	@Value(value = "${list.treatments.not.uploaded.file}")
	private String LIST_TREATMENTS_NOT_UPLOADED_FILE;
	
	@Value(value = "${list.treatment.plans.not.uploaded.file}")
	private String LIST_TREATMENT_PLANS_NOT_UPLOADED_FILE;
	
	@Value(value = "${list.invoices.not.uploaded.file}")
	private String LIST_INVOICES_NOT_UPLOADED_FILE;
	
	@Value(value = "${list.payments.not.uploaded.file}")
	private String LIST_PAYMENTS_NOT_UPLOADED_FILE;
	
	@Value(value = "${list.treatment.services.not.uploaded.file}")
	private String LIST_TREATMENT_SERVICES_NOT_UPLOADED_FILE;
	
	private String patientInitial = "";

	@Autowired
	private ESPatientRepository esPatientRepository;

	@Value(value = "${bucket.name}")
	private String bucketName;

	@Value(value = "${mail.aws.key.id}")
	private String AWS_KEY;

	@Value(value = "${mail.aws.secret.key}")
	private String AWS_SECRET_KEY;
	
	@Value(value = "${list.images.result}")
	private String LIST_IMAGES_RESULT;
	
//	@Autowired
//	private ESDrugRepository esDrugRepository;
//	
//	@Autowired
//	private ElasticsearchTemplate elasticsearchTemplate;
	
	@Autowired
	private PatientVisitService patientTrackService;
	
	@Autowired
	private FileManager fileManager;
	
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
			// esDrugRepository.findById(drugCollection.getId().toString());
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
			// esTreatmentServiceRepository.findById(treatmentServicesCollection.getId().toString());
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

				ESPatientDocument document = esPatientRepository.findById(patientCollection.getId().toString()).orElse(null);
				if (document != null)
					esPatientRepository.delete(document);
				userRepository.deleteById(patientCollection.getUserId());
				patientRepository.delete(patientCollection);
			}
			
			
//			List<PatientCollection> patientCollections = patientRepository.findByDoctorId(doctorObjectId,
//					new Date(Long.parseLong("0")), new Sort(Direction.ASC, "createdTime"));
//			for(PatientCollection patientCollection : patientCollections) {
//				ESPatientDocument esPatientDocument = esPatientRepository.findById(patientCollection.getId().toString());
//				UserCollection user = userRepository.findById(patientCollection.getUserId());
//				if(!DPDoctorUtils.anyStringEmpty(user.getMobileNumber()) && user.getMobileNumber().length()==12) {
//					String mobileNumber = user.getMobileNumber().substring(2, 12);
//					user.setMobileNumber(mobileNumber);
//					user = userRepository.save(user);
//					if(esPatientDocument != null)esPatientDocument.setMobileNumber(mobileNumber);
//				}
//				if(!patientCollection.getPNUM().startsWith("P")) {
//					patientCollection.setPNUM("P"+patientCollection.getPNUM());
//					patientCollection = patientRepository.save(patientCollection);
//					if(esPatientDocument != null)esPatientDocument.setPNUM(patientCollection.getPNUM());
//				}
//				if(esPatientDocument != null)esPatientRepository.save(esPatientDocument);
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public Boolean parsePatientFile(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		
		try {
			Scanner scanner = new Scanner(new File(UPLOAD_PATIENTS_DATA_FILE));
	        while (scanner.hasNext()) {
	        		String csvLine = scanner.nextLine();
	        		List<String> line = CSVUtils.parseLine(csvLine);
	        			
	            System.out.println(line.get(0) +"..."+line.get(1));
	        }
	        scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
		
	}
	
	public Boolean parseRXFile(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		
		try {
			Scanner scanner = new Scanner(new File(UPLOAD_PRESCRIPTIONS_DATA_FILE));
	        while (scanner.hasNext()) {
	        		String csvLine = scanner.nextLine();
	        		List<String> line = CSVUtils.parseLine(csvLine);
	        			
	            System.out.println(line.get(0) +"..."+line.get(1));
	        }
	        scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
		
	}
	
	public Boolean parseClinicalNotesFile(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		
		try {
			Scanner scanner = new Scanner(new File(UPLOAD_CLINICAL_NOTES_DATA_FILE));
	        while (scanner.hasNext()) {
	        		String csvLine = scanner.nextLine();
	        		List<String> line = CSVUtils.parseLine(csvLine);
	        			
	            System.out.println(line.get(0) +"..."+line.get(1));
	        }
	        scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
		
	}
	
	public Boolean parseAppointmentFile(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		
		try {
			Scanner scanner = new Scanner(new File(UPLOAD_APPOINTMENTS_DATA_FILE));
	        while (scanner.hasNext()) {
	        		String csvLine = scanner.nextLine();
	        		List<String> line = CSVUtils.parseLine(csvLine);
	        		System.out.println(line.get(0) +"..."+line.get(1));
	        }
	        scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
		
	}
	
	public Boolean parseTreatmentFile(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		
		try {
			Scanner scanner = new Scanner(new File(UPLOAD_TREATMENTS_DATA_FILE));
	        while (scanner.hasNext()) {
	        		String csvLine = scanner.nextLine();
	        		List<String> line = CSVUtils.parseLine(csvLine);
	        		System.out.println(line.get(0) +"..."+line.get(1));
	        }
	        scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
		
	}
	
	public Boolean parseTreatmentPlanFile(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		
		try {
			Scanner scanner = new Scanner(new File(UPLOAD_TREATMENTS_PLAN_DATA_FILE));
	        while (scanner.hasNext()) {
	        		String csvLine = scanner.nextLine();
	        		List<String> line = CSVUtils.parseLine(csvLine);
	        		System.out.println(line.get(0) +"..."+line.get(1));
	        }
	        scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
		
	}
	
	public Boolean parseInvoiceFile(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		
		try {
			Scanner scanner = new Scanner(new File(UPLOAD_INVOICES_DATA_FILE));
	        while (scanner.hasNext()) {
	        		String csvLine = scanner.nextLine();
	        		List<String> line = CSVUtils.parseLine(csvLine);
	        		System.out.println(line.get(0) +"..."+line.get(1));
	        }
	        scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
		
	}
	
	public Boolean parsePaymentsFile(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		
		try {
			Scanner scanner = new Scanner(new File(UPLOAD_PAYMENTS_DATA_FILE));
	        while (scanner.hasNext()) {
	        		String csvLine = scanner.nextLine();
	        		List<String> line = CSVUtils.parseLine(csvLine);
	        		System.out.println(line.get(0) +"..."+line.get(1));
	        }
	        scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
		
	}
	
	public Boolean parseProcedureDataFile(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		
		try {
			Scanner scanner = new Scanner(new File(UPLOAD_TREATMENT_SERVICES_DATA_FILE));
	        while (scanner.hasNext()) {
	        		String csvLine = scanner.nextLine();
	        		List<String> line = CSVUtils.parseLine(csvLine);
	        		System.out.println(line.get(0) +"..."+line.get(1));
	        }
	        scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
		
	}

	@Override
	public Boolean uploadPatientData(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		FileWriter fileWriter = null;
		Scanner scanner = null;
		int lineCount = 0;
		String csvLine = null;
		try {
			fileWriter = new FileWriter(LIST_PATIENTS_NOT_REGISTERED_FILE);
			fileWriter.append(FILE_HEADER.toString());
			fileWriter.append(NEW_LINE_SEPARATOR);

			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			UserCollection drCollection = userRepository.findById(doctorObjectId).orElse(null);

			PatientRegistrationRequest request = null;
			
			scanner = new Scanner(new File(UPLOAD_PATIENTS_DATA_FILE));
			
			Integer pNUMIndex = null, patientNameIndex = null, mobileNumberIndex = null, contactNumberIndex = null, emailAddressIndex = null, alternateMobileNumberIndex = null,
					genderIndex = null, streetAddressIndex = null, localityIndex = null, cityIndex = null, pincodeIndex = null,
					nationalIdIndex = null, dobIndex = null, ageIndex = null, bloodGroupIndex = null, remarksIndex = null,
					medicalHistoryIndex = null, referredByIndex = null, groupsIndex = null, patientNotesIndex = null;

			while (scanner.hasNext()) {
				csvLine = scanner.nextLine();
	            List<String> line = CSVUtils.parseLine(csvLine);
	            
	            if(lineCount == 0) {
	            		if(line != null && !line.isEmpty()) {
	            			for(int i=0; i<line.size(); i++) {
	            				
	            				String key = line.get(i).trim().replaceAll("[^a-zA-Z]", "").toUpperCase();
	            				
	            				switch (key) {
								case "PATIENTNUMBER": pNUMIndex = i;break;
								case "PATIENTNAME": patientNameIndex = i;break;
								case "MOBILENUMBER": mobileNumberIndex = i;break;
								case "CONTACTNUMBER": contactNumberIndex = i;break;
								case "EMAILADDRESS": emailAddressIndex = i;break;
								case "SECONDARYNUMBER": alternateMobileNumberIndex = i;break;
								case "GENDER": genderIndex = i;break;
								case "ADDRESS": streetAddressIndex = i;break;
								case "LOCALITY": localityIndex = i;break;
								case "CITY": cityIndex = i;break;
								case "PINCODE": pincodeIndex = i;break;
								case "DATEOFBIRTH": dobIndex = i;break;
								case "AGE": ageIndex = i;break;
								case "BLOODGROUP": bloodGroupIndex = i;break;
								case "MEDICALHISTORY": medicalHistoryIndex = i;break;
								case "REFERREDBY": referredByIndex = i;break;
								case "GROUPS": groupsIndex = i;break;
								case "PATIENTNOTES": patientNotesIndex = i;break;
								
								
								default:
									break;
								}
	            			}
	            		}
	            }else {
					int count = 0;
					request = new PatientRegistrationRequest();
					
					if (!DPDoctorUtils.anyStringEmpty(line.get(mobileNumberIndex))) {
						String mobileNumberValue = line.get(mobileNumberIndex).replaceAll("'", "").replaceAll("\"", "");
						if(!mobileNumberValue.equalsIgnoreCase("NONE")) {
							if (mobileNumberValue.startsWith("+91"))
								mobileNumberValue = mobileNumberValue.replace("+91", "");
							request.setMobileNumber(mobileNumberValue);
							

							List<UserCollection> userCollections = userRepository.findByMobileNumber(mobileNumberValue);
							if (userCollections != null && !userCollections.isEmpty()) {
								for (UserCollection userCollection : userCollections) {
									if (!userCollection.getUserName().equalsIgnoreCase(userCollection.getEmailAddress()))
										count++;
								}
							}
						}
						
					}
					
					if (count < Integer.parseInt(patientCount)) {
							if (patientNameIndex != null) {
								String patientName = line.get(patientNameIndex).replaceAll("'", "").replaceAll("\"", "");
								if(checkIfNotNullOrNone(patientName)) {
									request.setFirstName(patientName);
									request.setLocalPatientName(patientName);
							   }
							}
								
								
							if (genderIndex != null) {
								String gender = line.get(genderIndex).replaceAll("'", "").replaceAll("\"", "");
								if(checkIfNotNullOrNone(gender)){
									if(gender.equalsIgnoreCase("F"))gender="FEMALE";
									else if(gender.equalsIgnoreCase("M"))gender="MALE";
									request.setGender(gender);
								}
							}

							if(dobIndex != null) {
								String dateOfBirth = line.get(dobIndex).replaceAll("'", "").replaceAll("\"", "");
								if (checkIfNotNullOrNone(dateOfBirth)) {
									String[] dob = dateOfBirth.split("-");
									
									DOB dobObject = new DOB(Integer.parseInt(dob[2]), Integer.parseInt(dob[1]), Integer.parseInt(dob[0]));
									request.setDob(dobObject);
								}
							}

							if(ageIndex != null && checkIfNotNullOrNone(line.get(ageIndex).replaceAll("'", "").replaceAll("\"", ""))) {
								request.setAge(Integer.parseInt(line.get(ageIndex).replaceAll("'", "").replaceAll("\"", "")));
							}
								
							if (emailAddressIndex != null && checkIfNotNullOrNone(line.get(emailAddressIndex).replaceAll("'", "").replaceAll("\"", "")))
								request.setEmailAddress(line.get(emailAddressIndex).replaceAll("'", "").replaceAll("\"", ""));

							if (bloodGroupIndex != null && checkIfNotNullOrNone(line.get(bloodGroupIndex).replaceAll("'", "").replaceAll("\"", "")))
								request.setBloodGroup(line.get(bloodGroupIndex).replaceAll("'", ""));

							if (alternateMobileNumberIndex != null && checkIfNotNullOrNone(line.get(alternateMobileNumberIndex).replaceAll("'", "").replaceAll("\"", "")))
								request.setSecMobile(line.get(alternateMobileNumberIndex).replaceAll("'", ""));
							
							if (request.getSecMobile() == null && contactNumberIndex != null && checkIfNotNullOrNone(line.get(contactNumberIndex).replaceAll("'", "").replaceAll("\"", "")))
								request.setSecMobile(line.get(contactNumberIndex).replaceAll("'", ""));
							
							
							String country = null, city = null, state = null, postalCode = null, locality = null,
									streetAddress = null;

							if (streetAddressIndex != null && checkIfNotNullOrNone(line.get(streetAddressIndex).replaceAll("'", "").replaceAll("\"", "")))
								streetAddress = line.get(streetAddressIndex).replaceAll("'", "");
							
							if (localityIndex != null && checkIfNotNullOrNone(line.get(localityIndex).replaceAll("'", "").replaceAll("\"", "")))
								locality = line.get(localityIndex).replaceAll("'", "");
							
							if (cityIndex != null && checkIfNotNullOrNone(line.get(cityIndex).replaceAll("'", "").replaceAll("\"", "")))
								city = line.get(cityIndex).replaceAll("'", "");
							
							country = "India";

							if (pincodeIndex != null && checkIfNotNullOrNone(line.get(pincodeIndex).replaceAll("'", "").replaceAll("\"", "")))
								postalCode = line.get(pincodeIndex).replaceAll("'", "");

							if (!DPDoctorUtils.allStringsEmpty(country, city, state, postalCode, locality,
									streetAddress)) {
								Address address = new Address(country, city, state, postalCode, locality, null, null,
										null, streetAddress);
								request.setAddress(address);
							}

							if (referredByIndex != null && checkIfNotNullOrNone(line.get(referredByIndex).replaceAll("'", "").replaceAll("\"", ""))) {
								String referredBy = line.get(referredByIndex).replaceAll("'", "");
								Reference reference = new Reference();
								reference.setReference(referredBy);

								ReferencesCollection referencesCollection = referenceRepository.find(referredBy,
										doctorObjectId, locationObjectId, hospitalObjectId);
								if (referencesCollection != null)
									reference.setId(referencesCollection.getId().toString());

								request.setReferredBy(reference);
							}

							if (groupsIndex != null && checkIfNotNullOrNone(line.get(groupsIndex).replaceAll("'", "").replaceAll("\"", ""))) {
								String groupName = line.get(groupsIndex).replaceAll("'", "");
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
								if(groupCollection != null && groupCollection.getId() != null)request.setGroups(Arrays.asList(groupCollection.getId().toString()));
							}
							request.setDoctorId(doctorId);
							request.setLocationId(locationId);
							request.setHospitalId(hospitalId);

							if (pNUMIndex != null && checkIfNotNullOrNone(line.get(pNUMIndex).replaceAll("'", "").replaceAll("\"", ""))) {
								request.setPNUM(line.get(pNUMIndex).replaceAll("'", ""));

								patientInitial = request.getPNUM().replaceAll("[0-9]", "");
								
								Scanner scannerForApp = new Scanner(new File(UPLOAD_APPOINTMENTS_DATA_FILE));
						        while (scannerForApp.hasNext()) {
						        		List<String> appLine = CSVUtils.parseLine(scannerForApp.nextLine());
						        		if (appLine.get(1).equalsIgnoreCase(line.get(pNUMIndex))) {
											SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d HH:mm:ss");

											String dateSTri = appLine.get(0).replace("\"", "");
											dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
											Date date = dateFormat.parse(dateSTri);
											request.setRegistrationDate(date.getTime());
											scannerForApp.close();
											break;
										}
				       		        }
						        if (scannerForApp != null) {
									scannerForApp.close();
						        }
							} else {
								request.setRegistrationDate(new Date().getTime());
							}
							
							List<String> notes = request.getNotes();
							if(remarksIndex != null && checkIfNotNullOrNone(line.get(remarksIndex).replaceAll("'", "").replaceAll("\"", ""))) {
								if(notes == null)notes = new ArrayList<String>();
								notes.add(line.get(remarksIndex).replaceAll("'", ""));
							}
							
							if(patientNotesIndex != null && checkIfNotNullOrNone(line.get(patientNotesIndex).replaceAll("'", "").replaceAll("\"", ""))) {
								if(notes == null)notes = new ArrayList<String>();
								notes.add(line.get(patientNotesIndex).replaceAll("'", ""));
							}
							request.setNotes(notes);
							RegisteredPatientDetails registeredPatientDetails = registrationService
									.registerNewPatient(request);

							transactionalManagementService.addResource(
									new ObjectId(registeredPatientDetails.getUserId()), Resource.PATIENT, false);
							esRegistrationService
									.addPatient(registrationService.getESPatientDocument(registeredPatientDetails));

							if (medicalHistoryIndex != null && checkIfNotNullOrNone(line.get(medicalHistoryIndex).replaceAll("'", "").replaceAll("\"", ""))) {
								String diseases[] = line.get(medicalHistoryIndex).replaceAll("'", "").split(",");
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
							}
							System.out.println(line.get(mobileNumberIndex));
							response = true;
						} else {
							System.out.println(patientCount + " patients already exist with mobile number "
									+ request.getMobileNumber());
							fileWriter.append(csvLine);fileWriter.append(NEW_LINE_SEPARATOR);
						}
				}
				lineCount++;
			}

		} catch (Exception e) {
			response = false;
			e.printStackTrace();
		} finally {
			if (scanner != null) {
				try {
					scanner.close();
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
		Scanner scanner = null;
		int lineCount = 0;
		String csvLine = null;
		int dataCountNotUploaded = 0;
		FileWriter fileWriter = null;
		try {
			
			fileWriter = new FileWriter(LIST_PRESCRIPTIONS_NOT_UPLOADED_FILE);
			
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			
			UserCollection drCollection = userRepository.findById(doctorObjectId).orElse(null);
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

			scanner = new Scanner(new File(UPLOAD_PRESCRIPTIONS_DATA_FILE));
			
//			Integer pNUMIndex = null, patientNameIndex = null, mobileNumberIndex = null, contactNumberIndex = null, emailAddressIndex = null, alternateMobileNumberIndex = null,
//					genderIndex = null, streetAddressIndex = null, localityIndex = null, cityIndex = null, pincodeIndex = null,
//					nationalIdIndex = null, dobIndex = null, ageIndex = null, bloodGroupIndex = null, remarksIndex = null,
//					medicalHistoryIndex = null, referredByIndex = null, groupsIndex = null, patientNotesIndex = null;

			while (scanner.hasNext()) {
				csvLine = scanner.nextLine();
	            List<String> line = CSVUtils.parseLine(csvLine);
			
				if (lineCount > 0) {
					Boolean createVisit = false;
			
					if (!DPDoctorUtils.anyStringEmpty(line.get(2), line.get(3))) {
						PatientCollection patientCollection = patientRepository.findByLocationIDHospitalIDAndPNUM(
								locationObjectId, hospitalObjectId, line.get(2).replace("'", ""));
						if (patientCollection != null) {

							Date createdTime = new Date();
							if (!DPDoctorUtils.anyStringEmpty(line.get(0))) {
								SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d HH:mm:ss");

								String dateSTri = line.get(0).replace("'", "")+ " 13:00:00";
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

							String drugName = line.get(3).replace("'", "")
									+ ((checkIfNotNullOrNone(line.get(5)) && !DPDoctorUtils.anyStringEmpty(line.get(5)))
											? " " + line.get(5).replace("'", "") : "")
									+ ((checkIfNotNullOrNone(line.get(5)) && checkIfNotNullOrNone(line.get(6))
											&& !DPDoctorUtils.anyStringEmpty(line.get(6)))
													? " " + line.get(6).replace("'", "") : "");

							String drugType = (!DPDoctorUtils.anyStringEmpty(line.get(4))) ? line.get(4).replace("'", "")
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
							if (checkIfNotNullOrNone(line.get(7)) && line.get(7).contains("1")) {
								drugDirections = new ArrayList<DrugDirection>();
								drugDirections.add(beforeMealDirection);
							}
							if (checkIfNotNullOrNone(line.get(8)) && line.get(8).contains("1")) {
								if (drugDirections == null)
									drugDirections = new ArrayList<DrugDirection>();
								drugDirections.add(afterMealDirection);
							}
							drugAddEditRequest.setDirection(drugDirections);

							// DrugDuration13 14 
							Duration duration = null;
							if (checkIfNotNullOrNone(line.get(13)) && checkIfNotNullOrNone(line.get(14))){
								duration = new Duration();
								duration.setValue(checkIfNotNullOrNone(line.get(13)) ? line.get(13).replace("'", "") : "");
								duration.setDurationUnit(drugDurationMap.get(line.get(14).replace("'", "")));
								drugAddEditRequest.setDuration(duration);
							}
							

							// DrugDosage

							// Instructions12
							String instruction = (!DPDoctorUtils.anyStringEmpty(line.get(12))) ? line.get(12).replace("'", "") : null;

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
							fileWriter.append(csvLine);fileWriter.append(NEW_LINE_SEPARATOR);
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
			if (scanner != null) {
				try {
					scanner.close();
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
	public Boolean uploadAppointmentData(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		Scanner scanner = null;
		int lineCount = 0;
		String csvLine = null;
		int dataCountNotUploaded = 0;
		FileWriter fileWriter = null;
		Map<String, UserCollection> doctors = new HashMap<String, UserCollection>();
		
		try {
			
			fileWriter = new FileWriter(LIST_APPOINTMENTS_NOT_UPLOADED_FILE);
		
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			UserCollection drCollection = userRepository.findById(doctorObjectId).orElse(null);
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

			scanner = new Scanner(new File(UPLOAD_APPOINTMENTS_DATA_FILE));
						
			//			Integer pNUMIndex = null, patientNameIndex = null, mobileNumberIndex = null, contactNumberIndex = null, emailAddressIndex = null, alternateMobileNumberIndex = null,
			//					genderIndex = null, streetAddressIndex = null, localityIndex = null, cityIndex = null, pincodeIndex = null,
			//					nationalIdIndex = null, dobIndex = null, ageIndex = null, bloodGroupIndex = null, remarksIndex = null,
			//					medicalHistoryIndex = null, referredByIndex = null, groupsIndex = null, patientNotesIndex = null;

			while (scanner.hasNext()) {
				csvLine = scanner.nextLine();
	            List<String> line = CSVUtils.parseLine(csvLine);
				if (lineCount > 0) {
					if (!DPDoctorUtils.anyStringEmpty(line.get(0), line.get(1))) {
						PatientCollection patientCollection = patientRepository.findByLocationIDHospitalIDAndPNUM(
								locationObjectId, hospitalObjectId, line.get(1).replace("'", ""));
						if (patientCollection != null) {

							SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d HH:mm:ss");
							String dateSTri = line.get(0).replace("'", "");
							dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
							Date fromDate = dateFormat.parse(dateSTri);

							Calendar c = Calendar.getInstance();
							c.setTimeZone(TimeZone.getTimeZone("IST"));
							c.setTime(fromDate);
							int hour = c.get(Calendar.HOUR_OF_DAY);
							int fromTime = (hour * 60) + c.get(Calendar.MINUTE);

							WorkingHours workingHours = new WorkingHours();
							workingHours.setFromTime(fromTime);
							workingHours.setToTime(fromTime + 30);

							appointmentCollection = new AppointmentCollection();
							appointmentCollection.setTime(workingHours);
							appointmentCollection.setCreatedTime(fromDate);
							appointmentCollection.setUpdatedTime(fromDate);
							appointmentCollection.setFromDate(fromDate);
							appointmentCollection.setToDate(fromDate);
							appointmentCollection.setAppointmentId(
									UniqueIdInitial.APPOINTMENT.getInitial() + DPDoctorUtils.generateRandomId());

							if (checkIfNotNullOrNone(line.get(5))) {
								String state = line.get(5).replace("'", "");
								if (state.equalsIgnoreCase("CANCEL") || state.equalsIgnoreCase("CANCELLED"))
									appointmentCollection.setState(AppointmentState.CANCEL);
								else
									appointmentCollection.setState(AppointmentState.CONFIRM);
							}
							if(checkIfNotNullOrNone(line.get(3)))
								appointmentCollection.setExplanation(line.get(3).replace("'", ""));

							appointmentCollection.setLocationId(locationObjectId);
							appointmentCollection.setHospitalId(hospitalObjectId);
							appointmentCollection.setPatientId(patientCollection.getUserId());

							String drName = line.get(4).replace("'", "").replace("Dr. ", "").replace("Dr ", "").replace("Dr.", "").replace("Dr", "");
							UserCollection userCollection = doctors.get(drName.toLowerCase());
							if (userCollection == null) {
								List<UserCollection> collections = mongoTemplate.aggregate(
										Aggregation.newAggregation(
												Aggregation.match(new Criteria("firstName").regex(drName,"i")),
												new CustomAggregationOperation(new Document("$redact",
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
								appointmentCollection.setCreatedBy(userCollection.getTitle() + " " + userCollection.getFirstName());
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
									
									System.out.println(line.get(0) +"..." +line.get(1)+"..." +appointmentCollection.getCreatedBy());
									response = true;
								}else {
									System.out.println("Already present:" +line.get(0) +"..." +line.get(1)+"..." +appointmentCollection.getCreatedBy());
								}
								
							} else {
								dataCountNotUploaded++;
								fileWriter.append("Doctor not Found : "+csvLine);
								fileWriter.append(NEW_LINE_SEPARATOR);
							}
						} else {
							dataCountNotUploaded++;
							fileWriter.append("Patient not Found : "+csvLine);
							fileWriter.append(NEW_LINE_SEPARATOR);
						}
					}
				}
				lineCount++;
			}
			System.out.println("Appointments Done. dataCountNotUploaded: " + dataCountNotUploaded);
		} catch (Exception e) {
			response = false;
			e.printStackTrace();
		} finally {
			if (scanner != null) {
				try {
					scanner.close();
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

	private Boolean checkIfNotNullOrNone(String value) {
		if (value.equalsIgnoreCase("NONE'") || value.equalsIgnoreCase("NONE") || DPDoctorUtils.allStringsEmpty(value))
			return false;
		else
			return true;

	}

	@Override
	public Boolean uploadTreatmentPlansData(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		Scanner scanner = null;
		int lineCount = 0;
		String csvLine = null;
		int dataCountNotUploaded = 0;
		FileWriter fileWriter = null;
		Map<String, UserCollection> doctors = new HashMap<String, UserCollection>();
		
		try {
			
			fileWriter = new FileWriter(LIST_TREATMENT_PLANS_NOT_UPLOADED_FILE);
			
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			UserCollection drCollection = userRepository.findById(doctorObjectId).orElse(null);
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

			scanner = new Scanner(new File(UPLOAD_TREATMENTS_PLAN_DATA_FILE));
			
			//			Integer pNUMIndex = null, patientNameIndex = null, mobileNumberIndex = null, contactNumberIndex = null, emailAddressIndex = null, alternateMobileNumberIndex = null,
			//					genderIndex = null, streetAddressIndex = null, localityIndex = null, cityIndex = null, pincodeIndex = null,
			//					nationalIdIndex = null, dobIndex = null, ageIndex = null, bloodGroupIndex = null, remarksIndex = null,
			//					medicalHistoryIndex = null, referredByIndex = null, groupsIndex = null, patientNotesIndex = null;

			while (scanner.hasNext()) {
				csvLine = scanner.nextLine();
	            List<String> line = CSVUtils.parseLine(csvLine);

				if (lineCount > 0) {
					Boolean createVisit = false;
					if (!DPDoctorUtils.anyStringEmpty(line.get(0), line.get(1))) {
						PatientCollection patientCollection = patientRepository.findByLocationIDHospitalIDAndPNUM(
								locationObjectId, hospitalObjectId, line.get(1).replace("'", ""));
						if (patientCollection != null) {

							SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d HH:mm:ss");
							String dateSTri = line.get(0).replace("'", "") + " 13:00:00";
							dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
							Date fromDate = dateFormat.parse(dateSTri);

							String drName = line.get(3).replace("'", "").replace("Dr. ", "").replace("Dr ", "");
							UserCollection userCollection = doctors.get(drName.toLowerCase());
							if (userCollection == null) {
								List<UserCollection> collections = mongoTemplate.aggregate(
										Aggregation.newAggregation(
												Aggregation.match(new Criteria("firstName").regex(drName, "i")),
												new CustomAggregationOperation(new Document("$redact",
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

								String treatmentName = line.get(4).replace("'", "");
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

								if (checkIfNotNullOrNone(line.get(5)))
									treatmentService.setCost(Double.parseDouble(line.get(5).replace("'", "")));

								treatmentService.setDoctorId(patientTreatmentCollection.getDoctorId().toString());
								treatmentService.setLocationId(patientTreatmentCollection.getLocationId().toString());
								treatmentService.setHospitalId(patientTreatmentCollection.getHospitalId().toString());
								treatmentService = patientTreatmentServices.addFavouritesToService(treatmentService,
										patientTreatmentCollection.getCreatedBy());

								Treatment treatment = new Treatment();
								BeanUtil.map(treatmentService, treatment);
								treatment.setTreatmentServiceId(new ObjectId(treatmentService.getId()));

								double cost = treatment.getCost();
								if (checkIfNotNullOrNone(line.get(6))) {
									Quantity quantity = new Quantity();
									quantity.setType(QuantityEnum.QTY);
									quantity.setValue(Integer.parseInt(line.get(6).replace("'", "")));
									treatment.setQuantity(quantity);
								}else {
									Quantity quantity = new Quantity();
									quantity.setType(QuantityEnum.QTY);
									quantity.setValue(1);
									treatment.setQuantity(quantity);
								}

								//treatment.setFinalCost();

								if (checkIfNotNullOrNone(line.get(7))) {
																		
									Discount discount = new Discount();
									if (!checkIfNotNullOrNone(line.get(8))) {
										discount.setUnit(UnitType.INR);
									} else if ((line.get(8).replace("'", "")).equalsIgnoreCase("NUMBER")) {
										discount.setUnit(UnitType.INR);
									} else {
										discount.setUnit(UnitType.valueOf(line.get(8).replace("'", "")));
									}
									discount.setValue(Double.parseDouble(line.get(7).replace("'", "")));
									treatment.setDiscount(discount);
								}
							
								
								if(treatment.getQuantity().getValue() > 0) {
									
									cost =  cost * treatment.getQuantity().getValue();
									
									if(treatment.getDiscount() != null) {
										if (treatment.getDiscount().getUnit().name().equalsIgnoreCase(UnitType.PERCENT.name())) {
											treatment.setFinalCost(cost - (cost * (treatment.getDiscount().getValue() / 100)));
										} else {
											treatment.setFinalCost(cost - treatment.getDiscount().getValue());
										}
										if(totalDiscount == null){
											totalDiscount = new Discount();
											totalDiscount.setUnit(UnitType.INR);
											totalDiscount.setValue(0.0);
										}
										Double totaldiscountValue = totalDiscount.getValue() + (cost - treatment.getFinalCost());
										totalDiscount.setValue(totaldiscountValue);
									}else {
										treatment.setFinalCost(cost);
									}										
								}else {
									treatment.setFinalCost(0.0);
								}
							
								if (checkIfNotNullOrNone(line.get(10)))
									treatment.setNote(line.get(10).replace("'", ""));

								if (treatments == null)
									treatments = new ArrayList<Treatment>();
								treatments.add(treatment);

								patientTreatmentCollection.setTreatments(treatments);

								patientTreatmentCollection.setTotalCost(totalCost + treatment.getCost());
								patientTreatmentCollection.setGrandTotal(grandTotal + treatment.getFinalCost());
								patientTreatmentCollection.setTotalDiscount(totalDiscount);

								System.out.println(line.get(0)+".."+line.get(1));
								patientTreatmentCollection = patientTreamentRepository.save(patientTreatmentCollection);

								if (createVisit)
									addRecord(patientTreatmentCollection, VisitedFor.TREATMENT, null,
											patientTreatmentCollection.getPatientId(),
											patientTreatmentCollection.getDoctorId(),
											patientTreatmentCollection.getLocationId(),
											patientTreatmentCollection.getHospitalId(),
											patientTreatmentCollection.getId());
							} else {
								dataCountNotUploaded++;
								fileWriter.append("Doctor Not found:"+csvLine);
								fileWriter.append(NEW_LINE_SEPARATOR);
							}

						} else {
							dataCountNotUploaded++;
							fileWriter.append("Patient Not found:"+csvLine);
							fileWriter.append(NEW_LINE_SEPARATOR);
						}
					}
				}
				lineCount++;
				response = true;
			}
			System.out.println("treatments Plan Done. dataCountNotUploaded: " + dataCountNotUploaded);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (scanner != null) {
				try {
					scanner.close();
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
	public Boolean uploadTreatmentData(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		Scanner scanner = null;
		int lineCount = 0;
		String csvLine = null;
		int dataCountNotUploaded = 0;
		FileWriter fileWriter = null;
		Map<String, UserCollection> doctors = new HashMap<String, UserCollection>();
		
		try {
			
			fileWriter = new FileWriter(LIST_TREATMENTS_NOT_UPLOADED_FILE);

			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			UserCollection drCollection = userRepository.findById(doctorObjectId).orElse(null);
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

			scanner = new Scanner(new File(UPLOAD_TREATMENTS_DATA_FILE));
			
			//			Integer pNUMIndex = null, patientNameIndex = null, mobileNumberIndex = null, contactNumberIndex = null, emailAddressIndex = null, alternateMobileNumberIndex = null,
			//					genderIndex = null, streetAddressIndex = null, localityIndex = null, cityIndex = null, pincodeIndex = null,
			//					nationalIdIndex = null, dobIndex = null, ageIndex = null, bloodGroupIndex = null, remarksIndex = null,
			//					medicalHistoryIndex = null, referredByIndex = null, groupsIndex = null, patientNotesIndex = null;

			while (scanner.hasNext()) {
				csvLine = scanner.nextLine();
	            List<String> line = CSVUtils.parseLine(csvLine);
				if (lineCount > 0) {
					Boolean createVisit = false;
					if (!DPDoctorUtils.anyStringEmpty(line.get(0), line.get(1))) {
						PatientCollection patientCollection = patientRepository.findByLocationIDHospitalIDAndPNUM(
								locationObjectId, hospitalObjectId, line.get(1).replace("'", ""));
						if (patientCollection != null) {

							SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d HH:mm:ss");
							String dateSTri = line.get(0).replace("'", "") + " 13:00:00";
							dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
							Date fromDate = dateFormat.parse(dateSTri);

							String drName = line.get(9).replace("'", "").replace("Dr. ", "").replace("Dr ", "");
							UserCollection userCollection = doctors.get(drName.toLowerCase());
							if (userCollection == null) {
								List<UserCollection> collections = mongoTemplate.aggregate(
										Aggregation.newAggregation(
												Aggregation.match(new Criteria("firstName").regex(drName, "i")),
												new CustomAggregationOperation(new Document("$redact",
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
							
							if(userCollection != null) {
									
								String treatmentName = line.get(3).replace("'", "");
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

									if (checkIfNotNullOrNone(line.get(6)))
										treatmentService.setCost(Double.parseDouble(line.get(6).replace("'", "")));

									treatmentService.setDoctorId(patientTreatmentCollection.getDoctorId().toString());
									treatmentService.setLocationId(patientTreatmentCollection.getLocationId().toString());
									treatmentService.setHospitalId(patientTreatmentCollection.getHospitalId().toString());
									treatmentService = patientTreatmentServices.addFavouritesToService(treatmentService,
											patientTreatmentCollection.getCreatedBy());

									Treatment treatment = new Treatment();
									BeanUtil.map(treatmentService, treatment);
									treatment.setTreatmentServiceId(new ObjectId(treatmentService.getId()));
									if(checkIfNotNullOrNone(line.get(4))) {
										List<Fields> treatmentFieldList = new ArrayList<>();
										Fields treatmentFields = new Fields();
										treatmentFields.setKey("toothNumber");treatmentFields.setValue(line.get(4).replace("'", ""));
										treatmentFieldList.add(treatmentFields);
										treatment.setTreatmentFields(treatmentFieldList);
									}

									if (checkIfNotNullOrNone(line.get(5)))
										treatment.setNote(line.get(5).replace("'", ""));

									treatment.setFinalCost(treatment.getCost() * treatment.getQuantity().getValue());

									if (checkIfNotNullOrNone(line.get(7))) {
										
										double cost =  treatment.getCost() * treatment.getQuantity().getValue();
										
										Discount discount = new Discount();
										if (!checkIfNotNullOrNone(line.get(8))) {
											discount.setUnit(UnitType.INR);
										} else if ((line.get(8).replace("'", "")).equalsIgnoreCase("NUMBER")) {
											discount.setUnit(UnitType.INR);
										} else {
											discount.setUnit(UnitType.valueOf(line.get(8).replace("'", "")));
										}
										discount.setValue(Double.parseDouble(line.get(7).replace("'", "")));
										treatment.setDiscount(discount);

										if (discount.getUnit().name().equalsIgnoreCase(UnitType.PERCENT.name())) {
											treatment.setFinalCost(cost
													- (cost * (discount.getValue() / 100)));
										} else {
											treatment.setFinalCost(cost - discount.getValue());
										}

										if (totalDiscount == null) {
											totalDiscount = new Discount();
											totalDiscount.setUnit(UnitType.INR);
											totalDiscount.setValue(treatment.getFinalCost() - treatment.getCost());
										} else {
											totalDiscount.setValue(totalDiscount.getValue() + (treatment.getFinalCost() - treatment.getCost()));
										}
									}

									if (treatments == null)
										treatments = new ArrayList<Treatment>();
									treatments.add(treatment);
									patientTreatmentCollection.setTreatments(treatments);

									patientTreatmentCollection.setTotalCost(totalCost + treatment.getCost());
									patientTreatmentCollection.setGrandTotal(grandTotal + treatment.getFinalCost());
									patientTreatmentCollection.setTotalDiscount(totalDiscount);

									System.out.println(line.get(0)+".."+line.get(1));
									patientTreatmentCollection = patientTreamentRepository.save(patientTreatmentCollection);
									if (createVisit)
										addRecord(patientTreatmentCollection, VisitedFor.TREATMENT, null,
												patientTreatmentCollection.getPatientId(),
												patientTreatmentCollection.getDoctorId(),
												patientTreatmentCollection.getLocationId(),
												patientTreatmentCollection.getHospitalId(), patientTreatmentCollection.getId());
							}else {
								dataCountNotUploaded++;
								fileWriter.append("Doctor Not found:"+csvLine);
								fileWriter.append(NEW_LINE_SEPARATOR);
							}
						} else {
							dataCountNotUploaded++;
							fileWriter.append("Patient Not found:"+csvLine);
							fileWriter.append(NEW_LINE_SEPARATOR);
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
			if (scanner != null) {
				try {
					scanner.close();
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
	public Boolean assignPNUMToPatientsHavingPNUMAsNull(String doctorId, String locationId, String hospitalId) {
		try {
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
			List<PatientVisitCollection> patientVisitCollections = patientVisitRepository.findAll();
			for (PatientVisitCollection patientVisitCollection : patientVisitCollections) {

				patientVisitCollection.setAdminCreatedTime(patientVisitCollection.getCreatedTime());
			}
			patientVisitRepository.saveAll(patientVisitCollections);

			List<PatientTreatmentCollection> patientTreatmentCollections = patientTreamentRepository.findAll();
			for (PatientTreatmentCollection patientTreatmentCollection : patientTreatmentCollections) {

				patientTreatmentCollection.setAdminCreatedTime(patientTreatmentCollection.getCreatedTime());
			}
			patientTreamentRepository.saveAll(patientTreatmentCollections);
			List<ClinicalNotesCollection> clinicalNotesCollections = clinicalNotesRepository.findAll();
			for (ClinicalNotesCollection clinicalNotesCollection : clinicalNotesCollections) {

				clinicalNotesCollection.setAdminCreatedTime(clinicalNotesCollection.getCreatedTime());
			}
			clinicalNotesRepository.saveAll(clinicalNotesCollections);
			List<PrescriptionCollection> prescriptionCollections = prescriptionRepository.findAll();
			for (PrescriptionCollection prescriptionCollection : prescriptionCollections) {

				prescriptionCollection.setAdminCreatedTime(prescriptionCollection.getCreatedTime());
			}
			prescriptionRepository.saveAll(prescriptionCollections);
			List<OTReportsCollection> otReportsCollections = otReportsRepository.findAll();
			for (OTReportsCollection otReportsCollection : otReportsCollections) {

				otReportsCollection.setAdminCreatedTime(otReportsCollection.getCreatedTime());
			}
			otReportsRepository.saveAll(otReportsCollections);
			List<DeliveryReportsCollection> deliveryReportsCollections = deliveryReportsRepository.findAll();
			for (DeliveryReportsCollection deliveryReportsCollection : deliveryReportsCollections) {

				deliveryReportsCollection.setAdminCreatedTime(deliveryReportsCollection.getCreatedTime());
			}
			deliveryReportsRepository.saveAll(deliveryReportsCollections);

			List<OPDReportsCollection> opdReportsCollections = opdReportsRepository.findAll();
			for (OPDReportsCollection opdReportsCollection : opdReportsCollections) {

				opdReportsCollection.setAdminCreatedTime(opdReportsCollection.getCreatedTime());
			}
			opdReportsRepository.saveAll(opdReportsCollections);

			List<IPDReportsCollection> ipdReportsCollections = ipdReportsRepository.findAll();
			for (IPDReportsCollection ipdReportsCollection : ipdReportsCollections) {

				ipdReportsCollection.setAdminCreatedTime(ipdReportsCollection.getCreatedTime());
			}
			ipdReportsRepository.saveAll(ipdReportsCollections);

			List<DoctorPatientInvoiceCollection> doctorPatientInvoiceCollections = doctorPatientInvoiceRepository
					.findAll();
			for (DoctorPatientInvoiceCollection doctorPatientInvoiceCollection : doctorPatientInvoiceCollections) {

				doctorPatientInvoiceCollection.setAdminCreatedTime(doctorPatientInvoiceCollection.getCreatedTime());
			}
			doctorPatientInvoiceRepository.saveAll(doctorPatientInvoiceCollections);

			List<AdmitCardCollection> admitCardCollections = admitCardRepository.findAll();
			for (AdmitCardCollection admitCardCollection : admitCardCollections) {

				admitCardCollection.setAdminCreatedTime(admitCardCollection.getCreatedTime());
			}
			admitCardRepository.saveAll(admitCardCollections);

			List<DischargeSummaryCollection> dischargeSummaryCollections = dischargeSummaryRepository.findAll();
			for (DischargeSummaryCollection dischargeSummaryCollection : dischargeSummaryCollections) {

				dischargeSummaryCollection.setAdminCreatedTime(dischargeSummaryCollection.getCreatedTime());
			}
			dischargeSummaryRepository.saveAll(dischargeSummaryCollections);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	
	@Override
	public Boolean uploadTreatmentServicesData(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		int dataCountNotUploaded = 0;
		int lineCount = 0;
		try {


			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			UserCollection drCollection = userRepository.findById(doctorObjectId).orElse(null);
			
			List<TreatmentServicesCollection> treatmentServicesCollections = null;

			Scanner scanner = new Scanner(new File(UPLOAD_TREATMENT_SERVICES_DATA_FILE));
	        while (scanner.hasNext()) {
	            List<String> line = CSVUtils.parseLine(scanner.nextLine());
	            
	            if (lineCount > 0) {
					treatmentServicesCollections = treatmentServicesRepository.findByNameAndLocationHospital(line.get(0), locationObjectId, hospitalObjectId, new Sort(Direction.DESC, "createdTime"));
					if(treatmentServicesCollections == null || treatmentServicesCollections.isEmpty()) {
						TreatmentServicesCollection servicesCollection = new TreatmentServicesCollection();
						servicesCollection.setAdminCreatedTime(new Date());
						if(!DPDoctorUtils.anyStringEmpty(line.get(1)))servicesCollection.setCost(Double.parseDouble(line.get(1)));
						servicesCollection.setCreatedBy(drCollection.getTitle() + " " + drCollection.getFirstName());
						servicesCollection.setCreatedTime(new Date());
						servicesCollection.setDiscarded(false);
						servicesCollection.setDoctorId(doctorObjectId);
						servicesCollection.setHospitalId(hospitalObjectId);
						servicesCollection.setLocationId(locationObjectId);
						servicesCollection.setName(line.get(0));
						servicesCollection.setUpdatedTime(new Date());
						servicesCollection.setTreatmentCode("TR" + DPDoctorUtils.generateRandomId());
						servicesCollection.setRankingCount(1);
						
						servicesCollection = treatmentServicesRepository.save(servicesCollection);
						
						transactionalManagementService.addResource(servicesCollection.getId(), Resource.TREATMENTSERVICE,
								false);
						ESTreatmentServiceDocument esTreatmentServiceDocument = new ESTreatmentServiceDocument();
						BeanUtil.map(servicesCollection, esTreatmentServiceDocument);
						esTreatmentService.addEditService(esTreatmentServiceDocument);
						System.out.println(line.get(0));
					}
					
				}
				lineCount++;
				response = true;
				
	        }
	        scanner.close();
			System.out.println("Treatments Services Done. dataCountNotUploaded: " + dataCountNotUploaded);
		} catch (Exception e) {
			response = false;
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public Boolean uploadClinicalNotesData(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		Scanner scanner = null;
		int lineCount = 0;
		String csvLine = null;
		int dataCountNotUploaded = 0;
		FileWriter fileWriter = null;
		Map<String, UserCollection> doctors = new HashMap<String, UserCollection>();
		
		try {
			
			fileWriter = new FileWriter(LIST_CLINICAL_NOTES_NOT_UPLOADED_FILE);

			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			
			UserCollection drCollection = userRepository.findById(doctorObjectId).orElse(null);
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

			scanner = new Scanner(new File(UPLOAD_CLINICAL_NOTES_DATA_FILE));
			
			//			Integer pNUMIndex = null, patientNameIndex = null, mobileNumberIndex = null, contactNumberIndex = null, emailAddressIndex = null, alternateMobileNumberIndex = null,
			//					genderIndex = null, streetAddressIndex = null, localityIndex = null, cityIndex = null, pincodeIndex = null,
			//					nationalIdIndex = null, dobIndex = null, ageIndex = null, bloodGroupIndex = null, remarksIndex = null,
			//					medicalHistoryIndex = null, referredByIndex = null, groupsIndex = null, patientNotesIndex = null;

			while (scanner.hasNext()) {
				csvLine = scanner.nextLine();
	            List<String> line = CSVUtils.parseLine(csvLine);
	            if (lineCount > 0) {
					Boolean createVisit = false;
					if (!DPDoctorUtils.anyStringEmpty(line.get(1), line.get(4))) {
						PatientCollection patientCollection = patientRepository.findByLocationIDHospitalIDAndPNUM(
								locationObjectId, hospitalObjectId, line.get(1).replace("'", ""));
						if (patientCollection != null) {

							Date createdTime = new Date();
							if (!DPDoctorUtils.anyStringEmpty(line.get(0))) {
								SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d HH:mm:ss");
								String dateSTri = line.get(0).replace("'", "")+ " 13:00:00";
								dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
								createdTime = dateFormat.parse(dateSTri);
							}

							String drName = line.get(3).replace("'", "").replace("Dr. ", "").replace("Dr ", "");
							UserCollection userCollection = doctors.get(drName.toLowerCase());
							if (userCollection == null) {
								List<UserCollection> collections = mongoTemplate.aggregate(
										Aggregation.newAggregation(
												Aggregation.match(new Criteria("firstName").regex(drName,"i")),
												new CustomAggregationOperation(new Document("$redact",
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
								
							if(userCollection != null) {
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
								
								String type = !DPDoctorUtils.anyStringEmpty(line.get(4)) ? line.get(4).replace("'", ""):"";
								
								String description = !DPDoctorUtils.anyStringEmpty(line.get(5)) ? line.get(5).replace("'", "") : "";
								
								
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
								}else if(type.equalsIgnoreCase("notes")) {
									if(DPDoctorUtils.allStringsEmpty(clinicalNotesCollection.getNote())) {
										clinicalNotesCollection.setNote(description);
									}else {
										clinicalNotesCollection.setNote(clinicalNotesCollection.getNote() + ", " +description);
									}
								}else if(type.equalsIgnoreCase("comments")) {
									if(clinicalNotesCollection.getComments() != null && !clinicalNotesCollection.getComments().isEmpty()) {
										clinicalNotesCollection.getComments().add(description);
									}else {
										List<String> comments = new ArrayList<>();
										comments.add(description);
										clinicalNotesCollection.setComments(comments);
									}
								}
									
								System.out.println(line.get(0)+".."+line.get(1));

								clinicalNotesCollection = clinicalNotesRepository.save(clinicalNotesCollection);

								if (createVisit)
									addRecord(clinicalNotesCollection, VisitedFor.CLINICAL_NOTES, null,
											clinicalNotesCollection.getPatientId(), clinicalNotesCollection.getDoctorId(),
											clinicalNotesCollection.getLocationId(), clinicalNotesCollection.getHospitalId(),
											clinicalNotesCollection.getId());
								response = true;
							}else {
								dataCountNotUploaded++;
								fileWriter.append("Doctor Not Found :"+csvLine);
							}

						} else {
							dataCountNotUploaded++;
							fileWriter.append("Patient Not Found :"+csvLine);
						}
					}
				}
				lineCount++;
			}
			System.out.println("CN Done. dataCountNotUploaded: " + dataCountNotUploaded);
		} catch (Exception e) {
			response = false;
			e.printStackTrace();
		} finally {
			if (scanner != null) {
				try {
					scanner.close();
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
	public Boolean uploadInvoicesData(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		Scanner scanner = null;
		int lineCount = 0;
		String csvLine = null;
		int dataCountNotUploaded = 0;
		FileWriter fileWriter = null;
		Map<String, UserCollection> doctors = new HashMap<String, UserCollection>();
		
		try {
			
			fileWriter = new FileWriter(LIST_INVOICES_NOT_UPLOADED_FILE);

			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			
			UserCollection drCollection = userRepository.findById(doctorObjectId).orElse(null);
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

			scanner = new Scanner(new File(UPLOAD_INVOICES_DATA_FILE));
			
			//			Integer pNUMIndex = null, patientNameIndex = null, mobileNumberIndex = null, contactNumberIndex = null, emailAddressIndex = null, alternateMobileNumberIndex = null,
			//					genderIndex = null, streetAddressIndex = null, localityIndex = null, cityIndex = null, pincodeIndex = null,
			//					nationalIdIndex = null, dobIndex = null, ageIndex = null, bloodGroupIndex = null, remarksIndex = null,
			//					medicalHistoryIndex = null, referredByIndex = null, groupsIndex = null, patientNotesIndex = null;

			while (scanner.hasNext()) {
				csvLine = scanner.nextLine();
	            List<String> line = CSVUtils.parseLine(csvLine);

				if (lineCount > 0) {
					if (!DPDoctorUtils.anyStringEmpty(line.get(1), line.get(5), line.get(4))) {
						PatientCollection patientCollection = patientRepository.findByLocationIDHospitalIDAndPNUM(
								locationObjectId, hospitalObjectId, line.get(1).replace("'", ""));	
						if (patientCollection != null) {

							Date createdTime = new Date();
							if (!DPDoctorUtils.anyStringEmpty(line.get(0))) {
								SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d hh:mm:ss");
								String dateSTri = line.get(0).replace("'", "")+ " 13:00:00";
								dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
								createdTime = dateFormat.parse(dateSTri);
							}

							String drName = line.get(3).replace("'", "").replace("Dr. ", "").replace("Dr ", "");
							UserCollection userCollection = doctors.get(drName.toLowerCase());
							if (userCollection == null) {
								List<UserCollection> collections = mongoTemplate.aggregate(
										Aggregation.newAggregation(
												Aggregation.match(new Criteria("firstName").regex(drName,"i")),
												new CustomAggregationOperation(new Document("$redact",
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
							if(userCollection!=null) {
								Discount totalDiscount = null;
								double totalCost = 0.0;
								double grandTotal = 0.0;
								
								doctorPatientInvoiceCollection = doctorPatientInvoiceRepository.find(line.get(4).replace("'", ""), userCollection.getId(), locationObjectId, hospitalObjectId);
								if (doctorPatientInvoiceCollection == null) {
									doctorPatientInvoiceCollection = new DoctorPatientInvoiceCollection();

									doctorPatientInvoiceCollection.setDoctorId(userCollection.getId());
									doctorPatientInvoiceCollection.setLocationId(locationObjectId);
									doctorPatientInvoiceCollection.setHospitalId(hospitalObjectId);
									doctorPatientInvoiceCollection.setPatientId(patientCollection.getUserId());
									doctorPatientInvoiceCollection.setInvoiceDate(createdTime);
									doctorPatientInvoiceCollection.setCreatedTime(createdTime);
									doctorPatientInvoiceCollection.setUpdatedTime(createdTime);
									doctorPatientInvoiceCollection.setAdminCreatedTime(createdTime);
									doctorPatientInvoiceCollection.setUniqueInvoiceId(line.get(4).replace("'", ""));
									doctorPatientInvoiceCollection.setCreatedBy(
											(userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
													+ userCollection.getFirstName());

								}else {
									totalDiscount = doctorPatientInvoiceCollection.getTotalDiscount();
									totalCost = doctorPatientInvoiceCollection.getTotalCost();
									grandTotal = doctorPatientInvoiceCollection.getGrandTotal();
								}
								
								List<InvoiceItem> invoiceItems = doctorPatientInvoiceCollection.getInvoiceItems();
								if(invoiceItems == null) invoiceItems = new ArrayList<InvoiceItem>();
								
								InvoiceItem invoiceItem = new InvoiceItem();
								invoiceItem.setDoctorId(userCollection.getId());
								invoiceItem.setDoctorName(doctorPatientInvoiceCollection.getCreatedBy());
								invoiceItem.setName(line.get(5).replace("'", ""));invoiceItem.setType(InvoiceItemType.SERVICE);
								
								if (checkIfNotNullOrNone(line.get(7))) {
									Quantity quantity = new Quantity();
									quantity.setType(QuantityEnum.QTY);
									quantity.setValue(Integer.parseInt(line.get(7).replace("'", "")));
									invoiceItem.setQuantity(quantity);
								}
								
								if (checkIfNotNullOrNone(line.get(6)))
									invoiceItem.setCost(Double.parseDouble(line.get(6).replace("'", "")));
								
								
								if (checkIfNotNullOrNone(line.get(8))) {
									Discount discount = new Discount();
									if (!checkIfNotNullOrNone(line.get(9))) {
										discount.setUnit(UnitType.INR);
									} else if ((line.get(9).replace("'", "")).equalsIgnoreCase("NUMBER")) {
										discount.setUnit(UnitType.INR);
									} else {
										discount.setUnit(UnitType.valueOf(line.get(9).replace("'", "")));
									}
									discount.setValue(Double.parseDouble(line.get(8).replace("'", "")));
									invoiceItem.setDiscount(discount);

									double cost = invoiceItem.getCost();
//									if(invoiceItem.getQuantity().getValue()>0) {
//										cost =  cost * invoiceItem.getQuantity().getValue();
//									}
//									
//
//									if (discount.getUnit().name().equalsIgnoreCase(UnitType.PERCENT.name())) {
//										invoiceItem.setFinalCost(
//												cost - (cost * (discount.getValue() / 100)));
//									} else {
//										invoiceItem.setFinalCost(cost - discount.getValue());
//									}

									if(invoiceItem.getQuantity().getValue() > 0) {
										
										cost =  cost * invoiceItem.getQuantity().getValue();
										
										if(invoiceItem.getDiscount() != null) {
											if (invoiceItem.getDiscount().getUnit().name().equalsIgnoreCase(UnitType.PERCENT.name())) {
												invoiceItem.setFinalCost(cost - (cost * (invoiceItem.getDiscount().getValue() / 100)));
											} else {
												invoiceItem.setFinalCost(cost - invoiceItem.getDiscount().getValue());
											}
											if(totalDiscount == null){
												totalDiscount = new Discount();
												totalDiscount.setUnit(UnitType.INR);
												totalDiscount.setValue(0.0);
											}
											Double totaldiscountValue = totalDiscount.getValue() + (cost - invoiceItem.getFinalCost());
											totalDiscount.setValue(totaldiscountValue);
										}else {
											invoiceItem.setFinalCost(cost);
										}										
									}else {
										invoiceItem.setFinalCost(0.0);
								}
									
									
								}
								
								if (checkIfNotNullOrNone(line.get(16)))
									invoiceItem.setNote(line.get(16).replace("'", ""));
								
								invoiceItems.add(invoiceItem);
								totalCost = totalCost + invoiceItem.getCost();
								grandTotal = grandTotal + invoiceItem.getFinalCost();
								
								doctorPatientInvoiceCollection.setInvoiceItems(invoiceItems);
								doctorPatientInvoiceCollection.setGrandTotal(grandTotal);
								doctorPatientInvoiceCollection.setTotalCost(totalCost);
								doctorPatientInvoiceCollection.setTotalDiscount(totalDiscount);
								doctorPatientInvoiceCollection.setBalanceAmount(grandTotal);
								
								if (line.get(14).equalsIgnoreCase("'1'")) {
									doctorPatientInvoiceCollection.setDiscarded(true);
								}
								
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
								System.out.println(line.get(1)+".."+line.get(5));
							}else {
								dataCountNotUploaded++;
								fileWriter.append("Doctor Not found:"+csvLine);
								fileWriter.append(NEW_LINE_SEPARATOR);
							}
						} else {
							dataCountNotUploaded++;
							fileWriter.append("Patient Not found:"+csvLine);
							fileWriter.append(NEW_LINE_SEPARATOR);
						}
					}else {
						dataCountNotUploaded++;
						fileWriter.append("Data incomplete:"+csvLine);
						fileWriter.append(NEW_LINE_SEPARATOR);
					}
				}
				lineCount++;
				response = true;
			}
			System.out.println("Invoices Done. dataCountNotUploaded: " + dataCountNotUploaded);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (scanner != null) {
				try {
					scanner.close();
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
	public Boolean uploadPaymentsData(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		Scanner scanner = null;
		int lineCount = 0;
		String csvLine = null;
		int dataCountNotUploaded = 0;
		FileWriter fileWriter = null;
		try {
			
			fileWriter = new FileWriter(LIST_PAYMENTS_NOT_UPLOADED_FILE);

			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			LocationCollection  locationCollection = locationRepository.findById(locationObjectId).orElse(null);
			
			UserCollection drCollection = userRepository.findById(doctorObjectId).orElse(null);
			DoctorPatientReceiptCollection doctorPatientReceiptCollection = null;

			scanner = new Scanner(new File(UPLOAD_PAYMENTS_DATA_FILE));
			
			//			Integer pNUMIndex = null, patientNameIndex = null, mobileNumberIndex = null, contactNumberIndex = null, emailAddressIndex = null, alternateMobileNumberIndex = null,
			//					genderIndex = null, streetAddressIndex = null, localityIndex = null, cityIndex = null, pincodeIndex = null,
			//					nationalIdIndex = null, dobIndex = null, ageIndex = null, bloodGroupIndex = null, remarksIndex = null,
			//					medicalHistoryIndex = null, referredByIndex = null, groupsIndex = null, patientNotesIndex = null;

			while (scanner.hasNext()) {
				Boolean save = true;
				csvLine = scanner.nextLine();
	            List<String> line = CSVUtils.parseLine(csvLine);
				if (lineCount > 0) {
					if (!DPDoctorUtils.anyStringEmpty(line.get(1))) {
						PatientCollection patientCollection = patientRepository.findByLocationIDHospitalIDAndPNUM(
								locationObjectId, hospitalObjectId, line.get(1).replace("'", ""));
						if (patientCollection != null) {

							Date createdTime = new Date();
							if (!DPDoctorUtils.anyStringEmpty(line.get(0))) {
								SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d HH:mm:ss");
								String dateSTri = line.get(0).replace("'", "")+ " 13:00:00";
								dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
								createdTime = dateFormat.parse(dateSTri);
							}

							doctorPatientReceiptCollection = doctorPatientReceiptRepository.findByUniqueInvoiceId(line.get(6).replace("'", ""), locationObjectId, hospitalObjectId);

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
								
								doctorPatientReceiptCollection.setCreatedBy(
										(drCollection.getTitle() != null ? drCollection.getTitle() + " " : "")
												+ drCollection.getFirstName());
								
								if(checkIfNotNullOrNone(line.get(3))) {
									doctorPatientReceiptCollection.setUniqueReceiptId(line.get(3).replace("'", ""));
								}else {
									doctorPatientReceiptCollection
									.setUniqueReceiptId(locationCollection.getReceiptInitial()+ ((int) mongoTemplate.count(
											new Query(new Criteria("locationId").is(doctorPatientReceiptCollection.getLocationId())
													.and("hospitalId").is(doctorPatientReceiptCollection.getHospitalId())),
											DoctorPatientReceiptCollection.class) + 1));
								}
							}
							
							DoctorPatientInvoiceCollection doctorPatientInvoiceCollection = null;
							if (line.get(14).equalsIgnoreCase("'1'")) {
								doctorPatientReceiptCollection.setUniqueInvoiceId(line.get(6).replace("'", ""));
								doctorPatientInvoiceCollection = doctorPatientInvoiceRepository.find(line.get(6).replace("'", ""), locationObjectId, hospitalObjectId);
								doctorPatientReceiptCollection.setDiscarded(true);
								doctorPatientReceiptCollection.setAmountPaid(Double.parseDouble(line.get(5).replace("'", "")));
								
								doctorPatientReceiptCollection.setBalanceAmount(doctorPatientInvoiceCollection.getBalanceAmount() - doctorPatientReceiptCollection.getAmountPaid());
								doctorPatientReceiptCollection.setInvoiceId(doctorPatientInvoiceCollection.getId());
								doctorPatientInvoiceCollection.setUniqueInvoiceId(doctorPatientInvoiceCollection.getUniqueInvoiceId());
								
							}else {
								
								if(checkIfNotNullOrNone(line.get(6))) {
									doctorPatientReceiptCollection.setUniqueInvoiceId(line.get(6).replace("'", ""));
									doctorPatientInvoiceCollection = doctorPatientInvoiceRepository.find(line.get(6).replace("'", ""), locationObjectId, hospitalObjectId);
									if(doctorPatientInvoiceCollection == null)save = false;
									else {
										doctorPatientReceiptCollection.setReceiptType(ReceiptType.INVOICE);
										doctorPatientReceiptCollection.setAmountPaid(Double.parseDouble(line.get(5).replace("'", "")));
										doctorPatientReceiptCollection.setBalanceAmount(doctorPatientInvoiceCollection.getBalanceAmount() - doctorPatientReceiptCollection.getAmountPaid());
										doctorPatientReceiptCollection.setInvoiceId(doctorPatientInvoiceCollection.getId());
										doctorPatientInvoiceCollection.setUniqueInvoiceId(doctorPatientInvoiceCollection.getUniqueInvoiceId());
										
										doctorPatientInvoiceCollection.setBalanceAmount(doctorPatientReceiptCollection.getBalanceAmount());
										
										doctorPatientReceiptCollection.setCreatedBy(doctorPatientInvoiceCollection.getCreatedBy());
										doctorPatientReceiptCollection.setDoctorId(doctorPatientInvoiceCollection.getDoctorId());
										doctorObjectId = doctorPatientInvoiceCollection.getDoctorId();
									}
								}else {
									doctorPatientReceiptCollection.setReceiptType(ReceiptType.ADVANCE);
									doctorPatientReceiptCollection.setRemainingAdvanceAmount(Double.parseDouble(line.get(5).replace("'", "")));
									doctorPatientReceiptCollection.setAmountPaid(Double.parseDouble(line.get(5).replace("'", "")));
									doctorPatientReceiptCollection.setBalanceAmount(0.0);
								}
								
							}
							if(save) {
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
								System.out.println(line.get(1)+".."+line.get(4));
								response = true;
							}else {
								dataCountNotUploaded++;
								fileWriter.append("Doctor Not Found:"+csvLine);
								fileWriter.append(NEW_LINE_SEPARATOR);
							}
						} else {
							dataCountNotUploaded++;
							fileWriter.append("Patient Not Found:"+csvLine);
							fileWriter.append(NEW_LINE_SEPARATOR);
						}
					}else {
						dataCountNotUploaded++;
						fileWriter.append("Data incomplete:"+csvLine);
						fileWriter.append(NEW_LINE_SEPARATOR);
					}
				}
				lineCount++;
			}
			System.out.println("Payments Done. dataCountNotUploaded: " + dataCountNotUploaded);
		} catch (Exception e) {
			response = false;
			e.printStackTrace();
		} finally {
			if (scanner != null) {
				try {
					scanner.close();
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
	public Boolean updateTreatmentsData(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		try {
			
			ObjectId locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);
			
			long count = mongoTemplate.count(new Query(new Criteria("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId)), PatientTreatmentCollection.class);
			
			if(count > 0) {
					List<PatientTreatmentCollection> patientTreatmentCollections = mongoTemplate.aggregate(Aggregation.newAggregation(
							Aggregation.match(new Criteria("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId))), PatientTreatmentCollection.class, PatientTreatmentCollection.class).getMappedResults();
					if(patientTreatmentCollections != null) {
						for(PatientTreatmentCollection patientTreatmentCollection : patientTreatmentCollections) {
							
							double totalCost = 0.0;
							double grandTotal = 0.0;
							Discount totalDiscount = null;
							List<Treatment> treatments = patientTreatmentCollection.getTreatments();
							if(treatments != null && !treatments.isEmpty()) {
								for(Treatment treatment : treatments) {
									
									double cost = treatment.getCost();
									if(treatment.getQuantity() == null) {
										Quantity quantity = new Quantity();
										quantity.setType(QuantityEnum.QTY);
										quantity.setValue(1);
										treatment.setQuantity(quantity);
									}
									if(treatment.getQuantity().getValue() > 0) {
											
											cost =  cost * treatment.getQuantity().getValue();
											
											if(treatment.getDiscount() != null) {
												if (treatment.getDiscount().getUnit().name().equalsIgnoreCase(UnitType.PERCENT.name())) {
													treatment.setFinalCost(cost - (cost * (treatment.getDiscount().getValue() / 100)));
												} else {
													treatment.setFinalCost(cost - treatment.getDiscount().getValue());
												}
												if(totalDiscount == null){
													totalDiscount = new Discount();
													totalDiscount.setUnit(UnitType.INR);
													totalDiscount.setValue(0.0);
												}
												Double totaldiscountValue = totalDiscount.getValue() + (cost - treatment.getFinalCost());
												totalDiscount.setValue(totaldiscountValue);
											}else {
												treatment.setFinalCost(cost);
											}										
										}else {
											treatment.setFinalCost(0.0);
									}
									
									totalCost = totalCost + cost;
									grandTotal = grandTotal + treatment.getFinalCost();
								}
								patientTreatmentCollection.setTreatments(null);	
								patientTreatmentCollection.setTreatments(treatments);
						   }
						   patientTreatmentCollection.setTotalDiscount(totalDiscount);
						   patientTreatmentCollection.setTotalCost(totalCost);
						   patientTreatmentCollection.setGrandTotal(grandTotal);
						   patientTreatmentCollection.setUpdatedTime(new Date());
						   patientTreatmentCollection = patientTreamentRepository.save(patientTreatmentCollection);
						   response = true;
						}		
					}
//				}
			}
		}catch (Exception e) {
			response = false;
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public Boolean uploadImages(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(LIST_IMAGES_RESULT);
			
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);
			
			
			UserCollection userCollection = userRepository.findById(doctorObjectId).orElse(null);
			LocationCollection locationCollection = locationRepository.findById(locationObjectId).orElse(null);
			
			BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
			AmazonS3 s3client = new AmazonS3Client(credentials);
			int profileCount = 0, totalCount = 0, notFound = 0, recordsAdded = 0;;
			ObjectListing listing = s3client.listObjects(bucketName,"2809");
			
			List<S3ObjectSummary> summaries = listing.getObjectSummaries();

			while (listing.isTruncated()) {
			   listing = s3client.listNextBatchOfObjects (listing);
			   summaries.addAll (listing.getObjectSummaries());
			}
			
			
			if(summaries != null) {
				for(S3ObjectSummary obListing : summaries) {
					String fileName = obListing.getKey().replace("2809/", "");
					String fileExtension = FilenameUtils.getExtension(fileName);
					System.out.println(fileName);
					int index = fileName.indexOf("MDC");
					
					if(index > 0) {
						String pNum = fileName.substring(index);
						String dateStr = pNum;
						pNum = pNum.substring(pNum.indexOf("MDC"), pNum.indexOf("_"));

						PatientCollection patientCollection = patientRepository.findByLocationIDHospitalIDAndPNUM(locationObjectId, hospitalObjectId, pNum);
						
						if(patientCollection != null) {
							if(fileName.startsWith("Profile")) {
								
								fileName = fileName.substring(index);
								String path = "profile-images";
																				
								S3Object object = s3client.getObject(new GetObjectRequest(bucketName, obListing.getKey()));
								InputStream objectData = object.getObjectContent();
						
								BufferedImage originalImage = ImageIO.read(objectData);
								ByteArrayOutputStream outstream = new ByteArrayOutputStream();
								ImageIO.write(originalImage, fileExtension, outstream);
								byte[] buffer = outstream.toByteArray();
								objectData = new ByteArrayInputStream(buffer);
						
								String contentType = URLConnection.guessContentTypeFromStream(objectData);
								ObjectMetadata metadata = new ObjectMetadata();
								metadata.setContentLength(buffer.length);
								metadata.setContentEncoding(fileExtension);
								metadata.setContentType(contentType);
								metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
								s3client.putObject(new PutObjectRequest(bucketName, path+"/"+fileName, objectData, metadata));
													
								patientCollection.setImageUrl(path+"/"+fileName);						
								patientCollection.setThumbnailUrl(saveThumnailImage(path, fileName, s3client));
								
								patientCollection.setUpdatedTime(new Date());
								patientCollection = patientRepository.save(patientCollection);
								transactionalManagementService.addResource(patientCollection.getUserId(), Resource.PATIENT,
										false);
								transactionalManagementService.checkPatient(patientCollection.getUserId());
								fileWriter.append("Profile Picture Update    "+fileName);
								fileWriter.append(NEW_LINE_SEPARATOR);
								
								profileCount=profileCount+1;
								
							}else {
								
								dateStr = dateStr.replace(pNum, "").replaceFirst("_", "");
								String time = dateStr;
								dateStr = dateStr.substring(0, dateStr.indexOf("_"));
									
								time = time.replace(dateStr, "").replaceFirst("_", "");
								time = time.substring(0, time.indexOf("_"));

								fileName = pNum+"_"+dateStr+"_"+time+"."+fileExtension;
								
								RecordsCollection recordsCollection = new RecordsCollection();
								
								SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d HH:mm:ss");
								String dateSTri = dateStr +" "+time;
								dateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
								Date fromDate = dateFormat.parse(dateSTri);
								
								recordsCollection.setPatientId(patientCollection.getUserId());
								recordsCollection.setLocationId(locationObjectId);
								recordsCollection.setDoctorId(doctorObjectId);
								recordsCollection.setHospitalId(hospitalObjectId);
								recordsCollection.setAdminCreatedTime(fromDate);
								recordsCollection.setCreatedTime(fromDate);
								recordsCollection.setUpdatedTime(fromDate);
								
								String recordLable = fileName.replace("."+fileExtension, "");
								String path = "records" + File.separator + patientCollection.getUserId();

								String recordPath = path + File.separator + fileName;

								recordsCollection.setRecordsUrl(recordPath);
								recordsCollection.setRecordsPath(recordPath);
								recordsCollection.setRecordsLabel(recordLable);
								
								recordsCollection.setUniqueEmrId(UniqueIdInitial.REPORTS.getInitial() + DPDoctorUtils.generateRandomId());

								recordsCollection.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());

								recordsCollection.setUploadedByLocation(locationCollection.getLocationName());
																
								S3Object object = s3client.getObject(new GetObjectRequest(bucketName, obListing.getKey()));
								InputStream objectData = object.getObjectContent();
												
								String contentType = URLConnection.guessContentTypeFromStream(objectData);
								ObjectMetadata metadata = new ObjectMetadata();
								metadata.setContentEncoding(fileExtension);
								metadata.setContentType(contentType);
								metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
								s3client.putObject(new PutObjectRequest(bucketName, recordPath, objectData, metadata));
								
								recordsCollection = recordsRepository.save(recordsCollection);
								
								
								Records visitRecord = new Records();
								BeanUtil.map(recordsCollection, visitRecord);
								visitRecord.setPrescriptionId(null);
								String visitId = patientVisitService.addRecord(visitRecord, VisitedFor.REPORTS, null);
								
								fileWriter.append("Record Added "+fileName);
								fileWriter.append(NEW_LINE_SEPARATOR);
								
								recordsAdded = recordsAdded +1;
							}
						}else {
							fileWriter.append("Patient Not Found "+fileName);
							fileWriter.append(NEW_LINE_SEPARATOR);
							notFound = notFound + 1;
						}
					}else {
						
						fileWriter.append("No PID "+fileName);
						fileWriter.append(NEW_LINE_SEPARATOR);
					}	
					
			        
					totalCount = totalCount + 1;
				}
			}
			System.out.println("totalCount" + totalCount);
			System.out.println("recordsAdded" + recordsAdded);
			System.out.println("profileCount" + profileCount);
			System.out.println("notFound" + notFound);
		}catch (Exception e) {
			response = false;
			e.printStackTrace();
		}
		return response;
	}

	private String saveThumnailImage(String path, String fileName, AmazonS3 s3client) {
		String thumbnailUrl = null;
		try {

			S3Object object = s3client.getObject(new GetObjectRequest(bucketName, path + File.separator + fileName));
			
			InputStream objectData = object.getObjectContent();
	
			BufferedImage originalImage = ImageIO.read(objectData);
			double ratio = (double) originalImage.getWidth() / originalImage.getHeight();
			int height = originalImage.getHeight();
	
			int width = originalImage.getWidth();
			int max = 120;
			if (width == height) {
				width = max;
				height = max;
			} else if (width > height) {
				height = max;
				width = (int) (ratio * max);
			} else {
				width = max;
				height = (int) (max / ratio);
			}
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			img.createGraphics().drawImage(originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0,
					null);
			
			String fileExtension = FilenameUtils.getExtension(fileName);
			fileName = fileName.replace("."+fileExtension, "") + "_thumb." + fileExtension;
			thumbnailUrl = path + File.separator + fileName;
	
			originalImage.flush();
			originalImage = null;
	
			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			ImageIO.write(img, fileExtension, outstream);
			byte[] buffer = outstream.toByteArray();
			objectData = new ByteArrayInputStream(buffer);
	
			String contentType = URLConnection.guessContentTypeFromStream(objectData);
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(buffer.length);
			metadata.setContentEncoding(fileExtension);
			metadata.setContentType(contentType);
			metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
			s3client.putObject(new PutObjectRequest(bucketName, thumbnailUrl, objectData, metadata));
		} catch (AmazonServiceException ase) {
			System.out.println("Error Message: " + ase.getMessage() + " HTTP Status Code: " + ase.getStatusCode()
			+ " AWS Error Code:   " + ase.getErrorCode() + " Error Type:       " + ase.getErrorType()
			+ " Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println(
					"Caught an AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		} catch (Exception e) {
			System.out.println("Error Message: " + e.getMessage());
		}
		return thumbnailUrl;
	}

	@Override
	public Boolean updateTreatmentServices() {
		Boolean response = false;
		try {
			List<TreatmentServiceUpdateResponse> treatmentServiceUpdateResponses = mongoTemplate.aggregate(
					Aggregation.newAggregation(new CustomAggregationOperation(new Document("$group",  
							new BasicDBObject("_id", new BasicDBObject("name", "$name")
									                .append("locationId", "$locationId"))
							.append("count", new BasicDBObject("$sum", 1))
							.append("doctorId", new BasicDBObject("$first", "$doctorId"))
							.append("locationId", new BasicDBObject("$first", "$locationId"))
							.append("treatmentServiceIds", new BasicDBObject("$push", "$_id")))), 
							Aggregation.match(new Criteria("count").gt(1))), 
					TreatmentServicesCollection.class, TreatmentServiceUpdateResponse.class).getMappedResults();
			
			if(treatmentServiceUpdateResponses != null && !treatmentServiceUpdateResponses.isEmpty()) {
				for(TreatmentServiceUpdateResponse treatmentServiceUpdateResponse : treatmentServiceUpdateResponses) {
					if(treatmentServiceUpdateResponse.getTreatmentServiceIds() != null && !treatmentServiceUpdateResponse.getTreatmentServiceIds().isEmpty()) {
						for(int i = 0; i<treatmentServiceUpdateResponse.getTreatmentServiceIds().size(); i++) {
							ObjectId treatmentServiceId = null;
							if(i == 0) {
								treatmentServiceId = treatmentServiceUpdateResponse.getTreatmentServiceIds().get(i);
							}else {
								ObjectId serviceId = treatmentServiceUpdateResponse.getTreatmentServiceIds().get(i);
								//patient treatment
								List<PatientTreatmentCollection> patientTreatmentCollections = mongoTemplate.aggregate(
										Aggregation.newAggregation(Aggregation.match(new Criteria("treatments.treatmentServiceId").is(serviceId))), 
										PatientTreatmentCollection.class, PatientTreatmentCollection.class).getMappedResults();
								if(patientTreatmentCollections != null && !patientTreatmentCollections.isEmpty()) {
									for(PatientTreatmentCollection patientTreatmentCollection: patientTreatmentCollections) {
										List<Treatment> treatments = patientTreatmentCollection.getTreatments();
										patientTreatmentCollection.setTreatments(null);
										for(Treatment treatment : treatments) {
											treatment.setTreatmentServiceId(treatmentServiceId);
											
										}
										patientTreatmentCollection.setTreatments(treatments);
										patientTreatmentCollection.setUpdatedTime(new Date());
										patientTreatmentCollection = patientTreamentRepository.save(patientTreatmentCollection);
									}
								}
								
								//invoices
								List<DoctorPatientInvoiceCollection> doctorPatientInvoiceCollections = mongoTemplate.aggregate(
										Aggregation.newAggregation(Aggregation.match(new Criteria("invoiceItems.itemId").is(serviceId))), 
										DoctorPatientInvoiceCollection.class, DoctorPatientInvoiceCollection.class).getMappedResults();
								if(doctorPatientInvoiceCollections != null && !doctorPatientInvoiceCollections.isEmpty()) {
									for(DoctorPatientInvoiceCollection doctorPatientInvoiceCollection : doctorPatientInvoiceCollections) {
										List<InvoiceItem> invoiceItems = doctorPatientInvoiceCollection.getInvoiceItems();
										doctorPatientInvoiceCollection.setInvoiceItems(null);
										for(InvoiceItem invoiceItem : invoiceItems) {
											invoiceItem.setItemId(treatmentServiceId);
											
										}
										doctorPatientInvoiceCollection.setInvoiceItems(invoiceItems);
										doctorPatientInvoiceCollection.setUpdatedTime(new Date());
										doctorPatientInvoiceCollection = doctorPatientInvoiceRepository.save(doctorPatientInvoiceCollection);
									}
								}
								
							 ESTreatmentServiceDocument esTreatmentServiceDocument = 	eSTreatmentServiceRepository.findById(serviceId.toString()).orElse(null);
							 if(esTreatmentServiceDocument != null)eSTreatmentServiceRepository.delete(esTreatmentServiceDocument);
							 
							 TreatmentServicesCollection servicesCollection = treatmentServicesRepository.findById(serviceId).orElse(null);
							 if(servicesCollection != null)treatmentServicesRepository.delete(servicesCollection);
							}
						
						
						}
					}
				}
			}
		}catch(Exception e) {
			response = false;
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public Boolean updateBillingData(String locationId, String hospitalId) {
		Boolean response = false;
		try {
			ObjectId locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);
			
			List<DoctorPatientInvoiceCollection> doctorPatientInvoiceCollections = mongoTemplate.aggregate(Aggregation.newAggregation(
							Aggregation.match(new Criteria("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId))), DoctorPatientInvoiceCollection.class, DoctorPatientInvoiceCollection.class).getMappedResults();
			if(doctorPatientInvoiceCollections != null) {
						for(DoctorPatientInvoiceCollection doctorPatientInvoiceCollection : doctorPatientInvoiceCollections) {
							if(!doctorPatientInvoiceCollection.getUniqueInvoiceId().startsWith("INV"))
								doctorPatientInvoiceCollection.setUniqueInvoiceId("INV"+doctorPatientInvoiceCollection.getUniqueInvoiceId());
							
							double totalCost = 0.0;
							double grandTotal = 0.0;
							Discount totalDiscount = null;
							List<InvoiceItem> treatments = doctorPatientInvoiceCollection.getInvoiceItems();
							if(treatments != null && !treatments.isEmpty()) {
								for(InvoiceItem treatment : treatments) {
									
									double cost = treatment.getCost();
									if(treatment.getQuantity() == null) {
										Quantity quantity = new Quantity();
										quantity.setType(QuantityEnum.QTY);
										quantity.setValue(1);
										treatment.setQuantity(quantity);
									}
									if(treatment.getQuantity().getValue() > 0) {
											
											cost =  cost * treatment.getQuantity().getValue();
											
											if(treatment.getDiscount() != null) {
												if (treatment.getDiscount().getUnit().name().equalsIgnoreCase(UnitType.PERCENT.name())) {
													treatment.setFinalCost(cost - (cost * (treatment.getDiscount().getValue() / 100)));
												} else {
													treatment.setFinalCost(cost - treatment.getDiscount().getValue());
												}
												if(totalDiscount == null){
													totalDiscount = new Discount();
													totalDiscount.setUnit(UnitType.INR);
													totalDiscount.setValue(0.0);
												}
												Double totaldiscountValue = totalDiscount.getValue() + (cost - treatment.getFinalCost());
												totalDiscount.setValue(totaldiscountValue);
											}else {
												treatment.setFinalCost(cost);
											}										
										}else {
											treatment.setFinalCost(0.0);
									}
									
									totalCost = totalCost + cost;
									grandTotal = grandTotal + treatment.getFinalCost();
								}
								doctorPatientInvoiceCollection.setInvoiceItems(null);
								doctorPatientInvoiceCollection.setInvoiceItems(treatments);
						   }
							doctorPatientInvoiceCollection.setTotalDiscount(totalDiscount);
							doctorPatientInvoiceCollection.setTotalCost(totalCost);
							doctorPatientInvoiceCollection.setGrandTotal(grandTotal);
							doctorPatientInvoiceCollection.setBalanceAmount(grandTotal);
							doctorPatientInvoiceCollection = doctorPatientInvoiceRepository.save(doctorPatientInvoiceCollection);

							DoctorPatientDueAmountCollection doctorPatientDueAmountCollection = doctorPatientDueAmountRepository
									.find(doctorPatientInvoiceCollection.getPatientId(),
											doctorPatientInvoiceCollection.getDoctorId(),
											doctorPatientInvoiceCollection.getLocationId(),
											doctorPatientInvoiceCollection.getHospitalId());
							doctorPatientDueAmountCollection.setDueAmount(doctorPatientDueAmountCollection.getDueAmount() + doctorPatientInvoiceCollection.getBalanceAmount());
							doctorPatientDueAmountCollection = doctorPatientDueAmountRepository.save(doctorPatientDueAmountCollection);
							
							DoctorPatientLedgerCollection doctorPatientLedgerCollection = doctorPatientLedgerRepository
									.findByInvoiceId(doctorPatientInvoiceCollection.getId());
							doctorPatientLedgerCollection.setDebitAmount(doctorPatientInvoiceCollection.getBalanceAmount());
							doctorPatientLedgerCollection = doctorPatientLedgerRepository.save(doctorPatientLedgerCollection);
						}
						response = true;
			}
			
			List<DoctorPatientReceiptCollection> doctorPatientReceiptCollections = mongoTemplate.aggregate(Aggregation.newAggregation(
					Aggregation.match(new Criteria("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId))), DoctorPatientReceiptCollection.class, DoctorPatientReceiptCollection.class).getMappedResults();
			if(doctorPatientReceiptCollections != null) {
					for(DoctorPatientReceiptCollection doctorPatientReceiptCollection : doctorPatientReceiptCollections) {
						if(doctorPatientReceiptCollection.getUniqueInvoiceId() != null && !doctorPatientReceiptCollection.getUniqueInvoiceId().startsWith("INV"))
							doctorPatientReceiptCollection.setUniqueInvoiceId("INV"+doctorPatientReceiptCollection.getUniqueInvoiceId());
							
						if(doctorPatientReceiptCollection.getUniqueReceiptId() != null && !doctorPatientReceiptCollection.getUniqueReceiptId().startsWith("RC"))
							doctorPatientReceiptCollection.setUniqueReceiptId("RC"+doctorPatientReceiptCollection.getUniqueReceiptId());
						

						DoctorPatientInvoiceCollection doctorPatientInvoiceCollection = null;
						if(doctorPatientReceiptCollection.getInvoiceId() != null) {
							doctorPatientInvoiceCollection = doctorPatientInvoiceRepository.findById(doctorPatientReceiptCollection.getInvoiceId()).orElse(null);
							doctorPatientReceiptCollection.setBalanceAmount(doctorPatientInvoiceCollection.getBalanceAmount() - doctorPatientReceiptCollection.getAmountPaid());
						}

						doctorPatientReceiptCollection = doctorPatientReceiptRepository.save(doctorPatientReceiptCollection);

						if(doctorPatientInvoiceCollection != null) {
							doctorPatientInvoiceCollection.setBalanceAmount(doctorPatientReceiptCollection.getBalanceAmount());
							doctorPatientInvoiceCollection = doctorPatientInvoiceRepository.save(doctorPatientInvoiceCollection);
						}
						
						
						DoctorPatientLedgerCollection doctorPatientLedgerCollection = doctorPatientLedgerRepository.findByReceiptId(doctorPatientReceiptCollection.getId());
						if(doctorPatientLedgerCollection != null) {
							doctorPatientLedgerCollection.setCreditAmount(doctorPatientReceiptCollection.getAmountPaid());
							doctorPatientLedgerCollection = doctorPatientLedgerRepository.save(doctorPatientLedgerCollection);
						}
						
						DoctorPatientDueAmountCollection doctorPatientDueAmountCollection = doctorPatientDueAmountRepository
								.find(doctorPatientReceiptCollection.getPatientId(),
										doctorPatientReceiptCollection.getDoctorId(),
										doctorPatientReceiptCollection.getLocationId(),
										doctorPatientReceiptCollection.getHospitalId());
						if(doctorPatientDueAmountCollection != null) {
							doctorPatientDueAmountCollection.setDueAmount(doctorPatientDueAmountCollection.getDueAmount() - doctorPatientReceiptCollection.getAmountPaid());
							doctorPatientDueAmountCollection = doctorPatientDueAmountRepository.save(doctorPatientDueAmountCollection);
						}
						
					}
					response = true;
		}

	}catch(Exception e) {
		response = false;
		e.printStackTrace();
	}
	return response;
	}

	@Override
	public Boolean upploadReports(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		try {
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			
			UserCollection drCollection = userRepository.findById(doctorObjectId).orElse(null);
			String createdBy = (drCollection.getTitle() != null ? drCollection.getTitle() + " " : "")+ drCollection.getFirstName();
			
			LocationCollection locationCollection = locationRepository.findById(locationObjectId).orElse(null);

			File dir = new File("/home/ubuntu/Reports");
			File[] directoryListing = dir.listFiles();
			if (directoryListing != null) {
			    for (File child : directoryListing) {
			    	
			      int indexOfPID = child.getName().indexOf("GDSC");
			      System.out.println(child.getName());
			      if(indexOfPID>0) {
			    	  String[] string = child.getName().substring(indexOfPID).split("_");
				      String PID = string[0];
				      
				      PatientCollection patientCollection = patientRepository.findByLocationIDHospitalIDAndPNUM(
				    		  locationObjectId, hospitalObjectId, PID);
						if (patientCollection != null) {
							 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							format.setTimeZone(TimeZone.getTimeZone("IST"));
						    
							Date createdTime = format.parse(string[1] +" "+string[2]);
						    RecordsCollection recordsCollection = new RecordsCollection();
						
						    recordsCollection.setDoctorId(doctorObjectId);
						    recordsCollection.setLocationId(locationObjectId);
						    recordsCollection.setHospitalId(hospitalObjectId);
						    recordsCollection.setPatientId(patientCollection.getUserId());
						    recordsCollection.setAdminCreatedTime(createdTime);
						    recordsCollection.setCreatedTime(createdTime);
						    recordsCollection.setUpdatedTime(new Date());
							recordsCollection.setUniqueEmrId(UniqueIdInitial.REPORTS.getInitial() + DPDoctorUtils.generateRandomId());
							recordsCollection.setCreatedBy(createdBy);
							recordsCollection.setUploadedByLocation(locationCollection.getLocationName());
							recordsCollection.setRecordsLabel(FilenameUtils.getBaseName(child.getName()));
							String path = "records" + File.separator + recordsCollection.getPatientId();

							String recordPath = path + File.separator + child.getName();

							recordsCollection.setRecordsUrl(recordPath);
							recordsCollection.setRecordsPath(recordPath);
							
							
							//save image
							BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_KEY, AWS_SECRET_KEY);
							AmazonS3 s3client = new AmazonS3Client(credentials);
							
							FileInputStream inputStream =  new FileInputStream(child);
							byte[] byteArray = IOUtils.toByteArray(inputStream);
							Long contentLength = Long.valueOf(byteArray.length);
							inputStream.close();
							FileInputStream fis = new FileInputStream(child);
							String contentType = URLConnection.guessContentTypeFromStream(fis);
							
							ObjectMetadata metadata = new ObjectMetadata();

							metadata.setContentLength(contentLength);
							metadata.setContentEncoding(FilenameUtils.getExtension(child.getName()));
							metadata.setContentType(contentType);
							metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);

							s3client.putObject(new PutObjectRequest(bucketName, recordPath, fis, metadata));
							
							recordsCollection.setRecordsType("IMAGE");
							
							if(FilenameUtils.getExtension(child.getName()).contains("pptx")) {
								recordsCollection.setRecordsType("PPT");
						     }else if(FilenameUtils.getExtension(child.getName()).contains("pdf")) {
									recordsCollection.setRecordsType("PDF");
						     }
							
							recordsCollection = recordsRepository.save(recordsCollection);
							
							Records records = new Records();
							BeanUtil.map(recordsCollection, records);
							String visitId = patientTrackService.addRecord(records, VisitedFor.REPORTS, null);
							System.out.println(visitId);
							fis.close();
							response = true;
						}
			      }else {
			    	  System.out.println("Patient Id is missing");
			      }
			      
			    }
			  }
		}catch(Exception e) {
			response = false;
			e.printStackTrace();
		}	
		return response;
	}
}
