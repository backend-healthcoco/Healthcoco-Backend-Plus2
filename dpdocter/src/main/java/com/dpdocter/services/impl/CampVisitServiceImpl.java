package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import com.dpdocter.beans.AcadamicProfile;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DentalAssessment;
import com.dpdocter.beans.DrugInfo;
import com.dpdocter.beans.ENTAssessment;
import com.dpdocter.beans.EyeAssessment;
import com.dpdocter.beans.GrowthAssessmentAndGeneralBioMetrics;
import com.dpdocter.beans.NutritionAssessment;
import com.dpdocter.beans.PhysicalAssessment;
import com.dpdocter.beans.RegistrationDetails;
import com.dpdocter.collections.AcadamicClassCollection;
import com.dpdocter.collections.AcadamicClassSectionCollection;
import com.dpdocter.collections.AcadamicProfileCollection;
import com.dpdocter.collections.DentalAssessmentCollection;
import com.dpdocter.collections.DrugInfoCollection;
import com.dpdocter.collections.ENTAssessmentCollection;
import com.dpdocter.collections.EyeAssessmentCollection;
import com.dpdocter.collections.GrowthAssessmentAndGeneralBioMetricsCollection;
import com.dpdocter.collections.NutritionAssessmentCollection;
import com.dpdocter.collections.NutritionSchoolAssociationCollection;
import com.dpdocter.collections.PhysicalAssessmentCollection;
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
import com.dpdocter.repository.PhysicalAssessmentRepository;
import com.dpdocter.response.AcadamicClassResponse;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.response.NutritionSchoolAssociationResponse;
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
			String branchId, String doctorId, String updatedTime, int page, int size, Boolean isDiscarded) {
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
	
	@Override
	public List<AcadamicProfile> getStudentProfile(int page, int size, String branchId, String schoolId, String classId,
			String sectionId, String searchTerm, Boolean discarded, String profileType, String userId,
			String updatedTime) {
		List<AcadamicProfile> response = null;
		try {
			Criteria criteria = new Criteria("branchId").is(new ObjectId(branchId)).and("schoolId")
					.is(new ObjectId(schoolId));
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
						Aggregation.unwind("acadamicSection"), projectList,
						Aggregation.sort(new Sort(Sort.Direction.ASC, "firstName")),

						Aggregation.skip((long) (page) * size), Aggregation.limit(size));
			} else {

				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("acadamic_class_cl", "classId", "_id", "acadamicClass"),
						Aggregation.unwind("acadamicClass"),
						Aggregation.lookup("acadamic_section_cl", "sectionId", "_id", "acadamicSection"),
						Aggregation.unwind("acadamicSection"), projectList,
						Aggregation.sort(new Sort(Sort.Direction.ASC, "firstName")));
			}
			response = mongoTemplate.aggregate(aggregation, AcadamicProfileCollection.class, AcadamicProfile.class)
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
			logger.error(e + " Error occured while getting Student Profile List");
			throw new BusinessException(ServiceError.Unknown, "Error occured while getting Student Profile list");
		}
		return response;
	}
	
	@Override
	public List<AcadamicProfile> getTeacherProfile(int page, int size, String branchId, String schoolId,
			String searchTerm, Boolean discarded, String profileType, String userId, String updatedTime) {
		List<AcadamicProfile> response = null;
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
			response = mongoTemplate.aggregate(aggregation, AcadamicProfileCollection.class, AcadamicProfile.class)
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
					Aggregation.unwind("acadamicSection"),

					new CustomAggregationOperation(new Document("$group", new BasicDBObject("_id", "$_id"))));

			response = mongoTemplate.aggregate(aggregation, AcadamicProfileCollection.class, AcadamicProfile.class)
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

			response = mongoTemplate.aggregate(aggregation, AcadamicProfileCollection.class, AcadamicProfile.class)
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
			AcadamicProfileCollection acadamicProfileCollection = acadamicProfileRespository
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
	public List<NutritionSchoolAssociationResponse> getAssociations(int page, int size, String doctorId,
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
						Aggregation.unwind("section"),
						Aggregation.lookup("acadamic_profile_cl", "teacherId", "_id", "teacher"),
						Aggregation.unwind("teacher"), projectList,

						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),

						Aggregation.limit(size));
			} else {

				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),

						Aggregation.lookup("acadamic_section_cl", "sectionId", "_id", "section"),
						Aggregation.unwind("section"),
						Aggregation.lookup("acadamic_profile_cl", "teacherId", "_id", "teacher"),
						Aggregation.unwind("teacher"), projectList,

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
					Aggregation.unwind("section"),
					Aggregation.lookup("acadamic_profile_cl", "teacherId", "_id", "teacher"),
					Aggregation.unwind("teacher"), projectList);
			response = mongoTemplate.aggregate(aggregation, AcadamicClassCollection.class, AcadamicClassResponse.class)
					.getMappedResults().size();


		} catch (BusinessException e) {
			logger.error("Error while counting Acadamic class" + e.getMessage());
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while counting Acadamic class" + e.getMessage());

		}

		return response;
	}

	
	
}