package com.dpdocter.services.impl;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.AppointmentDownloadData;
import com.dpdocter.beans.ClinicalNotesDownloadData;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.InvoiceDownloadData;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.PatientDownloadData;
import com.dpdocter.beans.PrescriptionDownloadData;
import com.dpdocter.beans.ReceiptDownloadData;
import com.dpdocter.beans.TestAndRecordData;
import com.dpdocter.beans.TreatmentDownloadData;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.ClinicalNotesCollection;
import com.dpdocter.collections.ComplaintCollection;
import com.dpdocter.collections.DiagnosisCollection;
import com.dpdocter.collections.DiagnosticTestCollection;
import com.dpdocter.collections.DoctorPatientInvoiceCollection;
import com.dpdocter.collections.DoctorPatientReceiptCollection;
import com.dpdocter.collections.DownloadDataRequestCollection;
import com.dpdocter.collections.DynamicUICollection;
import com.dpdocter.collections.InvestigationCollection;
import com.dpdocter.collections.NotesCollection;
import com.dpdocter.collections.ObservationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientTreatmentCollection;
import com.dpdocter.collections.PrescriptionCollection;
import com.dpdocter.enums.AppointmentType;
import com.dpdocter.enums.ClinicalNotesPermissionEnum;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DiagnosticTestRepository;
import com.dpdocter.repository.DoctorPatientInvoiceRepository;
import com.dpdocter.repository.DoctorPatientReceiptRepository;
import com.dpdocter.repository.DownloadDataRequestRepository;
import com.dpdocter.request.ExportRequest;
import com.dpdocter.services.DownloadDataService;
import com.dpdocter.services.MailService;
import com.mongodb.BasicDBObject;
import com.opencsv.CSVWriter;

import common.util.web.DPDoctorUtils;

@Service
public class DownloadDataServiceImpl implements DownloadDataService{

	private static Logger logger = Logger.getLogger(DownloadDataServiceImpl.class.getName());
	
	@Autowired
	DownloadDataRequestRepository downloadDataRequestRepository;
	
	private static final String NEW_LINE_SEPARATOR = "\n";

//	@Value(value = "${patients.data.file}")
//	private String PATIENTS_DATA_FILE;
	
	@Autowired
	private DiagnosticTestRepository diagnosticTestRepository;

	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private DoctorPatientInvoiceRepository doctorPatientInvoiceRepository;
	
	@Autowired
	private DoctorPatientReceiptRepository doctorPatientReceiptRepository;
	
	@Override
	public Boolean downlaodData(ExportRequest request) {
		Boolean response = false;
		DownloadDataRequestCollection downloadDataRequestCollection = null;
		try {
			downloadDataRequestCollection = new DownloadDataRequestCollection();
			BeanUtil.map(request, downloadDataRequestCollection);
			downloadDataRequestCollection.setCreatedTime(new Date());
			downloadDataRequestCollection.setUpdatedTime(new Date());
			downloadDataRequestCollection.setAdminCreatedTime(new Date());
			downloadDataRequestCollection = downloadDataRequestRepository.save(downloadDataRequestCollection);
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error downloading data");
			throw new BusinessException(ServiceError.Unknown, "Error downloading data");
		}
		return response;
	}

//	@Scheduled(cron="00 00 3 * * *", zone="IST")
	@Override
	public void sendDataToDoctor(){
		
		List<DownloadDataRequestCollection> downloadDataRequestCollections = mongoTemplate.aggregate(
				Aggregation.newAggregation(Aggregation.match(new Criteria("isMailSend").is(false)
						.and("dataType").exists(true))), 
				DownloadDataRequestCollection.class, DownloadDataRequestCollection.class).getMappedResults();
		
		if(downloadDataRequestCollections != null && !downloadDataRequestCollections.isEmpty())
		for(DownloadDataRequestCollection downloadDataRequestCollection : downloadDataRequestCollections) {
			
			List<MailAttachment> mailAttachments = new ArrayList<MailAttachment>();
			List<ComponentType> dataType = downloadDataRequestCollection.getDataType();
			
			if(dataType.contains(ComponentType.PATIENT)) {
				mailAttachments.add(generatePatientData(downloadDataRequestCollection.getDoctorId(), downloadDataRequestCollection.getLocationId(), downloadDataRequestCollection.getHospitalId()));
			}
			
			if(dataType.contains(ComponentType.PRESCRIPTIONS)) {
				mailAttachments.add(generatePatientData(downloadDataRequestCollection.getDoctorId(), downloadDataRequestCollection.getLocationId(), downloadDataRequestCollection.getHospitalId()));
			}
			
			downloadDataRequestCollection.setIsMailSend(true);
			downloadDataRequestCollection.setMailSendTime(new Date());
			downloadDataRequestCollection = downloadDataRequestRepository.save(downloadDataRequestCollection);
		}
		
	}

	@Override
	public MailAttachment generatePatientData(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId){
		MailAttachment mailAttachment = new MailAttachment();
		CSVWriter csvWriter = null;
		try {
			Criteria criteria = new Criteria("locationId").is(locationId).and("hospitalId").is(hospitalId).and("doctorId").is(doctorId);
			
			Aggregation aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria), Aggregation.lookup("user_cl", "userId", "_id", "user"),
							Aggregation.unwind("user"),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$user").append("preserveNullAndEmptyArrays", true))),
							Aggregation.lookup("patient_group_cl", "userId", "patientId", "patientGroupCollections"),
							Aggregation.match(new Criteria().orOperator(
									new Criteria("patientGroupCollections.discarded").is(false),
									new Criteria("patientGroupCollections").size(0))),
							
							new CustomAggregationOperation(
									new Document("$unwind", new BasicDBObject("path", "$patientGroupCollections")
											.append("preserveNullAndEmptyArrays", true))),
									
							
							Aggregation.lookup("group_cl", "patientGroupCollections.groupId", "_id", "groups"),
							new CustomAggregationOperation(
									new Document("$unwind", new BasicDBObject("path", "$groups")
											.append("preserveNullAndEmptyArrays", true))),
							
							
							Aggregation.lookup("referrences_cl", "referredBy", "_id", "reference"),
							new CustomAggregationOperation(
									new Document("$unwind", new BasicDBObject("path", "$reference")
											.append("preserveNullAndEmptyArrays", true))),
							
							new CustomAggregationOperation(new Document("$project", new BasicDBObject("_id", "$_id")
									.append("registrationDate", "$registrationDate")
									.append("patientId", "$PID")
									.append("localPatientName", "$localPatientName")
									.append("mobileNumber", "$user.mobileNumber")
									.append("emailAddress", "$emailAddress")
									.append("secMobile", "$secMobile")
									.append("gender", "$gender")
									.append("country", "$address.country")
									.append("city", "$address.city")
									.append("state", "$address.state")
									.append("postalCode", "$address.postalCode")
									.append("locality", "$address.locality")
									.append("landmarkDetails", "$address.landmarkDetails")
									.append("streetAddress", "$address.streetAddress")
									.append("adhaarId", "$adhaarId")
									.append("panCardNumber", "$panCardNumber")
									.append("drivingLicenseId", "$drivingLicenseId")
									.append("insuranceId", "$insuranceId")
									.append("dob", "$dob")
									.append("bloodGroup", "$bloodGroup")
									.append("referredBy", "$reference.reference")
									.append("groups", "$groups.name"))),
							
							new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id")
									.append("registrationDate", new BasicDBObject("$first","$registrationDate"))
									.append("patientId", new BasicDBObject("$first","$patientId"))
									.append("localPatientName", new BasicDBObject("$first","$localPatientName"))
									.append("mobileNumber", new BasicDBObject("$first","$mobileNumber"))
									.append("emailAddress", new BasicDBObject("$first","$emailAddress"))
									.append("secMobile", new BasicDBObject("$first","$secMobile"))
									.append("gender", new BasicDBObject("$first","$gender"))
									.append("country", new BasicDBObject("$first","$country"))
									.append("city", new BasicDBObject("$first","$city"))
									.append("state", new BasicDBObject("$first","$state"))
									.append("postalCode", new BasicDBObject("$first","$postalCode"))
									.append("locality", new BasicDBObject("$first","$locality"))
									.append("landmarkDetails", new BasicDBObject("$first","$landmarkDetails"))
									.append("streetAddress", new BasicDBObject("$first","$streetAddress"))
									.append("adhaarId", new BasicDBObject("$first","$adhaarId"))
									.append("panCardNumber", new BasicDBObject("$first","$panCardNumber"))
									.append("drivingLicenseId", new BasicDBObject("$first","$drivingLicenseId"))
									.append("insuranceId", new BasicDBObject("$first","$insuranceId"))
									.append("dob", new BasicDBObject("$first","$dob"))
									.append("bloodGroup", new BasicDBObject("$first","$bloodGroup"))
									.append("referredBy", new BasicDBObject("$first","$referredBy"))
									.append("groups", new BasicDBObject("$push","$groups")))),
							
							new CustomAggregationOperation(new Document("$sort", new BasicDBObject("date", 1)))
							);

			List<PatientDownloadData> patientDownloadDatas = mongoTemplate.aggregate(aggregation.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build()), PatientCollection.class, PatientDownloadData.class).getMappedResults();
			
			csvWriter = new CSVWriter(new FileWriter("/home/ubuntu/Patients.csv"));
			
			
		    writeHeader(PatientDownloadData.class, csvWriter, ComponentType.PATIENT, null);
		    
		    for (PatientDownloadData patientDownloadData : patientDownloadDatas) {
		    		if(patientDownloadData.getDob() != null) {
		    			patientDownloadData.setDateOfBirth(patientDownloadData.getDob().getDays() +"/"+ patientDownloadData.getDob().getMonths()+"/" + patientDownloadData.getDob().getYears() +"/");
		    			if(patientDownloadData.getDob().getAge() != null)patientDownloadData.setAge(patientDownloadData.getDob().getAge().getYears()+"");
		    		}
		    		if(patientDownloadData.getRegistrationDate() != null) {
		    			SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
			    		patientDownloadData.setDate(sdf.format(new Date(patientDownloadData.getRegistrationDate())));
		    		}
		    		
		    		patientDownloadData.setDob(null);
		    		writeData(patientDownloadData, csvWriter, ComponentType.PATIENT, null);
		    }
		 
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error downloading patient data");
//			throw new BusinessException(ServiceError.Unknown, "Error downloading patient data");
		}finally {
			try {
				if (csvWriter != null) {
					csvWriter.flush();
					csvWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return mailAttachment;
		
	}

	private void writeHeader(Class classOfObject, FileWriter fileWriter) throws IOException {
		int index = 1;
		String headerString = "";
	    for (Field field : classOfObject.getDeclaredFields()) {
	        field.setAccessible(true);
	        
	        if(!field.getName().equalsIgnoreCase("dob")) {
	        		if(!DPDoctorUtils.anyStringEmpty(headerString)) headerString = headerString + field.getName();
		        else headerString = headerString + "," + field.getName();
	        }
	        
//	    		Cell cell = fileWriter.createCell(++index);
//		    cell.setCellValue(field.getName());
	    }
	    fileWriter.append(headerString);
	    fileWriter.append(NEW_LINE_SEPARATOR);
	}

	private void writeData(Object obj, FileWriter fileWriter) throws IllegalArgumentException, IllegalAccessException, IOException {
		String dataString = "";
		int index = 1;
	    for (Field field : obj.getClass().getDeclaredFields()) {
	    		field.setAccessible(true);
	    		if(!field.getName().equalsIgnoreCase("dob")) {
		    		if(DPDoctorUtils.anyStringEmpty(dataString)) dataString = dataString + field.get(obj);
			    else dataString = dataString + "," + field.get(obj);
	    		}
//	    		Cell cell = fileWriter.createCell(index);
//		    cell.setCellValue(field.get(obj)+"");
	    }
	    dataString.replaceAll("\\[", "\"");
	    dataString.replaceAll("\\]", "\"");
	    fileWriter.append(dataString);
	    fileWriter.append(NEW_LINE_SEPARATOR);
	}

	@SuppressWarnings("resource")
	@Override
	public Boolean downloadClinicalItems(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		try {
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);
			
			Criteria criteria = new Criteria("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId).and("doctorId").is(doctorObjectId);
			
			
			List<MailAttachment> mailAttachments = new ArrayList<MailAttachment>();
			
			List<ComplaintCollection> complaintCollections = mongoTemplate.aggregate(Aggregation.newAggregation(
					Aggregation.match(criteria)), ComplaintCollection.class, ComplaintCollection.class).getMappedResults();
			
			FileWriter fileWriter = null;
			if(complaintCollections != null && !complaintCollections.isEmpty()) {
				fileWriter = new FileWriter("/home/ubuntu/Complaints.csv");
				fileWriter.append("Complaints");
			    fileWriter.append(NEW_LINE_SEPARATOR);
			    
				for(ComplaintCollection complaintCollection : complaintCollections) {
					fileWriter.append(complaintCollection.getComplaint());
				    fileWriter.append(NEW_LINE_SEPARATOR);
				}
				
				MailAttachment mailAttachment = new MailAttachment();
				mailAttachment.setAttachmentName("Complaints.csv");
				mailAttachment.setInputStream(new FileInputStream("/home/ubuntu/Complaints.csv"));
				mailAttachments.add(mailAttachment);
				fileWriter.close();
			}
		    
			List<ObservationCollection> observationCollections = mongoTemplate.aggregate(Aggregation.newAggregation(
					Aggregation.match(new Criteria("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId).and("doctorId").is(doctorObjectId))), ObservationCollection.class, ObservationCollection.class).getMappedResults();
			
			if(observationCollections != null && !observationCollections.isEmpty()) {
				fileWriter = new FileWriter("/home/ubuntu/Observations.csv");
				fileWriter.append("Observations");
			    fileWriter.append(NEW_LINE_SEPARATOR);
			    
				for(ObservationCollection observationCollection : observationCollections) {
					fileWriter.append(observationCollection.getObservation());
				    fileWriter.append(NEW_LINE_SEPARATOR);
				}
				
				MailAttachment mailAttachment = new MailAttachment();
				mailAttachment.setAttachmentName("Observations.csv");
				mailAttachment.setInputStream(new FileInputStream("/home/ubuntu/Observations.csv"));
				mailAttachments.add(mailAttachment);
				fileWriter.close();
			}
			
			List<InvestigationCollection> investigationCollections = mongoTemplate.aggregate(Aggregation.newAggregation(
					Aggregation.match(new Criteria("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId).and("doctorId").is(doctorObjectId))), InvestigationCollection.class, InvestigationCollection.class).getMappedResults();
			
			if(investigationCollections != null && !investigationCollections.isEmpty()) {
				fileWriter = new FileWriter("/home/ubuntu/Investigation.csv");
				fileWriter.append("Investigations");
			    fileWriter.append(NEW_LINE_SEPARATOR);
			    
				for(InvestigationCollection investigationCollection : investigationCollections) {
					fileWriter.append(investigationCollection.getInvestigation());
				    fileWriter.append(NEW_LINE_SEPARATOR);
				}
				
				MailAttachment mailAttachment = new MailAttachment();
				mailAttachment.setAttachmentName("Investigation.csv");
				mailAttachment.setInputStream(new FileInputStream("/home/ubuntu/Investigation.csv"));
				mailAttachments.add(mailAttachment);
				fileWriter.close();
			}
			
			List<DiagnosisCollection> diagnosisCollections = mongoTemplate.aggregate(Aggregation.newAggregation(
					Aggregation.match(new Criteria("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId).and("doctorId").is(doctorObjectId))), DiagnosisCollection.class, DiagnosisCollection.class).getMappedResults();
			
			if(diagnosisCollections != null && !diagnosisCollections.isEmpty()) {
				fileWriter = new FileWriter("/home/ubuntu/Diagnosis.csv");
				fileWriter.append("Diagnosis");
			    fileWriter.append(NEW_LINE_SEPARATOR);
			    
				for(DiagnosisCollection diagnosisCollection : diagnosisCollections) {
					fileWriter.append(diagnosisCollection.getDiagnosis());
				    fileWriter.append(NEW_LINE_SEPARATOR);
				}
				
				MailAttachment mailAttachment = new MailAttachment();
				mailAttachment.setAttachmentName("Diagnosis.csv");
				mailAttachment.setInputStream(new FileInputStream("/home/ubuntu/Diagnosis.csv"));
				mailAttachments.add(mailAttachment);
				fileWriter.close();
			}
			
			List<NotesCollection> notesCollections = mongoTemplate.aggregate(Aggregation.newAggregation(
					Aggregation.match(new Criteria("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId).and("doctorId").is(doctorObjectId))), NotesCollection.class, NotesCollection.class).getMappedResults();
			
			if(notesCollections != null && !notesCollections.isEmpty()) {
				fileWriter = new FileWriter("/home/ubuntu/Notes.csv");
				fileWriter.append("Notes");
			    fileWriter.append(NEW_LINE_SEPARATOR);
			    
				for(NotesCollection notesCollection : notesCollections) {
					fileWriter.append(notesCollection.getNote());
				    fileWriter.append(NEW_LINE_SEPARATOR);
				}
				
				MailAttachment mailAttachment = new MailAttachment();
				mailAttachment.setAttachmentName("Notes.csv");
				mailAttachment.setInputStream(new FileInputStream("/home/ubuntu/Notes.csv"));
				mailAttachments.add(mailAttachment);
				fileWriter.close();
			}
			
			
			response = mailService.sendEmailMultiAttach("neha.pateliya@healthcoco.com",
					"Dr. Ajit Mahant's clinical notes items data", "Please find attachments",
					mailAttachments);

		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error downloading clinical items");
			throw new BusinessException(ServiceError.Unknown, "Error downloading clinical items");
		}
		return response;
	}

	@Override
	public MailAttachment downloadPrescriptionData(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId) {
		MailAttachment mailAttachment = new MailAttachment();
		CSVWriter csvWriter = null;
		try {
			Criteria criteria = new Criteria("locationId").is(locationId).and("hospitalId").is(hospitalId).and("doctorId").is(doctorId);
			
			Aggregation aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria), Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
							Aggregation.unwind("user"),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$user").append("preserveNullAndEmptyArrays", true))),
							Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
							
							new CustomAggregationOperation(
									new Document("$unwind", new BasicDBObject("path", "$patient")
											.append("preserveNullAndEmptyArrays", true))),
							
							new CustomAggregationOperation(
									new Document("$redact",
											new BasicDBObject("$cond",
													new BasicDBObject("if", new BasicDBObject("$eq",
															Arrays.asList("$patient.locationId",
																	locationId)))
																			.append("then", "$$KEEP")
																			.append("else",
																					"$$PRUNE")))),
																
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$items").append("preserveNullAndEmptyArrays", true)
											.append("includeArrayIndex", "arrayIndex1"))),
							Aggregation.lookup("drug_cl", "items.drugId", "_id", "drug"),
							new CustomAggregationOperation(
									new Document("$unwind", new BasicDBObject("path", "$drug")
											.append("preserveNullAndEmptyArrays", true))),
							
							new CustomAggregationOperation(new Document("$project", new BasicDBObject("_id", "$_id")
									.append("doctorName", "$user.firstName")
									.append("patientName", "$patient.localPatientName")
									.append("patientId", "$patient.PID")
									.append("drugName", "$drug.drugName")
									.append("drugType", "$drug.drugType.type")
									.append("duration", new BasicDBObject("$concat", Arrays.asList("$items.duration.value"," ","$items.duration.durationUnit.unit")))
									.append("dosage", "$items.dosage")
									.append("explanation", "$explanation")
									.append("direction", "$items.direction.direction")
									.append("instructions", "$instructions")
									.append("diagnosticTests", "$diagnosticTests")
									.append("advice", "$advice")
									.append("date", new BasicDBObject("$dateToString", new BasicDBObject("format", "%Y-%m-%d").append("date","$createdTime"))))),
							
							new CustomAggregationOperation(new Document("$sort", new BasicDBObject("date", 1))));

			List<PrescriptionDownloadData> prescriptionDownloadDatas = mongoTemplate.aggregate(aggregation.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build()), PrescriptionCollection.class, PrescriptionDownloadData.class).getMappedResults();
			
			csvWriter = new CSVWriter(new FileWriter("/home/ubuntu/Prescriptions.csv"));
			
			
		    writeHeader(PrescriptionDownloadData.class, csvWriter, ComponentType.PRESCRIPTIONS, null);
		    
		    for (PrescriptionDownloadData prescriptionDownloadData : prescriptionDownloadDatas) {
		    		if(prescriptionDownloadData.getDiagnosticTests() != null && !prescriptionDownloadData.getDiagnosticTests().isEmpty()) {
		    			String test ="";
		    			for(TestAndRecordData tests : prescriptionDownloadData.getDiagnosticTests()) {
		    				DiagnosticTestCollection diagnosticTestCollection = diagnosticTestRepository.findById(tests.getTestId()).orElse(null);
		    				if(diagnosticTestCollection != null) {
		    					if(DPDoctorUtils.anyStringEmpty(test))test = "'"+diagnosticTestCollection.getTestName();
		    					else test = test +","+diagnosticTestCollection.getTestName();
		    				}
		    			}
		    			test = test + "'";
		    			prescriptionDownloadData.setTests(test);
		    		}
		    		writeData(prescriptionDownloadData, csvWriter, ComponentType.PRESCRIPTIONS, null);
		    }
		 
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error downloading prescription data");
			throw new BusinessException(ServiceError.Unknown, "Error downloading prescription data");
		}finally {
			try {
				if (csvWriter != null) {
					csvWriter.flush();
					csvWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return mailAttachment;
	}

	@Override
	public MailAttachment downloadAppointmentData(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId) {
		MailAttachment mailAttachment = new MailAttachment();
		CSVWriter csvWriter = null;
		try {
			Criteria criteria = new Criteria("locationId").is(locationId).and("hospitalId").is(hospitalId).and("doctorId").is(doctorId).and("type").is(AppointmentType.APPOINTMENT.getType());
			
			Aggregation aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria), Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
							Aggregation.unwind("user"),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$user").append("preserveNullAndEmptyArrays", true))),
							Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
							
							new CustomAggregationOperation(
									new Document("$unwind", new Document("path", "$patient")
											.append("preserveNullAndEmptyArrays", true))),
							new CustomAggregationOperation(
									new Document("$redact",
											new BasicDBObject("$cond",
													new BasicDBObject("if", new BasicDBObject("$eq",
															Arrays.asList("$patient.locationId",
																	"$locationId")))
																			.append("then", "$$KEEP")
																			.append("else",
																					"$$PRUNE")))),
							
							new CustomAggregationOperation(new Document("$project", new BasicDBObject("_id", "$_id")
									.append("doctorName", "$user.firstName")
									.append("patientName", "$patient.localPatientName")
									.append("patientId", "$patient.PID")
									.append("date", new BasicDBObject("$dateToString", new BasicDBObject("format", "%Y-%m-%d").append("date","$fromDate")))
									.append("startTime", "$time.fromTime")
									.append("endTime", "$time.toTime")
									.append("status", "$state")
									.append("explanation", "$explanation"))),
							
							new CustomAggregationOperation(new Document("$sort", new BasicDBObject("date", 1))));

			List<AppointmentDownloadData> appointmentDownloadDatas = mongoTemplate.aggregate(aggregation.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build()), AppointmentCollection.class, AppointmentDownloadData.class).getMappedResults();
			
			csvWriter = new CSVWriter(new FileWriter("/home/ubuntu/Appointments.csv"));
			
			
		    writeHeader(AppointmentDownloadData.class, csvWriter, ComponentType.APPOINTMENT, null);
		    
		    for (AppointmentDownloadData appointmentDownloadData : appointmentDownloadDatas) {
		    		writeData(appointmentDownloadData, csvWriter, ComponentType.APPOINTMENT, null);
		    }
		 
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error downloading appointment data");
			throw new BusinessException(ServiceError.Unknown, "Error downloading appointment data");
		}finally {
			try {
				if (csvWriter != null) {
					csvWriter.flush();
					csvWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return mailAttachment;
	}

	@Override
	public MailAttachment downloadTreatmentData(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId) {
		MailAttachment mailAttachment = new MailAttachment();
		CSVWriter csvWriter = null;
		try {
			Criteria criteria = new Criteria("locationId").is(locationId).and("hospitalId").is(hospitalId).and("doctorId").is(doctorId);
			
			Aggregation aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria), Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
							Aggregation.unwind("user"),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$user").append("preserveNullAndEmptyArrays", true))),
							Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
							
							new CustomAggregationOperation(
									new Document("$unwind", new BasicDBObject("path", "$patient")
											.append("preserveNullAndEmptyArrays", true))),
							new CustomAggregationOperation(
									new Document("$redact",
											new BasicDBObject("$cond",
													new BasicDBObject("if", new BasicDBObject("$eq",
															Arrays.asList("$patient.locationId",
																	"$locationId")))
																			.append("then", "$$KEEP")
																			.append("else",
																					"$$PRUNE")))),
																
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$treatments").append("preserveNullAndEmptyArrays", true)
											.append("includeArrayIndex", "arrayIndex1"))),
							Aggregation.lookup("treatment_services_cl", "treatments.treatmentServiceId", "_id","treatmentService"),
							new CustomAggregationOperation(
									new Document("$unwind", new BasicDBObject("path", "$treatmentService")
											.append("preserveNullAndEmptyArrays", true))),
							
							new CustomAggregationOperation(new Document("$project", new BasicDBObject("_id", "$_id")
									.append("doctorName", "$user.firstName")
									.append("patientName", "$patient.localPatientName")
									.append("patientId", "$patient.PID")
									.append("date", new BasicDBObject("$dateToString", new BasicDBObject("format", "%Y-%m-%d").append("date","$createdTime")))
									.append("treatmentName", "$treatmentService.name")
									.append("status", "$treatments.status")
									.append("cost", "$treatments.cost")
									.append("quantity", "$treatments.quantity.value")
									.append("quantityType","$treatments.quantity.type")
									.append("discount","$treatments.discount.value")
									.append("discountUnit","$treatments.discount.unit")
									.append("finalCost", "$treatments.finalCost")
									.append("note", "$treatments.note"))),
														
							new CustomAggregationOperation(new Document("$sort", new BasicDBObject("date", 1))));

			List<TreatmentDownloadData> treatmentDownloadDatas = mongoTemplate.aggregate(aggregation.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build()), PatientTreatmentCollection.class, TreatmentDownloadData.class).getMappedResults();
			
			csvWriter = new CSVWriter(new FileWriter("/home/ubuntu/Treatments.csv"));
			
			
		    writeHeader(TreatmentDownloadData.class, csvWriter, ComponentType.TREATMENT, null);
		    
		    for (TreatmentDownloadData treatmentDownloadData : treatmentDownloadDatas) {
		    		writeData(treatmentDownloadData, csvWriter, ComponentType.TREATMENT, null);
		    }
		 
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error downloading treatment data");
			throw new BusinessException(ServiceError.Unknown, "Error downloading treatment data");
		}finally {
			try {
				if (csvWriter != null) {
					csvWriter.flush();
					csvWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return mailAttachment;
	}

	@Override
	public MailAttachment downloadClinicalNotesData(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId) {
		MailAttachment mailAttachment = new MailAttachment();
		CSVWriter csvWriter = null;
		try {
			Criteria criteria = new Criteria("locationId").is(locationId).and("hospitalId").is(hospitalId).and("doctorId").is(doctorId);
			
			DynamicUICollection dynamicUICollection = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria)), DynamicUICollection.class, DynamicUICollection.class).getUniqueMappedResult();
			
			List<String> clinicalNotesPermission = null;
			if(dynamicUICollection != null && dynamicUICollection.getUiPermissions() != null && dynamicUICollection.getUiPermissions().getClinicalNotesPermissions()!=null && !dynamicUICollection.getUiPermissions().getClinicalNotesPermissions().isEmpty()){ 
				clinicalNotesPermission = dynamicUICollection.getUiPermissions().getClinicalNotesPermissions();
			}
			
			Aggregation aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria), Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
							Aggregation.unwind("user"),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$user").append("preserveNullAndEmptyArrays", true))),
							Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
							new CustomAggregationOperation(
									new Document("$unwind", new BasicDBObject("path", "$patient")
											.append("preserveNullAndEmptyArrays", true))),
							new CustomAggregationOperation(
									new Document("$redact",
											new BasicDBObject("$cond",
													new BasicDBObject("if", new BasicDBObject("$eq",
															Arrays.asList("$patient.locationId",
																	"$locationId")))
																			.append("then", "$$KEEP")
																			.append("else",
																					"$$PRUNE")))),
							new CustomAggregationOperation(new Document("$project", new BasicDBObject("_id", "$_id")
									.append("doctorName", "$user.firstName")
									.append("patientName", "$patient.localPatientName")
									.append("patientId", "$patient.PID")
									.append("date", new BasicDBObject("$dateToString", new BasicDBObject("format", "%Y-%m-%d").append("date","$createdTime")))
									.append("comments", "$comments")
									.append("pulse", "$vitalSigns.pulse")
									.append("temperature", "$vitalSigns.temperature")
									.append("breathing", "$vitalSigns.breathing")
									.append("bloodPressure", new BasicDBObject("$concat", Arrays.asList("$vitalSigns.bloodPressure.systolic","/","$vitalSigns.bloodPressure.diastolic")))
									.append("height", "$vitalSigns.height")
									.append("weight", "$vitalSigns.weight")
									.append("spo2", "$vitalSigns.spo2")
									.append("bmi", "$vitalSigns.bmi")
									.append("bsa", "$vitalSigns.bsa")
									.append("complaint", "$complaint")
									.append("observation", "$observation")
									.append("investigation","$investigation")
									.append("diagnosis","$diagnosis")
									.append("note","$note")
									.append("provisionalDiagnosis", "$provisionalDiagnosis")
									.append("generalExam", "$generalExam")
									.append("systemExam", "$systemExam")
									.append("presentComplaint", "$presentComplaint")
									.append("presentComplaintHistory", "$presentComplaintHistory")
									.append("menstrualHistory", "$menstrualHistory")
									.append("obstetricHistory","$obstetricHistory")
									.append("indicationOfUSG","$indicationOfUSG")
									.append("pv","$pv")
									.append("pa", "$pa")
									.append("ps", "$ps")
									.append("ecgDetails", "$ecgDetails")
									.append("xRayDetails", "$xRayDetails")
									.append("echo", "$echo")
									.append("holter", "$holter")
									.append("pcNose","$pcNose")
									.append("pcOralCavity","$pcOralCavity")
									.append("pcThroat","$pcThroat")
									.append("pcEars", "$pcEars")
									.append("noseExam", "$noseExam")
									.append("oralCavityThroatExam","$oralCavityThroatExam")
									.append("indirectLarygoscopyExam","$indirectLarygoscopyExam")
									.append("neckExam", "$neckExam")
									.append("earsExam", "$earsExam"))),
							new CustomAggregationOperation(new Document("$sort", new BasicDBObject("date", 1))));

			List<ClinicalNotesDownloadData> clinicalNotesDownloadDatas = mongoTemplate.aggregate(aggregation.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build()), ClinicalNotesCollection.class, ClinicalNotesDownloadData.class).getMappedResults();
			
			 csvWriter = new CSVWriter(new FileWriter("/home/ubuntu/ClinicalNotes.csv")); 
			 
			List<String> clinicalNotesFields = new ArrayList<String>();
			clinicalNotesFields.add("doctorName");
			clinicalNotesFields.add("patientName");
			clinicalNotesFields.add("patientId");
			clinicalNotesFields.add("date");
			clinicalNotesFields.add("comments");
			if(clinicalNotesPermission != null && !clinicalNotesPermission.isEmpty()) {
				if(clinicalNotesPermission.contains(ClinicalNotesPermissionEnum.VITAL_SIGNS.getPermissions())) {
					clinicalNotesFields.add("pulse");clinicalNotesFields.add("temperature");clinicalNotesFields.add("breathing");clinicalNotesFields.add("bloodPressure");
					clinicalNotesFields.add("height"); clinicalNotesFields.add("weight"); clinicalNotesFields.add("spo2"); clinicalNotesFields.add("bmi"); clinicalNotesFields.add("bsa");
				}
				if(clinicalNotesPermission.contains(ClinicalNotesPermissionEnum.COMPLAINT.getPermissions()))clinicalNotesFields.add("complaint");
				if(clinicalNotesPermission.contains(ClinicalNotesPermissionEnum.OBSERVATION.getPermissions()))clinicalNotesFields.add("observation");
				if(clinicalNotesPermission.contains(ClinicalNotesPermissionEnum.INVESTIGATIONS.getPermissions()))clinicalNotesFields.add("investigation");
				if(clinicalNotesPermission.contains(ClinicalNotesPermissionEnum.DIAGNOSIS.getPermissions()))clinicalNotesFields.add("diagnosis");
				if(clinicalNotesPermission.contains(ClinicalNotesPermissionEnum.NOTES.getPermissions()))clinicalNotesFields.add("note");
				if(clinicalNotesPermission.contains(ClinicalNotesPermissionEnum.PROVISIONAL_DIAGNOSIS.getPermissions()))clinicalNotesFields.add("provisionalDiagnosis");
				if(clinicalNotesPermission.contains(ClinicalNotesPermissionEnum.GENERAL_EXAMINATION.getPermissions()))clinicalNotesFields.add("generalExam");
				if(clinicalNotesPermission.contains(ClinicalNotesPermissionEnum.SYSTEMIC_EXAMINATION.getPermissions()))clinicalNotesFields.add("systemExam");
				if(clinicalNotesPermission.contains(ClinicalNotesPermissionEnum.PRESENT_COMPLAINT.getPermissions()))clinicalNotesFields.add("presentComplaint");
				if(clinicalNotesPermission.contains(ClinicalNotesPermissionEnum.HISTORY_OF_PRESENT_COMPLAINT.getPermissions()))clinicalNotesFields.add("presentComplaintHistory");
				if(clinicalNotesPermission.contains(ClinicalNotesPermissionEnum.MENSTRUAL_HISTORY.getPermissions()))clinicalNotesFields.add("menstrualHistory");
				if(clinicalNotesPermission.contains(ClinicalNotesPermissionEnum.OBSTETRIC_HISTORY.getPermissions()))clinicalNotesFields.add("obstetricHistory");			
			}else{
				clinicalNotesFields.add("pulse");clinicalNotesFields.add("temperature");clinicalNotesFields.add("breathing");clinicalNotesFields.add("bloodPressure");
				clinicalNotesFields.add("height"); clinicalNotesFields.add("weight"); clinicalNotesFields.add("spo2"); clinicalNotesFields.add("bmi"); clinicalNotesFields.add("bsa");	
				clinicalNotesFields.add("complaint");
				clinicalNotesFields.add("observation");
				clinicalNotesFields.add("investigation");
				clinicalNotesFields.add("diagnosis");
				clinicalNotesFields.add("note");
			}
		    writeHeader(ClinicalNotesDownloadData.class, csvWriter, ComponentType.CLINICAL_NOTES, clinicalNotesFields);
		    
		    for (ClinicalNotesDownloadData clinicalNotesDownloadData : clinicalNotesDownloadDatas) {
		    		writeData(clinicalNotesDownloadData, csvWriter, ComponentType.CLINICAL_NOTES, clinicalNotesFields);
		    }
		 
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error downloading clinical notes data");
			throw new BusinessException(ServiceError.Unknown, "Error downloading clinical notes data");
		}finally {
			try {
				if (csvWriter != null) {
					csvWriter.flush();
					csvWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return mailAttachment;
	}

	private void writeHeader(Class classOfObject, CSVWriter csvWriter, ComponentType componentType, List<String> fieldsName) {
		List<String> headerString = new ArrayList<String>();
	    
		switch (componentType) {
			case PATIENT:
				for (Field field : classOfObject.getDeclaredFields()) {
					field.setAccessible(true);
		        
					if(!field.getName().equalsIgnoreCase("dob") && !field.getName().equalsIgnoreCase("registrationDate")) {
						headerString.add(field.getName());
					}
				}
				break;

			case PRESCRIPTIONS:
				for (Field field : classOfObject.getDeclaredFields()) {
					field.setAccessible(true);
		        
					if(!field.getName().equalsIgnoreCase("diagnosticTests")) {
						headerString.add(field.getName());
					}
				}
				break;	
			case CLINICAL_NOTES:
				for (String field : fieldsName) {
	    			headerString.add(field);
				}
				break;	
			default:
				for (Field field : classOfObject.getDeclaredFields()) {
					field.setAccessible(true);
					headerString.add(field.getName());
				}
				break;
		}
		
	    csvWriter.writeNext(headerString.toArray(new String[0]));
	}

	private void writeData(Object obj, CSVWriter writer, ComponentType componentType, List<String> fieldsName) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		List<String> dataString = new ArrayList<String>();
		
		switch (componentType) {
			case PATIENT:
				for (Field field : obj.getClass().getDeclaredFields()) {
		    		field.setAccessible(true);
		    		if(!field.getName().equalsIgnoreCase("dob") && !field.getName().equalsIgnoreCase("registrationDate")) {
		    			dataString.add((field.get(obj) != null ? (String)(field.get(obj)+"") : ""));
		    		}
		    }
				break;
	
			case PRESCRIPTIONS:
				for (Field field : obj.getClass().getDeclaredFields()) {
		    		field.setAccessible(true);
		    		if(!field.getName().equalsIgnoreCase("diagnosticTests")) {
		    			dataString.add((field.get(obj) != null ? (String)(field.get(obj)+"") : ""));
		    		}
		    }
				break;	
			case CLINICAL_NOTES:
				for (Field field : obj.getClass().getDeclaredFields()) {
		    		field.setAccessible(true);
		    		if(fieldsName.contains(field.getName())) {
		    			dataString.add((field.get(obj) != null ? (String)(field.get(obj)) : ""));
		    		}
		    }
				break;	
			default:
				for (Field field : obj.getClass().getDeclaredFields()) {
		    		field.setAccessible(true);
		    		dataString.add((field.get(obj) != null ? (String)(field.get(obj)+"") : ""));
		    	}
				break;
		}
	   		writer.writeNext(dataString.toArray(new String [0]));
		
	}

	@Override
	public MailAttachment downloadInvoicesData(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId) {
		MailAttachment mailAttachment = new MailAttachment();
		CSVWriter csvWriter = null;
		try {
			Criteria criteria = new Criteria("locationId").is(locationId).and("hospitalId").is(hospitalId).and("doctorId").is(doctorId);
			
			Aggregation aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria), Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
							Aggregation.unwind("user"),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$user").append("preserveNullAndEmptyArrays", true))),
							Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
							new CustomAggregationOperation(
									new Document("$unwind", new BasicDBObject("path", "$patient")
											.append("preserveNullAndEmptyArrays", true))),
							new CustomAggregationOperation(
									new Document("$redact",
											new BasicDBObject("$cond",
													new BasicDBObject("if", new BasicDBObject("$eq",
															Arrays.asList("$patient.locationId",
																	"$locationId")))
																			.append("then", "$$KEEP")
																			.append("else",
																					"$$PRUNE")))),
							
																
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$invoiceItems").append("preserveNullAndEmptyArrays", true)
											.append("includeArrayIndex", "arrayIndex1"))),
							
							new CustomAggregationOperation(new Document("$project", new BasicDBObject("_id", "$_id")
									.append("doctorName", "$user.firstName")
									.append("patientName", "$patient.localPatientName")
									.append("patientId", "$patient.PID")
									.append("date", new BasicDBObject("$dateToString", new BasicDBObject("format", "%Y-%m-%d").append("date","$invoiceDate")))
									.append("invoiceId", "$uniqueInvoiceId")
									.append("name", new BasicDBObject("$concat",Arrays.asList("'","$invoiceItems.name","'")))
									.append("cost", "$invoiceItems.cost")
									.append("quantity", "$invoiceItems.quantity.value")
									.append("quantityType","$invoiceItems.quantity.type")
									.append("discount","$invoiceItems.discount.value")
									.append("discountUnit","$invoiceItems.discount.unit")
									.append("tax","$invoiceItems.tax.value")
									.append("taxUnit","$invoiceItems.tax.unit")
									.append("finalCost", "$invoiceItems.finalCost")
									.append("note", "$invoiceItems.note"))),
							
							new CustomAggregationOperation(new Document("$sort", new BasicDBObject("date", 1))));

			List<InvoiceDownloadData> treatmentDownloadDatas = mongoTemplate.aggregate(aggregation.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build()), DoctorPatientInvoiceCollection.class, InvoiceDownloadData.class).getMappedResults();
			
			csvWriter = new CSVWriter(new FileWriter("/home/ubuntu/Invoices.csv"));
			
			
		    writeHeader(InvoiceDownloadData.class, csvWriter, ComponentType.INVOICE, null);
		    
		    for (InvoiceDownloadData treatmentDownloadData : treatmentDownloadDatas) {
		    		writeData(treatmentDownloadData, csvWriter, ComponentType.INVOICE, null);
		    }
		 
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error downloading invoice data");
			throw new BusinessException(ServiceError.Unknown, "Error downloading invoice data");
		}finally {
			try {
				if (csvWriter != null) {
					csvWriter.flush();
					csvWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return mailAttachment;
	}

	@Override
	public MailAttachment downloadPaymentsData(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId) {
		MailAttachment mailAttachment = new MailAttachment();
		CSVWriter csvWriter = null;
		try {
			Criteria criteria = new Criteria("locationId").is(locationId).and("hospitalId").is(hospitalId).and("doctorId").is(doctorId);
			
			Aggregation aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria), Aggregation.lookup("user_cl", "doctorId", "_id", "user"),
							Aggregation.unwind("user"),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$user").append("preserveNullAndEmptyArrays", true))),
							Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
							new CustomAggregationOperation(
									new Document("$unwind", new BasicDBObject("path", "$patient")
											.append("preserveNullAndEmptyArrays", true))),
							new CustomAggregationOperation(
									new Document("$redact",
											new BasicDBObject("$cond",
													new BasicDBObject("if", new BasicDBObject("$eq",
															Arrays.asList("$patient.locationId",
																	locationId)))
																			.append("then", "$$KEEP")
																			.append("else",
																					"$$PRUNE")))),									
							
							new CustomAggregationOperation(new Document("$project", new BasicDBObject("_id", "$_id")
									.append("doctorName", "$user.firstName")
									.append("patientName", "$patient.localPatientName")
									.append("patientId", "$patient.PID")
									.append("date", new BasicDBObject("$dateToString", new BasicDBObject("format", "%Y-%m-%d").append("date","$createdTime")))
									.append("receiptId", "$uniqueReceiptId")
									.append("invoiceId", "$uniqueInvoiceId")
									.append("modeOfPayment", "$modeOfPayment")
									.append("amountPaid", "$amountPaid"))),
							
							new CustomAggregationOperation(new Document("$sort", new BasicDBObject("date", 1))));

			List<ReceiptDownloadData> receiptDownloadDatas = mongoTemplate.aggregate(aggregation.withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build()), DoctorPatientReceiptCollection.class, ReceiptDownloadData.class).getMappedResults();
			
			csvWriter = new CSVWriter(new FileWriter("/home/ubuntu/Payments.csv"));
						

			writeHeader(ReceiptDownloadData.class, csvWriter, ComponentType.RECEIPT, null);
		    
		    for (ReceiptDownloadData receiptDownloadData : receiptDownloadDatas) {
		    		writeData(receiptDownloadData, csvWriter, ComponentType.RECEIPT, null);
		    }
		 
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error downloading receipt data");
			throw new BusinessException(ServiceError.Unknown, "Error downloading receipt data");
		}finally {
			try {
				if (csvWriter != null) {
					csvWriter.flush();
					csvWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return mailAttachment;
	}

	@Override
	public Boolean update(String doctorId, String locationId, String hospitalId) {
		Boolean response = false;
		try {
			List<DoctorPatientReceiptCollection> doctorPatientReceiptCollections = mongoTemplate.aggregate(Aggregation.newAggregation(
					Aggregation.match(new Criteria("doctorId").is(new ObjectId(doctorId)).and("locationId").is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId))
							.and("uniqueReceiptId").is(null)),
					Aggregation.sort(new Sort(Direction.ASC, "createdTime"))), 
					DoctorPatientReceiptCollection.class, DoctorPatientReceiptCollection.class).getMappedResults();
			if(doctorPatientReceiptCollections != null) {
				int i = 3;
				for(DoctorPatientReceiptCollection doctorPatientReceiptCollection : doctorPatientReceiptCollections) {
					
					doctorPatientReceiptCollection.setUniqueReceiptId("RC"+i);
					i = i +1;
					doctorPatientReceiptCollection = doctorPatientReceiptRepository.save(doctorPatientReceiptCollection);
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error updating data");
//			throw new BusinessException(ServiceError.Unknown, "Error downloading receipt data");
		}
		return response;
	}
	
}
