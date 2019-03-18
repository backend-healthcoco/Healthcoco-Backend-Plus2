package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.MedicineOrder;
import com.dpdocter.beans.MedicineOrderAddEditItems;
import com.dpdocter.beans.MedicineOrderItems;
import com.dpdocter.collections.MedicineOrderCollection;
import com.dpdocter.enums.OrderStatus;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.MedicineOrderRepository;
import com.dpdocter.request.MedicinOrderAddEditAddressRequest;
import com.dpdocter.request.MedicineOrderPaymentAddEditRequest;
import com.dpdocter.request.MedicineOrderPreferenceAddEditRequest;
import com.dpdocter.request.MedicineOrderRXAddEditRequest;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.MedicineOrderService;
import com.mongodb.BasicDBObject;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;

import common.util.web.DPDoctorUtils;

@Service
public class MedicineOrderServiceImpl implements MedicineOrderService{

	private static Logger logger = Logger.getLogger(RecordsServiceImpl.class.getName());
	
	@Autowired
	FileManager fileManager;
	
	@Autowired
	private MedicineOrderRepository medicineOrderRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Value(value = "${image.path}")
	private String imagePath;

	
	@Override
	@Transactional
	public ImageURLResponse saveRXMedicineOrderImage(FormDataBodyPart file, String patientIdString) {
		String recordPath = null;
		ImageURLResponse imageURLResponse = null;
		try {

			Date createdTime = new Date();
			if (file != null) {
				String path = "medorderRX" + File.separator + patientIdString;
				FormDataContentDisposition fileDetail = file.getFormDataContentDisposition();
				String fileExtension = FilenameUtils.getExtension(fileDetail.getFileName());
				String fileName = fileDetail.getFileName().replaceFirst("." + fileExtension, "");

				recordPath = path + File.separator + fileName + createdTime.getTime() + fileExtension;
				imageURLResponse = fileManager.saveImage(file, recordPath, true);
				
				/*if(imageURLResponse != null)
				{
					imageURLResponse.setImageUrl(imagePath + imageURLResponse.getImageUrl());
					imageURLResponse.setThumbnailUrl(imagePath + imageURLResponse.getThumbnailUrl()); 
				}*/
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return imageURLResponse;
	}
	
	@Override
	@Transactional
	public MedicineOrder addeditRx(MedicineOrderRXAddEditRequest request) {
		MedicineOrder medicineOrder = null;
		MedicineOrderCollection medicineOrderCollection = null;

		try {

			medicineOrderCollection = medicineOrderRepository.findOne(new ObjectId(request.getId()));

			if (medicineOrderCollection == null) {
				medicineOrderCollection = new MedicineOrderCollection();
				medicineOrderCollection.setUniqueOrderId(UniqueIdInitial.MEDICINE_ORDER.getInitial() + DPDoctorUtils.generateRandomId());
			}

			medicineOrderCollection.setPatientId(new ObjectId(request.getPatientId()));
			medicineOrderCollection.setRxImage(request.getRxImage());
			if (request.getItems() != null) {
				List<MedicineOrderItems> items = new ArrayList<>();
				for (MedicineOrderAddEditItems addEditItems : request.getItems()) {
					MedicineOrderItems orderItems = new MedicineOrderItems();
					BeanUtil.map(addEditItems, orderItems);
					items.add(orderItems);
				}
				medicineOrderCollection.setItems(items);
			}

			medicineOrderCollection = medicineOrderRepository.save(medicineOrderCollection);
			if (medicineOrderCollection != null) {
				medicineOrder = new MedicineOrder();
				BeanUtil.map(medicineOrderCollection, medicineOrder);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return medicineOrder;
	}
	
	@Override
	@Transactional
	public MedicineOrder addeditAddress(MedicinOrderAddEditAddressRequest request) {
		MedicineOrder medicineOrder = null;
		MedicineOrderCollection medicineOrderCollection = null;

		try {

			medicineOrderCollection = medicineOrderRepository.findOne(new ObjectId(request.getId()));

			if (medicineOrderCollection == null) {
				medicineOrderCollection = new MedicineOrderCollection();
				medicineOrderCollection.setUniqueOrderId(UniqueIdInitial.MEDICINE_ORDER.getInitial() + DPDoctorUtils.generateRandomId());
			}

			medicineOrderCollection.setShippingAddress(request.getShippingAddress());
			medicineOrderCollection.setBillingAddress(request.getBillingAddress());
			
			medicineOrderCollection = medicineOrderRepository.save(medicineOrderCollection);
			if (medicineOrderCollection != null) {
				medicineOrder = new MedicineOrder();
				BeanUtil.map(medicineOrderCollection, medicineOrder);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return medicineOrder;
	}
	
	@Override
	@Transactional
	public MedicineOrder addeditPayment(MedicineOrderPaymentAddEditRequest request) {
		MedicineOrder medicineOrder = null;
		MedicineOrderCollection medicineOrderCollection = null;

		try {

			medicineOrderCollection = medicineOrderRepository.findOne(new ObjectId(request.getId()));

			if (medicineOrderCollection == null) {
				medicineOrderCollection = new MedicineOrderCollection();
				medicineOrderCollection.setUniqueOrderId(UniqueIdInitial.MEDICINE_ORDER.getInitial() + DPDoctorUtils.generateRandomId());
			}

			medicineOrderCollection.setTotalAmount(request.getTotalAmount());
			medicineOrderCollection.setDiscountedAmount(request.getDiscountedAmount());
			medicineOrderCollection.setDiscountedPercentage(request.getDiscountedPercentage());
			medicineOrderCollection.setFinalAmount(request.getFinalAmount());
			medicineOrderCollection.setDeliveryCharges(request.getDeliveryCharges());
			medicineOrderCollection.setPaymentMode(request.getPaymentMode());

			medicineOrderCollection = medicineOrderRepository.save(medicineOrderCollection);
			if (medicineOrderCollection != null) {
				medicineOrder = new MedicineOrder();
				BeanUtil.map(medicineOrderCollection, medicineOrder);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return medicineOrder;
	}
	
	@Override
	@Transactional
	public MedicineOrder addeditPreferences(MedicineOrderPreferenceAddEditRequest request) {
		MedicineOrder medicineOrder = null;
		MedicineOrderCollection medicineOrderCollection = null;

		try {

			medicineOrderCollection = medicineOrderRepository.findOne(new ObjectId(request.getId()));

			if (medicineOrderCollection == null) {
				medicineOrderCollection = new MedicineOrderCollection();
				medicineOrderCollection.setUniqueOrderId(UniqueIdInitial.MEDICINE_ORDER.getInitial() + DPDoctorUtils.generateRandomId());
			}

			medicineOrderCollection.setDeliveryPreference(request.getDeliveryPreference());
			medicineOrderCollection.setNextDeliveryDate(request.getNextDeliveryDate());

			medicineOrderCollection = medicineOrderRepository.save(medicineOrderCollection);
			if (medicineOrderCollection != null) {
				medicineOrder = new MedicineOrder();
				BeanUtil.map(medicineOrderCollection, medicineOrder);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return medicineOrder;
	}
	
	@Override
	@Transactional
	public MedicineOrder updateStatus(String id, OrderStatus status ) {
		MedicineOrder medicineOrder = null;
		MedicineOrderCollection medicineOrderCollection = null;

		try {

			medicineOrderCollection = medicineOrderRepository.findOne(new ObjectId(id));

			if (medicineOrderCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}

			medicineOrderCollection.setOrderStatus(status);

			medicineOrderCollection = medicineOrderRepository.save(medicineOrderCollection);
			if (medicineOrderCollection != null) {
				medicineOrder = new MedicineOrder();
				BeanUtil.map(medicineOrderCollection, medicineOrder);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return medicineOrder;
	}
	
	@Override
	@Transactional
	public MedicineOrder getOrderById(String id ) {
		MedicineOrder medicineOrder = null;
		MedicineOrderCollection medicineOrderCollection = null;

		try {

			medicineOrderCollection = medicineOrderRepository.findOne(new ObjectId(id));

			if (medicineOrderCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}

			medicineOrderCollection = medicineOrderRepository.save(medicineOrderCollection);
			if (medicineOrderCollection != null) {
				medicineOrder = new MedicineOrder();
				BeanUtil.map(medicineOrderCollection, medicineOrder);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return medicineOrder;
	}
	
	
	@Override
	@Transactional
	public Boolean discardOrder(String id, Boolean discarded ) {
		Boolean status = false;
		MedicineOrderCollection medicineOrderCollection = null;

		try {

			medicineOrderCollection = medicineOrderRepository.findOne(new ObjectId(id));

			if (medicineOrderCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}

			medicineOrderCollection.setDiscarded(discarded);
			medicineOrderCollection = medicineOrderRepository.save(medicineOrderCollection);
			status = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return status;
	}
	
	
	
	@Override
	@Transactional
	public List<MedicineOrder> getOrderList(String patientId , String updatedTime , String searchTerm, int page , int size) {
		List<MedicineOrder> orders = null;
		Aggregation aggregation =null;
		try {
			//Criteria criteria = new Criteria();
			
			long createdTimestamp = Long.parseLong(updatedTime);
			
			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));
			
			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria.and("patientId").is(new ObjectId(patientId));
			}
			
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("longName").regex("^" + searchTerm, "i"));
			}
			
			if (size > 0) {
				aggregation =Aggregation.newAggregation(
							Aggregation.lookup("vendor_cl", "vendorId", "_id", "vendor"),
							new CustomAggregationOperation(new BasicDBObject("$unwind",
									new BasicDBObject("path", "$vendor").append("preserveNullAndEmptyArrays",
											true))),
							Aggregation.match(criteria), Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
						 Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(
							Aggregation.lookup("vendor_cl", "vendorId", "_id", "vendor"),
							new CustomAggregationOperation(new BasicDBObject("$unwind",
									new BasicDBObject("path", "$vendor").append("preserveNullAndEmptyArrays",
											true))),
							Aggregation.match(criteria), Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			
			
			orders = mongoTemplate.aggregate(
					aggregation,
					MedicineOrderCollection.class, MedicineOrder.class).getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return orders;
	}
	
	
	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}
	
}
