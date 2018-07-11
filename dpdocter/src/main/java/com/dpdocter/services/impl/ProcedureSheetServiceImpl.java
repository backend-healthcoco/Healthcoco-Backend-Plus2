package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.LabReports;
import com.dpdocter.beans.PatientShortCard;
import com.dpdocter.beans.ProcedureSheet;
import com.dpdocter.collections.DentalImagingCollection;
import com.dpdocter.collections.LabReportsCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.ProcedureSheetCollection;
import com.dpdocter.collections.ProcedureSheetStructureCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.ProcedureSheetRepository;
import com.dpdocter.repository.ProcedureSheetStructureRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AddEditProcedureSheetRequest;
import com.dpdocter.request.AddEditProcedureSheetStructureRequest;
import com.dpdocter.request.LabReportsAddRequest;
import com.dpdocter.response.DentalImagingResponse;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.ProcedureSheetResponse;
import com.dpdocter.response.ProcedureSheetStructureResponse;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.ProcedureSheetService;
import com.mongodb.BasicDBObject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.DPDoctorUtils;

@Service
public class ProcedureSheetServiceImpl implements ProcedureSheetService{

	@Autowired
	private ProcedureSheetRepository procedureSheetRepository;
	
	@Autowired
	private ProcedureSheetStructureRepository procedureSheetStructureRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PatientRepository patientRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private FileManager fileManager;
	
	@Override
	@Transactional
	public ProcedureSheetResponse addEditProcedureSheet(AddEditProcedureSheetRequest request)
	{
		ProcedureSheetResponse response = null;
		ProcedureSheetCollection procedureSheetCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				procedureSheetCollection = procedureSheetRepository.findOne(new ObjectId(request.getId()));
			} else {
				procedureSheetCollection = new ProcedureSheetCollection();
				procedureSheetCollection.setCreatedTime(new Date());
				UserCollection userCollection = userRepository.findOne(new ObjectId(request.getDoctorId()));
				procedureSheetCollection.setCreatedBy(userCollection.getFirstName());
			}
			BeanUtil.map(request, procedureSheetCollection);
			procedureSheetCollection = procedureSheetRepository.save(procedureSheetCollection);
			if (procedureSheetCollection != null) {
				response = new ProcedureSheetResponse();
				BeanUtil.map(procedureSheetCollection, response);
				PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(
						procedureSheetCollection.getPatientId(), procedureSheetCollection.getDoctorId(),
						procedureSheetCollection.getLocationId(), procedureSheetCollection.getHospitalId());
				if (patientCollection != null) {
					PatientShortCard patientShortCard = new PatientShortCard();
					BeanUtil.map(patientCollection, patientShortCard);
					response.setPatient(patientShortCard);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}
	
	@Override
	@Transactional
	public ProcedureSheetResponse getProcedureSheet(String id)
	{
		ProcedureSheetResponse response = null;
		ProcedureSheetCollection procedureSheetCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(id)) {
				procedureSheetCollection = procedureSheetRepository.findOne(new ObjectId(id));
			} else {
				throw new BusinessException(ServiceError.NoRecord , "Record not found");
			}
			if (procedureSheetCollection != null) {
				response = new ProcedureSheetResponse();
				BeanUtil.map(procedureSheetCollection, response);
				PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(
						procedureSheetCollection.getPatientId(), procedureSheetCollection.getDoctorId(),
						procedureSheetCollection.getLocationId(), procedureSheetCollection.getHospitalId());
				if (patientCollection != null) {
					PatientShortCard patientShortCard = new PatientShortCard();
					BeanUtil.map(patientCollection, patientShortCard);
					response.setPatient(patientShortCard);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}
	
	@Override
	@Transactional
	public List<ProcedureSheetResponse> getProcedureSheetList(String doctorId, String hospitalId, String locationId,
			String patientId, String searchTerm, Long from, Long to, Boolean discarded , int page , int size , String type) {
		List<ProcedureSheetResponse> responses = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria.and("patientId").is(new ObjectId(patientId));
			}
			if (to != null) {
				criteria.and("updatedTime").gte(new Date(from)).lte(DPDoctorUtils.getEndTime(new Date(to)));
			} else {
				criteria.and("updatedTime").gte(new Date(from));
			}
			if(discarded != null) {
				criteria.and("discarded").is(discarded);
			}
			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria.and("type").is(new ObjectId(type));
			}
			
			if (size > 0)
				aggregation = Aggregation.newAggregation(
						Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(
						Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			
			AggregationResults<ProcedureSheetResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					ProcedureSheetCollection.class, ProcedureSheetResponse.class);
			responses = aggregationResults.getMappedResults();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return responses;
		
	}
	
	@Override
	@Transactional
	public ProcedureSheetResponse discardProcedureSheet(String id , Boolean discarded)
	{
		ProcedureSheetResponse response = null;
		ProcedureSheetCollection procedureSheetCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(id)) {
				procedureSheetCollection = procedureSheetRepository.findOne(new ObjectId(id));
				procedureSheetCollection.setDiscarded(discarded);
				procedureSheetCollection = procedureSheetRepository.save(procedureSheetCollection);
			} else {
				throw new BusinessException(ServiceError.NoRecord , "Record not found");
			}
			if (procedureSheetCollection != null) {
				response = new ProcedureSheetResponse();
				BeanUtil.map(procedureSheetCollection, response);
				PatientCollection patientCollection = patientRepository.findByUserIdDoctorIdLocationIdAndHospitalId(
						procedureSheetCollection.getPatientId(), procedureSheetCollection.getDoctorId(),
						procedureSheetCollection.getLocationId(), procedureSheetCollection.getHospitalId());
				if (patientCollection != null) {
					PatientShortCard patientShortCard = new PatientShortCard();
					BeanUtil.map(patientCollection, patientShortCard);
					response.setPatient(patientShortCard);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}
	

	@Override
	@Transactional
	public ProcedureSheetStructureResponse addEditProcedureSheetStructure(AddEditProcedureSheetStructureRequest request)
	{
		ProcedureSheetStructureResponse response = null;
		ProcedureSheetStructureCollection procedureSheetStructureCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				procedureSheetStructureCollection = procedureSheetStructureRepository.findOne(new ObjectId(request.getId()));
			} else {
				procedureSheetStructureCollection = new ProcedureSheetStructureCollection();
				procedureSheetStructureCollection.setCreatedTime(new Date());
				UserCollection userCollection = userRepository.findOne(new ObjectId(request.getDoctorId()));
				procedureSheetStructureCollection.setCreatedBy(userCollection.getFirstName());
			}
			BeanUtil.map(request, procedureSheetStructureCollection);
			procedureSheetStructureCollection = procedureSheetStructureRepository.save(procedureSheetStructureCollection);
			if (procedureSheetStructureCollection != null) {
				response = new ProcedureSheetStructureResponse();
				BeanUtil.map(procedureSheetStructureCollection, response);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}
	
	@Override
	@Transactional
	public ImageURLResponse addDiagrams(FormDataBodyPart file) {
		ImageURLResponse imageURLResponse = null;
		try {
			if (file != null) {
				String path = "procedure-sheet";
				FormDataContentDisposition fileDetail = file.getFormDataContentDisposition();
				String fileExtension = FilenameUtils.getExtension(fileDetail.getFileName());
				String fileName = fileDetail.getFileName().replaceFirst("." + fileExtension, "");
				String recordPath = path + File.separator + fileName + System.currentTimeMillis() + "." + fileExtension;
				imageURLResponse = fileManager.saveImage(file, recordPath, true);
			}
			

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return imageURLResponse;
	}

	
	@Override
	@Transactional
	public ProcedureSheetStructureResponse getProcedureSheetStructure(String id)
	{
		ProcedureSheetStructureResponse response = null;
		ProcedureSheetStructureCollection procedureSheetStructureCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(id)) {
				procedureSheetStructureCollection = procedureSheetStructureRepository.findOne(new ObjectId(id));
			} else {
				throw new BusinessException(ServiceError.NoRecord , "Record not found");
			}
			if (procedureSheetStructureCollection != null) {
				response = new ProcedureSheetStructureResponse();
				BeanUtil.map(procedureSheetStructureCollection, response);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}
	
	
	@Override
	@Transactional
	public ProcedureSheetStructureResponse discardProcedureSheetStructure(String id , Boolean discarded)
	{
		ProcedureSheetStructureResponse response = null;
		ProcedureSheetStructureCollection procedureSheetStructureCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(id)) {
				procedureSheetStructureCollection = procedureSheetStructureRepository.findOne(new ObjectId(id));
				procedureSheetStructureCollection.setDiscarded(discarded);
				procedureSheetStructureCollection = procedureSheetStructureRepository.save(procedureSheetStructureCollection);
			} else {
				throw new BusinessException(ServiceError.NoRecord , "Record not found");
			}
			if (procedureSheetStructureCollection != null) {
				response = new ProcedureSheetStructureResponse();
				BeanUtil.map(procedureSheetStructureCollection, response);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}
	
	@Override
	@Transactional
	public List<ProcedureSheetStructureResponse> getProcedureSheetStructureList(String doctorId, String hospitalId, String locationId,
			 String searchTerm, Long from, Long to, Boolean discarded , int page , int size ,String type) {
		List<ProcedureSheetStructureResponse> responses = null;
		try {
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").is(new ObjectId(hospitalId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").is(new ObjectId(locationId));
			}
			if (to != null) {
				criteria.and("updatedTime").gte(new Date(from)).lte(DPDoctorUtils.getEndTime(new Date(to)));
			} else {
				criteria.and("updatedTime").gte(new Date(from));
			}
			if(discarded != null) {
				criteria.and("discarded").is(discarded);
			}
			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria.and("type").is(new ObjectId(type));
			}
			if (size > 0)
				aggregation = Aggregation.newAggregation(
						Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(
						Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			
			AggregationResults<ProcedureSheetStructureResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					ProcedureSheetStructureCollection.class, ProcedureSheetStructureResponse.class);
			responses = aggregationResults.getMappedResults();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return responses;
		
	}
	
}
