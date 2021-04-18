package com.dpdocter.services.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
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
import org.springframework.web.multipart.MultipartFile;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DrugInfo;
import com.dpdocter.beans.MedicineOrder;
import com.dpdocter.beans.MedicineOrderAddEditItems;
import com.dpdocter.beans.MedicineOrderItems;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.beans.TrackingOrder;
import com.dpdocter.beans.UserCart;
import com.dpdocter.collections.DrugInfoCollection;
import com.dpdocter.collections.MedicineOrderCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.TrackingOrderCollection;
import com.dpdocter.collections.UserCartCollection;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.OrderStatus;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DrugInfoRepository;
import com.dpdocter.repository.MedicineOrderRepository;
import com.dpdocter.repository.TrackingOrderRepository;
import com.dpdocter.repository.UserCartRepository;
import com.dpdocter.request.DrugCodeListRequest;
import com.dpdocter.request.MedicineOrderAddEditAddressRequest;
import com.dpdocter.request.MedicineOrderPaymentAddEditRequest;
import com.dpdocter.request.MedicineOrderPreferenceAddEditRequest;
import com.dpdocter.request.MedicineOrderRXAddEditRequest;
import com.dpdocter.request.MedicineOrderRxImageRequest;
import com.dpdocter.request.UpdateOrderStatusRequest;
import com.dpdocter.response.ImageURLResponse;
import com.dpdocter.services.FileManager;
import com.dpdocter.services.MedicineOrderService;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.SMSServices;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class MedicineOrderServiceImpl implements MedicineOrderService{

	private static Logger logger = LogManager.getLogger(RecordsServiceImpl.class.getName());
	
	@Autowired
	FileManager fileManager;
	
	@Autowired
	private MedicineOrderRepository medicineOrderRepository;
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private TrackingOrderRepository trackingOrderRepository;
	
	@Autowired
	private UserCartRepository userCartRepository;
	
	@Value(value = "${image.path}")
	private String imagePath;
	
	@Value(value = "${medicine.order.placed.message}")
	private String ORDER_PLACED_MESSAGE;
	@Value(value = "${medicine.order.confirmed.message}")
	private String ORDER_CONFIRMED_MESSAGE;
	@Value(value = "${medicine.order.dispatched.message}")
	private String ORDER_DISPATCHED_MESSAGE;
	@Value(value = "${medicine.order.picked.message}")
	private String ORDER_PICKED_MESSAGE;
	@Value(value = "${medicine.order.out.of.delivery.message}")
	private String ORDER_OUT_OF_DELIVERY_MESSAGE;
	@Value(value = "${medicine.order.delivered.message}")
	private String ORDER_DELIVERED_MESSAGE;
	@Value(value = "${medicine.order.packed.message}")
	private String ORDER_PACKED_MESSAGE;
	
	
	@Autowired
	private SMSServices smsServices;
	
	@Autowired
	private PushNotificationServices pushNotificationServices;
	
	@Autowired
	private DrugInfoRepository drugInfoRepository;

	
	@Override
	@Transactional
	public ImageURLResponse saveRXMedicineOrderImage(MultipartFile file, String patientIdString) {
		String recordPath = null;
		ImageURLResponse imageURLResponse = null;
		try {

			Date createdTime = new Date();
			if (file != null) {
				if(DPDoctorUtils.anyStringEmpty(patientIdString))
				{
					patientIdString = patientIdString.replace("\"", "");
				}
				String path = "medorderRX" + File.separator + patientIdString;
				String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
				String fileName = file.getOriginalFilename().replaceFirst("." + fileExtension, "");

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

			if(request.getId() != null)
			{
				medicineOrderCollection = medicineOrderRepository.findById(new ObjectId(request.getId())).orElse(null);
			}

			if (medicineOrderCollection == null) {
				medicineOrderCollection = new MedicineOrderCollection();
				medicineOrderCollection.setUniqueOrderId(UniqueIdInitial.MEDICINE_ORDER.getInitial() + DPDoctorUtils.generateRandomId());
				medicineOrderCollection.setCreatedTime(new Date());
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
			medicineOrderCollection.setIsPrescriptionRequired(request.getIsPrescriptionRequired());

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
	public MedicineOrder addeditRxImage(MedicineOrderRxImageRequest request) {
		MedicineOrder medicineOrder = null;
		MedicineOrderCollection medicineOrderCollection = null;

		try {

			if(request.getId() != null)
			{
				medicineOrderCollection = medicineOrderRepository.findById(new ObjectId(request.getId())).orElse(null);
			}

			if (medicineOrderCollection == null) {
				medicineOrderCollection = new MedicineOrderCollection();
				medicineOrderCollection.setUniqueOrderId(UniqueIdInitial.MEDICINE_ORDER.getInitial() + DPDoctorUtils.generateRandomId());
			}

			medicineOrderCollection.setRxImage(request.getRxImage());

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
	public MedicineOrder addeditAddress(MedicineOrderAddEditAddressRequest request) {
		MedicineOrder medicineOrder = null;
		MedicineOrderCollection medicineOrderCollection = null;

		try {

			if(request.getId() != null)
			{
				medicineOrderCollection = medicineOrderRepository.findById(new ObjectId(request.getId())).orElse(null);
			}
			if (medicineOrderCollection == null) {
				medicineOrderCollection = new MedicineOrderCollection();
				medicineOrderCollection.setUniqueOrderId(UniqueIdInitial.MEDICINE_ORDER.getInitial() + DPDoctorUtils.generateRandomId());
			}

			medicineOrderCollection.setShippingAddress(request.getShippingAddress());
			medicineOrderCollection.setBillingAddress(request.getBillingAddress());
			medicineOrderCollection.setDeliveryPreference(request.getDeliveryPreference());
			medicineOrderCollection.setNextDeliveryDate(request.getNextDeliveryDate());
			medicineOrderCollection.setPaymentMode(request.getPaymentMode());
			medicineOrderCollection.setPatientName(request.getPatientName());
			medicineOrderCollection.setMobileNumber(request.getMobileNumber());
			medicineOrderCollection.setEmailAddress(request.getEmailAddress());
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

			if(request.getId() != null)
			{
				medicineOrderCollection = medicineOrderRepository.findById(new ObjectId(request.getId())).orElse(null);
			}
			if (medicineOrderCollection == null) {
				medicineOrderCollection = new MedicineOrderCollection();
				medicineOrderCollection.setUniqueOrderId(UniqueIdInitial.MEDICINE_ORDER.getInitial() + DPDoctorUtils.generateRandomId());
			}

			medicineOrderCollection.setTotalAmount(request.getTotalAmount());
			medicineOrderCollection.setDiscountedAmount(request.getDiscountedAmount());
			medicineOrderCollection.setDiscountedPercentage(request.getDiscountedPercentage());
			medicineOrderCollection.setFinalAmount(request.getFinalAmount());
			medicineOrderCollection.setDeliveryCharges(request.getDeliveryCharges());
			medicineOrderCollection.setNotes(request.getNotes());
			medicineOrderCollection.setCashHandlingCharges(request.getCashHandlingCharges());
			medicineOrderCollection.setCallingPreference(request.getCallingPreference());
			medicineOrderCollection.setOrderStatus(request.getOrderStatus());
			medicineOrderCollection.setIsPrescriptionRequired(request.getIsPrescriptionRequired());
			
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

			if(request.getId() != null)
			{
				medicineOrderCollection = medicineOrderRepository.findById(new ObjectId(request.getId())).orElse(null);
			}
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
		String message = "";
		TrackingOrder trackingOrder = null;
		try {

			medicineOrderCollection = medicineOrderRepository.findById(new ObjectId(id)).orElse(null);

			if (medicineOrderCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}

			medicineOrderCollection.setOrderStatus(status);
			switch (status) {
			case PLACED:
				message = ORDER_PLACED_MESSAGE;
				pushNotificationServices.notifyUser(String.valueOf(medicineOrderCollection.getPatientId()), message,
						ComponentType.ORDER_PLACED.getType(), id, null);
				sendStatusChangeMessage(String.valueOf(medicineOrderCollection.getPatientId()),
						medicineOrderCollection.getPatientName(), medicineOrderCollection.getMobileNumber(), message);
				trackingOrder = new TrackingOrder();
				trackingOrder.setOrderId(id);
				trackingOrder.setNote(message);
				trackingOrder.setStatus(status);
				trackingOrder.setTimestamp(System.currentTimeMillis());
				addeditTrackingDetails(trackingOrder);
				break;
				
			case CONFIRMED:
				message = ORDER_CONFIRMED_MESSAGE;
				pushNotificationServices.notifyUser(String.valueOf(medicineOrderCollection.getPatientId()), message, ComponentType.ORDER_CONFIRMED.getType(), id, null);
				sendStatusChangeMessage(String.valueOf(medicineOrderCollection.getPatientId()), medicineOrderCollection.getPatientName(), medicineOrderCollection.getMobileNumber(), message);
				trackingOrder = new TrackingOrder();
				trackingOrder.setOrderId(id);
				trackingOrder.setNote(message);
				trackingOrder.setStatus(status);
				trackingOrder.setTimestamp(System.currentTimeMillis());
				addeditTrackingDetails(trackingOrder);
				break;

				
			case PACKED:
				message = ORDER_PACKED_MESSAGE;
				pushNotificationServices.notifyUser(String.valueOf(medicineOrderCollection.getPatientId()), message, ComponentType.ORDER_PACKED.getType(), id, null);
				sendStatusChangeMessage(String.valueOf(medicineOrderCollection.getPatientId()), medicineOrderCollection.getPatientName(), medicineOrderCollection.getMobileNumber(), message);
				trackingOrder = new TrackingOrder();
				trackingOrder.setOrderId(id);
				trackingOrder.setNote(message);
				trackingOrder.setStatus(status);
				trackingOrder.setTimestamp(System.currentTimeMillis());
				addeditTrackingDetails(trackingOrder);
				break;

				
			case DISPATCHED:
				message = ORDER_DISPATCHED_MESSAGE;
				pushNotificationServices.notifyUser(String.valueOf(medicineOrderCollection.getPatientId()), message, ComponentType.ORDER_DISPATCHED.getType(), id, null);
				sendStatusChangeMessage(String.valueOf(medicineOrderCollection.getPatientId()), medicineOrderCollection.getPatientName(), medicineOrderCollection.getMobileNumber(), message);
				trackingOrder = new TrackingOrder();
				trackingOrder.setOrderId(id);
				trackingOrder.setNote(message);
				trackingOrder.setStatus(status);
				trackingOrder.setTimestamp(System.currentTimeMillis());
				addeditTrackingDetails(trackingOrder);
				break;

				
			case OUT_FOR_DELIVERY:
				message = ORDER_OUT_OF_DELIVERY_MESSAGE;
				if(medicineOrderCollection.getCollectionBoy() != null)
				{
					message = message.replace("{deliveryBoy}", medicineOrderCollection.getCollectionBoy().getName());
				}
				else{
					message = message.replace("{deliveryBoy}", "our representative");
				}
				pushNotificationServices.notifyUser(String.valueOf(medicineOrderCollection.getPatientId()), message, ComponentType.ORDER_OUT_FOR_DELIVERY.getType(), id, null);
				sendStatusChangeMessage(String.valueOf(medicineOrderCollection.getPatientId()), medicineOrderCollection.getPatientName(), medicineOrderCollection.getMobileNumber(), message);
				trackingOrder = new TrackingOrder();
				trackingOrder.setOrderId(id);
				trackingOrder.setNote(message);
				trackingOrder.setStatus(status);
				trackingOrder.setTimestamp(System.currentTimeMillis());
				addeditTrackingDetails(trackingOrder);
				break;
				
			case DELIVERED:
				message = ORDER_DELIVERED_MESSAGE;
				pushNotificationServices.notifyUser(String.valueOf(medicineOrderCollection.getPatientId()), message, ComponentType.ORDER_OUT_FOR_DELIVERY.getType(), id, null);
				sendStatusChangeMessage(String.valueOf(medicineOrderCollection.getPatientId()), medicineOrderCollection.getPatientName(), medicineOrderCollection.getMobileNumber(), message);
				trackingOrder = new TrackingOrder();
				trackingOrder.setOrderId(id);
				trackingOrder.setNote(message);
				trackingOrder.setStatus(status);
				trackingOrder.setTimestamp(System.currentTimeMillis());
				addeditTrackingDetails(trackingOrder);
				break;


			default:
				break;
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
	public MedicineOrder updateStatus(UpdateOrderStatusRequest request ) {
		MedicineOrder medicineOrder = null;
		MedicineOrderCollection medicineOrderCollection = null;

		try {

			medicineOrderCollection = medicineOrderRepository.findById(new ObjectId(request.getOrderId())).orElse(null);

			if (medicineOrderCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}

			medicineOrderCollection.setOrderStatus(request.getStatus());

			medicineOrderCollection = medicineOrderRepository.save(medicineOrderCollection);
			
			if (medicineOrderCollection != null) {
				medicineOrder = new MedicineOrder();
				BeanUtil.map(medicineOrderCollection, medicineOrder);
			}
			
			addeditTrackingDetails(request.getTrackingOrder());

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

			medicineOrderCollection = medicineOrderRepository.findById(new ObjectId(id)).orElse(null);

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

			medicineOrderCollection = medicineOrderRepository.findById(new ObjectId(id)).orElse(null);

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
	public List<MedicineOrder> getOrderList(String patientId , String updatedTime , String searchTerm, int page , int size , List<String> status) {
		List<MedicineOrder> orders = null;
		Aggregation aggregation =null;
		try {
			//Criteria criteria = new Criteria();
			
			long createdTimestamp = Long.parseLong(updatedTime);
			
			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));
			
			if (!DPDoctorUtils.anyStringEmpty(patientId)) {
				criteria.and("patientId").is(new ObjectId(patientId));
			}
			
			if(status != null && !status.isEmpty())
			{
				criteria.and("orderStatus").in(status);
			}
			
			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("longName").regex("^" + searchTerm, "i"));
			}
			
			if (size > 0) {
				aggregation =Aggregation.newAggregation(
							Aggregation.lookup("vendor_cl", "vendorId", "_id", "vendor"),
							new CustomAggregationOperation(new Document("$unwind",
									new BasicDBObject("path", "$vendor").append("preserveNullAndEmptyArrays",
											true))),
							Aggregation.match(criteria), Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
						 Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(
							Aggregation.lookup("vendor_cl", "vendorId", "_id", "vendor"),
							new CustomAggregationOperation(new Document("$unwind",
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
	
	

	@Override
	@Transactional
	public UserCart addeditUserCart(UserCart request) {
		UserCart userCart = null;
		UserCartCollection userCartCollection = null;

		try {

			if(request.getId() != null)
			{
				userCartCollection = userCartRepository.findById(new ObjectId(request.getId())).orElse(null);
			}

			if (userCartCollection == null) {
				userCartCollection = new UserCartCollection();
				userCartCollection.setCreatedTime(new Date());
				//medicineOrderCollection.setUniqueOrderId(UniqueIdInitial.MEDICINE_ORDER.getInitial() + DPDoctorUtils.generateRandomId());
			}

			BeanUtil.map(request, userCartCollection);
			/*List<MedicineOrderItems> orderItems = new ArrayList<>();
			for(MedicineOrderAddEditItems items : request.getItems()) {
				MedicineOrderItems medicineOrderItems  =  new MedicineOrderItems();
				BeanUtil.map(items, medicineOrderItems);
				orderItems.add(medicineOrderItems);
			}*/
			userCartCollection.setItems(request.getItems());
			userCartCollection = userCartRepository.save(userCartCollection);
			
			if (userCartCollection != null) {
				userCart = new UserCart();
				BeanUtil.map(userCartCollection, userCart);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return userCart;
	}
	
	@Override
	@Transactional
	public UserCart getUserCartById(String id ) {
		UserCart userCart = null;
		UserCartCollection userCartCollection = null;

		try {

			userCartCollection = userCartRepository.findById(new ObjectId(id)).orElse(null);

			if (userCartCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}

			userCartCollection = userCartRepository.save(userCartCollection);
			if (userCartCollection != null) {
				userCart = new UserCart();
				BeanUtil.map(userCartCollection, userCart);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return userCart;
	}
	
	
	@Override
	@Transactional
	public UserCart getUserCartByuserId(String id ) {
		UserCart userCart = null;
		UserCartCollection userCartCollection = null;

		try {

			userCartCollection = userCartRepository.findByUserId(new ObjectId(id));

			if (userCartCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}

			userCartCollection = userCartRepository.save(userCartCollection);
			if (userCartCollection != null) {
				userCart = new UserCart();
				BeanUtil.map(userCartCollection, userCart);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return userCart;
	}
	
	
	@Override
	@Transactional
	public UserCart clearCart(String id ) {
		UserCart userCart = null;
		UserCartCollection userCartCollection = null;

		try {

			userCartCollection = userCartRepository.findById(new ObjectId(id)).orElse(null);

			if (userCartCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Record not found");
			}

			userCartCollection.setDiscountedAmount(null);
			userCartCollection.setDiscountedPercentage(null);
			userCartCollection.setFinalAmount(null);
			userCartCollection.setTotalAmount(null);
			userCartCollection.setItems(null);
			userCartCollection.setQuantity(null);
			userCartCollection.setRxImage(null);
			
			userCartCollection = userCartRepository.save(userCartCollection);
			if (userCartCollection != null) {
				userCart = new UserCart();
				BeanUtil.map(userCartCollection, userCart);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return userCart;
	}
	
	@Override
	@Transactional
	public TrackingOrder addeditTrackingDetails(TrackingOrder request) {
		TrackingOrder trackingOrder = null;
		TrackingOrderCollection trackingOrderCollection = null;

		try {

			if(request.getId() != null)
			{
				trackingOrderCollection = trackingOrderRepository.findById(new ObjectId(request.getId())).orElse(null);
			}

			if (trackingOrderCollection == null) {
				trackingOrderCollection = new TrackingOrderCollection();
				trackingOrderCollection.setCreatedTime(new Date());
				//medicineOrderCollection.setUniqueOrderId(UniqueIdInitial.MEDICINE_ORDER.getInitial() + DPDoctorUtils.generateRandomId());
			}

			BeanUtil.map(request, trackingOrderCollection);

			trackingOrderCollection = trackingOrderRepository.save(trackingOrderCollection);
			
			if (trackingOrderCollection != null) {
				trackingOrder = new TrackingOrder();
				BeanUtil.map(trackingOrderCollection, trackingOrder);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e);
		}
		return trackingOrder;
	}
	
	@Override
	@Transactional
	public List<TrackingOrder> getTrackingList(String orderId , String updatedTime , String searchTerm, int page , int size) {
		List<TrackingOrder> orders = null;
		Aggregation aggregation =null;
		try {
			//Criteria criteria = new Criteria();
			
			long createdTimestamp = Long.parseLong(updatedTime);
			
			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));
			
			if (!DPDoctorUtils.anyStringEmpty(orderId)) {
				criteria.and("orderId").is(new ObjectId(orderId));
			}
			
			/*if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("longName").regex("^" + searchTerm, "i"));
			}*/
			
			if (size > 0) {
				aggregation =Aggregation.newAggregation(
							
							Aggregation.match(criteria), Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
						 Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(
							
							Aggregation.match(criteria), Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			
			
			orders = mongoTemplate.aggregate(
					aggregation,
					TrackingOrderCollection.class, TrackingOrder.class).getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return orders;
	}
	
	@Override
	@Transactional
	public List<DrugInfo> getDrugInfo(int page, int size, String updatedTime,
			String searchTerm, Boolean discarded) {
		List<DrugInfo> response = null;
		try {
			long createdTimeStamp = Long.parseLong(updatedTime);
			Aggregation aggregation = null;
			Criteria criteria = new Criteria();
			if (discarded != null)
				criteria.and("discarded").is(discarded);

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("brandName").regex("^" + searchTerm, "i"),
						new Criteria("drugType").regex("^" + searchTerm, "i"));
			}
			
			if (size > 0) {
				aggregation =Aggregation.newAggregation(
				Aggregation.match(criteria),Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
				Aggregation.skip((page) * size),Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(
				Aggregation.match(criteria),Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}
			response = mongoTemplate.aggregate(
					aggregation,
					DrugInfoCollection.class, DrugInfo.class).getMappedResults();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Advice");
		}
		return response;
	}
	
	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}
	
	private void sendStatusChangeMessage(String patientId , String patientName, String mobileNumber , String message) {
		try {
			
			if (mobileNumber != null) {
				SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
				
				smsTrackDetail.setType("APP_LINK_THROUGH_PRESCRIPTION");
				SMSDetail smsDetail = new SMSDetail();
				smsDetail.setUserId(new ObjectId(patientId));
				SMS sms = new SMS();
				smsDetail.setUserName(patientName);

				SMSAddress smsAddress = new SMSAddress();
				smsAddress.setRecipient(mobileNumber);
				sms.setSmsAddress(smsAddress);

				smsDetail.setSms(sms);
				smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
				List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
				smsDetails.add(smsDetail);
				smsTrackDetail.setSmsDetails(smsDetails);
				smsServices.sendSMS(smsTrackDetail, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	@Override
	@Transactional
	public DrugInfo getDrugByDrugCode(String drugCode) {
		DrugInfo drugAddEditResponse = null;
		try {
			DrugInfoCollection drugCollection = drugInfoRepository.findByDrugCode(drugCode);
			if (drugCollection != null) {
				drugAddEditResponse = new DrugInfo();
				BeanUtil.map(drugCollection, drugAddEditResponse);
			} else {
				logger.warn("Drug not found. Please check Drug Id");
				throw new BusinessException(ServiceError.NoRecord, "Drug not found. Please check Drug Id");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e + " Error Occurred While Getting Drug");
			throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Drug");
		}
		return drugAddEditResponse;
	}
	
	
	@Override
	@Transactional
	public List<DrugInfo> getDrugByDrugCodes(DrugCodeListRequest request) {
		List<DrugInfo> drugInfos = null;
		Aggregation aggregation = null;
		try {
			Criteria criteria = new Criteria("drugCode").in(request.getDrugCodes());

			aggregation = Aggregation.newAggregation(Aggregation.match(criteria));

			drugInfos = mongoTemplate.aggregate(aggregation, DrugInfoCollection.class, DrugInfo.class)
					.getMappedResults();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		}
		return drugInfos;
	}
	
}
