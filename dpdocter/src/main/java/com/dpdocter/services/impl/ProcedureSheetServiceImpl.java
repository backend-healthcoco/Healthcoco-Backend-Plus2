package com.dpdocter.services.impl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.DefaultPrintSettings;
import com.dpdocter.beans.PatientShortCard;
import com.dpdocter.beans.ProcedureConsentForm;
import com.dpdocter.beans.ProcedureConsentFormFields;
import com.dpdocter.beans.ProcedureConsentFormStructure;
import com.dpdocter.beans.ProcedureSheetField;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.ProcedureSheetCollection;
import com.dpdocter.collections.ProcedureSheetStructureCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.ProcedureSheetRepository;
import com.dpdocter.repository.ProcedureSheetStructureRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.AddEditProcedureSheetRequest;
import com.dpdocter.request.AddEditProcedureSheetStructureRequest;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.response.ProcedureSheetResponse;
import com.dpdocter.response.ProcedureSheetStructureResponse;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.ProcedureSheetService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.DPDoctorUtils;

@Service
public class ProcedureSheetServiceImpl implements ProcedureSheetService {

	private static Logger logger = Logger.getLogger(ProcedureSheetServiceImpl.class.getName());

	
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

	@Autowired
	private PrintSettingsRepository printSettingsRepository;

	@Autowired
	private PatientVisitService patientVisitService;

	@Autowired
	private JasperReportService jasperReportService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${jasper.print.procedure.sheet.fileName}")
	private String procedureSheetJasper;

	@Override
	@Transactional
	public ProcedureSheetResponse addEditProcedureSheet(AddEditProcedureSheetRequest request) {
		ProcedureSheetResponse response = null;
		ProcedureSheetCollection procedureSheetCollection = null;
		ProcedureConsentForm procedureConsentForm = null;
		List<Map<String, ProcedureSheetField>> procedureSheetFields = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				procedureSheetCollection = procedureSheetRepository.findById(new ObjectId(request.getId())).orElse(null);
			} else {
				procedureSheetCollection = new ProcedureSheetCollection();
				procedureSheetCollection.setCreatedTime(new Date());
				UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
				procedureSheetCollection.setCreatedBy(userCollection.getFirstName());
			}

			if (request.getProcedureConsentForm() != null) {
				procedureConsentForm = new ProcedureConsentForm();
				procedureConsentForm.setHeaderFields(request.getProcedureConsentForm().getHeaderFields());
				procedureConsentForm.setFooterFields(request.getProcedureConsentForm().getFooterFields());
				procedureConsentForm.setBody(request.getProcedureConsentForm().getBody());
			}

			procedureSheetFields = request.getProcedureSheetFields();
			request.setProcedureSheetFields(null);
			request.setProcedureConsentForm(null);
			BeanUtil.map(request, procedureSheetCollection);
			if (request.getDiagrams() != null && !request.getDiagrams().isEmpty()) {
				procedureSheetCollection.setDiagrams(new ArrayList<ImageURLResponse>());
				procedureSheetCollection.setDiagrams(request.getDiagrams());
			} else {
				procedureSheetCollection.setDiagrams(null);
			}

			procedureSheetCollection.setProcedureSheetFields(procedureSheetFields);
			procedureSheetCollection.setDiagrams(request.getDiagrams());
			procedureSheetCollection.setProcedureConsentForm(procedureConsentForm);
			procedureSheetCollection = procedureSheetRepository.save(procedureSheetCollection);
			if (procedureSheetCollection != null) {
				response = new ProcedureSheetResponse();
				procedureSheetFields = procedureSheetCollection.getProcedureSheetFields();
				procedureSheetCollection.setProcedureSheetFields(null);
				if (procedureSheetCollection.getProcedureConsentForm() != null) {
					procedureConsentForm = new ProcedureConsentForm();
					procedureConsentForm
							.setHeaderFields(procedureSheetCollection.getProcedureConsentForm().getHeaderFields());
					procedureConsentForm
							.setFooterFields(procedureSheetCollection.getProcedureConsentForm().getFooterFields());
					procedureConsentForm.setBody(procedureSheetCollection.getProcedureConsentForm().getBody());
				}

				BeanUtil.map(procedureSheetCollection, response);
				response.setProcedureSheetFields(procedureSheetFields);
				response.setProcedureConsentForm(procedureConsentForm);
				response.setDiagrams(procedureSheetCollection.getDiagrams());

				PatientCollection patientCollection = patientRepository.findByUserIdLocationIdAndHospitalId(procedureSheetCollection.getPatientId(),
						procedureSheetCollection.getLocationId(), procedureSheetCollection.getHospitalId());
				if (patientCollection != null) {
					PatientShortCard patientShortCard = new PatientShortCard();
					BeanUtil.map(patientCollection, patientShortCard);
					response.setPatient(patientShortCard);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			//e.printStackTrace();
			logger.warn(e);
		}
		return response;
	}

	@Override
	@Transactional
	public ProcedureSheetResponse getProcedureSheet(String id) {
		ProcedureSheetResponse response = null;
		ProcedureSheetCollection procedureSheetCollection = null;
		List<Map<String, ProcedureSheetField>> procedureSheetFields = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(id)) {
				procedureSheetCollection = procedureSheetRepository.findById(new ObjectId(id)).orElse(null);
			} else {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			if (procedureSheetCollection != null) {
				
				response = new ProcedureSheetResponse();
				procedureSheetFields = procedureSheetCollection.getProcedureSheetFields();
				
				procedureSheetCollection.setProcedureSheetFields(null);
				//BeanUtil.map(procedureSheetCollection, response);
				response.setId(String.valueOf(procedureSheetCollection.getId()));
				response.setDoctorId(String.valueOf(procedureSheetCollection.getDoctorId()));
				response.setLocationId(String.valueOf(procedureSheetCollection.getLocationId()));
				response.setHospitalId(String.valueOf(procedureSheetCollection.getHospitalId()));
				response.setPatientId(String.valueOf(procedureSheetCollection.getPatientId()));
				response.setProcedureSheetStructureId(String.valueOf(procedureSheetCollection.getProcedureSheetStructureId()));
				response.setProcedureName(procedureSheetCollection.getProcedureName());
				response.setProcedureConsentForm(procedureSheetCollection.getProcedureConsentForm());
				response.setDiscarded(procedureSheetCollection.getDiscarded());
				response.setCreatedTime(procedureSheetCollection.getCreatedTime());
				response.setUpdatedTime(procedureSheetCollection.getUpdatedTime());
				response.setCreatedBy(procedureSheetCollection.getCreatedBy());
				response.setProcedureSheetFields(procedureSheetFields);
				response.setDiagrams(procedureSheetCollection.getDiagrams());

				PatientCollection patientCollection = patientRepository.findByUserIdLocationIdAndHospitalId(procedureSheetCollection.getPatientId(),
						procedureSheetCollection.getLocationId(), procedureSheetCollection.getHospitalId());
				if (patientCollection != null) {
					PatientShortCard patientShortCard = new PatientShortCard();
					BeanUtil.map(patientCollection, patientShortCard);
					response.setPatient(patientShortCard);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			//e.printStackTrace();
			logger.warn(e);
		}
		return response;
	}

	@Override
	@Transactional
	public List<ProcedureSheetResponse> getProcedureSheetList(String doctorId, String hospitalId, String locationId,
			String patientId, String searchTerm, Long from, Long to, Boolean discarded, int page, int size,
			String type) {
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
			if (discarded != null) {
				criteria.and("discarded").is(discarded);
			}
			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria.and("type").is(type);
			}

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<ProcedureSheetResponse> aggregationResults = mongoTemplate.aggregate(aggregation,
					ProcedureSheetCollection.class, ProcedureSheetResponse.class);

			responses = aggregationResults.getMappedResults();

			for (ProcedureSheetResponse procedureSheetResponse : responses) {
				procedureSheetResponse = getProcedureSheet(procedureSheetResponse.getId());
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return responses;

	}
	
	
	@Override
	@Transactional
	public Integer getProcedureSheetListCount(String doctorId, String hospitalId, String locationId,
			String patientId, String searchTerm, Long from, Long to, Boolean discarded,
			String type) {
		Integer count = 0;
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
			if (discarded != null) {
				criteria.and("discarded").is(discarded);
			}
			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria.and("type").is(type);
			}

			count = (int) mongoTemplate.count(new Query(criteria), ProcedureSheetCollection.class);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return count;

	}

	@Override
	@Transactional
	public ProcedureSheetResponse discardProcedureSheet(String id, Boolean discarded) {
		ProcedureSheetResponse response = null;
		ProcedureSheetCollection procedureSheetCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(id)) {
				procedureSheetCollection = procedureSheetRepository.findById(new ObjectId(id)).orElse(null);
				procedureSheetCollection.setDiscarded(discarded);
				procedureSheetCollection = procedureSheetRepository.save(procedureSheetCollection);
			} else {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			if (procedureSheetCollection != null) {
				response = new ProcedureSheetResponse();
				BeanUtil.map(procedureSheetCollection, response);
				PatientCollection patientCollection = patientRepository.findByUserIdLocationIdAndHospitalId(procedureSheetCollection.getPatientId(),
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
	public ProcedureSheetStructureResponse addEditProcedureSheetStructure(
			AddEditProcedureSheetStructureRequest request) {
		ProcedureSheetStructureResponse response = null;
		ProcedureSheetStructureCollection procedureSheetStructureCollection = null;
		List<Map<String, ProcedureConsentFormFields>> procedureSheetFields = null;
		// List<Map<String, ProcedureConsentFormFields>> procedureSheetHeaderFields =
		// null;
		// List<Map<String, ProcedureConsentFormFields>> procedureSheetFooterFields =
		// null;
		ProcedureConsentFormStructure procedureConsentFormStructure = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				procedureSheetStructureCollection = procedureSheetStructureRepository
						.findById(new ObjectId(request.getId())).orElse(null);
			} else {
				procedureSheetStructureCollection = new ProcedureSheetStructureCollection();
				procedureSheetStructureCollection.setCreatedTime(new Date());
				UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
				procedureSheetStructureCollection.setCreatedBy(userCollection.getFirstName());
			}

			if (request.getProcedureConsentFormStructure() != null) {
				procedureConsentFormStructure = new ProcedureConsentFormStructure();
				procedureConsentFormStructure
						.setHeaderFields(request.getProcedureConsentFormStructure().getHeaderFields());
				procedureConsentFormStructure
						.setFooterFields(request.getProcedureConsentFormStructure().getFooterFields());
				procedureConsentFormStructure.setBody(request.getProcedureConsentFormStructure().getBody());
			}
			procedureSheetFields = request.getProcedureSheetFields();
			request.setProcedureSheetFields(null);
			request.setProcedureConsentFormStructure(null);
			BeanUtil.map(request, procedureSheetStructureCollection);
			procedureSheetStructureCollection.setProcedureSheetFields(procedureSheetFields);
			procedureSheetStructureCollection.setDiagrams(request.getDiagrams());
			procedureSheetStructureCollection.setProcedureConsentFormStructure(procedureConsentFormStructure);

			procedureSheetStructureCollection = procedureSheetStructureRepository
					.save(procedureSheetStructureCollection);
			if (procedureSheetStructureCollection != null) {
				if (procedureSheetStructureCollection.getProcedureConsentFormStructure() != null) {
					procedureConsentFormStructure = new ProcedureConsentFormStructure();
					procedureConsentFormStructure.setHeaderFields(
							procedureSheetStructureCollection.getProcedureConsentFormStructure().getHeaderFields());
					procedureConsentFormStructure.setFooterFields(
							procedureSheetStructureCollection.getProcedureConsentFormStructure().getFooterFields());
					procedureConsentFormStructure
							.setBody(procedureSheetStructureCollection.getProcedureConsentFormStructure().getBody());
				}
				response = new ProcedureSheetStructureResponse();

				procedureSheetFields = procedureSheetStructureCollection.getProcedureSheetFields();
				procedureSheetStructureCollection.setProcedureSheetFields(null);
				procedureSheetStructureCollection.setProcedureConsentFormStructure(null);
				BeanUtil.map(procedureSheetStructureCollection, response);
				response.setDiagrams(procedureSheetStructureCollection.getDiagrams());
				response.setProcedureSheetFields(procedureSheetFields);
				response.setProcedureConsentFormStructure(procedureConsentFormStructure);

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
	public ProcedureSheetStructureResponse getProcedureSheetStructure(String id) {
		ProcedureSheetStructureResponse response = null;
		ProcedureSheetStructureCollection procedureSheetStructureCollection = null;
		List<Map<String, ProcedureConsentFormFields>> procedureSheetFields = null;
		ProcedureConsentFormStructure procedureConsentFormStructure = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(id)) {
				procedureSheetStructureCollection = procedureSheetStructureRepository.findById(new ObjectId(id)).orElse(null);
			} else {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			if (procedureSheetStructureCollection != null) {
				if (procedureSheetStructureCollection.getProcedureConsentFormStructure() != null) {
					procedureConsentFormStructure = new ProcedureConsentFormStructure();
					procedureConsentFormStructure.setHeaderFields(
							procedureSheetStructureCollection.getProcedureConsentFormStructure().getHeaderFields());
					procedureConsentFormStructure.setFooterFields(
							procedureSheetStructureCollection.getProcedureConsentFormStructure().getFooterFields());
					procedureConsentFormStructure
							.setBody(procedureSheetStructureCollection.getProcedureConsentFormStructure().getBody());
				}
				response = new ProcedureSheetStructureResponse();
				procedureSheetFields = procedureSheetStructureCollection.getProcedureSheetFields();
				procedureSheetStructureCollection.setProcedureSheetFields(null);
				procedureSheetStructureCollection.setProcedureConsentFormStructure(null);
				BeanUtil.map(procedureSheetStructureCollection, response);
				response.setDiagrams(procedureSheetStructureCollection.getDiagrams());
				response.setProcedureSheetFields(procedureSheetFields);
				response.setProcedureConsentFormStructure(procedureConsentFormStructure);


			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.warn(e);
		}
		return response;
	}

	@Override
	@Transactional
	public ProcedureSheetStructureResponse discardProcedureSheetStructure(String id, Boolean discarded) {
		ProcedureSheetStructureResponse response = null;
		ProcedureSheetStructureCollection procedureSheetStructureCollection = null;
		try {
			if (!DPDoctorUtils.anyStringEmpty(id)) {
				procedureSheetStructureCollection = procedureSheetStructureRepository.findById(new ObjectId(id)).orElse(null);
				procedureSheetStructureCollection.setDiscarded(discarded);
				procedureSheetStructureCollection = procedureSheetStructureRepository
						.save(procedureSheetStructureCollection);
			} else {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
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
	public List<ProcedureSheetStructureResponse> getProcedureSheetStructureList(String doctorId, String hospitalId,
			String locationId, String searchTerm, Long from, Long to, Boolean discarded, int page, int size,
			String type) {
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
			if (discarded != null) {
				criteria.and("discarded").is(discarded);
			}
			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria.and("type").is(new ObjectId(type));
			}
			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));

			AggregationResults<ProcedureSheetStructureResponse> aggregationResults = mongoTemplate.aggregate(
					aggregation, ProcedureSheetStructureCollection.class, ProcedureSheetStructureResponse.class);
			responses = aggregationResults.getMappedResults();

			for (ProcedureSheetStructureResponse procedureSheetStructureResponse : responses) {
				procedureSheetStructureResponse = getProcedureSheetStructure(procedureSheetStructureResponse.getId());
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return responses;

	}
	
	
	@Override
	@Transactional
	public Integer getProcedureSheetStructureListCount(String doctorId, String hospitalId,
			String locationId, String searchTerm, Long from, Long to, Boolean discarded, 
			String type) {
		Integer count = 0;
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
			if (discarded != null) {
				criteria.and("discarded").is(discarded);
			}
			if (!DPDoctorUtils.anyStringEmpty(type)) {
				criteria.and("type").is(new ObjectId(type));
			}
			count = (int) mongoTemplate.count(new Query(criteria), ProcedureSheetStructureCollection.class);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return count;

	}

	public String downloadProcedureSheet(String id) {
		String response = null;
		try {
			ProcedureSheetCollection procedureSheetCollection = procedureSheetRepository.findById(new ObjectId(id)).orElse(null);

			if (procedureSheetCollection != null) {
				JasperReportResponse jasperReportResponse = createProcedureSheetJasper(procedureSheetCollection);
				if (jasperReportResponse != null)
					response = getFinalImageURL(jasperReportResponse.getPath());
				if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
					if (jasperReportResponse.getFileSystemResource().getFile().exists())
						jasperReportResponse.getFileSystemResource().getFile().delete();
			} else {

				throw new BusinessException(ServiceError.NotFound, "Error while getting procedure Sheet PDF");
			}
		} catch (Exception e) {
			e.printStackTrace();

			throw new BusinessException(ServiceError.Unknown, "Error while getting procedure Sheet PDF");
		}
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	private JasperReportResponse createProcedureSheetJasper(ProcedureSheetCollection procedureSheetCollection)
			throws NumberFormatException, IOException {
		JasperReportResponse response = null;
		Map<String, Object> parameters = new HashMap<String, Object>();
		PrintSettingsCollection printSettings = null;
		List<String> keys = null;
		String pattern = "dd/MM/yyyy";
		String key = null;
		String value = null;
		List<DBObject> items = null;
		DBObject item = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
		String field = "";
		ProcedureConsentForm procedureConsentForm = procedureSheetCollection.getProcedureConsentForm();
		if (procedureConsentForm != null) {
			field = "";

			if (procedureConsentForm.getHeaderFields() != null && !procedureConsentForm.getHeaderFields().isEmpty()) {
				for (Map<String, String> map : procedureConsentForm.getHeaderFields()) {
					for (Map.Entry<String, String> entry : map.entrySet()) {
						if (entry != null) {
							key = entry.getKey();
							value = entry.getValue();
							if (!DPDoctorUtils.anyStringEmpty(key, value))
								field = field + "<b>" + key + " : </b>" + value + "<br>";

						}
					}
				}

				field = field + "<br><br>";
				parameters.put("headerField", field);
			}

			if (!DPDoctorUtils.anyStringEmpty(procedureConsentForm.getBody())) {
				parameters.put("body",
						procedureConsentForm.getBody().replace("\n", "<br>").replace("\t", " ") + "<br><br>");
			}

			if (procedureConsentForm.getFooterFields() != null && !procedureConsentForm.getFooterFields().isEmpty()) {
				items = new ArrayList<DBObject>();

				Boolean isImage = false;
				for (Map<String, String> map : procedureConsentForm.getFooterFields()) {


					for (Map.Entry<String, String> entry : map.entrySet()) {
						item = new BasicDBObject();
						if (entry != null) {
							key = entry.getKey();
							value = entry.getValue();

							isImage = false;
							if (!DPDoctorUtils.anyStringEmpty(key, value))

								if (value.toUpperCase().contains(imagePath.toUpperCase())) {

									value = value.replace(" ", "%20");
									isImage = true;
								} else {
									isImage = false;
								}

						}

						item.put("key", key);
						item.put("value", value);
						item.put("isImage", isImage);
						items.add(item);
					}
					parameters.put("footerFields", items);
				}
			}
		}

		if (procedureSheetCollection.getDiagrams() != null && !procedureSheetCollection.getDiagrams().isEmpty())

		{

			for (ImageURLResponse urlResponse : procedureSheetCollection.getDiagrams()) {
				urlResponse.setImageUrl(urlResponse.getImageUrl().replace(" ", "%20"));
				urlResponse.setThumbnailUrl(urlResponse.getThumbnailUrl().replace(" ", "%20"));
			}

			parameters.put("diagram", procedureSheetCollection.getDiagrams());
		}

		if (procedureSheetCollection.getProcedureSheetFields() != null
				&& !procedureSheetCollection.getProcedureSheetFields().isEmpty()) {
			items = new ArrayList<DBObject>();
			for (Map<String, ProcedureSheetField> fields : procedureSheetCollection.getProcedureSheetFields()) {

				keys = new ArrayList<String>(fields.keySet());
				field = "";
				String i = "";
				String [] fieldList = null;
				if (keys != null && !keys.isEmpty()) {
					fieldList = new String [fields.keySet().size()];
					for (int index = 0; index < fields.keySet().size(); index++) {
						i = keys.get(index);
						value = fields.get(i).getValue();
						if (!DPDoctorUtils.anyStringEmpty(i, value)) {
							field = "<b>" + i + " : </b>" + value;
							if (fields.get(i).getSequenceNo() != null) {
								fieldList[fields.get(i).getSequenceNo().intValue()]= field;
							}
						}

					}
					for (int index = 0; index < fieldList.length; index++) {
						item = new BasicDBObject();
						if (!DPDoctorUtils.anyStringEmpty(fieldList[index])) {
							
							item.put("fieldOne", fieldList[index]);

						}
						index++;
						if (index < fieldList.length) {

							if (!DPDoctorUtils.anyStringEmpty(fieldList[index]))

							item.put("fieldTwo", fieldList[index]);
						}
						index++;
						if (index < fieldList.length) {

							if (!DPDoctorUtils.anyStringEmpty(fieldList[index]))
								

							item.put("fieldThree", fieldList[index]);
						}
						index++;
						if (index < fields.size()) {

							if (!DPDoctorUtils.anyStringEmpty(fieldList[index]))

							item.put("fieldFour", fieldList[index]);
						}

						items.add(item);

					}
				}

			}
			parameters.put("item", items);
		}

		String pdfName = "PROCEDURE-SHEET-" + procedureSheetCollection.getId().toString() + new Date().getTime();

		printSettings = printSettingsRepository.getSettings(procedureSheetCollection.getDoctorId(),
				(!DPDoctorUtils.anyStringEmpty(procedureSheetCollection.getLocationId())
						? procedureSheetCollection.getLocationId()
						: null),
				(!DPDoctorUtils.anyStringEmpty(procedureSheetCollection.getHospitalId())
						? procedureSheetCollection.getHospitalId()
						: null));

		if (printSettings == null) {
			printSettings = new PrintSettingsCollection();
			DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
			BeanUtil.map(defaultPrintSettings, printSettings);

		}
		patientVisitService.generatePrintSetup(parameters, printSettings, printSettings.getDoctorId());
		String layout = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
				: "PORTRAIT";
		String pageSize = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4")
				: "A4";
		Integer topMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getTopMargin() : 20)
				: 20;
		Integer bottonMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getBottomMargin() : 20)
				: 20;
		Integer leftMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != null
						? printSettings.getPageSetup().getLeftMargin()
						: 20)
				: 20;
		Integer rightMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
						? printSettings.getPageSetup().getRightMargin()
						: 20)
				: 20;
		response = jasperReportService.createPDF(ComponentType.PROCEDURE_SHEET, parameters, procedureSheetJasper,
				layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));

		return response;
	}

}
