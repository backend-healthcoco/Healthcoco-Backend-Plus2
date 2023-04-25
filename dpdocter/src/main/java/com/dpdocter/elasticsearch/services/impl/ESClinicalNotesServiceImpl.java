package com.dpdocter.elasticsearch.services.impl;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.dpdocter.elasticsearch.document.ESComplaintsDocument;
import com.dpdocter.elasticsearch.document.ESDiagnosesDocument;
import com.dpdocter.elasticsearch.document.ESDiagramsDocument;
import com.dpdocter.elasticsearch.document.ESDoctorDocument;
import com.dpdocter.elasticsearch.document.ESECGDetailsDocument;
import com.dpdocter.elasticsearch.document.ESEarsExaminationDocument;
import com.dpdocter.elasticsearch.document.ESEchoDocument;
import com.dpdocter.elasticsearch.document.ESGeneralExamDocument;
import com.dpdocter.elasticsearch.document.ESHolterDocument;
import com.dpdocter.elasticsearch.document.ESIndicationOfUSGDocument;
import com.dpdocter.elasticsearch.document.ESIndirectLarygoscopyExaminationDocument;
import com.dpdocter.elasticsearch.document.ESInvestigationsDocument;
import com.dpdocter.elasticsearch.document.ESMenstrualHistoryDocument;
import com.dpdocter.elasticsearch.document.ESNeckExaminationDocument;
import com.dpdocter.elasticsearch.document.ESNoseExaminationDocument;
import com.dpdocter.elasticsearch.document.ESNotesDocument;
import com.dpdocter.elasticsearch.document.ESNursingCareExaminationDocument;
import com.dpdocter.elasticsearch.document.ESObservationsDocument;
import com.dpdocter.elasticsearch.document.ESObstetricHistoryDocument;
import com.dpdocter.elasticsearch.document.ESOralCavityAndThroatExaminationDocument;
import com.dpdocter.elasticsearch.document.ESPADocument;
import com.dpdocter.elasticsearch.document.ESPSDocument;
import com.dpdocter.elasticsearch.document.ESPVDocument;
import com.dpdocter.elasticsearch.document.ESPresentComplaintDocument;
import com.dpdocter.elasticsearch.document.ESPresentComplaintHistoryDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintEarsDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintNoseDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintOralCavityDocument;
import com.dpdocter.elasticsearch.document.ESPresentingComplaintThroatDocument;
import com.dpdocter.elasticsearch.document.ESProcedureNoteDocument;
import com.dpdocter.elasticsearch.document.ESProvisionalDiagnosisDocument;
import com.dpdocter.elasticsearch.document.ESSpecialityDocument;
import com.dpdocter.elasticsearch.document.ESSystemExamDocument;
import com.dpdocter.elasticsearch.document.ESXRayDetailsDocument;
import com.dpdocter.elasticsearch.repository.ESComplaintsRepository;
import com.dpdocter.elasticsearch.repository.ESDiagnosesRepository;
import com.dpdocter.elasticsearch.repository.ESDiagramsRepository;
import com.dpdocter.elasticsearch.repository.ESDoctorRepository;
import com.dpdocter.elasticsearch.repository.ESECGDetailsRepository;
import com.dpdocter.elasticsearch.repository.ESEarsExaminationRepository;
import com.dpdocter.elasticsearch.repository.ESEchoRepository;
import com.dpdocter.elasticsearch.repository.ESGeneralExamRepository;
import com.dpdocter.elasticsearch.repository.ESHolterRepository;
import com.dpdocter.elasticsearch.repository.ESIndicationOfUSGRepository;
import com.dpdocter.elasticsearch.repository.ESIndirectLarygoscopryExaminationRepository;
import com.dpdocter.elasticsearch.repository.ESInvestigationsRepository;
import com.dpdocter.elasticsearch.repository.ESMenstrualHistoryRepository;
import com.dpdocter.elasticsearch.repository.ESNeckExaminationRepository;
import com.dpdocter.elasticsearch.repository.ESNoseExaminationRepository;
import com.dpdocter.elasticsearch.repository.ESNotesRepository;
import com.dpdocter.elasticsearch.repository.ESNursingCareRepository;
import com.dpdocter.elasticsearch.repository.ESObservationsRepository;
import com.dpdocter.elasticsearch.repository.ESObstetricHistoryRepository;
import com.dpdocter.elasticsearch.repository.ESOralCavityThroatExaminationRepository;
import com.dpdocter.elasticsearch.repository.ESPARepository;
import com.dpdocter.elasticsearch.repository.ESPSRepository;
import com.dpdocter.elasticsearch.repository.ESPVRepository;
import com.dpdocter.elasticsearch.repository.ESPresentComplaintHistoryRepository;
import com.dpdocter.elasticsearch.repository.ESPresentComplaintRepository;
import com.dpdocter.elasticsearch.repository.ESPresentingComplaintEarsRepository;
import com.dpdocter.elasticsearch.repository.ESPresentingComplaintNoseRepository;
import com.dpdocter.elasticsearch.repository.ESPresentingComplaintOralCavityRepository;
import com.dpdocter.elasticsearch.repository.ESPresentingComplaintThroatRepository;
import com.dpdocter.elasticsearch.repository.ESProcedureNoteRepository;
import com.dpdocter.elasticsearch.repository.ESProvisionalDiagnosisRepository;
import com.dpdocter.elasticsearch.repository.ESSystemExamRepository;
import com.dpdocter.elasticsearch.repository.ESXRayDetailsRepository;
import com.dpdocter.elasticsearch.services.ESClinicalNotesService;
import com.dpdocter.enums.Range;
import com.dpdocter.enums.Resource;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.TransactionalManagementService;

import common.util.web.DPDoctorUtils;
import common.util.web.Response;

@Service
public class ESClinicalNotesServiceImpl implements ESClinicalNotesService {

	private static Logger logger = Logger.getLogger(ESClinicalNotesServiceImpl.class.getName());

	@Autowired
	private ESComplaintsRepository esComplaintsRepository;

	@Autowired
	private ESDiagnosesRepository esDiagnosesRepository;

	@Autowired
	private ESNotesRepository esNotesRepository;

	@Autowired
	private ESDiagramsRepository esDiagramsRepository;

	@Autowired
	private ESInvestigationsRepository esInvestigationsRepository;

	@Autowired
	private ESObservationsRepository esObservationsRepository;

	@Autowired
	private ESPresentComplaintRepository esPresentComplaintRepository;

	@Autowired
	private ESPresentComplaintHistoryRepository esPresentComplaintHistoryRepository;

	@Autowired
	private ESProvisionalDiagnosisRepository esProvisionalDiagnosisRepository;

	@Autowired
	private ESSystemExamRepository esSystemExamRepository;

	@Autowired
	private ESGeneralExamRepository esGeneralExamRepository;

	@Autowired
	private ESMenstrualHistoryRepository esMenstrualHistoryRepository;

	@Autowired
	private ESObstetricHistoryRepository esObstetricHistoryRepository;

	@Autowired
	private ESIndicationOfUSGRepository esIndicationOfUSGRepository;

	@Autowired
	private ESPARepository espaRepository;

	@Autowired
	private ESPSRepository espsRepository;

	@Autowired
	private ESPVRepository espvRepository;

	@Autowired
	private ESXRayDetailsRepository esxRayDetailsRepository;

	@Autowired
	private ESECGDetailsRepository esecgDetailsRepository;

	@Autowired
	private ESEchoRepository esEchoRepository;

	@Autowired
	private ESHolterRepository esholterRepository;

	@Autowired
	private TransactionalManagementService transnationalService;

	@Autowired
	private ESDoctorRepository esDoctorRepository;

	@Autowired
	private ESProcedureNoteRepository esProcedureNoteRepository;

	@Autowired
	private ESPresentingComplaintNoseRepository esPresentingComplaintNoseRepository;

	@Autowired
	private ESPresentingComplaintEarsRepository esPresentingComplaintEarsRepository;

	@Autowired
	private ESPresentingComplaintThroatRepository esPresentingComplaintThroatRepository;

	@Autowired
	private ESPresentingComplaintOralCavityRepository esPresentingComplaintOralCavityRepository;

	@Autowired
	private ESNeckExaminationRepository esNeckExaminationRepository;

	@Autowired
	private ESNoseExaminationRepository esNoseExaminationRepository;

	@Autowired
	private ESOralCavityThroatExaminationRepository esOralCavityThroatExaminationRepository;

	@Autowired
	private ESEarsExaminationRepository esEarsExaminationRepository;

	@Autowired
	private ESIndirectLarygoscopryExaminationRepository esIndirectLarygoscopryExaminationRepository;

	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private ESNursingCareRepository nursingCareRepository;

	@Override
	public boolean addComplaints(ESComplaintsDocument request) {
		boolean response = false;
		try {
			esComplaintsRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.COMPLAINT, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Complaints");
		}
		return response;
	}

	@Override
	public boolean addDiagnoses(ESDiagnosesDocument request) {
		boolean response = false;
		try {
			esDiagnosesRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.DIAGNOSIS, true);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Diagnosis");
		}
		return response;
	}

	@Override
	public boolean addNotes(ESNotesDocument request) {
		boolean response = false;
		try {
			esNotesRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.NOTES, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Notes");
		}
		return response;
	}

	@Override
	public boolean addDiagrams(ESDiagramsDocument request) {
		boolean response = false;
		try {
			esDiagramsRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.DIAGRAM, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Diagrams");
		}
		return response;
	}

	@Override
	public boolean addInvestigations(ESInvestigationsDocument request) {
		boolean response = false;
		try {
			esInvestigationsRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.INVESTIGATION, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Investigations");
		}
		return response;
	}

	@Override
	public boolean addObservations(ESObservationsDocument request) {
		boolean response = false;
		try {
			esObservationsRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.OBSERVATION, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Observations");
		}
		return response;
	}

	@Override
	public Response<ESObservationsDocument> searchObservations(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESObservationsDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalObservations(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomObservations(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalObservations(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		default:
			break;
		}

		return response;
	}

	@Override
	public Response<ESInvestigationsDocument> searchInvestigations(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESInvestigationsDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalInvestigations(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomInvestigations(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalInvestigations(page, size, doctorId, locationId, hospitalId, updatedTime,
					discarded, searchTerm);
			break;
		default:
			break;
		}

		return response;
	}

	@Override
	public Response<ESDiagramsDocument> searchDiagrams(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESDiagramsDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {
		case GLOBAL:
			response = getGlobalDiagrams(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomDiagrams(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalDiagrams(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESNotesDocument> searchNotes(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESNotesDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {
		case GLOBAL:
			response = getGlobalNotes(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomNotes(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalNotes(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESDiagnosesDocument> searchDiagnoses(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESDiagnosesDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {
		case GLOBAL:
			response = getGlobalDiagnosis(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomDiagnosis(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalDiagnosis(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESComplaintsDocument> searchComplaints(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESComplaintsDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalComplaints(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomComplaints(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalComplaints(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESPresentComplaintDocument> searchPresentComplaints(String range, int page, int size,
			String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded,
			String searchTerm) {
		Response<ESPresentComplaintDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalPresentComplaints(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomPresentComplaints(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalPresentComplaints(page, size, doctorId, locationId, hospitalId, updatedTime,
					discarded, searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESPresentComplaintHistoryDocument> searchPresentComplaintsHistory(String range, int page, int size,
			String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded,
			String searchTerm) {
		Response<ESPresentComplaintHistoryDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalPresentComplaintHistory(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomPresentComplaintHistory(page, size, doctorId, locationId, hospitalId, updatedTime,
					discarded, searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalPresentComplaintHistory(page, size, doctorId, locationId, hospitalId, updatedTime,
					discarded, searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESProvisionalDiagnosisDocument> searchProvisionalDiagnosis(String range, int page, int size,
			String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded,
			String searchTerm) {
		Response<ESProvisionalDiagnosisDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalProvisionalDiagnosis(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomProvisionalDiagnosis(page, size, doctorId, locationId, hospitalId, updatedTime,
					discarded, searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalProvisionalDiagnosis(page, size, doctorId, locationId, hospitalId, updatedTime,
					discarded, searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESGeneralExamDocument> searchGeneralExam(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESGeneralExamDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalGeneralExam(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomGeneralExam(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalGeneralExam(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESSystemExamDocument> searchSystemExam(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESSystemExamDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalSystemExam(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomSystemExam(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalSystemExam(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESMenstrualHistoryDocument> searchMenstrualHistory(String range, int page, int size,
			String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded,
			String searchTerm) {
		Response<ESMenstrualHistoryDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalMenstrualHistory(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomMenstrualHistory(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalMenstrualHistory(page, size, doctorId, locationId, hospitalId, updatedTime,
					discarded, searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESObstetricHistoryDocument> searchObstetricHistory(String range, int page, int size,
			String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded,
			String searchTerm) {
		Response<ESObstetricHistoryDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalObstetricHistory(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomObstetricsHistory(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalObstetricHistory(page, size, doctorId, locationId, hospitalId, updatedTime,
					discarded, searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESIndicationOfUSGDocument> searchIndicationOfUSG(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESIndicationOfUSGDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalIndicationOfUSG(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomIndicationOfUSG(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalIndicationOfUSG(page, size, doctorId, locationId, hospitalId, updatedTime,
					discarded, searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESPADocument> searchPA(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESPADocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalPA(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomPA(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalPA(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESPVDocument> searchPV(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESPVDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalPV(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomPV(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalPV(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESPSDocument> searchPS(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESPSDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalPS(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomPS(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalPS(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESECGDetailsDocument> searchECGDetails(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESECGDetailsDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalECGDetails(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomECGDetails(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalECGDetails(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESEchoDocument> searchEcho(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESEchoDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalEcho(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomEcho(page, size, doctorId, locationId, hospitalId, updatedTime, discarded, searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalEcho(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESHolterDocument> searchHolter(String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESHolterDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalHolter(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomHolter(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalHolter(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESXRayDetailsDocument> searchXRayDetails(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESXRayDetailsDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalXRayDetails(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomXRayDetails(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalXRayDetails(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESComplaintsDocument> getCustomGlobalComplaints(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESComplaintsDocument> response = new Response<ESComplaintsDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));
						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.COMPLAINT, page, size, doctorId,
					locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null, null,
					"complaint");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESComplaintsDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESComplaintsDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESComplaintsDocument> getGlobalComplaints(int page, int size, String doctorId, String updatedTime,
			Boolean discarded, String searchTerm) {
		Response<ESComplaintsDocument> response = new Response<ESComplaintsDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.COMPLAINT, page, size, updatedTime,
					discarded, null, searchTerm, specialities, null, null, "complaint");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESComplaintsDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESComplaintsDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
		}
		return response;
	}

	private Response<ESComplaintsDocument> getCustomComplaints(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESComplaintsDocument> response = new Response<ESComplaintsDocument>();
		try {
			SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
					updatedTime, discarded, null, searchTerm, null, null, "complaint");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESComplaintsDocument.class);

			if (count > 0) {
				response = new Response<ESComplaintsDocument>();
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESComplaintsDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
		}
		return response;
	}

	@SuppressWarnings({ "unchecked" })
	private Response<ESDiagramsDocument> getCustomGlobalDiagrams(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESDiagramsDocument> response = new Response<ESDiagramsDocument>();

		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
					.must(QueryBuilders.rangeQuery("updatedTime").from(new Date().getTime()));

			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				boolQueryBuilder.must(
						boolQuery().should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("doctorId")))
								.should(QueryBuilders.termQuery("doctorId", doctorId)).minimumShouldMatch(1));

			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId)) {
				boolQueryBuilder
						.must(boolQuery()
								.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("locationId")))
								.should(QueryBuilders.termQuery("locationId", locationId)).minimumShouldMatch(1))
						.must(boolQuery()
								.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery("hospitalId")))
								.should(QueryBuilders.termQuery("hospitalId", hospitalId)).minimumShouldMatch(1));
			}

			if (specialities != null && !specialities.isEmpty()) {
				BoolQueryBuilder specialityQueryBuilder = boolQuery()
						.should(boolQuery().mustNot(QueryBuilders.existsQuery("speciality")));
				for (String speciality : specialities) {
					if (!DPDoctorUtils.anyStringEmpty(speciality)) {
						specialityQueryBuilder.should(QueryBuilders.matchQuery("speciality", speciality));
					}
				}
				specialityQueryBuilder.minimumShouldMatch(1);
				boolQueryBuilder.must(specialityQueryBuilder);
			} else
				boolQueryBuilder.mustNot(QueryBuilders.existsQuery("speciality"));

			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("tags", searchTerm));
			if (!discarded)
				boolQueryBuilder.must(QueryBuilders.termQuery("discarded", discarded));

			SearchQuery searchQuery = null;
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(PageRequest.of((int) page, size, Direction.DESC, "updatedTime")).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC)).build();

			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(), ESDiagramsDocument.class);

			if (count > 0) {
				response.setCount(count);
				List<ESDiagramsDocument> diagrams = elasticsearchTemplate.queryForList(searchQuery,
						ESDiagramsDocument.class);
				response.setDataList(getFinalDiagrams(diagrams));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
		}
		return response;
	}

	@SuppressWarnings({ "unchecked" })
	private Response<ESDiagramsDocument> getGlobalDiagrams(int page, int size, String doctorId, String updatedTime,
			Boolean discarded, String searchTerm) {
		Response<ESDiagramsDocument> response = new Response<ESDiagramsDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));
						if (!DPDoctorUtils.anyStringEmpty(searchTerm))
							boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("speciality", searchTerm));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}
			BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder()
					.must(QueryBuilders.rangeQuery("updatedTime").from(Long.parseLong(updatedTime))
							.to(new Date().getTime()))
					.mustNot(QueryBuilders.existsQuery("doctorId")).mustNot(QueryBuilders.existsQuery("locationId"))
					.mustNot(QueryBuilders.existsQuery("hospitalId"));

			if (specialities != null && !specialities.isEmpty()) {
				BoolQueryBuilder specialityQueryBuilder = boolQuery()
						.should(boolQuery().mustNot(QueryBuilders.existsQuery("speciality")));
				for (String speciality : specialities) {
					if (!DPDoctorUtils.anyStringEmpty(speciality)) {
						specialityQueryBuilder.should(QueryBuilders.matchQuery("speciality", speciality));
					}
				}
				specialityQueryBuilder.minimumShouldMatch(1);
				boolQueryBuilder.must(specialityQueryBuilder);
			} else
				boolQueryBuilder.mustNot(QueryBuilders.existsQuery("speciality"));

			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("tags", searchTerm));
			if (!discarded)
				boolQueryBuilder.must(QueryBuilders.termQuery("discarded", discarded));

			SearchQuery searchQuery = null;
			if (size > 0)
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withPageable(PageRequest.of((int) page, size, Direction.DESC, "updatedTime")).build();
			else
				searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
						.withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC)).build();

			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(), ESDiagramsDocument.class);

			if (count > 0) {
				response.setCount(count);
				List<ESDiagramsDocument> diagrams = elasticsearchTemplate.queryForList(searchQuery,
						ESDiagramsDocument.class);
				response.setDataList(getFinalDiagrams(diagrams));
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
		}
		return response;
	}

	private Response<ESDiagramsDocument> getCustomDiagrams(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESDiagramsDocument> response = new Response<ESDiagramsDocument>();
		try {

			if (doctorId == null)
				response.setDataList(new ArrayList<ESDiagramsDocument>());
			else {
				BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder().must(QueryBuilders.rangeQuery("updatedTime")
						.from(Long.parseLong(updatedTime)).to(new Date().getTime()))
						.must(QueryBuilders.termQuery("doctorId", doctorId));

				if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
					boolQueryBuilder.must(QueryBuilders.termQuery("locationId", locationId))
							.must(QueryBuilders.termQuery("hospitalId", hospitalId));
				if (!DPDoctorUtils.anyStringEmpty(searchTerm))
					boolQueryBuilder.must(QueryBuilders.matchPhrasePrefixQuery("tags", searchTerm));
				if (!discarded)
					boolQueryBuilder.must(QueryBuilders.termQuery("discarded", discarded));

				SearchQuery searchQuery = null;
				if (size > 0)
					searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
							.withPageable(PageRequest.of((int) page, size, Direction.DESC, "updatedTime")).build();
				else
					searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
							.withSort(SortBuilders.fieldSort("updatedTime").order(SortOrder.DESC)).build();

				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESDiagramsDocument.class);
				if (count > 0) {
					response.setCount(count);
					List<ESDiagramsDocument> diagrams = elasticsearchTemplate.queryForList(searchQuery,
							ESDiagramsDocument.class);
					response.setDataList(getFinalDiagrams(diagrams));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESInvestigationsDocument> getCustomGlobalInvestigations(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESInvestigationsDocument> response = new Response<ESInvestigationsDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.INVESTIGATION, page, size,
					doctorId, locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null,
					null, "investigation");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESInvestigationsDocument.class);
			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESInvestigationsDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESInvestigationsDocument> getGlobalInvestigations(int page, int size, String doctorId,
			String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESInvestigationsDocument> response = new Response<ESInvestigationsDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.INVESTIGATION, page, size, updatedTime,
					discarded, null, searchTerm, specialities, null, null, "investigation");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESInvestigationsDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESInvestigationsDocument.class));
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
		}
		return response;
	}

	private Response<ESInvestigationsDocument> getCustomInvestigations(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESInvestigationsDocument> response = new Response<ESInvestigationsDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESInvestigationsDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "investigation");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESInvestigationsDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(
							elasticsearchTemplate.queryForList(searchQuery, ESInvestigationsDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESObservationsDocument> getCustomGlobalObservations(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESObservationsDocument> response = new Response<ESObservationsDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.OBSERVATION, page, size, doctorId,
					locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null, null,
					"observation");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESObservationsDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESObservationsDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESObservationsDocument> getGlobalObservations(int page, int size, String doctorId,
			String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESObservationsDocument> response = new Response<ESObservationsDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.OBSERVATION, page, size, updatedTime,
					discarded, null, searchTerm, specialities, null, null, "observation");

			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESObservationsDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESInvestigationsDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
		}
		return response;
	}

	private Response<ESObservationsDocument> getCustomObservations(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESObservationsDocument> response = new Response<ESObservationsDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESObservationsDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "observation");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESObservationsDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESObservationsDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESDiagnosesDocument> getCustomGlobalDiagnosis(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESDiagnosesDocument> response = new Response<ESDiagnosesDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.DIAGNOSIS, page, size, doctorId,
					locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null, null,
					"diagnosis");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESDiagnosesDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESDiagnosesDocument.class));
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESDiagnosesDocument> getGlobalDiagnosis(int page, int size, String doctorId, String updatedTime,
			Boolean discarded, String searchTerm) {
		Response<ESDiagnosesDocument> response = new Response<ESDiagnosesDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.DIAGNOSIS, page, size, updatedTime,
					discarded, null, searchTerm, specialities, null, null, "diagnosis");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESDiagnosesDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESDiagnosesDocument.class));
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
		}
		return response;
	}

	private Response<ESDiagnosesDocument> getCustomDiagnosis(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESDiagnosesDocument> response = new Response<ESDiagnosesDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESDiagnosesDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "diagnosis");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESDiagnosesDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESDiagnosesDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESNotesDocument> getCustomGlobalNotes(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {

		Response<ESNotesDocument> response = new Response<ESNotesDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.NOTES, page, size, doctorId,
					locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null, null, "note");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(), ESNotesDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESNotesDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESNotesDocument> getGlobalNotes(int page, int size, String doctorId, String updatedTime,
			Boolean discarded, String searchTerm) {
		Response<ESNotesDocument> response = new Response<ESNotesDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.NOTES, page, size, updatedTime,
					discarded, null, searchTerm, specialities, null, null, "note");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(), ESNotesDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESNotesDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
		}
		return response;
	}

	private Response<ESNotesDocument> getCustomNotes(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESNotesDocument> response = new Response<ESNotesDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESNotesDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "note");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESNotesDocument.class);
				if (count > 0) {
					response.setCount(count);
					response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESNotesDocument.class));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESPresentComplaintDocument> getCustomGlobalPresentComplaints(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESPresentComplaintDocument> response = new Response<ESPresentComplaintDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.PRESENT_COMPLAINT, page, size,
					doctorId, locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null,
					null, "presentComplaint");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESPresentComplaintDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESPresentComplaintDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Present Complaints");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESPresentComplaintDocument> getGlobalPresentComplaints(int page, int size, String doctorId,
			String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESPresentComplaintDocument> response = new Response<ESPresentComplaintDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.PRESENT_COMPLAINT, page, size,
					updatedTime, discarded, null, searchTerm, specialities, null, null, "presentComplaint");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESPresentComplaintDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESPresentComplaintDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Present Complaints");
		}
		return response;
	}

	private Response<ESPresentComplaintDocument> getCustomPresentComplaints(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESPresentComplaintDocument> response = new Response<ESPresentComplaintDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESPresentComplaintDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "presentComplaint");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESPresentComplaintDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(
							elasticsearchTemplate.queryForList(searchQuery, ESPresentComplaintDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Present Complaints");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESPresentComplaintHistoryDocument> getCustomGlobalPresentComplaintHistory(int page, int size,
			String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded,
			String searchTerm) {
		Response<ESPresentComplaintHistoryDocument> response = new Response<ESPresentComplaintHistoryDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.HISTORY_OF_PRESENT_COMPLAINT, page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities,
					null, null, "presentComplaintHistory");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESPresentComplaintHistoryDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(
						elasticsearchTemplate.queryForList(searchQuery, ESPresentComplaintHistoryDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While Getting Present Complaints History");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESPresentComplaintHistoryDocument> getGlobalPresentComplaintHistory(int page, int size,
			String doctorId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESPresentComplaintHistoryDocument> response = new Response<ESPresentComplaintHistoryDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.HISTORY_OF_PRESENT_COMPLAINT, page, size,
					updatedTime, discarded, null, searchTerm, specialities, null, null, "presentComplaintHistory");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESPresentComplaintHistoryDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(
						elasticsearchTemplate.queryForList(searchQuery, ESPresentComplaintHistoryDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While Getting Present Complaints History");
		}
		return response;
	}

	private Response<ESPresentComplaintHistoryDocument> getCustomPresentComplaintHistory(int page, int size,
			String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded,
			String searchTerm) {
		Response<ESPresentComplaintHistoryDocument> response = new Response<ESPresentComplaintHistoryDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESPresentComplaintHistoryDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "presentComplaintHistory");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESPresentComplaintHistoryDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(
							elasticsearchTemplate.queryForList(searchQuery, ESPresentComplaintHistoryDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown,
					"Error Occurred While Getting Present Complaints History");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESProvisionalDiagnosisDocument> getCustomGlobalProvisionalDiagnosis(int page, int size,
			String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded,
			String searchTerm) {
		Response<ESProvisionalDiagnosisDocument> response = new Response<ESProvisionalDiagnosisDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.PROVISIONAL_DIAGNOSIS, page, size,
					doctorId, locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null,
					null, "provisionalDiagnosis");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESProvisionalDiagnosisDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(
						elasticsearchTemplate.queryForList(searchQuery, ESProvisionalDiagnosisDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Provisional Diagnosis");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESProvisionalDiagnosisDocument> getGlobalProvisionalDiagnosis(int page, int size, String doctorId,
			String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESProvisionalDiagnosisDocument> response = new Response<ESProvisionalDiagnosisDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.PROVISIONAL_DIAGNOSIS, page, size,
					updatedTime, discarded, null, searchTerm, specialities, null, null, "provisionalDiagnosis");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESProvisionalDiagnosisDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(
						elasticsearchTemplate.queryForList(searchQuery, ESProvisionalDiagnosisDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Provisional Diagnosis");
		}
		return response;
	}

	private Response<ESProvisionalDiagnosisDocument> getCustomProvisionalDiagnosis(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESProvisionalDiagnosisDocument> response = new Response<ESProvisionalDiagnosisDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESProvisionalDiagnosisDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "provisionalDiagnosis");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESProvisionalDiagnosisDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(
							elasticsearchTemplate.queryForList(searchQuery, ESProvisionalDiagnosisDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Provisional Diagnosis");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESGeneralExamDocument> getCustomGlobalGeneralExam(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESGeneralExamDocument> response = new Response<ESGeneralExamDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.GENERAL_EXAMINATION, page, size,
					doctorId, locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null,
					null, "generalExam");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESGeneralExamDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESGeneralExamDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting General Examination");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESGeneralExamDocument> getGlobalGeneralExam(int page, int size, String doctorId,
			String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESGeneralExamDocument> response = new Response<ESGeneralExamDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.GENERAL_EXAMINATION, page, size,
					updatedTime, discarded, null, searchTerm, specialities, null, null, "generalExam");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESGeneralExamDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESGeneralExamDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting General Examination");
		}
		return response;
	}

	private Response<ESGeneralExamDocument> getCustomGeneralExam(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESGeneralExamDocument> response = new Response<ESGeneralExamDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESGeneralExamDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "generalExam");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESGeneralExamDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESGeneralExamDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting General Examination");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESSystemExamDocument> getCustomGlobalSystemExam(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESSystemExamDocument> response = new Response<ESSystemExamDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.SYSTEMIC_EXAMINATION, page, size,
					doctorId, locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null,
					null, "systemExam");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESSystemExamDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESSystemExamDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting System Examination");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESSystemExamDocument> getGlobalSystemExam(int page, int size, String doctorId, String updatedTime,
			Boolean discarded, String searchTerm) {
		Response<ESSystemExamDocument> response = new Response<ESSystemExamDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.SYSTEMIC_EXAMINATION, page, size,
					updatedTime, discarded, null, searchTerm, specialities, null, null, "systemExam");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESSystemExamDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESSystemExamDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting System Examination");
		}
		return response;
	}

	private Response<ESSystemExamDocument> getCustomSystemExam(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESSystemExamDocument> response = new Response<ESSystemExamDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESSystemExamDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "systemExam");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESSystemExamDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESSystemExamDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting General Examination");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESMenstrualHistoryDocument> getCustomGlobalMenstrualHistory(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESMenstrualHistoryDocument> response = new Response<ESMenstrualHistoryDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.MENSTRUAL_HISTORY, page, size,
					doctorId, locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null,
					null, "menstrualHistory");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESMenstrualHistoryDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESMenstrualHistoryDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Menstrual History");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESMenstrualHistoryDocument> getGlobalMenstrualHistory(int page, int size, String doctorId,
			String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESMenstrualHistoryDocument> response = new Response<ESMenstrualHistoryDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.MENSTRUAL_HISTORY, page, size,
					updatedTime, discarded, null, searchTerm, specialities, null, null, "menstrualHistory");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESMenstrualHistoryDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESMenstrualHistoryDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Menstrual History");
		}
		return response;
	}

	private Response<ESMenstrualHistoryDocument> getCustomMenstrualHistory(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESMenstrualHistoryDocument> response = new Response<ESMenstrualHistoryDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESMenstrualHistoryDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "menstrualHistory");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESMenstrualHistoryDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(
							elasticsearchTemplate.queryForList(searchQuery, ESMenstrualHistoryDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Menstrual");
		}
		return response;
	}

	@Override
	public boolean addPresentComplaint(ESPresentComplaintDocument request) {
		boolean response = false;
		try {
			esPresentComplaintRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.PRESENT_COMPLAINT, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Present Complaint");
		}
		return response;
	}

	@Override
	public boolean addPresentComplaintHistory(ESPresentComplaintHistoryDocument request) {
		boolean response = false;
		try {
			esPresentComplaintHistoryRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.HISTORY_OF_PRESENT_COMPLAINT,
					true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Present Complaint History");
		}
		return response;

	}

	@Override
	public boolean addProvisionalDiagnosis(ESProvisionalDiagnosisDocument request) {
		boolean response = false;
		try {
			esProvisionalDiagnosisRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.PROVISIONAL_DIAGNOSIS, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Provisional Diagnosis");
		}
		return response;

	}

	@Override
	public boolean addSystemExam(ESSystemExamDocument request) {
		boolean response = false;
		try {
			esSystemExamRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.SYSTEMIC_EXAMINATION, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Investigations");
		}
		return response;

	}

	@Override
	public boolean addGeneralExam(ESGeneralExamDocument request) {
		boolean response = false;
		try {
			esGeneralExamRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.GENERAL_EXAMINATION, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Investigations");
		}
		return response;

	}

	@Override
	public boolean addMenstrualHistory(ESMenstrualHistoryDocument request) {
		boolean response = false;
		try {
			esMenstrualHistoryRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.MENSTRUAL_HISTORY, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Investigations");
		}
		return response;

	}

	@Override
	public boolean addObstetricsHistory(ESObstetricHistoryDocument request) {
		boolean response = false;
		try {
			esObstetricHistoryRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.OBSTETRIC_HISTORY, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Investigations");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESObstetricHistoryDocument> getCustomGlobalObstetricHistory(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESObstetricHistoryDocument> response = new Response<ESObstetricHistoryDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.OBSTETRIC_HISTORY, page, size,
					doctorId, locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null,
					null, "obstetricHistory");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESObstetricHistoryDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESObstetricHistoryDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Obstetric History");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESObstetricHistoryDocument> getGlobalObstetricHistory(int page, int size, String doctorId,
			String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESObstetricHistoryDocument> response = new Response<ESObstetricHistoryDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.OBSTETRIC_HISTORY, page, size,
					updatedTime, discarded, null, searchTerm, specialities, null, null, "obstetricHistory");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESObstetricHistoryDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESObstetricHistoryDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Obstetric History");
		}
		return response;
	}

	private Response<ESObstetricHistoryDocument> getCustomObstetricsHistory(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESObstetricHistoryDocument> response = new Response<ESObstetricHistoryDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESObstetricHistoryDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "obstetricHistory");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESObstetricHistoryDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(
							elasticsearchTemplate.queryForList(searchQuery, ESObstetricHistoryDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Obstetric History");
		}
		return response;
	}
	// @Override
	// public List<ESDiagramsDocument> searchDiagramsBySpeciality(String
	// searchTerm) {
	// List<ESDiagramsDocument> response = null;
	// try {
	// response = esDiagramsRepository.findBySpeciality(searchTerm);
	// } catch (Exception e) {
	// e.printStackTrace();
	// logger.error(e + " Error Occurred While Searching Diagrams");
	// throw new BusinessException(ServiceError.Unknown, "Error Occurred While
	// Searching Diagrams");
	// }
	// return response;
	// }

	@Override
	public boolean addPA(ESPADocument request) {
		boolean response = false;
		try {
			espaRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.PA, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving P/A");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESPADocument> getCustomGlobalPA(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESPADocument> response = new Response<ESPADocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.PA, page, size, doctorId,
					locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null, null, "pa");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(), ESPADocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESPADocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/A");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESPADocument> getGlobalPA(int page, int size, String doctorId, String updatedTime,
			Boolean discarded, String searchTerm) {
		Response<ESPADocument> response = new Response<ESPADocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.PA, page, size, updatedTime, discarded,
					null, searchTerm, specialities, null, null, "pa");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(), ESPADocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESPADocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/A");
		}
		return response;
	}

	private Response<ESPADocument> getCustomPA(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESPADocument> response = new Response<ESPADocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESPADocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "pa");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(), ESPADocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESPADocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Indication of USG");
		}
		return response;
	}

	private Response<ESProcedureNoteDocument> getCustomProcedureNote(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESProcedureNoteDocument> response = new Response<ESProcedureNoteDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESProcedureNoteDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "procedureNote");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESProcedureNoteDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(
							elasticsearchTemplate.queryForList(searchQuery, ESProcedureNoteDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting ProcedureNote Document");
		}
		return response;
	}

	@Override
	public boolean addIndicationOfUSG(ESIndicationOfUSGDocument request) {
		boolean response = false;
		try {
			esIndicationOfUSGRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.INDICATION_OF_USG, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Indication of USG");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESIndicationOfUSGDocument> getCustomGlobalIndicationOfUSG(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESIndicationOfUSGDocument> response = new Response<ESIndicationOfUSGDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.INDICATION_OF_USG, page, size,
					doctorId, locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null,
					null, "indicationOfUSG");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESIndicationOfUSGDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESIndicationOfUSGDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Indication of USG");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESIndicationOfUSGDocument> getGlobalIndicationOfUSG(int page, int size, String doctorId,
			String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESIndicationOfUSGDocument> response = new Response<ESIndicationOfUSGDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.INDICATION_OF_USG, page, size,
					updatedTime, discarded, null, searchTerm, specialities, null, null, "indicationOfUSG");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESIndicationOfUSGDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESIndicationOfUSGDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Indication of USG");
		}
		return response;
	}

	private Response<ESIndicationOfUSGDocument> getCustomIndicationOfUSG(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESIndicationOfUSGDocument> response = new Response<ESIndicationOfUSGDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESIndicationOfUSGDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "indicationOfUSG");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESIndicationOfUSGDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(
							elasticsearchTemplate.queryForList(searchQuery, ESIndicationOfUSGDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Indication of USG");
		}
		return response;
	}

	/**
	 * Start of functionality to add PV
	 */
	@Override
	public boolean addPV(ESPVDocument request) {
		boolean response = false;
		try {
			espvRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.PV, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving P/V");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESPVDocument> getCustomGlobalPV(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESPVDocument> response = new Response<ESPVDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.PV, page, size, doctorId,
					locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null, null, "pv");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(), ESPVDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESPVDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/V");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESProcedureNoteDocument> getCustomGlobalProcedureNote(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESProcedureNoteDocument> response = new Response<ESProcedureNoteDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.PROCEDURE_NOTE, page, size,
					doctorId, locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null,
					null, "procedureNote");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESProcedureNoteDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESProcedureNoteDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Procedure Note");
		}
		return response;

	}

	/**
	 * 
	 * @param page
	 * @param size
	 * @param doctorId
	 * @param updatedTime
	 * @param discarded
	 * @param searchTerm
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Response<ESPVDocument> getGlobalPV(int page, int size, String doctorId, String updatedTime,
			Boolean discarded, String searchTerm) {
		Response<ESPVDocument> response = new Response<ESPVDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.PV, page, size, updatedTime, discarded,
					null, searchTerm, specialities, null, null, "pv");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(), ESPVDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESPVDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/V");
		}
		return response;
	}

	private Response<ESPVDocument> getCustomPV(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESPVDocument> response = new Response<ESPVDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESPVDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "pv");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(), ESPVDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESPVDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PV");
		}
		return response;
	}

	@Override
	public boolean addPS(ESPSDocument request) {
		boolean response = false;
		try {
			espsRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.PS, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving P/S");
		}
		return response;

	}

	/**
	 * End of functionality to add PS
	 */

	@Override
	public boolean addXRayDetails(ESXRayDetailsDocument request) {
		boolean response = false;
		try {
			esxRayDetailsRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.XRAY, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving X ray details");
		}
		return response;

	}

	@Override
	public boolean addECGDetails(ESECGDetailsDocument request) {
		boolean response = false;
		try {
			esecgDetailsRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.ECG, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving ECG details");
		}
		return response;

	}

	@Override
	public boolean addEcho(ESEchoDocument request) {
		boolean response = false;
		try {
			esEchoRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.ECHO, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Echo details");
		}
		return response;

	}

	@Override
	public boolean addHolter(ESHolterDocument request) {
		boolean response = false;
		try {
			esholterRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.HOLTER, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Holter");
		}
		return response;

	}

	/**
	 * End of functionality to add PS
	 */

	/**
	 * 
	 * @param page        - page no for pagination
	 * @param size        - size for pagination
	 * @param doctorId
	 * @param locationId
	 * @param hospitalId
	 * @param updatedTime
	 * @param discarded
	 * @param searchTerm  - searchterm for search
	 * @return
	 */

	@SuppressWarnings("unchecked")
	private Response<ESPSDocument> getCustomGlobalPS(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESPSDocument> response = new Response<ESPSDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.PS, page, size, doctorId,
					locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null, null, "ps");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(), ESPSDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESPSDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/S");
		}
		return response;

	}

	/**
	 * 
	 * @param page
	 * @param size
	 * @param doctorId
	 * @param updatedTime
	 * @param discarded
	 * @param searchTerm
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Response<ESPSDocument> getGlobalPS(int page, int size, String doctorId, String updatedTime,
			Boolean discarded, String searchTerm) {
		Response<ESPSDocument> response = new Response<ESPSDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.PS, page, size, updatedTime, discarded,
					null, searchTerm, specialities, null, null, "ps");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(), ESPSDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESPSDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/S");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESProcedureNoteDocument> getGlobalProcedureNote(int page, int size, String doctorId,
			String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESProcedureNoteDocument> response = new Response<ESProcedureNoteDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.PROCEDURE_NOTE, page, size, updatedTime,
					discarded, null, searchTerm, specialities, null, null, "procedureNote");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESProcedureNoteDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESProcedureNoteDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Procedure Note");
		}
		return response;
	}

	/**
	 * 
	 * @param page
	 * @param size
	 * @param doctorId
	 * @param locationId
	 * @param hospitalId
	 * @param updatedTime
	 * @param discarded
	 * @param searchTerm
	 * @return
	 */
	private Response<ESPSDocument> getCustomPS(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESPSDocument> response = new Response<ESPSDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESPSDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "ps");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(), ESPSDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESPSDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PS");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESXRayDetailsDocument> getCustomGlobalXRayDetails(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESXRayDetailsDocument> response = new Response<ESXRayDetailsDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.XRAY, page, size, doctorId,
					locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null, null,
					"xRayDetails");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESXRayDetailsDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESXRayDetailsDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting X Ray details");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESXRayDetailsDocument> getGlobalXRayDetails(int page, int size, String doctorId,
			String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESXRayDetailsDocument> response = new Response<ESXRayDetailsDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.XRAY, page, size, updatedTime, discarded,
					null, searchTerm, specialities, null, null, "xRayDetails");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESXRayDetailsDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESXRayDetailsDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting X-Ray Details");
		}
		return response;
	}

	private Response<ESXRayDetailsDocument> getCustomXRayDetails(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESXRayDetailsDocument> response = new Response<ESXRayDetailsDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESXRayDetailsDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "xRayDetails");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESXRayDetailsDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESXRayDetailsDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting X-Ray details");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESEchoDocument> getCustomGlobalEcho(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESEchoDocument> response = new Response<ESEchoDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.ECHO, page, size, doctorId,
					locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null, null, "echo");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(), ESEchoDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESEchoDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Echo");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESEchoDocument> getGlobalEcho(int page, int size, String doctorId, String updatedTime,
			Boolean discarded, String searchTerm) {
		Response<ESEchoDocument> response = new Response<ESEchoDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.ECHO, page, size, updatedTime, discarded,
					null, searchTerm, specialities, null, null, "echo");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(), ESEchoDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESEchoDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Echo");
		}
		return response;
	}

	private Response<ESEchoDocument> getCustomEcho(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESEchoDocument> response = new Response<ESEchoDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESEchoDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "echo");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(), ESEchoDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESEchoDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Echo");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESHolterDocument> getCustomGlobalHolter(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESHolterDocument> response = new Response<ESHolterDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.HOLTER, page, size, doctorId,
					locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null, null,
					"holter");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(), ESHolterDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESHolterDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Holter");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESHolterDocument> getGlobalHolter(int page, int size, String doctorId, String updatedTime,
			Boolean discarded, String searchTerm) {
		Response<ESHolterDocument> response = new Response<ESHolterDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.HOLTER, page, size, updatedTime,
					discarded, null, searchTerm, specialities, null, null, "holter");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(), ESHolterDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESHolterDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Holter");
		}
		return response;
	}

	private Response<ESHolterDocument> getCustomHolter(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESHolterDocument> response = new Response<ESHolterDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESHolterDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "holter");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESHolterDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESHolterDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Holter");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESECGDetailsDocument> getCustomGlobalECGDetails(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESECGDetailsDocument> response = new Response<ESECGDetailsDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.ECG, page, size, doctorId,
					locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null, null,
					"ecgDetails");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESECGDetailsDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESECGDetailsDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting ECG details");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESECGDetailsDocument> getGlobalECGDetails(int page, int size, String doctorId, String updatedTime,
			Boolean discarded, String searchTerm) {
		Response<ESECGDetailsDocument> response = new Response<ESECGDetailsDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.ECG, page, size, updatedTime, discarded,
					null, searchTerm, specialities, null, null, "ecgDetails");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESECGDetailsDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESECGDetailsDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting ECG Details");
		}
		return response;
	}

	private Response<ESECGDetailsDocument> getCustomECGDetails(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESECGDetailsDocument> response = new Response<ESECGDetailsDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESECGDetailsDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "ecgDetails");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESECGDetailsDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESECGDetailsDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting ECG details");
		}
		return response;
	}

	@Override
	public boolean addProcedureNote(ESProcedureNoteDocument request) {
		boolean response = false;
		try {
			esProcedureNoteRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.PROCEDURE_NOTE, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving P/V");
		}
		return response;

	}

	@Override
	public boolean addPCNose(ESPresentingComplaintNoseDocument request) {
		boolean response = false;
		try {
			esPresentingComplaintNoseRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.PC_NOSE, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving PC Note");
		}
		return response;

	}

	@Override
	public boolean addPCEars(ESPresentingComplaintEarsDocument request) {
		boolean response = false;
		try {
			esPresentingComplaintEarsRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.PC_EARS, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving PC Note");
		}
		return response;

	}

	@Override
	public boolean addPCThroat(ESPresentingComplaintThroatDocument request) {
		boolean response = false;
		try {
			esPresentingComplaintThroatRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.PC_THROAT, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving PC Note");
		}
		return response;

	}

	@Override
	public boolean addPCOralCavity(ESPresentingComplaintOralCavityDocument request) {
		boolean response = false;
		try {
			esPresentingComplaintOralCavityRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.PC_ORAL_CAVITY, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving PC Note");
		}
		return response;

	}

	@Override
	public boolean addNeckExam(ESNeckExaminationDocument request) {
		boolean response = false;
		try {
			esNeckExaminationRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.NECK_EXAM, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving PC Note");
		}
		return response;

	}

	@Override
	public boolean addNoseExam(ESNoseExaminationDocument request) {
		boolean response = false;
		try {
			esNoseExaminationRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.NOSE_EXAM, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving PC Note");
		}
		return response;

	}

	@Override
	public boolean addEarsExam(ESEarsExaminationDocument request) {
		boolean response = false;
		try {
			esEarsExaminationRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.EARS_EXAM, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving PC Note");
		}
		return response;

	}

	@Override
	public boolean addOralCavityThroatExam(ESOralCavityAndThroatExaminationDocument request) {
		boolean response = false;
		try {
			esOralCavityThroatExaminationRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.ORAL_CAVITY_THROAT_EXAM, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving PC Note");
		}
		return response;

	}

	@Override
	public boolean addIndirectLarygoscopyExam(ESIndirectLarygoscopyExaminationDocument request) {
		boolean response = false;
		try {
			esIndirectLarygoscopryExaminationRepository.save(request);
			response = true;
			transnationalService.addResource(new ObjectId(request.getId()), Resource.INDIRECT_LARYGOSCOPY_EXAM, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving PC Note");
		}
		return response;

	}

	@Override
	public Response<ESProcedureNoteDocument> searchProcedureNote(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESProcedureNoteDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalProcedureNote(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomProcedureNote(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalProcedureNote(page, size, doctorId, locationId, hospitalId, updatedTime,
					discarded, searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESPresentingComplaintNoseDocument> searchPCNose(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESPresentingComplaintNoseDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalPCNose(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomPCNose(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalPCNose(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESPresentingComplaintEarsDocument> searchPCEars(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESPresentingComplaintEarsDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalPCEars(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomPCEars(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalPCEars(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESPresentingComplaintThroatDocument> searchPCThroat(String range, int page, int size,
			String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded,
			String searchTerm) {
		Response<ESPresentingComplaintThroatDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalPCThroat(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomPCThroat(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalPCThroat(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESPresentingComplaintOralCavityDocument> searchPCOralCavity(String range, int page, int size,
			String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded,
			String searchTerm) {
		Response<ESPresentingComplaintOralCavityDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalPCOralCavity(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomPCOralCavity(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalPCOralCavity(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESNeckExaminationDocument> searchNeckExam(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESNeckExaminationDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalNeckExam(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomNeckExam(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalNeckExam(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESNoseExaminationDocument> searchNoseExam(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESNoseExaminationDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalNoseExam(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomNoseExam(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalNoseExam(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESEarsExaminationDocument> searchEarsExam(String range, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESEarsExaminationDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalEarsExam(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomEarsExam(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalEarsExam(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESOralCavityAndThroatExaminationDocument> searchOralCavityThroatExam(String range, int page,
			int size, String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded,
			String searchTerm) {
		Response<ESOralCavityAndThroatExaminationDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalOralCavityThroatExam(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomOralCavityThroatExam(page, size, doctorId, locationId, hospitalId, updatedTime,
					discarded, searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalOralCavityThroatExam(page, size, doctorId, locationId, hospitalId, updatedTime,
					discarded, searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@Override
	public Response<ESIndirectLarygoscopyExaminationDocument> searchIndirectLarygoscopyExam(String range, int page,
			int size, String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded,
			String searchTerm) {
		Response<ESIndirectLarygoscopyExaminationDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalIndirectLarygoscopyExam(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomIndierctLarygoscopyExam(page, size, doctorId, locationId, hospitalId, updatedTime,
					discarded, searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalIndirectLarygoscopyExam(page, size, doctorId, locationId, hospitalId, updatedTime,
					discarded, searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESPresentingComplaintEarsDocument> getCustomGlobalPCEars(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESPresentingComplaintEarsDocument> response = new Response<ESPresentingComplaintEarsDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.PC_EARS, page, size, doctorId,
					locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null, null,
					"pcEars");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESPresentingComplaintEarsDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(
						elasticsearchTemplate.queryForList(searchQuery, ESPresentingComplaintEarsDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting X Ray details");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESPresentingComplaintEarsDocument> getGlobalPCEars(int page, int size, String doctorId,
			String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESPresentingComplaintEarsDocument> response = new Response<ESPresentingComplaintEarsDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.PC_EARS, page, size, updatedTime,
					discarded, null, searchTerm, specialities, null, null, "pcEars");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESPresentingComplaintEarsDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(
						elasticsearchTemplate.queryForList(searchQuery, ESPresentingComplaintEarsDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PC Note");
		}
		return response;
	}

	private Response<ESPresentingComplaintEarsDocument> getCustomPCEars(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESPresentingComplaintEarsDocument> response = new Response<ESPresentingComplaintEarsDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESPresentingComplaintEarsDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "pcEars");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESPresentingComplaintEarsDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(
							elasticsearchTemplate.queryForList(searchQuery, ESPresentingComplaintEarsDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PC Note");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESPresentingComplaintThroatDocument> getCustomGlobalPCThroat(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESPresentingComplaintThroatDocument> response = new Response<ESPresentingComplaintThroatDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.PC_THROAT, page, size, doctorId,
					locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null, null,
					"pcThroat");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESPresentingComplaintThroatDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(
						elasticsearchTemplate.queryForList(searchQuery, ESPresentingComplaintThroatDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting X Ray details");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESPresentingComplaintThroatDocument> getGlobalPCThroat(int page, int size, String doctorId,
			String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESPresentingComplaintThroatDocument> response = new Response<ESPresentingComplaintThroatDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.PC_THROAT, page, size, updatedTime,
					discarded, null, searchTerm, specialities, null, null, "pcThroat");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESPresentingComplaintThroatDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(
						elasticsearchTemplate.queryForList(searchQuery, ESPresentingComplaintThroatDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PC Note");
		}
		return response;
	}

	private Response<ESPresentingComplaintThroatDocument> getCustomPCThroat(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESPresentingComplaintThroatDocument> response = new Response<ESPresentingComplaintThroatDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESPresentingComplaintThroatDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "pcThroat");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESPresentingComplaintThroatDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(
							elasticsearchTemplate.queryForList(searchQuery, ESPresentingComplaintThroatDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PC Note");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESPresentingComplaintOralCavityDocument> getCustomGlobalPCOralCavity(int page, int size,
			String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded,
			String searchTerm) {
		Response<ESPresentingComplaintOralCavityDocument> response = new Response<ESPresentingComplaintOralCavityDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.PC_ORAL_CAVITY, page, size,
					doctorId, locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null,
					null, "pcOralCavity");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESPresentingComplaintOralCavityDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(
						elasticsearchTemplate.queryForList(searchQuery, ESPresentingComplaintOralCavityDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting X Ray details");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESPresentingComplaintOralCavityDocument> getGlobalPCOralCavity(int page, int size, String doctorId,
			String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESPresentingComplaintOralCavityDocument> response = new Response<ESPresentingComplaintOralCavityDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.PC_ORAL_CAVITY, page, size, updatedTime,
					discarded, null, searchTerm, specialities, null, null, "pcOralCavity");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESPresentingComplaintOralCavityDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(
						elasticsearchTemplate.queryForList(searchQuery, ESPresentingComplaintOralCavityDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PC Note");
		}
		return response;
	}

	private Response<ESPresentingComplaintOralCavityDocument> getCustomPCOralCavity(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESPresentingComplaintOralCavityDocument> response = new Response<ESPresentingComplaintOralCavityDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESPresentingComplaintOralCavityDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "pcOralCavity");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESPresentingComplaintOralCavityDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(elasticsearchTemplate.queryForList(searchQuery,
							ESPresentingComplaintOralCavityDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PC Note");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESPresentingComplaintNoseDocument> getCustomGlobalPCNose(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESPresentingComplaintNoseDocument> response = new Response<ESPresentingComplaintNoseDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.PC_NOSE, page, size, doctorId,
					locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null, null,
					"pcNose");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESPresentingComplaintNoseDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(
						elasticsearchTemplate.queryForList(searchQuery, ESPresentingComplaintNoseDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting X Ray details");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESPresentingComplaintNoseDocument> getGlobalPCNose(int page, int size, String doctorId,
			String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESPresentingComplaintNoseDocument> response = new Response<ESPresentingComplaintNoseDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.PC_NOSE, page, size, updatedTime,
					discarded, null, searchTerm, specialities, null, null, "pcNose");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESPresentingComplaintNoseDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(
						elasticsearchTemplate.queryForList(searchQuery, ESPresentingComplaintNoseDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PC Note");
		}
		return response;
	}

	/**
	 * 
	 * @param page
	 * @param size
	 * @param doctorId
	 * @param locationId
	 * @param hospitalId
	 * @param updatedTime
	 * @param discarded
	 * @param searchTerm
	 * @return
	 */
	private Response<ESPresentingComplaintNoseDocument> getCustomPCNose(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESPresentingComplaintNoseDocument> response = new Response<ESPresentingComplaintNoseDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESPresentingComplaintNoseDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "pcNose");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESPresentingComplaintNoseDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(
							elasticsearchTemplate.queryForList(searchQuery, ESPresentingComplaintNoseDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PC Note");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESNeckExaminationDocument> getCustomGlobalNeckExam(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESNeckExaminationDocument> response = new Response<ESNeckExaminationDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.NECK_EXAM, page, size, doctorId,
					locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null, null,
					"neckExam");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESNeckExaminationDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESNeckExaminationDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting X Ray details");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESNeckExaminationDocument> getGlobalNeckExam(int page, int size, String doctorId,
			String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESNeckExaminationDocument> response = new Response<ESNeckExaminationDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.NECK_EXAM, page, size, updatedTime,
					discarded, null, searchTerm, specialities, null, null, "neckExam");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESNeckExaminationDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESNeckExaminationDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PC Note");
		}
		return response;
	}

	private Response<ESNeckExaminationDocument> getCustomNeckExam(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESNeckExaminationDocument> response = new Response<ESNeckExaminationDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESNeckExaminationDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "neckExam");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESNeckExaminationDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(
							elasticsearchTemplate.queryForList(searchQuery, ESNeckExaminationDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PC Note");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESNoseExaminationDocument> getCustomGlobalNoseExam(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESNoseExaminationDocument> response = new Response<ESNoseExaminationDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.NOSE_EXAM, page, size, doctorId,
					locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null, null,
					"noseExam");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESNoseExaminationDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESNoseExaminationDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting X Ray details");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESNoseExaminationDocument> getGlobalNoseExam(int page, int size, String doctorId,
			String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESNoseExaminationDocument> response = new Response<ESNoseExaminationDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.NOSE_EXAM, page, size, updatedTime,
					discarded, null, searchTerm, specialities, null, null, "noseExam");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESNoseExaminationDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESNoseExaminationDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PC Note");
		}
		return response;
	}

	private Response<ESNoseExaminationDocument> getCustomNoseExam(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESNoseExaminationDocument> response = new Response<ESNoseExaminationDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESNoseExaminationDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "noseExam");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESNoseExaminationDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(
							elasticsearchTemplate.queryForList(searchQuery, ESNoseExaminationDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PC Note");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESEarsExaminationDocument> getCustomGlobalEarsExam(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESEarsExaminationDocument> response = new Response<ESEarsExaminationDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.EARS_EXAM, page, size, doctorId,
					locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null, null,
					"earsExam");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESEarsExaminationDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESEarsExaminationDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting X Ray details");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESEarsExaminationDocument> getGlobalEarsExam(int page, int size, String doctorId,
			String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESEarsExaminationDocument> response = new Response<ESEarsExaminationDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.EARS_EXAM, page, size, updatedTime,
					discarded, null, searchTerm, specialities, null, null, "earsExam");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESEarsExaminationDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery, ESEarsExaminationDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PC Note");
		}
		return response;
	}

	private Response<ESEarsExaminationDocument> getCustomEarsExam(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESEarsExaminationDocument> response = new Response<ESEarsExaminationDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESEarsExaminationDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "earsExam");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESEarsExaminationDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(
							elasticsearchTemplate.queryForList(searchQuery, ESEarsExaminationDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PC Note");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESIndirectLarygoscopyExaminationDocument> getCustomGlobalIndirectLarygoscopyExam(int page,
			int size, String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded,
			String searchTerm) {
		Response<ESIndirectLarygoscopyExaminationDocument> response = new Response<ESIndirectLarygoscopyExaminationDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.INDIRECT_LARYGOSCOPY_EXAM, page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities,
					null, null, "indirectLarygoscopyExam");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESIndirectLarygoscopyExaminationDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery,
						ESIndirectLarygoscopyExaminationDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting X Ray details");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESIndirectLarygoscopyExaminationDocument> getGlobalIndirectLarygoscopyExam(int page, int size,
			String doctorId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESIndirectLarygoscopyExaminationDocument> response = new Response<ESIndirectLarygoscopyExaminationDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.INDIRECT_LARYGOSCOPY_EXAM, page, size,
					updatedTime, discarded, null, searchTerm, specialities, null, null, "indirectLarygoscopyExam");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESIndirectLarygoscopyExaminationDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery,
						ESIndirectLarygoscopyExaminationDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PC Note");
		}
		return response;
	}

	private Response<ESIndirectLarygoscopyExaminationDocument> getCustomIndierctLarygoscopyExam(int page, int size,
			String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded,
			String searchTerm) {
		Response<ESIndirectLarygoscopyExaminationDocument> response = new Response<ESIndirectLarygoscopyExaminationDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESIndirectLarygoscopyExaminationDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "indirectLarygoscopyExam");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESIndirectLarygoscopyExaminationDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(elasticsearchTemplate.queryForList(searchQuery,
							ESIndirectLarygoscopyExaminationDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PC Note");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESOralCavityAndThroatExaminationDocument> getCustomGlobalOralCavityThroatExam(int page, int size,
			String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded,
			String searchTerm) {
		Response<ESOralCavityAndThroatExaminationDocument> response = new Response<ESOralCavityAndThroatExaminationDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.ORAL_CAVITY_THROAT_EXAM, page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities,
					null, null, "oralCavityThroatExam");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESOralCavityAndThroatExaminationDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery,
						ESOralCavityAndThroatExaminationDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting X Ray details");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private Response<ESOralCavityAndThroatExaminationDocument> getGlobalOralCavityThroatExam(int page, int size,
			String doctorId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESOralCavityAndThroatExaminationDocument> response = new Response<ESOralCavityAndThroatExaminationDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.INDIRECT_LARYGOSCOPY_EXAM, page, size,
					updatedTime, discarded, null, searchTerm, specialities, null, null, "oralCavityThroatExam");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESOralCavityAndThroatExaminationDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(elasticsearchTemplate.queryForList(searchQuery,
						ESOralCavityAndThroatExaminationDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PC Note");
		}
		return response;
	}

	private Response<ESOralCavityAndThroatExaminationDocument> getCustomOralCavityThroatExam(int page, int size,
			String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded,
			String searchTerm) {
		Response<ESOralCavityAndThroatExaminationDocument> response = new Response<ESOralCavityAndThroatExaminationDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESOralCavityAndThroatExaminationDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "oralCavityThroatExam");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESOralCavityAndThroatExaminationDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(elasticsearchTemplate.queryForList(searchQuery,
							ESOralCavityAndThroatExaminationDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PC Note");
		}
		return response;
	}

	private List<ESDiagramsDocument> getFinalDiagrams(List<ESDiagramsDocument> diagrams) {
		for (ESDiagramsDocument diagram : diagrams) {
			if (diagram.getDiagramUrl() != null) {
				diagram.setDiagramUrl(getFinalImageURL(diagram.getDiagramUrl()));
			}
		}
		return diagrams;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	@Override
	public boolean addNursingCareExam(ESNursingCareExaminationDocument nursingCareExaminationDocument) {
		boolean response = false;
		try {
			nursingCareRepository.save(nursingCareExaminationDocument);
			response = true;
			transnationalService.addResource(new ObjectId(nursingCareExaminationDocument.getId()),
					Resource.NURSING_CAREEXAM, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Saving Notes");
		}
		return response;
	}

	@Override
	public Response<ESNursingCareExaminationDocument> searchNursingCareExam(String range, int page, int size,
			String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded,
			String searchTerm) {
		Response<ESNursingCareExaminationDocument> response = null;
		switch (Range.valueOf(range.toUpperCase())) {

		case GLOBAL:
			response = getGlobalNursingCareExam(page, size, doctorId, updatedTime, discarded, searchTerm);
			break;
		case CUSTOM:
			response = getCustomNursingCareExam(page, size, doctorId, locationId, hospitalId, updatedTime, discarded,
					searchTerm);
			break;
		case BOTH:
			response = getCustomGlobalNursingCareExam(page, size, doctorId, locationId, hospitalId, updatedTime,
					discarded, searchTerm);
			break;
		default:
			break;
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESNursingCareExaminationDocument> getGlobalNursingCareExam(int page, int size, String doctorId,
			String updatedTime, Boolean discarded, String searchTerm) {

		Response<ESNursingCareExaminationDocument> response = new Response<ESNursingCareExaminationDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createGlobalQuery(Resource.NURSING_CAREEXAM, page, size,
					updatedTime, discarded, null, searchTerm, specialities, null, null, "nursingCare");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESNursingCareExaminationDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(
						elasticsearchTemplate.queryForList(searchQuery, ESNursingCareExaminationDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PC Note");
		}
		return response;
	}

	private Response<ESNursingCareExaminationDocument> getCustomNursingCareExam(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		Response<ESNursingCareExaminationDocument> response = new Response<ESNursingCareExaminationDocument>();
		try {
			if (doctorId == null)
				response.setDataList(new ArrayList<ESNursingCareExaminationDocument>());
			else {
				SearchQuery searchQuery = DPDoctorUtils.createCustomQuery(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded, null, searchTerm, null, null, "nursingCare");
				Integer count = (int) elasticsearchTemplate.count(
						new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
						ESNursingCareExaminationDocument.class);

				if (count > 0) {
					response.setCount(count);
					response.setDataList(
							elasticsearchTemplate.queryForList(searchQuery, ESNursingCareExaminationDocument.class));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting PC Note");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private Response<ESNursingCareExaminationDocument> getCustomGlobalNursingCareExam(int page, int size,
			String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded,
			String searchTerm) {
		Response<ESNursingCareExaminationDocument> response = new Response<ESNursingCareExaminationDocument>();
		try {
			List<ESDoctorDocument> doctorCollections = null;
			Collection<String> specialities = Collections.EMPTY_LIST;

			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				doctorCollections = esDoctorRepository.findByUserId(doctorId, PageRequest.of(0, 1));
				if (doctorCollections != null && !doctorCollections.isEmpty()) {
					List<String> specialitiesId = doctorCollections.get(0).getSpecialities();
					if (specialitiesId != null && !specialitiesId.isEmpty() && !specialitiesId.contains(null)) {
						BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
								.must(QueryBuilders.termsQuery("_id", specialitiesId));

						int count = (int) elasticsearchTemplate.count(
								new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build(),
								ESSpecialityDocument.class);
						if (count > 0) {
							SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
									.withPageable(PageRequest.of(0, count)).build();
							List<ESSpecialityDocument> resultsSpeciality = elasticsearchTemplate
									.queryForList(searchQuery, ESSpecialityDocument.class);
							if (resultsSpeciality != null && !resultsSpeciality.isEmpty()) {
								specialities = CollectionUtils.collect(resultsSpeciality,
										new BeanToPropertyValueTransformer("speciality"));
								specialities.add("ALL");
							}
						}
					}
				}
			}

			SearchQuery searchQuery = DPDoctorUtils.createCustomGlobalQuery(Resource.NURSING_CAREEXAM, page, size,
					doctorId, locationId, hospitalId, updatedTime, discarded, null, searchTerm, specialities, null,
					null, "nursingCare");
			Integer count = (int) elasticsearchTemplate.count(
					new NativeSearchQueryBuilder().withQuery(searchQuery.getQuery()).build(),
					ESNursingCareExaminationDocument.class);

			if (count > 0) {
				response.setCount(count);
				response.setDataList(
						elasticsearchTemplate.queryForList(searchQuery, ESNursingCareExaminationDocument.class));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting X Ray details");
		}
		return response;
	}

}
