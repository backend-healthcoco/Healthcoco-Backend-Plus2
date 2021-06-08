package com.dpdocter.services.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Comment;
import com.dpdocter.beans.CommentRequest;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.ForumRequest;
import com.dpdocter.beans.ForumResponse;
import com.dpdocter.collections.CommentCollection;
import com.dpdocter.collections.ForumResponseCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.CommentRepository;
import com.dpdocter.repository.ForumResponseRepository;
import com.dpdocter.services.CommunityBuildingService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class CommunityBuildingServiceImpl implements CommunityBuildingService{
	
	private static Logger logger = LogManager.getLogger(CommunityBuildingServiceImpl.class.getName());

	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private ForumResponseRepository forumRepository;
	
	@Autowired
	private CommentRepository commentRepository;

	@Override
	public ForumResponse addEditForumResponse(ForumRequest request) {
		ForumResponse response = null;
		try {
			ForumResponseCollection collection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				collection = forumRepository.findById(new ObjectId(request.getId())).orElse(null);
				if (collection != null) {

					BeanUtil.map(request, collection);
					
//					if(request.getUserIds()!=null)
//					{
//						collection.setUserIds(null);
//						collection.setUserIds(request.getUserIds());
//					}
					collection.setUpdatedTime(new Date());
					forumRepository.save(collection);
				} else
					throw new BusinessException(ServiceError.NoRecord);
			} else {
				collection = new ForumResponseCollection();
				BeanUtil.map(request, collection);
				collection.setCreatedTime(new Date());
				collection.setUpdatedTime(new Date());
				forumRepository.save(collection);
			}
			response = new ForumResponse();
			BeanUtil.map(collection, response);

		} catch (BusinessException e) {
			logger.error("Error while getting learning screen");
			throw new BusinessException(ServiceError.Unknown, "Error while getting learning screen");
		}
		return response;
	}

	@Override
	public List<ForumResponse> getForumResponse(int page, int size, String searchTerm, Boolean discarded) {
		List<ForumResponse> response = null;
		try {
			Criteria criteria = new Criteria();
			if (discarded != null)
				criteria.and("discarded").is(discarded);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm))
				criteria = criteria.orOperator(new Criteria("text").regex("^" + searchTerm, "i"),
						new Criteria("text").regex("^" + searchTerm));
			
			CustomAggregationOperation project = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("_id", "$_id").append("userId", "$userId").append("userName", "$userName")
							.append("text", "$text").append("title", "$title").append("comments", "$comments")));

			CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id")
					.append("userId", new BasicDBObject("$first", "$userId"))
					.append("userName", new BasicDBObject("$first", "$userName"))
					.append("text", new BasicDBObject("$first", "$text"))
						.append("title", new BasicDBObject("$first", "$title"))
						.append("comments", new BasicDBObject("$addToSet", "$comments"))));

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("comment_cl","postId" ,"_id","comments"),
						Aggregation.unwind("comments"),
						Aggregation.match(new Criteria("comments.discarded").is(false)),
						Aggregation.match(criteria),group,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));

			} else {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("comment_cl","postId" ,"_id","comments"),
						Aggregation.unwind("comments"),
						Aggregation.match(new Criteria("comments.discarded").is(false)),
						Aggregation.match(criteria),group,
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			}
			response = mongoTemplate.aggregate(aggregation, ForumResponseCollection.class, ForumResponse.class)
					.getMappedResults();

		} catch (BusinessException e) {
			logger.error("Error while getting forum "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while getting forum");
		}
		return response;
	}
	
	@Override
	public ForumResponse getForumResponseById(String id) {
		ForumResponse response = null;
		try {
			CustomAggregationOperation project = new CustomAggregationOperation(new Document("$project",
					new BasicDBObject("_id", "$_id").append("userId", "$userId").append("userName", "$userName")
							.append("text", "$text").append("title", "$title").append("comments", "$comments")));

			CustomAggregationOperation group = new CustomAggregationOperation(new Document("$group",
					new BasicDBObject("_id", "$_id")
					.append("userId", new BasicDBObject("$first", "$userId"))
					.append("userName", new BasicDBObject("$first", "$userName"))
					.append("text", new BasicDBObject("$first", "$text"))
						.append("title", new BasicDBObject("$first", "$title"))
						.append("comments", new BasicDBObject("$addToSet", "$comments"))));
			
			Aggregation aggregation = null;

			aggregation = Aggregation.newAggregation(Aggregation.match(new Criteria("_id").is(new ObjectId(id))),
					Aggregation.lookup("comment_cl","postId" ,"_id","comments"),
					Aggregation.unwind("comments"),
					Aggregation.match(new Criteria("comments.discarded").is(false)),
					group,
					Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));

			response = mongoTemplate.aggregate(aggregation, ForumResponseCollection.class, ForumResponse.class)
					.getUniqueMappedResult();


			
		}catch (BusinessException e) {
			logger.error("Error while get by forum "+e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while get by forum");
		}
		return response;
	}
	
	
	@Override
	public Comment addEditComment(CommentRequest request) {
		Comment response = null;
		try {

			CommentCollection commentCollection = null;

			if (request.getId() != null) {
				commentCollection = commentRepository.findById(new ObjectId(request.getId())).orElse(null);
				BeanUtil.map(request, commentCollection);
				commentCollection.setUpdatedTime(new Date());
				commentRepository.save(commentCollection);
			} else {
				commentCollection = new CommentCollection();
				BeanUtil.map(request, commentCollection);
				commentCollection.setCreatedTime(new Date());
				commentCollection.setUpdatedTime(new Date());
				commentRepository.save(commentCollection);
			}
			response = new Comment();
			BeanUtil.map(commentCollection, response);

		}

		catch (BusinessException e) {
			logger.error("Error while addEdit post " + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error while addEdit post");
		}
		return response;
	}

}
