package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.MyVideo;
import com.dpdocter.beans.Video;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.MyVideoCollection;
import com.dpdocter.collections.SpecialityCollection;
import com.dpdocter.collections.VideoCollection;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.MyVideoRepository;
import com.dpdocter.repository.SpecialityRepository;
import com.dpdocter.repository.VideoRepository;
import com.dpdocter.request.AddMyVideoRequest;
import com.dpdocter.request.AddVideoRequest;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.VideoService;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.DPDoctorUtils;

@Service
public class VideoServiceImpl implements VideoService {

	@Autowired
	private VideoRepository videoRepository;
	
	@Autowired
	private MyVideoRepository myVideoRepository;
	
	

	@Autowired
	private FileManager fileManager;

	@Autowired
	private DoctorRepository doctorRepository;

	@Autowired
	private SpecialityRepository specialityRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	
	@Override
	@Transactional
	public Video addVideo(FormDataBodyPart file, AddVideoRequest request) {
		Video response = null;
		VideoCollection videoCollection = null;
		ImageURLResponse imageURLResponse = null;
		try {
			if (file != null) {
				String path = "video" + File.separator + request.getSpeciality();
				FormDataContentDisposition fileDetail = file.getFormDataContentDisposition();
				String fileExtension = FilenameUtils.getExtension(fileDetail.getFileName());
				String fileName = fileDetail.getFileName().replaceFirst("." + fileExtension, "");
				String recordPath = path + File.separator + fileName + System.currentTimeMillis() + "." + fileExtension;
				imageURLResponse = fileManager.saveImage(file, recordPath, false);
			}
			if (imageURLResponse != null) {
				videoCollection = new VideoCollection();
				BeanUtil.map(request, videoCollection);
				videoCollection.setVideoUrl(imageURLResponse.getImageUrl());
				videoCollection.setCreatedTime(new Date());
			}
			videoCollection = videoRepository.save(videoCollection);
			response = new Video();
			BeanUtil.map(videoCollection, response);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}
	

	@Override
	@Transactional
	public MyVideo addMyVideo(FormDataBodyPart file, AddMyVideoRequest request) {
		MyVideo response = null;
		MyVideoCollection myVideoCollection = null;
		ImageURLResponse imageURLResponse = null;
		try {
			if (file != null) {
				String path = "video" + File.separator + request.getDoctorId();
				FormDataContentDisposition fileDetail = file.getFormDataContentDisposition();
				String fileExtension = FilenameUtils.getExtension(fileDetail.getFileName());
				String fileName = fileDetail.getFileName().replaceFirst("." + fileExtension, "");
				String recordPath = path + File.separator + fileName + System.currentTimeMillis() + "." + fileExtension;
				imageURLResponse = fileManager.saveImage(file, recordPath, false);
			}
			if (imageURLResponse != null) {
				myVideoCollection = new MyVideoCollection();
				BeanUtil.map(request, myVideoCollection);
				myVideoCollection.setVideoUrl(imageURLResponse.getImageUrl());
				myVideoCollection.setCreatedTime(new Date());
			}
			myVideoCollection = myVideoRepository.save(myVideoCollection);
			response = new MyVideo();
			BeanUtil.map(myVideoCollection, response);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}

	@Override
	@Transactional
	public List<Video> getVideos(String doctorId, String searchTerm, List<String> tags, int page, int size) {
		Aggregation aggregation = null;
		List<String> specialities = null;
		List<Video> response = null;
		try {
			DoctorCollection doctorCollection = doctorRepository.findByUserId(new ObjectId(doctorId));
			if (doctorCollection != null) {
				String speciality = null;

				if (doctorCollection.getSpecialities() != null || !doctorCollection.getSpecialities().isEmpty()) {
					specialities = new ArrayList<>();
					for (ObjectId specialityId : doctorCollection.getSpecialities()) {
						SpecialityCollection specialityCollection = specialityRepository.findById(specialityId).orElse(null);
						if (specialityCollection != null) {
							speciality = specialityCollection.getSpeciality();
							specialities.add(speciality);
						}
					}
				}
			}
			
			Criteria criteria =  new Criteria().and("speciality").in(specialities);
			
			if(tags != null && !tags.isEmpty())
			{
				criteria.and("tags").in(tags);
			}
			
			aggregation = Aggregation.newAggregation(
					Aggregation.match(criteria)
					, Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<Video> aggregationResults = mongoTemplate.aggregate(aggregation, VideoCollection.class,
					Video.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}
	
	@Override
	@Transactional
	public List<MyVideo> getMyVideos(String doctorId, String searchTerm, int page, int size) {
		Aggregation aggregation = null;
		List<MyVideo> response = null;
		try {
			
			aggregation = Aggregation.newAggregation(
					Aggregation.match(new Criteria().and("doctorId").in(new ObjectId(doctorId)))
					, Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<MyVideo> aggregationResults = mongoTemplate.aggregate(aggregation, MyVideoCollection.class,
					MyVideo.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}
	
	@Override
	@Transactional
	public List<Video> getLocationVideos(String doctorId ,String locationId, String hospitalId, String searchTerm, int page, int size , List<String> tags) {
		Aggregation aggregation = null;
		List<Video> response = null;
		try {
			Criteria criteria = new Criteria();
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				criteria.and("doctorId").in(new ObjectId(doctorId));
			}
			if (!DPDoctorUtils.anyStringEmpty(locationId)) {
				criteria.and("locationId").in(new ObjectId(locationId));
			}
			if (!DPDoctorUtils.anyStringEmpty(hospitalId)) {
				criteria.and("hospitalId").in(new ObjectId(hospitalId));
			}
			if(tags != null)
			{
				criteria.and("tags").in(tags);
			}
			aggregation = Aggregation.newAggregation(
					Aggregation.match(criteria)
					, Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")));
			AggregationResults<Video> aggregationResults = mongoTemplate.aggregate(aggregation, VideoCollection.class,
					Video.class);
			response = aggregationResults.getMappedResults();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}
}