package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.Records;
import com.dpdocter.beans.Tags;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.RecordsCollection;
import com.dpdocter.collections.RecordsTagsCollection;
import com.dpdocter.collections.TagsCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.RecordsRepository;
import com.dpdocter.repository.RecordsTagsRepository;
import com.dpdocter.repository.TagsRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.RecordsAddRequest;
import com.dpdocter.request.RecordsSearchRequest;
import com.dpdocter.request.TagRecordRequest;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.MailService;
import com.dpdocter.services.RecordsService;
@Service
public class RecordsServiceImpl implements RecordsService {
	@Autowired
	private FileManager fileManager;
	
	@Autowired
	private RecordsRepository recordsRepository;
	
	@Autowired
	private TagsRepository tagsRepository;
	
	@Autowired
	private RecordsTagsRepository recordsTagsRepository;
	
	@Autowired
	private PatientRepository patientRepository;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private UserRepository userRepository;


	@Value(value = "${IMAGE_RESOURCE}")
	private String imageResource;

	@Override
	public Records addRecord(RecordsAddRequest request) {
		try {
			String path = request.getPatientId() + File.separator + "records";
			//save image
			String recordUrl = fileManager.saveImageAndReturnImageUrl(request.getFileDetails(),path);
			String fileName = request.getFileDetails().getFileName()
					+ "." + request.getFileDetails().getFileExtension();
			String recordPath = imageResource + File.separator + path + File.separator + fileName;
			
			//save records
			RecordsCollection recordsCollection = new RecordsCollection();
			BeanUtil.map(request, recordsCollection);
			recordsCollection.setRecordsUrl(recordUrl);
			recordsCollection.setRecordsPath(recordPath);
			recordsCollection.setRecordsLable(getFileNameFromImageURL(recordUrl));
			recordsCollection = recordsRepository.save(recordsCollection);
			Records records = new Records();
			BeanUtil.map(recordsCollection, records);
			return records;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
			
		}

	}
	
	@Override
	public void emailRecordToPatient(String recordId,String emailAddr) {
		try {
			RecordsCollection recordsCollection = recordsRepository.findOne(recordId);
			if(recordsCollection != null){
					FileSystemResource file = new FileSystemResource(recordsCollection.getRecordsPath());
					MailAttachment mailAttachment = new MailAttachment();
					mailAttachment.setAttachmentName(recordsCollection.getRecordsLable());
					mailAttachment.setFileSystemResource(file);
					mailService.sendEmail(emailAddr, "Records", "PFA.", mailAttachment);
				
			}else{
				throw new BusinessException(ServiceError.Unknown,"Record not found.Please check recordId.");
			}
			
		} catch (BusinessException e) {
			throw new BusinessException(ServiceError.Unknown,e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		
	}
	
	private String getFileNameFromImageURL(String url){
		String arr [] = url.split("/");
		String imageName =  arr[arr.length - 1];
		imageName = imageName.substring(0, imageName.lastIndexOf("."));
		return imageName;
	}

	@Override
	public void tagRecord(TagRecordRequest request) {
		try {
		/*	//save tags
			List<TagsCollection> tagsCollections = null;
			if(request.getTags() != null){
				tagsCollections = new ArrayList<TagsCollection>();
				for(Tags tag : request.getTags()){
				TagsCollection tagsCollection =new TagsCollection();
				BeanUtil.map(tag, tagsCollection);
				tagsCollection = tagsRepository.save(tagsCollection);
				tagsCollections.add(tagsCollection);
			}*/
			//save recrds tags map
			List<RecordsTagsCollection> recordsTagsCollections = new ArrayList<RecordsTagsCollection>();
			for(String tagId : request.getTags()){
				RecordsTagsCollection recordsTagsCollection = new RecordsTagsCollection();
				recordsTagsCollection.setrecordsId(request.getRecordId());
				recordsTagsCollection.setTagsId(tagId);
				recordsTagsCollections.add(recordsTagsCollection);
			}
			recordsTagsRepository.save(recordsTagsCollections);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		
		
	}

	@Override
	public void changeReportLabel(String recordId,String label) {
		try {
			RecordsCollection recordsCollection = recordsRepository.findOne(recordId);
			if(recordsCollection == null){
				throw new BusinessException(ServiceError.Unknown, "Record not found.Check RecordId !");
			}
			recordsCollection.setRecordsLable(label);
			recordsRepository.save(recordsCollection);
		}catch (BusinessException e) {
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		
		
	}

	@Override
	public List<Records> searchRecords(RecordsSearchRequest request) {
		List<Records> records = null;
		try {
			if(request.getTagId() != null){
				List<RecordsTagsCollection> recordsTagsCollections = recordsTagsRepository.findByTagsId(request.getTagId());
				@SuppressWarnings("unchecked")
				Collection<String> recordIds =  CollectionUtils.collect(recordsTagsCollections, new BeanToPropertyValueTransformer("recordsId")); 
				@SuppressWarnings("unchecked")
				List<RecordsCollection> recordsCollections = IteratorUtils.toList(recordsRepository.findAll(recordIds).iterator());
				records = new ArrayList<Records>();
				BeanUtil.map(recordsCollections, records);
			}else{
				List<RecordsCollection> recordsCollections = recordsRepository.findRecords(request.getDoctorId(),request.getLocationId(),request.getHospitalId(),false);
				records = new ArrayList<Records>();
				BeanUtil.map(recordsCollections, records);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		
		return records;
	}

	@Override
	public Tags addEditTag(Tags tags) {
		try {
			TagsCollection tagsCollection = new TagsCollection();
			BeanUtil.map(tags, tagsCollection);
			tagsCollection = tagsRepository.save(tagsCollection);
			BeanUtil.map(tagsCollection, tags);
			return tags;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	public List<Tags> getAllTags(String doctorId, String locationId,
			String hospitalId) {
		List<Tags> tags = null;
		try {
			
			List<TagsCollection> tagsCollections = null;
			if(doctorId != null && locationId != null && hospitalId != null){
				tagsCollections = new ArrayList<TagsCollection>();
				tags = new ArrayList<Tags>();
				tagsCollections = tagsRepository.findByDoctorIdAndlocationIdAndHospitalId(doctorId, locationId, hospitalId);
				BeanUtil.map(tagsCollections, tags);
			}else if(doctorId != null && locationId != null && hospitalId == null){
				tagsCollections = new ArrayList<TagsCollection>();
				tags = new ArrayList<Tags>();
				tagsCollections = tagsRepository.findByDoctorIdAndlocationId(doctorId, locationId);
				BeanUtil.map(tagsCollections, tags);
			}else if(doctorId != null && locationId == null && hospitalId == null){
				tagsCollections = new ArrayList<TagsCollection>();
				tags = new ArrayList<Tags>();
				tagsCollections = tagsRepository.findByDoctorId(doctorId);
				BeanUtil.map(tagsCollections, tags);
			}else{
				throw new BusinessException(ServiceError.Unknown, "Invalid Input !");
			}
		} catch (BusinessException e) {
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return tags;
	}

	@Override
	public String getPatientEmailAddress(String patientId) {
		String emailAddress = null;
		try {
				PatientCollection patientCollection = 
						patientRepository.findByUserId(patientId);
				if(patientCollection != null){
					emailAddress = patientCollection.getEmailAddress();
				}else{
					throw new BusinessException(ServiceError.Unknown, "Invalid PatientId");
				}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return emailAddress;
	}

	@Override
	public File getRecordFile(String recordId) {
		try {
			RecordsCollection recordsCollection = recordsRepository.findOne(recordId);
			if(recordsCollection != null){
				return new File(recordsCollection.getRecordsPath());
			}else{
				throw new BusinessException(ServiceError.Unknown,"Record not found.Please check recordId.");
			}
			
		} catch (BusinessException e) {
			throw new BusinessException(ServiceError.Unknown,e.getMessage());
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	public void deleteRecord(String recordId) {
		try {
			RecordsCollection recordsCollection = recordsRepository.findOne(recordId);
			if(recordsCollection == null){
				throw new BusinessException(ServiceError.Unknown,"Record Not found.Check RecordId");
			}
			recordsCollection.setDeleted(true);
			recordsRepository.save(recordsCollection);
		}catch (BusinessException e) {
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		} 
		catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		
		
	}

	@Override
	public void deleteTag(String tagId) {
		try {
			tagsRepository.delete(tagId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		
	}

}
