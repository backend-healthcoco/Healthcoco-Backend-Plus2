package com.dpdocter.services.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.Blog;
import com.dpdocter.collections.BlogCollection;
import com.dpdocter.collections.BlogLikesCollection;
import com.dpdocter.collections.FavouriteBlogsCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.BlogLikesRepository;
import com.dpdocter.repository.BlogRepository;
import com.dpdocter.repository.FevouriteBlogsRepository;
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

	@Autowired
	private BlogLikesRepository blogLikesRepository;

	@Autowired
	private FevouriteBlogsRepository fevouriteBlogsRepository;

	@Override
	public List<Blog> getBlogs(int size, int page, String category, String userId, String title) {
		List<Blog> response = null;

		try {
			response = new ArrayList<Blog>();
			Criteria criteria = new Criteria().and("discarded").is(false);
			Aggregation aggregation = null;

			List<BlogCollection> blogCollections = null;

			if (!DPDoctorUtils.anyStringEmpty(title))
				criteria = criteria.orOperator(new Criteria("title").regex("^" + title, "i"));
			if (!DPDoctorUtils.anyStringEmpty(category))
				criteria = criteria.and("category").is(category);
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), Aggregation.skip((page) * size),
						Aggregation.limit(size), Aggregation.sort(Sort.Direction.DESC, "updatedTime"));

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
				if (!DPDoctorUtils.anyStringEmpty(blog.getTitleImage()))
					blog.setTitleImage(imagePath + blog.getTitleImage());
				if (!DPDoctorUtils.anyStringEmpty(userId)) {
					BlogLikesCollection blogLikesCollection = blogLikesRepository
							.findbyBlogIdAndUserId(blogCollection.getId(), new ObjectId(userId));
					if (blogLikesCollection != null)
						blog.setIsliked(!blogLikesCollection.getDiscarded());
				}
				response.add(blog);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return response;
	}

	@Override
	public List<Blog> getMostLikedOrViewedBlogs(int size, int page, String category, String title, String userId,
			Boolean forMostLiked) {
		List<Blog> response = null;
		try {
			response = new ArrayList<Blog>();
			Criteria criteria = new Criteria().and("discarded").is(false);
			Aggregation aggregation = null;

			List<BlogCollection> blogCollections = null;

			if (!DPDoctorUtils.anyStringEmpty(category))
				criteria = criteria.and("category").is(category);
			if (!DPDoctorUtils.anyStringEmpty(title))
				criteria = criteria.orOperator(new Criteria("title").regex("^" + title, "i"));
			if (forMostLiked) {

				if (size > 0) {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.skip((page) * size), Aggregation.limit(size),
							Aggregation.sort(Sort.Direction.DESC, "noOfLikes"));

				} else {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(Sort.Direction.DESC, "noOfLikes"));
				}
			} else {
				if (size > 0) {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.skip((page) * size), Aggregation.limit(size),
							Aggregation.sort(Sort.Direction.DESC, "views"));

				} else {
					aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(Sort.Direction.DESC, "views"));
				}
			}
			AggregationResults<BlogCollection> results = mongoTemplate.aggregate(aggregation, BlogCollection.class,
					BlogCollection.class);
			blogCollections = results.getMappedResults();
			response = new ArrayList<Blog>();
			for (BlogCollection blogCollection : blogCollections) {
				Blog blog = new Blog();
				BeanUtil.map(blogCollection, blog);
				if (!DPDoctorUtils.anyStringEmpty(blog.getTitleImage()))
					blog.setTitleImage(imagePath + blog.getTitleImage());
				if (!DPDoctorUtils.anyStringEmpty(userId)) {
					BlogLikesCollection blogLikesCollection = blogLikesRepository
							.findbyBlogIdAndUserId(blogCollection.getId(), new ObjectId(userId));
					if (blogLikesCollection != null)
						blog.setIsliked(!blogLikesCollection.getDiscarded());
				}
				response.add(blog);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return response;
	}

	@Override
	public long countBlogs(String category, String title) {
		long count = 0;
		Criteria criteria = new Criteria().and("discarded").is(false);
		if (!DPDoctorUtils.anyStringEmpty(title))
			criteria = criteria.orOperator(new Criteria("title").regex("^" + title, "i"));
		if (!DPDoctorUtils.anyStringEmpty(category))
			criteria = criteria.and("category").is(category);
		Query query = new Query();
		query.addCriteria(criteria);
		count = mongoTemplate.count(query, BlogCollection.class);
		return count;
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
	public Blog getBlog(String blogId, String userId) {
		Blog response = null;
		try {
			BlogCollection blogCollection = blogRepository.findOne(new ObjectId(blogId));
			blogCollection.setViews(blogCollection.getViews() + 1);
			blogCollection = blogRepository.save(blogCollection);
			response = new Blog();
			BeanUtil.map(blogCollection, response);
			response.setArticle(this.getBlogArticle(response.getArticleId()));
			if (userId != null) {
				BlogLikesCollection blogLikesCollection = blogLikesRepository
						.findbyBlogIdAndUserId(blogCollection.getId(), new ObjectId(userId));
				response.setIsliked(blogLikesCollection.getDiscarded());
			}
			if (!DPDoctorUtils.anyStringEmpty(response.getTitleImage()))
				response.setTitleImage(imagePath + response.getTitleImage());

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return response;
	}

	@Override
	public Blog updateLikes(String blogId, String userId) {
		Blog response = null;
		try {
			BlogLikesCollection blogLikesCollection = null;
			UserCollection userCollection = userRepository.findOne(new ObjectId(userId));

			BlogCollection blogCollection = blogRepository.findOne(new ObjectId(blogId));
			if (userCollection != null && blogCollection != null) {
				blogLikesCollection = blogLikesRepository.findbyBlogIdAndUserId(new ObjectId(blogId),
						new ObjectId(userId));
				if (blogLikesCollection != null) {
					if (!blogLikesCollection.getDiscarded()) {
						blogCollection.setNoOfLikes(blogCollection.getNoOfLikes() - 1);
						blogLikesCollection.setDiscarded(true);
					} else {
						blogCollection.setNoOfLikes(blogCollection.getNoOfLikes() + 1);
						blogLikesCollection.setDiscarded(false);
					}

				} else {
					blogLikesCollection = new BlogLikesCollection();
					blogLikesCollection.setBlogId(new ObjectId(blogId));
					blogLikesCollection.setUserId(new ObjectId(userId));
					blogLikesCollection.setDiscarded(false);
					blogLikesCollection.setCreatedTime(new Date());
					blogCollection.setNoOfLikes(blogCollection.getNoOfLikes() + 1);
					blogLikesCollection.setDiscarded(false);
				}
				blogLikesCollection.setUpdatedTime(new Date());
				blogLikesCollection = blogLikesRepository.save(blogLikesCollection);
				blogCollection = blogRepository.save(blogCollection);
				response = new Blog();
				BeanUtil.map(blogCollection, response);
				if (!DPDoctorUtils.anyStringEmpty(response.getTitleImage()))
					response.setTitleImage(imagePath + response.getTitleImage());
				response.setIsliked(!blogLikesCollection.getDiscarded());

			} else {
				throw new BusinessException(ServiceError.Unknown, "Invalid user or blog");

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

		return response;
	}

	@Override
	public Boolean addFevouriteBlog(String blogId, String userId) {
		try {
			FavouriteBlogsCollection favouriteBlogsCollection = null;
			UserCollection userCollection = userRepository.findOne(new ObjectId(userId));
			BlogCollection blogCollection = blogRepository.findOne(new ObjectId(blogId));
			if (userCollection != null && blogCollection != null) {
				favouriteBlogsCollection = fevouriteBlogsRepository.findbyBlogIdAndUserId(new ObjectId(blogId),
						new ObjectId(userId));
				if (favouriteBlogsCollection != null) {
					if (!favouriteBlogsCollection.getDiscarded()) {

						favouriteBlogsCollection.setDiscarded(true);
					} else {
						favouriteBlogsCollection.setDiscarded(false);
					}

				} else {
					favouriteBlogsCollection = new FavouriteBlogsCollection();
					favouriteBlogsCollection.setBlogId(new ObjectId(blogId));
					favouriteBlogsCollection.setUserId(new ObjectId(userId));
					favouriteBlogsCollection.setDiscarded(false);
					favouriteBlogsCollection.setCreatedTime(new Date());
					favouriteBlogsCollection.setDiscarded(false);
				}
				favouriteBlogsCollection.setUpdatedTime(new Date());
				favouriteBlogsCollection = fevouriteBlogsRepository.save(favouriteBlogsCollection);
				return !favouriteBlogsCollection.getDiscarded();

			} else {
				throw new BusinessException(ServiceError.Unknown, "Invalid user or blog");

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}

	}

	@Override
	public List<Blog> getFevouriteBlogs(int size, int page, String category, String userId, String title) {
		List<Blog> response = null;
		try {
			Criteria criteria = new Criteria().and("discarded").is(false);
			Aggregation aggregation = null;

			List<BlogCollection> blogCollections = null;

			if (!DPDoctorUtils.anyStringEmpty(userId))
				criteria = criteria.and("fevourite.userId").is(userId);
			if (!DPDoctorUtils.anyStringEmpty(category))
				criteria = criteria.and("category").is(category);
			if (!DPDoctorUtils.anyStringEmpty(title))
				criteria = criteria.orOperator(new Criteria("title").regex("^" + title, "i"));
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("fevourite_Blogs_cl", "_id", "blogId", "fevourite"),
						Aggregation.unwind("fevourite"), Aggregation.match(criteria), Aggregation.skip((page) * size),
						Aggregation.limit(size), Aggregation.sort(Sort.Direction.DESC, "fevourite.updatedTime"));

			} else {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("fevourite_Blogs_cl", "_id", "blogId", "fevourite"),
						Aggregation.match(criteria), Aggregation.unwind("fevourite"),
						Aggregation.sort(Sort.Direction.DESC, "fevourite.updatedTime"));
			}

			AggregationResults<BlogCollection> results = mongoTemplate.aggregate(aggregation, BlogCollection.class,
					BlogCollection.class);
			blogCollections = results.getMappedResults();
			response = new ArrayList<Blog>();
			for (BlogCollection blogCollection : blogCollections) {
				Blog blog = new Blog();
				BeanUtil.map(blogCollection, blog);
				if (!DPDoctorUtils.anyStringEmpty(blog.getTitleImage()))
					blog.setTitleImage(imagePath + blog.getTitleImage());
				if (!DPDoctorUtils.anyStringEmpty(userId)) {
					BlogLikesCollection blogLikesCollection = blogLikesRepository
							.findbyBlogIdAndUserId(blogCollection.getId(), new ObjectId(userId));
					if (blogLikesCollection != null)
						blog.setIsliked(!blogLikesCollection.getDiscarded());
				}
				response.add(blog);

			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}

		return response;

	}

}
