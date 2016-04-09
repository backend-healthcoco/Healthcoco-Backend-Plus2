package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.Hospital;
import com.dpdocter.beans.Location;
import com.dpdocter.beans.Resume;
import com.dpdocter.beans.User;
import com.dpdocter.collections.HospitalCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.ResumeCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.HospitalRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.ResumeRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.services.AdminServices;
import com.dpdocter.services.FileManager;

import common.util.web.DPDoctorUtils;

@Service
public class AdminServicesImpl implements AdminServices {

	private static Logger logger = Logger.getLogger(AdminServicesImpl.class.getName());
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	HospitalRepository hospitalRepository;
	
	@Autowired
	LocationRepository locationRepository;
	
	@Autowired
	ResumeRepository resumeRepository;
	
	@Autowired
    private FileManager fileManager;

	@Override
	public List<User> getInactiveUsers(int page, int size) {
		List<User> response = null;
		try{
			List<UserCollection> userCollections = null;
			if(size > 0)userCollections = userRepository.findInactiveDoctors(true, new PageRequest(page, size, Direction.DESC, "createdTime"));
			else userCollections = userRepository.findInactiveDoctors(true, new Sort(Direction.DESC, "createdTime"));
			if(userCollections != null){
				response = new ArrayList<User>();
				BeanUtil.map(userCollections, response);
			}
		}catch(Exception e){
			logger.error("Error while getting inactive users "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while getting inactive users "+ e.getMessage());
		}
		return response;
	}

	@Override
	public List<Hospital> getHospitals(int page, int size) {
		List<Hospital> response = null;
		try{
			List<HospitalCollection> hospitalCollections = null;
			if(size > 0)hospitalCollections = hospitalRepository.findAll(new PageRequest(page, size, Direction.DESC, "createdTime")).getContent();
			else hospitalCollections = hospitalRepository.findAll(new Sort(Direction.DESC, "createdTime"));
			if(hospitalCollections != null){
				response = new ArrayList<Hospital>();
				BeanUtil.map(hospitalCollections, response);
			}
		}catch(Exception e){
			logger.error("Error while getting hospitals "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while getting inactive hospitals "+ e.getMessage());
		}
		return response;
	}

	@Override
	public List<Location> getClinics(int page, int size, String hospitalId) {
		List<Location> response = null;
		try{
			List<LocationCollection> locationCollections = null;
			if(DPDoctorUtils.anyStringEmpty(hospitalId)){
				if(size > 0)locationCollections = locationRepository.findAll(new PageRequest(page, size, Direction.DESC, "createdTime")).getContent();
				else locationCollections = locationRepository.findAll(new Sort(Direction.DESC, "createdTime"));
			}else{
				if(size > 0)locationCollections = locationRepository.find(hospitalId, new PageRequest(page, size, Direction.DESC, "createdTime"));
				else locationCollections = locationRepository.find(hospitalId, new Sort(Direction.DESC, "createdTime"));
			}
			if(locationCollections != null){
				response = new ArrayList<Location>();
				BeanUtil.map(locationCollections, response);
			}
		}catch(Exception e){
			logger.error("Error while getting clinics "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while getting inactive clinics "+ e.getMessage());
		}
		return response;
	}

	@Override
	public Resume addResumes(Resume request) {
		Resume response = null;
		try{
			ResumeCollection resumeCollection = new ResumeCollection();
			BeanUtil.map(request, resumeCollection);
			resumeCollection.setCreatedTime(new Date());
			if (request.getFileDetails() != null) {
				request.getFileDetails().setFileName(request.getFileDetails().getFileName() + (new Date()).getTime());
				String path = "resume" + File.separator + request.getType();
				// save image
				String resumeUrl = fileManager.saveImageAndReturnImageUrl(request.getFileDetails(), path);
				resumeCollection.setPath(resumeUrl);
			    }
			resumeCollection = resumeRepository.save(resumeCollection);
			if(resumeCollection != null){
				response = new Resume();
				BeanUtil.map(resumeCollection, response);
			}
		}catch(Exception e){
			logger.error("Error while adding resume "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while adding resume "+ e.getMessage());
		}
		return response;
	}

	@Override
	public List<Resume> getResumes(int page, int size, String type) {
		List<Resume> response = null;
		try{
			List<ResumeCollection> resumeCollections = null;
			if(DPDoctorUtils.anyStringEmpty(type)){
				if(size > 0)resumeCollections = resumeRepository.findAll(new PageRequest(page, size, Direction.DESC, "createdTime")).getContent();
				else resumeCollections = resumeRepository.findAll(new Sort(Direction.DESC, "createdTime"));
			}else{
				if(size > 0)resumeCollections = resumeRepository.find(type, new PageRequest(page, size, Direction.DESC, "createdTime"));
				else resumeCollections = resumeRepository.find(type, new Sort(Direction.DESC, "createdTime"));
			}
			if(resumeCollections != null){
				response = new ArrayList<Resume>();
				BeanUtil.map(resumeCollections, response);
			}
		}catch(Exception e){
			logger.error("Error while getting clinics "+ e.getMessage());
			e.printStackTrace();
		    throw new BusinessException(ServiceError.Unknown,"Error while getting inactive clinics "+ e.getMessage());
		}
		return response;
	}
}
