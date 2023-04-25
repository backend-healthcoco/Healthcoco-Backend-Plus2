package com.dpdocter.services.impl;

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

import com.dpdocter.beans.IssueTrack;
import com.dpdocter.collections.IssueTrackCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.IssueStatus;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.IssueTrackRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.IssueTrackService;
import com.dpdocter.services.PushNotificationServices;

import common.util.web.DPDoctorUtils;

@Service
public class IssueTrackServiceImpl implements IssueTrackService {

	private static Logger logger = Logger.getLogger(IssueTrackServiceImpl.class.getName());

	@Autowired
	private IssueTrackRepository issueTrackRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	PushNotificationServices pushNotificationServices;

	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	@Transactional
	public IssueTrack addEditIssue(IssueTrack request) {
		IssueTrack response = null;
		IssueTrackCollection issueTrackCollection = new IssueTrackCollection();
		BeanUtil.map(request, issueTrackCollection);
		try {
			if (request.getId() == null) {
				issueTrackCollection.setStatus(IssueStatus.OPEN);
				issueTrackCollection
						.setIssueCode(UniqueIdInitial.ISSUETRACK.getInitial() + DPDoctorUtils.generateRandomId());
				issueTrackCollection.setCreatedTime(new Date());
			} else {
				IssueTrackCollection oldIssueTrackCollection = issueTrackRepository
						.findById(new ObjectId(request.getId())).orElse(null);
				issueTrackCollection.setCreatedTime(oldIssueTrackCollection.getCreatedTime());
				issueTrackCollection.setCreatedBy(oldIssueTrackCollection.getCreatedBy());
				issueTrackCollection.setDiscarded(oldIssueTrackCollection.getDiscarded());
				issueTrackCollection.setStatus(oldIssueTrackCollection.getStatus());
				issueTrackCollection.setIssueCode(oldIssueTrackCollection.getIssueCode());
			}

			if (!DPDoctorUtils.anyStringEmpty(request.getDoctorId())) {
				UserCollection userCollection = userRepository.findById(new ObjectId(request.getDoctorId()))
						.orElse(null);
				if (request.getId() == null && userCollection != null)
					issueTrackCollection
							.setCreatedBy((userCollection.getTitle() != null ? userCollection.getTitle() + " " : "")
									+ userCollection.getFirstName());
//		String body = mailBodyGenerator.generateIssueTrackEmailBody(userCollection.getUserName(), userCollection.getFirstName(), userCollection.getMiddleName(), userCollection.getLastName());
//		mailService.sendEmail(userCollection.getEmailAddress(), addIssueSubject, body, null);
				pushNotificationServices.notifyUser(userCollection.getId().toString(),
						"Your issue " + issueTrackCollection.getIssueCode()
								+ " has been recorded, we will keep you updated on the progress of the issue",
						null, null, null);
			}
			issueTrackCollection = issueTrackRepository.save(issueTrackCollection);

			if (issueTrackCollection != null) {
				response = new IssueTrack();
				BeanUtil.map(issueTrackCollection, response);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Add Edit Issue");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Add Edit Issue");
		}
		return response;
	}

	@Override
	@Transactional
	public List<IssueTrack> getIssues(long page, int size, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, List<String> scope) {
		List<IssueTrack> response = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);
			ObjectId doctorObjectId = null, locationObjectId = null, hospitalObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				doctorObjectId = new ObjectId(doctorId);
			if (!DPDoctorUtils.anyStringEmpty(locationId))
				locationObjectId = new ObjectId(locationId);
			if (!DPDoctorUtils.anyStringEmpty(hospitalId))
				hospitalObjectId = new ObjectId(hospitalId);

			Criteria criteria = new Criteria("updatedTime").gte(new Date(createdTimeStamp));
			if (!discarded)
				criteria.and("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(doctorObjectId))
				criteria.and("doctorId").is(doctorObjectId);
			if (!DPDoctorUtils.anyStringEmpty(locationObjectId, hospitalObjectId))
				criteria.and("locationId").is(locationObjectId).and("hospitalId").is(hospitalObjectId);
			if (!scope.isEmpty())
				criteria.and("status").in(scope);
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			}
			AggregationResults<IssueTrack> aggregationResults = mongoTemplate.aggregate(aggregation,
					IssueTrackCollection.class, IssueTrack.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Issue");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Issue");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean updateIssueStatus(String issueId, String status, String doctorId, String locationId,
			String hospitalId) {
		Boolean response = false;
		IssueTrackCollection issueTrackCollection = null;
		try {
			issueTrackCollection = issueTrackRepository.findById(new ObjectId(issueId)).orElse(null);
			if (issueTrackCollection != null) {
				if (issueTrackCollection.getDoctorId() != null && issueTrackCollection.getHospitalId() != null
						&& issueTrackCollection.getLocationId() != null) {
					if (issueTrackCollection.getDoctorId().toString().equals(doctorId)
							&& issueTrackCollection.getHospitalId().toString().equals(hospitalId)
							&& issueTrackCollection.getLocationId().toString().equals(locationId)) {
						if (issueTrackCollection.getStatus().equals(IssueStatus.COMPLETED)) {
							if (IssueStatus.valueOf(status.toUpperCase()).equals(IssueStatus.REOPEN)) {
								issueTrackCollection.setStatus(IssueStatus.valueOf(status.toUpperCase()));
								issueTrackRepository.save(issueTrackCollection);
								response = true;
							} else {
								logger.warn("Doctor can only reopen the issue");
								throw new BusinessException(ServiceError.NotAcceptable,
										"Doctor can only reopen the issue");
							}
						} else {
							logger.warn("Doctor can only reopen the issue if issue is Completed");
							throw new BusinessException(ServiceError.NotAcceptable,
									"Doctor can only reopen the issue if issue is Completed");
						}
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
					throw new BusinessException(ServiceError.InvalidInput,
							"Invalid Doctor Id, Hospital Id, Or Location Id");
				}
			} else {
				logger.warn("Issue not found!");
				throw new BusinessException(ServiceError.NoRecord, "Issue not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while updating status");
			throw new BusinessException(ServiceError.Unknown, "Error while updating status");
		}
		return response;
	}

	@Override
	@Transactional
	public Boolean updateIssueStatus(String issueId, String status) {
		Boolean response = false;
		IssueTrackCollection issueTrackCollection = null;
		try {
			issueTrackCollection = issueTrackRepository.findById(new ObjectId(issueId)).orElse(null);
			if (issueTrackCollection != null) {
				if (!IssueStatus.valueOf(status.toUpperCase()).equals(IssueStatus.REOPEN)) {
					issueTrackCollection.setStatus(IssueStatus.valueOf(status.toUpperCase()));
					UserCollection userCollection = userRepository.findById(issueTrackCollection.getDoctorId())
							.orElse(null);
					if (userCollection != null) {
						if (status.equalsIgnoreCase(IssueStatus.INPROGRESS.getStatus()))
							pushNotificationServices.notifyUser(userCollection.getId().toString(),
									"We have started working on the issue " + issueTrackCollection.getIssueCode()
											+ ", we will keep you upadated on the progress of the issue",
									null, null, null);
						else if (status.equalsIgnoreCase(IssueStatus.COMPLETED.getStatus()))
							pushNotificationServices.notifyUser(userCollection.getId().toString(), "Your issue "
									+ issueTrackCollection.getIssueCode()
									+ " has been resolved, please let us know if you are not satisfied; we will be happy to look at it again",
									null, null, null);

					}
					issueTrackRepository.save(issueTrackCollection);
					response = true;
				} else {
					logger.warn("Only Doctor can reopen the issue");
					throw new BusinessException(ServiceError.NotAcceptable, "Only Doctor can reopen the issue");
				}

			} else {
				logger.warn("Issue not found!");
				throw new BusinessException(ServiceError.NoRecord, "Issue not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error while updating status");
			throw new BusinessException(ServiceError.Unknown, "Error while updating status");
		}
		return response;

	}

	@Override
	@Transactional
	public IssueTrack deleteIssue(String issueId, String doctorId, String locationId, String hospitalId,
			Boolean discarded) {
		IssueTrack response = null;
		try {
			IssueTrackCollection issueTrackCollection = issueTrackRepository.findById(new ObjectId(issueId))
					.orElse(null);
			if (issueTrackCollection != null) {
				if (issueTrackCollection.getDoctorId() != null && issueTrackCollection.getHospitalId() != null
						&& issueTrackCollection.getLocationId() != null) {
					if (issueTrackCollection.getDoctorId().toString().equals(doctorId)
							&& issueTrackCollection.getHospitalId().toString().equals(hospitalId)
							&& issueTrackCollection.getLocationId().toString().equals(locationId)) {
						issueTrackCollection.setDiscarded(discarded);
						issueTrackCollection.setUpdatedTime(new Date());
						issueTrackRepository.save(issueTrackCollection);
						response = new IssueTrack();
						BeanUtil.map(issueTrackCollection, response);
					} else {
						logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
						throw new BusinessException(ServiceError.InvalidInput,
								"Invalid Doctor Id, Hospital Id, Or Location Id");
					}
				} else {
					logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
					throw new BusinessException(ServiceError.InvalidInput,
							"Invalid Doctor Id, Hospital Id, Or Location Id");
				}

			} else {
				logger.warn("Issue not found!");
				throw new BusinessException(ServiceError.NoRecord, "Issue not found!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

}
