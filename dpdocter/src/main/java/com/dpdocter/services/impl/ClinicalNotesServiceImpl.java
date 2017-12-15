package com.dpdocter.services.impl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.mail.MessagingException;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
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

import com.dpdocter.beans.Appointment;
import com.dpdocter.beans.ClinicalNotes;
import com.dpdocter.beans.ClinicalnoteLookupBean;
import com.dpdocter.beans.Complaint;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DefaultPrintSettings;
import com.dpdocter.beans.Diagnoses;
import com.dpdocter.beans.Diagram;
import com.dpdocter.beans.ECGDetails;
import com.dpdocter.beans.EarsExamination;
import com.dpdocter.beans.Echo;
import com.dpdocter.beans.GeneralExam;
import com.dpdocter.beans.Holter;
import com.dpdocter.beans.IndicationOfUSG;
import com.dpdocter.beans.IndirectLarygoscopyExamination;
import com.dpdocter.beans.Investigation;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.MenstrualHistory;
import com.dpdocter.beans.NeckExamination;
import com.dpdocter.beans.NoseExamination;
import com.dpdocter.beans.Notes;
import com.dpdocter.beans.Observation;
import com.dpdocter.beans.ObstetricHistory;
import com.dpdocter.beans.OralCavityAndThroatExamination;
import com.dpdocter.beans.PA;
import com.dpdocter.beans.PS;
import com.dpdocter.beans.PV;
import com.dpdocter.beans.PresentComplaint;
import com.dpdocter.beans.PresentComplaintHistory;
import com.dpdocter.beans.PresentingComplaintEars;
import com.dpdocter.beans.PresentingComplaintNose;
import com.dpdocter.beans.PresentingComplaintOralCavity;
import com.dpdocter.beans.PresentingComplaintThroat;
import com.dpdocter.beans.ProcedureNote;
import com.dpdocter.beans.ProvisionalDiagnosis;
import com.dpdocter.beans.SystemExam;
import com.dpdocter.beans.XRayDetails;
import com.dpdocter.collections.AppointmentCollection;
import com.dpdocter.collections.ClinicalNotesCollection;
import com.dpdocter.collections.ComplaintCollection;
import com.dpdocter.collections.DiagnosisCollection;
import com.dpdocter.collections.DiagramsCollection;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.ECGDetailsCollection;
import com.dpdocter.collections.EarsExaminationCollection;
import com.dpdocter.collections.EchoCollection;
import com.dpdocter.collections.EmailTrackCollection;
import com.dpdocter.collections.GeneralExamCollection;
import com.dpdocter.collections.HistoryCollection;
import com.dpdocter.collections.HolterCollection;
import com.dpdocter.collections.IndicationOfUSGCollection;
import com.dpdocter.collections.IndirectLarygoscopyExaminationCollection;
import com.dpdocter.collections.InvestigationCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.MenstrualHistoryCollection;
import com.dpdocter.collections.NeckExaminationCollection;
import com.dpdocter.collections.NoseExaminationCollection;
import com.dpdocter.collections.NotesCollection;
import com.dpdocter.collections.ObservationCollection;
import com.dpdocter.collections.ObstetricHistoryCollection;
import com.dpdocter.collections.OralCavityAndThroatExaminationCollection;
import com.dpdocter.collections.PACollection;
import com.dpdocter.collections.PSCollection;
import com.dpdocter.collections.PVCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PatientVisitCollection;
import com.dpdocter.collections.PresentComplaintCollection;
import com.dpdocter.collections.PresentComplaintHistoryCollection;
import com.dpdocter.collections.PresentingComplaintEarsCollection;
import com.dpdocter.collections.PresentingComplaintNoseCollection;
import com.dpdocter.collections.PresentingComplaintOralCavityCollection;
import com.dpdocter.collections.PresentingComplaintThroatCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.ProcedureNoteCollection;
import com.dpdocter.collections.ProvisionalDiagnosisCollection;
import com.dpdocter.collections.SystemExamCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.XRayDetailsCollection;
import com.dpdocter.enums.ClinicalItems;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.Range;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.enums.VitalSignsUnit;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AppointmentRepository;
import com.dpdocter.repository.ClinicalNotesRepository;
import com.dpdocter.repository.ComplaintRepository;
import com.dpdocter.repository.DiagnosisRepository;
import com.dpdocter.repository.DiagramsRepository;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.ECGDetailsRepository;
import com.dpdocter.repository.EarsExaminationRepository;
import com.dpdocter.repository.EchoRepository;
import com.dpdocter.repository.GeneralExamRepository;
import com.dpdocter.repository.HistoryRepository;
import com.dpdocter.repository.HolterRepository;
import com.dpdocter.repository.IndicationOfUSGRepository;
import com.dpdocter.repository.IndirectLarygoscopyExaminationRepository;
import com.dpdocter.repository.InvestigationRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.MenstrualHistoryRepository;
import com.dpdocter.repository.NeckExaminationRepository;
import com.dpdocter.repository.NoseExaminationRepository;
import com.dpdocter.repository.NotesRepository;
import com.dpdocter.repository.ObservationRepository;
import com.dpdocter.repository.ObstetricHistoryRepository;
import com.dpdocter.repository.OralCavityThroatExaminationRepository;
import com.dpdocter.repository.PARepository;
import com.dpdocter.repository.PSRepository;
import com.dpdocter.repository.PVRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PatientVisitRepository;
import com.dpdocter.repository.PresentComplaintHistoryRepository;
import com.dpdocter.repository.PresentComplaintRepository;
import com.dpdocter.repository.PresentingComplaintEarsRepository;
import com.dpdocter.repository.PresentingComplaintNosesRepository;
import com.dpdocter.repository.PresentingComplaintOralCavityRepository;
import com.dpdocter.repository.PresentingComplaintThroatRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.ProcedureNoteRepository;
import com.dpdocter.repository.ProvisionalDiagnosisRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.SystemExamRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.XRayDetailsRepository;
import com.dpdocter.request.AppointmentRequest;
import com.dpdocter.request.ClinicalNotesAddRequest;
import com.dpdocter.request.ClinicalNotesEditRequest;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.response.MailResponse;
import com.dpdocter.services.AppointmentService;
import com.dpdocter.services.ClinicalNotesService;
import com.dpdocter.services.EmailTackService;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PatientVisitService;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import common.util.web.DPDoctorUtils;

@Service
public class ClinicalNotesServiceImpl implements ClinicalNotesService {

	private static Logger logger = Logger.getLogger(ClinicalNotesServiceImpl.class.getName());

	@Autowired
	private ClinicalNotesRepository clinicalNotesRepository;

	@Autowired
	private ComplaintRepository complaintRepository;

	@Autowired
	private ObservationRepository observationRepository;

	@Autowired
	private ProvisionalDiagnosisRepository provisionalDiagnosisRepository;

	@Autowired
	private GeneralExamRepository generalExamRepository;

	@Autowired
	private SystemExamRepository systemExamRepository;

	@Autowired
	private MenstrualHistoryRepository menstrualHistoryRepository;

	@Autowired
	private InvestigationRepository investigationRepository;

	@Autowired
	private DiagnosisRepository diagnosisRepository;

	@Autowired
	private PresentComplaintRepository presentComplaintRepository;

	@Autowired
	private PresentComplaintHistoryRepository presentComplaintHistoryRepository;

	@Autowired
	private ObstetricHistoryRepository obstetricHistoryRepository;

	@Autowired
	private IndicationOfUSGRepository indicationOfUSGRepository;

	@Autowired
	private PARepository paRepository;

	@Autowired
	private PVRepository pvRepository;

	@Autowired
	private NotesRepository notesRepository;

	@Autowired
	private DiagramsRepository diagramsRepository;

	@Autowired
	private FileManager fileManager;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private SpecialityRepository specialityRepository;

	@Autowired
	private JasperReportService jasperReportService;

	@Autowired
	private MailService mailService;

	@Autowired
	private PrintSettingsRepository printSettingsRepository;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private EmailTackService emailTackService;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private PatientVisitRepository patientVisitRepository;

	@Autowired
	private PSRepository psRepository;

	@Autowired
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	private HistoryRepository historyRepository;

	@Autowired
	private PatientVisitService patientVisitService;

	@Autowired
	private XRayDetailsRepository xRayDetailsRepository;

	@Autowired
	private ECGDetailsRepository ecgDetailsRepository;

	@Autowired
	private EchoRepository echoRepository;

	@Autowired
	private HolterRepository holterRepository;

	@Autowired
	private ProcedureNoteRepository procedureNoteRepository;

	@Autowired
	private PresentingComplaintNosesRepository presentingComplaintNotesRepository;

	@Autowired
	private PresentingComplaintEarsRepository presentingComplaintEarsRepository;

	@Autowired
	private PresentingComplaintOralCavityRepository presentingComplaintOralCavityRepository;

	@Autowired
	private PresentingComplaintThroatRepository presentingComplaintThroatRepository;

	@Autowired
	private NoseExaminationRepository noseExaminationRepository;

	@Autowired
	private EarsExaminationRepository earsExaminationRepository;

	@Autowired
	private NeckExaminationRepository neckExaminationRepository;

	@Autowired
	private OralCavityThroatExaminationRepository oralCavityThroatExaminationRepository;

	@Autowired
	private IndirectLarygoscopyExaminationRepository indirectLarygoscopyExaminationRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;

	@Value(value = "${image.path}")
	private String imagePath;

	@Value(value = "${ClinicalNotes.getPatientsClinicalNotesWithVerifiedOTP}")
	private String getPatientsClinicalNotesWithVerifiedOTP;

	@Value(value = "${jasper.print.clinicalnotes.a4.fileName}")
	private String clinicalNotesA4FileName;

	@Value(value = "${jasper.print.clinicalnotes.a5.fileName}")
	private String clinicalNotesA5FileName;

	@Override
	@Transactional
	public ClinicalNotes addNotes(ClinicalNotesAddRequest request, Boolean isAppointmentAdd, String createdBy,
			Appointment appointment) {
		ClinicalNotes clinicalNotes = null;
		List<ObjectId> diagramIds = null;
		Date createdTime = new Date();
		try {
			if (isAppointmentAdd) {
				if (request.getAppointmentRequest() != null) {
					appointment = addNotesAppointment(request.getAppointmentRequest());
				}
			}

			ClinicalNotesCollection clinicalNotesCollection = new ClinicalNotesCollection();
			if (appointment != null) {
				request.setAppointmentId(appointment.getAppointmentId());
				request.setTime(appointment.getTime());
				request.setFromDate(appointment.getFromDate());
			}
			BeanUtil.map(request, clinicalNotesCollection);
			if (DPDoctorUtils.anyStringEmpty(createdBy)) {
				UserCollection userCollection = userRepository.findOne(clinicalNotesCollection.getDoctorId());
				if (userCollection != null) {
					createdBy = (userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
							+ userCollection.getFirstName();
				}
			}
			clinicalNotesCollection.setCreatedBy(createdBy);
			/*
			 * if(request.getPresentComplaint() != null ||
			 * !request.getPresentComplaint().isEmpty()) { ArrayList<String>
			 * presentComplaints = new
			 * ArrayList<String>(Arrays.asList(request.getPresentComplaint().
			 * split(","))); for( String presentComplaint : presentComplaints) {
			 * PresentComplaint complaint = new
			 * PresentComplaint(presentComplaint, request.getDoctorId(),
			 * request.getLocationId(), request.getHospitalId(), false, null); }
			 * }
			 */
			// complaintIds = new ArrayList<ObjectId>();
			/*
			 * if (request.getComplaint() != null &&
			 * !request.getComplaint().isEmpty() &&
			 * request.getGlobalComplaints() != null) { Set<String>
			 * customComplaints = compareGlobalElements(new
			 * HashSet<>(splitCSV(request.getComplaint())), new
			 * HashSet<>(splitCSV(request.getGlobalComplaints()))); if
			 * (customComplaints != null) for (String customComplaint :
			 * customComplaints) { Complaint complaint = new Complaint();
			 * complaint.setComplaint(customComplaint); ComplaintCollection
			 * complaintCollection = new ComplaintCollection();
			 * BeanUtil.map(complaint, complaintCollection);
			 * complaintCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * complaintCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * complaintCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * complaintCollection.setCreatedBy(createdBy);
			 * complaintCollection.setCreatedTime(createdTime);
			 * complaintCollection.setId(null); complaintCollection =
			 * complaintRepository.save(complaintCollection);
			 * 
			 * transactionalManagementService.addResource(complaintCollection.
			 * getId(), Resource.COMPLAINT, false); ESComplaintsDocument
			 * esComplaints = new ESComplaintsDocument();
			 * BeanUtil.map(complaintCollection, esComplaints);
			 * esClinicalNotesService.addComplaints(esComplaints);
			 * 
			 * } }
			 * 
			 * // observationIds = new ArrayList<ObjectId>(); if
			 * (request.getObservation() != null &&
			 * !request.getObservation().isEmpty() &&
			 * request.getGlobalObservations() != null) { Set<String>
			 * customObservations = compareGlobalElements( new
			 * HashSet<>(splitCSV(request.getObservation())), new
			 * HashSet<>(splitCSV(request.getGlobalObservations()))); if
			 * (customObservations != null) for (String customObservation :
			 * customObservations) { Observation observation = new
			 * Observation();
			 * 
			 * observation.setDoctorId(request.getDoctorId());
			 * observation.setLocationId(request.getLocationId());
			 * observation.setHospitalId(request.getHospitalId());
			 * observation.setDoctorId(request.getDoctorId());
			 * 
			 * observation.setObservation(customObservation);
			 * 
			 * ObservationCollection observationCollection = new
			 * ObservationCollection(); BeanUtil.map(observation,
			 * observationCollection); observationCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * observationCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * observationCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * observationCollection.setCreatedBy(createdBy);
			 * observationCollection.setCreatedTime(createdTime);
			 * observationCollection.setId(null); observationCollection =
			 * observationRepository.save(observationCollection);
			 * 
			 * transactionalManagementService.addResource(observationCollection.
			 * getId(), Resource.OBSERVATION, false); ESObservationsDocument
			 * esObservations = new ESObservationsDocument();
			 * BeanUtil.map(observationCollection, esObservations);
			 * esClinicalNotesService.addObservations(esObservations);
			 * 
			 * } }
			 * 
			 * // investigationIds = new ArrayList<ObjectId>(); if
			 * (request.getInvestigation() != null &&
			 * !request.getInvestigation().isEmpty() &&
			 * request.getGlobalInvestigations() != null) {
			 * 
			 * Set<String> customInvestigations = compareGlobalElements( new
			 * HashSet<>(splitCSV(request.getInvestigation())), new
			 * HashSet<>(splitCSV(request.getGlobalInvestigations()))); for
			 * (String customInvestigation : customInvestigations) {
			 * Investigation investigation = new Investigation();
			 * investigation.setInvestigation(customInvestigation);
			 * InvestigationCollection investigationCollection = new
			 * InvestigationCollection(); BeanUtil.map(investigation,
			 * investigationCollection); investigationCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * investigationCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * investigationCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * investigationCollection.setCreatedBy(createdBy);
			 * investigationCollection.setCreatedTime(createdTime);
			 * investigationCollection.setId(null); investigationCollection =
			 * investigationRepository.save(investigationCollection);
			 * 
			 * transactionalManagementService.addResource(
			 * investigationCollection.getId(), Resource.INVESTIGATION, false);
			 * ESInvestigationsDocument esInvestigations = new
			 * ESInvestigationsDocument(); BeanUtil.map(investigationCollection,
			 * esInvestigations);
			 * esClinicalNotesService.addInvestigations(esInvestigations);
			 * 
			 * // investigationIds.add(investigationCollection.getId()); }
			 * 
			 * }
			 * 
			 * // noteIds = new ArrayList<ObjectId>(); if (request.getNote() !=
			 * null && !request.getNote().isEmpty() && request.getGlobalNotes()
			 * != null) {
			 * 
			 * Set<String> customNotes = compareGlobalElements(new
			 * HashSet<>(splitCSV(request.getNote())), new
			 * HashSet<>(splitCSV(request.getGlobalNotes()))); for (String
			 * customNote : customNotes) { Notes note = new Notes();
			 * note.setNote(customNote); NotesCollection notesCollection = new
			 * NotesCollection(); BeanUtil.map(note, notesCollection);
			 * notesCollection.setDoctorId(new ObjectId(request.getDoctorId()));
			 * notesCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * notesCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * notesCollection.setCreatedBy(createdBy);
			 * notesCollection.setCreatedTime(createdTime);
			 * notesCollection.setId(null); notesCollection =
			 * notesRepository.save(notesCollection);
			 * transactionalManagementService.addResource(notesCollection.getId(
			 * ), Resource.NOTES, false); ESNotesDocument esNotes = new
			 * ESNotesDocument(); BeanUtil.map(notesCollection, esNotes);
			 * esClinicalNotesService.addNotes(esNotes); //
			 * noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 * 
			 * if (request.getProvisionalDiagnosis() != null &&
			 * !request.getProvisionalDiagnosis().isEmpty() &&
			 * request.getGlobalProvisionalDiagnoses() != null) { Set<String>
			 * customProvisionalDiagnoses = compareGlobalElements( new
			 * HashSet<>(splitCSV(request.getProvisionalDiagnosis())), new
			 * HashSet<>(splitCSV(request.getGlobalProvisionalDiagnoses())));
			 * for (String customProvisionalDiagnosis :
			 * customProvisionalDiagnoses) { ProvisionalDiagnosis
			 * provisionalDiagnosis = new ProvisionalDiagnosis();
			 * provisionalDiagnosis.setProvisionalDiagnosis(
			 * customProvisionalDiagnosis); ProvisionalDiagnosisCollection
			 * provisionalDiagnosisCollection = new
			 * ProvisionalDiagnosisCollection();
			 * BeanUtil.map(provisionalDiagnosis,
			 * provisionalDiagnosisCollection);
			 * provisionalDiagnosisCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * provisionalDiagnosisCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * provisionalDiagnosisCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * provisionalDiagnosisCollection.setCreatedBy(createdBy);
			 * provisionalDiagnosisCollection.setCreatedTime(createdTime);
			 * provisionalDiagnosisCollection.setId(null);
			 * provisionalDiagnosisCollection = provisionalDiagnosisRepository
			 * .save(provisionalDiagnosisCollection);
			 * transactionalManagementService.addResource(
			 * provisionalDiagnosisCollection.getId(),
			 * Resource.PROVISIONAL_DIAGNOSIS, false);
			 * ESProvisionalDiagnosisDocument esProvisionalDiagnosis = new
			 * ESProvisionalDiagnosisDocument();
			 * BeanUtil.map(provisionalDiagnosisCollection,
			 * esProvisionalDiagnosis);
			 * esClinicalNotesService.addProvisionalDiagnosis(
			 * esProvisionalDiagnosis); // noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 * 
			 * if (request.getPresentComplaint() != null &&
			 * !request.getPresentComplaint().isEmpty() &&
			 * request.getGlobalPresentComplaints() != null) { Set<String>
			 * customPresentComplaints = compareGlobalElements( new
			 * HashSet<>(splitCSV(request.getPresentComplaint())), new
			 * HashSet<>(splitCSV(request.getGlobalPresentComplaints()))); for
			 * (String customPresentComplaint : customPresentComplaints) {
			 * PresentComplaint presentComplaint = new PresentComplaint();
			 * presentComplaint.setPresentComplaint(customPresentComplaint);
			 * PresentComplaintCollection presentComplaintCollection = new
			 * PresentComplaintCollection(); BeanUtil.map(presentComplaint,
			 * presentComplaintCollection);
			 * presentComplaintCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * presentComplaintCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * presentComplaintCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * presentComplaintCollection.setCreatedBy(createdBy);
			 * presentComplaintCollection.setCreatedTime(createdTime);
			 * presentComplaintCollection.setId(null);
			 * presentComplaintCollection =
			 * presentComplaintRepository.save(presentComplaintCollection);
			 * transactionalManagementService.addResource(
			 * presentComplaintCollection.getId(), Resource.PRESENT_COMPLAINT,
			 * false); ESPresentComplaintDocument esPresentComplaint = new
			 * ESPresentComplaintDocument();
			 * BeanUtil.map(presentComplaintCollection, esPresentComplaint);
			 * esClinicalNotesService.addPresentComplaint(esPresentComplaint);
			 * 
			 * } }
			 * 
			 * if (request.getPresentComplaintHistory() != null &&
			 * !request.getPresentComplaintHistory().isEmpty() &&
			 * request.getGlobalPresentComplaintHistories() != null) {
			 * Set<String> customPresentComplaintHistories =
			 * compareGlobalElements( new
			 * HashSet<>(splitCSV(request.getPresentComplaintHistory())), new
			 * HashSet<>(splitCSV(request.getGlobalPresentComplaintHistories()))
			 * ); for (String customPresentComplaintHistory :
			 * customPresentComplaintHistories) { PresentComplaintHistory
			 * presentComplaintHistory = new PresentComplaintHistory();
			 * presentComplaintHistory.setPresentComplaintHistory(
			 * customPresentComplaintHistory); PresentComplaintHistoryCollection
			 * presentComplaintHistoryCollection = new
			 * PresentComplaintHistoryCollection();
			 * BeanUtil.map(presentComplaintHistory,
			 * presentComplaintHistoryCollection);
			 * presentComplaintHistoryCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * presentComplaintHistoryCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * presentComplaintHistoryCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * presentComplaintHistoryCollection.setCreatedBy(createdBy);
			 * presentComplaintHistoryCollection.setCreatedTime(createdTime);
			 * presentComplaintHistoryCollection.setId(null);
			 * presentComplaintHistoryCollection =
			 * presentComplaintHistoryRepository
			 * .save(presentComplaintHistoryCollection);
			 * transactionalManagementService.addResource(
			 * presentComplaintHistoryCollection.getId(),
			 * Resource.HISTORY_OF_PRESENT_COMPLAINT, false);
			 * ESPresentComplaintHistoryDocument esPresentComplaintHistory = new
			 * ESPresentComplaintHistoryDocument();
			 * BeanUtil.map(presentComplaintHistoryCollection,
			 * esPresentComplaintHistory);
			 * esClinicalNotesService.addPresentComplaintHistory(
			 * esPresentComplaintHistory);
			 * 
			 * } }
			 * 
			 * if (request.getGeneralExam() != null &&
			 * !request.getGeneralExam().isEmpty() &&
			 * request.getGlobalGeneralExams() != null) { Set<String>
			 * customGeneralExams = compareGlobalElements( new
			 * HashSet<>(splitCSV(request.getGeneralExam())), new
			 * HashSet<>(splitCSV(request.getGlobalGeneralExams()))); for
			 * (String customGeneralExam : customGeneralExams) { GeneralExam
			 * generalExam = new GeneralExam();
			 * generalExam.setGeneralExam(customGeneralExam);
			 * GeneralExamCollection generalExamCollection = new
			 * GeneralExamCollection(); BeanUtil.map(generalExam,
			 * generalExamCollection); generalExamCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * generalExamCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * generalExamCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * generalExamCollection.setCreatedBy(createdBy);
			 * generalExamCollection.setCreatedTime(createdTime);
			 * generalExamCollection.setId(null); generalExamCollection =
			 * generalExamRepository.save(generalExamCollection);
			 * transactionalManagementService.addResource(generalExamCollection.
			 * getId(), Resource.GENERAL_EXAMINATION, false);
			 * ESGeneralExamDocument esGeneralExam = new
			 * ESGeneralExamDocument(); BeanUtil.map(generalExamCollection,
			 * esGeneralExam);
			 * esClinicalNotesService.addGeneralExam(esGeneralExam); //
			 * noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 * 
			 * if (request.getSystemExam() != null &&
			 * !request.getSystemExam().isEmpty() &&
			 * request.getGlobalSystemExams() != null) { Set<String>
			 * customSystemExams = compareGlobalElements(new
			 * HashSet<>(splitCSV(request.getSystemExam())), new
			 * HashSet<>(splitCSV(request.getGlobalSystemExams()))); for (String
			 * customSystemExam : customSystemExams) { SystemExam systemExam =
			 * new SystemExam(); systemExam.setSystemExam(customSystemExam);
			 * SystemExamCollection systemExamCollection = new
			 * SystemExamCollection(); BeanUtil.map(systemExam,
			 * systemExamCollection); systemExamCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * systemExamCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * systemExamCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * systemExamCollection.setCreatedBy(createdBy);
			 * systemExamCollection.setCreatedTime(createdTime);
			 * systemExamCollection.setId(null); systemExamCollection =
			 * systemExamRepository.save(systemExamCollection);
			 * transactionalManagementService.addResource(systemExamCollection.
			 * getId(), Resource.SYSTEMIC_EXAMINATION, false);
			 * ESSystemExamDocument esSystemExam = new ESSystemExamDocument();
			 * BeanUtil.map(systemExamCollection, esSystemExam);
			 * esClinicalNotesService.addSystemExam(esSystemExam);
			 * 
			 * } }
			 * 
			 * if (request.getMenstrualHistory() != null &&
			 * !request.getMenstrualHistory().isEmpty() &&
			 * request.getGlobalMenstrualHistories() != null) { Set<String>
			 * customMenstrualHistories = compareGlobalElements( new
			 * HashSet<>(splitCSV(request.getMenstrualHistory())), new
			 * HashSet<>(splitCSV(request.getGlobalMenstrualHistories()))); for
			 * (String customMenstrualHistory : customMenstrualHistories) {
			 * MenstrualHistory menstrualHistory = new MenstrualHistory();
			 * menstrualHistory.setMenstrualHistory(customMenstrualHistory);
			 * MenstrualHistoryCollection menstrualHistoryCollection = new
			 * MenstrualHistoryCollection(); BeanUtil.map(menstrualHistory,
			 * menstrualHistoryCollection);
			 * menstrualHistoryCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * menstrualHistoryCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * menstrualHistoryCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * menstrualHistoryCollection.setCreatedBy(createdBy);
			 * menstrualHistoryCollection.setCreatedTime(createdTime);
			 * menstrualHistoryCollection.setId(null);
			 * menstrualHistoryCollection =
			 * menstrualHistoryRepository.save(menstrualHistoryCollection);
			 * transactionalManagementService.addResource(
			 * menstrualHistoryCollection.getId(), Resource.MENSTRUAL_HISTORY,
			 * false); ESMenstrualHistoryDocument esMenstrualHistory = new
			 * ESMenstrualHistoryDocument();
			 * BeanUtil.map(menstrualHistoryCollection, esMenstrualHistory);
			 * esClinicalNotesService.addMenstrualHistory(esMenstrualHistory);
			 * // noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 * 
			 * if (request.getObstetricHistory() != null &&
			 * !request.getObstetricHistory().isEmpty() &&
			 * request.getGlobalObstetricHistories() != null) { Set<String>
			 * customObstetricHistories = compareGlobalElements( new
			 * HashSet<>(splitCSV(request.getObstetricHistory())), new
			 * HashSet<>(splitCSV(request.getGlobalObstetricHistories()))); for
			 * (String customObstetricHistory : customObstetricHistories) {
			 * 
			 * ObstetricHistory obstetricHistory = new ObstetricHistory();
			 * obstetricHistory.setObstetricHistory(customObstetricHistory);
			 * ObstetricHistoryCollection obstetricHistoryCollection = new
			 * ObstetricHistoryCollection(); BeanUtil.map(obstetricHistory,
			 * obstetricHistoryCollection);
			 * obstetricHistoryCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * obstetricHistoryCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * obstetricHistoryCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * obstetricHistoryCollection.setCreatedBy(createdBy);
			 * obstetricHistoryCollection.setCreatedTime(createdTime);
			 * obstetricHistoryCollection.setId(null);
			 * obstetricHistoryCollection =
			 * obstetricHistoryRepository.save(obstetricHistoryCollection);
			 * transactionalManagementService.addResource(
			 * obstetricHistoryCollection.getId(), Resource.OBSTETRIC_HISTORY,
			 * false); ESObstetricHistoryDocument esObstetricHistory = new
			 * ESObstetricHistoryDocument();
			 * BeanUtil.map(obstetricHistoryCollection, esObstetricHistory);
			 * esClinicalNotesService.addObstetricsHistory(esObstetricHistory);
			 * // noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 * 
			 * // diagnosisIds = new ArrayList<ObjectId>(); if
			 * (request.getDiagnosis() != null &&
			 * !request.getDiagnosis().isEmpty() && request.getGlobalDiagnoses()
			 * != null) { Set<String> customDiagnoses =
			 * compareGlobalElements(new
			 * HashSet<>(splitCSV(request.getDiagnosis())), new
			 * HashSet<>(splitCSV(request.getGlobalDiagnoses()))); for (String
			 * customDiagnosis : customDiagnoses) {
			 * 
			 * Diagnoses diagnosis = new Diagnoses();
			 * diagnosis.setDiagnosis(customDiagnosis); DiagnosisCollection
			 * diagnosisCollection = new DiagnosisCollection();
			 * BeanUtil.map(diagnosis, diagnosisCollection);
			 * diagnosisCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * diagnosisCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * diagnosisCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * diagnosisCollection.setCreatedBy(createdBy);
			 * diagnosisCollection.setCreatedTime(createdTime);
			 * diagnosisCollection.setId(null); diagnosisCollection =
			 * diagnosisRepository.save(diagnosisCollection);
			 * transactionalManagementService.addResource(diagnosisCollection.
			 * getId(), Resource.DIAGNOSIS, false); ESDiagnosesDocument
			 * esDiagnoses = new ESDiagnosesDocument();
			 * BeanUtil.map(diagnosisCollection, esDiagnoses);
			 * esClinicalNotesService.addDiagnoses(esDiagnoses); //
			 * noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 * 
			 * if (request.getIndicationOfUSG() != null &&
			 * !request.getIndicationOfUSG().isEmpty() &&
			 * request.getGlobalIndicationOfUSGs() != null) { Set<String>
			 * customIndicationOfUSGs = compareGlobalElements( new
			 * HashSet<>(splitCSV(request.getIndicationOfUSG())), new
			 * HashSet<>(splitCSV(request.getGlobalIndicationOfUSGs()))); for
			 * (String customIndicationOfUSG : customIndicationOfUSGs) {
			 * 
			 * IndicationOfUSG indicationOfUSG = new IndicationOfUSG();
			 * indicationOfUSG.setIndicationOfUSG(customIndicationOfUSG);
			 * IndicationOfUSGCollection indicationOfUSGCollection = new
			 * IndicationOfUSGCollection(); BeanUtil.map(indicationOfUSG,
			 * indicationOfUSGCollection);
			 * indicationOfUSGCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * indicationOfUSGCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * indicationOfUSGCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * indicationOfUSGCollection.setCreatedBy(createdBy);
			 * indicationOfUSGCollection.setCreatedTime(createdTime);
			 * indicationOfUSGCollection.setId(null); indicationOfUSGCollection
			 * = indicationOfUSGRepository.save(indicationOfUSGCollection);
			 * transactionalManagementService.addResource(
			 * indicationOfUSGCollection.getId(), Resource.INDICATION_OF_USG,
			 * false); ESIndicationOfUSGDocument esIndicationOfUSG = new
			 * ESIndicationOfUSGDocument();
			 * BeanUtil.map(indicationOfUSGCollection, esIndicationOfUSG);
			 * esClinicalNotesService.addIndicationOfUSG(esIndicationOfUSG); //
			 * noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 * 
			 * if (request.getPa() != null && !request.getPa().isEmpty() &&
			 * request.getGlobalPAs() != null) { Set<String> customPAs =
			 * compareGlobalElements(new HashSet<>(splitCSV(request.getPa())),
			 * new HashSet<>(splitCSV(request.getGlobalPAs()))); for (String
			 * customPA : customPAs) {
			 * 
			 * PA pa = new PA(); pa.setPa(customPA); PACollection paCollection =
			 * new PACollection(); BeanUtil.map(pa, paCollection);
			 * paCollection.setDoctorId(new ObjectId(request.getDoctorId()));
			 * paCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * paCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * paCollection.setCreatedBy(createdBy);
			 * paCollection.setCreatedTime(createdTime);
			 * paCollection.setId(null); paCollection =
			 * paRepository.save(paCollection);
			 * transactionalManagementService.addResource(paCollection.getId(),
			 * Resource.PA, false); ESPADocument espa = new ESPADocument();
			 * BeanUtil.map(paCollection, espa);
			 * esClinicalNotesService.addPA(espa); //
			 * noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 * 
			 * if (request.getPv() != null && !request.getPv().isEmpty() &&
			 * request.getGlobalPVs() != null) { Set<String> customPVs =
			 * compareGlobalElements(new HashSet<>(splitCSV(request.getPv())),
			 * new HashSet<>(splitCSV(request.getGlobalPVs()))); for (String
			 * customPV : customPVs) {
			 * 
			 * PV pv = new PV(); pv.setPv(customPV); PVCollection pvCollection =
			 * new PVCollection(); BeanUtil.map(pv, pvCollection);
			 * pvCollection.setDoctorId(new ObjectId(request.getDoctorId()));
			 * pvCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * pvCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * pvCollection.setCreatedBy(createdBy);
			 * pvCollection.setCreatedTime(createdTime);
			 * pvCollection.setId(null); pvCollection =
			 * pvRepository.save(pvCollection);
			 * transactionalManagementService.addResource(pvCollection.getId(),
			 * Resource.PV, false); ESPVDocument espv = new ESPVDocument();
			 * BeanUtil.map(pvCollection, espv);
			 * esClinicalNotesService.addPV(espv); //
			 * noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 * 
			 * if (request.getPs() != null && !request.getPs().isEmpty() &&
			 * request.getGlobalPSs() != null) { Set<String> customPSs =
			 * compareGlobalElements(new HashSet<>(splitCSV(request.getPs())),
			 * new HashSet<>(splitCSV(request.getGlobalPSs()))); for (String
			 * customPS : customPSs) {
			 * 
			 * PS ps = new PS(); ps.setPs(customPS); ; PSCollection psCollection
			 * = new PSCollection(); BeanUtil.map(ps, psCollection);
			 * psCollection.setDoctorId(new ObjectId(request.getDoctorId()));
			 * psCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * psCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * psCollection.setCreatedBy(createdBy);
			 * psCollection.setCreatedTime(createdTime);
			 * psCollection.setId(null); psCollection =
			 * psRepository.save(psCollection);
			 * transactionalManagementService.addResource(psCollection.getId(),
			 * Resource.PS, false); ESPSDocument esps = new ESPSDocument();
			 * BeanUtil.map(psCollection, esps);
			 * esClinicalNotesService.addPS(esps); //
			 * noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 * 
			 * if (request.getxRayDetails() != null &&
			 * !request.getxRayDetails().isEmpty() &&
			 * request.getGlobalXRayDetails() != null) { Set<String>
			 * customXrayDetails = compareGlobalElements(new
			 * HashSet<>(splitCSV(request.getxRayDetails())), new
			 * HashSet<>(splitCSV(request.getGlobalXRayDetails()))); for (String
			 * customXrayDetail : customXrayDetails) {
			 * 
			 * XRayDetails xRayDetails = new XRayDetails();
			 * xRayDetails.setxRayDetails(customXrayDetail);
			 * XRayDetailsCollection xRayDetailsCollection = new
			 * XRayDetailsCollection(); BeanUtil.map(xRayDetails,
			 * xRayDetailsCollection); xRayDetailsCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * xRayDetailsCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * xRayDetailsCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * xRayDetailsCollection.setCreatedBy(createdBy);
			 * xRayDetailsCollection.setCreatedTime(createdTime);
			 * xRayDetailsCollection.setId(null); xRayDetailsCollection =
			 * xRayDetailsRepository.save(xRayDetailsCollection);
			 * transactionalManagementService.addResource(xRayDetailsCollection.
			 * getId(), Resource.XRAY, false); ESXRayDetailsDocument
			 * esxRayDetails = new ESXRayDetailsDocument();
			 * BeanUtil.map(xRayDetailsCollection, esxRayDetails);
			 * esClinicalNotesService.addXRayDetails(esxRayDetails); //
			 * noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 * 
			 * if (request.getEcgDetails() != null &&
			 * !request.getEcgDetails().isEmpty() &&
			 * request.getGlobalEcgDetails() != null) { Set<String>
			 * customECGDetails = compareGlobalElements(new
			 * HashSet<>(splitCSV(request.getEcgDetails())), new
			 * HashSet<>(splitCSV(request.getGlobalEcgDetails()))); for (String
			 * customECGDetail : customECGDetails) {
			 * 
			 * ECGDetails ecgDetails = new ECGDetails();
			 * ecgDetails.setEcgDetails(customECGDetail); ; ECGDetailsCollection
			 * ecgDetailsCollection = new ECGDetailsCollection();
			 * BeanUtil.map(ecgDetails, ecgDetailsCollection);
			 * ecgDetailsCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * ecgDetailsCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * ecgDetailsCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * ecgDetailsCollection.setCreatedBy(createdBy);
			 * ecgDetailsCollection.setCreatedTime(createdTime);
			 * ecgDetailsCollection.setId(null); ecgDetailsCollection =
			 * ecgDetailsRepository.save(ecgDetailsCollection);
			 * transactionalManagementService.addResource(ecgDetailsCollection.
			 * getId(), Resource.ECG, false); ESECGDetailsDocument esecgDetails
			 * = new ESECGDetailsDocument(); BeanUtil.map(ecgDetailsCollection,
			 * esecgDetails);
			 * esClinicalNotesService.addECGDetails(esecgDetails); //
			 * noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 * 
			 * if (request.getEcho() != null && !request.getEcho().isEmpty() &&
			 * request.getGlobalEchoes() != null) { Set<String> customEchoes =
			 * compareGlobalElements(new HashSet<>(splitCSV(request.getEcho())),
			 * new HashSet<>(splitCSV(request.getGlobalEchoes()))); for (String
			 * customEcho : customEchoes) {
			 * 
			 * Echo echo = new Echo(); echo.setEcho(customEcho); EchoCollection
			 * echoCollection = new EchoCollection(); BeanUtil.map(echo,
			 * echoCollection); echoCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * echoCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * echoCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * echoCollection.setCreatedBy(createdBy);
			 * echoCollection.setCreatedTime(createdTime);
			 * echoCollection.setId(null); echoCollection =
			 * echoRepository.save(echoCollection);
			 * transactionalManagementService.addResource(echoCollection.getId()
			 * , Resource.ECHO, false); ESEchoDocument esEcho = new
			 * ESEchoDocument(); BeanUtil.map(echoCollection, esEcho);
			 * esClinicalNotesService.addEcho(esEcho); //
			 * noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 * 
			 * if (request.getHolter() != null && !request.getHolter().isEmpty()
			 * && request.getGlobalHolters() != null) { Set<String>
			 * customHolters = compareGlobalElements(new
			 * HashSet<>(splitCSV(request.getHolter())), new
			 * HashSet<>(splitCSV(request.getGlobalHolters()))); for (String
			 * customHolter : customHolters) {
			 * 
			 * Holter holter = new Holter(); holter.setHolter(customHolter);
			 * HolterCollection holterCollection = new HolterCollection();
			 * BeanUtil.map(holter, holterCollection);
			 * holterCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * holterCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * holterCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * holterCollection.setCreatedBy(createdBy);
			 * holterCollection.setCreatedTime(createdTime);
			 * holterCollection.setId(null); holterCollection =
			 * holterRepository.save(holterCollection);
			 * transactionalManagementService.addResource(holterCollection.getId
			 * (), Resource.HOLTER, false); ESHolterDocument esHolter = new
			 * ESHolterDocument(); BeanUtil.map(holterCollection, esHolter);
			 * esClinicalNotesService.addHolter(esHolter); //
			 * noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 * 
			 * if (request.getProcedureNote() != null &&
			 * !request.getProcedureNote().isEmpty() &&
			 * request.getGlobalProcedureNotes() != null) { Set<String>
			 * customProcedureNotes = compareGlobalElements( new
			 * HashSet<>(splitCSV(request.getProcedureNote())), new
			 * HashSet<>(splitCSV(request.getGlobalProcedureNotes()))); for
			 * (String customProcedureNote : customProcedureNotes) {
			 * 
			 * ProcedureNote procedureNote = new ProcedureNote();
			 * procedureNote.setProcedureNote(customProcedureNote);
			 * ProcedureNoteCollection procedureNoteCollection = new
			 * ProcedureNoteCollection(); BeanUtil.map(procedureNote,
			 * procedureNoteCollection); procedureNoteCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * procedureNoteCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * procedureNoteCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * procedureNoteCollection.setCreatedBy(createdBy);
			 * procedureNoteCollection.setCreatedTime(createdTime);
			 * procedureNoteCollection.setId(null); procedureNoteCollection =
			 * procedureNoteRepository.save(procedureNoteCollection);
			 * transactionalManagementService.addResource(
			 * procedureNoteCollection.getId(), Resource.PROCEDURE_NOTE, false);
			 * ESProcedureNoteDocument esProcedureNoteDocument = new
			 * ESProcedureNoteDocument(); BeanUtil.map(procedureNoteCollection,
			 * esProcedureNoteDocument);
			 * esClinicalNotesService.addProcedureNote(esProcedureNoteDocument);
			 * // noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 * 
			 * if (request.getPcNose() != null && !request.getPcNose().isEmpty()
			 * && request.getGlobalPCNose() != null) { Set<String> customPCNoses
			 * = compareGlobalElements(new
			 * HashSet<>(splitCSV(request.getPcNose())), new
			 * HashSet<>(splitCSV(request.getGlobalPCNose()))); for (String
			 * customPCNose : customPCNoses) {
			 * 
			 * PresentingComplaintNose presentingComplaintNose = new
			 * PresentingComplaintNose();
			 * presentingComplaintNose.setPcNose(customPCNose);
			 * PresentingComplaintNoseCollection
			 * presentingComplaintNoseCollection = new
			 * PresentingComplaintNoseCollection();
			 * BeanUtil.map(presentingComplaintNose,
			 * presentingComplaintNoseCollection);
			 * presentingComplaintNoseCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * presentingComplaintNoseCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * presentingComplaintNoseCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * presentingComplaintNoseCollection.setCreatedBy(createdBy);
			 * presentingComplaintNoseCollection.setCreatedTime(createdTime);
			 * presentingComplaintNoseCollection.setId(null);
			 * presentingComplaintNoseCollection =
			 * presentingComplaintNotesRepository
			 * .save(presentingComplaintNoseCollection);
			 * transactionalManagementService.addResource(
			 * presentingComplaintNoseCollection.getId(), Resource.PC_NOSE,
			 * false); ESPresentingComplaintNoseDocument
			 * esPresentingComplaintNoseDocument = new
			 * ESPresentingComplaintNoseDocument();
			 * BeanUtil.map(presentingComplaintNoseCollection,
			 * esPresentingComplaintNoseDocument);
			 * esClinicalNotesService.addPCNose(
			 * esPresentingComplaintNoseDocument); //
			 * noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 * 
			 * if (request.getPcOralCavity() != null &&
			 * !request.getPcOralCavity().isEmpty() &&
			 * request.getGlobalPCOralCavity() != null) { Set<String>
			 * customPCOralCavities = compareGlobalElements( new
			 * HashSet<>(splitCSV(request.getPcOralCavity())), new
			 * HashSet<>(splitCSV(request.getGlobalPCOralCavity()))); for
			 * (String customPCOralCavity : customPCOralCavities) {
			 * 
			 * PresentingComplaintOralCavity presentingComplaintOralCavity = new
			 * PresentingComplaintOralCavity();
			 * presentingComplaintOralCavity.setPcOralCavity(customPCOralCavity)
			 * ; PresentingComplaintOralCavityCollection
			 * presentingComplaintOralCavityCollection = new
			 * PresentingComplaintOralCavityCollection();
			 * BeanUtil.map(presentingComplaintOralCavity,
			 * presentingComplaintOralCavityCollection);
			 * presentingComplaintOralCavityCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * presentingComplaintOralCavityCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * presentingComplaintOralCavityCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * presentingComplaintOralCavityCollection.setCreatedBy(createdBy);
			 * presentingComplaintOralCavityCollection.setCreatedTime(
			 * createdTime);
			 * presentingComplaintOralCavityCollection.setId(null);
			 * presentingComplaintOralCavityCollection =
			 * presentingComplaintOralCavityRepository
			 * .save(presentingComplaintOralCavityCollection);
			 * transactionalManagementService.addResource(
			 * presentingComplaintOralCavityCollection.getId(),
			 * Resource.PC_ORAL_CAVITY, false);
			 * ESPresentingComplaintEarsDocument
			 * esPresentingComplaintEarsDocument = new
			 * ESPresentingComplaintEarsDocument();
			 * BeanUtil.map(presentingComplaintOralCavityCollection,
			 * esPresentingComplaintEarsDocument);
			 * esClinicalNotesService.addPCEars(
			 * esPresentingComplaintEarsDocument); //
			 * noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 * 
			 * if (request.getPcEars() != null && !request.getPcEars().isEmpty()
			 * && request.getGlobalPCEars() != null) { Set<String> customPCEars
			 * = compareGlobalElements(new
			 * HashSet<>(splitCSV(request.getPcNose())), new
			 * HashSet<>(splitCSV(request.getPcNose()))); for (String
			 * customPCEar : customPCEars) {
			 * 
			 * PresentingComplaintEars presentingComplaintEars = new
			 * PresentingComplaintEars();
			 * presentingComplaintEars.setPcEars(customPCEar);
			 * PresentingComplaintEarsCollection
			 * presentingComplaintEarsCollection = new
			 * PresentingComplaintEarsCollection();
			 * BeanUtil.map(presentingComplaintEars,
			 * presentingComplaintEarsCollection);
			 * presentingComplaintEarsCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * presentingComplaintEarsCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * presentingComplaintEarsCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * presentingComplaintEarsCollection.setCreatedBy(createdBy);
			 * presentingComplaintEarsCollection.setCreatedTime(createdTime);
			 * presentingComplaintEarsCollection.setId(null);
			 * presentingComplaintEarsCollection =
			 * presentingComplaintEarsRepository
			 * .save(presentingComplaintEarsCollection);
			 * transactionalManagementService.addResource(
			 * presentingComplaintEarsCollection.getId(), Resource.PC_EARS,
			 * false); ESPresentingComplaintEarsDocument
			 * esPresentingComplaintEarsDocument = new
			 * ESPresentingComplaintEarsDocument();
			 * BeanUtil.map(presentingComplaintEarsCollection,
			 * esPresentingComplaintEarsDocument);
			 * esClinicalNotesService.addPCEars(
			 * esPresentingComplaintEarsDocument); //
			 * noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 * 
			 * if (request.getPcThroat() != null &&
			 * !request.getPcThroat().isEmpty() && request.getGlobalPCThroat()
			 * != null) { Set<String> customPCOralThroats =
			 * compareGlobalElements(new
			 * HashSet<>(splitCSV(request.getPcThroat())), new
			 * HashSet<>(splitCSV(request.getGlobalPCThroat()))); for (String
			 * customPCOralThroat : customPCOralThroats) {
			 * 
			 * PresentingComplaintThroat presentingComplaintThroat = new
			 * PresentingComplaintThroat();
			 * presentingComplaintThroat.setPcThroat(customPCOralThroat);
			 * PresentingComplaintThroatCollection
			 * presentingComplaintOralThroatCollection = new
			 * PresentingComplaintThroatCollection();
			 * BeanUtil.map(presentingComplaintThroat,
			 * presentingComplaintOralThroatCollection);
			 * presentingComplaintOralThroatCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * presentingComplaintOralThroatCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * presentingComplaintOralThroatCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * presentingComplaintOralThroatCollection.setCreatedBy(createdBy);
			 * presentingComplaintOralThroatCollection.setCreatedTime(
			 * createdTime);
			 * presentingComplaintOralThroatCollection.setId(null);
			 * presentingComplaintOralThroatCollection =
			 * presentingComplaintThroatRepository
			 * .save(presentingComplaintOralThroatCollection);
			 * transactionalManagementService.addResource(
			 * presentingComplaintOralThroatCollection.getId(),
			 * Resource.PC_THROAT, false); ESPresentingComplaintThroatDocument
			 * esPresentingComplaintThroatDocument = new
			 * ESPresentingComplaintThroatDocument();
			 * BeanUtil.map(presentingComplaintOralThroatCollection,
			 * esPresentingComplaintThroatDocument);
			 * esClinicalNotesService.addPCThroat(
			 * esPresentingComplaintThroatDocument); //
			 * noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 * 
			 * if (request.getPcThroat() != null &&
			 * !request.getPcThroat().isEmpty() && request.getGlobalPCThroat()
			 * != null) { Set<String> customPCOralThroats =
			 * compareGlobalElements(new
			 * HashSet<>(splitCSV(request.getPcThroat())), new
			 * HashSet<>(splitCSV(request.getGlobalPCThroat()))); for (String
			 * customPCOralThroat : customPCOralThroats) {
			 * 
			 * PresentingComplaintThroat presentingComplaintThroat = new
			 * PresentingComplaintThroat();
			 * presentingComplaintThroat.setPcThroat(customPCOralThroat);
			 * PresentingComplaintThroatCollection
			 * presentingComplaintOralThroatCollection = new
			 * PresentingComplaintThroatCollection();
			 * BeanUtil.map(presentingComplaintThroat,
			 * presentingComplaintOralThroatCollection);
			 * presentingComplaintOralThroatCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * presentingComplaintOralThroatCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * presentingComplaintOralThroatCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * presentingComplaintOralThroatCollection.setCreatedBy(createdBy);
			 * presentingComplaintOralThroatCollection.setCreatedTime(
			 * createdTime);
			 * presentingComplaintOralThroatCollection.setId(null);
			 * presentingComplaintOralThroatCollection =
			 * presentingComplaintThroatRepository
			 * .save(presentingComplaintOralThroatCollection);
			 * transactionalManagementService.addResource(
			 * presentingComplaintOralThroatCollection.getId(),
			 * Resource.PC_THROAT, false); ESPresentingComplaintThroatDocument
			 * esPresentingComplaintThroatDocument = new
			 * ESPresentingComplaintThroatDocument();
			 * BeanUtil.map(presentingComplaintOralThroatCollection,
			 * esPresentingComplaintThroatDocument);
			 * esClinicalNotesService.addPCThroat(
			 * esPresentingComplaintThroatDocument); //
			 * noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 * 
			 * if (request.getPcThroat() != null &&
			 * !request.getPcThroat().isEmpty() && request.getGlobalPCThroat()
			 * != null) { Set<String> customPCOralThroats =
			 * compareGlobalElements(new
			 * HashSet<>(splitCSV(request.getPcThroat())), new
			 * HashSet<>(splitCSV(request.getGlobalPCThroat()))); for (String
			 * customPCOralThroat : customPCOralThroats) {
			 * 
			 * PresentingComplaintThroat presentingComplaintThroat = new
			 * PresentingComplaintThroat();
			 * presentingComplaintThroat.setPcThroat(customPCOralThroat);
			 * PresentingComplaintThroatCollection
			 * presentingComplaintOralThroatCollection = new
			 * PresentingComplaintThroatCollection();
			 * BeanUtil.map(presentingComplaintThroat,
			 * presentingComplaintOralThroatCollection);
			 * presentingComplaintOralThroatCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * presentingComplaintOralThroatCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * presentingComplaintOralThroatCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * presentingComplaintOralThroatCollection.setCreatedBy(createdBy);
			 * presentingComplaintOralThroatCollection.setCreatedTime(
			 * createdTime);
			 * presentingComplaintOralThroatCollection.setId(null);
			 * presentingComplaintOralThroatCollection =
			 * presentingComplaintThroatRepository
			 * .save(presentingComplaintOralThroatCollection);
			 * transactionalManagementService.addResource(
			 * presentingComplaintOralThroatCollection.getId(),
			 * Resource.PC_THROAT, false); ESPresentingComplaintThroatDocument
			 * esPresentingComplaintThroatDocument = new
			 * ESPresentingComplaintThroatDocument();
			 * BeanUtil.map(presentingComplaintOralThroatCollection,
			 * esPresentingComplaintThroatDocument);
			 * esClinicalNotesService.addPCThroat(
			 * esPresentingComplaintThroatDocument); //
			 * noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 * 
			 * if (request.getNeckExam() != null &&
			 * !request.getNeckExam().isEmpty() && request.getGlobalNeckExam()
			 * != null) { Set<String> customNeckExams =
			 * compareGlobalElements(new
			 * HashSet<>(splitCSV(request.getNeckExam())), new
			 * HashSet<>(splitCSV(request.getGlobalNeckExam()))); for (String
			 * customNeckExam : customNeckExams) {
			 * 
			 * NeckExamination neckExamination = new NeckExamination();
			 * neckExamination.setNeckExam(customNeckExam);
			 * NeckExaminationCollection neckExaminationCollection = new
			 * NeckExaminationCollection(); BeanUtil.map(neckExamination,
			 * neckExaminationCollection);
			 * neckExaminationCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * neckExaminationCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * neckExaminationCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * neckExaminationCollection.setCreatedBy(createdBy);
			 * neckExaminationCollection.setCreatedTime(createdTime);
			 * neckExaminationCollection.setId(null); neckExaminationCollection
			 * = neckExaminationRepository.save(neckExaminationCollection);
			 * transactionalManagementService.addResource(
			 * neckExaminationCollection.getId(), Resource.NECK_EXAM, false);
			 * ESNeckExaminationDocument esNeckExaminationDocument = new
			 * ESNeckExaminationDocument();
			 * BeanUtil.map(neckExaminationCollection,
			 * esNeckExaminationDocument);
			 * esClinicalNotesService.addNeckExam(esNeckExaminationDocument); //
			 * noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 * 
			 * if (request.getNoseExam() != null &&
			 * !request.getNoseExam().isEmpty() && request.getGlobalNoseExam()
			 * != null) { Set<String> customNoseExams =
			 * compareGlobalElements(new
			 * HashSet<>(splitCSV(request.getNoseExam())), new
			 * HashSet<>(splitCSV(request.getGlobalNoseExam()))); for (String
			 * customNoseExam : customNoseExams) {
			 * 
			 * NoseExamination noseExamination = new NoseExamination();
			 * noseExamination.setNoseExam(customNoseExam);
			 * NoseExaminationCollection noseExaminationCollection = new
			 * NoseExaminationCollection(); BeanUtil.map(noseExamination,
			 * noseExaminationCollection);
			 * noseExaminationCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * noseExaminationCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * noseExaminationCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * noseExaminationCollection.setCreatedBy(createdBy);
			 * noseExaminationCollection.setCreatedTime(createdTime);
			 * noseExaminationCollection.setId(null); noseExaminationCollection
			 * = noseExaminationRepository.save(noseExaminationCollection);
			 * transactionalManagementService.addResource(
			 * noseExaminationCollection.getId(), Resource.NOSE_EXAM, false);
			 * ESNeckExaminationDocument esNeckExaminationDocument = new
			 * ESNeckExaminationDocument();
			 * BeanUtil.map(noseExaminationCollection,
			 * esNeckExaminationDocument);
			 * esClinicalNotesService.addNeckExam(esNeckExaminationDocument); //
			 * noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 * 
			 * if (request.getEarsExam() != null &&
			 * !request.getEarsExam().isEmpty() && request.getGlobalEarsExam()
			 * != null) { Set<String> customEarsExams =
			 * compareGlobalElements(new
			 * HashSet<>(splitCSV(request.getEarsExam())), new
			 * HashSet<>(splitCSV(request.getGlobalEarsExam()))); for (String
			 * customEarsExam : customEarsExams) {
			 * 
			 * EarsExamination earsExamination = new EarsExamination();
			 * earsExamination.setEarsExam(customEarsExam);
			 * EarsExaminationCollection earsExaminationCollection = new
			 * EarsExaminationCollection(); BeanUtil.map(earsExamination,
			 * earsExaminationCollection);
			 * earsExaminationCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * earsExaminationCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * earsExaminationCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * earsExaminationCollection.setCreatedBy(createdBy);
			 * earsExaminationCollection.setCreatedTime(createdTime);
			 * earsExaminationCollection.setId(null); earsExaminationCollection
			 * = earsExaminationRepository.save(earsExaminationCollection);
			 * transactionalManagementService.addResource(
			 * earsExaminationCollection.getId(), Resource.EARS_EXAM, false);
			 * ESEarsExaminationDocument esEarsExaminationDocument = new
			 * ESEarsExaminationDocument();
			 * BeanUtil.map(earsExaminationCollection,
			 * esEarsExaminationDocument);
			 * esClinicalNotesService.addEarsExam(esEarsExaminationDocument); //
			 * noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 * 
			 * if (request.getEarsExam() != null &&
			 * !request.getEarsExam().isEmpty() && request.getGlobalEarsExam()
			 * != null) { Set<String> customEarsExams =
			 * compareGlobalElements(new
			 * HashSet<>(splitCSV(request.getEarsExam())), new
			 * HashSet<>(splitCSV(request.getGlobalEarsExam()))); for (String
			 * customEarsExam : customEarsExams) {
			 * 
			 * EarsExamination earsExamination = new EarsExamination();
			 * earsExamination.setEarsExam(customEarsExam);
			 * EarsExaminationCollection earsExaminationCollection = new
			 * EarsExaminationCollection(); BeanUtil.map(earsExamination,
			 * earsExaminationCollection);
			 * earsExaminationCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * earsExaminationCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * earsExaminationCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * earsExaminationCollection.setCreatedBy(createdBy);
			 * earsExaminationCollection.setCreatedTime(createdTime);
			 * earsExaminationCollection.setId(null); earsExaminationCollection
			 * = earsExaminationRepository.save(earsExaminationCollection);
			 * transactionalManagementService.addResource(
			 * earsExaminationCollection.getId(), Resource.EARS_EXAM, false);
			 * ESEarsExaminationDocument esEarsExaminationDocument = new
			 * ESEarsExaminationDocument();
			 * BeanUtil.map(earsExaminationCollection,
			 * esEarsExaminationDocument);
			 * esClinicalNotesService.addEarsExam(esEarsExaminationDocument); //
			 * noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 * 
			 * if (request.getIndirectLarygoscopyExam() != null &&
			 * !request.getIndirectLarygoscopyExam().isEmpty() &&
			 * request.getGlobalIndirectLarygoscopyExam() != null) { Set<String>
			 * customIndirectLagyroScopyExams = compareGlobalElements( new
			 * HashSet<>(splitCSV(request.getIndirectLarygoscopyExam())), new
			 * HashSet<>(splitCSV(request.getGlobalIndirectLarygoscopyExam())));
			 * for (String customIndirectLagyroScopyExam :
			 * customIndirectLagyroScopyExams) {
			 * 
			 * IndirectLarygoscopyExamination indirectLarygoscopyExamination =
			 * new IndirectLarygoscopyExamination();
			 * indirectLarygoscopyExamination.setIndirectLarygoscopyExam(
			 * customIndirectLagyroScopyExam);
			 * IndirectLarygoscopyExaminationCollection
			 * indirectLarygoscopyExaminationCollection = new
			 * IndirectLarygoscopyExaminationCollection();
			 * BeanUtil.map(indirectLarygoscopyExamination,
			 * indirectLarygoscopyExaminationCollection);
			 * indirectLarygoscopyExaminationCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * indirectLarygoscopyExaminationCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * indirectLarygoscopyExaminationCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * indirectLarygoscopyExaminationCollection.setCreatedBy(createdBy);
			 * indirectLarygoscopyExaminationCollection.setCreatedTime(
			 * createdTime);
			 * indirectLarygoscopyExaminationCollection.setId(null);
			 * indirectLarygoscopyExaminationCollection =
			 * indirectLarygoscopyExaminationRepository
			 * .save(indirectLarygoscopyExaminationCollection);
			 * transactionalManagementService.addResource(
			 * indirectLarygoscopyExaminationCollection.getId(),
			 * Resource.INDIRECT_LARYGOSCOPY_EXAM, false);
			 * ESIndirectLarygoscopyExaminationDocument
			 * esIndirectLarygoscopyExaminationDocument = new
			 * ESIndirectLarygoscopyExaminationDocument();
			 * BeanUtil.map(indirectLarygoscopyExaminationCollection,
			 * esIndirectLarygoscopyExaminationDocument);
			 * esClinicalNotesService.addIndirectLarygoscopyExam(
			 * esIndirectLarygoscopyExaminationDocument); //
			 * noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 * 
			 * if (request.getOralCavityThroatExam() != null &&
			 * !request.getOralCavityThroatExam().isEmpty() &&
			 * request.getGlobalOralCavityThroatExam() != null) { Set<String>
			 * customOralCavityThroatExams = compareGlobalElements( new
			 * HashSet<>(splitCSV(request.getOralCavityThroatExam())), new
			 * HashSet<>(splitCSV(request.getGlobalOralCavityThroatExam())));
			 * for (String customOralCavityThroatExam :
			 * customOralCavityThroatExams) {
			 * 
			 * OralCavityAndThroatExamination oralCavityAndThroatExamination =
			 * new OralCavityAndThroatExamination();
			 * oralCavityAndThroatExamination.setOralCavityThroatExam(
			 * customOralCavityThroatExam);
			 * OralCavityAndThroatExaminationCollection
			 * oralCavityAndThroatExaminationCollection = new
			 * OralCavityAndThroatExaminationCollection();
			 * BeanUtil.map(oralCavityAndThroatExamination,
			 * oralCavityAndThroatExaminationCollection);
			 * oralCavityAndThroatExaminationCollection.setDoctorId(new
			 * ObjectId(request.getDoctorId()));
			 * oralCavityAndThroatExaminationCollection.setLocationId(new
			 * ObjectId(request.getLocationId()));
			 * oralCavityAndThroatExaminationCollection.setHospitalId(new
			 * ObjectId(request.getHospitalId()));
			 * oralCavityAndThroatExaminationCollection.setCreatedBy(createdBy);
			 * oralCavityAndThroatExaminationCollection.setCreatedTime(
			 * createdTime);
			 * oralCavityAndThroatExaminationCollection.setId(null);
			 * oralCavityAndThroatExaminationCollection =
			 * oralCavityThroatExaminationRepository
			 * .save(oralCavityAndThroatExaminationCollection);
			 * transactionalManagementService.addResource(
			 * oralCavityAndThroatExaminationCollection.getId(),
			 * Resource.ORAL_CAVITY_THROAT_EXAM, false);
			 * ESOralCavityAndThroatExaminationDocument
			 * esOralCavityAndThroatExaminationDocument = new
			 * ESOralCavityAndThroatExaminationDocument();
			 * BeanUtil.map(oralCavityAndThroatExaminationCollection,
			 * esOralCavityAndThroatExaminationDocument);
			 * esClinicalNotesService.addOralCavityThroatExam(
			 * esOralCavityAndThroatExaminationDocument); //
			 * noteIds.add(notesCollection.getId());
			 * 
			 * } }
			 */
			//
			// clinicalNotesCollection.setComplaints(complaintIds);
			// clinicalNotesCollection.setInvestigations(investigationIds);
			// clinicalNotesCollection.setObservations(observationIds);
			// clinicalNotesCollection.setDiagnoses(diagnosisIds);
			// clinicalNotesCollection.setNotes(noteIds);
			if (request.getDiagrams() == null) {
				clinicalNotesCollection.setDiagrams(null);
			} else {
				diagramIds = new ArrayList<ObjectId>();
				for (String diagramId : request.getDiagrams()) {
					diagramIds.add(new ObjectId(diagramId));
				}
			}
			clinicalNotesCollection
					.setUniqueEmrId(UniqueIdInitial.CLINICALNOTES.getInitial() + DPDoctorUtils.generateRandomId());
			clinicalNotesCollection.setCreatedTime(createdTime);
			clinicalNotesCollection = clinicalNotesRepository.save(clinicalNotesCollection);

			clinicalNotes = new ClinicalNotes();
			BeanUtil.map(clinicalNotesCollection, clinicalNotes);

			// if(complaintIds != null &&
			// !complaintIds.isEmpty())clinicalNotes.setComplaints(sortComplaints(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
			// Criteria("id").in(complaintIds))), ComplaintCollection.class,
			// Complaint.class).getMappedResults(), complaintIds));
			// if(investigationIds != null &&
			// !investigationIds.isEmpty())clinicalNotes.setInvestigations(sortInvestigations(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
			// Criteria("id").in(investigationIds))),
			// InvestigationCollection.class,
			// Investigation.class).getMappedResults(),investigationIds));
			// if(observationIds != null &&
			// !observationIds.isEmpty())clinicalNotes.setObservations(sortObservations(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
			// Criteria("id").in(observationIds))), ObservationCollection.class,
			// Observation.class).getMappedResults(), observationIds));
			// if(diagnosisIds != null &&
			// !diagnosisIds.isEmpty())clinicalNotes.setDiagnoses(sortDiagnoses(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
			// Criteria("id").in(diagnosisIds))), DiagnosisCollection.class,
			// Diagnoses.class).getMappedResults(), diagnosisIds));
			// if(noteIds != null &&
			// !noteIds.isEmpty())clinicalNotes.setNotes(sortNotes(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
			// Criteria("id").in(noteIds))), NotesCollection.class,
			// Notes.class).getMappedResults(), noteIds));
			if (diagramIds != null && !diagramIds.isEmpty())
				clinicalNotes
						.setDiagrams(
								sortDiagrams(
										mongoTemplate.aggregate(
												Aggregation.newAggregation(
														Aggregation.match(new Criteria("id").in(diagramIds))),
												DiagramsCollection.class, Diagram.class).getMappedResults(),
										diagramIds));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding Clinical notes",
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return clinicalNotes;
	}

	@Override
	@Transactional
	public ClinicalNotes getNotesById(String id, ObjectId visitId) {
		ClinicalNotes clinicalNote = null;
		try {
			ClinicalNotesCollection clinicalNotesCollection = clinicalNotesRepository.findOne(new ObjectId(id));
			if (clinicalNotesCollection != null) {
				clinicalNote = new ClinicalNotes();
				BeanUtil.map(clinicalNotesCollection, clinicalNote);
				if (!DPDoctorUtils.anyStringEmpty(clinicalNotesCollection.getAppointmentId())) {
					AppointmentCollection appointmentCollection = appointmentRepository
							.findByAppointmentId(clinicalNotesCollection.getAppointmentId());
					Appointment appointment = new Appointment();
					BeanUtil.map(appointmentCollection, appointment);
					clinicalNote.setAppointmentRequest(appointment);
				}

				// if(clinicalNotesCollection.getComplaints() != null &&
				// !clinicalNotesCollection.getComplaints().isEmpty())
				// clinicalNote.setComplaints(sortComplaints(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
				// Criteria("id").in(clinicalNotesCollection.getComplaints()))),
				// ComplaintCollection.class,
				// Complaint.class).getMappedResults(),
				// clinicalNotesCollection.getComplaints()));
				// if(clinicalNotesCollection.getInvestigations() != null &&
				// !clinicalNotesCollection.getInvestigations().isEmpty())
				// clinicalNote.setInvestigations(sortInvestigations(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
				// Criteria("id").in(clinicalNotesCollection.getInvestigations()))),
				// InvestigationCollection.class,
				// Investigation.class).getMappedResults(),
				// clinicalNotesCollection.getInvestigations()));
				// if(clinicalNotesCollection.getObservations() != null &&
				// !clinicalNotesCollection.getObservations().isEmpty())
				// clinicalNote.setObservations(sortObservations(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
				// Criteria("id").in(clinicalNotesCollection.getObservations()))),
				// ObservationCollection.class,
				// Observation.class).getMappedResults(),
				// clinicalNotesCollection.getObservations()));
				// if(clinicalNotesCollection.getDiagnoses() != null &&
				// !clinicalNotesCollection.getDiagnoses().isEmpty())
				// clinicalNote.setDiagnoses(sortDiagnoses(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
				// Criteria("id").in(clinicalNotesCollection.getDiagnoses()))),
				// DiagnosisCollection.class,
				// Diagnoses.class).getMappedResults(),
				// clinicalNotesCollection.getDiagnoses()));
				// if(clinicalNotesCollection.getNotes() != null &&
				// !clinicalNotesCollection.getNotes().isEmpty())
				// clinicalNote.setNotes(sortNotes(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
				// Criteria("id").in(clinicalNotesCollection.getNotes()))),
				// NotesCollection.class, Notes.class).getMappedResults(),
				// clinicalNotesCollection.getNotes()));
				if (clinicalNotesCollection.getDiagrams() != null && !clinicalNotesCollection.getDiagrams().isEmpty())
					clinicalNote.setDiagrams(sortDiagrams(mongoTemplate.aggregate(Aggregation.newAggregation(
							Aggregation.match(new Criteria("id").in(clinicalNotesCollection.getDiagrams()))),

							DiagramsCollection.class, Diagram.class).getMappedResults(),
							clinicalNotesCollection.getDiagrams()));

				if (DPDoctorUtils.anyStringEmpty(visitId)) {
					PatientVisitCollection patientVisitCollection = patientVisitRepository
							.findByClinialNotesId(clinicalNotesCollection.getId());
					if (patientVisitCollection != null)
						clinicalNote.setVisitId(patientVisitCollection.getId().toString());
				} else {
					clinicalNote.setVisitId(visitId.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);

			try {
				mailService.sendExceptionMail("Backend Business Exception :: While getting notes for id:" + id,
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return clinicalNote;
	}

	@Override
	@Transactional
	public ClinicalNotes editNotes(ClinicalNotesEditRequest request) {
		ClinicalNotes clinicalNotes = null;
		List<ObjectId> diagnosisIds = null;
		List<ObjectId> diagramIds = null;
		Appointment appointment = null;

		try {
			// save clinical notes.
			if (request.getAppointmentRequest() != null) {
				appointment = addNotesAppointment(request.getAppointmentRequest());
			}

			ClinicalNotesCollection clinicalNotesCollection = new ClinicalNotesCollection();
			if (appointment != null) {
				request.setAppointmentId(appointment.getAppointmentId());
				request.setTime(appointment.getTime());
				request.setFromDate(appointment.getFromDate());
			}
			BeanUtil.map(request, clinicalNotesCollection);
			ClinicalNotesCollection oldClinicalNotesCollection = clinicalNotesRepository
					.findOne(clinicalNotesCollection.getId());

			diagnosisIds = new ArrayList<ObjectId>();
			if (request.getDiagnoses() != null && !request.getDiagnoses().isEmpty()) {
				for (Diagnoses diagnosis : request.getDiagnoses()) {
					if (!DPDoctorUtils.anyStringEmpty(diagnosis.getId())) {
						diagnosisIds.add(new ObjectId(diagnosis.getId()));
					}
				}
			}

			clinicalNotesCollection.setDiagnoses(diagnosisIds);
			if (request.getDiagrams() == null) {
				clinicalNotesCollection.setDiagrams(diagramIds);
			} else {
				diagramIds = new ArrayList<ObjectId>();
				for (String diagramId : request.getDiagrams()) {
					diagramIds.add(new ObjectId(diagramId));
				}
			}

			clinicalNotesCollection.setCreatedTime(oldClinicalNotesCollection.getCreatedTime());
			clinicalNotesCollection.setCreatedBy(oldClinicalNotesCollection.getCreatedBy());
			clinicalNotesCollection.setDiscarded(oldClinicalNotesCollection.getDiscarded());
			clinicalNotesCollection.setInHistory(oldClinicalNotesCollection.isInHistory());
			clinicalNotesCollection.setUpdatedTime(new Date());
			clinicalNotesCollection.setUniqueEmrId(oldClinicalNotesCollection.getUniqueEmrId());
			clinicalNotesCollection = clinicalNotesRepository.save(clinicalNotesCollection);

			clinicalNotes = new ClinicalNotes();
			BeanUtil.map(clinicalNotesCollection, clinicalNotes);

			if (diagramIds != null && !diagramIds.isEmpty())
				clinicalNotes
						.setDiagrams(
								sortDiagrams(
										mongoTemplate.aggregate(
												Aggregation.newAggregation(
														Aggregation.match(new Criteria("id").in(diagramIds))),
												DiagramsCollection.class, Diagram.class).getMappedResults(),
										diagramIds));

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While editing clinical notes",
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return clinicalNotes;

	}

	@Override
	@Transactional
	public ClinicalNotes deleteNote(String id, Boolean discarded) {
		ClinicalNotes response = null;
		try {
			ClinicalNotesCollection clinicalNotes = clinicalNotesRepository.findOne(new ObjectId(id));
			if (clinicalNotes != null) {
				clinicalNotes.setDiscarded(discarded);
				clinicalNotes.setUpdatedTime(new Date());
				clinicalNotes = clinicalNotesRepository.save(clinicalNotes);
				response = new ClinicalNotes();
				BeanUtil.map(clinicalNotes, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While deleting note for id:" + id,
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public List<ClinicalNotes> getClinicalNotes(int page, int size, String doctorId, String locationId,
			String hospitalId, String patientId, String updatedTime, Boolean isOTPVerified, Boolean discarded,
			Boolean inHistory) {
		List<ClinicalnoteLookupBean> clinicalNotesCollections = null;
		List<ClinicalNotes> clinicalNotes = null;
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

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp)).and("patientId")
					.is(patientObjectId);
			if (!discarded)
				criteria.and("discarded").is(discarded);
			if (inHistory)
				criteria.and("inHistory").is(inHistory);

			if (!isOTPVerified) {
				if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
					criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
				if (!DPDoctorUtils.anyStringEmpty(doctorId))
					criteria.and("doctorId").is(doctorObjectId);
			}

			Aggregation aggregation = null;

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId", "appointmentRequest"),
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$appointmentRequest").append("preserveNullAndEmptyArrays",
										true))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId",
								"appointmentRequest"),
						new CustomAggregationOperation(
								new BasicDBObject("$unwind",
										new BasicDBObject("path", "$appointmentRequest")
												.append("preserveNullAndEmptyArrays", true))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<ClinicalnoteLookupBean> aggregationResults = mongoTemplate.aggregate(aggregation,
					ClinicalNotesCollection.class, ClinicalnoteLookupBean.class);
			clinicalNotesCollections = aggregationResults.getMappedResults();

			if (clinicalNotesCollections != null && !clinicalNotesCollections.isEmpty()) {
				clinicalNotes = new ArrayList<ClinicalNotes>();
				for (ClinicalnoteLookupBean clinicalNotesCollection : clinicalNotesCollections) {
					ClinicalNotes clinicalNote = getClinicalNote(clinicalNotesCollection);
					clinicalNotes.add(clinicalNote);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(" Error Occurred While Getting Clinical Notes");
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While getting clinical notes",
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Clinical Notes");
		}
		return clinicalNotes;
	}

	public ClinicalNotes getClinicalNote(ClinicalnoteLookupBean clinicalNotesCollection) {
		ClinicalNotes clinicalNote = new ClinicalNotes();
		BeanUtil.map(clinicalNotesCollection, clinicalNote);
		// if(clinicalNotesCollection.getComplaints() != null &&
		// !clinicalNotesCollection.getComplaints().isEmpty())
		// clinicalNote.setComplaints(sortComplaints(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
		// Criteria("id").in(clinicalNotesCollection.getComplaints()))),
		// ComplaintCollection.class, Complaint.class).getMappedResults(),
		// clinicalNotesCollection.getComplaints()));
		// if(clinicalNotesCollection.getInvestigations() != null &&
		// !clinicalNotesCollection.getInvestigations().isEmpty())
		// clinicalNote.setInvestigations(sortInvestigations(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
		// Criteria("id").in(clinicalNotesCollection.getInvestigations()))),
		// InvestigationCollection.class,
		// Investigation.class).getMappedResults(),
		// clinicalNotesCollection.getInvestigations()));
		// if(clinicalNotesCollection.getObservations() != null &&
		// !clinicalNotesCollection.getObservations().isEmpty())
		// clinicalNote.setObservations(sortObservations(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
		// Criteria("id").in(clinicalNotesCollection.getObservations()))),
		// ObservationCollection.class, Observation.class).getMappedResults(),
		// clinicalNotesCollection.getObservations()));
		// if(clinicalNotesCollection.getDiagnoses() != null &&
		// !clinicalNotesCollection.getDiagnoses().isEmpty())
		// clinicalNote.setDiagnoses(sortDiagnoses(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
		// Criteria("id").in(clinicalNotesCollection.getDiagnoses()))),
		// DiagnosisCollection.class, Diagnoses.class).getMappedResults(),
		// clinicalNotesCollection.getDiagnoses()));
		// if(clinicalNotesCollection.getNotes() != null &&
		// !clinicalNotesCollection.getNotes().isEmpty())
		// clinicalNote.setNotes(sortNotes(mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(new
		// Criteria("id").in(clinicalNotesCollection.getNotes()))),
		// NotesCollection.class, Notes.class).getMappedResults(),
		// clinicalNotesCollection.getNotes()));
		if (clinicalNotesCollection.getDiagrams() != null && !clinicalNotesCollection.getDiagrams().isEmpty())
			clinicalNote
					.setDiagrams(
							sortDiagrams(
									mongoTemplate
											.aggregate(
													Aggregation.newAggregation(Aggregation.match(new Criteria("id")
															.in(clinicalNotesCollection.getDiagrams()))),
													DiagramsCollection.class, Diagram.class)
											.getMappedResults(),
									clinicalNotesCollection.getDiagrams()));

		PatientVisitCollection patientVisitCollection = patientVisitRepository
				.findByClinialNotesId(clinicalNotesCollection.getId());
		if (patientVisitCollection != null)
			clinicalNote.setVisitId(patientVisitCollection.getId().toString());

		return clinicalNote;
	}

	@Override
	@Transactional
	public Complaint addEditComplaint(Complaint complaint) {
		try {
			ComplaintCollection complaintCollection = new ComplaintCollection();
			BeanUtil.map(complaint, complaintCollection);
			if (DPDoctorUtils.anyStringEmpty(complaintCollection.getId())) {
				complaintCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(complaintCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(complaintCollection.getDoctorId());
					if (userCollection != null) {
						complaintCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					complaintCollection.setCreatedBy("ADMIN");
				}
			} else {
				ComplaintCollection oldComplaintCollection = complaintRepository.findOne(complaintCollection.getId());
				complaintCollection.setCreatedBy(oldComplaintCollection.getCreatedBy());
				complaintCollection.setCreatedTime(oldComplaintCollection.getCreatedTime());
				complaintCollection.setDiscarded(oldComplaintCollection.getDiscarded());
			}
			complaintCollection = complaintRepository.save(complaintCollection);

			BeanUtil.map(complaintCollection, complaint);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding/editing complaint",
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return complaint;
	}

	@Override
	@Transactional
	public Observation addEditObservation(Observation observation) {
		try {
			ObservationCollection observationCollection = new ObservationCollection();
			BeanUtil.map(observation, observationCollection);
			if (DPDoctorUtils.anyStringEmpty(observationCollection.getId())) {
				observationCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(observationCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(observationCollection.getDoctorId());
					if (userCollection != null) {
						observationCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					observationCollection.setCreatedBy("ADMIN");
				}
			} else {
				ObservationCollection oldObservationCollection = observationRepository
						.findOne(observationCollection.getId());
				observationCollection.setCreatedBy(oldObservationCollection.getCreatedBy());
				observationCollection.setCreatedTime(oldObservationCollection.getCreatedTime());
				observationCollection.setDiscarded(oldObservationCollection.getDiscarded());
			}
			observationCollection = observationRepository.save(observationCollection);

			BeanUtil.map(observationCollection, observation);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding/editing observation",
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return observation;
	}

	@Override
	@Transactional
	public ProvisionalDiagnosis addEditProvisionalDiagnosis(ProvisionalDiagnosis provisionalDiagnosis) {
		try {
			ProvisionalDiagnosisCollection provisionalDiagnosisCollection = new ProvisionalDiagnosisCollection();
			BeanUtil.map(provisionalDiagnosis, provisionalDiagnosisCollection);
			if (DPDoctorUtils.anyStringEmpty(provisionalDiagnosisCollection.getId())) {
				provisionalDiagnosisCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(provisionalDiagnosisCollection.getDoctorId())) {
					UserCollection userCollection = userRepository
							.findOne(provisionalDiagnosisCollection.getDoctorId());
					if (userCollection != null) {
						provisionalDiagnosisCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					provisionalDiagnosisCollection.setCreatedBy("ADMIN");
				}
			} else {
				ProvisionalDiagnosisCollection oldProvisionalDiagnosisCollection = provisionalDiagnosisRepository
						.findOne(provisionalDiagnosisCollection.getId());
				provisionalDiagnosisCollection.setCreatedBy(oldProvisionalDiagnosisCollection.getCreatedBy());
				provisionalDiagnosisCollection.setCreatedTime(oldProvisionalDiagnosisCollection.getCreatedTime());
				provisionalDiagnosisCollection.setDiscarded(oldProvisionalDiagnosisCollection.getDiscarded());
			}
			provisionalDiagnosisCollection = provisionalDiagnosisRepository.save(provisionalDiagnosisCollection);

			BeanUtil.map(provisionalDiagnosisCollection, provisionalDiagnosis);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail(
						"Backend Business Exception :: While adding/editing provisional diagnosis", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return provisionalDiagnosis;
	}

	@Override
	@Transactional
	public GeneralExam addEditGeneralExam(GeneralExam generalExam) {
		try {
			GeneralExamCollection generalExamCollection = new GeneralExamCollection();
			BeanUtil.map(generalExam, generalExamCollection);
			if (DPDoctorUtils.anyStringEmpty(generalExamCollection.getId())) {
				generalExamCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(generalExamCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(generalExamCollection.getDoctorId());
					if (userCollection != null) {
						generalExamCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					generalExamCollection.setCreatedBy("ADMIN");
				}
			} else {
				GeneralExamCollection oldGeneralExamCollection = generalExamRepository
						.findOne(generalExamCollection.getId());
				generalExamCollection.setCreatedBy(oldGeneralExamCollection.getCreatedBy());
				generalExamCollection.setCreatedTime(oldGeneralExamCollection.getCreatedTime());
				generalExamCollection.setDiscarded(oldGeneralExamCollection.getDiscarded());
			}
			generalExamCollection = generalExamRepository.save(generalExamCollection);

			BeanUtil.map(generalExamCollection, generalExam);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding/editing general examination",
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return generalExam;
	}

	@Override
	@Transactional
	public SystemExam addEditSystemExam(SystemExam systemExam) {
		try {
			SystemExamCollection systemExamCollection = new SystemExamCollection();
			BeanUtil.map(systemExam, systemExamCollection);
			if (DPDoctorUtils.anyStringEmpty(systemExamCollection.getId())) {
				systemExamCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(systemExamCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(systemExamCollection.getDoctorId());
					if (userCollection != null) {
						systemExamCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					systemExamCollection.setCreatedBy("ADMIN");
				}
			} else {
				SystemExamCollection oldSystemExamCollection = systemExamRepository
						.findOne(systemExamCollection.getId());
				systemExamCollection.setCreatedBy(oldSystemExamCollection.getCreatedBy());
				systemExamCollection.setCreatedTime(oldSystemExamCollection.getCreatedTime());
				systemExamCollection.setDiscarded(oldSystemExamCollection.getDiscarded());
			}
			systemExamCollection = systemExamRepository.save(systemExamCollection);

			BeanUtil.map(systemExamCollection, systemExam);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail(
						"Backend Business Exception :: While adding/editing systematic examination", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return systemExam;
	}

	@Override
	@Transactional
	public MenstrualHistory addEditMenstrualHistory(MenstrualHistory menstrualHistory) {
		try {
			MenstrualHistoryCollection menstrualHistoryCollection = new MenstrualHistoryCollection();
			BeanUtil.map(menstrualHistory, menstrualHistoryCollection);
			if (DPDoctorUtils.anyStringEmpty(menstrualHistoryCollection.getId())) {
				menstrualHistoryCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(menstrualHistoryCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(menstrualHistoryCollection.getDoctorId());
					if (userCollection != null) {
						menstrualHistoryCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					menstrualHistoryCollection.setCreatedBy("ADMIN");
				}
			} else {
				MenstrualHistoryCollection oldMenstrualHistoryCollection = menstrualHistoryRepository
						.findOne(menstrualHistoryCollection.getId());
				menstrualHistoryCollection.setCreatedBy(oldMenstrualHistoryCollection.getCreatedBy());
				menstrualHistoryCollection.setCreatedTime(oldMenstrualHistoryCollection.getCreatedTime());
				menstrualHistoryCollection.setDiscarded(oldMenstrualHistoryCollection.getDiscarded());
			}
			menstrualHistoryCollection = menstrualHistoryRepository.save(menstrualHistoryCollection);

			BeanUtil.map(menstrualHistoryCollection, menstrualHistory);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding/editing menstrual history",
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return menstrualHistory;
	}

	@Override
	@Transactional
	public PresentComplaint addEditPresentComplaint(PresentComplaint presentComplaint) {
		try {
			PresentComplaintCollection presentComplaintCollection = new PresentComplaintCollection();
			BeanUtil.map(presentComplaint, presentComplaintCollection);
			if (DPDoctorUtils.anyStringEmpty(presentComplaintCollection.getId())) {
				presentComplaintCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(presentComplaintCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(presentComplaintCollection.getDoctorId());
					if (userCollection != null) {
						presentComplaintCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					presentComplaintCollection.setCreatedBy("ADMIN");
				}
			} else {
				PresentComplaintCollection oldPresentComplaintCollection = presentComplaintRepository
						.findOne(presentComplaintCollection.getId());
				presentComplaintCollection.setCreatedBy(oldPresentComplaintCollection.getCreatedBy());
				presentComplaintCollection.setCreatedTime(oldPresentComplaintCollection.getCreatedTime());
				presentComplaintCollection.setDiscarded(oldPresentComplaintCollection.getDiscarded());
			}
			presentComplaintCollection = presentComplaintRepository.save(presentComplaintCollection);

			BeanUtil.map(presentComplaintCollection, presentComplaint);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding/editing present complaint",
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return presentComplaint;
	}

	@Override
	@Transactional
	public PresentComplaintHistory addEditPresentComplaintHistory(PresentComplaintHistory presentComplaintHistory) {
		try {
			PresentComplaintHistoryCollection presentComplaintHistoryCollection = new PresentComplaintHistoryCollection();
			BeanUtil.map(presentComplaintHistory, presentComplaintHistoryCollection);
			if (DPDoctorUtils.anyStringEmpty(presentComplaintHistoryCollection.getId())) {
				presentComplaintHistoryCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(presentComplaintHistoryCollection.getDoctorId())) {
					UserCollection userCollection = userRepository
							.findOne(presentComplaintHistoryCollection.getDoctorId());
					if (userCollection != null) {
						presentComplaintHistoryCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					presentComplaintHistoryCollection.setCreatedBy("ADMIN");
				}
			} else {
				PresentComplaintHistoryCollection oldPresentComplaintHistoryCollection = presentComplaintHistoryRepository
						.findOne(presentComplaintHistoryCollection.getId());
				presentComplaintHistoryCollection.setCreatedBy(oldPresentComplaintHistoryCollection.getCreatedBy());
				presentComplaintHistoryCollection.setCreatedTime(oldPresentComplaintHistoryCollection.getCreatedTime());
				presentComplaintHistoryCollection.setDiscarded(oldPresentComplaintHistoryCollection.getDiscarded());
			}
			presentComplaintHistoryCollection = presentComplaintHistoryRepository
					.save(presentComplaintHistoryCollection);

			BeanUtil.map(presentComplaintHistoryCollection, presentComplaintHistory);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail(
						"Backend Business Exception :: While adding/editing present complaint history", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return presentComplaintHistory;
	}

	@Override
	@Transactional
	public ObstetricHistory addEditObstetricHistory(ObstetricHistory obstetricHistory) {
		try {
			ObstetricHistoryCollection obstetricHistoryCollection = new ObstetricHistoryCollection();
			BeanUtil.map(obstetricHistory, obstetricHistoryCollection);
			if (DPDoctorUtils.anyStringEmpty(obstetricHistoryCollection.getId())) {
				obstetricHistoryCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(obstetricHistoryCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(obstetricHistoryCollection.getDoctorId());
					if (userCollection != null) {
						obstetricHistoryCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					obstetricHistoryCollection.setCreatedBy("ADMIN");
				}
			} else {
				ObstetricHistoryCollection oldObstetricHistoryCollection = obstetricHistoryRepository
						.findOne(obstetricHistoryCollection.getId());
				obstetricHistoryCollection.setCreatedBy(oldObstetricHistoryCollection.getCreatedBy());
				obstetricHistoryCollection.setCreatedTime(oldObstetricHistoryCollection.getCreatedTime());
				obstetricHistoryCollection.setDiscarded(oldObstetricHistoryCollection.getDiscarded());
			}
			obstetricHistoryCollection = obstetricHistoryRepository.save(obstetricHistoryCollection);

			BeanUtil.map(obstetricHistoryCollection, obstetricHistory);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding/editing obstetrics history",
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return obstetricHistory;
	}

	@Override
	@Transactional
	public Investigation addEditInvestigation(Investigation investigation) {
		try {
			InvestigationCollection investigationCollection = new InvestigationCollection();
			BeanUtil.map(investigation, investigationCollection);
			if (DPDoctorUtils.anyStringEmpty(investigationCollection.getId())) {
				investigationCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(investigationCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(investigationCollection.getDoctorId());
					if (userCollection != null) {
						investigationCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					investigationCollection.setCreatedBy("ADMIN");
				}
			} else {
				InvestigationCollection oldInvestigationCollection = investigationRepository
						.findOne(investigationCollection.getId());
				investigationCollection.setCreatedBy(oldInvestigationCollection.getCreatedBy());
				investigationCollection.setCreatedTime(oldInvestigationCollection.getCreatedTime());
				investigationCollection.setDiscarded(oldInvestigationCollection.getDiscarded());
			}
			investigationCollection = investigationRepository.save(investigationCollection);

			BeanUtil.map(investigationCollection, investigation);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding/editing investigation",
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return investigation;
	}

	@Override
	@Transactional
	public Diagnoses addEditDiagnosis(Diagnoses diagnosis) {
		try {
			DiagnosisCollection diagnosisCollection = new DiagnosisCollection();
			BeanUtil.map(diagnosis, diagnosisCollection);
			if (DPDoctorUtils.anyStringEmpty(diagnosisCollection.getId())) {
				diagnosisCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(diagnosisCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(diagnosisCollection.getDoctorId());
					if (userCollection != null) {
						diagnosisCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					diagnosisCollection.setCreatedBy("ADMIN");
				}
			} else {
				DiagnosisCollection oldDiagnosisCollection = diagnosisRepository.findOne(diagnosisCollection.getId());
				diagnosisCollection.setCreatedBy(oldDiagnosisCollection.getCreatedBy());
				diagnosisCollection.setCreatedTime(oldDiagnosisCollection.getCreatedTime());
				diagnosisCollection.setDiscarded(oldDiagnosisCollection.getDiscarded());
			}
			diagnosisCollection = diagnosisRepository.save(diagnosisCollection);

			BeanUtil.map(diagnosisCollection, diagnosis);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding/editing diagnosis",
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return diagnosis;
	}

	@Override
	@Transactional
	public Notes addEditNotes(Notes notes) {
		try {
			NotesCollection notesCollection = new NotesCollection();
			BeanUtil.map(notes, notesCollection);
			if (DPDoctorUtils.anyStringEmpty(notesCollection.getId())) {
				notesCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(notesCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(notesCollection.getDoctorId());
					if (userCollection != null) {
						notesCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					notesCollection.setCreatedBy("ADMIN");
				}
			} else {
				NotesCollection oldNotesCollection = notesRepository.findOne(notesCollection.getId());
				notesCollection.setCreatedBy(oldNotesCollection.getCreatedBy());
				notesCollection.setCreatedTime(oldNotesCollection.getCreatedTime());
				notesCollection.setDiscarded(oldNotesCollection.getDiscarded());
			}
			notesCollection = notesRepository.save(notesCollection);

			BeanUtil.map(notesCollection, notes);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding/editing notes",
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return notes;
	}

	@Override
	@Transactional
	public Diagram addEditDiagram(Diagram diagram) {
		try {
			if (diagram.getDiagram() != null) {
				String path = "clinicalNotes" + File.separator + "diagrams";
				diagram.getDiagram().setFileName(diagram.getDiagram().getFileName() + new Date().getTime());
				ImageURLResponse imageURLResponse = fileManager.saveImageAndReturnImageUrl(diagram.getDiagram(), path,
						false);
				diagram.setDiagramUrl(imageURLResponse.getImageUrl());

			}
			DiagramsCollection diagramsCollection = new DiagramsCollection();
			BeanUtil.map(diagram, diagramsCollection);
			if (DPDoctorUtils.allStringsEmpty(diagram.getDoctorId()))
				diagramsCollection.setDoctorId(null);
			if (DPDoctorUtils.allStringsEmpty(diagram.getLocationId()))
				diagramsCollection.setLocationId(null);
			if (DPDoctorUtils.allStringsEmpty(diagram.getHospitalId()))
				diagramsCollection.setHospitalId(null);

			if (DPDoctorUtils.anyStringEmpty(diagramsCollection.getId())) {
				diagramsCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(diagramsCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(diagramsCollection.getDoctorId());
					if (userCollection != null) {
						diagramsCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					diagramsCollection.setCreatedBy("ADMIN");
				}
			} else {
				DiagramsCollection oldDiagramsCollection = diagramsRepository.findOne(diagramsCollection.getId());
				diagramsCollection.setCreatedBy(oldDiagramsCollection.getCreatedBy());
				diagramsCollection.setCreatedTime(oldDiagramsCollection.getCreatedTime());
				diagramsCollection.setDiscarded(oldDiagramsCollection.getDiscarded());
				if (diagram.getDiagram() == null) {
					diagramsCollection.setDiagramUrl(oldDiagramsCollection.getDiagramUrl());
					diagramsCollection.setFileExtension(oldDiagramsCollection.getFileExtension());
				}
			}
			diagramsCollection = diagramsRepository.save(diagramsCollection);
			BeanUtil.map(diagramsCollection, diagram);
			diagram.setDiagram(null);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While adding/editing diagram",
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return diagram;
	}

	@Override
	@Transactional
	public Complaint deleteComplaint(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		Complaint response = null;
		try {
			ComplaintCollection complaintCollection = complaintRepository.findOne(new ObjectId(id));
			if (complaintCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(complaintCollection.getDoctorId(),
						complaintCollection.getHospitalId(), complaintCollection.getLocationId())) {
					if (complaintCollection.getDoctorId().toString().equals(doctorId)
							&& complaintCollection.getHospitalId().toString().equals(hospitalId)
							&& complaintCollection.getLocationId().toString().equals(locationId)) {

						complaintCollection.setDiscarded(discarded);
						complaintCollection.setUpdatedTime(new Date());
						complaintRepository.save(complaintCollection);
						response = new Complaint();
						BeanUtil.map(complaintCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					complaintCollection.setDiscarded(discarded);
					complaintCollection.setUpdatedTime(new Date());
					complaintRepository.save(complaintCollection);
					response = new Complaint();
					BeanUtil.map(complaintCollection, response);
				}
			} else {
				logger.warn("Complaint not found!");
				throw new BusinessException(ServiceError.NoRecord, "Complaint not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While deleting complaint", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Observation deleteObservation(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		Observation response = null;
		try {
			ObservationCollection observationCollection = observationRepository.findOne(new ObjectId(id));
			if (observationCollection != null) {
				if (DPDoctorUtils.anyStringEmpty(observationCollection.getDoctorId(),
						observationCollection.getHospitalId(), observationCollection.getLocationId())) {
					if (observationCollection.getDoctorId().toString().equals(doctorId)
							&& observationCollection.getHospitalId().toString().equals(hospitalId)
							&& observationCollection.getLocationId().toString().equals(locationId)) {
						observationCollection.setDiscarded(discarded);
						observationCollection.setUpdatedTime(new Date());
						observationRepository.save(observationCollection);
						response = new Observation();
						BeanUtil.map(observationCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					observationCollection.setDiscarded(discarded);
					observationCollection.setUpdatedTime(new Date());
					observationRepository.save(observationCollection);
					response = new Observation();
					BeanUtil.map(observationCollection, response);
				}
			} else {
				logger.warn("Observation not found!");
				throw new BusinessException(ServiceError.NoRecord, "Observation not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While deleting observation",
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Investigation deleteInvestigation(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		Investigation response = null;
		try {
			InvestigationCollection investigationCollection = investigationRepository.findOne(new ObjectId(id));
			if (investigationCollection != null) {
				if (investigationCollection.getDoctorId() != null && investigationCollection.getHospitalId() != null
						&& investigationCollection.getLocationId() != null) {
					if (investigationCollection.getDoctorId().toString().equals(doctorId)
							&& investigationCollection.getHospitalId().toString().equals(hospitalId)
							&& investigationCollection.getLocationId().toString().equals(locationId)) {
						investigationCollection.setDiscarded(discarded);
						investigationCollection.setUpdatedTime(new Date());
						investigationRepository.save(investigationCollection);
						response = new Investigation();
						BeanUtil.map(investigationCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					investigationCollection.setDiscarded(discarded);
					investigationCollection.setUpdatedTime(new Date());
					investigationRepository.save(investigationCollection);
					response = new Investigation();
					BeanUtil.map(investigationCollection, response);
				}
			} else {
				logger.warn("Investigation not found!");
				throw new BusinessException(ServiceError.NoRecord, "Investigation not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While deleting investigation",
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Diagnoses deleteDiagnosis(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		Diagnoses response = null;
		try {
			DiagnosisCollection diagnosisCollection = diagnosisRepository.findOne(new ObjectId(id));
			if (diagnosisCollection != null) {
				if (diagnosisCollection.getDoctorId() != null && diagnosisCollection.getHospitalId() != null
						&& diagnosisCollection.getLocationId() != null) {
					if (diagnosisCollection.getDoctorId().toString().equals(doctorId)
							&& diagnosisCollection.getHospitalId().toString().equals(hospitalId)
							&& diagnosisCollection.getLocationId().toString().equals(locationId)) {
						diagnosisCollection.setDiscarded(discarded);
						diagnosisCollection.setUpdatedTime(new Date());
						diagnosisRepository.save(diagnosisCollection);
						response = new Diagnoses();
						BeanUtil.map(diagnosisCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					diagnosisCollection.setDiscarded(discarded);
					diagnosisCollection.setUpdatedTime(new Date());
					diagnosisRepository.save(diagnosisCollection);
					response = new Diagnoses();
					BeanUtil.map(diagnosisCollection, response);
				}

			} else {
				logger.warn("Diagnosis not found!");
				throw new BusinessException(ServiceError.NoRecord, "Diagnosis not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While deleting diagnosis", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Notes deleteNotes(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
		Notes response = null;
		try {
			NotesCollection notesCollection = notesRepository.findOne(new ObjectId(id));
			if (notesCollection != null) {
				if (notesCollection.getDoctorId() != null && notesCollection.getHospitalId() != null
						&& notesCollection.getLocationId() != null) {
					if (notesCollection.getDoctorId().toString().equals(doctorId)
							&& notesCollection.getHospitalId().toString().equals(hospitalId)
							&& notesCollection.getLocationId().toString().equals(locationId)) {
						notesCollection.setDiscarded(discarded);
						notesCollection.setUpdatedTime(new Date());
						notesRepository.save(notesCollection);
						response = new Notes();
						BeanUtil.map(notesCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					notesCollection.setDiscarded(discarded);
					notesCollection.setUpdatedTime(new Date());
					notesRepository.save(notesCollection);
					response = new Notes();
					BeanUtil.map(notesCollection, response);
				}

			} else {
				logger.warn("Notes not found!");
				throw new BusinessException(ServiceError.NoRecord, "Notes not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While deleting notes", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Diagram deleteDiagram(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
		Diagram response = null;
		try {
			DiagramsCollection diagramsCollection = diagramsRepository.findOne(new ObjectId(id));
			if (diagramsCollection != null) {
				if (diagramsCollection.getDoctorId() != null && diagramsCollection.getHospitalId() != null
						&& diagramsCollection.getLocationId() != null) {
					if (diagramsCollection.getDoctorId().toString().equals(doctorId)
							&& diagramsCollection.getHospitalId().toString().equals(hospitalId)
							&& diagramsCollection.getLocationId().toString().equals(locationId)) {
						diagramsCollection.setDiscarded(discarded);
						diagramsCollection.setUpdatedTime(new Date());
						diagramsRepository.save(diagramsCollection);
						response = new Diagram();
						BeanUtil.map(diagramsCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					diagramsCollection.setDiscarded(discarded);
					diagramsCollection.setUpdatedTime(new Date());
					diagramsRepository.save(diagramsCollection);
					response = new Diagram();
					BeanUtil.map(diagramsCollection, response);
				}

			} else {
				logger.warn("Diagram not found!");
				throw new BusinessException(ServiceError.NoRecord, "Diagram not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While deleting diagram", e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public Integer getClinicalNotesCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, boolean isOTPVerified) {
		Integer clinicalNotesCount = 0;
		try {
			Criteria criteria = new Criteria("discarded").is(false).and("patientId").is(patientObjectId);
			if (!isOTPVerified) {
				if (!DPDoctorUtils.anyStringEmpty(locationObjectId, hospitalObjectId))
					criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
				if (!DPDoctorUtils.anyStringEmpty(doctorObjectId))
					criteria.and("doctorId").is(doctorObjectId);
			}
			clinicalNotesCount = (int) mongoTemplate.count(new Query(criteria), ClinicalNotesCollection.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While getting clinical notes count",
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Clinical Notes Count");
		}
		return clinicalNotesCount;
	}

	@Override
	@Transactional
	public List<?> getClinicalItems(String type, String range, int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded, String searchTerm) {
		List<?> response = new ArrayList<Object>();

		switch (ClinicalItems.valueOf(type.toUpperCase())) {

		case COMPLAINTS: {

			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalComplaints(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomComplaints(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalComplaints(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}
		case INVESTIGATIONS: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalInvestigations(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomInvestigations(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			case BOTH:
				response = getCustomGlobalInvestigations(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}
		case OBSERVATIONS: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalObservations(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomObservations(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalObservations(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}
		case DIAGNOSIS: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalDiagnosis(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomDiagnosis(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalDiagnosis(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}
		case NOTES: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalNotes(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomNotes(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalNotes(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}
		case DIAGRAMS: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalDiagrams(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomDiagrams(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalDiagrams(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}

		case PRESENT_COMPLAINT: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalPresentComplaint(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomPresentComplaint(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			case BOTH:
				response = getCustomGlobalPresentComplaint(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}

		case HISTORY_OF_PRESENT_COMPLAINT: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalPresentComplaintHistory(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomPresentComplaintHistory(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			case BOTH:
				response = getCustomGlobalPresentComplaintHistory(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}

		case PROVISIONAL_DIAGNOSIS: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalProvisionalDiagnosis(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomProvisionalDiagnosis(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			case BOTH:
				response = getCustomGlobalProvisionalDiagnosis(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}

		case GENERAL_EXAMINATION: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalGeneralExam(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomGeneralExam(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalGeneralExam(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}

		case SYSTEMIC_EXAMINATION: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalSystemExam(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomSystemExam(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalSystemExam(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}

		case MENSTRUAL_HISTORY: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalMenstrualHistory(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomMenstrualHistory(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			case BOTH:
				response = getCustomGlobalMenstrualHistory(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}

		case OBSTETRIC_HISTORY: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalObstetricHistory(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomObstetricHistory(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			case BOTH:
				response = getCustomGlobalObstetricHistory(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}

		case INDICATION_OF_USG: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalIndicationOfUSG(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomIndicationOfUSG(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			case BOTH:
				response = getCustomGlobalIndicationOfUSG(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}

		case PA: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalPA(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomPA(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalPA(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}

		case PV: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalPV(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomPV(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalPV(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}
		case PS: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalPS(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomPS(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalPS(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}

		case ECG: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalECGDetails(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomECGDetails(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalECGDetails(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}
		case XRAY: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalXRayDetails(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomXRayDetails(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalXRayDetails(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}
		case ECHO: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalEcho(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomEcho(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalEcho(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}
		case HOLTER: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalHolter(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomHolter(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalHolter(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}
		case PROCEDURE_NOTE: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalProcedureNote(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomProcedureNote(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalProcedureNote(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}

		case PC_NOSE: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalPCNOse(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomPCNose(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalPCNOse(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}

		case PC_EARS: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalPCEars(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomPCEars(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalPCEars(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}

		case PC_THROAT: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalPCThroat(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomPCThroat(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalPCThroat(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}

		case NECK_EXAM: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalNeckExam(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomNeckExam(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalNeckExam(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}

		case NOSE_EXAM: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalNoseExam(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomNoseExam(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalNoseExam(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}

		case PC_ORAL_CAVITY: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalPCOralCavity(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomPCOralCavity(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalPCOralCavity(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}

		case EARS_EXAM: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalEarsExam(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomEarsExam(page, size, doctorId, locationId, hospitalId, updatedTime, discarded);
				break;
			case BOTH:
				response = getCustomGlobalEarsExam(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			default:
				break;
			}
			break;
		}

		case ORAL_CAVITY_THROAT_EXAM: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalOralCavityAndThroat(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomOralCavityAndThroatExam(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			case BOTH:
				response = getCustomGlobalOralCavityAndThroatExam(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}

		case INDIRECT_LAGYROSCOPY_EXAM: {
			switch (Range.valueOf(range.toUpperCase())) {

			case GLOBAL:
				response = getGlobalIndirectLarygoscopyExam(page, size, doctorId, updatedTime, discarded);
				break;
			case CUSTOM:
				response = getCustomIndirectLarygoscopyExam(page, size, doctorId, locationId, hospitalId, updatedTime,
						discarded);
				break;
			case BOTH:
				response = getCustomGlobalIndirectLarygoscopyExam(page, size, doctorId, locationId, hospitalId,
						updatedTime, discarded);
				break;
			default:
				break;
			}
			break;
		}

		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Complaint> getCustomGlobalComplaints(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Complaint> response = new ArrayList<Complaint>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<Complaint> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					ComplaintCollection.class, Complaint.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While getting custom global complaints",
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<Complaint> getGlobalComplaints(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<Complaint> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<Complaint> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page,
					size, updatedTime, discarded, null, null, specialities, null), ComplaintCollection.class,
					Complaint.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While Getting global complaints",
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting global complaints");
		}
		return response;
	}

	private List<Complaint> getCustomComplaints(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Complaint> response = null;
		try {
			AggregationResults<Complaint> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							ComplaintCollection.class, Complaint.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While getting custom complaints",
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Complaints");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Investigation> getCustomGlobalInvestigations(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Investigation> response = new ArrayList<Investigation>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<Investigation> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					InvestigationCollection.class, Investigation.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While getting custom global investigation",
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Investigation> getGlobalInvestigations(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<Investigation> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<Investigation> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities, null),
					InvestigationCollection.class, Investigation.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While getting global investigation",
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
		}
		return response;
	}

	private List<Investigation> getCustomInvestigations(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Investigation> response = null;
		boolean[] discards = new boolean[2];
		discards[0] = false;
		try {
			AggregationResults<Investigation> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							InvestigationCollection.class, Investigation.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			try {
				mailService.sendExceptionMail("Backend Business Exception :: While getting custom investigation",
						e.getMessage());
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Investigations");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Observation> getCustomGlobalObservations(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Observation> response = new ArrayList<Observation>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<Observation> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					ObservationCollection.class, Observation.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<Observation> getGlobalObservations(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<Observation> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<Observation> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities, null),
					ObservationCollection.class, Observation.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
		}
		return response;
	}

	private List<Observation> getCustomObservations(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Observation> response = null;
		try {
			AggregationResults<Observation> results = mongoTemplate
					.aggregate(DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, null), Observation.class, Observation.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Observations");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Diagnoses> getCustomGlobalDiagnosis(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Diagnoses> response = new ArrayList<Diagnoses>();

		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<Diagnoses> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					DiagnosisCollection.class, Diagnoses.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<Diagnoses> getGlobalDiagnosis(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<Diagnoses> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<Diagnoses> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page,
					size, updatedTime, discarded, null, null, specialities, null), DiagnosisCollection.class,
					Diagnoses.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
		}
		return response;
	}

	private List<Diagnoses> getCustomDiagnosis(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Diagnoses> response = null;
		try {
			AggregationResults<Diagnoses> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							DiagnosisCollection.class, Diagnoses.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagnosis");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Notes> getCustomGlobalNotes(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<Notes> response = new ArrayList<Notes>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<Notes> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					NotesCollection.class, Notes.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<Notes> getGlobalNotes(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
		List<Notes> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<Notes> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page,
					size, updatedTime, discarded, null, null, specialities, null), NotesCollection.class, Notes.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
		}
		return response;
	}

	private List<Notes> getCustomNotes(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<Notes> response = null;
		try {
			AggregationResults<Notes> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null, null),
					NotesCollection.class, Notes.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Notes");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Diagram> getCustomGlobalDiagrams(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Diagram> response = new ArrayList<Diagram>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<Diagram> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					DiagramsCollection.class, Diagram.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<Diagram> getGlobalDiagrams(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<Diagram> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<Diagram> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page,
					size, updatedTime, discarded, null, null, specialities, null), DiagramsCollection.class,
					Diagram.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
		}
		return response;
	}

	private List<Diagram> getCustomDiagrams(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<Diagram> response = null;
		try {
			AggregationResults<Diagram> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null, null),
					DiagramsCollection.class, Diagram.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Diagrams");
		}
		return response;
	}

	@Override
	@Transactional
	public void emailClinicalNotes(String clinicalNotesId, String doctorId, String locationId, String hospitalId,
			String emailAddress) {
		try {
			MailResponse mailResponse = createMailData(clinicalNotesId, doctorId, locationId, hospitalId);
			String body = mailBodyGenerator.generateEMREmailBody(mailResponse.getPatientName(),
					mailResponse.getDoctorName(), mailResponse.getClinicName(), mailResponse.getClinicAddress(),
					mailResponse.getMailRecordCreatedDate(), "Clinical Notes", "emrMailTemplate.vm");
			mailService.sendEmail(emailAddress, mailResponse.getDoctorName() + " sent you Clinical Notes", body,
					mailResponse.getMailAttachment());

			if (mailResponse.getMailAttachment() != null
					&& mailResponse.getMailAttachment().getFileSystemResource() != null)
				if (mailResponse.getMailAttachment().getFileSystemResource().getFile().exists())
					mailResponse.getMailAttachment().getFileSystemResource().getFile().delete();
		} catch (Exception e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

	}

	@Override
	@Transactional
	public MailResponse getClinicalNotesMailData(String clinicalNotesId, String doctorId, String locationId,
			String hospitalId) {
		return createMailData(clinicalNotesId, doctorId, locationId, hospitalId);
	}

	private MailResponse createMailData(String clinicalNotesId, String doctorId, String locationId, String hospitalId) {
		MailResponse response = null;
		ClinicalNotesCollection clinicalNotesCollection = null;
		MailAttachment mailAttachment = null;
		PatientCollection patient = null;
		UserCollection user = null;
		EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
		try {
			clinicalNotesCollection = clinicalNotesRepository.findOne(new ObjectId(clinicalNotesId));
			if (clinicalNotesCollection != null) {
				if (clinicalNotesCollection.getDoctorId() != null && clinicalNotesCollection.getHospitalId() != null
						&& clinicalNotesCollection.getLocationId() != null) {
					if (clinicalNotesCollection.getDoctorId().equals(doctorId)
							&& clinicalNotesCollection.getHospitalId().equals(hospitalId)
							&& clinicalNotesCollection.getLocationId().equals(locationId)) {

						user = userRepository.findOne(clinicalNotesCollection.getPatientId());
						patient = patientRepository.findByUserIdLocationIdAndHospitalId(
								clinicalNotesCollection.getPatientId(), clinicalNotesCollection.getLocationId(),
								clinicalNotesCollection.getHospitalId());

						emailTrackCollection.setDoctorId(new ObjectId(doctorId));
						emailTrackCollection.setHospitalId(new ObjectId(hospitalId));
						emailTrackCollection.setLocationId(new ObjectId(locationId));
						emailTrackCollection.setType(ComponentType.CLINICAL_NOTES.getType());
						emailTrackCollection.setSubject("Clinical Notes");
						if (user != null) {
							emailTrackCollection.setPatientName(user.getFirstName());
							emailTrackCollection.setPatientId(user.getId());
						}
						JasperReportResponse jasperReportResponse = createJasper(clinicalNotesCollection, patient, user,
								null, false, false, false, false, false, false, false, false, false);
						mailAttachment = new MailAttachment();
						mailAttachment.setAttachmentName(FilenameUtils.getName(jasperReportResponse.getPath()));
						mailAttachment.setFileSystemResource(jasperReportResponse.getFileSystemResource());
						UserCollection doctorUser = userRepository.findOne(new ObjectId(doctorId));
						LocationCollection locationCollection = locationRepository.findOne(new ObjectId(locationId));

						response = new MailResponse();
						response.setMailAttachment(mailAttachment);
						response.setDoctorName(doctorUser.getTitle() + " " + doctorUser.getFirstName());
						String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
								? locationCollection.getStreetAddress() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
										? locationCollection.getLandmarkDetails() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
										? locationCollection.getLocality() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
										? locationCollection.getCity() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
										? locationCollection.getState() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
										? locationCollection.getCountry() + ", " : "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
										? locationCollection.getPostalCode() : "");

						if (address.charAt(address.length() - 2) == ',') {
							address = address.substring(0, address.length() - 2);
						}
						response.setClinicAddress(address);
						response.setClinicName(locationCollection.getLocationName());
						SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
						sdf.setTimeZone(TimeZone.getTimeZone("IST"));
						response.setMailRecordCreatedDate(sdf.format(clinicalNotesCollection.getCreatedTime()));
						response.setPatientName(user.getFirstName());

						emailTackService.saveEmailTrack(emailTrackCollection);

					} else {
						logger.warn("Clinical Notes Id, doctorId, location Id, hospital Id does not match");
						throw new BusinessException(ServiceError.NotFound,
								"Clinical Notes Id, doctorId, location Id, hospital Id does not match");
					}
				}
			} else {
				logger.warn("Clinical Notes not found. Please check clinicalNotesId.");
				throw new BusinessException(ServiceError.NotFound,
						"Clinical Notes not found. Please check clinicalNotesId.");
			}
		} catch (BusinessException e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	public boolean containsIgnoreCase(String str, List<String> list) {
		if (list != null && !list.isEmpty())
			for (String i : list) {
				if (i.equalsIgnoreCase(str))
					return true;
			}
		return false;
	}

	@Override
	@Transactional
	public List<ClinicalNotes> getClinicalNotes(String patientId, int page, int size, String updatedTime,
			Boolean discarded) {
		List<ClinicalNotes> clinicalNotes = null;
		List<ClinicalnoteLookupBean> clinicalnoteLookupBeans = null;
		boolean[] discards = new boolean[2];
		discards[0] = false;

		boolean[] inHistorys = new boolean[2];
		inHistorys[0] = true;
		inHistorys[1] = false;
		Aggregation aggregation = null;

		try {
			if (discarded)
				discards[1] = true;

			long createdTimestamp = Long.parseLong(updatedTime);
			Criteria criteria = new Criteria("discarded").in(discards).and("updatedTime").gt(createdTimestamp)
					.and("inHistory").in(inHistorys);
			ObjectId patientObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				patientObjectId = new ObjectId(patientId);
				criteria = new Criteria("patientId").is(patientObjectId);
			}

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId", "appointmentRequest"),
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$appointmentRequest").append("preserveNullAndEmptyArrays",
										true))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));

			} else
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("appointment_cl", "appointmentId", "appointmentId",
								"appointmentRequest"),
						new CustomAggregationOperation(
								new BasicDBObject("$unwind",
										new BasicDBObject("path", "$appointmentRequest")
												.append("preserveNullAndEmptyArrays", true))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<ClinicalnoteLookupBean> aggregationResults = mongoTemplate.aggregate(aggregation,
					ClinicalNotesCollection.class, ClinicalnoteLookupBean.class);
			clinicalnoteLookupBeans = aggregationResults.getMappedResults();

			if (clinicalnoteLookupBeans != null && !clinicalnoteLookupBeans.isEmpty()) {
				clinicalNotes = new ArrayList<ClinicalNotes>();
				for (ClinicalnoteLookupBean clinicalnoteLookupBean : clinicalnoteLookupBeans) {
					ClinicalNotes clinicalNote = getClinicalNote(clinicalnoteLookupBean);
					clinicalNotes.add(clinicalNote);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return clinicalNotes;
	}

	@Override
	public String getClinicalNotesFile(String clinicalNotesId, Boolean showPH, Boolean showPLH, Boolean showFH,
			Boolean showDA, Boolean showUSG, Boolean isCustomPDF, Boolean showLMP, Boolean showEDD,
			Boolean showNoOfChildren) {
		String response = null;
		HistoryCollection historyCollection = null;
		try {
			ClinicalNotesCollection clinicalNotesCollection = clinicalNotesRepository
					.findOne(new ObjectId(clinicalNotesId));

			if (clinicalNotesCollection != null) {
				PatientCollection patient = patientRepository.findByUserIdLocationIdAndHospitalId(
						clinicalNotesCollection.getPatientId(), clinicalNotesCollection.getLocationId(),
						clinicalNotesCollection.getHospitalId());
				UserCollection user = userRepository.findOne(clinicalNotesCollection.getPatientId());
				if (showPH || showPLH || showFH || showDA) {
					historyCollection = historyRepository.findHistory(clinicalNotesCollection.getLocationId(),
							clinicalNotesCollection.getHospitalId(), clinicalNotesCollection.getPatientId());
				}
				JasperReportResponse jasperReportResponse = createJasper(clinicalNotesCollection, patient, user,
						historyCollection, showPH, showPLH, showFH, showDA, showUSG, isCustomPDF, showLMP, showEDD,
						showNoOfChildren);
				if (jasperReportResponse != null)
					response = getFinalImageURL(jasperReportResponse.getPath());
				if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
					if (jasperReportResponse.getFileSystemResource().getFile().exists())
						jasperReportResponse.getFileSystemResource().getFile().delete();
			} else {
				logger.warn("Patient Visit Id does not exist");
				throw new BusinessException(ServiceError.NotFound, "Patient Visit Id does not exist");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting Patient Visits PDF");
			throw new BusinessException(ServiceError.Unknown, "Error while getting Patient Visits PDF");
		}
		return response;
	}

	private JasperReportResponse createJasper(ClinicalNotesCollection clinicalNotesCollection,
			PatientCollection patient, UserCollection user, HistoryCollection historyCollection, Boolean showPH,

			Boolean showPLH, Boolean showFH, Boolean showDA, Boolean showUSG, Boolean isCustomPDF, Boolean showLMP,
			Boolean showEDD, Boolean showNoOfChildren) throws IOException, ParseException {

		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperReportResponse response = null;

		Boolean showTitle = false;
		PrintSettingsCollection printSettings = printSettingsRepository.getSettings(
				clinicalNotesCollection.getDoctorId(), clinicalNotesCollection.getLocationId(),
				clinicalNotesCollection.getHospitalId(), ComponentType.ALL.getType());

		if (printSettings == null) {
			printSettings = new PrintSettingsCollection();
			DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
			BeanUtil.map(defaultPrintSettings, printSettings);
		}

		parameters.put("observations", clinicalNotesCollection.getObservation());
		parameters.put("notes", clinicalNotesCollection.getNote());
		parameters.put("investigations", clinicalNotesCollection.getInvestigation());
		parameters.put("diagnosis", clinicalNotesCollection.getDiagnosis());
		parameters.put("complaints", clinicalNotesCollection.getComplaint());
		parameters.put("presentComplaint", clinicalNotesCollection.getPresentComplaint());
		parameters.put("presentComplaintHistory", clinicalNotesCollection.getPresentComplaintHistory());
		parameters.put("generalExam", clinicalNotesCollection.getGeneralExam());
		parameters.put("systemExam", clinicalNotesCollection.getSystemExam());
		parameters.put("noseExam", clinicalNotesCollection.getNoseExam());
		parameters.put("oralCavityThroatExam", clinicalNotesCollection.getOralCavityThroatExam());
		parameters.put("indirectLarygoscopyExam", clinicalNotesCollection.getIndirectLarygoscopyExam());
		parameters.put("neckExam", clinicalNotesCollection.getNeckExam());
		parameters.put("earsExam", clinicalNotesCollection.getEarsExam());
		parameters.put("pcNose", clinicalNotesCollection.getPcNose());
		parameters.put("pcOralCavity", clinicalNotesCollection.getPcOralCavity());
		parameters.put("pcThroat", clinicalNotesCollection.getPcThroat());
		parameters.put("pcEars", clinicalNotesCollection.getPcEars());
		parameters.put("menstrualHistory", clinicalNotesCollection.getMenstrualHistory());
		parameters.put("obstetricHistory", clinicalNotesCollection.getObstetricHistory());
		parameters.put("provisionalDiagnosis", clinicalNotesCollection.getProvisionalDiagnosis());
		if (!DPDoctorUtils.allStringsEmpty(clinicalNotesCollection.getPcNose())
				|| !DPDoctorUtils.allStringsEmpty(clinicalNotesCollection.getPcEars())
				|| !DPDoctorUtils.allStringsEmpty(clinicalNotesCollection.getPcOralCavity())
				|| !DPDoctorUtils.allStringsEmpty(clinicalNotesCollection.getPcThroat())) {
			parameters.put("ComplaintsTitle", "Complaints :");
			showTitle = true;
		}

		parameters.put("showPCTitle", showTitle);
		showTitle = false;
		if (!DPDoctorUtils.allStringsEmpty(clinicalNotesCollection.getEarsExam())
				|| !DPDoctorUtils.allStringsEmpty(clinicalNotesCollection.getNeckExam())
				|| !DPDoctorUtils.allStringsEmpty(clinicalNotesCollection.getIndirectLarygoscopyExam())
				|| !DPDoctorUtils.allStringsEmpty(clinicalNotesCollection.getOralCavityThroatExam())
				|| !DPDoctorUtils.allStringsEmpty(clinicalNotesCollection.getNoseExam())) {
			parameters.put("Examination", "Examination :");
			showTitle = true;
		}
		parameters.put("showExamTitle", showTitle);
		showTitle = false;
		if (!isCustomPDF || showUSG) {
			parameters.put("indicationOfUSG", clinicalNotesCollection.getIndicationOfUSG());
		}
		parameters.put("pv", clinicalNotesCollection.getPv());
		parameters.put("pa", clinicalNotesCollection.getPa());
		parameters.put("ps", clinicalNotesCollection.getPs());
		parameters.put("ecgDetails", clinicalNotesCollection.getEcgDetails());
		parameters.put("xRayDetails", clinicalNotesCollection.getxRayDetails());
		parameters.put("echo", clinicalNotesCollection.getEcho());
		parameters.put("holter", clinicalNotesCollection.getHolter());
		parameters.put("procedureNote", clinicalNotesCollection.getProcedureNote());
		parameters.put("personalHistoryTobacco", clinicalNotesCollection.getPersonalHistoryTobacco());
		parameters.put("personalHistoryAlcohol", clinicalNotesCollection.getPersonalHistoryAlcohol());
		parameters.put("personalHistorySmoking", clinicalNotesCollection.getPersonalHistorySmoking());
		parameters.put("personalHistoryDiet", clinicalNotesCollection.getPersonalHistoryDiet());
		parameters.put("personalHistoryOccupation", clinicalNotesCollection.getPersonalHistoryOccupation());
		parameters.put("generalHistoryDrugs", clinicalNotesCollection.getGeneralHistoryDrugs());
		parameters.put("generalHistoryMedicine", clinicalNotesCollection.getGeneralHistoryMedicine());
		parameters.put("generalHistoryAllergies", clinicalNotesCollection.getGeneralHistoryAllergies());
		parameters.put("generalHistorySurgical", clinicalNotesCollection.getGeneralHistorySurgical());
		parameters.put("pastHistory", clinicalNotesCollection.getPastHistory());
		parameters.put("familyHistory", clinicalNotesCollection.getFamilyHistory());
		parameters.put("painScale",
				 clinicalNotesCollection.getPainScale());
		if (clinicalNotesCollection.getLmp() != null && (!isCustomPDF || showLMP))
			parameters.put("lmp", new SimpleDateFormat("dd-MM-yyyy").format(clinicalNotesCollection.getLmp()));
		if (clinicalNotesCollection.getEdd() != null && (!isCustomPDF || showEDD))
			parameters.put("edd", new SimpleDateFormat("dd-MM-yyyy").format(clinicalNotesCollection.getEdd()));
		if ((!isCustomPDF || showNoOfChildren) && (clinicalNotesCollection.getNoOfMaleChildren() > 0
				|| clinicalNotesCollection.getNoOfFemaleChildren() > 0)) {
			parameters.put("noOfChildren", clinicalNotesCollection.getNoOfMaleChildren() + "|"
					+ clinicalNotesCollection.getNoOfFemaleChildren());
		}
		List<DBObject> diagramIds = new ArrayList<DBObject>();
		if (clinicalNotesCollection.getDiagrams() != null)
			for (ObjectId diagramId : clinicalNotesCollection.getDiagrams()) {
				DBObject diagram = new BasicDBObject();
				DiagramsCollection diagramsCollection = diagramsRepository.findOne(diagramId);
				if (diagramsCollection != null) {
					if (diagramsCollection.getDiagramUrl() != null) {
						diagram.put("url", getFinalImageURL(diagramsCollection.getDiagramUrl()));
					}
					diagram.put("tags", diagramsCollection.getTags());
					diagramIds.add(diagram);
				}
			}
		if (!diagramIds.isEmpty())
			parameters.put("diagrams", diagramIds);
		else
			parameters.put("diagrams", null);

		parameters.put("clinicalNotesId", clinicalNotesCollection.getId().toString());
		if (clinicalNotesCollection.getVitalSigns() != null) {
			String vitalSigns = null;

			String pulse = clinicalNotesCollection.getVitalSigns().getPulse();
			pulse = (pulse != null && !pulse.isEmpty() ? "Pulse: " + pulse.trim() + " " + VitalSignsUnit.PULSE.getUnit()
					: "");
			if (!DPDoctorUtils.allStringsEmpty(pulse))
				vitalSigns = pulse;

			String temp = clinicalNotesCollection.getVitalSigns().getTemperature();
			temp = (temp != null && !temp.isEmpty()
					? "Temperature: " + temp.trim() + " " + VitalSignsUnit.TEMPERATURE.getUnit() : "");
			if (!DPDoctorUtils.allStringsEmpty(temp)) {
				if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
					vitalSigns = vitalSigns + ",  " + temp;
				else
					vitalSigns = temp;
			}

			String breathing = clinicalNotesCollection.getVitalSigns().getBreathing();
			breathing = (breathing != null && !breathing.isEmpty()
					? "Breathing: " + breathing.trim() + " " + VitalSignsUnit.BREATHING.getUnit() : "");
			if (!DPDoctorUtils.allStringsEmpty(breathing)) {
				if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
					vitalSigns = vitalSigns + ",  " + breathing;
				else
					vitalSigns = breathing;
			}

			String weight = clinicalNotesCollection.getVitalSigns().getWeight();
			weight = (weight != null && !weight.isEmpty()
					? "Weight: " + weight.trim() + " " + VitalSignsUnit.WEIGHT.getUnit() : "");
			if (!DPDoctorUtils.allStringsEmpty(temp)) {
				if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
					vitalSigns = vitalSigns + ",  " + weight;
				else
					vitalSigns = weight;
			}

			String bloodPressure = "";
			if (clinicalNotesCollection.getVitalSigns().getBloodPressure() != null) {
				String systolic = clinicalNotesCollection.getVitalSigns().getBloodPressure().getSystolic();
				systolic = systolic != null && !systolic.isEmpty() ? systolic.trim() : "";

				String diastolic = clinicalNotesCollection.getVitalSigns().getBloodPressure().getDiastolic();
				diastolic = diastolic != null && !diastolic.isEmpty() ? diastolic.trim() : "";

				if (!DPDoctorUtils.anyStringEmpty(systolic, diastolic))
					bloodPressure = "B.P: " + systolic + "/" + diastolic + " " + VitalSignsUnit.BLOODPRESSURE.getUnit();
				if (!DPDoctorUtils.allStringsEmpty(bloodPressure)) {
					if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
						vitalSigns = vitalSigns + ",  " + bloodPressure;
					else
						vitalSigns = bloodPressure;
				}
			}
			String spo2 = clinicalNotesCollection.getVitalSigns().getSpo2();
			spo2 = (spo2 != null && !spo2.isEmpty() ? "SPO2: " + spo2 + " " + VitalSignsUnit.SPO2.getUnit() : "");
			if (!DPDoctorUtils.allStringsEmpty(spo2)) {
				if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
					vitalSigns = vitalSigns + ",  " + spo2;
				else
					vitalSigns = spo2;
			}
			String height = clinicalNotesCollection.getVitalSigns().getHeight();
			height = (height != null && !height.isEmpty() ? "Height: " + height + " " + VitalSignsUnit.HEIGHT.getUnit()
					: "");
			if (!DPDoctorUtils.allStringsEmpty(height)) {
				if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
					vitalSigns = vitalSigns + ",  " + height;
				else
					vitalSigns = spo2;
			}

			String bmi = clinicalNotesCollection.getVitalSigns().getBmi();
			if (!DPDoctorUtils.allStringsEmpty(bmi)) {
				if (bmi.equalsIgnoreCase("nan"))
					bmi = "";

			} else {
				bmi = "";
			}

			if (!DPDoctorUtils.allStringsEmpty(bmi)) {
				bmi = "Bmi: " + String.format("%.3f", Double.parseDouble(bmi));
				if (!DPDoctorUtils.allStringsEmpty(bmi))
					vitalSigns = vitalSigns + ",  " + bmi;
				else
					vitalSigns = bmi;
			}

			String bsa = clinicalNotesCollection.getVitalSigns().getBsa();
			if (!DPDoctorUtils.allStringsEmpty(bsa)) {
				if (bsa.equalsIgnoreCase("nan"))
					bsa = "";

			} else {
				bsa = "";
			}
			if (!DPDoctorUtils.allStringsEmpty(bsa)) {
				bsa = "Bsa: " + String.format("%.3f", Double.parseDouble(bsa));
				if (!DPDoctorUtils.allStringsEmpty(vitalSigns))
					vitalSigns = vitalSigns + ",  " + bsa;
				else
					vitalSigns = bsa;
			}
			parameters.put("vitalSigns", vitalSigns != null && !vitalSigns.isEmpty() ? vitalSigns : null);
		} else
			parameters.put("vitalSigns", null);

		if (parameters.get("followUpAppointment") == null
				&& !DPDoctorUtils.anyStringEmpty(clinicalNotesCollection.getAppointmentId())
				&& clinicalNotesCollection.getTime() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
			String _24HourTime = String.format("%02d:%02d", clinicalNotesCollection.getTime().getFromTime() / 60,
					clinicalNotesCollection.getTime().getFromTime() % 60);
			SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
			SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
			sdf.setTimeZone(TimeZone.getTimeZone("IST"));
			_24HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));
			_12HourSDF.setTimeZone(TimeZone.getTimeZone("IST"));

			Date _24HourDt = _24HourSDF.parse(_24HourTime);
			String dateTime = _12HourSDF.format(_24HourDt) + ", " + sdf.format(clinicalNotesCollection.getFromDate());
			parameters.put("followUpAppointment", "Next Review on " + dateTime);
		}

		if (historyCollection != null) {
			parameters.put("showHistory", true);
			patientVisitService.includeHistoryInPdf(historyCollection, showPH, showPLH, showFH, showDA, parameters);
		}
		patientVisitService.generatePatientDetails(
				(printSettings != null && printSettings.getHeaderSetup() != null
						? printSettings.getHeaderSetup().getPatientDetails() : null),
				patient,
				"<b>CID: </b>" + (clinicalNotesCollection.getUniqueEmrId() != null
						? clinicalNotesCollection.getUniqueEmrId() : "--"),
				patient.getLocalPatientName(), user.getMobileNumber(), parameters,
				clinicalNotesCollection.getUpdatedTime());
		patientVisitService.generatePrintSetup(parameters, printSettings, clinicalNotesCollection.getDoctorId());
		String pdfName = (user != null ? user.getFirstName() : "") + "CLINICALNOTES-"
				+ clinicalNotesCollection.getUniqueEmrId() + new Date().getTime();

		String layout = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
				: "PORTRAIT";
		String pageSize = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4") : "A4";
		Integer topMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getTopMargin() : 20) : 20;
		Integer bottonMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getBottomMargin() : 20) : 20;
		Integer leftMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != 20
						? printSettings.getPageSetup().getLeftMargin() : 20)
				: 20;
		Integer rightMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
						? printSettings.getPageSetup().getRightMargin() : 20)
				: 20;
		response = jasperReportService.createPDF(ComponentType.CLINICAL_NOTES, parameters, clinicalNotesA4FileName,
				layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));
		return response;
	}

	public List<Complaint> sortComplaints(List<Complaint> complaints, List<ObjectId> complaintIds) {
		List<Complaint> response = new ArrayList<Complaint>();
		if (complaints != null && !complaints.isEmpty()) {
			for (ObjectId id : complaintIds)
				for (Complaint complaint : complaints)
					if (complaint.getId().equalsIgnoreCase(id.toString()))
						response.add(complaint);
		}
		return response;
	}

	public List<Diagram> sortDiagrams(List<Diagram> mappedResults, List<ObjectId> diagrams) {
		List<Diagram> response = new ArrayList<Diagram>();
		if (mappedResults != null && !mappedResults.isEmpty()) {
			for (ObjectId id : diagrams)
				for (Diagram diagram : mappedResults)
					if (diagram.getId().equalsIgnoreCase(id.toString()))
						response.add(diagram);
		}
		return response;
	}

	public List<Notes> sortNotes(List<Notes> mappedResults, List<ObjectId> notes) {
		List<Notes> response = new ArrayList<Notes>();
		if (mappedResults != null && !mappedResults.isEmpty()) {
			for (ObjectId id : notes)
				for (Notes note : mappedResults)
					if (note.getId().equalsIgnoreCase(id.toString()))
						response.add(note);
		}
		return response;
	}

	public List<Diagnoses> sortDiagnoses(List<Diagnoses> mappedResults, List<ObjectId> diagnoses) {
		List<Diagnoses> response = new ArrayList<Diagnoses>();
		if (mappedResults != null && !mappedResults.isEmpty()) {
			for (ObjectId id : diagnoses)
				for (Diagnoses diagnosis : mappedResults)
					if (diagnosis.getId().equalsIgnoreCase(id.toString()))
						response.add(diagnosis);
		}
		return response;
	}

	public List<Observation> sortObservations(List<Observation> mappedResults, List<ObjectId> observations) {
		List<Observation> response = new ArrayList<Observation>();
		if (mappedResults != null && !mappedResults.isEmpty()) {
			for (ObjectId id : observations)
				for (Observation observation : mappedResults)
					if (observation.getId().equalsIgnoreCase(id.toString()))
						response.add(observation);
		}
		return response;
	}

	public List<Investigation> sortInvestigations(List<Investigation> mappedResults, List<ObjectId> investigations) {
		List<Investigation> response = new ArrayList<Investigation>();
		if (mappedResults != null && !mappedResults.isEmpty()) {
			for (ObjectId id : investigations)
				for (Investigation investigation : mappedResults)
					if (investigation.getId().equalsIgnoreCase(id.toString()))
						response.add(investigation);
		}
		return response;
	}

	private Appointment addNotesAppointment(AppointmentRequest appointment) {
		Appointment response = null;
		if (appointment.getAppointmentId() == null) {
			response = appointmentService.addAppointment(appointment, false);
		} else {
			response = new Appointment();
			BeanUtil.map(appointment, response);
		}
		return response;
	}

	@Override
	public Boolean updateQuery() {
		Boolean response = false;
		try {
			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<PresentComplaint> getCustomGlobalPresentComplaint(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<PresentComplaint> response = new ArrayList<PresentComplaint>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<PresentComplaint> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					PresentComplaintCollection.class, PresentComplaint.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Present Complaint");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<PresentComplaint> getGlobalPresentComplaint(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<PresentComplaint> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<PresentComplaint> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities, null),
					PresentComplaintCollection.class, PresentComplaint.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Present Complaint");
		}
		return response;
	}

	private List<PresentComplaint> getCustomPresentComplaint(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<PresentComplaint> response = null;
		try {
			AggregationResults<PresentComplaint> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							PresentComplaintCollection.class, PresentComplaint.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Present Complaint");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<PresentComplaintHistory> getCustomGlobalPresentComplaintHistory(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<PresentComplaintHistory> response = new ArrayList<PresentComplaintHistory>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<PresentComplaintHistory> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					PresentComplaintHistoryCollection.class, PresentComplaintHistory.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Present Complaint History");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<PresentComplaintHistory> getGlobalPresentComplaintHistory(int page, int size, String doctorId,
			String updatedTime, Boolean discarded) {
		List<PresentComplaintHistory> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<PresentComplaintHistory> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null,
									specialities, null),
							PresentComplaintHistoryCollection.class, PresentComplaintHistory.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Present Complaint History");
		}
		return response;
	}

	private List<PresentComplaintHistory> getCustomPresentComplaintHistory(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<PresentComplaintHistory> response = null;
		try {
			AggregationResults<PresentComplaintHistory> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded, null, null, null),
					PresentComplaintHistoryCollection.class, PresentComplaintHistory.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Present Complaint History");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<ProvisionalDiagnosis> getCustomGlobalProvisionalDiagnosis(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<ProvisionalDiagnosis> response = new ArrayList<ProvisionalDiagnosis>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<ProvisionalDiagnosis> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					ProvisionalDiagnosisCollection.class, ProvisionalDiagnosis.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Provisional Diagnosis");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<ProvisionalDiagnosis> getGlobalProvisionalDiagnosis(int page, int size, String doctorId,
			String updatedTime, Boolean discarded) {
		List<ProvisionalDiagnosis> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<ProvisionalDiagnosis> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null,
									specialities, null),
							ProvisionalDiagnosisCollection.class, ProvisionalDiagnosis.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Provisional Diagnosis");
		}
		return response;
	}

	private List<ProvisionalDiagnosis> getCustomProvisionalDiagnosis(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<ProvisionalDiagnosis> response = null;
		try {
			AggregationResults<ProvisionalDiagnosis> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded, null, null, null),
					ProvisionalDiagnosisCollection.class, ProvisionalDiagnosis.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Provisional Diagnosis");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<GeneralExam> getCustomGlobalGeneralExam(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<GeneralExam> response = new ArrayList<GeneralExam>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<GeneralExam> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					GeneralExamCollection.class, GeneralExam.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting General Exam");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<GeneralExam> getGlobalGeneralExam(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<GeneralExam> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<GeneralExam> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities, null),
					GeneralExamCollection.class, GeneralExam.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting General Exam");
		}
		return response;
	}

	private List<GeneralExam> getCustomGeneralExam(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<GeneralExam> response = null;
		try {
			AggregationResults<GeneralExam> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							GeneralExamCollection.class, GeneralExam.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting General Exam");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<SystemExam> getCustomGlobalSystemExam(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<SystemExam> response = new ArrayList<SystemExam>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<SystemExam> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					SystemExamCollection.class, SystemExam.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting System Exam");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<SystemExam> getGlobalSystemExam(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<SystemExam> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<SystemExam> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page,
					size, updatedTime, discarded, null, null, specialities, null), SystemExamCollection.class,
					SystemExam.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting System Exam");
		}
		return response;
	}

	private List<SystemExam> getCustomSystemExam(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<SystemExam> response = null;
		try {
			AggregationResults<SystemExam> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							SystemExamCollection.class, SystemExam.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting System Exam");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<MenstrualHistory> getCustomGlobalMenstrualHistory(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<MenstrualHistory> response = new ArrayList<MenstrualHistory>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<MenstrualHistory> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					MenstrualHistoryCollection.class, MenstrualHistory.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Menstrual History");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<MenstrualHistory> getGlobalMenstrualHistory(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<MenstrualHistory> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<MenstrualHistory> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities, null),
					MenstrualHistoryCollection.class, MenstrualHistory.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Menstrual History");
		}
		return response;
	}

	private List<MenstrualHistory> getCustomMenstrualHistory(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<MenstrualHistory> response = null;
		try {
			AggregationResults<MenstrualHistory> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							MenstrualHistoryCollection.class, MenstrualHistory.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Menstrual History");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<ObstetricHistory> getCustomGlobalObstetricHistory(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<ObstetricHistory> response = new ArrayList<ObstetricHistory>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<ObstetricHistory> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					ObstetricHistoryCollection.class, ObstetricHistory.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Obstetric History");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<ObstetricHistory> getGlobalObstetricHistory(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<ObstetricHistory> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<ObstetricHistory> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities, null),
					ObstetricHistoryCollection.class, ObstetricHistory.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Menstrual History");
		}
		return response;
	}

	private List<ObstetricHistory> getCustomObstetricHistory(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<ObstetricHistory> response = null;
		try {
			AggregationResults<ObstetricHistory> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							ObstetricHistoryCollection.class, ObstetricHistory.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Obstetric History");
		}
		return response;
	}

	@Override
	@Transactional
	public ProvisionalDiagnosis deleteProvisionalDiagnosis(String id, String doctorId, String locationId,
			String hospitalId, Boolean discarded) {
		ProvisionalDiagnosis response = null;
		try {
			ProvisionalDiagnosisCollection provisionalDiagnosisCollection = provisionalDiagnosisRepository
					.findOne(new ObjectId(id));
			if (provisionalDiagnosisCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(provisionalDiagnosisCollection.getDoctorId(),
						provisionalDiagnosisCollection.getHospitalId(),
						provisionalDiagnosisCollection.getLocationId())) {
					if (provisionalDiagnosisCollection.getDoctorId().toString().equals(doctorId)
							&& provisionalDiagnosisCollection.getHospitalId().toString().equals(hospitalId)
							&& provisionalDiagnosisCollection.getLocationId().toString().equals(locationId)) {

						provisionalDiagnosisCollection.setDiscarded(discarded);
						provisionalDiagnosisCollection.setUpdatedTime(new Date());
						provisionalDiagnosisRepository.save(provisionalDiagnosisCollection);
						response = new ProvisionalDiagnosis();
						BeanUtil.map(provisionalDiagnosisCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					provisionalDiagnosisCollection.setDiscarded(discarded);
					provisionalDiagnosisCollection.setUpdatedTime(new Date());
					provisionalDiagnosisRepository.save(provisionalDiagnosisCollection);
					response = new ProvisionalDiagnosis();
					BeanUtil.map(provisionalDiagnosisCollection, response);
				}
			} else {
				logger.warn("Provisional Diagnosis not found!");
				throw new BusinessException(ServiceError.NoRecord, "Provisional Diagnosis not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public GeneralExam deleteGeneralExam(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		GeneralExam response = null;
		try {
			GeneralExamCollection generalExamCollection = generalExamRepository.findOne(new ObjectId(id));
			if (generalExamCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(generalExamCollection.getDoctorId(),
						generalExamCollection.getHospitalId(), generalExamCollection.getLocationId())) {
					if (generalExamCollection.getDoctorId().toString().equals(doctorId)
							&& generalExamCollection.getHospitalId().toString().equals(hospitalId)
							&& generalExamCollection.getLocationId().toString().equals(locationId)) {

						generalExamCollection.setDiscarded(discarded);
						generalExamCollection.setUpdatedTime(new Date());
						generalExamRepository.save(generalExamCollection);
						response = new GeneralExam();
						BeanUtil.map(generalExamCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					generalExamCollection.setDiscarded(discarded);
					generalExamCollection.setUpdatedTime(new Date());
					generalExamRepository.save(generalExamCollection);
					response = new GeneralExam();
					BeanUtil.map(generalExamCollection, response);
				}
			} else {
				logger.warn("General Exam not found!");
				throw new BusinessException(ServiceError.NoRecord, "General Exam not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public PresentComplaintHistory deletePresentComplaintHistory(String id, String doctorId, String locationId,
			String hospitalId, Boolean discarded) {
		PresentComplaintHistory response = null;
		try {
			PresentComplaintHistoryCollection presentComplaintHistoryCollection = presentComplaintHistoryRepository
					.findOne(new ObjectId(id));
			if (presentComplaintHistoryCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(presentComplaintHistoryCollection.getDoctorId(),
						presentComplaintHistoryCollection.getHospitalId(),
						presentComplaintHistoryCollection.getLocationId())) {
					if (presentComplaintHistoryCollection.getDoctorId().toString().equals(doctorId)
							&& presentComplaintHistoryCollection.getHospitalId().toString().equals(hospitalId)
							&& presentComplaintHistoryCollection.getLocationId().toString().equals(locationId)) {

						presentComplaintHistoryCollection.setDiscarded(discarded);
						presentComplaintHistoryCollection.setUpdatedTime(new Date());
						presentComplaintHistoryRepository.save(presentComplaintHistoryCollection);
						response = new PresentComplaintHistory();
						BeanUtil.map(presentComplaintHistoryCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					presentComplaintHistoryCollection.setDiscarded(discarded);
					presentComplaintHistoryCollection.setUpdatedTime(new Date());
					presentComplaintHistoryRepository.save(presentComplaintHistoryCollection);
					response = new PresentComplaintHistory();
					BeanUtil.map(presentComplaintHistoryCollection, response);
				}
			} else {
				logger.warn("Present Complaint History not found!");
				throw new BusinessException(ServiceError.NoRecord, "Present Complaint History not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public SystemExam deleteSystemExam(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		SystemExam response = null;
		try {
			SystemExamCollection systemExamCollection = systemExamRepository.findOne(new ObjectId(id));
			if (systemExamCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(systemExamCollection.getDoctorId(),
						systemExamCollection.getHospitalId(), systemExamCollection.getLocationId())) {
					if (systemExamCollection.getDoctorId().toString().equals(doctorId)
							&& systemExamCollection.getHospitalId().toString().equals(hospitalId)
							&& systemExamCollection.getLocationId().toString().equals(locationId)) {

						systemExamCollection.setDiscarded(discarded);
						systemExamCollection.setUpdatedTime(new Date());
						systemExamRepository.save(systemExamCollection);
						response = new SystemExam();
						BeanUtil.map(systemExamCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					systemExamCollection.setDiscarded(discarded);
					systemExamCollection.setUpdatedTime(new Date());
					systemExamRepository.save(systemExamCollection);
					response = new SystemExam();
					BeanUtil.map(systemExamCollection, response);
				}
			} else {
				logger.warn("System Exam not found!");
				throw new BusinessException(ServiceError.NoRecord, "System Exam not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public PresentComplaint deletePresentComplaint(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		PresentComplaint response = null;
		try {
			PresentComplaintCollection presentComplaintCollection = presentComplaintRepository
					.findOne(new ObjectId(id));
			if (presentComplaintCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(presentComplaintCollection.getDoctorId(),
						presentComplaintCollection.getHospitalId(), presentComplaintCollection.getLocationId())) {
					if (presentComplaintCollection.getDoctorId().toString().equals(doctorId)
							&& presentComplaintCollection.getHospitalId().toString().equals(hospitalId)
							&& presentComplaintCollection.getLocationId().toString().equals(locationId)) {

						presentComplaintCollection.setDiscarded(discarded);
						presentComplaintCollection.setUpdatedTime(new Date());
						presentComplaintRepository.save(presentComplaintCollection);
						response = new PresentComplaint();
						BeanUtil.map(presentComplaintCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					presentComplaintCollection.setDiscarded(discarded);
					presentComplaintCollection.setUpdatedTime(new Date());
					presentComplaintRepository.save(presentComplaintCollection);
					response = new PresentComplaint();
					BeanUtil.map(presentComplaintCollection, response);
				}
			} else {
				logger.warn("Present Complaint not found!");
				throw new BusinessException(ServiceError.NoRecord, "Present Complaint not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public ObstetricHistory deleteObstetricHistory(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		ObstetricHistory response = null;
		try {
			ObstetricHistoryCollection obstetricHistoryCollection = obstetricHistoryRepository
					.findOne(new ObjectId(id));
			if (obstetricHistoryCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(obstetricHistoryCollection.getDoctorId(),
						obstetricHistoryCollection.getHospitalId(), obstetricHistoryCollection.getLocationId())) {
					if (obstetricHistoryCollection.getDoctorId().toString().equals(doctorId)
							&& obstetricHistoryCollection.getHospitalId().toString().equals(hospitalId)
							&& obstetricHistoryCollection.getLocationId().toString().equals(locationId)) {

						obstetricHistoryCollection.setDiscarded(discarded);
						obstetricHistoryCollection.setUpdatedTime(new Date());
						obstetricHistoryRepository.save(obstetricHistoryCollection);
						response = new ObstetricHistory();
						BeanUtil.map(obstetricHistoryCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					obstetricHistoryCollection.setDiscarded(discarded);
					obstetricHistoryCollection.setUpdatedTime(new Date());
					obstetricHistoryRepository.save(obstetricHistoryCollection);
					response = new ObstetricHistory();
					BeanUtil.map(obstetricHistoryCollection, response);
				}
			} else {
				logger.warn("Obstetric History not found!");
				throw new BusinessException(ServiceError.NoRecord, "Obstetric History not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public MenstrualHistory deleteMenstrualHistory(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		MenstrualHistory response = null;
		try {
			MenstrualHistoryCollection menstrualHistoryCollection = menstrualHistoryRepository
					.findOne(new ObjectId(id));
			if (menstrualHistoryCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(menstrualHistoryCollection.getDoctorId(),
						menstrualHistoryCollection.getHospitalId(), menstrualHistoryCollection.getLocationId())) {
					if (menstrualHistoryCollection.getDoctorId().toString().equals(doctorId)
							&& menstrualHistoryCollection.getHospitalId().toString().equals(hospitalId)
							&& menstrualHistoryCollection.getLocationId().toString().equals(locationId)) {

						menstrualHistoryCollection.setDiscarded(discarded);
						menstrualHistoryCollection.setUpdatedTime(new Date());
						menstrualHistoryRepository.save(menstrualHistoryCollection);
						response = new MenstrualHistory();
						BeanUtil.map(menstrualHistoryCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					menstrualHistoryCollection.setDiscarded(discarded);
					menstrualHistoryCollection.setUpdatedTime(new Date());
					menstrualHistoryRepository.save(menstrualHistoryCollection);
					response = new MenstrualHistory();
					BeanUtil.map(menstrualHistoryCollection, response);
				}
			} else {
				logger.warn("Menstrual History not found!");
				throw new BusinessException(ServiceError.NoRecord, "Menstrual History not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public IndicationOfUSG addEditIndicationOfUSG(IndicationOfUSG indicationOfUSG) {
		try {
			IndicationOfUSGCollection indicationOfUSGCollection = new IndicationOfUSGCollection();
			BeanUtil.map(indicationOfUSG, indicationOfUSGCollection);
			if (DPDoctorUtils.anyStringEmpty(indicationOfUSGCollection.getId())) {
				indicationOfUSGCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(indicationOfUSGCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(indicationOfUSGCollection.getDoctorId());
					if (userCollection != null) {
						indicationOfUSGCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					indicationOfUSGCollection.setCreatedBy("ADMIN");
				}
			} else {
				IndicationOfUSGCollection oldIndicationOfUSGCollection = indicationOfUSGRepository
						.findOne(indicationOfUSGCollection.getId());
				indicationOfUSGCollection.setCreatedBy(oldIndicationOfUSGCollection.getCreatedBy());
				indicationOfUSGCollection.setCreatedTime(oldIndicationOfUSGCollection.getCreatedTime());
				indicationOfUSGCollection.setDiscarded(oldIndicationOfUSGCollection.getDiscarded());
			}
			indicationOfUSGCollection = indicationOfUSGRepository.save(indicationOfUSGCollection);

			BeanUtil.map(indicationOfUSGCollection, indicationOfUSG);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return indicationOfUSG;
	}

	@Override
	public IndicationOfUSG deleteIndicationOfUSG(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		IndicationOfUSG response = null;
		try {
			IndicationOfUSGCollection indicationOfUSGCollection = indicationOfUSGRepository.findOne(new ObjectId(id));
			if (indicationOfUSGCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(indicationOfUSGCollection.getDoctorId(),
						indicationOfUSGCollection.getHospitalId(), indicationOfUSGCollection.getLocationId())) {
					if (indicationOfUSGCollection.getDoctorId().toString().equals(doctorId)
							&& indicationOfUSGCollection.getHospitalId().toString().equals(hospitalId)
							&& indicationOfUSGCollection.getLocationId().toString().equals(locationId)) {

						indicationOfUSGCollection.setDiscarded(discarded);
						indicationOfUSGCollection.setUpdatedTime(new Date());
						indicationOfUSGRepository.save(indicationOfUSGCollection);
						response = new IndicationOfUSG();
						BeanUtil.map(indicationOfUSGCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					indicationOfUSGCollection.setDiscarded(discarded);
					indicationOfUSGCollection.setUpdatedTime(new Date());
					indicationOfUSGRepository.save(indicationOfUSGCollection);
					response = new IndicationOfUSG();
					BeanUtil.map(indicationOfUSGCollection, response);
				}
			} else {
				logger.warn("Indication of USG not found!");
				throw new BusinessException(ServiceError.NoRecord, "Indication of USG not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<IndicationOfUSG> getCustomGlobalIndicationOfUSG(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<IndicationOfUSG> response = new ArrayList<IndicationOfUSG>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<IndicationOfUSG> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					IndicationOfUSGCollection.class, IndicationOfUSG.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Indication Of USG");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<IndicationOfUSG> getGlobalIndicationOfUSG(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<IndicationOfUSG> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<IndicationOfUSG> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities, null),
					IndicationOfUSGCollection.class, IndicationOfUSG.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Indication of USG");
		}
		return response;
	}

	private List<IndicationOfUSG> getCustomIndicationOfUSG(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<IndicationOfUSG> response = null;
		try {
			AggregationResults<IndicationOfUSG> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							IndicationOfUSGCollection.class, IndicationOfUSG.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Indication of USG");
		}
		return response;
	}

	@Override
	@Transactional
	public PV addEditPV(PV pv) {
		try {
			PVCollection pvCollection = new PVCollection();
			BeanUtil.map(pv, pvCollection);
			if (DPDoctorUtils.anyStringEmpty(pvCollection.getId())) {
				pvCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(pvCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(pvCollection.getDoctorId());
					if (userCollection != null) {
						pvCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					pvCollection.setCreatedBy("ADMIN");
				}
			} else {
				PVCollection oldPVCollecion = pvRepository.findOne(pvCollection.getId());
				pvCollection.setCreatedBy(oldPVCollecion.getCreatedBy());
				pvCollection.setCreatedTime(oldPVCollecion.getCreatedTime());
				pvCollection.setDiscarded(oldPVCollecion.getDiscarded());
			}
			pvCollection = pvRepository.save(pvCollection);

			BeanUtil.map(pvCollection, pv);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return pv;
	}

	@Override
	public PV deletePV(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
		PV response = null;
		try {
			PVCollection pvCollection = pvRepository.findOne(new ObjectId(id));
			if (pvCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(pvCollection.getDoctorId(), pvCollection.getHospitalId(),
						pvCollection.getLocationId())) {
					if (pvCollection.getDoctorId().toString().equals(doctorId)
							&& pvCollection.getHospitalId().toString().equals(hospitalId)
							&& pvCollection.getLocationId().toString().equals(locationId)) {

						pvCollection.setDiscarded(discarded);
						pvCollection.setUpdatedTime(new Date());
						pvRepository.save(pvCollection);
						response = new PV();
						BeanUtil.map(pvCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					pvCollection.setDiscarded(discarded);
					pvCollection.setUpdatedTime(new Date());
					pvRepository.save(pvCollection);
					response = new PV();
					BeanUtil.map(pvCollection, response);
				}
			} else {
				logger.warn("P/V not found!");
				throw new BusinessException(ServiceError.NoRecord, "P/V not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<PV> getCustomGlobalPV(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<PV> response = new ArrayList<PV>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<PV> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, specialities, null),
							PVCollection.class, PV.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/V");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<PV> getGlobalPV(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
		List<PV> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<PV> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page, size,
					updatedTime, discarded, null, null, specialities, null), PVCollection.class, PV.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/V");
		}
		return response;
	}

	private List<PV> getCustomPV(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<PV> response = null;
		try {
			AggregationResults<PV> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page, size,
					doctorId, locationId, hospitalId, updatedTime, discarded, null, null, null), PVCollection.class,
					PV.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/V");
		}
		return response;
	}

	@Override
	@Transactional
	public PA addEditPA(PA pa) {
		try {
			PACollection paCollection = new PACollection();
			BeanUtil.map(pa, paCollection);
			if (DPDoctorUtils.anyStringEmpty(paCollection.getId())) {
				paCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(paCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(paCollection.getDoctorId());
					if (userCollection != null) {
						paCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					paCollection.setCreatedBy("ADMIN");
				}
			} else {
				PACollection oldPACollecion = paRepository.findOne(paCollection.getId());
				paCollection.setCreatedBy(oldPACollecion.getCreatedBy());
				paCollection.setCreatedTime(oldPACollecion.getCreatedTime());
				paCollection.setDiscarded(oldPACollecion.getDiscarded());
			}
			paCollection = paRepository.save(paCollection);

			BeanUtil.map(paCollection, pa);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return pa;
	}

	@Override
	public PA deletePA(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
		PA response = null;
		try {
			PACollection paCollection = paRepository.findOne(new ObjectId(id));
			if (paCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(paCollection.getDoctorId(), paCollection.getHospitalId(),
						paCollection.getLocationId())) {
					if (paCollection.getDoctorId().toString().equals(doctorId)
							&& paCollection.getHospitalId().toString().equals(hospitalId)
							&& paCollection.getLocationId().toString().equals(locationId)) {

						paCollection.setDiscarded(discarded);
						paCollection.setUpdatedTime(new Date());
						paRepository.save(paCollection);
						response = new PA();
						BeanUtil.map(paCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					paCollection.setDiscarded(discarded);
					paCollection.setUpdatedTime(new Date());
					paRepository.save(paCollection);
					response = new PA();
					BeanUtil.map(paCollection, response);
				}
			} else {
				logger.warn("Indication of USG not found!");
				throw new BusinessException(ServiceError.NoRecord, "P/A not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	@Transactional
	public PS addEditPS(PS ps) {
		try {
			PSCollection psCollection = new PSCollection();
			BeanUtil.map(ps, psCollection);
			if (DPDoctorUtils.anyStringEmpty(psCollection.getId())) {
				psCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(psCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(psCollection.getDoctorId());
					if (userCollection != null) {
						psCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					psCollection.setCreatedBy("ADMIN");
				}
			} else {
				PSCollection oldPSCollecion = psRepository.findOne(psCollection.getId());
				psCollection.setCreatedBy(oldPSCollecion.getCreatedBy());
				psCollection.setCreatedTime(oldPSCollecion.getCreatedTime());
				psCollection.setDiscarded(oldPSCollecion.getDiscarded());
			}
			psCollection = psRepository.save(psCollection);

			BeanUtil.map(psCollection, ps);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return ps;
	}

	@Override
	@Transactional
	public ECGDetails addEditECGDetails(ECGDetails ecgDetails) {
		try {
			ECGDetailsCollection ecgDetailsCollection = new ECGDetailsCollection();
			BeanUtil.map(ecgDetails, ecgDetailsCollection);
			if (DPDoctorUtils.anyStringEmpty(ecgDetailsCollection.getId())) {
				ecgDetailsCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(ecgDetailsCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(ecgDetailsCollection.getDoctorId());
					if (userCollection != null) {
						ecgDetailsCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					ecgDetailsCollection.setCreatedBy("ADMIN");
				}
			} else {
				ECGDetailsCollection oldECGDetailsCollection = ecgDetailsRepository
						.findOne(ecgDetailsCollection.getId());
				ecgDetailsCollection.setCreatedBy(oldECGDetailsCollection.getCreatedBy());
				ecgDetailsCollection.setCreatedTime(oldECGDetailsCollection.getCreatedTime());
				ecgDetailsCollection.setDiscarded(oldECGDetailsCollection.getDiscarded());
			}
			ecgDetailsCollection = ecgDetailsRepository.save(ecgDetailsCollection);

			BeanUtil.map(ecgDetailsCollection, ecgDetails);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return ecgDetails;
	}

	@Override
	@Transactional
	public XRayDetails addEditXRayDetails(XRayDetails xRayDetails) {
		try {
			XRayDetailsCollection xRayDetailsCollection = new XRayDetailsCollection();
			BeanUtil.map(xRayDetails, xRayDetailsCollection);
			if (DPDoctorUtils.anyStringEmpty(xRayDetailsCollection.getId())) {
				xRayDetailsCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(xRayDetailsCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(xRayDetailsCollection.getDoctorId());
					if (userCollection != null) {
						xRayDetailsCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					xRayDetailsCollection.setCreatedBy("ADMIN");
				}
			} else {
				XRayDetailsCollection oldXRayDetailsCollection = xRayDetailsRepository
						.findOne(xRayDetailsCollection.getId());
				xRayDetailsCollection.setCreatedBy(oldXRayDetailsCollection.getCreatedBy());
				xRayDetailsCollection.setCreatedTime(oldXRayDetailsCollection.getCreatedTime());
				xRayDetailsCollection.setDiscarded(oldXRayDetailsCollection.getDiscarded());
			}
			xRayDetailsCollection = xRayDetailsRepository.save(xRayDetailsCollection);

			BeanUtil.map(xRayDetailsCollection, xRayDetails);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return xRayDetails;
	}

	@Override
	@Transactional
	public Echo addEditEcho(Echo echo) {
		try {
			EchoCollection echoCollection = new EchoCollection();
			BeanUtil.map(echo, echoCollection);
			if (DPDoctorUtils.anyStringEmpty(echoCollection.getId())) {
				echoCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(echoCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(echoCollection.getDoctorId());
					if (userCollection != null) {
						echoCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					echoCollection.setCreatedBy("ADMIN");
				}
			} else {
				EchoCollection oldEchoCollection = echoRepository.findOne(echoCollection.getId());
				echoCollection.setCreatedBy(oldEchoCollection.getCreatedBy());
				echoCollection.setCreatedTime(oldEchoCollection.getCreatedTime());
				echoCollection.setDiscarded(oldEchoCollection.getDiscarded());
			}
			echoCollection = echoRepository.save(echoCollection);

			BeanUtil.map(echoCollection, echo);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return echo;
	}

	@Override
	@Transactional
	public Holter addEditHolter(Holter holter) {
		try {
			HolterCollection holterCollection = new HolterCollection();
			BeanUtil.map(holter, holterCollection);
			if (DPDoctorUtils.anyStringEmpty(holterCollection.getId())) {
				holterCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(holterCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(holterCollection.getDoctorId());
					if (userCollection != null) {
						holterCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					holterCollection.setCreatedBy("ADMIN");
				}
			} else {
				HolterCollection oldHolterCollection = holterRepository.findOne(holterCollection.getId());
				holterCollection.setCreatedBy(oldHolterCollection.getCreatedBy());
				holterCollection.setCreatedTime(oldHolterCollection.getCreatedTime());
				holterCollection.setDiscarded(oldHolterCollection.getDiscarded());
			}
			holterCollection = holterRepository.save(holterCollection);

			BeanUtil.map(holterCollection, holter);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return holter;
	}

	@Override
	@Transactional
	public ProcedureNote addEditProcedureNote(ProcedureNote precedureNote) {
		try {
			ProcedureNoteCollection procedureNoteCollection = new ProcedureNoteCollection();
			BeanUtil.map(precedureNote, procedureNoteCollection);
			if (DPDoctorUtils.anyStringEmpty(procedureNoteCollection.getId())) {
				procedureNoteCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(procedureNoteCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(procedureNoteCollection.getDoctorId());
					if (userCollection != null) {
						procedureNoteCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					procedureNoteCollection.setCreatedBy("ADMIN");
				}
			} else {
				ProcedureNoteCollection oldProcedureNoteCollection = procedureNoteRepository
						.findOne(procedureNoteCollection.getId());
				procedureNoteCollection.setCreatedBy(oldProcedureNoteCollection.getCreatedBy());
				procedureNoteCollection.setCreatedTime(oldProcedureNoteCollection.getCreatedTime());
				procedureNoteCollection.setDiscarded(oldProcedureNoteCollection.getDiscarded());
			}
			procedureNoteCollection = procedureNoteRepository.save(procedureNoteCollection);

			BeanUtil.map(procedureNoteCollection, precedureNote);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return precedureNote;
	}

	@Override
	@Transactional
	public PresentingComplaintNose addEditPCNose(PresentingComplaintNose presentingComplaintNotes) {
		try {
			PresentingComplaintNoseCollection presentingComplaintNotesCollection = new PresentingComplaintNoseCollection();
			BeanUtil.map(presentingComplaintNotes, presentingComplaintNotesCollection);
			if (DPDoctorUtils.anyStringEmpty(presentingComplaintNotesCollection.getId())) {
				presentingComplaintNotesCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(presentingComplaintNotesCollection.getDoctorId())) {
					UserCollection userCollection = userRepository
							.findOne(presentingComplaintNotesCollection.getDoctorId());
					if (userCollection != null) {
						presentingComplaintNotesCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					presentingComplaintNotesCollection.setCreatedBy("ADMIN");
				}
			} else {
				PresentingComplaintNoseCollection oldPresentingComplaintNotesCollection = presentingComplaintNotesRepository
						.findOne(presentingComplaintNotesCollection.getId());
				presentingComplaintNotesCollection.setCreatedBy(oldPresentingComplaintNotesCollection.getCreatedBy());
				presentingComplaintNotesCollection
						.setCreatedTime(oldPresentingComplaintNotesCollection.getCreatedTime());
				presentingComplaintNotesCollection.setDiscarded(oldPresentingComplaintNotesCollection.getDiscarded());
			}
			presentingComplaintNotesCollection = presentingComplaintNotesRepository
					.save(presentingComplaintNotesCollection);

			BeanUtil.map(presentingComplaintNotesCollection, presentingComplaintNotes);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return presentingComplaintNotes;
	}

	@Override
	@Transactional
	public EarsExamination addEditEarsExam(EarsExamination earsExamination) {
		try {
			EarsExaminationCollection earsExaminationCollection = new EarsExaminationCollection();
			BeanUtil.map(earsExamination, earsExaminationCollection);
			if (DPDoctorUtils.anyStringEmpty(earsExaminationCollection.getId())) {
				earsExaminationCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(earsExaminationCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(earsExaminationCollection.getDoctorId());
					if (userCollection != null) {
						earsExaminationCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					earsExaminationCollection.setCreatedBy("ADMIN");
				}
			} else {
				EarsExaminationCollection oldEarsExaminationCollection = earsExaminationRepository
						.findOne(earsExaminationCollection.getId());
				earsExaminationCollection.setCreatedBy(oldEarsExaminationCollection.getCreatedBy());
				earsExaminationCollection.setCreatedTime(oldEarsExaminationCollection.getCreatedTime());
				earsExaminationCollection.setDiscarded(oldEarsExaminationCollection.getDiscarded());
			}
			earsExaminationCollection = earsExaminationRepository.save(earsExaminationCollection);

			BeanUtil.map(earsExaminationCollection, earsExamination);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return earsExamination;
	}

	@Override
	@Transactional
	public NeckExamination addEditNeckExam(NeckExamination neckExamination) {
		try {
			NeckExaminationCollection neckExaminationCollection = new NeckExaminationCollection();
			BeanUtil.map(neckExamination, neckExaminationCollection);
			if (DPDoctorUtils.anyStringEmpty(neckExaminationCollection.getId())) {
				neckExaminationCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(neckExaminationCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(neckExaminationCollection.getDoctorId());
					if (userCollection != null) {
						neckExaminationCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					neckExaminationCollection.setCreatedBy("ADMIN");
				}
			} else {
				NeckExaminationCollection oldNeckExaminationCollection = neckExaminationRepository
						.findOne(neckExaminationCollection.getId());
				neckExaminationCollection.setCreatedBy(oldNeckExaminationCollection.getCreatedBy());
				neckExaminationCollection.setCreatedTime(oldNeckExaminationCollection.getCreatedTime());
				neckExaminationCollection.setDiscarded(oldNeckExaminationCollection.getDiscarded());
			}
			neckExaminationCollection = neckExaminationRepository.save(neckExaminationCollection);

			BeanUtil.map(neckExaminationCollection, neckExamination);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return neckExamination;
	}

	@Override
	@Transactional
	public NoseExamination addEditNoseExam(NoseExamination noseExamination) {
		try {
			NoseExaminationCollection noseExaminationCollection = new NoseExaminationCollection();
			BeanUtil.map(noseExamination, noseExaminationCollection);
			if (DPDoctorUtils.anyStringEmpty(noseExaminationCollection.getId())) {
				noseExaminationCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(noseExaminationCollection.getDoctorId())) {
					UserCollection userCollection = userRepository.findOne(noseExaminationCollection.getDoctorId());
					if (userCollection != null) {
						noseExaminationCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					noseExaminationCollection.setCreatedBy("ADMIN");
				}
			} else {
				NoseExaminationCollection oldNoseExaminationCollection = noseExaminationRepository
						.findOne(noseExaminationCollection.getId());
				noseExaminationCollection.setCreatedBy(oldNoseExaminationCollection.getCreatedBy());
				noseExaminationCollection.setCreatedTime(oldNoseExaminationCollection.getCreatedTime());
				noseExaminationCollection.setDiscarded(oldNoseExaminationCollection.getDiscarded());
			}
			noseExaminationCollection = noseExaminationRepository.save(noseExaminationCollection);

			BeanUtil.map(noseExaminationCollection, noseExamination);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return noseExamination;
	}

	@Override
	@Transactional
	public OralCavityAndThroatExamination addEditOralCavityThroatExam(
			OralCavityAndThroatExamination oralCavityAndThroatExamination) {
		try {
			OralCavityAndThroatExaminationCollection oralCavityAndThroatExaminationCollection = new OralCavityAndThroatExaminationCollection();
			BeanUtil.map(oralCavityAndThroatExamination, oralCavityAndThroatExaminationCollection);
			if (DPDoctorUtils.anyStringEmpty(oralCavityAndThroatExaminationCollection.getId())) {
				oralCavityAndThroatExaminationCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(oralCavityAndThroatExaminationCollection.getDoctorId())) {
					UserCollection userCollection = userRepository
							.findOne(oralCavityAndThroatExaminationCollection.getDoctorId());
					if (userCollection != null) {
						oralCavityAndThroatExaminationCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					oralCavityAndThroatExaminationCollection.setCreatedBy("ADMIN");
				}
			} else {
				OralCavityAndThroatExaminationCollection oldOralCavityAndThroatExaminationCollection = oralCavityThroatExaminationRepository
						.findOne(oralCavityAndThroatExaminationCollection.getId());
				oralCavityAndThroatExaminationCollection
						.setCreatedBy(oldOralCavityAndThroatExaminationCollection.getCreatedBy());
				oralCavityAndThroatExaminationCollection
						.setCreatedTime(oldOralCavityAndThroatExaminationCollection.getCreatedTime());
				oralCavityAndThroatExaminationCollection
						.setDiscarded(oldOralCavityAndThroatExaminationCollection.getDiscarded());
			}
			oralCavityAndThroatExaminationCollection = oralCavityThroatExaminationRepository
					.save(oralCavityAndThroatExaminationCollection);

			BeanUtil.map(oralCavityAndThroatExaminationCollection, oralCavityAndThroatExamination);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return oralCavityAndThroatExamination;
	}

	@Override
	@Transactional
	public IndirectLarygoscopyExamination addEditIndirectLarygoscopyExam(
			IndirectLarygoscopyExamination indirectLarygoscopyExamination) {
		try {
			IndirectLarygoscopyExaminationCollection indirectLarygoscopyExaminationCollection = new IndirectLarygoscopyExaminationCollection();
			BeanUtil.map(indirectLarygoscopyExamination, indirectLarygoscopyExaminationCollection);
			if (DPDoctorUtils.anyStringEmpty(indirectLarygoscopyExaminationCollection.getId())) {
				indirectLarygoscopyExaminationCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(indirectLarygoscopyExamination.getDoctorId())) {
					UserCollection userCollection = userRepository
							.findOne(indirectLarygoscopyExaminationCollection.getDoctorId());
					if (userCollection != null) {
						indirectLarygoscopyExamination
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					indirectLarygoscopyExaminationCollection.setCreatedBy("ADMIN");
				}
			} else {
				IndirectLarygoscopyExaminationCollection oldIndirectLarygoscopyExaminationCollection = indirectLarygoscopyExaminationRepository
						.findOne(indirectLarygoscopyExaminationCollection.getId());
				indirectLarygoscopyExaminationCollection
						.setCreatedBy(oldIndirectLarygoscopyExaminationCollection.getCreatedBy());
				indirectLarygoscopyExaminationCollection
						.setCreatedTime(oldIndirectLarygoscopyExaminationCollection.getCreatedTime());
				indirectLarygoscopyExaminationCollection
						.setDiscarded(oldIndirectLarygoscopyExaminationCollection.getDiscarded());
			}
			indirectLarygoscopyExaminationCollection = indirectLarygoscopyExaminationRepository
					.save(indirectLarygoscopyExaminationCollection);

			BeanUtil.map(indirectLarygoscopyExaminationCollection, indirectLarygoscopyExamination);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return indirectLarygoscopyExamination;
	}

	@Override
	@Transactional
	public PresentingComplaintEars addEditPCEars(PresentingComplaintEars presentingComplaintEars) {
		try {
			PresentingComplaintEarsCollection presentingComplaintEarsCollection = new PresentingComplaintEarsCollection();
			BeanUtil.map(presentingComplaintEars, presentingComplaintEarsCollection);
			if (DPDoctorUtils.anyStringEmpty(presentingComplaintEarsCollection.getId())) {
				presentingComplaintEarsCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(presentingComplaintEarsCollection.getDoctorId())) {
					UserCollection userCollection = userRepository
							.findOne(presentingComplaintEarsCollection.getDoctorId());
					if (userCollection != null) {
						presentingComplaintEarsCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					presentingComplaintEarsCollection.setCreatedBy("ADMIN");
				}
			} else {
				PresentingComplaintEarsCollection oldPresentingComplaintEarCollection = presentingComplaintEarsRepository
						.findOne(presentingComplaintEarsCollection.getId());
				presentingComplaintEarsCollection.setCreatedBy(oldPresentingComplaintEarCollection.getCreatedBy());
				presentingComplaintEarsCollection.setCreatedTime(oldPresentingComplaintEarCollection.getCreatedTime());
				presentingComplaintEarsCollection.setDiscarded(oldPresentingComplaintEarCollection.getDiscarded());
			}
			presentingComplaintEarsCollection = presentingComplaintEarsRepository
					.save(presentingComplaintEarsCollection);

			BeanUtil.map(presentingComplaintEarsCollection, presentingComplaintEars);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return presentingComplaintEars;
	}

	@Override
	@Transactional
	public PresentingComplaintThroat addEditPCThroat(PresentingComplaintThroat presentingComplaintThroat) {
		try {
			PresentingComplaintThroatCollection presentingComplaintThroatCollection = new PresentingComplaintThroatCollection();
			BeanUtil.map(presentingComplaintThroat, presentingComplaintThroatCollection);
			if (DPDoctorUtils.anyStringEmpty(presentingComplaintThroatCollection.getId())) {
				presentingComplaintThroatCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(presentingComplaintThroatCollection.getDoctorId())) {
					UserCollection userCollection = userRepository
							.findOne(presentingComplaintThroatCollection.getDoctorId());
					if (userCollection != null) {
						presentingComplaintThroatCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					presentingComplaintThroatCollection.setCreatedBy("ADMIN");
				}
			} else {
				PresentingComplaintThroatCollection oldPresentingComplaintThroatCollection = presentingComplaintThroatRepository
						.findOne(presentingComplaintThroatCollection.getId());
				presentingComplaintThroatCollection.setCreatedBy(oldPresentingComplaintThroatCollection.getCreatedBy());
				presentingComplaintThroatCollection
						.setCreatedTime(oldPresentingComplaintThroatCollection.getCreatedTime());
				presentingComplaintThroatCollection.setDiscarded(oldPresentingComplaintThroatCollection.getDiscarded());
			}
			presentingComplaintThroatCollection = presentingComplaintThroatRepository
					.save(presentingComplaintThroatCollection);

			BeanUtil.map(presentingComplaintThroatCollection, presentingComplaintThroat);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return presentingComplaintThroat;
	}

	@Override
	@Transactional
	public PresentingComplaintOralCavity addEditPCOralCavity(
			PresentingComplaintOralCavity presentingComplaintOralCavity) {
		try {
			PresentingComplaintOralCavityCollection presentingComplaintOralCavityCollection = new PresentingComplaintOralCavityCollection();
			BeanUtil.map(presentingComplaintOralCavity, presentingComplaintOralCavityCollection);
			if (DPDoctorUtils.anyStringEmpty(presentingComplaintOralCavityCollection.getId())) {
				presentingComplaintOralCavityCollection.setCreatedTime(new Date());
				if (!DPDoctorUtils.anyStringEmpty(presentingComplaintOralCavityCollection.getDoctorId())) {
					UserCollection userCollection = userRepository
							.findOne(presentingComplaintOralCavityCollection.getDoctorId());
					if (userCollection != null) {
						presentingComplaintOralCavityCollection
								.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				} else {
					presentingComplaintOralCavityCollection.setCreatedBy("ADMIN");
				}
			} else {
				PresentingComplaintOralCavityCollection oldPresentingComplaintOralCavityCollection = presentingComplaintOralCavityRepository
						.findOne(presentingComplaintOralCavityCollection.getId());
				presentingComplaintOralCavityCollection
						.setCreatedBy(oldPresentingComplaintOralCavityCollection.getCreatedBy());
				presentingComplaintOralCavityCollection
						.setCreatedTime(oldPresentingComplaintOralCavityCollection.getCreatedTime());
				presentingComplaintOralCavityCollection
						.setDiscarded(oldPresentingComplaintOralCavityCollection.getDiscarded());
			}
			presentingComplaintOralCavityCollection = presentingComplaintOralCavityRepository
					.save(presentingComplaintOralCavityCollection);

			BeanUtil.map(presentingComplaintOralCavityCollection, presentingComplaintOralCavity);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return presentingComplaintOralCavity;
	}

	@SuppressWarnings("unchecked")
	private List<PA> getCustomGlobalPA(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<PA> response = new ArrayList<PA>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<PA> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, specialities, null),
							PACollection.class, PA.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/A");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<PA> getGlobalPA(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
		List<PA> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<PA> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page, size,
					updatedTime, discarded, null, null, specialities, null), PACollection.class, PA.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/A");
		}
		return response;
	}

	private List<PA> getCustomPA(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<PA> response = null;
		try {
			AggregationResults<PA> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page, size,
					doctorId, locationId, hospitalId, updatedTime, discarded, null, null, null), PACollection.class,
					PA.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/A");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<ProcedureNote> getCustomGlobalProcedureNote(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<ProcedureNote> response = new ArrayList<ProcedureNote>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<ProcedureNote> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					ProcedureNoteCollection.class, ProcedureNote.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Precedure Note");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<ProcedureNote> getGlobalProcedureNote(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<ProcedureNote> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<ProcedureNote> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities, null),
					ProcedureNoteCollection.class, ProcedureNote.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Precedure Note");
		}
		return response;
	}

	private List<ProcedureNote> getCustomProcedureNote(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<ProcedureNote> response = null;
		try {
			AggregationResults<ProcedureNote> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							ProcedureNoteCollection.class, ProcedureNote.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Procedure Note");
		}
		return response;
	}

	@Override
	public PS deletePS(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
		PS response = null;
		try {
			PSCollection psCollection = psRepository.findOne(new ObjectId(id));
			if (psCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(psCollection.getDoctorId(), psCollection.getHospitalId(),
						psCollection.getLocationId())) {
					if (psCollection.getDoctorId().toString().equals(doctorId)
							&& psCollection.getHospitalId().toString().equals(hospitalId)
							&& psCollection.getLocationId().toString().equals(locationId)) {

						psCollection.setDiscarded(discarded);
						psCollection.setUpdatedTime(new Date());
						psRepository.save(psCollection);
						response = new PS();
						BeanUtil.map(psCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					psCollection.setDiscarded(discarded);
					psCollection.setUpdatedTime(new Date());
					psRepository.save(psCollection);
					response = new PS();
					BeanUtil.map(psCollection, response);
				}
			} else {
				logger.warn("Indication of USG not found!");
				throw new BusinessException(ServiceError.NoRecord, "P/A not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public XRayDetails deleteXRayDetails(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		XRayDetails response = null;
		try {
			XRayDetailsCollection xRayDetailsCollection = xRayDetailsRepository.findOne(new ObjectId(id));
			if (xRayDetailsCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(xRayDetailsCollection.getDoctorId(),
						xRayDetailsCollection.getHospitalId(), xRayDetailsCollection.getLocationId())) {
					if (xRayDetailsCollection.getDoctorId().toString().equals(doctorId)
							&& xRayDetailsCollection.getHospitalId().toString().equals(hospitalId)
							&& xRayDetailsCollection.getLocationId().toString().equals(locationId)) {

						xRayDetailsCollection.setDiscarded(discarded);
						xRayDetailsCollection.setUpdatedTime(new Date());
						xRayDetailsRepository.save(xRayDetailsCollection);
						response = new XRayDetails();
						BeanUtil.map(xRayDetailsCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					xRayDetailsCollection.setDiscarded(discarded);
					xRayDetailsCollection.setUpdatedTime(new Date());
					xRayDetailsRepository.save(xRayDetailsCollection);
					response = new XRayDetails();
					BeanUtil.map(xRayDetailsCollection, response);
				}
			} else {
				logger.warn("X ray details not found!");
				throw new BusinessException(ServiceError.NoRecord, "X-RAY details not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public Echo deleteEcho(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
		Echo response = null;
		try {
			EchoCollection echoCollection = echoRepository.findOne(new ObjectId(id));
			if (echoCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(echoCollection.getDoctorId(), echoCollection.getHospitalId(),
						echoCollection.getLocationId())) {
					if (echoCollection.getDoctorId().toString().equals(doctorId)
							&& echoCollection.getHospitalId().toString().equals(hospitalId)
							&& echoCollection.getLocationId().toString().equals(locationId)) {

						echoCollection.setDiscarded(discarded);
						echoCollection.setUpdatedTime(new Date());
						echoRepository.save(echoCollection);
						response = new Echo();
						BeanUtil.map(echoCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					echoCollection.setDiscarded(discarded);
					echoCollection.setUpdatedTime(new Date());
					echoRepository.save(echoCollection);
					response = new Echo();
					BeanUtil.map(echoCollection, response);
				}
			} else {
				logger.warn("Echo not found!");
				throw new BusinessException(ServiceError.NoRecord, "Echo not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public ECGDetails deleteECGDetails(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		ECGDetails response = null;
		try {
			ECGDetailsCollection ecgDetailsCollection = ecgDetailsRepository.findOne(new ObjectId(id));
			if (ecgDetailsCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(ecgDetailsCollection.getDoctorId(),
						ecgDetailsCollection.getHospitalId(), ecgDetailsCollection.getLocationId())) {
					if (ecgDetailsCollection.getDoctorId().toString().equals(doctorId)
							&& ecgDetailsCollection.getHospitalId().toString().equals(hospitalId)
							&& ecgDetailsCollection.getLocationId().toString().equals(locationId)) {

						ecgDetailsCollection.setDiscarded(discarded);
						ecgDetailsCollection.setUpdatedTime(new Date());
						ecgDetailsRepository.save(ecgDetailsCollection);
						response = new ECGDetails();
						BeanUtil.map(ecgDetailsCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					ecgDetailsCollection.setDiscarded(discarded);
					ecgDetailsCollection.setUpdatedTime(new Date());
					ecgDetailsRepository.save(ecgDetailsCollection);
					response = new ECGDetails();
					BeanUtil.map(ecgDetailsCollection, response);
				}
			} else {
				logger.warn("eCG details not found!");
				throw new BusinessException(ServiceError.NoRecord, "ECG details not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public Holter deleteHolter(String id, String doctorId, String locationId, String hospitalId, Boolean discarded) {
		Holter response = null;
		try {
			HolterCollection holterCollection = holterRepository.findOne(new ObjectId(id));
			if (holterCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(holterCollection.getDoctorId(), holterCollection.getHospitalId(),
						holterCollection.getLocationId())) {
					if (holterCollection.getDoctorId().toString().equals(doctorId)
							&& holterCollection.getHospitalId().toString().equals(hospitalId)
							&& holterCollection.getLocationId().toString().equals(locationId)) {

						holterCollection.setDiscarded(discarded);
						holterCollection.setUpdatedTime(new Date());
						holterRepository.save(holterCollection);
						response = new Holter();
						BeanUtil.map(holterCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					holterCollection.setDiscarded(discarded);
					holterCollection.setUpdatedTime(new Date());
					holterRepository.save(holterCollection);
					response = new Holter();
					BeanUtil.map(holterCollection, response);
				}
			} else {
				logger.warn("Holter not found!");
				throw new BusinessException(ServiceError.NoRecord, "Holter not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public ProcedureNote deleteProcedureNote(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		ProcedureNote response = null;
		try {
			ProcedureNoteCollection procedureNoteCollection = procedureNoteRepository.findOne(new ObjectId(id));
			if (procedureNoteCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(procedureNoteCollection.getDoctorId(),
						procedureNoteCollection.getHospitalId(), procedureNoteCollection.getLocationId())) {
					if (procedureNoteCollection.getDoctorId().toString().equals(doctorId)
							&& procedureNoteCollection.getHospitalId().toString().equals(hospitalId)
							&& procedureNoteCollection.getLocationId().toString().equals(locationId)) {

						procedureNoteCollection.setDiscarded(discarded);
						procedureNoteCollection.setUpdatedTime(new Date());
						procedureNoteRepository.save(procedureNoteCollection);
						response = new ProcedureNote();
						BeanUtil.map(procedureNoteCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					procedureNoteCollection.setDiscarded(discarded);
					procedureNoteCollection.setUpdatedTime(new Date());
					procedureNoteRepository.save(procedureNoteCollection);
					response = new ProcedureNote();
					BeanUtil.map(procedureNoteCollection, response);
				}
			} else {
				logger.warn("Holter not found!");
				throw new BusinessException(ServiceError.NoRecord, "Procedure Note not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public PresentingComplaintNose deletePCNose(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		PresentingComplaintNose response = null;
		try {
			PresentingComplaintNoseCollection presentingComplaintNoseCollection = presentingComplaintNotesRepository
					.findOne(new ObjectId(id));
			if (presentingComplaintNoseCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(presentingComplaintNoseCollection.getDoctorId(),
						presentingComplaintNoseCollection.getHospitalId(),
						presentingComplaintNoseCollection.getLocationId())) {
					if (presentingComplaintNoseCollection.getDoctorId().toString().equals(doctorId)
							&& presentingComplaintNoseCollection.getHospitalId().toString().equals(hospitalId)
							&& presentingComplaintNoseCollection.getLocationId().toString().equals(locationId)) {

						presentingComplaintNoseCollection.setDiscarded(discarded);
						presentingComplaintNoseCollection.setUpdatedTime(new Date());
						presentingComplaintNotesRepository.save(presentingComplaintNoseCollection);
						response = new PresentingComplaintNose();
						BeanUtil.map(presentingComplaintNoseCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					presentingComplaintNoseCollection.setDiscarded(discarded);
					presentingComplaintNoseCollection.setUpdatedTime(new Date());
					presentingComplaintNotesRepository.save(presentingComplaintNoseCollection);
					response = new PresentingComplaintNose();
					BeanUtil.map(presentingComplaintNoseCollection, response);
				}
			} else {
				logger.warn("PC nose not found!");
				throw new BusinessException(ServiceError.NoRecord, "PC nose not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public PresentingComplaintEars deletePCEars(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		PresentingComplaintEars response = null;
		try {
			PresentingComplaintEarsCollection presentingComplaintEarsCollection = presentingComplaintEarsRepository
					.findOne(new ObjectId(id));
			if (presentingComplaintEarsCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(presentingComplaintEarsCollection.getDoctorId(),
						presentingComplaintEarsCollection.getHospitalId(),
						presentingComplaintEarsCollection.getLocationId())) {
					if (presentingComplaintEarsCollection.getDoctorId().toString().equals(doctorId)
							&& presentingComplaintEarsCollection.getHospitalId().toString().equals(hospitalId)
							&& presentingComplaintEarsCollection.getLocationId().toString().equals(locationId)) {

						presentingComplaintEarsCollection.setDiscarded(discarded);
						presentingComplaintEarsCollection.setUpdatedTime(new Date());
						presentingComplaintEarsRepository.save(presentingComplaintEarsCollection);
						response = new PresentingComplaintEars();
						BeanUtil.map(presentingComplaintEarsCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					presentingComplaintEarsCollection.setDiscarded(discarded);
					presentingComplaintEarsCollection.setUpdatedTime(new Date());
					presentingComplaintEarsRepository.save(presentingComplaintEarsCollection);
					response = new PresentingComplaintEars();
					BeanUtil.map(presentingComplaintEarsCollection, response);
				}
			} else {
				logger.warn("PC ears not found!");
				throw new BusinessException(ServiceError.NoRecord, "PC ears not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public PresentingComplaintOralCavity deletePCOralCavity(String id, String doctorId, String locationId,
			String hospitalId, Boolean discarded) {
		PresentingComplaintOralCavity response = null;
		try {
			PresentingComplaintOralCavityCollection presentingComplaintOralCavityCollection = presentingComplaintOralCavityRepository
					.findOne(new ObjectId(id));
			if (presentingComplaintOralCavityCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(presentingComplaintOralCavityCollection.getDoctorId(),
						presentingComplaintOralCavityCollection.getHospitalId(),
						presentingComplaintOralCavityCollection.getLocationId())) {
					if (presentingComplaintOralCavityCollection.getDoctorId().toString().equals(doctorId)
							&& presentingComplaintOralCavityCollection.getHospitalId().toString().equals(hospitalId)
							&& presentingComplaintOralCavityCollection.getLocationId().toString().equals(locationId)) {

						presentingComplaintOralCavityCollection.setDiscarded(discarded);
						presentingComplaintOralCavityCollection.setUpdatedTime(new Date());
						presentingComplaintOralCavityRepository.save(presentingComplaintOralCavityCollection);
						response = new PresentingComplaintOralCavity();
						BeanUtil.map(presentingComplaintOralCavityCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					presentingComplaintOralCavityCollection.setDiscarded(discarded);
					presentingComplaintOralCavityCollection.setUpdatedTime(new Date());
					presentingComplaintOralCavityRepository.save(presentingComplaintOralCavityCollection);
					response = new PresentingComplaintOralCavity();
					BeanUtil.map(presentingComplaintOralCavityCollection, response);
				}
			} else {
				logger.warn("PC ears not found!");
				throw new BusinessException(ServiceError.NoRecord, "PC ears not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public PresentingComplaintThroat deletePCThroat(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		PresentingComplaintThroat response = null;
		try {
			PresentingComplaintThroatCollection presentingComplaintThroatCollection = presentingComplaintThroatRepository
					.findOne(new ObjectId(id));
			if (presentingComplaintThroatCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(presentingComplaintThroatCollection.getDoctorId(),
						presentingComplaintThroatCollection.getHospitalId(),
						presentingComplaintThroatCollection.getLocationId())) {
					if (presentingComplaintThroatCollection.getDoctorId().toString().equals(doctorId)
							&& presentingComplaintThroatCollection.getHospitalId().toString().equals(hospitalId)
							&& presentingComplaintThroatCollection.getLocationId().toString().equals(locationId)) {

						presentingComplaintThroatCollection.setDiscarded(discarded);
						presentingComplaintThroatCollection.setUpdatedTime(new Date());
						presentingComplaintThroatRepository.save(presentingComplaintThroatCollection);
						response = new PresentingComplaintThroat();
						BeanUtil.map(presentingComplaintThroatCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					presentingComplaintThroatCollection.setDiscarded(discarded);
					presentingComplaintThroatCollection.setUpdatedTime(new Date());
					presentingComplaintThroatRepository.save(presentingComplaintThroatCollection);
					response = new PresentingComplaintThroat();
					BeanUtil.map(presentingComplaintThroatCollection, response);
				}
			} else {
				logger.warn("PC ears not found!");
				throw new BusinessException(ServiceError.NoRecord, "PC ears not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public NeckExamination deleteNeckExam(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		NeckExamination response = null;
		try {
			NeckExaminationCollection neckExaminationCollection = neckExaminationRepository.findOne(new ObjectId(id));
			if (neckExaminationCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(neckExaminationCollection.getDoctorId(),
						neckExaminationCollection.getHospitalId(), neckExaminationCollection.getLocationId())) {
					if (neckExaminationCollection.getDoctorId().toString().equals(doctorId)
							&& neckExaminationCollection.getHospitalId().toString().equals(hospitalId)
							&& neckExaminationCollection.getLocationId().toString().equals(locationId)) {

						neckExaminationCollection.setDiscarded(discarded);
						neckExaminationCollection.setUpdatedTime(new Date());
						neckExaminationRepository.save(neckExaminationCollection);
						response = new NeckExamination();
						BeanUtil.map(neckExaminationCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					neckExaminationCollection.setDiscarded(discarded);
					neckExaminationCollection.setUpdatedTime(new Date());
					neckExaminationRepository.save(neckExaminationCollection);
					response = new NeckExamination();
					BeanUtil.map(neckExaminationCollection, response);
				}
			} else {
				logger.warn("PC ears not found!");
				throw new BusinessException(ServiceError.NoRecord, "PC ears not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public NoseExamination deleteNoseExam(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		NoseExamination response = null;
		try {
			NoseExaminationCollection noseExaminationCollection = noseExaminationRepository.findOne(new ObjectId(id));
			if (noseExaminationCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(noseExaminationCollection.getDoctorId(),
						noseExaminationCollection.getHospitalId(), noseExaminationCollection.getLocationId())) {
					if (noseExaminationCollection.getDoctorId().toString().equals(doctorId)
							&& noseExaminationCollection.getHospitalId().toString().equals(hospitalId)
							&& noseExaminationCollection.getLocationId().toString().equals(locationId)) {

						noseExaminationCollection.setDiscarded(discarded);
						noseExaminationCollection.setUpdatedTime(new Date());
						noseExaminationRepository.save(noseExaminationCollection);
						response = new NoseExamination();
						BeanUtil.map(noseExaminationCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					noseExaminationCollection.setDiscarded(discarded);
					noseExaminationCollection.setUpdatedTime(new Date());
					noseExaminationRepository.save(noseExaminationCollection);
					response = new NoseExamination();
					BeanUtil.map(noseExaminationCollection, response);
				}
			} else {
				logger.warn("PC ears not found!");
				throw new BusinessException(ServiceError.NoRecord, "PC ears not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public OralCavityAndThroatExamination deleteOralCavityThroatExam(String id, String doctorId, String locationId,
			String hospitalId, Boolean discarded) {
		OralCavityAndThroatExamination response = null;
		try {
			OralCavityAndThroatExaminationCollection oralCavityAndThroatExaminationCollection = oralCavityThroatExaminationRepository
					.findOne(new ObjectId(id));
			if (oralCavityAndThroatExaminationCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(oralCavityAndThroatExaminationCollection.getDoctorId(),
						oralCavityAndThroatExaminationCollection.getHospitalId(),
						oralCavityAndThroatExaminationCollection.getLocationId())) {
					if (oralCavityAndThroatExaminationCollection.getDoctorId().toString().equals(doctorId)
							&& oralCavityAndThroatExaminationCollection.getHospitalId().toString().equals(hospitalId)
							&& oralCavityAndThroatExaminationCollection.getLocationId().toString().equals(locationId)) {

						oralCavityAndThroatExaminationCollection.setDiscarded(discarded);
						oralCavityAndThroatExaminationCollection.setUpdatedTime(new Date());
						oralCavityThroatExaminationRepository.save(oralCavityAndThroatExaminationCollection);
						response = new OralCavityAndThroatExamination();
						BeanUtil.map(oralCavityAndThroatExaminationCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					oralCavityAndThroatExaminationCollection.setDiscarded(discarded);
					oralCavityAndThroatExaminationCollection.setUpdatedTime(new Date());
					oralCavityThroatExaminationRepository.save(oralCavityAndThroatExaminationCollection);
					response = new OralCavityAndThroatExamination();
					BeanUtil.map(oralCavityAndThroatExaminationCollection, response);
				}
			} else {
				logger.warn("PC ears not found!");
				throw new BusinessException(ServiceError.NoRecord, "PC ears not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public EarsExamination deleteEarsExam(String id, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		EarsExamination response = null;
		try {
			EarsExaminationCollection earsExaminationCollection = earsExaminationRepository.findOne(new ObjectId(id));
			if (earsExaminationCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(earsExaminationCollection.getDoctorId(),
						earsExaminationCollection.getHospitalId(), earsExaminationCollection.getLocationId())) {
					if (earsExaminationCollection.getDoctorId().toString().equals(doctorId)
							&& earsExaminationCollection.getHospitalId().toString().equals(hospitalId)
							&& earsExaminationCollection.getLocationId().toString().equals(locationId)) {

						earsExaminationCollection.setDiscarded(discarded);
						earsExaminationCollection.setUpdatedTime(new Date());
						earsExaminationRepository.save(earsExaminationCollection);
						response = new EarsExamination();
						BeanUtil.map(earsExaminationCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					earsExaminationCollection.setDiscarded(discarded);
					earsExaminationCollection.setUpdatedTime(new Date());
					earsExaminationRepository.save(earsExaminationCollection);
					response = new EarsExamination();
					BeanUtil.map(earsExaminationCollection, response);
				}
			} else {
				logger.warn("PC ears not found!");
				throw new BusinessException(ServiceError.NoRecord, "PC ears not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@Override
	public IndirectLarygoscopyExamination deleteIndirectLarygoscopyExam(String id, String doctorId, String locationId,
			String hospitalId, Boolean discarded) {
		IndirectLarygoscopyExamination response = null;
		try {
			IndirectLarygoscopyExaminationCollection indirectLarygoscopyExaminationCollection = indirectLarygoscopyExaminationRepository
					.findOne(new ObjectId(id));
			if (indirectLarygoscopyExaminationCollection != null) {
				if (!DPDoctorUtils.anyStringEmpty(indirectLarygoscopyExaminationCollection.getDoctorId(),
						indirectLarygoscopyExaminationCollection.getHospitalId(),
						indirectLarygoscopyExaminationCollection.getLocationId())) {
					if (indirectLarygoscopyExaminationCollection.getDoctorId().toString().equals(doctorId)
							&& indirectLarygoscopyExaminationCollection.getHospitalId().toString().equals(hospitalId)
							&& indirectLarygoscopyExaminationCollection.getLocationId().toString().equals(locationId)) {

						indirectLarygoscopyExaminationCollection.setDiscarded(discarded);
						indirectLarygoscopyExaminationCollection.setUpdatedTime(new Date());
						indirectLarygoscopyExaminationRepository.save(indirectLarygoscopyExaminationCollection);
						response = new IndirectLarygoscopyExamination();
						BeanUtil.map(indirectLarygoscopyExaminationCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					indirectLarygoscopyExaminationCollection.setDiscarded(discarded);
					indirectLarygoscopyExaminationCollection.setUpdatedTime(new Date());
					indirectLarygoscopyExaminationRepository.save(indirectLarygoscopyExaminationCollection);
					response = new IndirectLarygoscopyExamination();
					BeanUtil.map(indirectLarygoscopyExaminationCollection, response);
				}
			} else {
				logger.warn("PC ears not found!");
				throw new BusinessException(ServiceError.NoRecord, "PC ears not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<PS> getGlobalPS(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
		List<PS> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<PS> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page, size,
					updatedTime, discarded, null, null, specialities, null), PSCollection.class, PS.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/S");
		}
		return response;
	}

	private List<PS> getCustomPS(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<PS> response = null;
		try {
			AggregationResults<PS> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page, size,
					doctorId, locationId, hospitalId, updatedTime, discarded, null, null, null), PSCollection.class,
					PS.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/S");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<PS> getCustomGlobalPS(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<PS> response = new ArrayList<PS>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<PS> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, specialities, null),
							PSCollection.class, PS.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting P/S");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<ECGDetails> getGlobalECGDetails(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<ECGDetails> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<ECGDetails> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page,
					size, updatedTime, discarded, null, null, specialities, null), ECGDetailsCollection.class,
					ECGDetails.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting ECG Details");
		}
		return response;
	}

	private List<ECGDetails> getCustomECGDetails(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<ECGDetails> response = null;
		try {
			AggregationResults<ECGDetails> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							ECGDetailsCollection.class, ECGDetails.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting ECG Details");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<ECGDetails> getCustomGlobalECGDetails(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<ECGDetails> response = new ArrayList<ECGDetails>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<ECGDetails> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					ECGDetailsCollection.class, ECGDetails.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting ECG details");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<XRayDetails> getGlobalXRayDetails(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<XRayDetails> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<XRayDetails> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities, null),
					XRayDetailsCollection.class, XRayDetails.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting XRay Details");
		}
		return response;
	}

	private List<XRayDetails> getCustomXRayDetails(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<XRayDetails> response = null;
		try {
			AggregationResults<XRayDetails> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							XRayDetailsCollection.class, XRayDetails.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting XRay Details");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<XRayDetails> getCustomGlobalXRayDetails(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<XRayDetails> response = new ArrayList<XRayDetails>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<XRayDetails> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					XRayDetailsCollection.class, XRayDetails.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting XRay Details");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<Echo> getGlobalEcho(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
		List<Echo> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<Echo> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page, size,
					updatedTime, discarded, null, null, specialities, null), EchoCollection.class, Echo.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Echo");
		}
		return response;
	}

	private List<Echo> getCustomEcho(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<Echo> response = null;
		try {
			AggregationResults<Echo> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page, size,
					doctorId, locationId, hospitalId, updatedTime, discarded, null, null, null), EchoCollection.class,
					Echo.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Echo");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Echo> getCustomGlobalEcho(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<Echo> response = new ArrayList<Echo>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<Echo> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, specialities, null),
							EchoCollection.class, Echo.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Echo");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<Holter> getGlobalHolter(int page, int size, String doctorId, String updatedTime, Boolean discarded) {
		List<Holter> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<Holter> results = mongoTemplate.aggregate(DPDoctorUtils.createGlobalAggregation(page,
					size, updatedTime, discarded, null, null, specialities, null), HolterCollection.class,
					Holter.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Holter");
		}
		return response;
	}

	private List<Holter> getCustomHolter(int page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded) {
		List<Holter> response = null;
		try {
			AggregationResults<Holter> results = mongoTemplate.aggregate(DPDoctorUtils.createCustomAggregation(page,
					size, doctorId, locationId, hospitalId, updatedTime, discarded, null, null, null),
					HolterCollection.class, Holter.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Holter");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<Holter> getCustomGlobalHolter(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<Holter> response = new ArrayList<Holter>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<Holter> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					HolterCollection.class, Holter.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Holter");
		}
		return response;

	}

	/*
	 * private Set<String> compareGlobalElements(Set<String> editedElements,
	 * Set<String> globalElements) { editedElements.removeAll(globalElements);
	 * return editedElements;
	 * 
	 * }
	 * 
	 * private List<String> splitCSV(String value) { List<String> list = new
	 * ArrayList<String>(Arrays.asList(value.trim().split("\\s*,\\s*"))); return
	 * list;
	 * 
	 * }
	 */
	@SuppressWarnings("unchecked")
	private List<PresentingComplaintNose> getCustomGlobalPCNOse(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<PresentingComplaintNose> response = new ArrayList<PresentingComplaintNose>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<PresentingComplaintNose> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					PresentingComplaintNoseCollection.class, PresentingComplaintNose.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Precedure Note");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<PresentingComplaintNose> getGlobalPCNOse(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<PresentingComplaintNose> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<PresentingComplaintNose> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null,
									specialities, null),
							PresentingComplaintNoseCollection.class, PresentingComplaintNose.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Precedure Note");
		}
		return response;
	}

	private List<PresentingComplaintNose> getCustomPCNose(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<PresentingComplaintNose> response = null;
		try {
			AggregationResults<PresentingComplaintNose> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded, null, null, null),
					PresentingComplaintNoseCollection.class, PresentingComplaintNose.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Procedure Note");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<PresentingComplaintEars> getCustomGlobalPCEars(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<PresentingComplaintEars> response = new ArrayList<PresentingComplaintEars>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<PresentingComplaintEars> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					PresentingComplaintEarsCollection.class, PresentingComplaintEars.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Precedure Note");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<PresentingComplaintEars> getGlobalPCEars(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<PresentingComplaintEars> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<PresentingComplaintEars> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null,
									specialities, null),
							PresentingComplaintEarsCollection.class, PresentingComplaintEars.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Precedure Note");
		}
		return response;
	}

	private List<PresentingComplaintEars> getCustomPCEars(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<PresentingComplaintEars> response = null;
		try {
			AggregationResults<PresentingComplaintEars> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded, null, null, null),
					PresentingComplaintEarsCollection.class, PresentingComplaintEars.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Procedure Note");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<PresentingComplaintThroat> getCustomGlobalPCThroat(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<PresentingComplaintThroat> response = new ArrayList<PresentingComplaintThroat>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<PresentingComplaintThroat> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					PresentingComplaintThroatCollection.class, PresentingComplaintThroat.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Precedure Note");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<PresentingComplaintThroat> getGlobalPCThroat(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<PresentingComplaintThroat> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<PresentingComplaintThroat> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null,
									specialities, null),
							PresentingComplaintThroatCollection.class, PresentingComplaintThroat.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Precedure Note");
		}
		return response;
	}

	private List<PresentingComplaintThroat> getCustomPCThroat(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<PresentingComplaintThroat> response = null;
		try {
			AggregationResults<PresentingComplaintThroat> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded, null, null, null),
					PresentingComplaintThroatCollection.class, PresentingComplaintThroat.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Procedure Note");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<PresentingComplaintOralCavity> getCustomGlobalPCOralCavity(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<PresentingComplaintOralCavity> response = new ArrayList<PresentingComplaintOralCavity>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<PresentingComplaintOralCavity> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					PresentingComplaintOralCavityCollection.class, PresentingComplaintOralCavity.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Precedure Note");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<PresentingComplaintOralCavity> getGlobalPCOralCavity(int page, int size, String doctorId,
			String updatedTime, Boolean discarded) {
		List<PresentingComplaintOralCavity> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<PresentingComplaintOralCavity> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null,
									specialities, null),
							PresentingComplaintOralCavityCollection.class, PresentingComplaintOralCavity.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Precedure Note");
		}
		return response;
	}

	private List<PresentingComplaintOralCavity> getCustomPCOralCavity(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<PresentingComplaintOralCavity> response = null;
		try {
			AggregationResults<PresentingComplaintOralCavity> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded, null, null, null),
					PresentingComplaintOralCavityCollection.class, PresentingComplaintOralCavity.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Procedure Note");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<NoseExamination> getCustomGlobalNoseExam(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<NoseExamination> response = new ArrayList<NoseExamination>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<NoseExamination> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					NoseExaminationCollection.class, NoseExamination.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Precedure Note");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<NoseExamination> getGlobalNoseExam(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<NoseExamination> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<NoseExamination> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities, null),
					NoseExaminationCollection.class, NoseExamination.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Precedure Note");
		}
		return response;
	}

	private List<NoseExamination> getCustomNoseExam(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<NoseExamination> response = null;
		try {
			AggregationResults<NoseExamination> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							NoseExaminationCollection.class, NoseExamination.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Procedure Note");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<NeckExamination> getCustomGlobalNeckExam(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<NeckExamination> response = new ArrayList<NeckExamination>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<NeckExamination> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					NeckExaminationCollection.class, NeckExamination.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Precedure Note");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<NeckExamination> getGlobalNeckExam(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<NeckExamination> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<NeckExamination> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities, null),
					NeckExaminationCollection.class, NeckExamination.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Precedure Note");
		}
		return response;
	}

	private List<NeckExamination> getCustomNeckExam(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<NeckExamination> response = null;
		try {
			AggregationResults<NeckExamination> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							NeckExaminationCollection.class, NeckExamination.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Procedure Note");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<EarsExamination> getCustomGlobalEarsExam(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<EarsExamination> response = new ArrayList<EarsExamination>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<EarsExamination> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					EarsExaminationCollection.class, EarsExamination.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Precedure Note");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<EarsExamination> getGlobalEarsExam(int page, int size, String doctorId, String updatedTime,
			Boolean discarded) {
		List<EarsExamination> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<EarsExamination> results = mongoTemplate.aggregate(DPDoctorUtils
					.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities, null),
					EarsExaminationCollection.class, EarsExamination.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Precedure Note");
		}
		return response;
	}

	private List<EarsExamination> getCustomEarsExam(int page, int size, String doctorId, String locationId,
			String hospitalId, String updatedTime, Boolean discarded) {
		List<EarsExamination> response = null;
		try {
			AggregationResults<EarsExamination> results = mongoTemplate
					.aggregate(
							DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId,
									updatedTime, discarded, null, null, null),
							EarsExaminationCollection.class, EarsExamination.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Procedure Note");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<OralCavityAndThroatExamination> getCustomGlobalOralCavityAndThroatExam(int page, int size,
			String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<OralCavityAndThroatExamination> response = new ArrayList<OralCavityAndThroatExamination>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<OralCavityAndThroatExamination> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					OralCavityAndThroatExaminationCollection.class, OralCavityAndThroatExamination.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Precedure Note");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<OralCavityAndThroatExamination> getGlobalOralCavityAndThroat(int page, int size, String doctorId,
			String updatedTime, Boolean discarded) {
		List<OralCavityAndThroatExamination> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<OralCavityAndThroatExamination> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities,
							null),
					OralCavityAndThroatExaminationCollection.class, OralCavityAndThroatExamination.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Precedure Note");
		}
		return response;
	}

	private List<OralCavityAndThroatExamination> getCustomOralCavityAndThroatExam(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<OralCavityAndThroatExamination> response = null;
		try {
			AggregationResults<OralCavityAndThroatExamination> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded, null, null, null),
					OralCavityAndThroatExaminationCollection.class, OralCavityAndThroatExamination.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Procedure Note");
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private List<IndirectLarygoscopyExamination> getCustomGlobalIndirectLarygoscopyExam(int page, int size,
			String doctorId, String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<IndirectLarygoscopyExamination> response = new ArrayList<IndirectLarygoscopyExamination>();
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add(null);
				specialities.add("ALL");
			}

			AggregationResults<IndirectLarygoscopyExamination> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomGlobalAggregation(page, size, doctorId, locationId, hospitalId,
							updatedTime, discarded, null, null, specialities, null),
					IndirectLarygoscopyExaminationCollection.class, IndirectLarygoscopyExamination.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Precedure Note");
		}
		return response;

	}

	@SuppressWarnings("unchecked")
	private List<IndirectLarygoscopyExamination> getGlobalIndirectLarygoscopyExam(int page, int size, String doctorId,
			String updatedTime, Boolean discarded) {
		List<IndirectLarygoscopyExamination> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection == null) {
				logger.warn("No Doctor Found");
				throw new BusinessException(ServiceError.InvalidInput, "No Doctor Found");
			}
			Collection<String> specialities = null;
			if (doctorCollection.getSpecialities() != null && !doctorCollection.getSpecialities().isEmpty()) {
				specialities = CollectionUtils.collect(
						(Collection<?>) specialityRepository.findAll(doctorCollection.getSpecialities()),
						new BeanToPropertyValueTransformer("speciality"));
				specialities.add("ALL");
				specialities.add(null);
			}

			AggregationResults<IndirectLarygoscopyExamination> results = mongoTemplate.aggregate(
					DPDoctorUtils.createGlobalAggregation(page, size, updatedTime, discarded, null, null, specialities,
							null),
					IndirectLarygoscopyExaminationCollection.class, IndirectLarygoscopyExamination.class);
			response = results.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Precedure Note");
		}
		return response;
	}

	private List<IndirectLarygoscopyExamination> getCustomIndirectLarygoscopyExam(int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded) {
		List<IndirectLarygoscopyExamination> response = null;
		try {
			AggregationResults<IndirectLarygoscopyExamination> results = mongoTemplate.aggregate(
					DPDoctorUtils.createCustomAggregation(page, size, doctorId, locationId, hospitalId, updatedTime,
							discarded, null, null, null),
					IndirectLarygoscopyExaminationCollection.class, IndirectLarygoscopyExamination.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Procedure Note");
		}
		return response;
	}

	@Override
	@Transactional
	public List<Diagnoses> getDiagnosesListBySpeciality(String speciality, String searchTerm) {
		List<Diagnoses> response = null;
		Aggregation aggregation = null;
		Criteria criteria = new Criteria().and("speciality").in(speciality);
		if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
			criteria = criteria.orOperator(new Criteria("diagnosis").regex("^" + searchTerm, "i"),
					new Criteria("diagnosis").regex("^" + searchTerm),
					new Criteria("category").regex("^" + searchTerm, "i"),
					new Criteria("category").regex("^" + searchTerm));
		}
		criteria.and("category").exists(true);
		aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
				Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
		AggregationResults<Diagnoses> aggregationResults = mongoTemplate.aggregate(aggregation,
				DiagnosisCollection.class, Diagnoses.class);
		response = aggregationResults.getMappedResults();
		return response;
	}
}
