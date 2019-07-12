package com.dpdocter.services.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.bson.Document;
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
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.collections.BlogCollection;
import com.dpdocter.collections.BlogLikesCollection;
import com.dpdocter.collections.FavouriteBlogsCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.BlogCategoryType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.BlogLikesRepository;
import com.dpdocter.repository.BlogRepository;
import com.dpdocter.repository.FevouriteBlogsRepository;
import com.dpdocter.repository.GridFsRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.BlogCategoryWithPageSize;
import com.dpdocter.request.BlogRequest;
import com.dpdocter.response.BlogResponse;
import com.dpdocter.services.BlogService;
import com.mongodb.BasicDBObject;
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
	public BlogResponse getBlogs(int size, long page, String category, String userId, String title) {
		BlogResponse response = new BlogResponse();

		List<Blog> listblog = null;

		try {

			Criteria criteria = new Criteria().and("discarded").is(false);
			Aggregation aggregation;
			List<BlogCollection> blogCollections;

			if (!DPDoctorUtils.anyStringEmpty(title)) {
				criteria = criteria.orOperator(new Criteria("title").regex("^" + title, "i"));
			}
			if (!DPDoctorUtils.anyStringEmpty(category)) {
				criteria = criteria.and("category").is(category);
			}
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"), Aggregation.skip((page) * size),
						Aggregation.limit(size));

			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "createdTime"));
			}
			AggregationResults<BlogCollection> results = mongoTemplate.aggregate(aggregation, BlogCollection.class,
					BlogCollection.class);
			blogCollections = results.getMappedResults();
			listblog = new ArrayList<Blog>();
			for (BlogCollection blogCollection : blogCollections) {
				Blog blog = new Blog();
				BeanUtil.map(blogCollection, blog);

				if (!DPDoctorUtils.anyStringEmpty(blog.getTitleImage()))
					blog.setTitleImage(imagePath + blog.getTitleImage());
				if (!DPDoctorUtils.anyStringEmpty(userId)) {

					BlogLikesCollection blogLikesCollection = blogLikesRepository
							.findbyBlogIdAndUserId(blogCollection.getId(), new ObjectId(userId));
					FavouriteBlogsCollection favouriteBlogsCollection = fevouriteBlogsRepository
							.findbyBlogIdAndUserId(blogCollection.getId(), new ObjectId(userId));
					if (favouriteBlogsCollection != null) {
						blog.setIsFavourite(!favouriteBlogsCollection.getDiscarded());
					}

					if (blogLikesCollection != null) {
						blog.setIsliked(!blogLikesCollection.getDiscarded());
					}
				}
				listblog.add(blog);

			}
			response.setBlogs(listblog);

			response.setTotalsize((int) mongoTemplate.count(new Query(criteria), BlogCollection.class));
		} catch (

		Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());

		}
		return response;
	}

	@Override
	public List<Blog> getMostLikedOrViewedBlogs(int size, long page, String category, String title, String userId,
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
					FavouriteBlogsCollection favouriteBlogsCollection = fevouriteBlogsRepository
							.findbyBlogIdAndUserId(blogCollection.getId(), new ObjectId(userId));
					if (favouriteBlogsCollection != null) {
						blog.setIsFavourite(!favouriteBlogsCollection.getDiscarded());
					}
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
	public Blog getBlog(String blogId, String slugUrl, String userId) {
		Blog response = null;
		try {
			BlogCollection blogCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(slugUrl)) {
				blogCollection = blogRepository.findBySlugURL(slugUrl);
			} else {
				blogCollection = blogRepository.findById(new ObjectId(blogId)).orElse(null);
			}
			if (blogCollection != null) {
				blogCollection.setViews(blogCollection.getViews() + 1);
				blogCollection = blogRepository.save(blogCollection);
				response = new Blog();
				BeanUtil.map(blogCollection, response);
				response.setArticle(this.getBlogArticle(response.getArticleId()));
				if (!DPDoctorUtils.anyStringEmpty(userId)) {
					FavouriteBlogsCollection favouriteBlogsCollection = fevouriteBlogsRepository
							.findbyBlogIdAndUserId(blogCollection.getId(), new ObjectId(userId));
					if (favouriteBlogsCollection != null) {
						response.setIsFavourite(!favouriteBlogsCollection.getDiscarded());
					}
					BlogLikesCollection blogLikesCollection = blogLikesRepository
							.findbyBlogIdAndUserId(blogCollection.getId(), new ObjectId(userId));

					if (blogLikesCollection != null) {
						response.setIsliked(!blogLikesCollection.getDiscarded());

					}
				}
				if (!DPDoctorUtils.anyStringEmpty(response.getTitleImage()))
					response.setTitleImage(imagePath + response.getTitleImage());
			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid slug url");
			}
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
			UserCollection userCollection = userRepository.findById(new ObjectId(userId)).orElse(null);

			BlogCollection blogCollection = blogRepository.findById(new ObjectId(blogId)).orElse(null);
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
				FavouriteBlogsCollection favouriteBlogsCollection = fevouriteBlogsRepository
						.findbyBlogIdAndUserId(blogCollection.getId(), new ObjectId(userId));
				if (favouriteBlogsCollection != null) {
					response.setIsFavourite(!favouriteBlogsCollection.getDiscarded());
				}
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
			UserCollection userCollection = userRepository.findById(new ObjectId(userId)).orElse(null);
			BlogCollection blogCollection = blogRepository.findById(new ObjectId(blogId)).orElse(null);
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
	public List<Blog> getFevouriteBlogs(int size, long page, String category, String userId, String title) {
		List<Blog> response = null;
		try {
			Criteria criteria = new Criteria().and("fevourite.discarded").is(false);
			Aggregation aggregation = null;

			List<BlogCollection> blogCollections = null;

			if (!DPDoctorUtils.anyStringEmpty(userId))
				criteria = criteria.and("fevourite.userId").is(new ObjectId(userId));
			if (!DPDoctorUtils.anyStringEmpty(category))
				criteria = criteria.and("category").is(category);
			if (!DPDoctorUtils.anyStringEmpty(title))
				criteria = criteria.orOperator(new Criteria("title").regex("^" + title, "i"));
			if (size > 0) {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("fevourite_Blogs_cl", "_id", "blogId", "fevourite"),
						Aggregation.unwind("fevourite"), Aggregation.match(criteria), Aggregation.skip((page) * size),
						Aggregation.limit(size), Aggregation.sort(Sort.Direction.DESC, "fevourite.createdTime"));

			} else {
				aggregation = Aggregation.newAggregation(
						Aggregation.lookup("fevourite_Blogs_cl", "_id", "blogId", "fevourite"),
						Aggregation.unwind("fevourite"), Aggregation.match(criteria),
						Aggregation.sort(Sort.Direction.DESC, "fevourite.createdTime"));
			}

			AggregationResults<BlogCollection> results = mongoTemplate.aggregate(aggregation, "blog_cl",
					BlogCollection.class);
			blogCollections = results.getMappedResults();
			response = new ArrayList<Blog>();
			for (BlogCollection blogCollection : blogCollections) {
				Blog blog = new Blog();
				blog.setIsFavourite(true);
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

	public BlogCategoryType[] getBlogCategory() {

		return BlogCategoryType.values();

	}

	@Override
	public List<BlogResponse> getBlogs(BlogRequest request) {
		List<BlogResponse> response = null;
		try {
			
			CustomAggregationOperation projectOperation = new CustomAggregationOperation(
					new Document("$project", new BasicDBObject("title","$title")
							.append("titleImage", new BasicDBObject("$cond", 
									new BasicDBObject("if", new BasicDBObject("eq", Arrays.asList("$titleImage", null)))
									          .append("then", new BasicDBObject("$concat", Arrays.asList(imagePath, "$titleImage")))
									          .append("else", null)))
							.append("superCategory","$superCategory").append("category","$category")
							.append("articleId","$articleId").append("isActive","$isActive")
							.append("article","$article").append("noOfLikes","$noOfLikes")
							.append("isliked",true).append("isFavourite",true)
							.append("views","$views")
							.append("postBy","$postBy").append("discarded","$discarded")
							.append("shortDesc","$shortDesc")
							.append("metaKeyword","$metaKeyword").append("slugURL","$slugURL")
							.append("adminCreatedTime","$adminCreatedTime").append("createdTime","$createdTime")
							.append("updatedTime","$updatedTime").append("createdBy","$createdBy")
							.append("updatedTime","$updatedTime").append("createdBy","$createdBy")));
										
			
			CustomAggregationOperation groupOperation = new CustomAggregationOperation(
					new Document("$group", new BasicDBObject("id", "$_id")
							.append("title", new BasicDBObject("$first","$title"))
							.append("titleImage", new BasicDBObject("$first","$titleImage"))
							.append("superCategory", new BasicDBObject("$first","$superCategory"))
							.append("category", new BasicDBObject("$first","$category"))
							.append("articleId", new BasicDBObject("$first","$articleId"))
							.append("isActive", new BasicDBObject("$first","$isActive"))
							.append("article", new BasicDBObject("$first","$article"))
							.append("noOfLikes", new BasicDBObject("$first","$noOfLikes"))
							.append("views", new BasicDBObject("$first","$views"))
							.append("postBy", new BasicDBObject("$first","$postBy"))
							.append("discarded", new BasicDBObject("$first","$discarded"))
							.append("shortDesc",new BasicDBObject("$first", "$shortDesc"))
							.append("metaKeyword",new BasicDBObject("$first","$metaKeyword"))
							.append("slugURL",new BasicDBObject("$first", "$slugURL"))
							.append("adminCreatedTime",new BasicDBObject("$first", "$adminCreatedTime"))
							.append("createdTime", new BasicDBObject("$first","$createdTime"))
							.append("updatedTime", new BasicDBObject("$first","$updatedTime"))
							.append("createdBy", new BasicDBObject("$first","$createdBy"))));
			
			if(request.getBlogSuperCategories() != null && !request.getBlogSuperCategories().isEmpty()) {
				Aggregation aggregation = null;
				
				for(BlogCategoryWithPageSize blogCategoryWithPageSize : request.getBlogSuperCategories()) {
					
					Criteria criteria = new Criteria("discarded").is(false);

					if (!DPDoctorUtils.anyStringEmpty(request.getTitle())) {
						criteria = criteria.orOperator(new Criteria("title").regex("^" + request.getTitle(), "i"));
					}
					if (!DPDoctorUtils.anyStringEmpty(request.getCategory())) {
						criteria = criteria.and("category").is(request.getCategory());
					}
					criteria = criteria.and("superCategory").is(blogCategoryWithPageSize.getSuperCategory().getType());
					if(blogCategoryWithPageSize.getSize() > 0) {
						
						aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("blog_likes_cl", "_id", "blogId", "blogLikesCollection"),
								projectOperation, groupOperation,
								Aggregation.sort(Sort.Direction.DESC, "createdTime"), 
								Aggregation.skip((blogCategoryWithPageSize.getPage()) * blogCategoryWithPageSize.getSize()),
								Aggregation.limit(blogCategoryWithPageSize.getSize()));
					} else {
						aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
								projectOperation, groupOperation, Aggregation.sort(Sort.Direction.DESC, "createdTime"));
					}
					List<Blog> blogs = mongoTemplate.aggregate(aggregation, BlogCollection.class, Blog.class)
							.getMappedResults();
					if (blogs != null && !blogs.isEmpty()) {
						if (response == null)response = new ArrayList<BlogResponse>();

						for(Blog blog : blogs) {
							/*if (!DPDoctorUtils.anyStringEmpty(blog.getTitleImage()))
								blog.setTitleImage(imagePath + blog.getTitleImage());*/
							if (!DPDoctorUtils.anyStringEmpty(request.getUserId())) {
								BlogLikesCollection blogLikesCollection = blogLikesRepository.findbyBlogIdAndUserId(new ObjectId(blog.getId()), new ObjectId(request.getUserId()));
								if (blogLikesCollection != null) {
									blog.setIsliked(!blogLikesCollection.getDiscarded());
								}
								
								FavouriteBlogsCollection favouriteBlogsCollection = fevouriteBlogsRepository.findbyBlogIdAndUserId(new ObjectId(blog.getId()), new ObjectId(request.getUserId()));
								if (favouriteBlogsCollection != null) {
									blog.setIsFavourite(!favouriteBlogsCollection.getDiscarded());
								}	
							}
						}
						BlogResponse blogResponse = new BlogResponse();
						blogResponse.setBlogs(blogs);
						blogResponse.setSuperCategory(blogCategoryWithPageSize.getSuperCategory());
						response.add(blogResponse);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error While Getting Blogs" + e.getMessage());
			throw new BusinessException(ServiceError.Unknown, "Error While Getting Blogs" + e.getMessage());

		}
		return response;
	}

}
