package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.AcademicProfile;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DentalAssessment;
import com.dpdocter.beans.DoctorSchoolAssociation;
import com.dpdocter.beans.DrugInfo;
import com.dpdocter.beans.ENTAssessment;
import com.dpdocter.beans.EyeAssessment;
import com.dpdocter.beans.GrowthAssessmentAndGeneralBioMetrics;
import com.dpdocter.beans.NutritionAssessment;
import com.dpdocter.beans.NutritionRDA;
import com.dpdocter.beans.PhysicalAssessment;
import com.dpdocter.beans.RegistrationDetails;
import com.dpdocter.beans.UserTreatment;
import com.dpdocter.collections.AcadamicClassCollection;
import com.dpdocter.collections.AcadamicClassSectionCollection;
import com.dpdocter.collections.AcademicProfileCollection;
import com.dpdocter.collections.DentalAssessmentCollection;
import com.dpdocter.collections.DietPlanCollection;
import com.dpdocter.collections.DoctorSchoolAssociationCollection;
import com.dpdocter.collections.DrugInfoCollection;
import com.dpdocter.collections.ENTAssessmentCollection;
import com.dpdocter.collections.EyeAssessmentCollection;
import com.dpdocter.collections.GrowthAssessmentAndGeneralBioMetricsCollection;
import com.dpdocter.collections.NutritionAssessmentCollection;
import com.dpdocter.collections.NutritionRDACollection;
import com.dpdocter.collections.NutritionSchoolAssociationCollection;
import com.dpdocter.collections.PhysicalAssessmentCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.collections.UserTreatmentCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.AcadamicClassRepository;
import com.dpdocter.repository.AcadamicClassSectionRepository;
import com.dpdocter.repository.AcadamicProfileRespository;
import com.dpdocter.repository.DentalAssessmentRepository;
import com.dpdocter.repository.ENTAssessmentRepository;
import com.dpdocter.repository.EyeAssessmentRepository;
import com.dpdocter.repository.GrowthAssessmentAndGeneralBioMetricsRepository;
import com.dpdocter.repository.NutritionAssessmentRepository;
import com.dpdocter.repository.PatientLifeStyleRepository;
import com.dpdocter.repository.PhysicalAssessmentRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.repository.UserTreatmentRepository;
import com.dpdocter.response.AcadamicClassResponse;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.NutritionSchoolAssociationResponse;
import com.dpdocter.response.UserAssessment;
import com.dpdocter.services.CampVisitService;
import com.dpdocter.services.FileManager;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class CampVisitServiceImpl implements CampVisitService {

	private Logger logger = LogManager.getLogger(CampVisitServiceImpl.class);

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private GrowthAssessmentAndGeneralBioMetricsRepository growthAssessmentAndGeneralBioMetricsRepository;

	@Autowired
	private PhysicalAssessmentRepository physicalAssessmentRepository;

	@Autowired
	private ENTAssessmentRepository entAssessmentRepository;

	@Autowired
	private DentalAssessmentRepository dentalAssessmentRepository;

	@Autowired
	private EyeAssessmentRepository eyeAssessmentRepository;

	@Autowired
	private NutritionAssessmentRepository nutritionAssessmentRepository;
	
	@Autowired
	private AcadamicProfileRespository acadamicProfileRespository;
	
	@Autowired
	private AcadamicClassRepository acadamicClassRepository;
	
	@Autowired
	private AcadamicClassSectionRepository acadamicClassSectionRepository;
	
	@Autowired
	private FileManager fileManager;

	@Autowired
	private PatientLifeStyleRepository patientLifeStyleRepository;
	
	@Autowired
	private UserTreatmentRepository userTreatmentRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Override
	@Transactional
	public GrowthAssessmentAndGeneralBioMetrics addEditGrowthAssessmentAndGeneralBioMetrics(
			GrowthAssessmentAndGeneralBioMetrics request) {
		GrowthAssessmentAndGeneralBioMetrics response = null;
		GrowthAssessmentAndGeneralBioMetricsCollection growthAssessmentAndGeneralBioMetricsCollection = null;
		try {
			if (request.getId() == null) {
				growthAssessmentAndGeneralBioMetricsCollection = new GrowthAssessmentAndGeneralBioMetricsCollection();
				request.setCreatedTime(new Date());

			} else {
				growthAssessmentAndGeneralBioMetricsCollection = growthAssessmentAndGeneralBioMetricsRepository
						.findById(new ObjectId(request.getId())).orElse(null);
			}
			BeanUtil.map(request, growthAssessmentAndGeneralBioMetricsCollection);
			growthAssessmentAndGeneralBioMetricsCollection.setImages(request.getImages());
			growthAssessmentAndGeneralBioMetricsCollection = growthAssessmentAndGeneralBioMetricsRepository
					.save(growthAssessmentAndGeneralBioMetricsCollection);

			response = new GrowthAssessmentAndGeneralBioMetrics();
			BeanUtil.map(growthAssessmentAndGeneralBioMetricsCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			// logger.error(e + " Something went wrong");
			throw new BusinessException(ServiceError.Unknown, "Something went wrong");
		}
		return response;
	}

	@Override
	@Transactional
	public GrowthAssessmentAndGeneralBioMetrics getGrowthAssessmentAndGeneralBioMetricsById(String id) {
		GrowthAssessmentAndGeneralBioMetrics response = null;
		GrowthAssessmentAndGeneralBioMetricsCollection growthAssessmentAndGeneralBioMetricsCollection = null;

		try {

			growthAssessmentAndGeneralBioMetricsCollection = growthAssessmentAndGeneralBioMetricsRepository
					.findById(new ObjectId(id)).orElse(null);

			if (growthAssessmentAndGeneralBioMetricsCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}

			growthAssessmentAndGeneralBioMetricsCollection = growthAssessmentAndGeneralBioMetricsRepository
					.save(growthAssessmentAndGeneralBioMetricsCollection);
			if (growthAssessmentAndGeneralBioMetricsCollection != null) {
				response = new GrowthAssessmentAndGeneralBioMetrics();
				BeanUtil.map(growthAssessmentAndGeneralBioMetricsCollection, response);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			// logger.error(e);
		}
		return response;
	}

	@Override
	@Transactional
	public GrowthAssessmentAndGeneralBioMetrics discardGrowthAssessmentAndGeneralBioMetricsById(String id,
			Boolean discarded) {
		GrowthAssessmentAndGeneralBioMetrics response = null;
		GrowthAssessmentAndGeneralBioMetricsCollection growthAssessmentAndGeneralBioMetricsCollection = null;

		try {

			growthAssessmentAndGeneralBioMetricsCollection = growthAssessmentAndGeneralBioMetricsRepository
					.findById(new ObjectId(id)).orElse(null);

			if (growthAssessmentAndGeneralBioMetricsCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			growthAssessmentAndGeneralBioMetricsCollection.setDiscarded(discarded);
			growthAssessmentAndGeneralBioMetricsCollection = growthAssessmentAndGeneralBioMetricsRepository
					.save(growthAssessmentAndGeneralBioMetricsCollection);
			if (growthAssessmentAndGeneralBioMetricsCollection != null) {
				response = new GrowthAssessmentAndGeneralBioMetrics();
				BeanUtil.map(growthAssessmentAndGeneralBioMetricsCollection, response);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			// logger.error(e);
		}
		return response;
	}

	@Override
	@Transactional
	public List<GrowthAssessmentAndGeneralBioMetrics> getGrowthAssessmentAndGeneralBioMetricsList(
			String academicProfileId, String schoolId, String branchId, String doctorId, String updatedTime, int page,
			int size, Boolean isDiscarded) {
		List<GrowthAssessmentAndGeneralBioMetrics> growthAssessmentAndGeneralBioMetrics = null;
		Aggregation aggregation = null;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));

			if (academicProfileId != null) {
				criteria.and("academicProfileId").is(new ObjectId(academicProfileId));
			}

			if (schoolId != null) {
				criteria.and("schoolId").is(new ObjectId(schoolId));
			}

			if (branchId != null) {
				criteria.and("branchId").is(new ObjectId(branchId));
			}

			if (doctorId != null) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (isDiscarded != null) {
				criteria.and("discarded").is(isDiscarded);
			}

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
						Aggregation.skip((long) (page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}

			growthAssessmentAndGeneralBioMetrics = mongoTemplate.aggregate(aggregation,
					GrowthAssessmentAndGeneralBioMetricsCollection.class, GrowthAssessmentAndGeneralBioMetrics.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return growthAssessmentAndGeneralBioMetrics;
	}

	@Override
	@Transactional
	public Integer getGrowthAssessmentAndGeneralBioMetricsListCount(String academicProfileId, String schoolId,
			String branchId, String doctorId, String updatedTime, int page, int size, Boolean isDiscarded) {
		Integer count = 0;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));

			if (academicProfileId != null) {
				criteria.and("academicProfileId").is(new ObjectId(academicProfileId));
			}

			if (schoolId != null) {
				criteria.and("schoolId").is(new ObjectId(schoolId));
			}

			if (branchId != null) {
				criteria.and("branchId").is(new ObjectId(branchId));
			}

			if (doctorId != null) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (isDiscarded != null) {
				criteria.and("discarded").is(isDiscarded);
			}

			count = (int) mongoTemplate.count(new Query(criteria),
					GrowthAssessmentAndGeneralBioMetricsCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return count;
	}

	@Override
	@Transactional
	public PhysicalAssessment addEditPhysicalAssessment(PhysicalAssessment request) {

		PhysicalAssessment response = null;
		PhysicalAssessmentCollection physicalAssessmentCollection = null;
		try {
			if (request.getId() == null) {
				physicalAssessmentCollection = new PhysicalAssessmentCollection();
				request.setCreatedTime(new Date());

			} else {
				physicalAssessmentCollection = physicalAssessmentRepository.findById(new ObjectId(request.getId()))
						.orElse(null);
			}
			BeanUtil.map(request, physicalAssessmentCollection);

			physicalAssessmentCollection.setGeneralSigns(request.getGeneralSigns());
			physicalAssessmentCollection.setHead(request.getHead());
			physicalAssessmentCollection.setLymphatic(request.getLymphatic());
			physicalAssessmentCollection.setSkin(request.getSkin());
			physicalAssessmentCollection.setCardiovascular(request.getCardiovascular());
			physicalAssessmentCollection.setRespiratory(request.getRespiratory());
			physicalAssessmentCollection.setAbdomen(request.getAbdomen());
			physicalAssessmentCollection.setNuerological(request.getNuerological());
			physicalAssessmentCollection.setOrthopedics(request.getOrthopedics());
			physicalAssessmentCollection.setDeficienciesSuspected(request.getDeficienciesSuspected());
			physicalAssessmentCollection.setNervousSystem(request.getNervousSystem());
			physicalAssessmentCollection.setxRay(request.getxRay());
			physicalAssessmentCollection.setBloodTest(request.getBloodTest());
			physicalAssessmentCollection.setStoolTest(request.getStoolTest());
			physicalAssessmentCollection.setUrineTest(request.getUrineTest());
			physicalAssessmentCollection.setImages(request.getImages());
			physicalAssessmentCollection.setSuggestedImages(request.getSuggestedImages());
			physicalAssessmentCollection = physicalAssessmentRepository.save(physicalAssessmentCollection);

			response = new PhysicalAssessment();
			BeanUtil.map(physicalAssessmentCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			// logger.error(e + " Something went wrong");
			throw new BusinessException(ServiceError.Unknown, "Something went wrong");
		}
		return response;
	}

	@Override
	@Transactional
	public PhysicalAssessment getPhysicalAssessmentById(String id) {
		PhysicalAssessment response = null;
		PhysicalAssessmentCollection physicalAssessmentCollection = null;

		try {

			physicalAssessmentCollection = physicalAssessmentRepository.findById(new ObjectId(id)).orElse(null);

			if (physicalAssessmentCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}

			physicalAssessmentCollection = physicalAssessmentRepository.save(physicalAssessmentCollection);
			if (physicalAssessmentCollection != null) {
				response = new PhysicalAssessment();
				BeanUtil.map(physicalAssessmentCollection, response);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			// logger.error(e);
		}
		return response;
	}

	@Override
	@Transactional
	public PhysicalAssessment discardPhysicalAssessment(String id, Boolean discarded) {
		PhysicalAssessment response = null;
		PhysicalAssessmentCollection physicalAssessmentCollection = null;

		try {

			physicalAssessmentCollection = physicalAssessmentRepository.findById(new ObjectId(id)).orElse(null);

			if (physicalAssessmentCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			physicalAssessmentCollection.setDiscarded(discarded);
			physicalAssessmentCollection = physicalAssessmentRepository.save(physicalAssessmentCollection);
			if (physicalAssessmentCollection != null) {
				response = new PhysicalAssessment();
				BeanUtil.map(physicalAssessmentCollection, response);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			// logger.error(e);
		}
		return response;
	}

	@Override
	@Transactional
	public List<PhysicalAssessment> getPhysicalAssessmentList(String academicProfileId, String schoolId,
			String branchId, String doctorId, String updatedTime, int page, int size, Boolean isDiscarded) {
		List<PhysicalAssessment> physicalAssessments = null;
		Aggregation aggregation = null;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));

			if (academicProfileId != null) {
				criteria.and("academicProfileId").is(new ObjectId(academicProfileId));
			}

			if (schoolId != null) {
				criteria.and("schoolId").is(new ObjectId(schoolId));
			}

			if (branchId != null) {
				criteria.and("branchId").is(new ObjectId(branchId));
			}

			if (doctorId != null) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (isDiscarded != null) {
				criteria.and("discarded").is(isDiscarded);
			}

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
						Aggregation.skip(((long) page) * size), Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}

			physicalAssessments = mongoTemplate
					.aggregate(aggregation, PhysicalAssessmentCollection.class, PhysicalAssessment.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return physicalAssessments;
	}

	@Override
	@Transactional
	public Integer getPhysicalAssessmentCount(String academicProfileId, String schoolId, String branchId,
			String doctorId, String updatedTime, int page, int size, Boolean isDiscarded) {
		Integer count = 0;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));

			if (academicProfileId != null) {
				criteria.and("academicProfileId").is(new ObjectId(academicProfileId));
			}

			if (schoolId != null) {
				criteria.and("schoolId").is(new ObjectId(schoolId));
			}

			if (branchId != null) {
				criteria.and("branchId").is(new ObjectId(branchId));
			}

			if (doctorId != null) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (isDiscarded != null) {
				criteria.and("discarded").is(isDiscarded);
			}

			count = (int) mongoTemplate.count(new Query(criteria), PhysicalAssessmentCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return count;
	}

	@Override
	@Transactional
	public ENTAssessment addEditENTAssessment(ENTAssessment request) {

		ENTAssessment response = null;
		ENTAssessmentCollection entAssessmentCollection = null;
		try {
			if (request.getId() == null) {
				entAssessmentCollection = new ENTAssessmentCollection();
				request.setCreatedTime(new Date());

			} else {
				entAssessmentCollection = entAssessmentRepository.findById(new ObjectId(request.getId())).orElse(null);
			}
			BeanUtil.map(request, entAssessmentCollection);
			entAssessmentCollection.setRightEar(request.getRightEar());
			entAssessmentCollection.setLeftEar(request.getLeftEar());
			entAssessmentCollection.setNose(request.getNose());
			entAssessmentCollection.setOralCavityAndThroat(request.getOralCavityAndThroat());
			entAssessmentCollection.setImages(request.getImages());
			entAssessmentCollection = entAssessmentRepository.save(entAssessmentCollection);
			response = new ENTAssessment();
			BeanUtil.map(entAssessmentCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			// logger.error(e + " Something went wrong");
			throw new BusinessException(ServiceError.Unknown, "Something went wrong");
		}
		return response;
	}

	@Override
	@Transactional
	public ENTAssessment getENTAssessmentById(String id) {
		ENTAssessment response = null;
		ENTAssessmentCollection entAssessmentCollection = null;

		try {

			entAssessmentCollection = entAssessmentRepository.findById(new ObjectId(id)).orElse(null);

			if (entAssessmentCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}

			entAssessmentCollection = entAssessmentRepository.save(entAssessmentCollection);
			if (entAssessmentCollection != null) {
				response = new ENTAssessment();
				BeanUtil.map(entAssessmentCollection, response);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			// logger.error(e);
		}
		return response;
	}

	@Override
	@Transactional
	public ENTAssessment discardENTAssessmentById(String id, Boolean discarded) {
		ENTAssessment response = null;
		ENTAssessmentCollection entAssessmentCollection = null;

		try {

			entAssessmentCollection = entAssessmentRepository.findById(new ObjectId(id)).orElse(null);

			if (entAssessmentCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			entAssessmentCollection.setDiscarded(discarded);
			entAssessmentCollection = entAssessmentRepository.save(entAssessmentCollection);
			if (entAssessmentCollection != null) {
				response = new ENTAssessment();
				BeanUtil.map(entAssessmentCollection, response);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			// logger.error(e);
		}
		return response;
	}

	@Override
	@Transactional
	public List<ENTAssessment> getENTAssessmentList(String academicProfileId, String schoolId, String branchId,
			String doctorId, String updatedTime, int page, int size, Boolean isDiscarded) {
		List<ENTAssessment> entAssessments = null;
		Aggregation aggregation = null;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));

			if (academicProfileId != null) {
				criteria.and("academicProfileId").is(new ObjectId(academicProfileId));
			}

			if (schoolId != null) {
				criteria.and("schoolId").is(new ObjectId(schoolId));
			}

			if (branchId != null) {
				criteria.and("branchId").is(new ObjectId(branchId));
			}

			if (doctorId != null) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (isDiscarded != null) {
				criteria.and("discarded").is(isDiscarded);
			}

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}

			entAssessments = mongoTemplate.aggregate(aggregation, ENTAssessmentCollection.class, ENTAssessment.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return entAssessments;
	}

	@Override
	@Transactional
	public Integer getENTAssessmentListCount(String academicProfileId, String schoolId, String branchId,
			String doctorId, String updatedTime, int page, int size, Boolean isDiscarded) {
		Integer count = 0;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));

			if (academicProfileId != null) {
				criteria.and("academicProfileId").is(new ObjectId(academicProfileId));
			}

			if (schoolId != null) {
				criteria.and("schoolId").is(new ObjectId(schoolId));
			}

			if (branchId != null) {
				criteria.and("branchId").is(new ObjectId(branchId));
			}

			if (doctorId != null) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (isDiscarded != null) {
				criteria.and("discarded").is(isDiscarded);
			}

			count = (int) mongoTemplate.count(new Query(criteria), ENTAssessmentCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return count;
	}

	@Override
	@Transactional
	public DentalAssessment addEditDentalAssessment(DentalAssessment request) {
		DentalAssessment response = null;
		DentalAssessmentCollection dentalAssessmentCollection = null;

		try {
			if (request.getId() == null) {
				dentalAssessmentCollection = new DentalAssessmentCollection();
				request.setCreatedTime(new Date());

			} else {
				dentalAssessmentCollection = dentalAssessmentRepository.findById(new ObjectId(request.getId())).orElse(null);
			}
			BeanUtil.map(request, dentalAssessmentCollection);
			dentalAssessmentCollection.setChiefComplaints(request.getChiefComplaints());
			dentalAssessmentCollection.setHabits(request.getHabits());
			dentalAssessmentCollection.setSuggestedInvestigation(request.getSuggestedInvestigation());
			dentalAssessmentCollection.setImages(request.getImages());
			dentalAssessmentCollection.setTeethExamination(request.getTeethExamination());
			dentalAssessmentCollection = dentalAssessmentRepository.save(dentalAssessmentCollection);
			response = new DentalAssessment();
			BeanUtil.map(dentalAssessmentCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			// logger.error(e + " Something went wrong");
			throw new BusinessException(ServiceError.Unknown, "Something went wrong");
		}
		return response;
	}

	@Override
	@Transactional
	public DentalAssessment getDentalAssessmentById(String id) {
		DentalAssessment response = null;
		DentalAssessmentCollection dentalAssessmentCollection = null;

		try {

			dentalAssessmentCollection = dentalAssessmentRepository.findById(new ObjectId(id)).orElse(null);

			if (dentalAssessmentCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}

			dentalAssessmentCollection = dentalAssessmentRepository.save(dentalAssessmentCollection);
			if (dentalAssessmentCollection != null) {
				response = new DentalAssessment();
				BeanUtil.map(dentalAssessmentCollection, response);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			// logger.error(e);
		}
		return response;
	}

	@Override
	@Transactional
	public DentalAssessment discardDentalAssessmentById(String id, Boolean discarded) {
		DentalAssessment response = null;
		DentalAssessmentCollection dentalAssessmentCollection = null;

		try {

			dentalAssessmentCollection = dentalAssessmentRepository.findById(new ObjectId(id)).orElse(null);

			if (dentalAssessmentCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			dentalAssessmentCollection.setDiscarded(discarded);
			dentalAssessmentCollection = dentalAssessmentRepository.save(dentalAssessmentCollection);
			if (dentalAssessmentCollection != null) {
				response = new DentalAssessment();
				BeanUtil.map(dentalAssessmentCollection, response);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			// logger.error(e);
		}
		return response;
	}

	@Override
	@Transactional
	public List<DentalAssessment> getDentalAssessmentList(String academicProfileId, String schoolId, String branchId,
			String doctorId, String updatedTime, int page, int size, Boolean isDiscarded) {
		List<DentalAssessment> dentalAssessments = null;
		Aggregation aggregation = null;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));

			if (academicProfileId != null) {
				criteria.and("academicProfileId").is(new ObjectId(academicProfileId));
			}

			if (schoolId != null) {
				criteria.and("schoolId").is(new ObjectId(schoolId));
			}

			if (branchId != null) {
				criteria.and("branchId").is(new ObjectId(branchId));
			}

			if (doctorId != null) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (isDiscarded != null) {
				criteria.and("discarded").is(isDiscarded);
			}

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}

			dentalAssessments = mongoTemplate
					.aggregate(aggregation, DentalAssessmentCollection.class, DentalAssessment.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return dentalAssessments;
	}

	@Override
	@Transactional
	public Integer getDentalAssessmentListCount(String academicProfileId, String schoolId, String branchId,
			String doctorId, String updatedTime, int page, int size, Boolean isDiscarded) {
		Integer count = 0;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));

			if (academicProfileId != null) {
				criteria.and("academicProfileId").is(new ObjectId(academicProfileId));
			}

			if (schoolId != null) {
				criteria.and("schoolId").is(new ObjectId(schoolId));
			}

			if (branchId != null) {
				criteria.and("branchId").is(new ObjectId(branchId));
			}

			if (doctorId != null) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (isDiscarded != null) {
				criteria.and("discarded").is(isDiscarded);
			}

			count = (int) mongoTemplate.count(new Query(criteria), DentalAssessmentCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return count;
	}

	@Override
	@Transactional
	public EyeAssessment addEditEyeAssessment(EyeAssessment request) {
		EyeAssessment response = null;
		EyeAssessmentCollection eyeAssessmentCollection = null;

		try {
			if (request.getId() == null) {
				eyeAssessmentCollection = new EyeAssessmentCollection();
				request.setCreatedTime(new Date());

			} else {
				eyeAssessmentCollection = eyeAssessmentRepository.findById(new ObjectId(request.getId())).orElse(null);
			}
			BeanUtil.map(request, eyeAssessmentCollection);
			eyeAssessmentCollection.setClinicalLeftEye(request.getClinicalLeftEye());
			eyeAssessmentCollection.setClinicalRightEye(request.getClinicalRightEye());
			eyeAssessmentCollection.setSuggesstedInvestigation(request.getSuggesstedInvestigation());
			eyeAssessmentCollection.setImages(request.getImages());
			eyeAssessmentCollection = eyeAssessmentRepository.save(eyeAssessmentCollection);
			response = new EyeAssessment();
			BeanUtil.map(eyeAssessmentCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			// logger.error(e + " Something went wrong");
			throw new BusinessException(ServiceError.Unknown, "Something went wrong");
		}
		return response;
	}

	@Override
	@Transactional
	public EyeAssessment getEyeAssessmentById(String id) {
		EyeAssessment response = null;
		EyeAssessmentCollection eyeAssessmentCollection = null;

		try {

			eyeAssessmentCollection = eyeAssessmentRepository.findById(new ObjectId(id)).orElse(null);

			if (eyeAssessmentCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}

			eyeAssessmentCollection = eyeAssessmentRepository.save(eyeAssessmentCollection);
			if (eyeAssessmentCollection != null) {
				response = new EyeAssessment();
				BeanUtil.map(eyeAssessmentCollection, response);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			// logger.error(e);
		}
		return response;
	}

	@Override
	@Transactional
	public EyeAssessment discardEyeAssessmentById(String id, Boolean discarded) {
		EyeAssessment response = null;
		EyeAssessmentCollection eyeAssessmentCollection = null;

		try {

			eyeAssessmentCollection = eyeAssessmentRepository.findById(new ObjectId(id)).orElse(null);

			if (eyeAssessmentCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			eyeAssessmentCollection.setDiscarded(discarded);
			eyeAssessmentCollection = eyeAssessmentRepository.save(eyeAssessmentCollection);
			if (eyeAssessmentCollection != null) {
				response = new EyeAssessment();
				BeanUtil.map(eyeAssessmentCollection, response);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			// logger.error(e);
		}
		return response;
	}

	@Override
	@Transactional
	public List<EyeAssessment> getEyeAssessmentList(String academicProfileId, String schoolId, String branchId,
			String doctorId, String updatedTime, int page, int size, Boolean isDiscarded) {
		List<EyeAssessment> eyeAssessments = null;
		Aggregation aggregation = null;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));

			if (academicProfileId != null) {
				criteria.and("academicProfileId").is(new ObjectId(academicProfileId));
			}

			if (schoolId != null) {
				criteria.and("schoolId").is(new ObjectId(schoolId));
			}

			if (branchId != null) {
				criteria.and("branchId").is(new ObjectId(branchId));
			}

			if (doctorId != null) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (isDiscarded != null) {
				criteria.and("discarded").is(isDiscarded);
			}

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}

			eyeAssessments = mongoTemplate.aggregate(aggregation, EyeAssessmentCollection.class, EyeAssessment.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return eyeAssessments;
	}

	@Override
	@Transactional
	public Integer getEyeAssessmentListCount(String academicProfileId, String schoolId, String branchId,
			String doctorId, String updatedTime, int page, int size, Boolean isDiscarded) {
		Integer count = 0;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));

			if (academicProfileId != null) {
				criteria.and("academicProfileId").is(new ObjectId(academicProfileId));
			}

			if (schoolId != null) {
				criteria.and("schoolId").is(new ObjectId(schoolId));
			}

			if (branchId != null) {
				criteria.and("branchId").is(new ObjectId(branchId));
			}

			if (doctorId != null) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (isDiscarded != null) {
				criteria.and("discarded").is(isDiscarded);
			}

			count = (int) mongoTemplate.count(new Query(criteria), EyeAssessmentCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return count;
	}

	@Override
	@Transactional
	public NutritionAssessment addEditNutritionAssessment(NutritionAssessment request) {

		NutritionAssessment response = null;
		NutritionAssessmentCollection nutritionAssessmentCollection = null;
		try {
			if (request.getId() == null) {
				nutritionAssessmentCollection = new NutritionAssessmentCollection();
				request.setCreatedTime(new Date());

			} else {
				nutritionAssessmentCollection = nutritionAssessmentRepository.findById(new ObjectId(request.getId())).orElse(null);
			}
			BeanUtil.map(request, nutritionAssessmentCollection);
			nutritionAssessmentCollection.setMealTimings(request.getMealTimings());
			nutritionAssessmentCollection.setFoodPatterns(request.getFoodPatterns());
			nutritionAssessmentCollection.setAddictionOfParents(request.getAddictionOfParents());
			nutritionAssessmentCollection.setImages(request.getImages());
			nutritionAssessmentCollection.setDrugs(request.getDrugs());
			nutritionAssessmentCollection.setClinicalManifestation(request.getClinicalManifestation());
			nutritionAssessmentCollection.setDrinkingWaterType(request.getDrinkingWaterType());
			nutritionAssessmentCollection.setExerciseType(request.getExerciseType());
			nutritionAssessmentCollection = nutritionAssessmentRepository.save(nutritionAssessmentCollection);
			response = new NutritionAssessment();
			BeanUtil.map(nutritionAssessmentCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			// logger.error(e + " Something went wrong");
			throw new BusinessException(ServiceError.Unknown, "Something went wrong");
		}
		return response;
	}

	@Override
	@Transactional
	public NutritionAssessment getNutritionAssessmentById(String id) {
		NutritionAssessment response = null;
		NutritionAssessmentCollection nutritionAssessmentCollection = null;

		try {

			nutritionAssessmentCollection = nutritionAssessmentRepository.findById(new ObjectId(id)).orElse(null);

			if (nutritionAssessmentCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}

			nutritionAssessmentCollection = nutritionAssessmentRepository.save(nutritionAssessmentCollection);
			if (nutritionAssessmentCollection != null) {
				response = new NutritionAssessment();
				BeanUtil.map(nutritionAssessmentCollection, response);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			// logger.error(e);
		}
		return response;
	}

	@Override
	@Transactional
	public NutritionAssessment discardNutritionAssessmentById(String id, Boolean discarded) {
		NutritionAssessment response = null;
		NutritionAssessmentCollection nutritionAssessmentCollection = null;

		try {

			nutritionAssessmentCollection = nutritionAssessmentRepository.findById(new ObjectId(id)).orElse(null);

			if (nutritionAssessmentCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}
			nutritionAssessmentCollection.setDiscarded(discarded);
			nutritionAssessmentCollection = nutritionAssessmentRepository.save(nutritionAssessmentCollection);
			if (nutritionAssessmentCollection != null) {
				response = new NutritionAssessment();
				BeanUtil.map(nutritionAssessmentCollection, response);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			// logger.error(e);
		}
		return response;
	}

	@Override
	@Transactional
	public List<NutritionAssessment> getNutritionAssessmentList(String academicProfileId, String schoolId,
			String branchId, String doctorId, String updatedTime, int page, int size, Boolean isDiscarded, String recipe) {
		List<NutritionAssessment> nutritionAssessments = null;
		Aggregation aggregation = null;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));

			if (academicProfileId != null) {
				criteria.and("academicProfileId").is(new ObjectId(academicProfileId));
			}

			if (schoolId != null) {
				criteria.and("schoolId").is(new ObjectId(schoolId));
			}

			if (branchId != null) {
				criteria.and("branchId").is(new ObjectId(branchId));
			}

			if (doctorId != null) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (isDiscarded != null) {
				criteria.and("discarded").is(isDiscarded);
			}
			if(!DPDoctorUtils.anyStringEmpty(recipe)) {
				criteria = criteria.orOperator(new Criteria("foodPatterns.recipeName").regex("^" + recipe, "i"),
							new Criteria("foodPatterns.recipeName").regex(recipe));
			}
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}

			nutritionAssessments = mongoTemplate
					.aggregate(aggregation, NutritionAssessmentCollection.class, NutritionAssessment.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return nutritionAssessments;
	}

	@Override
	@Transactional
	public Integer getNutritionAssessmentListCount(String academicProfileId, String schoolId, String branchId,
			String doctorId, String updatedTime, int page, int size, Boolean isDiscarded, String recipe) {
		Integer count = 0;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));

			if (academicProfileId != null) {
				criteria.and("academicProfileId").is(new ObjectId(academicProfileId));
			}

			if (schoolId != null) {
				criteria.and("schoolId").is(new ObjectId(schoolId));
			}

			if (branchId != null) {
				criteria.and("branchId").is(new ObjectId(branchId));
			}

			if (doctorId != null) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}

			if (isDiscarded != null) {
				criteria.and("discarded").is(isDiscarded);
			}
			if(!DPDoctorUtils.anyStringEmpty(recipe)) {
				criteria = criteria.orOperator(new Criteria("foodPatterns.recipeName").regex("^" + recipe, "i"),
							new Criteria("foodPatterns.recipeName").regex(recipe));
			}
			count = (int) mongoTemplate.count(new Query(criteria), NutritionAssessmentCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return count;
	}

	@Override
	@Transactional
	public ImageURLResponse addCampVisitImage(MultipartFile file) {
		ImageURLResponse imageURLResponse = null;
		/*
		 * try { if (file != null) { String path = "camp-visit"; String fileExtension =
		 * FilenameUtils.getExtension(file.getOriginalFilename()); String fileName =
		 * file.getOriginalFilename().replaceFirst("." + fileExtension, ""); String
		 * recordPath = path + File.separator + fileName + System.currentTimeMillis() +
		 * "." + fileExtension; imageURLResponse = fileManager.saveImage(file,
		 * recordPath, true);
		 * 
		 * } } catch (Exception e) { e.printStackTrace(); }
		 */
		return imageURLResponse;
	}

	@Override
	@Transactional
	public List<DrugInfo> getDrugInfo(int page, int size, String updatedTime, String searchTerm, Boolean discarded) {
		List<DrugInfo> response = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("brandName").regex("^" + searchTerm, "i"));
			}

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria));
			}

			response = mongoTemplate.aggregate(aggregation, DrugInfoCollection.class, DrugInfo.class)
					.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drugs");
		}
		return response;
	}

	@Override
	@Transactional
	public Integer getDrugInfoCount(String updatedTime, String searchTerm) {
		Integer count = 0;
		try {
			// Criteria criteria = new Criteria();

			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("brandName").regex("^" + searchTerm, "i"));
			}

			count = (int) mongoTemplate.count(new Query(criteria), DrugInfoCollection.class);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return count;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AcademicProfile> getStudentProfile(int page, int size, String branchId, String schoolId, String classId,
			String sectionId, String searchTerm, Boolean discarded, String profileType, String userId,
			String updatedTime, String assesmentType, String department, String departmentValue) {
		List<AcademicProfile> response = null;
		try {
			Criteria criteria = new Criteria("branchId").is(new ObjectId(branchId)).and("schoolId")
					.is(new ObjectId(schoolId));
			
			List<ObjectId> ids = null;
			if(!DPDoctorUtils.anyStringEmpty(assesmentType, department)) {
				Criteria criteriaForAssement = new Criteria("branchId").is(new ObjectId(branchId)).and("schoolId").is(new ObjectId(schoolId));
				switch(assesmentType.toUpperCase()) {
					case "GROWTH": 
						if(department.equalsIgnoreCase("underweight")) criteriaForAssement.and("bmi").gt(0).lte(18.4);
						else if(department.equalsIgnoreCase("healthy")) criteriaForAssement.and("bmi").gte(18.5).lte(24.9);
						else if(department.equalsIgnoreCase("overweight"))criteriaForAssement.and("bmi").gte(25.0).lte(29.9);
						else if(department.equalsIgnoreCase("obese"))criteriaForAssement.and("bmi").gte(30.0);
						
						List<GrowthAssessmentAndGeneralBioMetricsCollection> growthAssesment = mongoTemplate.aggregate(
								Aggregation.newAggregation(Aggregation.match(criteriaForAssement), 
										new CustomAggregationOperation(new Document("_id", "academicProfileId"))), GrowthAssessmentAndGeneralBioMetricsCollection.class, GrowthAssessmentAndGeneralBioMetricsCollection.class).getMappedResults();
						if(growthAssesment != null) {
							ids = (List<ObjectId>) CollectionUtils.collect(growthAssesment, new BeanToPropertyValueTransformer("id")) ;
							if(ids == null || ids.isEmpty())return null;
						}else return null;
						
						break;
					case "PHYSICAL":
						if(department.equalsIgnoreCase("handAndNailHygiene")) criteriaForAssement.and("handAndNailHygiene").is(true);
						else if(department.equalsIgnoreCase("hairHygiene")) criteriaForAssement.and("hairHygiene").is(true);
						
						else if(department.equalsIgnoreCase("Pallor")) criteriaForAssement.and("generalSigns").is("Pallor");
						else if(department.equalsIgnoreCase("Cyanosis")) criteriaForAssement.and("generalSigns").is("Cyanosis");
						else if(department.equalsIgnoreCase("Anaemia")) criteriaForAssement.and("generalSigns").is("Anaemia");
						else if(department.equalsIgnoreCase("Clubbing")) criteriaForAssement.and("generalSigns").is("Clubbing");
						else if(department.equalsIgnoreCase("Edema")) criteriaForAssement.and("generalSigns").is("Edema");
						
						else if(department.equalsIgnoreCase("Abnormal Shape")) criteriaForAssement.and("head").is("Abnormal Shape");
						else if(department.equalsIgnoreCase("Abnormal Hair Pigmentation")) criteriaForAssement.and("head").is("Abnormal Hair Pigmentation");
						else if(department.equalsIgnoreCase("Scalp Swelling")) criteriaForAssement.and("head").is("Scalp Swelling");
						else if(department.equalsIgnoreCase("Scalp Bruising")) criteriaForAssement.and("head").is("Scalp Bruising");
						else if(department.equalsIgnoreCase("Bony Swelling")) criteriaForAssement.and("head").is("Bony Swelling");
						
						else if(department.equalsIgnoreCase("Itching")) criteriaForAssement.and("skin").is("Itching");
						else if(department.equalsIgnoreCase("Scabies")) criteriaForAssement.and("skin").is("Scabies");
						else if(department.equalsIgnoreCase("Tinea")) criteriaForAssement.and("skin").is("Tinea");
						else if(department.equalsIgnoreCase("Herpes")) criteriaForAssement.and("skin").is("Herpes");
						else if(department.equalsIgnoreCase("Acne")) criteriaForAssement.and("skin").is("Acne");
						else if(department.equalsIgnoreCase("Contact Dermatitis")) criteriaForAssement.and("skin").is("Contact Dermatitis");
						else if(department.equalsIgnoreCase("Petechiae")) criteriaForAssement.and("skin").is("Petechiae");
						else if(department.equalsIgnoreCase("Purpura")) criteriaForAssement.and("skin").is("Purpura");
						else if(department.equalsIgnoreCase("Icterus")) criteriaForAssement.and("skin").is("Icterus");
						else if(department.equalsIgnoreCase("Impetigo")) criteriaForAssement.and("skin").is("Impetigo");
						else if(department.equalsIgnoreCase("Pigmentation")) criteriaForAssement.and("skin").is("Pigmentation");
						
						else if(department.equalsIgnoreCase("Abnormal Heart Sounds")) criteriaForAssement.and("cardiovascular").is("Abnormal Heart Sounds");
						else if(department.equalsIgnoreCase("Murmur")) criteriaForAssement.and("cardiovascular").is("Murmur");
						
						else if(department.equalsIgnoreCase("Barky Cough")) criteriaForAssement.and("respiratory").is("Barky Cough");
						else if(department.equalsIgnoreCase("Hoarse Voice")) criteriaForAssement.and("respiratory").is("Hoarse Voice");
						else if(department.equalsIgnoreCase("Auscultation")) criteriaForAssement.and("respiratory").is("Auscultation");
						
						else if(department.equalsIgnoreCase("Cervical")) criteriaForAssement.and("lymphatic").is("Cervical");
						else if(department.equalsIgnoreCase("Submandibular")) criteriaForAssement.and("lymphatic").is("Submandibular");
						
						else if(department.equalsIgnoreCase("Distented")) criteriaForAssement.and("abdomen").is("Distented");
						else if(department.equalsIgnoreCase("Tense")) criteriaForAssement.and("abdomen").is("Tense");
						else if(department.equalsIgnoreCase("Tender")) criteriaForAssement.and("abdomen").is("Tender");
						else if(department.equalsIgnoreCase("Intestinal sound")) criteriaForAssement.and("abdomen").is("Intestinal sound");
						
						else if(department.equalsIgnoreCase("Spinal Abnormalities")) criteriaForAssement.and("orthopedics").is("Spinal Abnormalities");
						
						else if(department.equalsIgnoreCase("Vitamin - C")) criteriaForAssement.and("deficienciesSuspected").is("Vitamin - C");
						else if(department.equalsIgnoreCase("Vitamin - D")) criteriaForAssement.and("deficienciesSuspected").is("Vitamin - D");
						else if(department.equalsIgnoreCase("Vitamin - B12")) criteriaForAssement.and("deficienciesSuspected").is("Vitamin - B12");
						else if(department.equalsIgnoreCase("Vitamin - A")) criteriaForAssement.and("deficienciesSuspected").is("Vitamin - A");
						else if(department.equalsIgnoreCase("Vitamin B Complex")) criteriaForAssement.and("deficienciesSuspected").is("Vitamin B Complex");
						else if(department.equalsIgnoreCase("Iron")) criteriaForAssement.and("deficienciesSuspected").is("Iron");
						else if(department.equalsIgnoreCase("Calcium")) criteriaForAssement.and("deficienciesSuspected").is("Calcium");
						
						else if(department.equalsIgnoreCase("Deep Tendon Reflex")) criteriaForAssement.and("nervousSystem").is("Deep Tendon Reflex");
						else if(department.equalsIgnoreCase("Pupillary Reflex")) criteriaForAssement.and("nervousSystem").is("Pupillary Reflex");
						else if(department.equalsIgnoreCase("Corneal Reflex")) criteriaForAssement.and("nervousSystem").is("Corneal Reflex");
						else if(department.equalsIgnoreCase("Muscular tone")) criteriaForAssement.and("nervousSystem").is("Muscular tone");
						
						else if(department.equalsIgnoreCase("nutritionConsultation")) criteriaForAssement.and("nutritionConsultation").is(true);
						else if(department.equalsIgnoreCase("xRay")) criteriaForAssement.and("xRay").is(true);
						else if(department.equalsIgnoreCase("ctMRIScanRegion")) criteriaForAssement.and("ctMRIScanRegion").exists(true);
						else if(department.equalsIgnoreCase("bloodTest"))criteriaForAssement.and("bloodTest").is(departmentValue);
						else if(department.equalsIgnoreCase("stoolTest"))criteriaForAssement.and("stoolTest").is(departmentValue);
						else if(department.equalsIgnoreCase("urineTest"))criteriaForAssement.and("urineTest").is(departmentValue);
						
						List<PhysicalAssessmentCollection> phyAssesment = mongoTemplate.aggregate(
								Aggregation.newAggregation(Aggregation.match(criteriaForAssement), 
										new CustomAggregationOperation(new Document("_id", "academicProfileId"))), PhysicalAssessmentCollection.class, PhysicalAssessmentCollection.class).getMappedResults();
						if(phyAssesment != null) {
							ids = (List<ObjectId>) CollectionUtils.collect(phyAssesment, new BeanToPropertyValueTransformer("id")) ;
							if(ids == null || ids.isEmpty())return null;
						}else return null;
						
						break;
					case "DENTAL":
						
						if(department.equalsIgnoreCase("habits"))criteriaForAssement.and("habits").is(departmentValue);
						else if(department.equalsIgnoreCase("chiefComplaints"))criteriaForAssement.and("chiefComplaints").is(departmentValue);
						else if(department.equalsIgnoreCase("gingivaStains"))criteriaForAssement.and("gingivaStains").is(departmentValue);
						else if(department.equalsIgnoreCase("gingivaCalculus"))criteriaForAssement.and("gingivaCalculus").is(departmentValue);
						
						else if(department.equalsIgnoreCase("teethExamination")) {
							if (departmentValue.equalsIgnoreCase("Decayed")) {
								criteriaForAssement.and("teethExamination.decayedDMFTIndex").exists(true);
							} else if (departmentValue.equalsIgnoreCase("Missing")) {
								criteriaForAssement.and("teethExamination.missingDMFTIndex").exists(true);
							} else if (departmentValue.equalsIgnoreCase("Filled")) {
								criteriaForAssement.and("teethExamination.filledDMFTIndex").exists(true);
							}else if (departmentValue.equalsIgnoreCase("malocclusion")) {
								criteriaForAssement.and("teethExamination.malocclusion").exists(true);
							}
						}else if(department.equalsIgnoreCase("doctorConsultations")) {
							criteriaForAssement.and("doctorConsultations").is(departmentValue);
						}else if(department.equalsIgnoreCase("oralHygiene"))criteriaForAssement.and("oralHygiene").is(departmentValue);
						
						else if(department.equalsIgnoreCase("suggestedInvestigation")) {
							criteriaForAssement.and("suggestedInvestigation").is(departmentValue);
						}
						
						List<DentalAssessmentCollection> dentalAssesment = mongoTemplate.aggregate(
								Aggregation.newAggregation(Aggregation.match(criteriaForAssement), 
										new CustomAggregationOperation(new Document("_id", "academicProfileId"))), DentalAssessmentCollection.class, DentalAssessmentCollection.class).getMappedResults();
						if(dentalAssesment != null) {
							ids = (List<ObjectId>) CollectionUtils.collect(dentalAssesment, new BeanToPropertyValueTransformer("id")) ;
							if(ids == null || ids.isEmpty())return null;
						}else return null;
						
						break;
					case "ENT":
						
						if(department.equalsIgnoreCase("leftEar")) {
							criteriaForAssement.and("leftEar").is(departmentValue);
						}else if(department.equalsIgnoreCase("nose")) {
							criteriaForAssement.and("nose").is(departmentValue);
						}else if(department.equalsIgnoreCase("rightEar")) {
							criteriaForAssement.and("rightEar").is(departmentValue);
						}else if(department.equalsIgnoreCase("oralCavityAndThroat")) {
							criteriaForAssement.and("oralCavityAndThroat").is(departmentValue);
						}else if(department.equalsIgnoreCase("doctorConsultations")) {
							criteriaForAssement.and("doctorConsultations").is(true);
						}
						
						List<ENTAssessmentCollection> entAssesment = mongoTemplate.aggregate(
								Aggregation.newAggregation(Aggregation.match(criteriaForAssement), 
										new CustomAggregationOperation(new Document("_id", "academicProfileId"))), ENTAssessmentCollection.class, ENTAssessmentCollection.class).getMappedResults();
						if(entAssesment != null) {
							ids = (List<ObjectId>) CollectionUtils.collect(entAssesment, new BeanToPropertyValueTransformer("id")) ;
							if(ids == null || ids.isEmpty())return null;
						}else return null;
						break;
					case "EYE":
						if(department.equalsIgnoreCase("optometryLeftEye")) {
							criteriaForAssement.and("optometryLeftEye").is(departmentValue);
						}else if(department.equalsIgnoreCase("optometryRightEye")) {
							criteriaForAssement.and("optometryRightEye").is(departmentValue);
						}else if(department.equalsIgnoreCase("clinicalRightEye")) {
							criteriaForAssement.and("clinicalRightEye").is(departmentValue);
						}else if(department.equalsIgnoreCase("clinicalLeftEye")) {
							criteriaForAssement.and("clinicalLeftEye").is(departmentValue);
						}else if(department.equalsIgnoreCase("wearGlasses")) {
							criteriaForAssement.and("wearGlasses").is(true);
						}else if(department.equalsIgnoreCase("suggesstedInvestigation")) {
							criteriaForAssement.and("suggesstedInvestigation").is(departmentValue);
						}else if(department.equalsIgnoreCase("doctorConsultation")) {
							criteriaForAssement.and("doctorConsultation").is(true);
						}
						
						List<ENTAssessmentCollection> entAassesment = mongoTemplate.aggregate(
								Aggregation.newAggregation(Aggregation.match(criteriaForAssement), 
										new CustomAggregationOperation(new Document("_id", "academicProfileId"))), ENTAssessmentCollection.class, ENTAssessmentCollection.class).getMappedResults();
						if(entAassesment != null) {
							ids = (List<ObjectId>) CollectionUtils.collect(entAassesment, new BeanToPropertyValueTransformer("id")) ;
							if(ids == null || ids.isEmpty())return null;
						}else return null;
						break;
					case "NUTRITION":
						if(department.equalsIgnoreCase("foodPreference")) {
							criteriaForAssement.and("foodPreference").is(departmentValue);
						}else if(department.equalsIgnoreCase("waterIntakePerDay")) {
							criteriaForAssement.and("waterIntakePerDay").gte(0);
						}else if(department.equalsIgnoreCase("sleepingHours")) {
							criteriaForAssement.and("sleepingHours").exists(true);
						}else if(department.equalsIgnoreCase("drinkingWaterType")) {
							criteriaForAssement.and("drinkingWaterType").is(departmentValue);
						}else if(department.equalsIgnoreCase("exerciseType")) {
							criteriaForAssement.and("exerciseType").is(departmentValue);
						}
						
						List<ENTAssessmentCollection> nutritionAssesment = mongoTemplate.aggregate(
								Aggregation.newAggregation(Aggregation.match(criteriaForAssement), 
										new CustomAggregationOperation(new Document("_id", "academicProfileId"))), ENTAssessmentCollection.class, ENTAssessmentCollection.class).getMappedResults();
						if(nutritionAssesment != null) {
							ids = (List<ObjectId>) CollectionUtils.collect(nutritionAssesment, new BeanToPropertyValueTransformer("id")) ;
							if(ids == null || ids.isEmpty())return null;
						}else return null;
						break;
				}
				criteria.and("id").in(ids);
			}
			
			if (!DPDoctorUtils.anyStringEmpty(classId)) {
				criteria.and("classId").is(new ObjectId(classId));
			}
			if (!DPDoctorUtils.anyStringEmpty(sectionId)) {
				criteria.and("sectionId").is(new ObjectId(sectionId));
			}
			if (!DPDoctorUtils.anyStringEmpty(userId)) {
				criteria.and("userId").is(new ObjectId(userId));
			}
			if (!DPDoctorUtils.anyStringEmpty(profileType)) {
				criteria.and("type").is(profileType);
			}
			if (discarded != null) {
				criteria.and("discarded").is(discarded);
			}
			if (!DPDoctorUtils.anyStringEmpty(updatedTime)) {
				criteria.and("createdTime").gte(new Date(Long.parseLong(updatedTime)));
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("firstName").regex("^" + searchTerm, "i"),
						new Criteria("firstName").regex("^" + searchTerm),
						new Criteria("rollNo").regex("^" + searchTerm),
						new Criteria("uniqueId").regex("^" + searchTerm),
						new Criteria("emailAddress").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"));
			}
			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$id"),
					Fields.field("userId", "$userId"), Fields.field("firstName", "$firstName"),
					Fields.field("localPatientName", "$localPatientName"),
					Fields.field("mobileNumber", "$mobileNumber"), Fields.field("uniqueId", "$uniqueId"),
					Fields.field("rollNo", "$rollNo"), Fields.field("acadamicClass", "$acadamicClass"),
					Fields.field("emailAddress", "$emailAddress"),
					Fields.field("acadamicSection", "$acadamicSection.section"),
					Fields.field("admissionDate", "$admissionDate"), Fields.field("type", "$type"),
					Fields.field("imageUrl", "$imageUrl"), Fields.field("thumbnailUrl", "$thumbnailUrl"),
					Fields.field("isSuperStar", "$isSuperStar"), Fields.field("createdTime", "$createdTime")));

			Aggregation aggregation = null;

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("acadamic_class_cl", "classId", "_id", "acadamicClass"),
						Aggregation.unwind("acadamicClass"),
						Aggregation.lookup("acadamic_section_cl", "sectionId", "_id", "acadamicSection"),
						Aggregation.unwind("acadamicSection", true), projectList,
						Aggregation.sort(new Sort(Sort.Direction.ASC, "firstName")),

						Aggregation.skip((long) (page) * size), Aggregation.limit(size));
			} else {

				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("acadamic_class_cl", "classId", "_id", "acadamicClass"),
						Aggregation.unwind("acadamicClass"),
						Aggregation.lookup("acadamic_section_cl", "sectionId", "_id", "acadamicSection"),
						Aggregation.unwind("acadamicSection", true), projectList,
						Aggregation.sort(new Sort(Sort.Direction.ASC, "firstName")));
			}
			response = mongoTemplate.aggregate(aggregation, AcademicProfileCollection.class, AcademicProfile.class)
					.getMappedResults();
			if(response != null) {
				for(AcademicProfile acadamicProfile : response) {
					
					Criteria assessmentCriteria = new Criteria("schoolId").is(new ObjectId(schoolId));
					
					if (!DPDoctorUtils.anyStringEmpty(branchId)) {
						assessmentCriteria.and("branchId").is(new ObjectId(branchId));
					}

//					if (!DPDoctorUtils.anyStringEmpty(campId)) {
//						assessmentCriteria.and("campId").is(new ObjectId(campId));
//					}
//				
//					if(fromDateTime != null && toDateTime != null) {
//						assessmentCriteria.and("createdTime").gte(fromDateTime).lte(toDateTime);
//					}else if(fromDateTime != null) {
//						assessmentCriteria.and("createdTime").gte(fromDateTime);
//					}else if(toDateTime != null) {
//						assessmentCriteria.and("createdTime").lte(toDateTime);
//					}
					
					assessmentCriteria.and("academicProfileId").is(new ObjectId(acadamicProfile.getId()));
					Integer growthAssessmentCount = (int) mongoTemplate.count(new Query(assessmentCriteria), GrowthAssessmentAndGeneralBioMetricsCollection.class);
					if(growthAssessmentCount > 0)acadamicProfile.setIsGrowthAssessmentPresent(true);
					
					Integer physicalAssessmentCount = (int) mongoTemplate.count(new Query(assessmentCriteria), PhysicalAssessmentCollection.class);
					if(physicalAssessmentCount > 0)acadamicProfile.setIsPhysicalAssessmentPresent(true);
					
					Integer eyeAssessmentCount = (int) mongoTemplate.count(new Query(assessmentCriteria), EyeAssessmentCollection.class);
					if(eyeAssessmentCount > 0)acadamicProfile.setIsEyeAssessmentPresent(true);
					
					Integer entAssessmentCount = (int) mongoTemplate.count(new Query(assessmentCriteria), ENTAssessmentCollection.class);
					if(entAssessmentCount > 0)acadamicProfile.setIsENTAssessmentPresent(true);
					
					Integer dentalAssessmentCount = (int) mongoTemplate.count(new Query(assessmentCriteria), DentalAssessmentCollection.class);
					if(dentalAssessmentCount > 0)acadamicProfile.setIsDentalAssessmentPresent(true);
					
					Integer nutritionAssessmentCount = (int) mongoTemplate.count(new Query(assessmentCriteria), NutritionAssessmentCollection.class);
					if(nutritionAssessmentCount > 0)acadamicProfile.setIsNutritionalAssessmentPresent(true);
					
					Integer dietPlanCount = (int) mongoTemplate.count(new Query(new Criteria("patientId").is(new ObjectId(acadamicProfile.getId()))), DietPlanCollection.class);
					if(dietPlanCount > 0)acadamicProfile.setIsDietPlanPresent(true);
				}
			}
			/*
			 * for (AcadamicProfile acadamicProfile : response) { if
			 * (!DPDoctorUtils.anyStringEmpty(acadamicProfile.getImageUrl())) {
			 * acadamicProfile.setImageUrl(imagePath + acadamicProfile.getImageUrl()); } if
			 * (!DPDoctorUtils.anyStringEmpty(acadamicProfile.getThumbnailUrl())) {
			 * acadamicProfile.setThumbnailUrl(imagePath +
			 * acadamicProfile.getThumbnailUrl()); } }
			 */

		} catch (BusinessException be) {
			logger.warn(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while getting Student Profile List");
			throw new BusinessException(ServiceError.Unknown, "Error occured while getting Student Profile list");
		}
		return response;
	}
	
	@Override
	public List<AcademicProfile> getTeacherProfile(int page, int size, String branchId, String schoolId,
			String searchTerm, Boolean discarded, String profileType, String userId, String updatedTime) {
		List<AcademicProfile> response = null;
		try {
			Criteria criteria = new Criteria("branchId").is(new ObjectId(branchId)).and("schoolId")
					.is(new ObjectId(schoolId));
			if (!DPDoctorUtils.anyStringEmpty(userId)) {
				criteria.and("userId").is(new ObjectId(userId));
			}
			if (!DPDoctorUtils.anyStringEmpty(profileType)) {
				criteria.and("type").is(profileType);
			}
			if (discarded != null) {
				criteria.and("discarded").is(discarded);
			}
			if (!DPDoctorUtils.anyStringEmpty(updatedTime)) {
				criteria.and("createdTime").gte(new Date(Long.parseLong(updatedTime)));
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("firstName").regex("^" + searchTerm, "i"),
						new Criteria("firstName").regex("^" + searchTerm),
						new Criteria("rollNo").regex("^" + searchTerm),
						new Criteria("uniqueId").regex("^" + searchTerm),
						new Criteria("emailAddress").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"));
			}
			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$id"),
					Fields.field("userId", "$userId"), Fields.field("firstName", "$firstName"),
					Fields.field("localPatientName", "$localPatientName"),
					Fields.field("mobileNumber", "$mobileNumber"), Fields.field("uniqueId", "$uniqueId"),
					Fields.field("rollNo", "$rollNo"), Fields.field("emailAddress", "$emailAddress"),
					Fields.field("admissionDate", "$admissionDate"), Fields.field("type", "$type"),
					Fields.field("imageUrl", "$imageUrl"), Fields.field("thumbnailUrl", "$thumbnailUrl"),
					Fields.field("isSuperStar", "$isSuperStar"), Fields.field("createdTime", "$createdTime")));

			Aggregation aggregation = null;

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), projectList,
						Aggregation.sort(new Sort(Sort.Direction.ASC, "firstName")),

						Aggregation.skip((long) (page) * size), Aggregation.limit(size));
			} else {

				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), projectList,
						Aggregation.sort(new Sort(Sort.Direction.ASC, "firstName")));
			}
			response = mongoTemplate.aggregate(aggregation, AcademicProfileCollection.class, AcademicProfile.class)
					.getMappedResults();
			/*
			 * for (AcadamicProfile acadamicProfile : response) { if
			 * (!DPDoctorUtils.anyStringEmpty(acadamicProfile.getImageUrl())) {
			 * acadamicProfile.setImageUrl(imagePath + acadamicProfile.getImageUrl()); } if
			 * (!DPDoctorUtils.anyStringEmpty(acadamicProfile.getThumbnailUrl())) {
			 * acadamicProfile.setThumbnailUrl(imagePath +
			 * acadamicProfile.getThumbnailUrl()); } }
			 */

		} catch (BusinessException be) {
			logger.warn(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while getting Teacher Profile List");
			throw new BusinessException(ServiceError.Unknown, "Error occured while getting Teacher Profile list");
		}
		return response;
	}

	
	@Override
	public Integer countStudentProfile(String branchId, String schoolId, String classId, String sectionId,
			String searchTerm, Boolean discarded, String profileType, String userId, String updatedTime) {
		Integer response = 0;
		try {
			Criteria criteria = new Criteria("branchId").is(new ObjectId(branchId)).and("schoolId")
					.is(new ObjectId(schoolId));
			if (!DPDoctorUtils.anyStringEmpty(userId)) {
				criteria.and("userId").is(new ObjectId(userId));
			}
			if (!DPDoctorUtils.anyStringEmpty(profileType)) {
				criteria.and("type").is(profileType);
			}
			if (!DPDoctorUtils.anyStringEmpty(classId)) {
				criteria.and("classId").is(new ObjectId(classId));
			}
			if (!DPDoctorUtils.anyStringEmpty(sectionId)) {
				criteria.and("sectionId").is(new ObjectId(sectionId));
			}
			if (discarded != null) {
				criteria.and("discarded").is(discarded);
			}
			if (!DPDoctorUtils.anyStringEmpty(updatedTime)) {
				criteria.and("createdTime").gte(new Date(Long.parseLong(updatedTime)));
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("firstName").regex("^" + searchTerm, "i"),
						new Criteria("firstName").regex("^" + searchTerm),
						new Criteria("rollNo").regex("^" + searchTerm),
						new Criteria("uniqueId").regex("^" + searchTerm),
						new Criteria("emailAddress").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"));
			}

			Aggregation aggregation = null;

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("acadamic_class_cl", "classId", "_id", "acadamicClass"),
					Aggregation.unwind("acadamicClass"),
					Aggregation.lookup("acadamic_section_cl", "sectionId", "_id", "acadamicSection"),
					Aggregation.unwind("acadamicSection", true),

					new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id"))));

			response = mongoTemplate.aggregate(aggregation, AcademicProfileCollection.class, AcademicProfile.class)
					.getMappedResults().size();

		} catch (BusinessException be) {
			logger.warn(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while Counting Student Profile List");
			throw new BusinessException(ServiceError.Unknown, "Error occured while Counting Student Profile list");
		}
		return response;
	}

	@Override
	public Integer countTeacherProfile(String branchId, String schoolId, String searchTerm, Boolean discarded,
			String profileType, String userId, String updatedTime) {
		Integer response = 0;
		try {
			Criteria criteria = new Criteria("branchId").is(new ObjectId(branchId)).and("schoolId")
					.is(new ObjectId(schoolId));
			if (!DPDoctorUtils.anyStringEmpty(userId)) {
				criteria.and("userId").is(new ObjectId(userId));
			}
			if (!DPDoctorUtils.anyStringEmpty(profileType)) {
				criteria.and("type").is(profileType);
			}

			if (discarded != null) {
				criteria.and("discarded").is(discarded);
			}
			if (!DPDoctorUtils.anyStringEmpty(updatedTime)) {
				criteria.and("createdTime").gte(new Date(Long.parseLong(updatedTime)));
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("firstName").regex("^" + searchTerm, "i"),
						new Criteria("firstName").regex("^" + searchTerm),
						new Criteria("rollNo").regex("^" + searchTerm),
						new Criteria("uniqueId").regex("^" + searchTerm),
						new Criteria("emailAddress").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"));
			}

			Aggregation aggregation = null;

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id"))));

			response = mongoTemplate.aggregate(aggregation, AcademicProfileCollection.class, AcademicProfile.class)
					.getMappedResults().size();

		} catch (BusinessException be) {
			logger.warn(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while Counting Teacher Profile List");
			throw new BusinessException(ServiceError.Unknown, "Error occured while Counting Teacher Profile list");
		}
		return response;
	}

	@Override
	public RegistrationDetails getAcadamicProfile(String profileId) {
		RegistrationDetails response = null;
		try {
			AcademicProfileCollection acadamicProfileCollection = acadamicProfileRespository
					.findById(new ObjectId(profileId)).orElse(null);
			if (acadamicProfileCollection != null) {
				response = new RegistrationDetails();
				BeanUtil.map(acadamicProfileCollection, response);

				if (!DPDoctorUtils.allStringsEmpty(acadamicProfileCollection.getClassId())) {
					AcadamicClassCollection acadamicClassCollection = acadamicClassRepository

							.findById(acadamicProfileCollection.getClassId()).orElse(null);
					if (acadamicClassCollection != null) {
						response.setAcadamicClass(acadamicClassCollection.getName());
					}
				}
				if (!DPDoctorUtils.allStringsEmpty(acadamicProfileCollection.getSectionId())) {
					AcadamicClassSectionCollection acadamicClassSeection = acadamicClassSectionRepository
							.findById(acadamicProfileCollection.getClassId()).orElse(null);
					if (acadamicClassSeection != null) {
						response.setAcadamicSection(acadamicClassSeection.getSection());
					}
				}
				if(response != null) {
						
						Criteria assessmentCriteria = new Criteria("schoolId").is(new ObjectId(response.getSchoolId()));
						
						if (!DPDoctorUtils.anyStringEmpty(response.getBranchId())) {
							assessmentCriteria.and("branchId").is(new ObjectId(response.getBranchId()));
						}

//						if (!DPDoctorUtils.anyStringEmpty(campId)) {
//							assessmentCriteria.and("campId").is(new ObjectId(campId));
//						}
//					
//						if(fromDateTime != null && toDateTime != null) {
//							assessmentCriteria.and("createdTime").gte(fromDateTime).lte(toDateTime);
//						}else if(fromDateTime != null) {
//							assessmentCriteria.and("createdTime").gte(fromDateTime);
//						}else if(toDateTime != null) {
//							assessmentCriteria.and("createdTime").lte(toDateTime);
//						}
						
						assessmentCriteria.and("academicProfileId").is(new ObjectId(profileId));
						Integer growthAssessmentCount = (int) mongoTemplate.count(new Query(assessmentCriteria), GrowthAssessmentAndGeneralBioMetricsCollection.class);
						if(growthAssessmentCount > 0)response.setIsGrowthAssessmentPresent(true);
						
						Integer physicalAssessmentCount = (int) mongoTemplate.count(new Query(assessmentCriteria), PhysicalAssessmentCollection.class);
						if(physicalAssessmentCount > 0)response.setIsPhysicalAssessmentPresent(true);
						
						Integer eyeAssessmentCount = (int) mongoTemplate.count(new Query(assessmentCriteria), EyeAssessmentCollection.class);
						if(eyeAssessmentCount > 0)response.setIsEyeAssessmentPresent(true);
						
						Integer entAssessmentCount = (int) mongoTemplate.count(new Query(assessmentCriteria), ENTAssessmentCollection.class);
						if(entAssessmentCount > 0)response.setIsENTAssessmentPresent(true);
						
						Integer dentalAssessmentCount = (int) mongoTemplate.count(new Query(assessmentCriteria), DentalAssessmentCollection.class);
						if(dentalAssessmentCount > 0)response.setIsDentalAssessmentPresent(true);
						
						Integer nutritionAssessmentCount = (int) mongoTemplate.count(new Query(assessmentCriteria), NutritionAssessmentCollection.class);
						if(nutritionAssessmentCount > 0)response.setIsNutritionalAssessmentPresent(true);
						
						Integer dietPlanCount = (int) mongoTemplate.count(new Query(new Criteria("academicProfileId").is(new ObjectId(profileId))), DietPlanCollection.class);
						if(dietPlanCount > 0)response.setIsDietPlanPresent(true);
				}
				/*
				 * if (!DPDoctorUtils.anyStringEmpty(response.getImageUrl())) {
				 * response.setImageUrl(imagePath + response.getImageUrl()); } if
				 * (!DPDoctorUtils.anyStringEmpty(response.getThumbnailUrl())) {
				 * response.setThumbnailUrl(imagePath + response.getThumbnailUrl()); }
				 */
			}
		} catch (BusinessException be) {
			logger.warn(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while getting Acadamic Profile");
			throw new BusinessException(ServiceError.Unknown, "Error occured while getting Acadamic Profile");
		}
		return response;
	}
	
	
	@Override
	public List<NutritionSchoolAssociationResponse> getNutritionAssociations(int page, int size, String doctorId,
			String searchTerm, String updatedTime) {
		List<NutritionSchoolAssociationResponse> response = null;
		try {
			
			ArrayList<ObjectId> objectIds = new ArrayList<>();
			objectIds.add(new ObjectId(doctorId));
			Criteria criteria = new Criteria("doctorId").in(objectIds);
			
			if (!DPDoctorUtils.anyStringEmpty(updatedTime)) {
				criteria.and("createdTime").gte(new Date(Long.parseLong(updatedTime)));
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("branch.branchName").regex("^" + searchTerm, "i"),
						new Criteria("branch.branchName").regex("^" + searchTerm));
			}

			Aggregation aggregation = null;

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "branch.branchName")),

						Aggregation.skip((long) (page) * size), Aggregation.limit(size));
			} else {

				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "branch.branchName")));
			}
			response = mongoTemplate.aggregate(aggregation, NutritionSchoolAssociationCollection.class, NutritionSchoolAssociationResponse.class)
					.getMappedResults();

		} catch (BusinessException be) {
			logger.warn(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while getting Teacher Profile List");
			throw new BusinessException(ServiceError.Unknown, "Error occured while getting Teacher Profile list");
		}
		return response;
	}
	
	@Override
	public List<AcadamicClassResponse> getAcadamicClass(int page, int size, String branchId, String schoolId,
			String searchTerm, Boolean discarded) {
		List<AcadamicClassResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			if (discarded != null)
				criteria.and("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("name").regex("^" + searchTerm),
						new Criteria("name").regex("^" + searchTerm, "i"));
			}
			if (!DPDoctorUtils.anyStringEmpty(branchId)) {
				criteria.and("branchId").is(new ObjectId(branchId));
			}
			if (!DPDoctorUtils.anyStringEmpty(schoolId)) {
				criteria.and("schoolId").is(new ObjectId(schoolId));
			}
			Aggregation aggregation = null;
			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$id"),
					Fields.field("section", "$section.section"), Fields.field("teacher", "$teacher.firstName"),
					Fields.field("branchId", "$branchId"), Fields.field("schoolId", "$schoolId"),
					Fields.field("sectionId", "$sectionId"), Fields.field("name", "$name"),
					Fields.field("teacherId", "$teacherId"), Fields.field("discarded", "$discarded"),
					Fields.field("createdTime", "$createdTime"), Fields.field("updatedTime", "$updatedTime")));
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),


						Aggregation.lookup("acadamic_section_cl", "sectionId", "_id", "section"),
						Aggregation.unwind("section", true),
						Aggregation.lookup("acadamic_profile_cl", "teacherId", "_id", "teacher"),
						Aggregation.unwind("teacher", true), projectList,

						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),

						Aggregation.limit(size));
			} else {

				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),

						Aggregation.lookup("acadamic_section_cl", "sectionId", "_id", "section"),
						Aggregation.unwind("section", true),
						Aggregation.lookup("acadamic_profile_cl", "teacherId", "_id", "teacher"),
						Aggregation.unwind("teacher", true), projectList,

						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			}
			response = mongoTemplate.aggregate(aggregation, AcadamicClassCollection.class, AcadamicClassResponse.class)
					.getMappedResults();


		} catch (BusinessException e) {
			logger.error("Error while get Acadamic class" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while get Acadamic class" + e.getMessage());

		}

		return response;
	}
	
	@Override
	public Integer countAcadamicClass(String branchId, String schoolId, String searchTerm, Boolean discarded) {
		Integer response = 0;
		try {
			Criteria criteria = new Criteria();
			if (discarded != null)
				criteria.and("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("name").regex("^" + searchTerm),
						new Criteria("name").regex("^" + searchTerm, "i"));
			}
			if (!DPDoctorUtils.anyStringEmpty(branchId)) {
				criteria.and("branchId").is(new ObjectId(branchId));
			}
			if (!DPDoctorUtils.anyStringEmpty(schoolId)) {
				criteria.and("schoolId").is(new ObjectId(schoolId));
			}
			Aggregation aggregation = null;

			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$id")));

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.lookup("acadamic_section_cl", "sectionId", "_id", "section"),
					Aggregation.unwind("section", true),
					Aggregation.lookup("acadamic_profile_cl", "teacherId", "_id", "teacher"),
					Aggregation.unwind("teacher", true), projectList);
			response = mongoTemplate.aggregate(aggregation, AcadamicClassCollection.class, AcadamicClassResponse.class)
					.getMappedResults().size();


		} catch (BusinessException e) {
			logger.error("Error while counting Acadamic class" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while counting Acadamic class" + e.getMessage());

		}

		return response;
	}
	
	
	@Override
	public List<AcademicProfile> getProfile(int page, int size, String userId, Boolean discarded, String searchTerm) {
		List<AcademicProfile> response = null;
		try {
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(userId)) {
				criteria.and("userId").is(new ObjectId(userId));
			}

			if (discarded != null) {
				criteria.and("discarded").is(discarded);
			}

			criteria.and("isSuperStar").is(true);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("firstName").regex("^" + searchTerm, "i"),
						new Criteria("firstName").regex("^" + searchTerm),
						new Criteria("rollNo").regex("^" + searchTerm),
						new Criteria("uniqueId").regex("^" + searchTerm),
						new Criteria("emailAddress").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"));
			}
			ProjectionOperation projectList = new ProjectionOperation(Fields.from(Fields.field("id", "$id"),
					Fields.field("userId", "$userId"), Fields.field("firstName", "$firstName"),
					Fields.field("localPatientName", "$localPatientName"),
					Fields.field("mobileNumber", "$mobileNumber"), Fields.field("uniqueId", "$uniqueId"),
					Fields.field("rollNo", "$rollNo"), Fields.field("acadamicClass", "$acadamicClass"),
					Fields.field("emailAddress", "$emailAddress"), Fields.field("school", "$school"),
					Fields.field("branch", "$branch"), Fields.field("acadamicSection", "$acadamicSection.section"),
					Fields.field("admissionDate", "$admissionDate"), Fields.field("type", "$type"),
					Fields.field("imageUrl", "$imageUrl"), Fields.field("thumbnailUrl", "$thumbnailUrl"),
					Fields.field("isSuperStar", "$isSuperStar"), Fields.field("createdTime", "$createdTime")));

			Aggregation aggregation = null;

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("acadamic_class_cl", "classId", "_id", "acadamicClass"),
						Aggregation.unwind("acadamicClass", true),
						Aggregation.lookup("acadamic_section_cl", "sectionId", "_id", "acadamicSection"),
						Aggregation.unwind("acadamicSection", true),
						Aggregation.lookup("school_branch_cl", "branchId", "_id", "branch"),
						Aggregation.unwind("branch"), Aggregation.match(new Criteria("branch.isActivated").is(true)),
						Aggregation.lookup("school_cl", "schoolId", "_id", "school"), Aggregation.unwind("school"),
						Aggregation.match(new Criteria("school.isActivated").is(true)), projectList,
						Aggregation.sort(new Sort(Sort.Direction.ASC, "firstName")),

						Aggregation.skip((long) (page) * size), Aggregation.limit(size));
			} else {

				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("acadamic_class_cl", "classId", "_id", "acadamicClass"),
						Aggregation.unwind("acadamicClass", true),
						Aggregation.lookup("acadamic_section_cl", "sectionId", "_id", "acadamicSection"),
						Aggregation.unwind("acadamicSection", true),
						Aggregation.lookup("school_branch_cl", "branchId", "_id", "branch"),
						Aggregation.unwind("branch"),Aggregation.match(new Criteria("branch.isActivated").is(true)),
						Aggregation.lookup("school_cl", "schoolId", "_id", "school"), Aggregation.unwind("school"),
						Aggregation.match(new Criteria("school.isActivated").is(true)), projectList,
						Aggregation.sort(new Sort(Sort.Direction.ASC, "firstName")));
			}
			response = mongoTemplate.aggregate(aggregation, AcademicProfileCollection.class, AcademicProfile.class)
					.getMappedResults();
//			for (AcadamicProfile acadamicProfile : response) {
//				if (!DPDoctorUtils.anyStringEmpty(acadamicProfile.getImageUrl())) {
//					acadamicProfile.setImageUrl(imagePath + acadamicProfile.getImageUrl());
//				}
//				if (!DPDoctorUtils.anyStringEmpty(acadamicProfile.getThumbnailUrl())) {
//					acadamicProfile.setThumbnailUrl(imagePath + acadamicProfile.getThumbnailUrl());
//				}
//			}

		} catch (BusinessException be) {
			logger.warn(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while getting Student Profile List");
			throw new BusinessException(ServiceError.Unknown, "Error occured while getting Student Profile list");
		}
		return response;
	}


	@Override
	public Integer countProfile(String userId, Boolean discarded, String searchTerm) {
		Integer response = 0;
		try {
			Criteria criteria = new Criteria();

			if (!DPDoctorUtils.anyStringEmpty(userId)) {
				criteria.and("userId").is(new ObjectId(userId));
			}

			if (discarded != null) {
				criteria.and("discarded").is(discarded);
			}

			criteria.and("isSuperStar").is(true);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("firstName").regex("^" + searchTerm, "i"),
						new Criteria("firstName").regex("^" + searchTerm),
						new Criteria("rollNo").regex("^" + searchTerm),
						new Criteria("uniqueId").regex("^" + searchTerm),
						new Criteria("emailAddress").regex("^" + searchTerm, "i"),
						new Criteria("mobileNumber").regex("^" + searchTerm, "i"));
			}
			
			Aggregation aggregation = null;

			

				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),					
						Aggregation.lookup("school_branch_cl", "branchId", "_id", "branch"),
						Aggregation.unwind("branch"),Aggregation.match(new Criteria("branch.isActivated").is(true)),
						Aggregation.lookup("school_cl", "schoolId", "_id", "school"), Aggregation.unwind("school"),
						Aggregation.match(new Criteria("school.isActivated").is(true)));
			
			response = mongoTemplate.aggregate(aggregation, AcademicProfileCollection.class, AcademicProfile.class)
					.getMappedResults().size();
			
		} catch (BusinessException be) {
			logger.warn(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while count Student Profile List");
			throw new BusinessException(ServiceError.Unknown, "Error occured while count Student Profile list");
		}
		return response;
	}

	@Override
	public NutritionRDA getRDAForUser(String academicProfileId, String doctorId, String locationId, String hospitalId) {
		NutritionRDA response = null;
		try {
			AcademicProfileCollection acadamicProfileCollection = acadamicProfileRespository.findById(new ObjectId(academicProfileId)).orElse(null);
			if(acadamicProfileCollection == null) {
				logger.warn("No academic profile found with this Id");
				throw new BusinessException(ServiceError.InvalidInput, "No academic profile found with this Id");
			}
			
			if(acadamicProfileCollection.getAddress() == null || DPDoctorUtils.allStringsEmpty(acadamicProfileCollection.getAddress().getCountry())) {
				logger.warn("Patient country is null or empty");
				throw new BusinessException(ServiceError.InvalidInput, "Patient country is null or empty");
			}
			
			if(DPDoctorUtils.allStringsEmpty(acadamicProfileCollection.getGender())) {
				logger.warn("Patient gender is null or empty");
				throw new BusinessException(ServiceError.InvalidInput, "Patient gender is null or empty");
			}
			
			if(acadamicProfileCollection.getDob() == null) {
				logger.warn("Patient date of birth is null or empty");
				throw new BusinessException(ServiceError.InvalidInput, "Patient date of birth is null or empty");
			}
			
			NutritionAssessmentCollection nutritionAssessment = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(new Criteria("academicProfileId").is(new ObjectId(academicProfileId)).and("discarded").is(false)), Aggregation.sort(Direction.DESC, "createdTime"),
							Aggregation.limit(1)), NutritionAssessmentCollection.class, NutritionAssessmentCollection.class).getUniqueMappedResult();
			
			if(nutritionAssessment == null) {
				logger.warn("No assessment is set for this patient");
				throw new BusinessException(ServiceError.InvalidInput, "No assessment is set for this patient");
			}
			Criteria criteria = new Criteria("country").is(acadamicProfileCollection.getAddress().getCountry())
					.and("gender").is(acadamicProfileCollection.getGender())
					.and("type").is(nutritionAssessment.getType());
			
			double ageInYears = acadamicProfileCollection.getDob().getAge().getYears() 
					+ (double)acadamicProfileCollection.getDob().getAge().getMonths()/12
					+ (double)acadamicProfileCollection.getDob().getAge().getDays()/365; 

			criteria.and("fromAgeInYears").lte(ageInYears).and("toAgeInYears").gte(ageInYears);
					
			List<String> emptyArr = new ArrayList<String>();
				criteria.orOperator(new Criteria("pregnancyCategory").is(null), new Criteria("pregnancyCategory").is(emptyArr));
			
			response = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria)), NutritionRDACollection.class ,NutritionRDA.class).getUniqueMappedResult(); 
		}catch(BusinessException e) {
			logger.error("Error while getting RDA for user " + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting RDA for user " + e.getMessage());

		}
		return response;
	}

	@Override
	public UserAssessment getUserAssessment(String academicProfileId, String doctorId) {
		UserAssessment response = new UserAssessment();
		try {
			response.setRegistrationDetails(getAcadamicProfile(academicProfileId));
			Criteria criteria = new Criteria("academicProfileId").is(new ObjectId(academicProfileId)).and("discarded").is(false);
			
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").is(new ObjectId(doctorId));
			}
			GrowthAssessmentAndGeneralBioMetrics growthAssessmentAndGeneralBioMetrics = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.sort(Direction.DESC, "createdTime"),
							Aggregation.limit(1)), GrowthAssessmentAndGeneralBioMetricsCollection.class, GrowthAssessmentAndGeneralBioMetrics.class).getUniqueMappedResult();
			response.setGrowthAssessmentAndGeneralBioMetrics(growthAssessmentAndGeneralBioMetrics);
			
			PhysicalAssessment physicalAssessment = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.sort(Direction.DESC, "createdTime"),
							Aggregation.limit(1)), PhysicalAssessmentCollection.class, PhysicalAssessment.class).getUniqueMappedResult();
			response.setPhysicalAssessment(physicalAssessment);
			
			
			ENTAssessment entAssessment = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.sort(Direction.DESC, "createdTime"),
							Aggregation.limit(1)), ENTAssessmentCollection.class, ENTAssessment.class).getUniqueMappedResult();
			response.setEntAssessment(entAssessment);
			
			DentalAssessment dentalAssessment = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.sort(Direction.DESC, "createdTime"),
							Aggregation.limit(1)), DentalAssessmentCollection.class, DentalAssessment.class).getUniqueMappedResult();
			response.setDentalAssessment(dentalAssessment);
			
			EyeAssessment eyeAssessment = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.sort(Direction.DESC, "createdTime"),
							Aggregation.limit(1)), EyeAssessmentCollection.class, EyeAssessment.class).getUniqueMappedResult();
			
			response.setEyeAssessment(eyeAssessment);
			
			NutritionAssessment nutritionAssessment = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.sort(Direction.DESC, "createdTime"),
							Aggregation.limit(1)), NutritionAssessmentCollection.class, NutritionAssessment.class).getUniqueMappedResult();
			
			response.setNutritionAssessment(nutritionAssessment);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public List<DoctorSchoolAssociation> getDoctorAssociations(int page, int size, String doctorId, String searchTerm,
			String updatedTime, String branchId, String department) {
		List<DoctorSchoolAssociation> response = null;
		try {
			
			ArrayList<ObjectId> objectIds = new ArrayList<>();
			objectIds.add(new ObjectId(doctorId));
			Criteria criteria = new Criteria("doctorId").in(objectIds);
			
			if (!DPDoctorUtils.anyStringEmpty(branchId)) {
				criteria.and("branchId").is(new ObjectId(branchId));
			}
			if (!DPDoctorUtils.anyStringEmpty(department)) {
				criteria.and("departments.departments").is(department);
			}
			if (!DPDoctorUtils.anyStringEmpty(updatedTime)) {
				criteria.and("createdTime").gte(new Date(Long.parseLong(updatedTime)));
			}
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("branch.branchName").regex("^" + searchTerm, "i"),
						new Criteria("branch.branchName").regex("^" + searchTerm));
			}

			Aggregation aggregation = null;

			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "branch.branchName")),

						Aggregation.skip((long) (page) * size), Aggregation.limit(size));
			} else {

				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.ASC, "branch.branchName")));
			}
			response = mongoTemplate.aggregate(aggregation, DoctorSchoolAssociationCollection.class, DoctorSchoolAssociation.class)
					.getMappedResults();

		} catch (BusinessException be) {
			logger.warn(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while getting Teacher Profile List");
			throw new BusinessException(ServiceError.Unknown, "Error occured while getting Teacher Profile list");
		}
		return response;
	}

	@Override
	public UserTreatment addUserTreatment(UserTreatment request) {
		UserTreatment response = null;
		try {
			
			UserTreatmentCollection userTreatmentCollection = null;
			if(!DPDoctorUtils.anyStringEmpty(request.getId())) {
				userTreatmentCollection = userTreatmentRepository.findById(new ObjectId(request.getId())).orElse(null);
				if(userTreatmentCollection == null) {
					throw new BusinessException(ServiceError.Unknown, "Invalid Id. No user treatment found");
				}
				request.setUpdatedTime(new Date());
				request.setAdminCreatedTime(userTreatmentCollection.getAdminCreatedTime());
				request.setCreatedTime(userTreatmentCollection.getCreatedTime());
				request.setCreatedBy(userTreatmentCollection.getCreatedBy());
				
				BeanUtil.map(request, userTreatmentCollection);
			}else {
				userTreatmentCollection = new UserTreatmentCollection();
				BeanUtil.map(request, userTreatmentCollection);
				
				userTreatmentCollection.setUpdatedTime(new Date());
				userTreatmentCollection.setCreatedTime(new Date());
				userTreatmentCollection.setAdminCreatedTime(new Date());
				
				if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
					UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId())).orElse(null);
					if (userCollection != null) {
						userTreatmentCollection.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
										+ userCollection.getFirstName());
					}
				}
			}
			userTreatmentCollection = userTreatmentRepository.save(userTreatmentCollection);
			response = new UserTreatment();
			BeanUtil.map(userTreatmentCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while adding User Treatment");
			throw new BusinessException(ServiceError.Unknown, "Error occured while adding User Treatment");
		}
		return response;
	}

	@Override
	public UserTreatment getUserTreatmentById(String id) {
		UserTreatment response = null;
		try {
			UserTreatmentCollection userTreatmentCollection = userTreatmentRepository.findById(new ObjectId(id)).orElse(null);
			if(userTreatmentCollection == null) {
				throw new BusinessException(ServiceError.Unknown, "Invalid Id. No user treatment found");
			}
			response = new UserTreatment();
			BeanUtil.map(userTreatmentCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while getting User Treatmentby Id");
			throw new BusinessException(ServiceError.Unknown, "Error occured while getting User Treatment by Id");
		}
		return response;
	}

	@Override
	public List<UserTreatment> getUserTreatments(int size, int page, String userId, String doctorId, String locationId,
			String hospitalId, Boolean discarded, String updatedTime, String department) {
		List<UserTreatment> response = null;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(updatedTime)) {
				criteria.and("createdTime").gte(new Date(Long.parseLong(updatedTime)));
			}
			if(!DPDoctorUtils.anyStringEmpty(userId))
				criteria.and("userId").is(new ObjectId(userId));
			
			if(!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));

			if(!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));

			if(!DPDoctorUtils.anyStringEmpty(hospitalId))
				criteria.and("hospitalId").is(new ObjectId(hospitalId));

			if(discarded != null)
				criteria.and("discarded").is(discarded);

			if(!DPDoctorUtils.anyStringEmpty(department))
				criteria.and("department").is(department);
			
			if(size > 0) {
				response = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
						Aggregation.skip((long) (page) * size), Aggregation.limit(size)), UserTreatmentCollection.class, UserTreatment.class).getMappedResults();
			}else {
				response = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime"))), UserTreatmentCollection.class, UserTreatment.class).getMappedResults();
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while getting User Treatment");
			throw new BusinessException(ServiceError.Unknown, "Error occured while getting User Treatment");
		}
		return response;
	}

	@Override
	public UserTreatment deleteUserTreatment(String id, Boolean discarded) {
		UserTreatment response = null;
		try {
			UserTreatmentCollection userTreatmentCollection = userTreatmentRepository.findById(new ObjectId(id)).orElse(null);
			if(userTreatmentCollection == null) {
				throw new BusinessException(ServiceError.Unknown, "Invalid Id. No user treatment found");
			}
			userTreatmentCollection.setDiscarded(discarded);
			userTreatmentCollection.setUpdatedTime(new Date());
			userTreatmentCollection = userTreatmentRepository.save(userTreatmentCollection);
			
			response = new UserTreatment();
			BeanUtil.map(userTreatmentCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while deleting User Treatment");
			throw new BusinessException(ServiceError.Unknown, "Error occured while deleting User Treatment");
		}
		return response;
	}

	@Override
	public List<Object> getUserTreatmentAnalyticsData(String doctorId, String locationId, String hospitalId,
			long fromDate, long toDate, String department, Boolean discarded) {
		List<Object> response = null;
		try {
			Criteria criteria = new Criteria();
			
			if(!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));

			if(!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(new ObjectId(locationId));

			if(!DPDoctorUtils.anyStringEmpty(hospitalId))
				criteria.and("hospitalId").is(new ObjectId(hospitalId));

			if(discarded != null)
				criteria.and("discarded").is(discarded);

			if(!DPDoctorUtils.anyStringEmpty(department))
				criteria.and("department").is(department);
			
			if (fromDate > 0 && toDate > 0) {
				criteria.and("createdTime").gte(new Date(fromDate)).lte(new Date(toDate));
			} else if (fromDate > 0) {
				criteria.and("createdTime").gte(new Date(fromDate));
			} else if (toDate > 0) {
				criteria.and("createdTime").lte(new Date(toDate));
			}

			response = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.unwind("department"),
					new CustomAggregationOperation(new Document("$group", 
							new BasicDBObject("department", new BasicDBObject("$first", "$department")
									.append("count", new BasicDBObject("$sum", 1)))))), UserTreatmentCollection.class, Object.class).getMappedResults();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error occured while getting User Treatment analytics");
			throw new BusinessException(ServiceError.Unknown, "Error occured while getting User Treatment analytics");
		}
		return response;
	}
	
}