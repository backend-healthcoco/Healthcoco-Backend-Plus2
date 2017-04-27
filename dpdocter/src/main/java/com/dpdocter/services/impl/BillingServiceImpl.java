package com.dpdocter.services.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.dpdocter.beans.InvoiceItemJasperDetails;
import com.dpdocter.collections.DoctorPatientDueAmountCollection;
import com.dpdocter.collections.DoctorPatientInvoiceCollection;
import com.dpdocter.collections.DoctorPatientLedgerCollection;
import com.dpdocter.collections.DoctorPatientReceiptCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.enums.BillingType;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.ReceiptType;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorPatientDueAmountRepository;
import com.dpdocter.repository.DoctorPatientInvoiceRepository;
import com.dpdocter.repository.DoctorPatientLedgerRepository;
import com.dpdocter.repository.DoctorPatientReceiptRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DoctorPatientInvoiceAndReceiptRequest;
import com.dpdocter.request.DoctorPatientReceiptRequest;
import com.dpdocter.response.AmountResponse;
import com.dpdocter.response.DoctorPatientInvoiceAndReceiptResponse;
import com.dpdocter.response.DoctorPatientLedgerResponse;
import com.dpdocter.response.DoctorPatientReceiptAddEditResponse;
import com.dpdocter.response.InvoiceItemResponse;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.services.BillingService;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.PatientVisitService;
import com.mongodb.BasicDBObject;

import common.util.web.DPDoctorUtils;

@Service
public class BillingServiceImpl implements BillingService {

	private static Logger logger = Logger.getLogger(BillingServiceImpl.class.getName());

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private DoctorPatientReceiptRepository doctorPatientReceiptRepository;

	@Autowired
	private DoctorPatientInvoiceRepository doctorPatientInvoiceRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PatientVisitService patientVisitService;

	@Autowired
	private PrintSettingsRepository printSettingsRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private PatientRepository patientRepository;

	@Value(value = "${jasper.print.receipt.a4.fileName}")
	private String receiptA4FileName;

	@Value(value = "${jasper.print.receipt.a5.fileName}")
	private String receiptA5FileName;

	@Value(value = "${jasper.print.invoice.a4.fileName}")
	private String invoiceA4FileName;

	@Value(value = "${jasper.print.invoice.a5.fileName}")
	private String invoiceA5FileName;

	@Autowired
	private JasperReportService jasperReportService;

	@Value(value = "${image.path}")
	private String imagePath;

	@Autowired
	DoctorPatientLedgerRepository doctorPatientLedgerRepository;

	@Autowired
	DoctorPatientDueAmountRepository doctorPatientDueAmountRepository;

	@Override
	public InvoiceAndReceiptInitials getInitials(String locationId) {
		InvoiceAndReceiptInitials response = null;
		try {
			LocationCollection locationCollection = locationRepository.findOne(new ObjectId(locationId));
			if (locationCollection != null) {
				response = new InvoiceAndReceiptInitials();
				response.setLocationId(locationId);
				response.setInvoiceInitial(locationCollection.getInvoiceInitial());
				response.setReceiptInitial(locationCollection.getReceiptInitial());
			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid location Id");
			}
		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			logger.error("Error while updating billing initials" + e);
			throw new BusinessException(ServiceError.Unknown, "Error while updating billing initials" + e);
		}
		return response;
	}

	@Override
	public InvoiceAndReceiptInitials updateInitials(InvoiceAndReceiptInitials request) {
		InvoiceAndReceiptInitials response = null;
		try {
			LocationCollection locationCollection = locationRepository.findOne(new ObjectId(request.getLocationId()));
			if (locationCollection != null) {
				locationCollection.setInvoiceInitial(request.getInvoiceInitial());
				locationCollection.setReceiptInitial(request.getReceiptInitial());
				locationCollection.setUpdatedTime(new Date());
				locationCollection = locationRepository.save(locationCollection);
				response = new InvoiceAndReceiptInitials();
				BeanUtil.map(locationCollection, response);
				response.setLocationId(locationCollection.getId().toString());
			} else {
				throw new BusinessException(ServiceError.InvalidInput, "Invalid location Id");
			}
		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			logger.error("Error while updating billing initials" + e);
			throw new BusinessException(ServiceError.Unknown, "Error while updating billing initials" + e);
		}
		return response;
	}

	@Override
	public DoctorPatientInvoice addEditInvoice(DoctorPatientInvoice request) {
		DoctorPatientInvoice response = null;
		try {
			Map<String, UserCollection> doctorsMap = new HashMap<String, UserCollection>();
			DoctorPatientInvoiceCollection doctorPatientInvoiceCollection = new DoctorPatientInvoiceCollection();
			ObjectId doctorObjectId = new ObjectId(request.getDoctorId());
			Double dueAmount = 0.0;
			if (DPDoctorUtils.anyStringEmpty(request.getId())) {
				BeanUtil.map(request, doctorPatientInvoiceCollection);
				UserCollection userCollection = userRepository.findOne(doctorObjectId);
				if (userCollection != null) {
					doctorPatientInvoiceCollection
							.setCreatedBy((!DPDoctorUtils.anyStringEmpty(userCollection.getTitle())
									? userCollection.getTitle() + " " : "") + userCollection.getFirstName());
					doctorsMap.put(request.getDoctorId(), userCollection);
				}
				doctorPatientInvoiceCollection.setCreatedTime(new Date());
				LocationCollection locationCollection = locationRepository
						.findOne(new ObjectId(request.getLocationId()));
				if (locationCollection == null)
					throw new BusinessException(ServiceError.InvalidInput, "Invalid Location Id");
				doctorPatientInvoiceCollection
						.setUniqueInvoiceId(
								locationCollection.getInvoiceInitial()
										+ ((int) mongoTemplate.count(
												new Query(new Criteria("locationId")
														.is(doctorPatientInvoiceCollection.getLocationId())
														.and("hospitalId")
														.is(doctorPatientInvoiceCollection.getHospitalId())),
												DoctorPatientInvoiceCollection.class) + 1));
				doctorPatientInvoiceCollection.setBalanceAmount(request.getGrandTotal());
				dueAmount = doctorPatientInvoiceCollection.getBalanceAmount();
			} else {
				doctorPatientInvoiceCollection = doctorPatientInvoiceRepository.findOne(new ObjectId(request.getId()));
				if (doctorPatientInvoiceCollection.getReceiptIds() != null
						&& !doctorPatientInvoiceCollection.getReceiptIds().isEmpty()) {
					throw new BusinessException(ServiceError.Unknown,
							"Invoice cannot be edited as receipt is already added.");
				}
				dueAmount = -doctorPatientInvoiceCollection.getBalanceAmount();
				doctorPatientInvoiceCollection.setUpdatedTime(new Date());
				doctorPatientInvoiceCollection.setTotalCost(request.getTotalCost());
				doctorPatientInvoiceCollection.setTotalDiscount(request.getTotalDiscount());
				doctorPatientInvoiceCollection.setTotalTax(request.getTotalTax());
				doctorPatientInvoiceCollection
						.setBalanceAmount((request.getGrandTotal() - doctorPatientInvoiceCollection.getGrandTotal())
								+ doctorPatientInvoiceCollection.getBalanceAmount());
				doctorPatientInvoiceCollection.setGrandTotal(request.getGrandTotal());
				if (doctorPatientInvoiceCollection.getBalanceAmount() < 0) {
					doctorPatientInvoiceCollection
							.setRefundAmount(doctorPatientInvoiceCollection.getBalanceAmount() * (-1));
					doctorPatientInvoiceCollection.setBalanceAmount(0.0);
				}
				dueAmount = dueAmount + doctorPatientInvoiceCollection.getBalanceAmount();
			}
			List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
			for (InvoiceItemResponse invoiceItemResponse : request.getInvoiceItems()) {
				if (DPDoctorUtils.anyStringEmpty(invoiceItemResponse.getDoctorId())) {
					invoiceItemResponse.setDoctorId(request.getDoctorId());
					invoiceItemResponse.setDoctorName(doctorPatientInvoiceCollection.getCreatedBy());
				} else {
					if (invoiceItemResponse.getDoctorId().toString().equalsIgnoreCase(request.getDoctorId()))
						invoiceItemResponse.setDoctorName(doctorPatientInvoiceCollection.getCreatedBy());
					else {
						UserCollection doctor = doctorsMap.get(invoiceItemResponse.getDoctorId().toString());
						if (doctor == null) {
							doctor = userRepository.findOne(new ObjectId(invoiceItemResponse.getDoctorId()));
							doctorsMap.put(invoiceItemResponse.getDoctorId(), doctor);
						}
						invoiceItemResponse.setDoctorName(
								(!DPDoctorUtils.anyStringEmpty(doctor.getTitle()) ? doctor.getTitle() + " " : "")
										+ doctor.getFirstName());
					}
				}
				InvoiceItem invoiceItem = new InvoiceItem();

				BeanUtil.map(invoiceItemResponse, invoiceItem);
				invoiceItems.add(invoiceItem);
				doctorPatientInvoiceCollection.setInvoiceItems(invoiceItems);
			}
			if (doctorPatientInvoiceCollection != null) {
				doctorPatientInvoiceCollection = doctorPatientInvoiceRepository.save(doctorPatientInvoiceCollection);
				response = new DoctorPatientInvoice();
				BeanUtil.map(doctorPatientInvoiceCollection, response);

				DoctorPatientLedgerCollection doctorPatientLedgerCollection = doctorPatientLedgerRepository
						.findByInvoiceId(doctorPatientInvoiceCollection.getId());
				if (doctorPatientLedgerCollection == null) {
					doctorPatientLedgerCollection = new DoctorPatientLedgerCollection();
					doctorPatientLedgerCollection.setPatientId(doctorPatientInvoiceCollection.getPatientId());
					doctorPatientLedgerCollection.setLocationId(doctorPatientInvoiceCollection.getLocationId());
					doctorPatientLedgerCollection.setHospitalId(doctorPatientInvoiceCollection.getHospitalId());
					doctorPatientLedgerCollection.setInvoiceId(doctorPatientInvoiceCollection.getId());
					// doctorPatientLedgerCollection.setDueAmount(balanceAmount
					// + doctorPatientInvoiceCollection.getBalanceAmount());
					doctorPatientLedgerCollection.setDebitAmount(doctorPatientInvoiceCollection.getBalanceAmount());
					doctorPatientLedgerCollection.setCreatedTime(new Date());
					doctorPatientLedgerCollection.setUpdatedTime(new Date());
				} else {
					// doctorPatientLedgerCollection.setDueAmount(balanceAmount
					// + doctorPatientInvoiceCollection.getBalanceAmount());
					doctorPatientLedgerCollection.setDebitAmount(doctorPatientInvoiceCollection.getBalanceAmount());
					doctorPatientLedgerCollection.setUpdatedTime(new Date());
				}
				doctorPatientLedgerCollection = doctorPatientLedgerRepository.save(doctorPatientLedgerCollection);
				DoctorPatientDueAmountCollection doctorPatientDueAmountCollection = doctorPatientDueAmountRepository
						.find(doctorPatientInvoiceCollection.getPatientId(), doctorPatientInvoiceCollection.getDoctorId(), doctorPatientInvoiceCollection.getLocationId(),
								doctorPatientInvoiceCollection.getHospitalId());
				if(doctorPatientDueAmountCollection == null){
					doctorPatientDueAmountCollection = new DoctorPatientDueAmountCollection();
					doctorPatientDueAmountCollection.setDoctorId(doctorPatientInvoiceCollection.getDoctorId());
					doctorPatientDueAmountCollection.setHospitalId(doctorPatientInvoiceCollection.getHospitalId());
					doctorPatientDueAmountCollection.setLocationId(doctorPatientInvoiceCollection.getLocationId());
					doctorPatientDueAmountCollection.setPatientId(doctorPatientInvoiceCollection.getPatientId());
					doctorPatientDueAmountCollection.setDueAmount(0.0);
				}
				doctorPatientDueAmountCollection.setDueAmount(doctorPatientDueAmountCollection.getDueAmount()+dueAmount);
				doctorPatientDueAmountRepository.save(doctorPatientDueAmountCollection);
			}
		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			logger.error("Error while adding invoice" + e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while adding invoice" + e);
		}
		return response;
	}

	@Override
	public DoctorPatientInvoice getInvoice(String invoiceId) {
		DoctorPatientInvoice response = null;
		try {
			DoctorPatientInvoiceCollection doctorPatientInvoiceCollection = doctorPatientInvoiceRepository
					.findOne(new ObjectId(invoiceId));
			if (doctorPatientInvoiceCollection != null) {
				response = new DoctorPatientInvoice();
				BeanUtil.map(doctorPatientInvoiceCollection, response);
			}

		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			logger.error("Error while getting invoice" + e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting invoice" + e);
		}
		return response;
	}

	@Override
	public List<DoctorPatientInvoice> getInvoices(String type, int page, int size, String doctorId, String locationId,
			String hospitalId, String patientId, String updatedTime, Boolean discarded) {
		List<DoctorPatientInvoice> responses = null;
		try {
			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));

			if (!DPDoctorUtils.anyStringEmpty(patientId))
				criteria.and("patientId").is(new ObjectId(patientId));
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
				criteria.and("locationId").is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));
			if (!discarded)
				criteria.and("discarded").is(discarded);

			switch (BillingType.valueOf(type.toUpperCase())) {
			case SETTLE:
				criteria.and("balanceAmount").is(0.0);
				break;
			case NONSETTLE:
				criteria.and("balanceAmount").gt(0.0);
				break;
			case BOTH:
				break;
			}
			if (size > 0) {
				responses = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
								Aggregation.skip((page) * size), Aggregation.limit(size)),
						DoctorPatientInvoiceCollection.class, DoctorPatientInvoice.class).getMappedResults();
			} else {
				responses = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime"))),
						DoctorPatientInvoiceCollection.class, DoctorPatientInvoice.class).getMappedResults();
			}

		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			logger.error("Error while getting invoices" + e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting invoices" + e);
		}
		return responses;
	}

	@Override
	public DoctorPatientInvoice deleteInvoice(String invoiceId, Boolean discarded) {
		DoctorPatientInvoice response = null;
		try {
			DoctorPatientInvoiceCollection doctorPatientInvoiceCollection = doctorPatientInvoiceRepository
					.findOne(new ObjectId(invoiceId));
			doctorPatientInvoiceCollection.setDiscarded(discarded);
			doctorPatientInvoiceCollection.setUpdatedTime(new Date());
			doctorPatientInvoiceCollection = doctorPatientInvoiceRepository.save(doctorPatientInvoiceCollection);
			if (doctorPatientInvoiceCollection != null) {
				response = new DoctorPatientInvoice();
				BeanUtil.map(doctorPatientInvoiceCollection, response);
			}
		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			logger.error("Error while deleting invoices" + e);
			throw new BusinessException(ServiceError.Unknown, "Error while deleting invoices" + e);
		}
		return response;
	}

	@Override
	public DoctorPatientReceiptAddEditResponse addEditReceipt(DoctorPatientReceiptRequest request) {
		DoctorPatientReceiptAddEditResponse response = null;
		DoctorPatientReceipt receipt = null;
		DoctorPatientInvoice invoice = null;
		try {
			Double dueAmount = 0.0;
			DoctorPatientReceiptCollection doctorPatientReceiptCollection = new DoctorPatientReceiptCollection();
			if (DPDoctorUtils.anyStringEmpty(request.getId())) {
				BeanUtil.map(request, doctorPatientReceiptCollection);
				UserCollection userCollection = userRepository.findOne(new ObjectId(request.getDoctorId()));
				if (userCollection != null) {
					doctorPatientReceiptCollection
							.setCreatedBy((!DPDoctorUtils.anyStringEmpty(userCollection.getTitle())
									? userCollection.getTitle() + " " : "") + userCollection.getFirstName());
				}
				doctorPatientReceiptCollection.setCreatedTime(new Date());
				LocationCollection locationCollection = locationRepository
						.findOne(new ObjectId(request.getLocationId()));
				if (locationCollection == null)
					throw new BusinessException(ServiceError.InvalidInput, "Invalid Location Id");
				doctorPatientReceiptCollection
						.setUniqueReceiptId(
								locationCollection.getReceiptInitial()
										+ ((int) mongoTemplate.count(
												new Query(new Criteria("locationId")
														.is(doctorPatientReceiptCollection.getLocationId())
														.and("hospitalId")
														.is(doctorPatientReceiptCollection.getHospitalId())),
												DoctorPatientReceiptCollection.class) + 1));
				dueAmount = request.getAmountPaid() != null ? request.getAmountPaid() : 0.0;
			} else {
				doctorPatientReceiptCollection = doctorPatientReceiptRepository.findOne(new ObjectId(request.getId()));
				dueAmount = (request.getAmountPaid() != null ? request.getAmountPaid() : 0.0) - doctorPatientReceiptCollection.getAmountPaid();
			}
			if (doctorPatientReceiptCollection.getReceiptType().name().equalsIgnoreCase(ReceiptType.ADVANCE.name())) {
				doctorPatientReceiptCollection.setRemainingAdvanceAmount(request.getAmountPaid());
				doctorPatientReceiptCollection.setBalanceAmount(0.0);
				doctorPatientReceiptCollection = doctorPatientReceiptRepository.save(doctorPatientReceiptCollection);
			} else if (doctorPatientReceiptCollection.getReceiptType().name()
					.equalsIgnoreCase(ReceiptType.INVOICE.name())) {

				if (request.getInvoiceIds() != null && !request.getInvoiceIds().isEmpty()) {
					DoctorPatientInvoiceCollection doctorPatientInvoiceCollection = doctorPatientInvoiceRepository
							.findOne(new ObjectId(request.getInvoiceIds().get(0)));
					if (doctorPatientInvoiceCollection == null) {
						throw new BusinessException(ServiceError.InvalidInput, "Invalid Invoice Id");
					}
					List<ObjectId> receiptIds = doctorPatientInvoiceCollection.getReceiptIds();
					if (request.getUsedAdvanceAmount() != null && request.getUsedAdvanceAmount() > 0) {

						List<DoctorPatientReceiptCollection> receiptsOfAdvancePayment = doctorPatientReceiptRepository
								.findAvailableAdvanceReceipts(ReceiptType.ADVANCE.name(),
										doctorPatientInvoiceCollection.getDoctorId(),
										doctorPatientInvoiceCollection.getLocationId(),
										doctorPatientInvoiceCollection.getHospitalId(),
										doctorPatientInvoiceCollection.getPatientId(),
										new Sort(Direction.ASC, "createdTime"));
						if (receiptsOfAdvancePayment == null || receiptsOfAdvancePayment.isEmpty())
							throw new BusinessException(ServiceError.InvalidInput, "Advance Amount is not available");

						Double advanceAmountToBeUsed = request.getUsedAdvanceAmount() != null ? request.getUsedAdvanceAmount() : 0.0;
						for (DoctorPatientReceiptCollection receiptCollection : receiptsOfAdvancePayment) {
							InvoiceIdWithAmount invoiceIdWithAmount = new InvoiceIdWithAmount();
							invoiceIdWithAmount.setUniqueInvoiceId(doctorPatientInvoiceCollection.getUniqueInvoiceId());
							invoiceIdWithAmount.setInvoiceId(doctorPatientInvoiceCollection.getId());
							if (advanceAmountToBeUsed > 0.0) {
								if (receiptCollection.getRemainingAdvanceAmount() > advanceAmountToBeUsed) {
									receiptCollection.setRemainingAdvanceAmount(
											receiptCollection.getRemainingAdvanceAmount() - advanceAmountToBeUsed);
									invoiceIdWithAmount.setUsedAdvanceAmount(advanceAmountToBeUsed);
									advanceAmountToBeUsed = 0.0;
								} else {
									receiptCollection.setRemainingAdvanceAmount(0.0);
									invoiceIdWithAmount
											.setUsedAdvanceAmount(receiptCollection.getRemainingAdvanceAmount());
									advanceAmountToBeUsed = advanceAmountToBeUsed
											- receiptCollection.getRemainingAdvanceAmount();
								}
								List<InvoiceIdWithAmount> invoiceIds = receiptCollection.getInvoiceIdsWithAmount();
								if (invoiceIds == null || invoiceIds.isEmpty()) {
									invoiceIds = new ArrayList<InvoiceIdWithAmount>();
								}

								invoiceIds.add(invoiceIdWithAmount);
								receiptCollection.setInvoiceIdsWithAmount(invoiceIds);
								receiptCollection.setUpdatedTime(new Date());

								doctorPatientReceiptRepository.save(receiptCollection);
								if (receiptIds == null)
									receiptIds = new ArrayList<ObjectId>();
								receiptIds.add(receiptCollection.getId());
							}
						}
						doctorPatientInvoiceCollection.setUsedAdvanceAmount(
								doctorPatientInvoiceCollection.getUsedAdvanceAmount() + (request.getUsedAdvanceAmount() != null ? request.getUsedAdvanceAmount() : 0.0));
						doctorPatientReceiptCollection
								.setUsedAdvanceAmount(doctorPatientInvoiceCollection.getUsedAdvanceAmount());
						doctorPatientInvoiceCollection
								.setBalanceAmount(doctorPatientInvoiceCollection.getBalanceAmount()
										- (request.getAmountPaid() != null ? request.getAmountPaid() : 0.0) - (request.getUsedAdvanceAmount() != null ? request.getUsedAdvanceAmount() : 0.0));
						doctorPatientReceiptCollection
								.setBalanceAmount(doctorPatientInvoiceCollection.getBalanceAmount());
					} else {
						doctorPatientInvoiceCollection.setUsedAdvanceAmount(
								doctorPatientInvoiceCollection.getUsedAdvanceAmount() + (request.getUsedAdvanceAmount() != null ? request.getUsedAdvanceAmount() : 0.0));
						doctorPatientReceiptCollection
								.setUsedAdvanceAmount(doctorPatientInvoiceCollection.getUsedAdvanceAmount());
						doctorPatientInvoiceCollection
								.setBalanceAmount(doctorPatientInvoiceCollection.getBalanceAmount()
										- (request.getAmountPaid() != null ? request.getAmountPaid() : 0.0) - (request.getUsedAdvanceAmount() != null ? request.getUsedAdvanceAmount() : 0.0));
						doctorPatientReceiptCollection
								.setBalanceAmount(doctorPatientInvoiceCollection.getBalanceAmount());
					}
					doctorPatientReceiptCollection
							.setUniqueInvoiceId(doctorPatientInvoiceCollection.getUniqueInvoiceId());
					doctorPatientReceiptCollection.setInvoiceId(doctorPatientInvoiceCollection.getId());
					doctorPatientReceiptCollection = doctorPatientReceiptRepository
							.save(doctorPatientReceiptCollection);

					if (receiptIds == null)
						receiptIds = new ArrayList<ObjectId>();
					receiptIds.add(doctorPatientReceiptCollection.getId());
					doctorPatientInvoiceCollection.setReceiptIds(receiptIds);
					doctorPatientInvoiceCollection.setUpdatedTime(new Date());
					doctorPatientInvoiceRepository.save(doctorPatientInvoiceCollection);
					invoice = new DoctorPatientInvoice();
					BeanUtil.map(doctorPatientInvoiceCollection, invoice);
				} else {
					throw new BusinessException(ServiceError.InvalidInput, "Invoice Id cannot be null");
				}
			}
			if (doctorPatientReceiptCollection != null) {
				response = new DoctorPatientReceiptAddEditResponse();
				receipt = new DoctorPatientReceipt();
				BeanUtil.map(doctorPatientReceiptCollection, receipt);
				response.setInvoice(invoice);
				response.setDoctorPatientReceipt(receipt);
				DoctorPatientLedgerCollection doctorPatientLedgerCollection = doctorPatientLedgerRepository
						.findByReceiptId(doctorPatientReceiptCollection.getId());
				if (doctorPatientLedgerCollection == null) {
					doctorPatientLedgerCollection = new DoctorPatientLedgerCollection();
					doctorPatientLedgerCollection.setPatientId(doctorPatientReceiptCollection.getPatientId());
					doctorPatientLedgerCollection.setLocationId(doctorPatientReceiptCollection.getLocationId());
					doctorPatientLedgerCollection.setHospitalId(doctorPatientReceiptCollection.getHospitalId());
					doctorPatientLedgerCollection.setReceiptId(doctorPatientReceiptCollection.getId());
					// doctorPatientLedgerCollection
					// .setDueAmount(balanceAmount -
					// doctorPatientReceiptCollection.getAmountPaid());
					doctorPatientLedgerCollection.setCreditAmount(doctorPatientReceiptCollection.getAmountPaid());
					doctorPatientLedgerCollection.setCreatedTime(new Date());
					doctorPatientLedgerCollection.setUpdatedTime(new Date());
				} else {
					doctorPatientLedgerCollection.setCreditAmount(doctorPatientReceiptCollection.getAmountPaid());
					// doctorPatientLedgerCollection
					// .setDueAmount(balanceAmount -
					// doctorPatientReceiptCollection.getAmountPaid());
					doctorPatientLedgerCollection.setUpdatedTime(new Date());
				}
				doctorPatientLedgerCollection = doctorPatientLedgerRepository.save(doctorPatientLedgerCollection);

				DoctorPatientDueAmountCollection doctorPatientDueAmountCollection = doctorPatientDueAmountRepository
						.find(doctorPatientReceiptCollection.getPatientId(),
								doctorPatientReceiptCollection.getDoctorId(),
								doctorPatientReceiptCollection.getLocationId(),
								doctorPatientReceiptCollection.getHospitalId());
				if (doctorPatientDueAmountCollection == null) {
					doctorPatientDueAmountCollection = new DoctorPatientDueAmountCollection();
					doctorPatientDueAmountCollection.setDoctorId(doctorPatientReceiptCollection.getDoctorId());
					doctorPatientDueAmountCollection.setHospitalId(doctorPatientReceiptCollection.getHospitalId());
					doctorPatientDueAmountCollection.setLocationId(doctorPatientReceiptCollection.getLocationId());
					doctorPatientDueAmountCollection.setPatientId(doctorPatientReceiptCollection.getPatientId());
				}
				doctorPatientDueAmountCollection
						.setDueAmount(doctorPatientDueAmountCollection.getDueAmount() - dueAmount);
				doctorPatientDueAmountRepository.save(doctorPatientDueAmountCollection);

				AmountResponse amountResponse = getTotalDueAndAdvanceAmount(request.getDoctorId(),
						request.getLocationId(), request.getHospitalId(), request.getPatientId());

				if (amountResponse != null) {
					response.setTotalRemainingAdvanceAmount(amountResponse.getTotalRemainingAdvanceAmount());
					response.setTotalDueAmount(amountResponse.getTotalDueAmount());
				}
			}
		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while adding receipts" + e);
			throw new BusinessException(ServiceError.Unknown, "Error while adding receipts" + e);
		}
		return response;
	}

	@Override
	public List<DoctorPatientReceipt> getReceipts(int page, int size, String doctorId, String locationId,
			String hospitalId, String patientId, String updatedTime, Boolean discarded) {
		List<DoctorPatientReceipt> responses = null;
		try {
			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));

			if (!DPDoctorUtils.anyStringEmpty(patientId))
				criteria.and("patientId").is(new ObjectId(patientId));
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
				criteria.and("locationId").is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));
			if (!discarded)
				criteria.and("discarded").is(discarded);

			if (size > 0) {
				responses = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								// Aggregation.lookup("doctor_patient_invoice_cl",
								// "invoiceId", "_id", "invoice"),
								// new CustomAggregationOperation(new
								// BasicDBObject("$unwind",
								// new BasicDBObject("path",
								// "$invoice").append("preserveNullAndEmptyArrays",
								// true))),
								Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")),
								Aggregation.skip((page) * size), Aggregation.limit(size)),
						DoctorPatientReceiptCollection.class, DoctorPatientReceipt.class).getMappedResults();
			} else {
				responses = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								// Aggregation.lookup("doctor_patient_invoice_cl",
								// "invoiceId", "_id", "invoice"),
								// new CustomAggregationOperation(new
								// BasicDBObject("$unwind",
								// new BasicDBObject("path",
								// "$invoice").append("preserveNullAndEmptyArrays",
								// true))),
								Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime"))),
						DoctorPatientReceiptCollection.class, DoctorPatientReceipt.class).getMappedResults();
			}
		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			logger.error("Error while getting receipts" + e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting receipts" + e);
		}
		return responses;
	}

	@Override
	public int getReceiptCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, Boolean isOTPVerified) {
		int response = 0;
		try {
			if (isOTPVerified)
			response = doctorPatientReceiptRepository.countByPatientId(patientObjectId);
		else
			response = doctorPatientReceiptRepository.countByPatientIdDoctorLocationHospital(patientObjectId,
					doctorObjectId, locationObjectId, hospitalObjectId);

		}
		catch (Exception e) {
			logger.error("Error while getting receipts count" + e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting receipts count" + e);
		}
		return response;
	}
	
	@Override
	public int getInvoiceCount(ObjectId doctorObjectId, ObjectId patientObjectId, ObjectId locationObjectId,
			ObjectId hospitalObjectId, Boolean isOTPVerified) {
		int response = 0;
		try {
			if (isOTPVerified)
			response = doctorPatientInvoiceRepository.countByPatientId(patientObjectId);
		else
			response = doctorPatientInvoiceRepository.countByPatientIdDoctorLocationHospital(patientObjectId,
					doctorObjectId, locationObjectId, hospitalObjectId);

		}
		catch (Exception e) {
			logger.error("Error while getting invoice count" + e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting invoice count" + e);
		}
		return response;
	}

	@Override
	public DoctorPatientReceipt deleteReceipt(String receiptId, Boolean discarded) {
		DoctorPatientReceipt response = null;
		try {
			Update update = new Update();
			update.addToSet("discarded", discarded);
			DoctorPatientReceiptCollection doctorPatientReceiptCollection = mongoTemplate.findAndModify(
					new Query(new Criteria("id").is(new ObjectId(receiptId))), update,
					DoctorPatientReceiptCollection.class);
			// doctorPatientReceiptRepository.findOne(new ObjectId(receiptId));

			if (doctorPatientReceiptCollection != null) {
				response = new DoctorPatientReceipt();
				BeanUtil.map(doctorPatientReceiptCollection, response);
			}
		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			logger.error("Error while deleting receipt " + e);
			throw new BusinessException(ServiceError.Unknown, "Error while deleting receipt" + e);
		}
		return response;
	}

	@Override
	public Double getAvailableAdvanceAmount(String doctorId, String locationId, String hospitalId, String patientId) {
		Double response = 0.0;
		try {
			Criteria criteria = new Criteria("patientId").is(new ObjectId(patientId)).and("locationId")
					.is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));

			DoctorPatientReceipt doctorPatientReceipt = mongoTemplate
					.aggregate(
							Aggregation.newAggregation(Aggregation.match(criteria),
									Aggregation.group("patientId").sum("remainingAdvanceAmount")
											.as("remainingAdvanceAmount")),
							DoctorPatientReceiptCollection.class, DoctorPatientReceipt.class)
					.getUniqueMappedResult();
			if (doctorPatientReceipt != null)
				response = doctorPatientReceipt.getRemainingAdvanceAmount();
		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			logger.error("Error while getting available advance amount " + e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting available advance amount" + e);
		}
		return response;
	}

	@Override
	public DoctorPatientInvoiceAndReceiptResponse addInvoiceAndPay(DoctorPatientInvoiceAndReceiptRequest request) {
		DoctorPatientInvoiceAndReceiptResponse response = null;
		try {
			Map<String, UserCollection> doctorsMap = new HashMap<String, UserCollection>();
			DoctorPatientInvoiceCollection doctorPatientInvoiceCollection = new DoctorPatientInvoiceCollection();
			ObjectId doctorObjectId = new ObjectId(request.getDoctorId());

			BeanUtil.map(request, doctorPatientInvoiceCollection);
			UserCollection userCollection = userRepository.findOne(doctorObjectId);
			if (userCollection != null) {
				doctorPatientInvoiceCollection.setCreatedBy((!DPDoctorUtils.anyStringEmpty(userCollection.getTitle())
						? userCollection.getTitle() + " " : "") + userCollection.getFirstName());
				doctorsMap.put(request.getDoctorId(), userCollection);
			}
			doctorPatientInvoiceCollection.setCreatedTime(new Date());
			LocationCollection locationCollection = locationRepository.findOne(new ObjectId(request.getLocationId()));
			if (locationCollection == null)
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Location Id");
			doctorPatientInvoiceCollection
					.setUniqueInvoiceId(
							locationCollection.getInvoiceInitial()
									+ ((int) mongoTemplate.count(
											new Query(new Criteria("locationId")
													.is(doctorPatientInvoiceCollection.getLocationId())
													.and("hospitalId")
													.is(doctorPatientInvoiceCollection.getHospitalId())),
											DoctorPatientInvoiceCollection.class) + 1));
			List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
			for (InvoiceItemResponse invoiceItemResponse : request.getInvoiceItems()) {
				if (DPDoctorUtils.anyStringEmpty(invoiceItemResponse.getDoctorId())) {
					invoiceItemResponse.setDoctorId(request.getDoctorId());
					invoiceItemResponse.setDoctorName(doctorPatientInvoiceCollection.getCreatedBy());
				} else {
					if (invoiceItemResponse.getDoctorId().toString().equalsIgnoreCase(request.getDoctorId()))
						invoiceItemResponse.setDoctorName(doctorPatientInvoiceCollection.getCreatedBy());
					else {
						UserCollection doctor = doctorsMap.get(invoiceItemResponse.getDoctorId().toString());
						if (doctor == null) {
							doctor = userRepository.findOne(new ObjectId(invoiceItemResponse.getDoctorId()));
							doctorsMap.put(invoiceItemResponse.getDoctorId(), doctor);
						}
						invoiceItemResponse.setDoctorName(
								(!DPDoctorUtils.anyStringEmpty(doctor.getTitle()) ? doctor.getTitle() + " " : "")
										+ doctor.getFirstName());
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
			doctorPatientReceiptCollection.setCreatedBy(
					(!DPDoctorUtils.anyStringEmpty(userCollection.getTitle()) ? userCollection.getTitle() + " " : "")
							+ userCollection.getFirstName());
			doctorPatientReceiptCollection.setCreatedTime(new Date());
			doctorPatientReceiptCollection
					.setUniqueReceiptId(
							locationCollection.getReceiptInitial()
									+ ((int) mongoTemplate.count(
											new Query(new Criteria("locationId")
													.is(doctorPatientReceiptCollection.getLocationId())
													.and("hospitalId")
													.is(doctorPatientReceiptCollection.getHospitalId())),
											DoctorPatientReceiptCollection.class) + 1));

			doctorPatientReceiptCollection.setReceiptType(ReceiptType.INVOICE);

			InvoiceIdWithAmount invoiceIdWithAmount = new InvoiceIdWithAmount();
			invoiceIdWithAmount.setUniqueInvoiceId(doctorPatientInvoiceCollection.getUniqueInvoiceId());
			invoiceIdWithAmount.setInvoiceId(doctorPatientInvoiceCollection.getId());
			doctorPatientReceiptCollection.setInvoiceIdsWithAmount(Arrays.asList(invoiceIdWithAmount));

			if (request.getUsedAdvanceAmount() != null && request.getUsedAdvanceAmount() > 0) {

				List<DoctorPatientReceiptCollection> receiptsOfAdvancePayment = doctorPatientReceiptRepository
						.findAvailableAdvanceReceipts(ReceiptType.ADVANCE.name(),
								doctorPatientInvoiceCollection.getDoctorId(),
								doctorPatientInvoiceCollection.getLocationId(),
								doctorPatientInvoiceCollection.getHospitalId(),
								doctorPatientInvoiceCollection.getPatientId(), new Sort(Direction.ASC, "createdTime"));
				if (receiptsOfAdvancePayment == null || receiptsOfAdvancePayment.isEmpty())
					throw new BusinessException(ServiceError.InvalidInput, "Advance Amount is not available");

				Double advanceAmountToBeUsed = request.getUsedAdvanceAmount() != null ? request.getUsedAdvanceAmount() : 0.0;
				for (DoctorPatientReceiptCollection receiptCollection : receiptsOfAdvancePayment) {
					invoiceIdWithAmount = new InvoiceIdWithAmount();
					invoiceIdWithAmount.setUniqueInvoiceId(doctorPatientInvoiceCollection.getUniqueInvoiceId());
					invoiceIdWithAmount.setInvoiceId(doctorPatientInvoiceCollection.getId());
					if (advanceAmountToBeUsed > 0.0) {
						if (receiptCollection.getRemainingAdvanceAmount() > advanceAmountToBeUsed) {
							receiptCollection.setRemainingAdvanceAmount(
									receiptCollection.getRemainingAdvanceAmount() - advanceAmountToBeUsed);
							invoiceIdWithAmount.setUsedAdvanceAmount(advanceAmountToBeUsed);
							advanceAmountToBeUsed = 0.0;
						} else {
							receiptCollection.setRemainingAdvanceAmount(0.0);
							invoiceIdWithAmount.setUsedAdvanceAmount(receiptCollection.getRemainingAdvanceAmount());
							advanceAmountToBeUsed = advanceAmountToBeUsed
									- receiptCollection.getRemainingAdvanceAmount();
						}
						List<InvoiceIdWithAmount> invoiceIds = receiptCollection.getInvoiceIdsWithAmount();
						if (invoiceIds == null || invoiceIds.isEmpty()) {
							invoiceIds = new ArrayList<InvoiceIdWithAmount>();
						}

						invoiceIds.add(invoiceIdWithAmount);
						receiptCollection.setInvoiceIdsWithAmount(invoiceIds);
						receiptCollection.setUpdatedTime(new Date());

						if (receiptIds == null)
							receiptIds = new ArrayList<ObjectId>();
						receiptIds.add(receiptCollection.getId());
						doctorPatientReceiptRepository.save(receiptCollection);
					}
				}
				doctorPatientInvoiceCollection.setUsedAdvanceAmount(
						doctorPatientInvoiceCollection.getUsedAdvanceAmount() + (request.getUsedAdvanceAmount() != null ? request.getUsedAdvanceAmount() : 0.0));
				doctorPatientReceiptCollection
						.setUsedAdvanceAmount(doctorPatientInvoiceCollection.getUsedAdvanceAmount());
				doctorPatientInvoiceCollection.setBalanceAmount(doctorPatientInvoiceCollection.getBalanceAmount()
						- (request.getAmountPaid() != null ? request.getAmountPaid() : 0.0) - (request.getUsedAdvanceAmount() != null ? request.getUsedAdvanceAmount() : 0.0));
				doctorPatientReceiptCollection.setBalanceAmount(doctorPatientInvoiceCollection.getBalanceAmount());
			} else {
				doctorPatientInvoiceCollection.setUsedAdvanceAmount(
						doctorPatientInvoiceCollection.getUsedAdvanceAmount() + (request.getUsedAdvanceAmount() != null ? request.getUsedAdvanceAmount() : 0.0));
				doctorPatientReceiptCollection
						.setUsedAdvanceAmount(doctorPatientInvoiceCollection.getUsedAdvanceAmount());
				doctorPatientInvoiceCollection.setBalanceAmount(doctorPatientInvoiceCollection.getBalanceAmount()
						- (request.getAmountPaid() != null ? request.getAmountPaid() : 0.0) - (request.getUsedAdvanceAmount() != null ? request.getUsedAdvanceAmount() : 0.0));
				doctorPatientReceiptCollection.setBalanceAmount(doctorPatientInvoiceCollection.getBalanceAmount());
			}
			doctorPatientReceiptCollection = doctorPatientReceiptRepository.save(doctorPatientReceiptCollection);

			if (receiptIds == null)
				receiptIds = new ArrayList<ObjectId>();
			receiptIds.add(doctorPatientReceiptCollection.getId());
			doctorPatientInvoiceCollection.setReceiptIds(receiptIds);
			doctorPatientInvoiceRepository.save(doctorPatientInvoiceCollection);

			if (doctorPatientInvoiceCollection != null) {
				response = new DoctorPatientInvoiceAndReceiptResponse();
				BeanUtil.map(doctorPatientInvoiceCollection, response);

				DoctorPatientReceipt doctorPatientReceipt = new DoctorPatientReceipt();
				BeanUtil.map(doctorPatientReceiptCollection, doctorPatientReceipt);
				response.setDoctorPatientReceipt(doctorPatientReceipt);
				DoctorPatientLedgerCollection doctorPatientLedgerCollection = doctorPatientLedgerRepository
						.findByInvoiceId(doctorPatientInvoiceCollection.getId());

				if (doctorPatientLedgerCollection == null) {
					doctorPatientLedgerCollection = new DoctorPatientLedgerCollection();
					doctorPatientLedgerCollection.setPatientId(doctorPatientInvoiceCollection.getPatientId());
					doctorPatientLedgerCollection.setLocationId(doctorPatientInvoiceCollection.getLocationId());
					doctorPatientLedgerCollection.setHospitalId(doctorPatientInvoiceCollection.getHospitalId());
					doctorPatientLedgerCollection.setInvoiceId(doctorPatientInvoiceCollection.getId());
					doctorPatientLedgerCollection.setDebitAmount(doctorPatientInvoiceCollection.getBalanceAmount());
					doctorPatientLedgerCollection.setCreatedTime(new Date());
					doctorPatientLedgerCollection.setUpdatedTime(new Date());
				} else {
					doctorPatientLedgerCollection.setDebitAmount(doctorPatientInvoiceCollection.getBalanceAmount());
					doctorPatientLedgerCollection.setUpdatedTime(new Date());
				}
				doctorPatientLedgerCollection = doctorPatientLedgerRepository.save(doctorPatientLedgerCollection);

				DoctorPatientLedgerCollection doctorPatientLedgerReceiptCollection = doctorPatientLedgerRepository
						.findByReceiptId(doctorPatientReceiptCollection.getId());

				if (doctorPatientLedgerReceiptCollection == null) {
					doctorPatientLedgerReceiptCollection = new DoctorPatientLedgerCollection();
					doctorPatientLedgerReceiptCollection.setPatientId(doctorPatientReceiptCollection.getPatientId());
					doctorPatientLedgerReceiptCollection.setLocationId(doctorPatientReceiptCollection.getLocationId());
					doctorPatientLedgerReceiptCollection.setHospitalId(doctorPatientReceiptCollection.getHospitalId());
					doctorPatientLedgerReceiptCollection.setReceiptId(doctorPatientReceiptCollection.getId());
					doctorPatientLedgerReceiptCollection
							.setCreditAmount(doctorPatientReceiptCollection.getAmountPaid());
					doctorPatientLedgerReceiptCollection.setCreatedTime(new Date());
					doctorPatientLedgerReceiptCollection.setUpdatedTime(new Date());
				} else {
					doctorPatientLedgerReceiptCollection
							.setCreditAmount(doctorPatientReceiptCollection.getAmountPaid());
					doctorPatientLedgerReceiptCollection.setUpdatedTime(new Date());
				}
				doctorPatientLedgerReceiptCollection = doctorPatientLedgerRepository
						.save(doctorPatientLedgerReceiptCollection);

				DoctorPatientDueAmountCollection doctorPatientDueAmountCollection = doctorPatientDueAmountRepository
						.find(doctorPatientReceiptCollection.getPatientId(),
								doctorPatientReceiptCollection.getDoctorId(),
								doctorPatientReceiptCollection.getLocationId(),
								doctorPatientReceiptCollection.getHospitalId());
				if (doctorPatientDueAmountCollection == null) {
					doctorPatientDueAmountCollection = new DoctorPatientDueAmountCollection();
					doctorPatientDueAmountCollection.setDoctorId(doctorPatientReceiptCollection.getDoctorId());
					doctorPatientDueAmountCollection.setHospitalId(doctorPatientReceiptCollection.getHospitalId());
					doctorPatientDueAmountCollection.setLocationId(doctorPatientReceiptCollection.getLocationId());
					doctorPatientDueAmountCollection.setPatientId(doctorPatientReceiptCollection.getPatientId());
				}
				doctorPatientDueAmountCollection.setDueAmount(doctorPatientDueAmountCollection.getDueAmount() +
						doctorPatientInvoiceCollection.getGrandTotal() - doctorPatientReceiptCollection.getAmountPaid());
				doctorPatientDueAmountRepository.save(doctorPatientDueAmountCollection);

			}
		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			logger.error("Error while adding invoice and receipt" + e);
			throw new BusinessException(ServiceError.Unknown, "Error while adding invoice and receipt" + e);
		}
		return response;
	}

	@Override
	public Double getTotalDueAmount(String doctorId, String locationId, String hospitalId, String patientId) {
		Double response = 0.0;
		AmountResponse dueAmount = null;
		try {
			Criteria criteria = new Criteria("patientId").is(new ObjectId(patientId)).and("locationId")
					.is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));

			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				dueAmount = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.group("doctorId").sum("dueAmount").as("totalDueAmount")),
						DoctorPatientLedgerCollection.class, AmountResponse.class).getUniqueMappedResult();
			} else {
				dueAmount = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.group("locationId").sum("dueAmount").as("totalDueAmount")),
						DoctorPatientLedgerCollection.class, AmountResponse.class).getUniqueMappedResult();
			}
			if (dueAmount != null) {
				response = dueAmount.getTotalDueAmount();
			}
		} catch (Exception e) {
			logger.error("Error while getting balance amount" + e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting balance amount" + e);
		}
		return response;
	}

	@Override
	public DoctorPatientLedgerResponse getLedger(String doctorId, String locationId, String hospitalId,
			String patientId, String from, String to, int page, int size, String updatedTime) {
		DoctorPatientLedgerResponse response = null;
		try {
			long updatedTimeStamp = Long.parseLong(updatedTime);
			Criteria criteria = new Criteria("updatedTime").gte(new Date(updatedTimeStamp)).and("patientId")
					.is(new ObjectId(patientId)).and("locationId").is(new ObjectId(locationId)).and("hospitalId")
					.is(new ObjectId(hospitalId));

			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));

			if (!DPDoctorUtils.anyStringEmpty(from)) {
				criteria.and("createdTime").gte(DPDoctorUtils.getStartTime(new Date(Long.parseLong(from))));
			}
			if (!DPDoctorUtils.anyStringEmpty(to)) {
				criteria.and("createdTime").gte(DPDoctorUtils.getStartTime(new Date(Long.parseLong(from))));
			}

			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation
						.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("doctor_patient_invoice_cl", "invoiceId", "_id", "invoice"),
								new CustomAggregationOperation(new BasicDBObject("$unwind",
										new BasicDBObject("path", "$invoice").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.lookup("doctor_patient_receipt_cl", "receiptId", "_id", "receipt"),
								new CustomAggregationOperation(new BasicDBObject("$unwind",
										new BasicDBObject("path", "$receipt").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.skip((page) * size), Aggregation.limit(size),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			} else {
				aggregation = Aggregation
						.newAggregation(Aggregation.match(criteria),
								Aggregation.lookup("doctor_patient_invoice_cl", "invoiceId", "_id", "invoice"),
								new CustomAggregationOperation(new BasicDBObject("$unwind",
										new BasicDBObject("path", "$invoice").append("preserveNullAndEmptyArrays",
												true))),
								Aggregation.lookup("doctor_patient_receipt_cl", "receiptId", "_id",
										"receipt"),
								new CustomAggregationOperation(
										new BasicDBObject("$unwind",
												new BasicDBObject("path", "$receipt")
														.append("preserveNullAndEmptyArrays", true))),
								Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			}

			List<DoctorPatientLedger> doctorPatientLedgers = mongoTemplate
					.aggregate(aggregation, DoctorPatientLedgerCollection.class, DoctorPatientLedger.class)
					.getMappedResults();
			if (doctorPatientLedgers != null && !doctorPatientLedgers.isEmpty()) {
				response = new DoctorPatientLedgerResponse();
				response.setDoctorPatientLedgers(doctorPatientLedgers);
				AmountResponse dueAmount = null;
				if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
					dueAmount = mongoTemplate.aggregate(
							Aggregation.newAggregation(Aggregation.match(criteria),
									Aggregation.group("doctorId").sum("dueAmount").as("totalDueAmount")),
							DoctorPatientLedgerCollection.class, AmountResponse.class).getUniqueMappedResult();
				} else {
					dueAmount = mongoTemplate.aggregate(
							Aggregation.newAggregation(Aggregation.match(criteria),
									Aggregation.group("locationId").sum("dueAmount").as("totalDueAmount")),
							DoctorPatientLedgerCollection.class, AmountResponse.class).getUniqueMappedResult();
				}
				if (dueAmount != null)
					response.setTotalDueAmount(dueAmount.getTotalDueAmount());
			}
		} catch (Exception e) {
			logger.error("Error while getting ledger" + e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting ledger" + e);
		}
		return response;
	}

	@Override
	public AmountResponse getTotalDueAndAdvanceAmount(String doctorId, String locationId, String hospitalId,
			String patientId) {
		AmountResponse response = null;
		try {
			Criteria criteria = new Criteria("patientId").is(new ObjectId(patientId)).and("locationId")
					.is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));

			AmountResponse dueAmount = null;
			if (!DPDoctorUtils.anyStringEmpty(doctorId)) {
				dueAmount = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.group("doctorId").sum("dueAmount").as("totalDueAmount")),
						DoctorPatientDueAmountCollection.class, AmountResponse.class).getUniqueMappedResult();
			} else {
				dueAmount = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.group("locationId").sum("dueAmount").as("totalDueAmount")),
						DoctorPatientDueAmountCollection.class, AmountResponse.class).getUniqueMappedResult();
			}

			DoctorPatientReceipt doctorPatientReceipt = mongoTemplate
					.aggregate(
							Aggregation.newAggregation(Aggregation.match(criteria),
									Aggregation.group("patientId").sum("remainingAdvanceAmount")
											.as("remainingAdvanceAmount")),
							DoctorPatientReceiptCollection.class, DoctorPatientReceipt.class)
					.getUniqueMappedResult();

			response = new AmountResponse();
			if (doctorPatientReceipt != null)
				response.setTotalRemainingAdvanceAmount(doctorPatientReceipt.getRemainingAdvanceAmount());
			if (dueAmount != null)
				response.setTotalDueAmount(dueAmount.getTotalDueAmount());
		} catch (Exception e) {
			logger.error("Error while getting balance amount" + e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting advance & balance amount" + e);
		}
		return response;
	}

	@Override
	public String downloadInvoice(String invoiceId) {
		String response = null;
		try {
			DoctorPatientInvoiceCollection doctorPatientInvoiceCollection = doctorPatientInvoiceRepository
					.findOne(new ObjectId(invoiceId));
			if (doctorPatientInvoiceCollection != null) {
				PatientCollection patient = patientRepository.findByUserIdLocationIdAndHospitalId(
						doctorPatientInvoiceCollection.getPatientId(), doctorPatientInvoiceCollection.getLocationId(),
						doctorPatientInvoiceCollection.getHospitalId());
				UserCollection user = userRepository.findOne(doctorPatientInvoiceCollection.getPatientId());
				JasperReportResponse jasperReportResponse = createJasper(doctorPatientInvoiceCollection, patient, user);
				if (jasperReportResponse != null)
					response = getFinalImageURL(jasperReportResponse.getPath());
				if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
					if (jasperReportResponse.getFileSystemResource().getFile().exists())
						jasperReportResponse.getFileSystemResource().getFile().delete();
			} else {
				logger.warn("Invoice Id does not exist");
				throw new BusinessException(ServiceError.NotFound, "Invoice Id does not exist");
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while getting download invoice" + e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting download invoice " + e);
		}
		return response;
	}

	private String getFinalImageURL(String imageURL) {
		if (imageURL != null) {
			return imagePath + imageURL;
		} else
			return null;
	}

	private JasperReportResponse createJasper(DoctorPatientInvoiceCollection doctorPatientInvoiceCollection,
			PatientCollection patient, UserCollection user) throws IOException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperReportResponse response = null;
		List<InvoiceItemJasperDetails> invoiceItemJasperDetails = null;
		if (doctorPatientInvoiceCollection.getInvoiceItems() != null
				&& !doctorPatientInvoiceCollection.getInvoiceItems().isEmpty()) {
			invoiceItemJasperDetails = new ArrayList<InvoiceItemJasperDetails>();
			Boolean showInvoiceItemQuantity = false;
			Boolean showDiscount = false;
			Boolean showStatus = false;
			int no = 0;
			for (InvoiceItem invoiceItem : doctorPatientInvoiceCollection.getInvoiceItems()) {
				InvoiceItemJasperDetails invoiceItemJasperDetail = new InvoiceItemJasperDetails();

				invoiceItemJasperDetail.setNo(++no);
				if (invoiceItem.getStatus() != null) {
					showStatus = true;
					String status = invoiceItem.getStatus().getTreamentStatus().replaceAll("_", " ");
					status = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
					invoiceItemJasperDetail.setStatus(status);
				} else {
					invoiceItemJasperDetail.setStatus("--");
				}
				invoiceItemJasperDetail.setServiceName(invoiceItem.getName());

				if (invoiceItem.getQuantity() != null) {
					showInvoiceItemQuantity = true;
					String quantity = invoiceItem.getQuantity().getValue() + " ";
					if (invoiceItem.getQuantity().getType() != null)
						quantity = quantity + invoiceItem.getQuantity().getType().getDuration();
					invoiceItemJasperDetail.setQuantity(quantity);
				} else {
					invoiceItemJasperDetail.setQuantity("");
				}
				invoiceItemJasperDetail.setTax((invoiceItem.getTax() != null)
						? invoiceItem.getTax().getValue() + " " + invoiceItem.getTax().getUnit().getUnit() : "");
				invoiceItemJasperDetail.setCost(invoiceItem.getCost() + "");
				if (invoiceItem.getDiscount() != null) {
					if (invoiceItem.getDiscount().getValue() > 0)
						showDiscount = true;
					invoiceItemJasperDetail.setDiscount(
							invoiceItem.getDiscount().getValue() + " " + invoiceItem.getDiscount().getUnit().getUnit());
				} else {
					invoiceItemJasperDetail.setDiscount("");
				}

				invoiceItemJasperDetail.setTotal(invoiceItem.getFinalCost() + "");
				invoiceItemJasperDetails.add(invoiceItemJasperDetail);
			}
			parameters.put("showDiscount", showDiscount);
			parameters.put("showStatus", showStatus);
			parameters.put("showInvoiceItemQuantity", showInvoiceItemQuantity);
			parameters.put("items", invoiceItemJasperDetails);
			String total = "";
			if (doctorPatientInvoiceCollection.getTotalCost() > 0)
				total = "<b>Total Cost :</b> " + doctorPatientInvoiceCollection.getTotalCost() + "₹ &nbsp;&nbsp;&nbsp;";
			parameters.put("totalCost", total);
			if (doctorPatientInvoiceCollection.getTotalDiscount() != null
					&& doctorPatientInvoiceCollection.getTotalDiscount().getValue() > 0.0) {
				total = total + "<b>Total Discount :</b> "
						+ doctorPatientInvoiceCollection.getTotalDiscount().getValue() + " "
						+ doctorPatientInvoiceCollection.getTotalDiscount().getUnit().getUnit() + "&nbsp;&nbsp;&nbsp;";
			}

			if (doctorPatientInvoiceCollection.getTotalTax() != null
					&& doctorPatientInvoiceCollection.getTotalTax().getValue() > 0.0)
				total = total + "<b>Total Tax :</b> " + doctorPatientInvoiceCollection.getTotalTax().getValue()
						+ doctorPatientInvoiceCollection.getTotalTax().getUnit().getUnit() + "&nbsp;&nbsp;&nbsp";

			if (doctorPatientInvoiceCollection.getGrandTotal() > 0)
				total = total + "<b>Grand Total :</b> " + doctorPatientInvoiceCollection.getGrandTotal() + "₹ &nbsp;";
			parameters.put("grandTotal", total);

			total = "<b>Paid:</b> " + (doctorPatientInvoiceCollection.getGrandTotal()
					- doctorPatientInvoiceCollection.getBalanceAmount()) + "₹  &nbsp;";
			parameters.put("paid", total);

			total = "<b>Balance:</b> " + doctorPatientInvoiceCollection.getBalanceAmount() + "₹  &nbsp;";
			parameters.put("balance", total);
			PrintSettingsCollection printSettings = printSettingsRepository.getSettings(
					doctorPatientInvoiceCollection.getDoctorId(), doctorPatientInvoiceCollection.getLocationId(),
					doctorPatientInvoiceCollection.getHospitalId(), ComponentType.ALL.getType());

			patientVisitService.generatePatientDetails(
					(printSettings != null && printSettings.getHeaderSetup() != null
							? printSettings.getHeaderSetup().getPatientDetails() : null),
					patient,
					"<b>INVID: </b>" + (doctorPatientInvoiceCollection.getUniqueInvoiceId() != null
							? doctorPatientInvoiceCollection.getUniqueInvoiceId() : "--"),
					patient.getLocalPatientName(), user.getMobileNumber(), parameters);
			patientVisitService.generatePrintSetup(parameters, printSettings,
					doctorPatientInvoiceCollection.getDoctorId());
			String pdfName = (user != null ? user.getFirstName() : "") + "INVOICE-"
					+ doctorPatientInvoiceCollection.getUniqueInvoiceId() + new Date().getTime();

			String layout = printSettings != null
					? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
					: "PORTRAIT";
			String pageSize = printSettings != null
					? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4") : "A4";
			Integer topMargin = printSettings != null
					? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getTopMargin() : 20) : 20;
			Integer bottonMargin = printSettings != null
					? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getBottomMargin() : 20) : 20;
			Integer leftMargin = printSettings != null
					? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != 20
							? printSettings.getPageSetup().getLeftMargin() : 20)
					: 20;
			Integer rightMargin = printSettings != null
					? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
							? printSettings.getPageSetup().getRightMargin() : 20)
					: 20;
			response = jasperReportService.createPDF(ComponentType.INVOICE, parameters, invoiceA4FileName, layout,
					pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
					Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));
		}
		return response;
	}

	@Override
	public String downloadReceipt(String receiptId) {
		String response = null;
		try {
			DoctorPatientReceiptCollection doctorPatientReceiptCollection = doctorPatientReceiptRepository
					.findOne(new ObjectId(receiptId));
			if (doctorPatientReceiptCollection != null) {
				PatientCollection patient = patientRepository.findByUserIdLocationIdAndHospitalId(
						doctorPatientReceiptCollection.getPatientId(), doctorPatientReceiptCollection.getLocationId(),
						doctorPatientReceiptCollection.getHospitalId());
				UserCollection user = userRepository.findOne(doctorPatientReceiptCollection.getPatientId());
				JasperReportResponse jasperReportResponse = createJasperForReceipt(doctorPatientReceiptCollection,
						patient, user);
				if (jasperReportResponse != null)
					response = getFinalImageURL(jasperReportResponse.getPath());
				if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
					if (jasperReportResponse.getFileSystemResource().getFile().exists())
						jasperReportResponse.getFileSystemResource().getFile().delete();
			} else {
				logger.warn("Invoice Id does not exist");
				throw new BusinessException(ServiceError.NotFound, "Invoice Id does not exist");
			}

		} catch (Exception e) {
			logger.error("Error while getting download invoice" + e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting download invoice " + e);
		}
		return response;
	}

	private JasperReportResponse createJasperForReceipt(DoctorPatientReceiptCollection doctorPatientReceiptCollection,
			PatientCollection patient, UserCollection user) throws IOException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperReportResponse response = null;
		String pattern = "dd/MM/yyyy";
		UserCollection doctor = userRepository.findOne(doctorPatientReceiptCollection.getDoctorId());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String content = "<br>Received with thanks<br>&nbsp;&nbsp;&nbsp;The sum of Rupees:- "
				+ doctorPatientReceiptCollection.getAmountPaid() + " by "
				+ doctorPatientReceiptCollection.getModeOfPayment() + " On Date:-"
				+ simpleDateFormat.format(doctorPatientReceiptCollection.getReceivedDate());
		parameters.put("content", content);
		parameters.put("paid", "RS.&nbsp;" + doctorPatientReceiptCollection.getAmountPaid());
		parameters.put("name", doctor.getTitle().toUpperCase() + " " + doctor.getFirstName());
		PrintSettingsCollection printSettings = printSettingsRepository.getSettings(
				doctorPatientReceiptCollection.getDoctorId(), doctorPatientReceiptCollection.getLocationId(),
				doctorPatientReceiptCollection.getHospitalId(), ComponentType.ALL.getType());

		patientVisitService.generatePatientDetails(
				(printSettings != null && printSettings.getHeaderSetup() != null
						? printSettings.getHeaderSetup().getPatientDetails() : null),
				patient,
				"<b>RECEIPTID: </b>" + (doctorPatientReceiptCollection.getUniqueReceiptId() != null
						? doctorPatientReceiptCollection.getUniqueReceiptId() : "--"),
				patient.getLocalPatientName(), user.getMobileNumber(), parameters);
		patientVisitService.generatePrintSetup(parameters, printSettings, doctorPatientReceiptCollection.getDoctorId());
		String pdfName = (user != null ? user.getFirstName() : "") + "RECEIPT-"
				+ doctorPatientReceiptCollection.getUniqueReceiptId() + new Date().getTime();

		String layout = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
				: "PORTRAIT";
		String pageSize = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4") : "A4";
		Integer topMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getTopMargin() : 20) : 20;
		Integer bottonMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getBottomMargin() : 20) : 20;
		Integer leftMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != 20
						? printSettings.getPageSetup().getLeftMargin() : 20)
				: 20;
		Integer rightMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
						? printSettings.getPageSetup().getRightMargin() : 20)
				: 20;
		response = jasperReportService.createPDF(ComponentType.RECEIPT, parameters, receiptA4FileName, layout, pageSize,
				topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));

		return response;
	}

}
