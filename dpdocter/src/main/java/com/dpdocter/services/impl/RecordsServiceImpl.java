package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.Records;
import com.dpdocter.beans.Tags;
import com.dpdocter.collections.RecordsCollection;
import com.dpdocter.collections.RecordsTagsCollection;
import com.dpdocter.collections.TagsCollection;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.RecordsRepository;
import com.dpdocter.repository.RecordsTagsRepository;
import com.dpdocter.repository.TagsRepository;
import com.dpdocter.request.RecordsAddRequest;
import com.dpdocter.request.RecordsSearchRequest;
import com.dpdocter.services.FileManager;
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

	@Override
	public Records addRecord(RecordsAddRequest request,MultipartFile image) {
		try {
			String path = request.getPatientId() + File.separator + "records";
			//save image
			String recordUrl = fileManager.saveImageAndReturnImageUrl(path, image);
			//save records
			RecordsCollection recordsCollection = new RecordsCollection();
			BeanUtil.map(request, recordsCollection);
			recordsCollection.setrecordsUrl(recordUrl);
			recordsCollection.setrecordsLable(removeExtensionFromImageName(image.getOriginalFilename()));
			recordsCollection = recordsRepository.save(recordsCollection);
			Records records = new Records();
			BeanUtil.map(recordsCollection, records);
			return records;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
			
		}

	}
	
	private String removeExtensionFromImageName(String imageName){
		imageName = imageName.substring(0, imageName.lastIndexOf("."));
		return imageName;
	}

	@Override
	public void tagRecord(List<Tags> tags, String recordId) {
		try {
			//save tags
			List<TagsCollection> tagsCollections = null;
			if(tags != null){
				tagsCollections = new ArrayList<TagsCollection>();
				BeanUtil.map(tags, tagsCollections);
				tagsCollections = tagsRepository.save(tagsCollections);
			}
			//save recrds tags map
			List<RecordsTagsCollection> recordsTagsCollections = new ArrayList<RecordsTagsCollection>();
			for(TagsCollection tagsCollection : tagsCollections){
				RecordsTagsCollection recordsTagsCollection = new RecordsTagsCollection();
				recordsTagsCollection.setrecordsId(recordId);
				recordsTagsCollection.setTagsId(tagsCollection.getId());
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
			recordsCollection.setrecordsLable(label);
			recordsRepository.save(recordsCollection);
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
				List<RecordsCollection> recordsCollections = recordsRepository.findRecords(request.getPatientId(), request.getDoctorId());
				records = new ArrayList<Records>();
				BeanUtil.map(recordsCollections, records);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		
		return records;
	}
	
	
	
	

}
