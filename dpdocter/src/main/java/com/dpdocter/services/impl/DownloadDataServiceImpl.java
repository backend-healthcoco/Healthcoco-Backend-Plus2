package com.dpdocter.services.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.PatientDownloadData;
import com.dpdocter.collections.DownloadDataRequestCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DownloadDataRequestRepository;
import com.dpdocter.request.ExportRequest;
import com.dpdocter.services.DownloadDateServices;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class DownloadDataServiceImpl implements DownloadDateServices{

	private static Logger logger = Logger.getLogger(DownloadDataServiceImpl.class.getName());
	
	@Autowired
	DownloadDataRequestRepository downloadDataRequestRepository;
	
	private static final String COMMA_DELIMITER = ",";

	private static final String NEW_LINE_SEPARATOR = "\n";

//	@Value(value = "${patients.data.file}")
	private String PATIENTS_DATA_FILE;
	
	@Autowired
	MongoTemplate mongoTemplate;
	
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
			downloadDataRequestCollection.setIsMailSend(true);
			downloadDataRequestCollection.setMailSendTime(new Date());
//			downloadDataRequestCollection = downloadDataRequestRepository.save(downloadDataRequestCollection);
		}
		
	}

	private MailAttachment generatePatientData(ObjectId doctorId, ObjectId locationId, ObjectId hospitalId){
		MailAttachment mailAttachment = new MailAttachment();
//		Workbook workbook = new HSSFWorkbook();
		FileWriter fileWriter = null;
		try {
			Criteria criteria = new Criteria("locationId").is(locationId).and("hospitalId").is(hospitalId).and("doctorId").is(doctorId);
			
			Aggregation aggregation = Aggregation
					.newAggregation(Aggregation.match(criteria), Aggregation.lookup("user_cl", "userId", "_id", "user"),
							Aggregation.unwind("user"),
							new CustomAggregationOperation(new BasicDBObject("$unwind",
									new BasicDBObject("path", "$user").append("preserveNullAndEmptyArrays", true))),
							Aggregation.lookup("patient_group_cl", "userId", "patientId", "patientGroupCollections"),
							Aggregation.match(new Criteria().orOperator(
									new Criteria("patientGroupCollections.discarded").is(false),
									new Criteria("patientGroupCollections").size(0))),
							
							new CustomAggregationOperation(
									new BasicDBObject("$unwind", new BasicDBObject("path", "$patientGroupCollections")
											.append("preserveNullAndEmptyArrays", true))),
									
							
							Aggregation.lookup("group_cl", "patientGroupCollections.groupId", "_id", "groups"),
							new CustomAggregationOperation(
									new BasicDBObject("$unwind", new BasicDBObject("path", "$groups")
											.append("preserveNullAndEmptyArrays", true))),
							
							
							Aggregation.lookup("referrences_cl", "referredBy", "_id", "reference"),
							new CustomAggregationOperation(
									new BasicDBObject("$unwind", new BasicDBObject("path", "$reference")
											.append("preserveNullAndEmptyArrays", true))),
							
							new CustomAggregationOperation(new BasicDBObject("$project", new BasicDBObject("_id", "$_id")
									.append("PID", "$PID")
									.append("localPatientName", "$localPatientName")
									.append("mobileNumber", "$user.mobileNumber")
									.append("emailAddress", "$emailAddress")
									.append("secMobile", "$secMobile")
									.append("gender", "$gender")
									.append("country", "$country")
									.append("city", "$city")
									.append("state", "$state")
									.append("postalCode", "$postalCode")
									.append("locality", "$locality")
									.append("landmarkDetails", "$landmarkDetails")
									.append("streetAddress", "$streetAddress")
									.append("adhaarId", "$adhaarId")
									.append("panCardNumber", "$panCardNumber")
									.append("drivingLicenseId", "$drivingLicenseId")
									.append("insuranceId", "$insuranceId")
									.append("dob", "$dob")
									.append("bloodGroup", "$bloodGroup")
									.append("referredBy", "$reference.reference")
									.append("groups", "$groups.name"))),
							
							new CustomAggregationOperation(new BasicDBObject("$group", new BasicDBObject("_id", "$_id")
									.append("PID", new BasicDBObject("$first","$PID"))
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
									.append("groups", new BasicDBObject("$push","$groups"))))
							);

			List<PatientDownloadData> patientDownloadDatas = mongoTemplate.aggregate(aggregation, PatientCollection.class, PatientDownloadData.class).getMappedResults();
			
			
//		    Sheet sheet = workbook.createSheet();
			fileWriter = new FileWriter("/Users/nehakariya/Patients.csv");
			
			
		    int rowCount = 0;
//		    Row headerRow = sheet.createRow(++rowCount);
		    writeHeader(PatientDownloadData.class, fileWriter);
		    
		    for (PatientDownloadData patientDownloadData : patientDownloadDatas) {
		    		if(patientDownloadData.getDob() != null) {
		    			patientDownloadData.setDateOfBirth(patientDownloadData.getDob().getDays() +"/"+ patientDownloadData.getDob().getMonths()+"/" + patientDownloadData.getDob().getYears() +"/");
		    			patientDownloadData.setAge(patientDownloadData.getDob().getAge().getYears()+"");
		    		}
//		        Row row = sheet.createRow(++rowCount);
		        writeData(patientDownloadData, fileWriter);
		    }
		 
//		    File patientFile = new File("/Users/nehakariya/Patients.xlsx");
//		    		patientFile.createNewFile();
		    
//		    FileOutputStream outputStream = new FileOutputStream(patientFile);
//		    workbook.write(outputStream);
		    
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e + "Error downloading patient data");
			throw new BusinessException(ServiceError.Unknown, "Error downloading patient data");
		}finally {
			try {
				if (fileWriter != null) {
					fileWriter.flush();
					fileWriter.close();
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
		        else headerString = headerString + COMMA_DELIMITER + field.getName();
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
			    else dataString = dataString + COMMA_DELIMITER + field.get(obj);
	    		}
//	    		Cell cell = fileWriter.createCell(index);
//		    cell.setCellValue(field.get(obj)+"");
	    }
	    dataString.replaceAll("\\[", "\"");
	    dataString.replaceAll("\\]", "\"");
	    fileWriter.append(dataString);
	    fileWriter.append(NEW_LINE_SEPARATOR);
	}
	
}
