package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Drug;
import com.dpdocter.beans.PrescriptionItem;
import com.dpdocter.beans.PrescriptionItemDetail;
import com.dpdocter.collections.DischargeSummaryCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DischargeSummaryRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DischargeSummaryRequest;
import com.dpdocter.request.DrugAddEditRequest;
import com.dpdocter.response.DischargeSummaryResponse;
import com.dpdocter.services.DischargeSummaryService;
import com.dpdocter.services.PrescriptionServices;

import common.util.web.DPDoctorUtils;

@Service
public class DischargeSummaryServiceImpl implements DischargeSummaryService {

	private static Logger logger = Logger.getLogger(DischargeSummaryServiceImpl.class.getName());

	@Autowired
	private DischargeSummaryRepository dischargeSummaryRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DrugRepository drugRepository;

	@Autowired
	private PrescriptionServices prescriptionServices;

	@Transactional
	@Override
	public DischargeSummaryResponse addEditDischargeSummary(DischargeSummaryRequest dischargeSummary) {

		DischargeSummaryResponse response = null;
		try {

			DischargeSummaryCollection dischargeSummaryCollection = null;

			if (dischargeSummary.getId() == null) {
				dischargeSummaryCollection = new DischargeSummaryCollection();
				dischargeSummary.setDischargeId(
						UniqueIdInitial.DISCHARGE_SUMMARY.getInitial() + "-" + DPDoctorUtils.generateRandomId());
			} else {
				dischargeSummaryCollection = dischargeSummaryRepository.findOne(new ObjectId(dischargeSummary.getId()));
			}
			if (dischargeSummaryCollection != null) {

				BeanUtil.map(dischargeSummary, dischargeSummaryCollection);

				UserCollection doctor = userRepository.findOne(dischargeSummaryCollection.getDoctorId());

				dischargeSummaryCollection.setCreatedTime(new Date());
				dischargeSummaryCollection.setCreatedBy(doctor.getFirstName());

				dischargeSummaryCollection = dischargeSummaryRepository.save(dischargeSummaryCollection);
				response = new DischargeSummaryResponse();

				BeanUtil.map(dischargeSummaryCollection, response);
				if (dischargeSummaryCollection.getPrescriptions() != null) {
					List<PrescriptionItemDetail> items = null;
					for (PrescriptionItem item : dischargeSummaryCollection.getPrescriptions().getItems()) {
						PrescriptionItemDetail prescriptionItemDetail = new PrescriptionItemDetail();
						BeanUtil.map(item, prescriptionItemDetail);
						items = new ArrayList<PrescriptionItemDetail>();
						DrugCollection drugCollection = drugRepository.findOne(item.getDrugId());
						Drug drug = new Drug();
						if (drugCollection != null) {
							BeanUtil.map(drugCollection, drug);
							DrugAddEditRequest drugAddEditRequest = new DrugAddEditRequest();
							BeanUtil.map(drugCollection, drugAddEditRequest);
							drugAddEditRequest.setDoctorId(dischargeSummaryCollection.getDoctorId().toString());
							drugAddEditRequest.setHospitalId(dischargeSummaryCollection.getHospitalId().toString());
							drugAddEditRequest.setLocationId(dischargeSummaryCollection.getLocationId().toString());
							drugAddEditRequest.setDirection(item.getDirection());
							drugAddEditRequest.setDuration(item.getDuration());
							drugAddEditRequest.setDosage(item.getDosage());
							drugAddEditRequest.setDosageTime(item.getDosageTime());
							prescriptionServices.addFavouriteDrug(drugAddEditRequest);
						}
						prescriptionItemDetail.setDrug(drug);
						items.add(prescriptionItemDetail);

					}
					response.getPrescriptions().setItems(items);
				}

			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid  discharge summary Id  ");

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while adding  discharge summary : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while adding discharge summary : " + e.getCause().getMessage());

		}
		return response;
	}

	/*
	 * @Transactional
	 * 
	 * @Override public List<DischargeSummary> getAllDischargeSummary() {
	 * List<DischargeSummary> response = null; DischargeSummary dischargeSummary
	 * = null; List<DischargeSummaryCollection> dischargeSummaryCollections =
	 * null;
	 * 
	 * dischargeSummaryCollections = dischargeSummaryRepository.findAll(); for
	 * (DischargeSummaryCollection dischargeSummaryCollection :
	 * dischargeSummaryCollections) { dischargeSummary = new DischargeSummary();
	 * BeanUtil.map(dischargeSummaryCollection, dischargeSummary);
	 * response.add(dischargeSummary); }
	 * 
	 * return response; }
	 */

	@Override
	@Transactional
	public List<DischargeSummaryResponse> getDischargeSummary(String doctorId, String locationId, String hospitalId,
			String patientId, int page, int size, String updatedTime) {
		List<DischargeSummaryResponse> response = null;
		try {
			DischargeSummaryResponse summaryResponse = null;
			ObjectId patientObjectId = null, doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(patientId))
				patientObjectId = new ObjectId(patientId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(Long.parseLong(updatedTime))).and("patientId")
					.is(patientObjectId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				criteria.and("locationId").is(locationObjectId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				criteria.and("hospitalId").is(hospitalObjectId);
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(doctorObjectId);

			Aggregation aggregation = null;

			if (size > 0)
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			else
				aggregation = Aggregation.newAggregation(

						Aggregation.match(criteria), Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			AggregationResults<DischargeSummaryCollection> aggregationResults = mongoTemplate.aggregate(aggregation,
					DischargeSummaryCollection.class, DischargeSummaryCollection.class);
			List<DischargeSummaryCollection> dischargeSummaryCollections = aggregationResults.getMappedResults();
			response = new ArrayList<DischargeSummaryResponse>();
			for (DischargeSummaryCollection dischargeSummaryCollection : dischargeSummaryCollections) {
				summaryResponse = new DischargeSummaryResponse();
				BeanUtil.map(dischargeSummaryCollection, summaryResponse);

				if (dischargeSummaryCollection.getPrescriptions() != null) {
					List<PrescriptionItemDetail> items = new ArrayList<PrescriptionItemDetail>();
					if (dischargeSummaryCollection.getPrescriptions().getItems() != null
							&& !dischargeSummaryCollection.getPrescriptions().getItems().isEmpty())
						for (PrescriptionItem item : dischargeSummaryCollection.getPrescriptions().getItems()) {
							PrescriptionItemDetail prescriptionItemDetail = new PrescriptionItemDetail();
							BeanUtil.map(item, prescriptionItemDetail);
							if (item.getDrugId() != null) {
								DrugCollection drugCollection = drugRepository.findOne(item.getDrugId());
								Drug drug = new Drug();
								BeanUtil.map(drugCollection, drug);
								prescriptionItemDetail.setDrug(drug);
							}
							items.add(prescriptionItemDetail);

						}
					summaryResponse.getPrescriptions().setItems(items);
				}
				response.add(summaryResponse);

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while getting discharge summary : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while getting discharge summary : " + e.getCause().getMessage());
		}
		return response;
	}

	@Override
	public DischargeSummaryResponse viewDischargeSummary(String dischargeSummeryId) {
		DischargeSummaryResponse response = null;
		try {
			DischargeSummaryCollection dischargeSummaryCollection = dischargeSummaryRepository
					.findOne(new ObjectId(dischargeSummeryId));
			if (dischargeSummaryCollection != null) {
				response = new DischargeSummaryResponse();
				BeanUtil.map(dischargeSummaryCollection, response);

				if (dischargeSummaryCollection.getPrescriptions() != null) {
					List<PrescriptionItemDetail> items = new ArrayList<PrescriptionItemDetail>();

					if (dischargeSummaryCollection.getPrescriptions().getItems() != null
							&& !dischargeSummaryCollection.getPrescriptions().getItems().isEmpty())
						for (PrescriptionItem item : dischargeSummaryCollection.getPrescriptions().getItems()) {
							PrescriptionItemDetail prescriptionItemDetail = new PrescriptionItemDetail();

							BeanUtil.map(item, prescriptionItemDetail);

							if (item.getDrugId() != null) {
								DrugCollection drugCollection = drugRepository.findOne(item.getDrugId());
								Drug drug = new Drug();
								BeanUtil.map(drugCollection, drug);
								prescriptionItemDetail.setDrug(drug);
							}
							items.add(prescriptionItemDetail);

						}
					response.getPrescriptions().setItems(items);
				}

			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid discharge summaryId ");

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while view discharge summary : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while view discharge summary : " + e.getCause().getMessage());
		}

		return response;
	}

	@Override
	public int getDischargeSummaryCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, boolean isOTPVerified) {
		int response = 0;
		try {
			if (isOTPVerified)
				response = dischargeSummaryRepository.countByPatientId(patientObjectId);
			else
				response = dischargeSummaryRepository.countByPatientIdDoctorLocationHospital(patientObjectId,
						doctorObjectId, locationObjectId, hospitalObjectId);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while count discharge summary : " + e.getCause().getMessage());
			throw new BusinessException(ServiceError.Unknown,
					"Error while count discharge summary : " + e.getCause().getMessage());
		}

		return response;
	}

}
