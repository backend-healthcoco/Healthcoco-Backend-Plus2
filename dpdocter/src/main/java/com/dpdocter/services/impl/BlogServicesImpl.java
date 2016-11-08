package com.dpdocter.services.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Blog;
import com.dpdocter.collections.BlogCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.BlogRepository;
import com.dpdocter.repository.GridFsRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.BlogService;
import com.mongodb.gridfs.GridFSDBFile;

import common.util.web.DPDoctorUtils;

@Transactional
@Service
public class BlogServicesImpl implements BlogService {

	private static Logger logger = Logger.getLogger(BlogServicesImpl.class.getName());

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private GridFsRepository gridfsRepository;

	@Autowired
	private BlogRepository blogRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public List<Blog> getBlogs(int size, int page, String category, String userId, String title) {
		List<Blog> response = null;

		try {
			response = new ArrayList<Blog>();
			Criteria criteria = new Criteria().and("discarded").is(false);
			Aggregation aggregation = null;

			List<BlogCollection> blogCollections = null;
			if (!DPDoctorUtils.anyStringEmpty(userId))
				criteria = criteria.and(userId).is(new ObjectId(userId));
			if (!DPDoctorUtils.anyStringEmpty(title))
				criteria = criteria.orOperator(new Criteria("title").regex("^" + title, "i"));
			if (!DPDoctorUtils.anyStringEmpty(category))
				criteria = criteria.and(category);
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.skip((page) * size),
						Aggregation.sort(Sort.Direction.DESC, "updatedTime"));

			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "updatedTime"));
			}
			AggregationResults<BlogCollection> results = mongoTemplate.aggregate(aggregation, BlogCollection.class,
					BlogCollection.class);
			blogCollections = results.getMappedResults();
			response = new ArrayList<Blog>();
			for (BlogCollection blogCollection : blogCollections) {
				Blog blog = new Blog();
				BeanUtil.map(blogCollection, blog);
				blog.setArticle(this.getBlogArticle(blog.getArticleId()));
				if (!DPDoctorUtils.anyStringEmpty(blog.getTitleImage()))
					blog.setTitleImage(imagePath + blog.getTitleImage());
				if (blogCollection.getLikes() != null)
					blog.setNoOfLikes(blogCollection.getLikes().size());
				response.add(blog);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return response;
	}

	private String getBlogArticle(String id) {

		try {
			GridFSDBFile file = gridfsRepository.read(new ObjectId(id));
			InputStream inputStream = file.getInputStream();

			return IOUtils.toString(inputStream);
		} catch (Exception e) {
			return null;
		}

	}

	@Override
	public Blog getBlog(String id, String userId) {
		Blog response = null;
		try {
			BlogCollection blogCollection = blogRepository.findOne(new ObjectId(id));
			blogCollection.setViews(blogCollection.getViews() + 1);
			blogCollection = blogRepository.save(blogCollection);
			response = new Blog();
			BeanUtil.map(blogCollection, response);
			response.setArticle(this.getBlogArticle(response.getArticleId()));
			if (!DPDoctorUtils.anyStringEmpty(response.getTitleImage()))
				response.setTitleImage(imagePath + response.getTitleImage());
			if (blogCollection.getLikes() != null) {
				if (!DPDoctorUtils.anyStringEmpty(userId))
					response.setIsliked(blogCollection.getLikes().contains(new ObjectId(userId)));
				response.setNoOfLikes(blogCollection.getLikes().size());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return response;
	}

	@Override
	public Blog updateLikes(String id, String userId) {
		Blog response = null;
		boolean present = false;
		Set<ObjectId> set = new HashSet<ObjectId>();
		try {
			UserCollection userCollection = null;
			ObjectId userObjectId = null;
			if (!DPDoctorUtils.anyStringEmpty(userId)) {
				userObjectId = new ObjectId(userId);
				userCollection = userRepository.findOne(userObjectId);
			}
			if (userCollection != null) {
				BlogCollection blogCollection = blogRepository.findOne(new ObjectId(id));
				if (blogCollection.getLikes() != null)
				set.addAll(blogCollection.getLikes());
				present = set.add(userObjectId);
				if (present == false)
					set.remove(userObjectId);
				blogCollection.setLikes(set);
				blogCollection = blogRepository.save(blogCollection);
				response = new Blog();
				BeanUtil.map(blogCollection, response);
				response.setIsliked(present);
				response.setArticle(this.getBlogArticle(response.getArticleId()));
				if (!DPDoctorUtils.anyStringEmpty(response.getTitleImage()))
					response.setTitleImage(imagePath + response.getTitleImage());
				if (blogCollection.getLikes() != null)
					response.setNoOfLikes(blogCollection.getLikes().size());
			} else {
				throw new BusinessException(ServiceError.Unknown, "Invalid user ");

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return response;
	}

}
