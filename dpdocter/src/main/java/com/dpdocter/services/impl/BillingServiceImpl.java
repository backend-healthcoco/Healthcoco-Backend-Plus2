package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DoctorPatientInvoice;
import com.dpdocter.beans.DoctorPatientLedger;
import com.dpdocter.beans.DoctorPatientReceipt;
import com.dpdocter.beans.InvoiceAndReceiptInitials;
import com.dpdocter.beans.InvoiceIdWithAmount;
import com.dpdocter.beans.InvoiceItem;
import com.dpdocter.collections.DoctorPatientInvoiceCollection;
import com.dpdocter.collections.DoctorPatientLedgerCollection;
import com.dpdocter.collections.DoctorPatientReceiptCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.BillingType;
import com.dpdocter.enums.ReceiptType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorPatientInvoiceRepository;
import com.dpdocter.repository.DoctorPatientLedgerRepository;
import com.dpdocter.repository.DoctorPatientReceiptRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DoctorPatientInvoiceAndReceiptRequest;
import com.dpdocter.request.DoctorPatientReceiptRequest;
import com.dpdocter.response.AmountResponse;
import com.dpdocter.response.DoctorPatientInvoiceAndReceiptResponse;
import com.dpdocter.response.DoctorPatientLedgerResponse;
import com.dpdocter.response.DoctorPatientReceiptAddEditResponse;
import com.dpdocter.response.InvoiceItemResponse;
import com.dpdocter.services.BillingService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class BillingServiceImpl implements BillingService {

	private static Logger logger = Logger.getLogger(BillingServiceImpl.class.getName());
	
	@Autowired
	LocationRepository locationRepository;
	
	@Autowired
	DoctorPatientReceiptRepository doctorPatientReceiptRepository;
	
	@Autowired
	DoctorPatientInvoiceRepository 	doctorPatientInvoiceRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	DoctorPatientLedgerRepository doctorPatientLedgerRepository;
	@Override
	public InvoiceAndReceiptInitials getInitials(String locationId) {
		InvoiceAndReceiptInitials response = null;
		try{
			LocationCollection locationCollection = locationRepository.findOne(new ObjectId(locationId));
			if(locationCollection != null){
				response = new InvoiceAndReceiptInitials();
				response.setLocationId(locationId);
				response.setInvoiceInitial(locationCollection.getInvoiceInitial());
				response.setReceiptInitial(locationCollection.getReceiptInitial());
			}else{
				throw new BusinessException(ServiceError.InvalidInput, "Invalid location Id");
			}
		}catch(BusinessException be){
			logger.error(be);
			throw be;
		}catch(Exception e){
			logger.error("Error while updating billing initials"+e);
			throw new BusinessException(ServiceError.Unknown, "Error while updating billing initials"+e);
		}
		return response;
	}
	
	@Override
	public InvoiceAndReceiptInitials updateInitials(InvoiceAndReceiptInitials request) {
		InvoiceAndReceiptInitials response = null;
		try{
			LocationCollection locationCollection = locationRepository.findOne(new ObjectId(request.getLocationId()));
			if(locationCollection != null){
				locationCollection.setInvoiceInitial(request.getInvoiceInitial());
				locationCollection.setReceiptInitial(request.getReceiptInitial());
				locationCollection.setUpdatedTime(new Date());
				locationCollection = locationRepository.save(locationCollection);
				response = new InvoiceAndReceiptInitials();
				BeanUtil.map(locationCollection, response);
				response.setLocationId(locationCollection.getId().toString());
			}else{
				throw new BusinessException(ServiceError.InvalidInput, "Invalid location Id");
			}
		}catch(BusinessException be){
			logger.error(be);
			throw be;
		}catch(Exception e){
			logger.error("Error while updating billing initials"+e);
			throw new BusinessException(ServiceError.Unknown, "Error while updating billing initials"+e);
		}
		return response;
	}

	@Override
	public DoctorPatientInvoice addEditInvoice(DoctorPatientInvoice request) {
		DoctorPatientInvoice response = null;
		try{
			Map<String, UserCollection>  doctorsMap = new HashMap<String, UserCollection>();
			DoctorPatientInvoiceCollection doctorPatientInvoiceCollection = new DoctorPatientInvoiceCollection();
			ObjectId doctorObjectId = new ObjectId(request.getDoctorId());
			if(DPDoctorUtils.anyStringEmpty(request.getId())){
				BeanUtil.map(request, doctorPatientInvoiceCollection);
				UserCollection userCollection = userRepository.findOne(doctorObjectId);
				if(userCollection != null){
					doctorPatientInvoiceCollection.setCreatedBy((!DPDoctorUtils.anyStringEmpty(userCollection.getTitle())?userCollection.getTitle()+" ":"")+
							userCollection.getFirstName());	
					doctorsMap.put(request.getDoctorId(), userCollection);
				}
				doctorPatientInvoiceCollection.setCreatedTime(new Date());
				LocationCollection locationCollection = locationRepository.findOne(new ObjectId(request.getLocationId()));
				if(locationCollection == null)throw new BusinessException(ServiceError.InvalidInput, "Invalid Location Id");
				doctorPatientInvoiceCollection.setUniqueInvoiceId(locationCollection.getInvoiceInitial()+
						((int)mongoTemplate.count(new Query(new Criteria("locationId").is(doctorPatientInvoiceCollection.getLocationId()).
								and("hospitalId").is(doctorPatientInvoiceCollection.getHospitalId())), DoctorPatientInvoiceCollection.class)+1));
				doctorPatientInvoiceCollection.setBalanceAmount(request.getGrandTotal());
			}
			else{
				doctorPatientInvoiceCollection = doctorPatientInvoiceRepository.findOne(new ObjectId(request.getId()));
				doctorPatientInvoiceCollection.setUpdatedTime(new Date());
				doctorPatientInvoiceCollection.setTotalCost(request.getTotalCost());
				doctorPatientInvoiceCollection.setTotalDiscount(request.getTotalDiscount());
				doctorPatientInvoiceCollection.setTotalTax(request.getTotalTax());
				doctorPatientInvoiceCollection.setGrandTotal(request.getGrandTotal());
				doctorPatientInvoiceCollection.setBalanceAmount((request.getGrandTotal()-doctorPatientInvoiceCollection.getGrandTotal())+
						doctorPatientInvoiceCollection.getBalanceAmount());
				if(doctorPatientInvoiceCollection.getBalanceAmount()<0){
					doctorPatientInvoiceCollection.setRefundAmount(doctorPatientInvoiceCollection.getBalanceAmount()*(-1));
					doctorPatientInvoiceCollection.setBalanceAmount(0.0);
				}
			}
			List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
			for(InvoiceItemResponse invoiceItemResponse : request.getInvoiceItems()){
				if(DPDoctorUtils.anyStringEmpty(invoiceItemResponse.getDoctorId())){
					invoiceItemResponse.setDoctorId(request.getDoctorId());
					invoiceItemResponse.setDoctorName(doctorPatientInvoiceCollection.getCreatedBy());
				}else{
					if(invoiceItemResponse.getDoctorId().toString().equalsIgnoreCase(request.getDoctorId()))
						invoiceItemResponse.setDoctorName(doctorPatientInvoiceCollection.getCreatedBy());
					else{
						UserCollection doctor = doctorsMap.get(invoiceItemResponse.getDoctorId().toString());
						if(doctor == null){
							doctor = userRepository.findOne(new ObjectId(invoiceItemResponse.getDoctorId()));
							doctorsMap.put(invoiceItemResponse.getDoctorId(), doctor);
						}
						invoiceItemResponse.setDoctorName((!DPDoctorUtils.anyStringEmpty(doctor.getTitle())?doctor.getTitle()+" ":"")+
							doctor.getFirstName());
					}
				}
				InvoiceItem invoiceItem = new InvoiceItem();
				
				BeanUtil.map(invoiceItemResponse, invoiceItem);
				invoiceItems.add(invoiceItem);
				doctorPatientInvoiceCollection.setInvoiceItems(invoiceItems);
			}
			if(doctorPatientInvoiceCollection != null){
				doctorPatientInvoiceCollection = doctorPatientInvoiceRepository.save(doctorPatientInvoiceCollection);
				response = new DoctorPatientInvoice();
				BeanUtil.map(doctorPatientInvoiceCollection, response);
				
				DoctorPatientLedgerCollection doctorPatientLedgerCollection = doctorPatientLedgerRepository.findByInvoiceId(doctorPatientInvoiceCollection.getId());
				Double balanceAmount = getBalanceAmount(null, doctorPatientInvoiceCollection.getLocationId().toString(), doctorPatientInvoiceCollection.getHospitalId().toString(), doctorPatientInvoiceCollection.getPatientId().toString());
				if(doctorPatientLedgerCollection == null){
					doctorPatientLedgerCollection = new DoctorPatientLedgerCollection();
					doctorPatientLedgerCollection.setPatientId(doctorPatientInvoiceCollection.getPatientId());
					doctorPatientLedgerCollection.setLocationId(doctorPatientInvoiceCollection.getLocationId());
					doctorPatientLedgerCollection.setHospitalId(doctorPatientInvoiceCollection.getHospitalId());
					doctorPatientLedgerCollection.setInvoiceId(doctorPatientInvoiceCollection.getId());
					doctorPatientLedgerCollection.setBalanceAmount(balanceAmount+doctorPatientInvoiceCollection.getBalanceAmount());
					doctorPatientLedgerCollection.setDebitAmount(doctorPatientInvoiceCollection.getBalanceAmount());
					doctorPatientLedgerCollection.setCreatedTime(new Date());
					doctorPatientLedgerCollection.setUpdatedTime(new Date());
				}else{
					doctorPatientLedgerCollection.setBalanceAmount(balanceAmount+doctorPatientInvoiceCollection.getBalanceAmount());
					doctorPatientLedgerCollection.setDebitAmount(doctorPatientInvoiceCollection.getBalanceAmount());
					doctorPatientLedgerCollection.setUpdatedTime(new Date());
				}
				doctorPatientLedgerCollection = doctorPatientLedgerRepository.save(doctorPatientLedgerCollection);
			}
		}catch(BusinessException be){
			logger.error(be);
			throw be;
		}catch(Exception e){
			logger.error("Error while adding invoice"+e);
			throw new BusinessException(ServiceError.Unknown, "Error while adding invoice"+e);
		}
		return response;
	}

	@Override
	public DoctorPatientInvoice getInvoice(String invoiceId) {
		DoctorPatientInvoice response = null;
		try{
			DoctorPatientInvoiceCollection doctorPatientInvoiceCollection = doctorPatientInvoiceRepository.findOne(new ObjectId(invoiceId));
			if(doctorPatientInvoiceCollection != null){
				response = new DoctorPatientInvoice();
				BeanUtil.map(doctorPatientInvoiceCollection, response);
			}
			
		}catch(BusinessException be){
			logger.error(be);
			throw be;
		}catch(Exception e){
			logger.error("Error while getting invoice"+e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting invoice"+e);
		}
		return response;
	}
	
	@Override
	public List<DoctorPatientInvoice> getInvoices(String type, int page, int size, String doctorId, String locationId,
			String hospitalId, String patientId, String updatedTime, Boolean discarded) {
		List<DoctorPatientInvoice> responses = null;
		try{
			long createdTimestamp = Long.parseLong(updatedTime);
			
			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));
					
			if (!DPDoctorUtils.anyStringEmpty(patientId))criteria.and("patientId").is(new ObjectId(patientId));
			if (!DPDoctorUtils.anyStringEmpty(doctorId))criteria.and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))criteria.and("locationId").is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));
			if (!discarded)criteria.and("discarded").is(discarded);
			
			switch (BillingType.valueOf(type.toUpperCase())) {
			  case SETTLE:criteria.and("balanceAmount").is(0.0);break;
			  case NONSETTLE:criteria.and("balanceAmount").gt(0.0);break;
			  case BOTH:break;	  
			}
			if(size > 0){
				responses = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size)), DoctorPatientInvoiceCollection.class, DoctorPatientInvoice.class).getMappedResults();
			}else{
				responses = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime"))), DoctorPatientInvoiceCollection.class, DoctorPatientInvoice.class).getMappedResults();
			}
				
			
		}catch(BusinessException be){
			logger.error(be);
			throw be;
		}catch(Exception e){
			logger.error("Error while getting invoices"+e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting invoices"+e);
		}
		return responses;
	}

	@Override
	public DoctorPatientInvoice deleteInvoice(String invoiceId, Boolean discarded) {
		DoctorPatientInvoice response = null;
		try{
			DoctorPatientInvoiceCollection doctorPatientInvoiceCollection = doctorPatientInvoiceRepository.findOne(new ObjectId(invoiceId));
			doctorPatientInvoiceCollection.setDiscarded(discarded);
			doctorPatientInvoiceCollection.setUpdatedTime(new Date());
			doctorPatientInvoiceCollection = doctorPatientInvoiceRepository.save(doctorPatientInvoiceCollection);
			if(doctorPatientInvoiceCollection != null){
				response = new DoctorPatientInvoice();
				BeanUtil.map(doctorPatientInvoiceCollection, response);
			}
		}catch(BusinessException be){
			logger.error(be);
			throw be;
		}catch(Exception e){
			logger.error("Error while deleting invoices"+e);
			throw new BusinessException(ServiceError.Unknown, "Error while deleting invoices"+e);
		}
		return response;
	}

	@Override
	public DoctorPatientReceiptAddEditResponse addEditReceipt(DoctorPatientReceiptRequest request) {
		DoctorPatientReceiptAddEditResponse response = null;
		DoctorPatientReceipt receipt = null;
		DoctorPatientInvoice invoice = null;
		try{
			DoctorPatientReceiptCollection doctorPatientReceiptCollection = new DoctorPatientReceiptCollection();
			if(DPDoctorUtils.anyStringEmpty(request.getId())){
				BeanUtil.map(request, doctorPatientReceiptCollection);
				UserCollection userCollection = userRepository.findOne(new ObjectId(request.getDoctorId()));
				if(userCollection != null){
					doctorPatientReceiptCollection.setCreatedBy((!DPDoctorUtils.anyStringEmpty(userCollection.getTitle())?userCollection.getTitle()+" ":"")+
							userCollection.getFirstName());	
				}
				doctorPatientReceiptCollection.setCreatedTime(new Date());
				LocationCollection locationCollection = locationRepository.findOne(new ObjectId(request.getLocationId()));
				if(locationCollection == null)throw new BusinessException(ServiceError.InvalidInput, "Invalid Location Id");
				doctorPatientReceiptCollection.setUniqueReceiptId(locationCollection.getReceiptInitial()+
						((int)mongoTemplate.count(new Query(new Criteria("locationId").is(doctorPatientReceiptCollection.getLocationId()).
								and("hospitalId").is(doctorPatientReceiptCollection.getHospitalId())), DoctorPatientReceiptCollection.class)+1));
			}else{
				doctorPatientReceiptCollection = doctorPatientReceiptRepository.findOne(new ObjectId(request.getId()));
			}
			if(doctorPatientReceiptCollection.getReceiptType().name().equalsIgnoreCase(ReceiptType.ADVANCE.name())){
				doctorPatientReceiptCollection.setRemainingAdvanceAmount(request.getAmountPaid());
				doctorPatientReceiptCollection.setBalanceAmount(0.0);
				doctorPatientReceiptCollection = doctorPatientReceiptRepository.save(doctorPatientReceiptCollection);
			}else if(doctorPatientReceiptCollection.getReceiptType().name().equalsIgnoreCase(ReceiptType.INVOICE.name())){
				
				if(request.getInvoiceIds() != null && !request.getInvoiceIds().isEmpty()){
					DoctorPatientInvoiceCollection doctorPatientInvoiceCollection  = doctorPatientInvoiceRepository.findOne(new ObjectId(request.getInvoiceIds().get(0)));
					if(doctorPatientInvoiceCollection == null){
						throw new BusinessException(ServiceError.InvalidInput, "Invalid Invoice Id");
					}
					List<ObjectId> receiptIds = doctorPatientInvoiceCollection.getReceiptIds();
					if(request.getUsedAdvanceAmount() > 0){
						
						List<DoctorPatientReceiptCollection> receiptsOfAdvancePayment = doctorPatientReceiptRepository.
								findAvailableAdvanceReceipts(ReceiptType.ADVANCE.name(), doctorPatientInvoiceCollection.getDoctorId(), 
										doctorPatientInvoiceCollection.getLocationId(), doctorPatientInvoiceCollection.getHospitalId(),
										doctorPatientInvoiceCollection.getPatientId(), new Sort(Direction.ASC, "createdTime"));
						if(receiptsOfAdvancePayment == null || receiptsOfAdvancePayment.isEmpty())
							throw new BusinessException(ServiceError.InvalidInput, "Advance Amount is not available");
						
						Double advanceAmountToBeUsed = request.getUsedAdvanceAmount();
						for(DoctorPatientReceiptCollection receiptCollection : receiptsOfAdvancePayment){
							InvoiceIdWithAmount invoiceIdWithAmount = new InvoiceIdWithAmount();
							invoiceIdWithAmount.setUniqueInvoiceId(doctorPatientInvoiceCollection.getUniqueInvoiceId());
							invoiceIdWithAmount.setInvoiceId(doctorPatientInvoiceCollection.getId());
						if(advanceAmountToBeUsed > 0.0){
							if(receiptCollection.getRemainingAdvanceAmount()>advanceAmountToBeUsed){
								receiptCollection.setRemainingAdvanceAmount(receiptCollection.getRemainingAdvanceAmount()-advanceAmountToBeUsed);
								invoiceIdWithAmount.setUsedAdvanceAmount(advanceAmountToBeUsed);
								advanceAmountToBeUsed = 0.0;
							}
							else{
								receiptCollection.setRemainingAdvanceAmount(0.0);
								invoiceIdWithAmount.setUsedAdvanceAmount(receiptCollection.getRemainingAdvanceAmount());
								advanceAmountToBeUsed = advanceAmountToBeUsed - receiptCollection.getRemainingAdvanceAmount();
							}
							List<InvoiceIdWithAmount> invoiceIds = receiptCollection.getInvoiceIdsWithAmount();
							if(invoiceIds == null || invoiceIds.isEmpty()){
								invoiceIds = new ArrayList<InvoiceIdWithAmount>();
							}
							
							invoiceIds.add(invoiceIdWithAmount);
							receiptCollection.setInvoiceIdsWithAmount(invoiceIds);
							receiptCollection.setUpdatedTime(new Date());
							
							if(receiptIds == null)receiptIds = new ArrayList<ObjectId>();
							receiptIds.add(receiptCollection.getId());
							doctorPatientReceiptRepository.save(receiptCollection);
						}
						}
						doctorPatientInvoiceCollection.setUsedAdvanceAmount(request.getUsedAdvanceAmount());
						doctorPatientInvoiceCollection.setBalanceAmount(doctorPatientInvoiceCollection.getGrandTotal()-
								request.getAmountPaid()-request.getUsedAdvanceAmount());
					}else{
						doctorPatientInvoiceCollection.setBalanceAmount(doctorPatientInvoiceCollection.getGrandTotal()-
								request.getAmountPaid());
					}
					doctorPatientReceiptCollection.setUniqueInvoiceId(doctorPatientInvoiceCollection.getUniqueInvoiceId());
					doctorPatientReceiptCollection.setInvoiceId(doctorPatientInvoiceCollection.getId());
					doctorPatientReceiptCollection = doctorPatientReceiptRepository.save(doctorPatientReceiptCollection);
					
					if(receiptIds == null)receiptIds = new ArrayList<ObjectId>();
					receiptIds.add(doctorPatientReceiptCollection.getId());
					doctorPatientInvoiceCollection.setReceiptIds(receiptIds);
					doctorPatientInvoiceCollection.setUpdatedTime(new Date());
					doctorPatientInvoiceRepository.save(doctorPatientInvoiceCollection);
					invoice = new DoctorPatientInvoice();
					BeanUtil.map(doctorPatientInvoiceCollection, invoice);
				}else{
					throw new BusinessException(ServiceError.InvalidInput, "Invoice Id cannot be null");
				}
			}
			if(doctorPatientReceiptCollection != null){
				response = new DoctorPatientReceiptAddEditResponse();
				receipt = new DoctorPatientReceipt();
				BeanUtil.map(doctorPatientReceiptCollection, receipt);
				response.setInvoice(invoice);
				response.setDoctorPatientReceipt(receipt);
				DoctorPatientLedgerCollection doctorPatientLedgerCollection = doctorPatientLedgerRepository.findByReceiptId(doctorPatientReceiptCollection.getId());
				Double balanceAmount = getBalanceAmount(null, doctorPatientReceiptCollection.getLocationId().toString(), doctorPatientReceiptCollection.getHospitalId().toString(), doctorPatientReceiptCollection.getPatientId().toString());
				if(doctorPatientLedgerCollection == null){
					doctorPatientLedgerCollection = new DoctorPatientLedgerCollection();
					doctorPatientLedgerCollection.setPatientId(doctorPatientReceiptCollection.getPatientId());
					doctorPatientLedgerCollection.setLocationId(doctorPatientReceiptCollection.getLocationId());
					doctorPatientLedgerCollection.setHospitalId(doctorPatientReceiptCollection.getHospitalId());
					doctorPatientLedgerCollection.setReceiptId(doctorPatientReceiptCollection.getId());
					doctorPatientLedgerCollection.setBalanceAmount(balanceAmount-doctorPatientReceiptCollection.getAmountPaid());
					doctorPatientLedgerCollection.setCreditAmount(doctorPatientReceiptCollection.getAmountPaid());
					doctorPatientLedgerCollection.setCreatedTime(new Date());
					doctorPatientLedgerCollection.setUpdatedTime(new Date());
				}else{
					doctorPatientLedgerCollection.setCreditAmount(doctorPatientReceiptCollection.getAmountPaid());
					doctorPatientLedgerCollection.setBalanceAmount(balanceAmount-doctorPatientReceiptCollection.getAmountPaid());
					doctorPatientLedgerCollection.setUpdatedTime(new Date());
				}
				doctorPatientLedgerCollection = doctorPatientLedgerRepository.save(doctorPatientLedgerCollection);
				
				Criteria criteria = new Criteria("patientId").is(new ObjectId(request.getPatientId()))
						.and("locationId").is(new ObjectId(request.getLocationId())).and("hospitalId").is(new ObjectId(request.getHospitalId()));
				
				
				List<DoctorPatientLedger> doctorPatientLedgerList = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
						Aggregation.skip(0), Aggregation.limit(1)), DoctorPatientLedgerCollection.class, DoctorPatientLedger.class).getMappedResults();
				
				if(!DPDoctorUtils.anyStringEmpty(request.getDoctorId()))criteria.and("doctorId").is(new ObjectId(request.getDoctorId()));
				DoctorPatientReceipt doctorPatientReceipt = mongoTemplate.aggregate(Aggregation.newAggregation(
						Aggregation.match(criteria), Aggregation.group("patientId").sum("remainingAdvanceAmount").as("remainingAdvanceAmount")),
						DoctorPatientReceiptCollection.class, DoctorPatientReceipt.class).getUniqueMappedResult();
				
				if(doctorPatientReceipt != null)response.setTotalRemainingAdvanceAmount(doctorPatientReceipt.getRemainingAdvanceAmount());
				if(doctorPatientLedgerList != null && !doctorPatientLedgerList.isEmpty())response.setTotalBalanceAmount(doctorPatientLedgerList.get(0).getBalanceAmount());
			}
		}catch(BusinessException be){
			logger.error(be);
			throw be;
		}catch(Exception e){
			logger.error("Error while adding receipts"+e);
			throw new BusinessException(ServiceError.Unknown, "Error while adding receipts"+e);
		}
		return response;
	}

	@Override
	public List<DoctorPatientReceipt> getReceipts(int page, int size, String doctorId, String locationId,
			String hospitalId, String patientId, String updatedTime, Boolean discarded) {
		List<DoctorPatientReceipt> responses = null;
		try{
			long createdTimestamp = Long.parseLong(updatedTime);
			
			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));
					
			if (!DPDoctorUtils.anyStringEmpty(patientId))criteria.and("patientId").is(new ObjectId(patientId));
			if (!DPDoctorUtils.anyStringEmpty(doctorId))criteria.and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))criteria.and("locationId").is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));
			if (!discarded)criteria.and("discarded").is(discarded);
			
			if(size > 0){
				responses = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
//						Aggregation.lookup("doctor_patient_invoice_cl", "invoiceId", "_id", "invoice"),
//						new CustomAggregationOperation(new BasicDBObject("$unwind",
//								new BasicDBObject("path", "$invoice").append("preserveNullAndEmptyArrays", true))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size)), DoctorPatientReceiptCollection.class, DoctorPatientReceipt.class).getMappedResults();
			}else{
				responses = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
//						Aggregation.lookup("doctor_patient_invoice_cl", "invoiceId", "_id", "invoice"),
//						new CustomAggregationOperation(new BasicDBObject("$unwind",
//								new BasicDBObject("path", "$invoice").append("preserveNullAndEmptyArrays", true))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime"))), DoctorPatientReceiptCollection.class, DoctorPatientReceipt.class).getMappedResults();
			}
		}catch(BusinessException be){
			logger.error(be);
			throw be;
		}catch(Exception e){
			logger.error("Error while getting receipts"+e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting receipts"+e);
		}
		return responses;
	}

	@Override
	public DoctorPatientReceipt deleteReceipt(String receiptId, Boolean discarded) {
		DoctorPatientReceipt response = null;
		try{
			Update update = new Update();
			update.addToSet("discarded", discarded);
			DoctorPatientReceiptCollection doctorPatientReceiptCollection = mongoTemplate.findAndModify(new Query(new Criteria("id").is(new ObjectId(receiptId))), update, DoctorPatientReceiptCollection.class);
//					doctorPatientReceiptRepository.findOne(new ObjectId(receiptId));
			doctorPatientReceiptCollection.setDiscarded(discarded);
			doctorPatientReceiptCollection.setUpdatedTime(new Date());
			doctorPatientReceiptCollection = doctorPatientReceiptRepository.save(doctorPatientReceiptCollection);
			if(doctorPatientReceiptCollection != null){
				response = new DoctorPatientReceipt();
				BeanUtil.map(doctorPatientReceiptCollection, response);
			}
		}catch(BusinessException be){
			logger.error(be);
			throw be;
		}catch(Exception e){
			logger.error("Error while deleting receipt "+e);
			throw new BusinessException(ServiceError.Unknown, "Error while deleting receipt"+e);
		}
		return response;
	}

	@Override
	public Double getAvailableAdvanceAmount(String doctorId, String locationId, String hospitalId, String patientId) {
		Double response = 0.0;
		try{
			Criteria criteria = new Criteria("patientId").is(new ObjectId(patientId)).and("locationId").is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));
			if (!DPDoctorUtils.anyStringEmpty(doctorId))criteria.and("doctorId").is(new ObjectId(doctorId));
			
			DoctorPatientReceipt doctorPatientReceipt = mongoTemplate.aggregate(Aggregation.newAggregation(
					Aggregation.match(criteria), Aggregation.group("patientId").sum("remainingAdvanceAmount").as("remainingAdvanceAmount")),
					DoctorPatientReceiptCollection.class, DoctorPatientReceipt.class).getUniqueMappedResult();
			if(doctorPatientReceipt != null)response = doctorPatientReceipt.getRemainingAdvanceAmount();
		}catch(BusinessException be){
			logger.error(be);
			throw be;
		}catch(Exception e){
			logger.error("Error while getting available advance amount "+e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting available advance amount"+e);
		}
		return response;
	}

	@Override
	public DoctorPatientInvoiceAndReceiptResponse addInvoiceAndPay(DoctorPatientInvoiceAndReceiptRequest request) {
		DoctorPatientInvoiceAndReceiptResponse response = null;
		try{
			Map<String, UserCollection>  doctorsMap = new HashMap<String, UserCollection>();
			DoctorPatientInvoiceCollection doctorPatientInvoiceCollection = new DoctorPatientInvoiceCollection();
			ObjectId doctorObjectId = new ObjectId(request.getDoctorId());
			
			BeanUtil.map(request, doctorPatientInvoiceCollection);
			UserCollection userCollection = userRepository.findOne(doctorObjectId);
			if(userCollection != null){
					doctorPatientInvoiceCollection.setCreatedBy((!DPDoctorUtils.anyStringEmpty(userCollection.getTitle())?userCollection.getTitle()+" ":"")+
							userCollection.getFirstName());	
					doctorsMap.put(request.getDoctorId(), userCollection);
			}
			doctorPatientInvoiceCollection.setCreatedTime(new Date());
			LocationCollection locationCollection = locationRepository.findOne(new ObjectId(request.getLocationId()));
			if(locationCollection == null)throw new BusinessException(ServiceError.InvalidInput, "Invalid Location Id");
			doctorPatientInvoiceCollection.setUniqueInvoiceId(locationCollection.getInvoiceInitial()+
						((int)mongoTemplate.count(new Query(new Criteria("locationId").is(doctorPatientInvoiceCollection.getLocationId()).
								and("hospitalId").is(doctorPatientInvoiceCollection.getHospitalId())), DoctorPatientInvoiceCollection.class)+1));
			List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
			for(InvoiceItemResponse invoiceItemResponse : request.getInvoiceItems()){
				if(DPDoctorUtils.anyStringEmpty(invoiceItemResponse.getDoctorId())){
					invoiceItemResponse.setDoctorId(request.getDoctorId());
					invoiceItemResponse.setDoctorName(doctorPatientInvoiceCollection.getCreatedBy());
				}else{
					if(invoiceItemResponse.getDoctorId().toString().equalsIgnoreCase(request.getDoctorId()))
						invoiceItemResponse.setDoctorName(doctorPatientInvoiceCollection.getCreatedBy());
					else{
						UserCollection doctor = doctorsMap.get(invoiceItemResponse.getDoctorId().toString());
						if(doctor == null){
							doctor = userRepository.findOne(new ObjectId(invoiceItemResponse.getDoctorId()));
							doctorsMap.put(invoiceItemResponse.getDoctorId(), doctor);
						}
						invoiceItemResponse.setDoctorName((!DPDoctorUtils.anyStringEmpty(doctor.getTitle())?doctor.getTitle()+" ":"")+
							doctor.getFirstName());
					}
				}
				InvoiceItem invoiceItem = new InvoiceItem();
				
				BeanUtil.map(invoiceItemResponse, invoiceItem);
				invoiceItems.add(invoiceItem);
				doctorPatientInvoiceCollection.setInvoiceItems(invoiceItems);
			}
			doctorPatientInvoiceCollection = doctorPatientInvoiceRepository.save(doctorPatientInvoiceCollection);
			List<ObjectId> receiptIds = doctorPatientInvoiceCollection.getReceiptIds();
			DoctorPatientReceiptCollection doctorPatientReceiptCollection = new DoctorPatientReceiptCollection();
			BeanUtil.map(request, doctorPatientReceiptCollection);
			doctorPatientReceiptCollection.setCreatedBy((!DPDoctorUtils.anyStringEmpty(userCollection.getTitle())?userCollection.getTitle()+" ":"")+
						userCollection.getFirstName());	
			doctorPatientReceiptCollection.setCreatedTime(new Date());
			doctorPatientReceiptCollection.setUniqueReceiptId(locationCollection.getReceiptInitial()+
					((int)mongoTemplate.count(new Query(new Criteria("locationId").is(doctorPatientReceiptCollection.getLocationId()).
							and("hospitalId").is(doctorPatientReceiptCollection.getHospitalId())), DoctorPatientReceiptCollection.class)+1));
		
			doctorPatientReceiptCollection.setReceiptType(ReceiptType.INVOICE);
			
			InvoiceIdWithAmount invoiceIdWithAmount = new InvoiceIdWithAmount();
			invoiceIdWithAmount.setUniqueInvoiceId(doctorPatientInvoiceCollection.getUniqueInvoiceId());
			invoiceIdWithAmount.setInvoiceId(doctorPatientInvoiceCollection.getId());
			doctorPatientReceiptCollection.setInvoiceIdsWithAmount(Arrays.asList(invoiceIdWithAmount));
				
			if(request.getUsedAdvanceAmount() > 0){
					
					List<DoctorPatientReceiptCollection> receiptsOfAdvancePayment = doctorPatientReceiptRepository.
							findAvailableAdvanceReceipts(ReceiptType.ADVANCE.name(), doctorPatientInvoiceCollection.getDoctorId(), doctorPatientInvoiceCollection.getLocationId(), doctorPatientInvoiceCollection.getHospitalId(),
									doctorPatientInvoiceCollection.getPatientId(), new Sort(Direction.ASC, "createdTime"));
					if(receiptsOfAdvancePayment == null || receiptsOfAdvancePayment.isEmpty())
						throw new BusinessException(ServiceError.InvalidInput, "Advance Amount is not available");
					
					Double advanceAmountToBeUsed = request.getUsedAdvanceAmount();
					for(DoctorPatientReceiptCollection receiptCollection : receiptsOfAdvancePayment){
						invoiceIdWithAmount = new InvoiceIdWithAmount();
						invoiceIdWithAmount.setUniqueInvoiceId(doctorPatientInvoiceCollection.getUniqueInvoiceId());
						invoiceIdWithAmount.setInvoiceId(doctorPatientInvoiceCollection.getId());
					if(advanceAmountToBeUsed > 0.0){
						if(receiptCollection.getRemainingAdvanceAmount()>advanceAmountToBeUsed){
							receiptCollection.setRemainingAdvanceAmount(receiptCollection.getRemainingAdvanceAmount()-advanceAmountToBeUsed);
							invoiceIdWithAmount.setUsedAdvanceAmount(advanceAmountToBeUsed);
							advanceAmountToBeUsed = 0.0;
						}
						else{
							receiptCollection.setRemainingAdvanceAmount(0.0);
							invoiceIdWithAmount.setUsedAdvanceAmount(receiptCollection.getRemainingAdvanceAmount());
							advanceAmountToBeUsed = advanceAmountToBeUsed - receiptCollection.getRemainingAdvanceAmount();
						}
						List<InvoiceIdWithAmount> invoiceIds = receiptCollection.getInvoiceIdsWithAmount();
						if(invoiceIds == null || invoiceIds.isEmpty()){
							invoiceIds = new ArrayList<InvoiceIdWithAmount>();
						}
						
						invoiceIds.add(invoiceIdWithAmount);
						receiptCollection.setInvoiceIdsWithAmount(invoiceIds);
						receiptCollection.setUpdatedTime(new Date());
						
						if(receiptIds == null)receiptIds=new ArrayList<ObjectId>();
						receiptIds.add(receiptCollection.getId());
						doctorPatientReceiptRepository.save(receiptCollection);
					}
					}
					doctorPatientInvoiceCollection.setUsedAdvanceAmount(request.getUsedAdvanceAmount());
					doctorPatientInvoiceCollection.setBalanceAmount(doctorPatientInvoiceCollection.getGrandTotal()-
							request.getAmountPaid()-request.getUsedAdvanceAmount());
					doctorPatientReceiptCollection.setBalanceAmount(doctorPatientInvoiceCollection.getGrandTotal()-
							request.getAmountPaid()-request.getUsedAdvanceAmount());
				}else{
					doctorPatientReceiptCollection.setBalanceAmount(doctorPatientInvoiceCollection.getGrandTotal()-
							request.getAmountPaid());
					doctorPatientInvoiceCollection.setBalanceAmount(doctorPatientInvoiceCollection.getGrandTotal()-
							request.getAmountPaid());
				}
			doctorPatientReceiptCollection = doctorPatientReceiptRepository.save(doctorPatientReceiptCollection);
			
			if(receiptIds == null)receiptIds=new ArrayList<ObjectId>();
			receiptIds.add(doctorPatientReceiptCollection.getId());
			doctorPatientInvoiceCollection.setReceiptIds(receiptIds);
			doctorPatientInvoiceRepository.save(doctorPatientInvoiceCollection);
			
			if(doctorPatientInvoiceCollection != null){
				response = new DoctorPatientInvoiceAndReceiptResponse();
				BeanUtil.map(doctorPatientInvoiceCollection, response);
				
				DoctorPatientReceipt doctorPatientReceipt = new DoctorPatientReceipt();
				BeanUtil.map(doctorPatientReceiptCollection, doctorPatientReceipt);
				response.setDoctorPatientReceipt(doctorPatientReceipt);
				DoctorPatientLedgerCollection doctorPatientLedgerCollection = doctorPatientLedgerRepository.findByInvoiceId(doctorPatientInvoiceCollection.getId());
				Double balanceAmount = getBalanceAmount(null, doctorPatientReceiptCollection.getLocationId().toString(), doctorPatientReceiptCollection.getHospitalId().toString(), doctorPatientReceiptCollection.getPatientId().toString());
				
				if(doctorPatientLedgerCollection == null){
					doctorPatientLedgerCollection = new DoctorPatientLedgerCollection();
					doctorPatientLedgerCollection.setPatientId(doctorPatientInvoiceCollection.getPatientId());
					doctorPatientLedgerCollection.setLocationId(doctorPatientInvoiceCollection.getLocationId());
					doctorPatientLedgerCollection.setHospitalId(doctorPatientInvoiceCollection.getHospitalId());
					doctorPatientLedgerCollection.setInvoiceId(doctorPatientInvoiceCollection.getId());
					doctorPatientLedgerCollection.setBalanceAmount(balanceAmount+doctorPatientInvoiceCollection.getBalanceAmount());
					doctorPatientLedgerCollection.setDebitAmount(doctorPatientInvoiceCollection.getBalanceAmount());
					doctorPatientLedgerCollection.setCreatedTime(new Date());
					doctorPatientLedgerCollection.setUpdatedTime(new Date());
				}else{
					doctorPatientLedgerCollection.setBalanceAmount(balanceAmount+doctorPatientInvoiceCollection.getBalanceAmount());
					doctorPatientLedgerCollection.setDebitAmount(doctorPatientInvoiceCollection.getBalanceAmount());
					doctorPatientLedgerCollection.setUpdatedTime(new Date());
				}
				doctorPatientLedgerCollection = doctorPatientLedgerRepository.save(doctorPatientLedgerCollection);
				
				DoctorPatientLedgerCollection doctorPatientLedgerReceiptCollection = doctorPatientLedgerRepository.findByReceiptId(doctorPatientReceiptCollection.getId());
				balanceAmount = getBalanceAmount(null, doctorPatientReceiptCollection.getLocationId().toString(), doctorPatientReceiptCollection.getHospitalId().toString(), doctorPatientReceiptCollection.getPatientId().toString());
				if(doctorPatientLedgerReceiptCollection == null){
					doctorPatientLedgerReceiptCollection = new DoctorPatientLedgerCollection();
					doctorPatientLedgerReceiptCollection.setPatientId(doctorPatientReceiptCollection.getPatientId());
					doctorPatientLedgerReceiptCollection.setLocationId(doctorPatientReceiptCollection.getLocationId());
					doctorPatientLedgerReceiptCollection.setHospitalId(doctorPatientReceiptCollection.getHospitalId());
					doctorPatientLedgerReceiptCollection.setReceiptId(doctorPatientReceiptCollection.getId());
					doctorPatientLedgerReceiptCollection.setBalanceAmount(balanceAmount-doctorPatientReceiptCollection.getAmountPaid());
					doctorPatientLedgerReceiptCollection.setCreditAmount(doctorPatientReceiptCollection.getAmountPaid());
					doctorPatientLedgerReceiptCollection.setCreatedTime(new Date());
					doctorPatientLedgerReceiptCollection.setUpdatedTime(new Date());
				}else{
					doctorPatientLedgerReceiptCollection.setCreditAmount(doctorPatientReceiptCollection.getAmountPaid());
					doctorPatientLedgerReceiptCollection.setBalanceAmount(balanceAmount-doctorPatientReceiptCollection.getAmountPaid());
					doctorPatientLedgerReceiptCollection.setUpdatedTime(new Date());
				}
				doctorPatientLedgerReceiptCollection = doctorPatientLedgerRepository.save(doctorPatientLedgerReceiptCollection);
			}
		}catch(BusinessException be){
			logger.error(be);
			throw be;
		}catch(Exception e){
			logger.error("Error while adding invoice and receipt"+e);
			throw new BusinessException(ServiceError.Unknown, "Error while adding invoice and receipt"+e);
		}
		return response;
	}

	@Override
	public Double getBalanceAmount(String doctorId, String locationId, String hospitalId, String patientId) {
		Double response = 0.0;
		try{
			Criteria criteria = new Criteria("patientId").is(new ObjectId(patientId))
					.and("locationId").is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));
			
			if(!DPDoctorUtils.anyStringEmpty(doctorId))criteria.and("doctorId").is(new ObjectId(doctorId));
			List<DoctorPatientLedger> doctorPatientLedgerList = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
					Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
					Aggregation.skip(0), Aggregation.limit(1)), DoctorPatientLedgerCollection.class, DoctorPatientLedger.class).getMappedResults();
			
			if(doctorPatientLedgerList != null && !doctorPatientLedgerList.isEmpty()){
				response = doctorPatientLedgerList.get(0).getBalanceAmount();
			}
		}catch(Exception e){
			logger.error("Error while getting balance amount"+e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting balance amount"+e);
		}
		return response;
	}

	@Override
	public DoctorPatientLedgerResponse getLedger(String doctorId, String locationId, String hospitalId, String patientId,	String from, String to, int page, int size, String updatedTime) {
		DoctorPatientLedgerResponse response = null;
		try{
			long updatedTimeStamp = Long.parseLong(updatedTime);
			Criteria criteria = new Criteria("updatedTime").gte(new Date(updatedTimeStamp)).and("patientId").is(new ObjectId(patientId))
					.and("locationId").is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));
			
			if(!DPDoctorUtils.anyStringEmpty(doctorId))criteria.and("doctorId").is(new ObjectId(doctorId));
			
			if (!DPDoctorUtils.anyStringEmpty(from)) {
				criteria.and("createdTime").gte(DPDoctorUtils.getStartTime(new Date(Long.parseLong(from))));
			}
			if (!DPDoctorUtils.anyStringEmpty(to)) {
				criteria.and("createdTime").gte(DPDoctorUtils.getStartTime(new Date(Long.parseLong(from))));
			}

			Aggregation aggregation = null;
			if(size > 0){
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), 
						Aggregation.lookup("doctor_patient_invoice_cl", "invoiceId", "_id", "invoice"),
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$invoice").append("preserveNullAndEmptyArrays", true))),
						Aggregation.lookup("doctor_patient_receipt_cl", "receiptId", "_id", "receipt"),
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$receipt").append("preserveNullAndEmptyArrays", true))),
						Aggregation.skip((page) * size), Aggregation.limit(size),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}else{
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria), 
						Aggregation.lookup("doctor_patient_invoice_cl", "invoiceId", "_id", "invoice"),
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$invoice").append("preserveNullAndEmptyArrays", true))),
						Aggregation.lookup("doctor_patient_receipt_cl", "receiptId", "_id", "receipt"),
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$receipt").append("preserveNullAndEmptyArrays", true))),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));	
			}
			
			List<DoctorPatientLedger> doctorPatientLedgers = mongoTemplate.aggregate(aggregation, DoctorPatientLedgerCollection.class, DoctorPatientLedger.class).getMappedResults();
			if(doctorPatientLedgers !=  null && !doctorPatientLedgers.isEmpty()){
				response = new DoctorPatientLedgerResponse();
				response.setDoctorPatientLedgers(doctorPatientLedgers);
				if(size>0 && page == 0)response.setTotalBalanceAmount(doctorPatientLedgers.get(0).getBalanceAmount());
				else{
					List<DoctorPatientLedger> doctorPatientLedgerList = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.skip(0), Aggregation.limit(1),
							Aggregation.sort(new Sort(Direction.DESC, "createdTime"))), DoctorPatientLedgerCollection.class, DoctorPatientLedger.class).getMappedResults();
					
					response.setTotalBalanceAmount(doctorPatientLedgerList.get(0).getBalanceAmount());
				}
			}
		}catch(Exception e){
			logger.error("Error while getting ledger"+e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting ledger"+e);
		}
		return response;
	}

	@Override
	public AmountResponse getBalanceAndAdvanceAmount(String doctorId, String locationId, String hospitalId,	String patientId) {
		AmountResponse response = null;
		try{
			Criteria criteria = new Criteria("patientId").is(new ObjectId(patientId))
					.and("locationId").is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));
			
			
			List<DoctorPatientLedger> doctorPatientLedgerList = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria),
							Aggregation.sort(new Sort(Direction.DESC, "createdTime")),
					Aggregation.skip(0), Aggregation.limit(1)), DoctorPatientLedgerCollection.class, DoctorPatientLedger.class).getMappedResults();
			
			if(!DPDoctorUtils.anyStringEmpty(doctorId))criteria.and("doctorId").is(new ObjectId(doctorId));
			DoctorPatientReceipt doctorPatientReceipt = mongoTemplate.aggregate(Aggregation.newAggregation(
					Aggregation.match(criteria), Aggregation.group("patientId").sum("remainingAdvanceAmount").as("remainingAdvanceAmount")),
					DoctorPatientReceiptCollection.class, DoctorPatientReceipt.class).getUniqueMappedResult();
			
			response = new AmountResponse(); 			
			if(doctorPatientReceipt != null)response.setAdvanceAmount(doctorPatientReceipt.getRemainingAdvanceAmount());
			if(doctorPatientLedgerList != null && !doctorPatientLedgerList.isEmpty())response.setBalanceAmount(doctorPatientLedgerList.get(0).getBalanceAmount());
		}catch(Exception e){
			logger.error("Error while getting balance amount"+e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting advance & balance amount"+e);
		}
		return response;
	}
}
