package com.dpdocter.services.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dpdocter.beans.AdvanceReceiptIdWithAmount;
import com.dpdocter.beans.CustomAggregationOperation;
import com.dpdocter.beans.DefaultPrintSettings;
import com.dpdocter.beans.DoctorExpense;
import com.dpdocter.beans.DoctorPatientInvoice;
import com.dpdocter.beans.DoctorPatientLedger;
import com.dpdocter.beans.DoctorPatientReceipt;
import com.dpdocter.beans.Drug;
import com.dpdocter.beans.ExpenseType;
import com.dpdocter.beans.Fields;
import com.dpdocter.beans.InventoryBatch;
import com.dpdocter.beans.InventoryItem;
import com.dpdocter.beans.InventoryStock;
import com.dpdocter.beans.InvoiceAndReceiptInitials;
import com.dpdocter.beans.InvoiceItem;
import com.dpdocter.beans.InvoiceItemJasperDetails;
import com.dpdocter.beans.MailAttachment;
import com.dpdocter.beans.ReceiptJasperDetails;
import com.dpdocter.beans.SMS;
import com.dpdocter.beans.SMSAddress;
import com.dpdocter.beans.SMSDetail;
import com.dpdocter.collections.DoctorExpenseCollection;
import com.dpdocter.collections.DoctorPatientDueAmountCollection;
import com.dpdocter.collections.DoctorPatientInvoiceCollection;
import com.dpdocter.collections.DoctorPatientLedgerCollection;
import com.dpdocter.collections.DoctorPatientReceiptCollection;
import com.dpdocter.collections.DrugCollection;
import com.dpdocter.collections.EmailTrackCollection;
import com.dpdocter.collections.ExpenseTypeCollection;
import com.dpdocter.collections.LocationCollection;
import com.dpdocter.collections.PatientCollection;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.SMSTrackDetail;
import com.dpdocter.collections.UserCollection;
import com.dpdocter.elasticsearch.document.ESExpenseTypeDocument;
import com.dpdocter.elasticsearch.services.ESExpenseTypeService;
import com.dpdocter.enums.BillingType;
import com.dpdocter.enums.ComponentType;
import com.dpdocter.enums.InvoiceItemType;
import com.dpdocter.enums.ReceiptType;
import com.dpdocter.enums.SMSStatus;
import com.dpdocter.enums.UniqueIdInitial;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorExpenseRepository;
import com.dpdocter.repository.DoctorPatientDueAmountRepository;
import com.dpdocter.repository.DoctorPatientInvoiceRepository;
import com.dpdocter.repository.DoctorPatientLedgerRepository;
import com.dpdocter.repository.DoctorPatientReceiptRepository;
import com.dpdocter.repository.DrugRepository;
import com.dpdocter.repository.ExpenseTypeRepository;
import com.dpdocter.repository.LocationRepository;
import com.dpdocter.repository.PatientRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.repository.UserRepository;
import com.dpdocter.request.DoctorPatientInvoiceAndReceiptRequest;
import com.dpdocter.request.DoctorPatientReceiptRequest;
import com.dpdocter.request.InvoiceItemChangeStatusRequest;
import com.dpdocter.response.AmountResponse;
import com.dpdocter.response.DoctorPatientInvoiceAndReceiptResponse;
import com.dpdocter.response.DoctorPatientLedgerResponse;
import com.dpdocter.response.DoctorPatientReceiptAddEditResponse;
import com.dpdocter.response.DoctorPatientReceiptLookupResponse;
import com.dpdocter.response.InvoiceItemResponse;
import com.dpdocter.response.JasperReportResponse;
import com.dpdocter.response.MailResponse;
import com.dpdocter.services.BillingService;
import com.dpdocter.services.EmailTackService;
import com.dpdocter.services.InventoryService;
import com.dpdocter.services.JasperReportService;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.dpdocter.services.PatientVisitService;
import com.dpdocter.services.PrescriptionServices;
import com.dpdocter.services.PushNotificationServices;
import com.dpdocter.services.SMSServices;
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
	private MailBodyGenerator mailBodyGenerator;

	@Autowired
	private PrintSettingsRepository printSettingsRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private EmailTackService emailTackService;

	@Autowired
	private InventoryService inventoryService;

	@Autowired
	private MailService mailService;

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
	private SMSServices smsServices;

	@Autowired
	DoctorPatientLedgerRepository doctorPatientLedgerRepository;

	@Autowired
	DoctorPatientDueAmountRepository doctorPatientDueAmountRepository;

	@Value(value = "${sms.add.dueAmount.to.patient}")
	private String dueAmountRemainderSMS;

	@Value(value = "${jasper.print.multiple.receipt.fileName}")
	private String multipleReceiptFileName;

	@Autowired
	DrugRepository drugRepository;

	@Autowired
	private PrescriptionServices prescriptionServices;

	@Autowired
	PushNotificationServices pushNotificationServices;

	@Autowired
	private DoctorExpenseRepository doctorExpenseRepository;

	@Autowired
	private ExpenseTypeRepository expenseTypeRepository;

	@Autowired
	private ESExpenseTypeService esExpenseTypeService;

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

	@SuppressWarnings("unchecked")
	@Override
	public DoctorPatientInvoice addEditInvoice(DoctorPatientInvoice request) {
		DoctorPatientInvoice response = null;
		try {
			Map<String, UserCollection> doctorsMap = new HashMap<String, UserCollection>();
			DoctorPatientInvoiceCollection doctorPatientInvoiceCollection = new DoctorPatientInvoiceCollection();
			Collection<ObjectId> itemIds = null;

			ObjectId doctorObjectId = new ObjectId(request.getDoctorId());
			Double dueAmount = 0.0;

			if (DPDoctorUtils.anyStringEmpty(request.getId())) {
				BeanUtil.map(request, doctorPatientInvoiceCollection);
				UserCollection userCollection = userRepository.findOne(doctorObjectId);
				if (userCollection != null) {
					doctorPatientInvoiceCollection.setCreatedBy(
							(!DPDoctorUtils.anyStringEmpty(userCollection.getTitle()) ? userCollection.getTitle() + " "
									: "") + userCollection.getFirstName());
					doctorsMap.put(request.getDoctorId(), userCollection);
				}

				LocationCollection locationCollection = locationRepository
						.findOne(new ObjectId(request.getLocationId()));
				if (locationCollection == null)
					throw new BusinessException(ServiceError.InvalidInput, "Invalid Location Id");
				doctorPatientInvoiceCollection
						.setUniqueInvoiceId(locationCollection.getInvoiceInitial() + ((int) mongoTemplate.count(
								new Query(new Criteria("locationId").is(doctorPatientInvoiceCollection.getLocationId())
										.and("hospitalId").is(doctorPatientInvoiceCollection.getHospitalId())),
								DoctorPatientInvoiceCollection.class) + 1));
				doctorPatientInvoiceCollection.setBalanceAmount(request.getGrandTotal());
				dueAmount = doctorPatientInvoiceCollection.getBalanceAmount();
				if (doctorPatientInvoiceCollection.getInvoiceDate() == null)
					doctorPatientInvoiceCollection.setInvoiceDate(new Date());
				if (request.getCreatedTime() == null) {
					doctorPatientInvoiceCollection.setCreatedTime(new Date());
				}
				doctorPatientInvoiceCollection.setAdminCreatedTime(new Date());
			} else {
				doctorPatientInvoiceCollection = doctorPatientInvoiceRepository.findOne(new ObjectId(request.getId()));
				Double paidAmount = doctorPatientInvoiceCollection.getGrandTotal()
						- doctorPatientInvoiceCollection.getBalanceAmount();

				List<DoctorPatientReceiptCollection> doPatientReceiptCollections = doctorPatientReceiptRepository
						.findByInvoiceId(doctorPatientInvoiceCollection.getId(), false);

				if (doPatientReceiptCollections != null && !doPatientReceiptCollections.isEmpty()) {
					throw new BusinessException(ServiceError.Unknown,
							"Invoice cannot be edited as receipt is already added.");
				}
				if (paidAmount > request.getGrandTotal()) {
					throw new BusinessException(ServiceError.Unknown,
							"Invoice cannot be edited as old invoice's total is less than paid amount.");
				}
				if (request.getCreatedTime() != null) {
					doctorPatientInvoiceCollection.setCreatedTime(request.getCreatedTime());
				}

				if (!doctorPatientInvoiceCollection.getDoctorId().toString().equalsIgnoreCase(request.toString())) {
					if (doctorPatientInvoiceCollection.getReceiptIds() == null
							|| doctorPatientInvoiceCollection.getReceiptIds().isEmpty()) {
						doctorPatientInvoiceCollection.setDoctorId(new ObjectId(request.getDoctorId()));
					} else {
						throw new BusinessException(ServiceError.Unknown,
								"Doctor cannot be updated as Receipt is already created.");
					}
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

			doctorPatientInvoiceCollection = doctorPatientInvoiceRepository.save(doctorPatientInvoiceCollection);
			if (doctorPatientInvoiceCollection != null && doctorPatientInvoiceCollection.getInvoiceItems() != null) {
				itemIds = CollectionUtils.collect(doctorPatientInvoiceCollection.getInvoiceItems(),
						new BeanToPropertyValueTransformer("itemId"));
			}
			List<InvoiceItem> invoiceItems = new ArrayList<InvoiceItem>();
			for (InvoiceItemResponse invoiceItemResponse : request.getInvoiceItems()) {
				InventoryStock inventoryStock = null;
				Long quantity = null;
				Drug drug = null;
				if (invoiceItemResponse.getType().equals(InvoiceItemType.PRODUCT)) {
					drug = prescriptionServices.getDrugById(invoiceItemResponse.getItemId());
				}
				itemIds.remove(new ObjectId(invoiceItemResponse.getItemId()));
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
				if (drug != null) {
					if (!DPDoctorUtils.anyStringEmpty(doctorPatientInvoiceCollection.getId())) {

						inventoryStock = inventoryService.getInventoryStockByInvoiceIdResourceId(
								request.getLocationId(), request.getHospitalId(), drug.getDrugCode(),
								doctorPatientInvoiceCollection.getId().toString());
						quantity = inventoryService.getInventoryStockItemCount(request.getLocationId(),
								request.getHospitalId(), drug.getDrugCode(),
								doctorPatientInvoiceCollection.getId().toString());
					}
					if (quantity != null && quantity > 0 && invoiceItemResponse.getInventoryBatch() != null) {
						if (inventoryStock.getBatchId().equals(invoiceItemResponse.getInventoryBatch().getId())) {
							if (quantity > invoiceItemResponse.getQuantity().getValue()) {
								Long diff = quantity - invoiceItemResponse.getQuantity().getValue();
								InventoryItem inventoryItem = inventoryService.getInventoryItemByResourceId(
										request.getLocationId(), request.getHospitalId(), drug.getDrugCode());
								if (invoiceItemResponse.getInventoryBatch() != null && inventoryItem != null) {
									createInventoryStock(drug.getDrugCode(), inventoryItem.getId(),
											invoiceItemResponse.getInventoryBatch(), request.getPatientId(),
											request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
											diff.intValue(), doctorPatientInvoiceCollection.getId().toString(),
											"ADDED");
								}
							} else if (quantity < invoiceItemResponse.getQuantity().getValue()) {
								Long diff = invoiceItemResponse.getQuantity().getValue() - quantity;
								InventoryItem inventoryItem = inventoryService.getInventoryItemByResourceId(
										request.getLocationId(), request.getHospitalId(), drug.getDrugCode());
								if (invoiceItemResponse.getInventoryBatch() != null && inventoryItem != null) {
									createInventoryStock(drug.getDrugCode(), inventoryItem.getId(),
											invoiceItemResponse.getInventoryBatch(), request.getPatientId(),
											request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
											diff.intValue(), doctorPatientInvoiceCollection.getId().toString(),
											"CONSUMED");
								}
							}

						} else {

							InventoryBatch oldInventoryBatch = inventoryService
									.getInventoryBatchById(inventoryStock.getBatchId());

							if (quantity > invoiceItemResponse.getQuantity().getValue()) {
								Long diff = quantity - invoiceItemResponse.getQuantity().getValue();
								InventoryItem inventoryItem = inventoryService.getInventoryItemByResourceId(
										request.getLocationId(), request.getHospitalId(), drug.getDrugCode());
								if (oldInventoryBatch != null && inventoryItem != null) {
									createInventoryStock(drug.getDrugCode(), inventoryItem.getId(), oldInventoryBatch,
											request.getPatientId(), request.getDoctorId(), request.getLocationId(),
											request.getHospitalId(), diff.intValue(),
											doctorPatientInvoiceCollection.getId().toString(), "ADDED");
								}
								if (invoiceItemResponse.getInventoryBatch() != null && inventoryItem != null) {
									createInventoryStock(drug.getDrugCode(), inventoryItem.getId(),
											invoiceItemResponse.getInventoryBatch(), request.getPatientId(),
											request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
											diff.intValue(), doctorPatientInvoiceCollection.getId().toString(),
											"CONSUMED");
								}
							} else if (quantity < invoiceItemResponse.getQuantity().getValue()) {
								Long diff = invoiceItemResponse.getQuantity().getValue() - quantity;
								InventoryItem inventoryItem = inventoryService.getInventoryItemByResourceId(
										request.getLocationId(), request.getHospitalId(), drug.getDrugCode());
								if (invoiceItemResponse.getInventoryBatch() != null && inventoryItem != null) {
									createInventoryStock(drug.getDrugCode(), inventoryItem.getId(),
											invoiceItemResponse.getInventoryBatch(), request.getPatientId(),
											request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
											diff.intValue(), doctorPatientInvoiceCollection.getId().toString(),
											"ADDED");
								}
								if (oldInventoryBatch != null && inventoryItem != null) {
									createInventoryStock(drug.getDrugCode(), inventoryItem.getId(), oldInventoryBatch,
											request.getPatientId(), request.getDoctorId(), request.getLocationId(),
											request.getHospitalId(), diff.intValue(),
											doctorPatientInvoiceCollection.getId().toString(), "CONSUMED");
								}
							}
						}

					} else {
						InventoryItem inventoryItem = inventoryService.getInventoryItemByResourceId(
								request.getLocationId(), request.getHospitalId(), drug.getDrugCode());
						if (invoiceItemResponse.getInventoryBatch() != null && inventoryItem != null) {
							createInventoryStock(drug.getDrugCode(), inventoryItem.getId(),
									invoiceItemResponse.getInventoryBatch(), request.getPatientId(),
									request.getDoctorId(), request.getLocationId(), request.getHospitalId(),
									invoiceItemResponse.getQuantity().getValue(),
									doctorPatientInvoiceCollection.getId().toString(), "CONSUMED");
						}
					}
				}

				InvoiceItem invoiceItem = new InvoiceItem();
				BeanUtil.map(invoiceItemResponse, invoiceItem);
				invoiceItems.add(invoiceItem);
				doctorPatientInvoiceCollection.setInvoiceItems(invoiceItems);
			}

			for (ObjectId itemId : itemIds) {
				DrugCollection drug = drugRepository.findOne(itemId);
				if (drug != null) {
					InventoryStock inventoryStock = inventoryService.getInventoryStockByInvoiceIdResourceId(
							request.getLocationId(), request.getHospitalId(), drug.getDrugCode(),
							doctorPatientInvoiceCollection.getId().toString());
					Long quantity = inventoryService.getInventoryStockItemCount(request.getLocationId(),
							request.getHospitalId(), drug.getDrugCode(),
							doctorPatientInvoiceCollection.getId().toString());
					InventoryItem inventoryItem = inventoryService.getInventoryItemByResourceId(request.getLocationId(),
							request.getHospitalId(), drug.getDrugCode());
					if (inventoryStock != null) {
						InventoryBatch inventoryBatch = inventoryService
								.getInventoryBatchById(inventoryStock.getBatchId());
						if (inventoryBatch != null && inventoryItem != null) {
							createInventoryStock(drug.getDrugCode(), inventoryItem.getId(), inventoryBatch,
									request.getPatientId(), request.getDoctorId(), request.getLocationId(),
									request.getHospitalId(), quantity.intValue(),
									doctorPatientInvoiceCollection.getId().toString(), "ADDED");
						}
					}
				}

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
					doctorPatientLedgerCollection.setDebitAmount(doctorPatientInvoiceCollection.getBalanceAmount());
					doctorPatientLedgerCollection.setCreatedTime(new Date());
					doctorPatientLedgerCollection.setUpdatedTime(new Date());
				} else {
					doctorPatientLedgerCollection.setDebitAmount(doctorPatientInvoiceCollection.getBalanceAmount());
					doctorPatientLedgerCollection.setUpdatedTime(new Date());
				}
				doctorPatientLedgerCollection = doctorPatientLedgerRepository.save(doctorPatientLedgerCollection);
				DoctorPatientDueAmountCollection doctorPatientDueAmountCollection = doctorPatientDueAmountRepository
						.find(doctorPatientInvoiceCollection.getPatientId(),
								doctorPatientInvoiceCollection.getDoctorId(),
								doctorPatientInvoiceCollection.getLocationId(),
								doctorPatientInvoiceCollection.getHospitalId());
				if (doctorPatientDueAmountCollection == null) {
					doctorPatientDueAmountCollection = new DoctorPatientDueAmountCollection();
					doctorPatientDueAmountCollection.setDoctorId(doctorPatientInvoiceCollection.getDoctorId());
					doctorPatientDueAmountCollection.setHospitalId(doctorPatientInvoiceCollection.getHospitalId());
					doctorPatientDueAmountCollection.setLocationId(doctorPatientInvoiceCollection.getLocationId());
					doctorPatientDueAmountCollection.setPatientId(doctorPatientInvoiceCollection.getPatientId());
					doctorPatientDueAmountCollection.setDueAmount(0.0);
				}
				doctorPatientDueAmountCollection
						.setDueAmount(doctorPatientDueAmountCollection.getDueAmount() + dueAmount);
				doctorPatientDueAmountRepository.save(doctorPatientDueAmountCollection);
			}

			pushNotificationServices.notifyUser(doctorPatientInvoiceCollection.getDoctorId().toString(),
					"Invoice Added", ComponentType.INVOICE_REFRESH.getType(),
					doctorPatientInvoiceCollection.getPatientId().toString(), null);

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

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp)).and("isPatientDiscarded")
					.ne(true);

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

	@SuppressWarnings("unchecked")
	@Override
	public DoctorPatientInvoice deleteInvoice(String invoiceId, Boolean discarded) {
		DoctorPatientInvoice response = null;
		Collection<ObjectId> itemIds = null;
		try {
			DoctorPatientInvoiceCollection doctorPatientInvoiceCollection = doctorPatientInvoiceRepository
					.findOne(new ObjectId(invoiceId));
			doctorPatientInvoiceCollection.setDiscarded(discarded);
			doctorPatientInvoiceCollection.setUpdatedTime(new Date());

			Double advanceAmount = 0.0;
			List<DoctorPatientReceiptCollection> doPatientReceiptCollections = doctorPatientReceiptRepository
					.findByInvoiceId(doctorPatientInvoiceCollection.getId(), false);
			if (doPatientReceiptCollections != null && !doPatientReceiptCollections.isEmpty()) {
				for (DoctorPatientReceiptCollection receiptCollection : doPatientReceiptCollections) {

					if (receiptCollection.getAdvanceReceiptIdWithAmounts() != null
							&& !receiptCollection.getAdvanceReceiptIdWithAmounts().isEmpty()) {
						advanceAmount = advanceAmount + receiptCollection.getUsedAdvanceAmount();
						for (AdvanceReceiptIdWithAmount receiptIdWithAmount : receiptCollection
								.getAdvanceReceiptIdWithAmounts()) {
							DoctorPatientReceiptCollection patientReceiptCollection = doctorPatientReceiptRepository
									.findOne(receiptIdWithAmount.getReceiptId());
							patientReceiptCollection
									.setRemainingAdvanceAmount(patientReceiptCollection.getRemainingAdvanceAmount()
											+ receiptIdWithAmount.getUsedAdvanceAmount());
							patientReceiptCollection.setUpdatedTime(new Date());
							doctorPatientReceiptRepository.save(patientReceiptCollection);
						}
					}
					DoctorPatientLedgerCollection patientLedgerCollection = doctorPatientLedgerRepository
							.findByReceiptId(receiptCollection.getId());
					patientLedgerCollection.setDiscarded(discarded);
					patientLedgerCollection.setUpdatedTime(new Date());
					doctorPatientLedgerRepository.save(patientLedgerCollection);

					receiptCollection.setUpdatedTime(new Date());
					// receiptCollection.setBalanceAmount(doctorPatientInvoiceCollection.getBalanceAmount());
					receiptCollection.setDiscarded(discarded);
				}

				// for(DoctorPatientReceiptCollection receiptCollection :
				// doPatientReceiptCollections){
				// receiptCollection.setBalanceAmount(doctorPatientInvoiceCollection.getBalanceAmount());
				// receiptCollection.setUpdatedTime(new Date());
				// }

				doctorPatientReceiptRepository.save(doPatientReceiptCollections);
			}
			doctorPatientInvoiceCollection = doctorPatientInvoiceRepository.save(doctorPatientInvoiceCollection);

			DoctorPatientLedgerCollection patientLedgerCollection = doctorPatientLedgerRepository
					.findByInvoiceId(doctorPatientInvoiceCollection.getId());
			patientLedgerCollection.setDiscarded(discarded);
			patientLedgerCollection.setUpdatedTime(new Date());
			doctorPatientLedgerRepository.save(patientLedgerCollection);

			DoctorPatientDueAmountCollection amountCollection = doctorPatientDueAmountRepository.find(
					doctorPatientInvoiceCollection.getPatientId(), doctorPatientInvoiceCollection.getDoctorId(),
					doctorPatientInvoiceCollection.getLocationId(), doctorPatientInvoiceCollection.getHospitalId());
			amountCollection.setUpdatedTime(new Date());
			amountCollection.setDueAmount(amountCollection.getDueAmount()
					- doctorPatientInvoiceCollection.getBalanceAmount() - advanceAmount);
			doctorPatientDueAmountRepository.save(amountCollection);

			if (doctorPatientInvoiceCollection != null && doctorPatientInvoiceCollection.getInvoiceItems() != null) {
				itemIds = CollectionUtils.collect(doctorPatientInvoiceCollection.getInvoiceItems(),
						new BeanToPropertyValueTransformer("itemId"));
			}

			if (doctorPatientInvoiceCollection != null) {
				response = new DoctorPatientInvoice();
				BeanUtil.map(doctorPatientInvoiceCollection, response);
			}

			for (ObjectId itemId : itemIds) {
				// itemId);
				DrugCollection drug = drugRepository.findOne(itemId);
				if (drug != null) {
					InventoryStock inventoryStock = inventoryService.getInventoryStockByInvoiceIdResourceId(
							response.getLocationId(), response.getHospitalId(), drug.getDrugCode(),
							doctorPatientInvoiceCollection.getId().toString());
					Long quantity = inventoryService.getInventoryStockItemCount(response.getLocationId(),
							response.getHospitalId(), drug.getDrugCode(),
							doctorPatientInvoiceCollection.getId().toString());
					InventoryItem inventoryItem = inventoryService.getInventoryItemByResourceId(
							response.getLocationId(), response.getHospitalId(), drug.getDrugCode());
					if (inventoryStock != null) {
						InventoryBatch inventoryBatch = inventoryService
								.getInventoryBatchById(inventoryStock.getBatchId());
						if (inventoryBatch != null && inventoryItem != null) {
							createInventoryStock(drug.getDrugCode(), inventoryItem.getId(), inventoryBatch,
									response.getPatientId(), response.getDoctorId(), response.getLocationId(),
									response.getHospitalId(), quantity.intValue(),
									doctorPatientInvoiceCollection.getId().toString(), "ADDED");
						}
					}
				}

			}
		} catch (BusinessException be) {
			be.printStackTrace();
			logger.error(be);
			throw be;
		} catch (Exception e) {
			e.printStackTrace();
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
					doctorPatientReceiptCollection.setCreatedBy(
							(!DPDoctorUtils.anyStringEmpty(userCollection.getTitle()) ? userCollection.getTitle() + " "
									: "") + userCollection.getFirstName());
				}
				doctorPatientReceiptCollection.setCreatedTime(new Date());
				LocationCollection locationCollection = locationRepository
						.findOne(new ObjectId(request.getLocationId()));
				if (locationCollection == null)
					throw new BusinessException(ServiceError.InvalidInput, "Invalid Location Id");
				doctorPatientReceiptCollection
						.setUniqueReceiptId(locationCollection.getReceiptInitial() + ((int) mongoTemplate.count(
								new Query(new Criteria("locationId").is(doctorPatientReceiptCollection.getLocationId())
										.and("hospitalId").is(doctorPatientReceiptCollection.getHospitalId())),
								DoctorPatientReceiptCollection.class) + 1));
				dueAmount = request.getAmountPaid() != null ? request.getAmountPaid() : 0.0;
				if (doctorPatientReceiptCollection.getReceivedDate() == null)
					doctorPatientReceiptCollection.setReceivedDate(new Date());
			} else {
				doctorPatientReceiptCollection = doctorPatientReceiptRepository.findOne(new ObjectId(request.getId()));
				dueAmount = (request.getAmountPaid() != null ? request.getAmountPaid() : 0.0)
						- doctorPatientReceiptCollection.getAmountPaid();
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
					List<AdvanceReceiptIdWithAmount> receiptIdWithAmounts = doctorPatientReceiptCollection
							.getAdvanceReceiptIdWithAmounts();
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

						Double advanceAmountToBeUsed = request.getUsedAdvanceAmount() != null
								? request.getUsedAdvanceAmount()
								: 0.0;

						for (DoctorPatientReceiptCollection receiptCollection : receiptsOfAdvancePayment) {
							AdvanceReceiptIdWithAmount invoiceIdWithAmount = new AdvanceReceiptIdWithAmount();
							invoiceIdWithAmount.setUniqueReceiptId(receiptCollection.getUniqueReceiptId());
							invoiceIdWithAmount.setReceiptId(receiptCollection.getId());
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

								if (receiptIdWithAmounts == null || receiptIdWithAmounts.isEmpty()) {
									receiptIdWithAmounts = new ArrayList<AdvanceReceiptIdWithAmount>();
								}

								receiptIdWithAmounts.add(invoiceIdWithAmount);
								receiptCollection.setUpdatedTime(new Date());

								doctorPatientReceiptRepository.save(receiptCollection);
							}
						}
						doctorPatientInvoiceCollection.setUsedAdvanceAmount(doctorPatientInvoiceCollection
								.getUsedAdvanceAmount()
								+ (request.getUsedAdvanceAmount() != null ? request.getUsedAdvanceAmount() : 0.0));
						doctorPatientReceiptCollection.setUsedAdvanceAmount(request.getUsedAdvanceAmount());
						doctorPatientInvoiceCollection.setBalanceAmount(doctorPatientInvoiceCollection
								.getBalanceAmount() - (request.getAmountPaid() != null ? request.getAmountPaid() : 0.0)
								- (request.getUsedAdvanceAmount() != null ? request.getUsedAdvanceAmount() : 0.0));

						if (doctorPatientInvoiceCollection.getBalanceAmount() < 0.0)
							doctorPatientInvoiceCollection.setBalanceAmount(0.0);
						doctorPatientReceiptCollection
								.setBalanceAmount(doctorPatientInvoiceCollection.getBalanceAmount());
					} else {
						doctorPatientInvoiceCollection.setUsedAdvanceAmount(doctorPatientInvoiceCollection
								.getUsedAdvanceAmount()
								+ (request.getUsedAdvanceAmount() != null ? request.getUsedAdvanceAmount() : 0.0));
						doctorPatientReceiptCollection.setUsedAdvanceAmount(request.getUsedAdvanceAmount());
						doctorPatientInvoiceCollection.setBalanceAmount(doctorPatientInvoiceCollection
								.getBalanceAmount() - (request.getAmountPaid() != null ? request.getAmountPaid() : 0.0)
								- (request.getUsedAdvanceAmount() != null ? request.getUsedAdvanceAmount() : 0.0));
						if (doctorPatientInvoiceCollection.getBalanceAmount() < 0.0)
							doctorPatientInvoiceCollection.setBalanceAmount(0.0);
						doctorPatientReceiptCollection
								.setBalanceAmount(doctorPatientInvoiceCollection.getBalanceAmount());
					}
					doctorPatientReceiptCollection
							.setUniqueInvoiceId(doctorPatientInvoiceCollection.getUniqueInvoiceId());
					doctorPatientReceiptCollection.setAdvanceReceiptIdWithAmounts(receiptIdWithAmounts);
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

			pushNotificationServices.notifyUser(doctorPatientReceiptCollection.getDoctorId().toString(),
					"Receipt Added", ComponentType.RECEIPT_REFRESH.getType(),
					doctorPatientReceiptCollection.getPatientId().toString(), null);

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

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp)).and("isPatientDiscarded")
					.ne(true);

			if (!DPDoctorUtils.anyStringEmpty(patientId))
				criteria.and("patientId").is(new ObjectId(patientId));
			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
				criteria.and("locationId").is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));
			if (!discarded)
				criteria.and("discarded").is(discarded);

			if (size > 0) {
				responses = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
						// Aggregation.lookup("doctor_patient_invoice_cl",
						// "invoiceId", "_id", "invoice"),
						// new CustomAggregationOperation(new
						// BasicDBObject("$unwind",
						// new BasicDBObject("path",
						// "$invoice").append("preserveNullAndEmptyArrays",
						// true))),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "createdTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size)), DoctorPatientReceiptCollection.class, DoctorPatientReceipt.class)
						.getMappedResults();
			} else {
				responses = mongoTemplate.aggregate(Aggregation.newAggregation(Aggregation.match(criteria),
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

		} catch (Exception e) {
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

		} catch (Exception e) {
			logger.error("Error while getting invoice count" + e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting invoice count" + e);
		}
		return response;
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public DoctorPatientReceipt deleteReceipt(String receiptId, Boolean discarded) {
		DoctorPatientReceipt response = null;
		try {
			DoctorPatientReceiptCollection doctorPatientReceiptCollection = doctorPatientReceiptRepository
					.findOne(new ObjectId(receiptId));

			if (doctorPatientReceiptCollection.getReceiptType().name().equalsIgnoreCase(ReceiptType.INVOICE.name())) {
				DoctorPatientInvoiceCollection receiptInvoiceCollection = doctorPatientInvoiceRepository
						.findOne(doctorPatientReceiptCollection.getInvoiceId());
				receiptInvoiceCollection.setBalanceAmount(
						receiptInvoiceCollection.getBalanceAmount() + doctorPatientReceiptCollection.getAmountPaid()
								+ doctorPatientReceiptCollection.getUsedAdvanceAmount());
				receiptInvoiceCollection.setUpdatedTime(new Date());
				doctorPatientInvoiceRepository.save(receiptInvoiceCollection);

				if (doctorPatientReceiptCollection.getAdvanceReceiptIdWithAmounts() != null
						&& !doctorPatientReceiptCollection.getAdvanceReceiptIdWithAmounts().isEmpty())
					for (AdvanceReceiptIdWithAmount receiptIdWithAmount : doctorPatientReceiptCollection
							.getAdvanceReceiptIdWithAmounts()) {
						DoctorPatientReceiptCollection patientReceiptCollection = doctorPatientReceiptRepository
								.findOne(receiptIdWithAmount.getReceiptId());
						patientReceiptCollection
								.setRemainingAdvanceAmount(patientReceiptCollection.getRemainingAdvanceAmount()
										+ receiptIdWithAmount.getUsedAdvanceAmount());
						patientReceiptCollection.setUpdatedTime(new Date());
						doctorPatientReceiptRepository.save(patientReceiptCollection);
					}
				doctorPatientReceiptCollection.setBalanceAmount(receiptInvoiceCollection.getBalanceAmount());

			} else {
				List<DoctorPatientReceiptCollection> patientReceiptCollections = doctorPatientReceiptRepository
						.findAllByAdvanceId(doctorPatientReceiptCollection.getId());

				if (patientReceiptCollections != null && !patientReceiptCollections.isEmpty())
					for (DoctorPatientReceiptCollection receiptCollection : patientReceiptCollections) {
						List<AdvanceReceiptIdWithAmount> removeCollections = new ArrayList<AdvanceReceiptIdWithAmount>();
						Double usedAdvanceAmt = 0.0;
						for (AdvanceReceiptIdWithAmount advanceReceiptIdWithAmount : receiptCollection
								.getAdvanceReceiptIdWithAmounts()) {
							if (advanceReceiptIdWithAmount.getReceiptId().toString()
									.equalsIgnoreCase(doctorPatientReceiptCollection.getId().toString())) {
								usedAdvanceAmt = usedAdvanceAmt + advanceReceiptIdWithAmount.getUsedAdvanceAmount();
								removeCollections.add(advanceReceiptIdWithAmount);
							}
						}
						receiptCollection.getAdvanceReceiptIdWithAmounts().remove(removeCollections);
						receiptCollection.setUpdatedTime(new Date());

						DoctorPatientInvoiceCollection receiptInvoiceCollection = doctorPatientInvoiceRepository
								.findOne(receiptCollection.getInvoiceId());
						receiptInvoiceCollection
								.setBalanceAmount(receiptInvoiceCollection.getBalanceAmount() + usedAdvanceAmt);
						receiptInvoiceCollection.setUpdatedTime(new Date());
						doctorPatientInvoiceRepository.save(receiptInvoiceCollection);

						receiptCollection.setBalanceAmount(receiptInvoiceCollection.getBalanceAmount());
						doctorPatientReceiptRepository.save(receiptCollection);
					}
			}

			DoctorPatientLedgerCollection patientLedgerCollection = doctorPatientLedgerRepository
					.findByReceiptId(doctorPatientReceiptCollection.getId());
			patientLedgerCollection.setDiscarded(discarded);
			patientLedgerCollection.setUpdatedTime(new Date());
			doctorPatientLedgerRepository.save(patientLedgerCollection);

			DoctorPatientDueAmountCollection amountCollection = doctorPatientDueAmountRepository.find(
					doctorPatientReceiptCollection.getPatientId(), doctorPatientReceiptCollection.getDoctorId(),
					doctorPatientReceiptCollection.getLocationId(), doctorPatientReceiptCollection.getHospitalId());
			amountCollection.setUpdatedTime(new Date());
			amountCollection
					.setDueAmount(amountCollection.getDueAmount() + doctorPatientReceiptCollection.getAmountPaid());
			doctorPatientDueAmountRepository.save(amountCollection);

			doctorPatientReceiptCollection.setUpdatedTime(new Date());
			doctorPatientReceiptCollection.setDiscarded(discarded);
			doctorPatientReceiptRepository.save(doctorPatientReceiptCollection);
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
					.is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId)).and("discarded")
					.is(false).and("isPatientDiscarded").ne(true);
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
				doctorPatientInvoiceCollection.setCreatedBy(
						(!DPDoctorUtils.anyStringEmpty(userCollection.getTitle()) ? userCollection.getTitle() + " "
								: "") + userCollection.getFirstName());
				doctorsMap.put(request.getDoctorId(), userCollection);
			}
			doctorPatientInvoiceCollection.setCreatedTime(new Date());
			if (doctorPatientInvoiceCollection.getInvoiceDate() == null)
				doctorPatientInvoiceCollection.setInvoiceDate(new Date());
			LocationCollection locationCollection = locationRepository.findOne(new ObjectId(request.getLocationId()));
			if (locationCollection == null)
				throw new BusinessException(ServiceError.InvalidInput, "Invalid Location Id");
			doctorPatientInvoiceCollection
					.setUniqueInvoiceId(locationCollection.getInvoiceInitial() + ((int) mongoTemplate.count(
							new Query(new Criteria("locationId").is(doctorPatientInvoiceCollection.getLocationId())
									.and("hospitalId").is(doctorPatientInvoiceCollection.getHospitalId())),
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

				InventoryItem inventoryItem = inventoryService.getInventoryItemByResourceId(request.getLocationId(),
						request.getHospitalId(), invoiceItemResponse.getItemId());
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
			if (doctorPatientReceiptCollection.getReceivedDate() == null)
				doctorPatientReceiptCollection.setReceivedDate(new Date());
			doctorPatientReceiptCollection
					.setUniqueReceiptId(locationCollection.getReceiptInitial() + ((int) mongoTemplate.count(
							new Query(new Criteria("locationId").is(doctorPatientReceiptCollection.getLocationId())
									.and("hospitalId").is(doctorPatientReceiptCollection.getHospitalId())),
							DoctorPatientReceiptCollection.class) + 1));

			doctorPatientReceiptCollection.setReceiptType(ReceiptType.INVOICE);

			List<AdvanceReceiptIdWithAmount> receiptIdWithAmounts = doctorPatientReceiptCollection
					.getAdvanceReceiptIdWithAmounts();
			if (request.getUsedAdvanceAmount() != null && request.getUsedAdvanceAmount() > 0) {

				List<DoctorPatientReceiptCollection> receiptsOfAdvancePayment = doctorPatientReceiptRepository
						.findAvailableAdvanceReceipts(ReceiptType.ADVANCE.name(),
								doctorPatientInvoiceCollection.getDoctorId(),
								doctorPatientInvoiceCollection.getLocationId(),
								doctorPatientInvoiceCollection.getHospitalId(),
								doctorPatientInvoiceCollection.getPatientId(), new Sort(Direction.ASC, "createdTime"));
				if (receiptsOfAdvancePayment == null || receiptsOfAdvancePayment.isEmpty())
					throw new BusinessException(ServiceError.InvalidInput, "Advance Amount is not available");

				Double advanceAmountToBeUsed = request.getUsedAdvanceAmount() != null ? request.getUsedAdvanceAmount()
						: 0.0;
				for (DoctorPatientReceiptCollection receiptCollection : receiptsOfAdvancePayment) {
					AdvanceReceiptIdWithAmount invoiceIdWithAmount = new AdvanceReceiptIdWithAmount();
					invoiceIdWithAmount.setUniqueReceiptId(receiptCollection.getUniqueReceiptId());
					invoiceIdWithAmount.setReceiptId(receiptCollection.getId());
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
						if (receiptIdWithAmounts == null || receiptIdWithAmounts.isEmpty()) {
							receiptIdWithAmounts = new ArrayList<AdvanceReceiptIdWithAmount>();
						}

						receiptIdWithAmounts.add(invoiceIdWithAmount);
						receiptCollection.setUpdatedTime(new Date());

						doctorPatientReceiptRepository.save(receiptCollection);
					}
				}
				doctorPatientInvoiceCollection
						.setUsedAdvanceAmount(doctorPatientInvoiceCollection.getUsedAdvanceAmount()
								+ (request.getUsedAdvanceAmount() != null ? request.getUsedAdvanceAmount() : 0.0));
				doctorPatientReceiptCollection.setUsedAdvanceAmount(request.getUsedAdvanceAmount());
				doctorPatientInvoiceCollection.setBalanceAmount(doctorPatientInvoiceCollection.getBalanceAmount()
						- (request.getAmountPaid() != null ? request.getAmountPaid() : 0.0)
						- (request.getUsedAdvanceAmount() != null ? request.getUsedAdvanceAmount() : 0.0));
				doctorPatientReceiptCollection.setBalanceAmount(doctorPatientInvoiceCollection.getBalanceAmount());
			} else {
				doctorPatientInvoiceCollection
						.setUsedAdvanceAmount(doctorPatientInvoiceCollection.getUsedAdvanceAmount()
								+ (request.getUsedAdvanceAmount() != null ? request.getUsedAdvanceAmount() : 0.0));
				doctorPatientReceiptCollection.setUsedAdvanceAmount(request.getUsedAdvanceAmount());
				doctorPatientInvoiceCollection.setBalanceAmount(doctorPatientInvoiceCollection.getBalanceAmount()
						- (request.getAmountPaid() != null ? request.getAmountPaid() : 0.0)
						- (request.getUsedAdvanceAmount() != null ? request.getUsedAdvanceAmount() : 0.0));
				doctorPatientReceiptCollection.setBalanceAmount(doctorPatientInvoiceCollection.getBalanceAmount());
			}
			doctorPatientReceiptCollection.setAdvanceReceiptIdWithAmounts(receiptIdWithAmounts);
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
				doctorPatientDueAmountCollection.setDueAmount(
						doctorPatientDueAmountCollection.getDueAmount() + doctorPatientInvoiceCollection.getGrandTotal()
								- doctorPatientReceiptCollection.getAmountPaid());
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
					.is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId))
					.and("isPatientDiscarded").ne(true);

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
					.is(new ObjectId(hospitalId)).and("isPatientDiscarded").ne(true);

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
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("doctor_patient_invoice_cl", "invoiceId", "_id", "invoice"),
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$invoice").append("preserveNullAndEmptyArrays", true))),
						Aggregation.lookup("doctor_patient_receipt_cl", "receiptId", "_id", "receipt"),
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$receipt").append("preserveNullAndEmptyArrays", true))),
						Aggregation.skip((page) * size), Aggregation.limit(size),
						Aggregation.sort(new Sort(Direction.DESC, "createdTime")));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.lookup("doctor_patient_invoice_cl", "invoiceId", "_id", "invoice"),
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$invoice").append("preserveNullAndEmptyArrays", true))),
						Aggregation.lookup("doctor_patient_receipt_cl", "receiptId", "_id", "receipt"),
						new CustomAggregationOperation(new BasicDBObject("$unwind",
								new BasicDBObject("path", "$receipt").append("preserveNullAndEmptyArrays", true))),
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
					.is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId))
					.and("isPatientDiscarded").ne(true);
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
							Aggregation.newAggregation(Aggregation.match(criteria.and("discarded").is(false)),
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
			Boolean showTax = false;
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

				String serviceName = invoiceItem.getName() != null ? invoiceItem.getName() : "";
				String fieldName = "";
				if (invoiceItem.getTreatmentFields() != null && !invoiceItem.getTreatmentFields().isEmpty()) {
					String key = "";
					for (Fields treatmentFile : invoiceItem.getTreatmentFields()) {
						key = treatmentFile.getKey();
						if (!DPDoctorUtils.anyStringEmpty(key)) {
							if (key.equalsIgnoreCase("toothNumber")) {
								key = "Tooth No :";
							}
							if (key.equalsIgnoreCase("material")) {
								key = "Material :";
							}

							if (!DPDoctorUtils.anyStringEmpty(treatmentFile.getValue())) {
								fieldName = fieldName + "<br><font size='1'><i>" + key + treatmentFile.getValue()
										+ "</i></font>";
							}
						}
					}
				}
				serviceName = serviceName == "" ? "--" : serviceName + fieldName;
				invoiceItemJasperDetail.setServiceName(serviceName);

				if (invoiceItem.getQuantity() != null) {
					showInvoiceItemQuantity = true;
					String quantity = invoiceItem.getQuantity().getValue() + " ";
					if (invoiceItem.getQuantity().getType() != null)
						quantity = quantity + invoiceItem.getQuantity().getType().getDuration();
					invoiceItemJasperDetail.setQuantity(quantity);
				} else {
					invoiceItemJasperDetail.setQuantity("");
				}
				if (invoiceItem.getTax() != null) {
					invoiceItemJasperDetail
							.setTax(invoiceItem.getTax().getValue() + " " + invoiceItem.getTax().getUnit().getUnit());
					if (invoiceItem.getTax().getValue() > 0) {
						showTax = true;
					}
				} else {
					invoiceItemJasperDetail.setTax("--");
				}
				invoiceItemJasperDetail.setCost(invoiceItem.getCost() + "");

				if (invoiceItem.getDiscount() != null) {
					if (invoiceItem.getDiscount().getValue() > 0)
						showDiscount = true;
					invoiceItemJasperDetail.setDiscount(
							invoiceItem.getDiscount().getValue() + " " + invoiceItem.getDiscount().getUnit().getUnit());
				} else {
					invoiceItemJasperDetail.setDiscount("--");
				}

				invoiceItemJasperDetail.setTotal(invoiceItem.getFinalCost() + "");
				invoiceItemJasperDetails.add(invoiceItemJasperDetail);
			}
			parameters.put("showDiscount", showDiscount);
			parameters.put("showTax", showTax);
			parameters.put("showStatus", showStatus);
			parameters.put("showInvoiceItemQuantity", showInvoiceItemQuantity);
			parameters.put("items", invoiceItemJasperDetails);
			String total = "";
			if (doctorPatientInvoiceCollection.getTotalCost() > 0)
				total = "<b>Total Cost :</b> ₹" + doctorPatientInvoiceCollection.getTotalCost() + " &nbsp;&nbsp;&nbsp;";
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
				total = total + "<b>Grand Total :</b> ₹" + doctorPatientInvoiceCollection.getGrandTotal() + " &nbsp;";
			parameters.put("grandTotal", total);

			total = "<b>Paid:</b> ₹" + (doctorPatientInvoiceCollection.getGrandTotal()
					- doctorPatientInvoiceCollection.getBalanceAmount()) + " &nbsp;";
			parameters.put("paid", total);

			total = "<b>Balance:</b>  ₹" + doctorPatientInvoiceCollection.getBalanceAmount() + "  &nbsp;";
			parameters.put("balance", total);
			PrintSettingsCollection printSettings = printSettingsRepository.getSettings(
					doctorPatientInvoiceCollection.getDoctorId(), doctorPatientInvoiceCollection.getLocationId(),
					doctorPatientInvoiceCollection.getHospitalId(), ComponentType.ALL.getType());

			if (printSettings == null) {
				printSettings = new PrintSettingsCollection();
				DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
				BeanUtil.map(defaultPrintSettings, printSettings);
			}

			patientVisitService.generatePatientDetails(
					(printSettings != null && printSettings.getHeaderSetup() != null
							? printSettings.getHeaderSetup().getPatientDetails()
							: null),
					patient,
					"<b>INVID: </b>" + (doctorPatientInvoiceCollection.getUniqueInvoiceId() != null
							? doctorPatientInvoiceCollection.getUniqueInvoiceId()
							: "--"),
					patient.getLocalPatientName(), user.getMobileNumber(), parameters,
					doctorPatientInvoiceCollection.getCreatedTime() != null
							? doctorPatientInvoiceCollection.getCreatedTime()
							: new Date(),
					printSettings.getHospitalUId(), printSettings.getIsPidHasDate());
			patientVisitService.generatePrintSetup(parameters, printSettings,
					doctorPatientInvoiceCollection.getDoctorId());
			String pdfName = (user != null ? user.getFirstName() : "") + "INVOICE-"
					+ doctorPatientInvoiceCollection.getUniqueInvoiceId() + new Date().getTime();

			String layout = printSettings != null
					? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
					: "PORTRAIT";
			String pageSize = printSettings != null
					? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4")
					: "A4";
			Integer topMargin = printSettings != null
					? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getTopMargin() : 20)
					: 20;
			Integer bottonMargin = printSettings != null
					? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getBottomMargin() : 20)
					: 20;
			Integer leftMargin = printSettings != null
					? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != 20
							? printSettings.getPageSetup().getLeftMargin()
							: 20)
					: 20;
			Integer rightMargin = printSettings != null
					? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
							? printSettings.getPageSetup().getRightMargin()
							: 20)
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
				logger.warn("Reciept Id does not exist");
				throw new BusinessException(ServiceError.NotFound, "Reciept Id does not exist");
			}

		} catch (Exception e) {

			logger.error("Error while getting download Reciept" + e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while getting download Reciept " + e);
		}
		return response;
	}

	private JasperReportResponse createJasperForReceipt(DoctorPatientReceiptCollection doctorPatientReceiptCollection,
			PatientCollection patient, UserCollection user) throws IOException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperReportResponse response = null;
		String pattern = "dd/MM/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String userName = "";
		if (!DPDoctorUtils.allStringsEmpty(user.getTitle())) {
			userName = user.getTitle();
		}
		String content = "<br>Received with thanks from &nbsp;&nbsp; " + userName + user.getFirstName()
				+ "<br>The sum of Rupees:- " + doctorPatientReceiptCollection.getAmountPaid() + "<br> By "
				+ doctorPatientReceiptCollection.getModeOfPayment() + "&nbsp;&nbsp;&nbsp;On Date:-"
				+ simpleDateFormat.format(doctorPatientReceiptCollection.getReceivedDate());
		parameters.put("content", content);
		parameters.put("paid", "Rs.&nbsp;" + doctorPatientReceiptCollection.getAmountPaid());
		PrintSettingsCollection printSettings = printSettingsRepository.getSettings(
				doctorPatientReceiptCollection.getDoctorId(), doctorPatientReceiptCollection.getLocationId(),
				doctorPatientReceiptCollection.getHospitalId(), ComponentType.ALL.getType());

		if (printSettings == null) {
			printSettings = new PrintSettingsCollection();
			DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
			BeanUtil.map(defaultPrintSettings, printSettings);
		}

		patientVisitService.generatePatientDetails((printSettings != null
				&& printSettings.getHeaderSetup() != null ? printSettings.getHeaderSetup().getPatientDetails() : null),
				patient,
				"<b>RECEIPTID: </b>" + (doctorPatientReceiptCollection.getUniqueReceiptId() != null
						? doctorPatientReceiptCollection.getUniqueReceiptId()
						: "--"),
				patient.getLocalPatientName(), user.getMobileNumber(), parameters,
				doctorPatientReceiptCollection.getReceivedDate() != null
						? doctorPatientReceiptCollection.getReceivedDate()
						: doctorPatientReceiptCollection.getUpdatedTime(),
				printSettings.getHospitalUId(), printSettings.getIsPidHasDate());

		patientVisitService.generatePrintSetup(parameters, printSettings, doctorPatientReceiptCollection.getDoctorId());
		String pdfName = (user != null ? user.getFirstName() : "") + "RECEIPT-"
				+ doctorPatientReceiptCollection.getUniqueReceiptId() + new Date().getTime();

		String layout = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
				: "PORTRAIT";
		String pageSize = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4")
				: "A4";
		Integer topMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getTopMargin() : 20)
				: 20;
		Integer bottonMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getBottomMargin() : 20)
				: 20;
		Integer leftMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != 20
						? printSettings.getPageSetup().getLeftMargin()
						: 20)
				: 20;
		Integer rightMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
						? printSettings.getPageSetup().getRightMargin()
						: 20)
				: 20;
		response = jasperReportService.createPDF(ComponentType.RECEIPT, parameters, receiptA4FileName, layout, pageSize,
				topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));

		return response;
	}

	public void emailInvoice(String invoiceId, String doctorId, String locationId, String hospitalId,
			String emailAddress) {
		MailResponse mailResponse = null;
		DoctorPatientInvoiceCollection doctorPatientInvoiceCollection = null;
		MailAttachment mailAttachment = null;
		PatientCollection patient = null;
		UserCollection user = null;
		EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
		try {
			doctorPatientInvoiceCollection = doctorPatientInvoiceRepository.findOne(new ObjectId(invoiceId));
			if (doctorPatientInvoiceCollection != null) {
				if (doctorPatientInvoiceCollection.getDoctorId() != null
						&& doctorPatientInvoiceCollection.getHospitalId() != null
						&& doctorPatientInvoiceCollection.getLocationId() != null) {
					if (doctorPatientInvoiceCollection.getDoctorId().equals(doctorId)
							&& doctorPatientInvoiceCollection.getHospitalId().equals(hospitalId)
							&& doctorPatientInvoiceCollection.getLocationId().equals(locationId)) {

						user = userRepository.findOne(doctorPatientInvoiceCollection.getPatientId());
						patient = patientRepository.findByUserIdLocationIdAndHospitalId(
								doctorPatientInvoiceCollection.getPatientId(),
								doctorPatientInvoiceCollection.getLocationId(),
								doctorPatientInvoiceCollection.getHospitalId());
						user.setFirstName(patient.getLocalPatientName());
						emailTrackCollection.setDoctorId(doctorPatientInvoiceCollection.getDoctorId());
						emailTrackCollection.setHospitalId(doctorPatientInvoiceCollection.getHospitalId());
						emailTrackCollection.setLocationId(doctorPatientInvoiceCollection.getLocationId());
						emailTrackCollection.setType(ComponentType.INVOICE.getType());
						emailTrackCollection.setSubject("Invoice");
						if (user != null) {
							emailTrackCollection.setPatientName(patient.getLocalPatientName());
							emailTrackCollection.setPatientId(user.getId());
						}

						JasperReportResponse jasperReportResponse = createJasper(doctorPatientInvoiceCollection,
								patient, user);
						mailAttachment = new MailAttachment();
						mailAttachment.setAttachmentName(FilenameUtils.getName(jasperReportResponse.getPath()));
						mailAttachment.setFileSystemResource(jasperReportResponse.getFileSystemResource());
						UserCollection doctorUser = userRepository.findOne(new ObjectId(doctorId));
						LocationCollection locationCollection = locationRepository.findOne(new ObjectId(locationId));

						mailResponse = new MailResponse();
						mailResponse.setMailAttachment(mailAttachment);
						mailResponse.setDoctorName(doctorUser.getTitle() + " " + doctorUser.getFirstName());
						String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
								? locationCollection.getStreetAddress() + ", "
								: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
										? locationCollection.getLandmarkDetails() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
										? locationCollection.getLocality() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
										? locationCollection.getCity() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
										? locationCollection.getState() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
										? locationCollection.getCountry() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
										? locationCollection.getPostalCode()
										: "");

						if (address.charAt(address.length() - 2) == ',') {
							address = address.substring(0, address.length() - 2);
						}
						mailResponse.setClinicAddress(address);
						mailResponse.setClinicName(locationCollection.getLocationName());
						SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
						sdf.setTimeZone(TimeZone.getTimeZone("IST"));
						mailResponse
								.setMailRecordCreatedDate(sdf.format(doctorPatientInvoiceCollection.getCreatedTime()));
						mailResponse.setPatientName(user.getFirstName());
						emailTackService.saveEmailTrack(emailTrackCollection);

					} else {
						logger.warn("Invoice Id, doctorId, location Id, hospital Id does not match");
						throw new BusinessException(ServiceError.NotFound,
								"Invoice Id, doctorId, location Id, hospital Id does not match");
					}
				}

			} else {
				logger.warn("Invoice not found.Please check invoiceId.");
				throw new BusinessException(ServiceError.NoRecord, "Invoice not found.Please check invoiceId.");
			}

			String body = mailBodyGenerator.generateEMREmailBody(mailResponse.getPatientName(),
					mailResponse.getDoctorName(), mailResponse.getClinicName(), mailResponse.getClinicAddress(),
					mailResponse.getMailRecordCreatedDate(), "Invoice", "emrMailTemplate.vm");
			Boolean response = mailService.sendEmail(emailAddress, mailResponse.getDoctorName() + " sent you Invoice",
					body, mailResponse.getMailAttachment());
			if (response != null && mailResponse.getMailAttachment() != null
					&& mailResponse.getMailAttachment().getFileSystemResource() != null)
				if (mailResponse.getMailAttachment().getFileSystemResource().getFile().exists())
					mailResponse.getMailAttachment().getFileSystemResource().getFile().delete();
		} catch (Exception e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	public void emailReceipt(String receiptId, String doctorId, String locationId, String hospitalId,
			String emailAddress) {
		MailResponse mailResponse = null;
		DoctorPatientReceiptCollection doctorPatientReceiptCollection = null;
		MailAttachment mailAttachment = null;
		PatientCollection patient = null;
		UserCollection user = null;
		EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
		try {
			doctorPatientReceiptCollection = doctorPatientReceiptRepository.findOne(new ObjectId(receiptId));
			if (doctorPatientReceiptCollection != null) {
				if (doctorPatientReceiptCollection.getDoctorId() != null
						&& doctorPatientReceiptCollection.getHospitalId() != null
						&& doctorPatientReceiptCollection.getLocationId() != null) {
					if (doctorPatientReceiptCollection.getDoctorId().equals(doctorId)
							&& doctorPatientReceiptCollection.getHospitalId().equals(hospitalId)
							&& doctorPatientReceiptCollection.getLocationId().equals(locationId)) {

						user = userRepository.findOne(doctorPatientReceiptCollection.getPatientId());
						patient = patientRepository.findByUserIdLocationIdAndHospitalId(
								doctorPatientReceiptCollection.getPatientId(),
								doctorPatientReceiptCollection.getLocationId(),
								doctorPatientReceiptCollection.getHospitalId());
						user.setFirstName(patient.getLocalPatientName());
						emailTrackCollection.setDoctorId(doctorPatientReceiptCollection.getDoctorId());
						emailTrackCollection.setHospitalId(doctorPatientReceiptCollection.getHospitalId());
						emailTrackCollection.setLocationId(doctorPatientReceiptCollection.getLocationId());
						emailTrackCollection.setType(ComponentType.RECEIPT.getType());
						emailTrackCollection.setSubject("Receipt");
						if (user != null) {
							emailTrackCollection.setPatientName(patient.getLocalPatientName());
							emailTrackCollection.setPatientId(user.getId());
						}

						JasperReportResponse jasperReportResponse = createJasperForReceipt(
								doctorPatientReceiptCollection, patient, user);
						mailAttachment = new MailAttachment();
						mailAttachment.setAttachmentName(FilenameUtils.getName(jasperReportResponse.getPath()));
						mailAttachment.setFileSystemResource(jasperReportResponse.getFileSystemResource());
						UserCollection doctorUser = userRepository.findOne(new ObjectId(doctorId));
						LocationCollection locationCollection = locationRepository.findOne(new ObjectId(locationId));

						mailResponse = new MailResponse();
						mailResponse.setMailAttachment(mailAttachment);
						mailResponse.setDoctorName(doctorUser.getTitle() + " " + doctorUser.getFirstName());
						String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
								? locationCollection.getStreetAddress() + ", "
								: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
										? locationCollection.getLandmarkDetails() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
										? locationCollection.getLocality() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
										? locationCollection.getCity() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
										? locationCollection.getState() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
										? locationCollection.getCountry() + ", "
										: "")
								+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
										? locationCollection.getPostalCode()
										: "");

						if (address.charAt(address.length() - 2) == ',') {
							address = address.substring(0, address.length() - 2);
						}
						mailResponse.setClinicAddress(address);
						mailResponse.setClinicName(locationCollection.getLocationName());
						SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
						sdf.setTimeZone(TimeZone.getTimeZone("IST"));
						mailResponse
								.setMailRecordCreatedDate(sdf.format(doctorPatientReceiptCollection.getCreatedTime()));
						mailResponse.setPatientName(user.getFirstName());
						emailTackService.saveEmailTrack(emailTrackCollection);

					} else {
						logger.warn("Receipt Id, doctorId, location Id, hospital Id does not match");
						throw new BusinessException(ServiceError.NotFound,
								"Receipt Id, doctorId, location Id, hospital Id does not match");
					}
				}

			} else {
				logger.warn("Receipt not found.Please check invoiceId.");
				throw new BusinessException(ServiceError.NoRecord, "Receipt not found.Please check invoiceId.");
			}

			String body = mailBodyGenerator.generateEMREmailBody(mailResponse.getPatientName(),
					mailResponse.getDoctorName(), mailResponse.getClinicName(), mailResponse.getClinicAddress(),
					mailResponse.getMailRecordCreatedDate(), "Receipt", "emrMailTemplate.vm");
			Boolean response = mailService.sendEmail(emailAddress, mailResponse.getDoctorName() + " sent you Receipt",
					body, mailResponse.getMailAttachment());
			if (response != null && mailResponse.getMailAttachment() != null
					&& mailResponse.getMailAttachment().getFileSystemResource() != null)
				if (mailResponse.getMailAttachment().getFileSystemResource().getFile().exists())
					mailResponse.getMailAttachment().getFileSystemResource().getFile().delete();
		} catch (Exception e) {
			logger.error(e);
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
	}

	@Override
	public Boolean sendDueRemainderToPatient(String doctorId, String locationId, String hospitalId, String patientId,
			String mobileNumber) {
		Boolean response = false;
		try {

			DoctorPatientDueAmountCollection doctorPatientDueAmountCollection = doctorPatientDueAmountRepository.find(
					new ObjectId(patientId), new ObjectId(doctorId), new ObjectId(locationId),
					new ObjectId(hospitalId));
			if (doctorPatientDueAmountCollection != null) {
				UserCollection patient = userRepository.findOne(new ObjectId(patientId));

				LocationCollection locationCollection = locationRepository.findOne(new ObjectId(locationId));
				SMSTrackDetail smsTrackDetail = new SMSTrackDetail();
				smsTrackDetail.setDoctorId(new ObjectId(doctorId));
				smsTrackDetail.setHospitalId(new ObjectId(hospitalId));
				smsTrackDetail.setLocationId(new ObjectId(locationId));
				smsTrackDetail.setType(ComponentType.DUE_AMOUNT.getType());
				SMSDetail smsDetail = new SMSDetail();
				smsDetail.setUserId(new ObjectId(patientId));
				smsDetail.setUserName(patient.getFirstName());
				SMS sms = new SMS();
				String message = dueAmountRemainderSMS;
				sms.setSmsText(message.replace("{patientName}", patient.getFirstName())
						.replace("{clinicNumber}",
								locationCollection.getClinicNumber() != null ? locationCollection.getClinicNumber()
										: "")
						.replace("{clinicName}", locationCollection.getLocationName())
						.replace("{dueAmount}", doctorPatientDueAmountCollection.getDueAmount().toString()));
				SMSAddress smsAddress = new SMSAddress();
				smsAddress.setRecipient(mobileNumber);
				sms.setSmsAddress(smsAddress);
				smsDetail.setSms(sms);
				smsDetail.setDeliveryStatus(SMSStatus.IN_PROGRESS);
				List<SMSDetail> smsDetails = new ArrayList<SMSDetail>();
				smsDetails.add(smsDetail);
				smsTrackDetail.setSmsDetails(smsDetails);
				smsServices.sendSMS(smsTrackDetail, true);
				response = true;
			}
		} catch (BusinessException be) {
			logger.error(be);
			throw be;
		} catch (Exception e) {
			logger.error("Error while getting invoices" + e);
			throw new BusinessException(ServiceError.Unknown, "Error while getting invoices" + e);
		}
		return response;
	}

	/*
	 * <<<<<<< HEAD private void updateInventoryItem(String itemId, String
	 * locationId , String hospitalId , Quantity quantity) {
	 * InventoryStockCollection ======= private void updateInventoryItem(String
	 * itemId, String locationId , String hospitalId , Quantity quantity) {
	 * InventoryStockCollection >>>>>>> e5408604ee47a0585cf6f3af38163a2902fe3750
	 * inventoryStockCollection =
	 * inventoryStockRepository.getByResourceIdLocationIdHospitalId(itemId,
	 * locationId, hospitalId); if(inventoryStockCollection != null) { Long
	 * stockCount = inventoryStockCollection.g } }
	 */

	private void createInventoryStock(String resourceId, String itemId, InventoryBatch inventoryBatch, String patientId,
			String doctorId, String locationId, String hospitalId, Integer inventoryQuantity, String invoiceId,
			String stockType) {
		InventoryStock inventoryStock = new InventoryStock();
		inventoryStock.setInventoryBatch(inventoryBatch);
		inventoryStock.setItemId(itemId);
		inventoryStock.setResourceId(resourceId);
		inventoryStock.setPatientId(patientId);
		inventoryStock.setDoctorId(doctorId);
		inventoryStock.setLocationId(locationId);
		inventoryStock.setHospitalId(hospitalId);
		inventoryStock.setInvoiceId(invoiceId);
		inventoryStock.setStockType(stockType);
		if (inventoryQuantity != null) {
			inventoryStock.setQuantity(inventoryQuantity.longValue());
		}
		inventoryStock = inventoryService.addInventoryStock(inventoryStock);
	}

	@Override
	public String downloadMultipleReceipt(List<String> ids) {
		String response = null;
		try {
			List<DoctorPatientReceiptLookupResponse> doctorPatientReceiptLookupResponses = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(ids)),
							Aggregation.lookup("doctor_patient_invoice_cl", "invoiceId", "_id", "invoiceCollection"),
							new CustomAggregationOperation(new BasicDBObject("$unwind",
									new BasicDBObject("path", "$invoiceCollection").append("preserveNullAndEmptyArrays",
											true))),
							Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
							Aggregation.unwind("patient"),
							new CustomAggregationOperation(new BasicDBObject("$redact", new BasicDBObject("$cond",
									new BasicDBObject("if",
											new BasicDBObject("$eq",
													Arrays.asList("$patient.locationId", "$locationId")))
															.append("then", "$$KEEP").append("else", "$$PRUNE")))),
							Aggregation.lookup("user_cl", "patientId", "_id", "patientUser"),
							Aggregation.unwind("patientUser"),
							Aggregation.sort(new Sort(Direction.ASC, "receivedDate"))),
					DoctorPatientReceiptCollection.class, DoctorPatientReceiptLookupResponse.class).getMappedResults();
			if (doctorPatientReceiptLookupResponses != null && !doctorPatientReceiptLookupResponses.isEmpty()) {

				JasperReportResponse jasperReportResponse = createJasperForMultipleReceipt(
						doctorPatientReceiptLookupResponses);
				if (jasperReportResponse != null)
					response = getFinalImageURL(jasperReportResponse.getPath());
				if (jasperReportResponse != null && jasperReportResponse.getFileSystemResource() != null)
					if (jasperReportResponse.getFileSystemResource().getFile().exists())
						jasperReportResponse.getFileSystemResource().getFile().delete();
			} else {
				logger.warn("Reciept Ids does not exist");
				throw new BusinessException(ServiceError.NotFound, "Reciept Ids does not exist");
			}

		} catch (Exception e) {
			logger.error("Error while downloading Reciepts" + e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while downloading Reciepts" + e);
		}
		return response;
	}

	private JasperReportResponse createJasperForMultipleReceipt(
			List<DoctorPatientReceiptLookupResponse> doctorPatientReceiptLookupResponses)
			throws NumberFormatException, IOException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		JasperReportResponse response = null;
		String pattern = "dd/MM/yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("IST"));
		List<ReceiptJasperDetails> receiptJasperDetails = new ArrayList<ReceiptJasperDetails>();

		PrintSettingsCollection printSettings = printSettingsRepository.getSettings(
				new ObjectId(doctorPatientReceiptLookupResponses.get(0).getDoctorId()),
				new ObjectId(doctorPatientReceiptLookupResponses.get(0).getLocationId()),
				new ObjectId(doctorPatientReceiptLookupResponses.get(0).getHospitalId()), ComponentType.ALL.getType());

		if (printSettings == null) {
			printSettings = new PrintSettingsCollection();
			DefaultPrintSettings defaultPrintSettings = new DefaultPrintSettings();
			BeanUtil.map(defaultPrintSettings, printSettings);
		}
		Double grandTotal = 0.0, totalPaid = 0.0, totalBalance = 0.0;
		List<String> invoiceIds = new ArrayList<String>();
		for (DoctorPatientReceiptLookupResponse doctorPatientReceiptLookupResponse : doctorPatientReceiptLookupResponses) {
			totalPaid = totalPaid + doctorPatientReceiptLookupResponse.getAmountPaid();
			ReceiptJasperDetails details = new ReceiptJasperDetails();
			details.setDate(simpleDateFormat.format(doctorPatientReceiptLookupResponse.getReceivedDate()));

			if (doctorPatientReceiptLookupResponse.getReceiptType().name()
					.equalsIgnoreCase(ReceiptType.ADVANCE.name())) {
				details.setTotal("--");
				details.setBalance("--");
				details.setPaid(doctorPatientReceiptLookupResponse.getAmountPaid() + "");
				details.setProcedure("ADVANCE");
				receiptJasperDetails.add(details);
			} else {
				if (doctorPatientReceiptLookupResponse.getInvoiceCollection() != null) {

					if (!invoiceIds
							.contains(doctorPatientReceiptLookupResponse.getInvoiceCollection().getId().toString())) {
						invoiceIds.add(doctorPatientReceiptLookupResponse.getInvoiceCollection().getId().toString());
						grandTotal = grandTotal
								+ doctorPatientReceiptLookupResponse.getInvoiceCollection().getGrandTotal();
					}
					Double total = doctorPatientReceiptLookupResponse.getInvoiceCollection().getGrandTotal();
					String paid = (doctorPatientReceiptLookupResponse.getAmountPaid() != null)
							? doctorPatientReceiptLookupResponse.getAmountPaid() + ""
							: "";
					if (doctorPatientReceiptLookupResponse.getUsedAdvanceAmount() != null
							&& doctorPatientReceiptLookupResponse.getUsedAdvanceAmount() != 0.0) {
						paid = ((!DPDoctorUtils.anyStringEmpty(paid) && !paid.equalsIgnoreCase("0.0")) ? paid + "+"
								: "") + doctorPatientReceiptLookupResponse.getUsedAdvanceAmount() + "(From Advance)";
					}
					Double balance = doctorPatientReceiptLookupResponse.getBalanceAmount();

					details.setTotal((total == null) ? "" : total + "");
					details.setBalance((balance == null) ? "" : balance + "");
					details.setPaid((paid == null) ? "" : paid + "");
					if (doctorPatientReceiptLookupResponse.getInvoiceCollection().getInvoiceItems() != null) {
						for (InvoiceItem invoiceItem : doctorPatientReceiptLookupResponse.getInvoiceCollection()
								.getInvoiceItems()) {
							details.setProcedure(invoiceItem.getName() + " ("
									+ doctorPatientReceiptLookupResponse.getUniqueInvoiceId() + ")");
							receiptJasperDetails.add(details);
							details = new ReceiptJasperDetails();
							details.setTotal("");
							details.setBalance("");
							details.setPaid("");
							details.setDate("");
						}
					}

				}
			}

		}
		totalBalance = grandTotal - totalPaid;
		parameters.put("receipts", receiptJasperDetails);
		parameters.put("grandTotal", grandTotal);
		parameters.put("totalPaid", totalPaid);
		parameters.put("totalBalance", totalBalance);
		PatientCollection patient = doctorPatientReceiptLookupResponses.get(0).getPatient();
		UserCollection user = doctorPatientReceiptLookupResponses.get(0).getPatientUser();
		patientVisitService
				.generatePatientDetails(
						(printSettings != null && printSettings.getHeaderSetup() != null
								? printSettings.getHeaderSetup().getPatientDetails()
								: null),
						patient, null, patient.getLocalPatientName(), user.getMobileNumber(), parameters,
						doctorPatientReceiptLookupResponses.get(0).getCreatedTime() != null
								? doctorPatientReceiptLookupResponses.get(0).getCreatedTime()
								: new Date(),
						printSettings.getHospitalUId(), printSettings.getIsPidHasDate());
		patientVisitService.generatePrintSetup(parameters, printSettings,
				new ObjectId(doctorPatientReceiptLookupResponses.get(0).getDoctorId()));
		String pdfName = (user != null ? user.getFirstName() : "") + "MULTIPLERECEIPT-" + new Date().getTime();

		String layout = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getLayout() : "PORTRAIT")
				: "PORTRAIT";
		String pageSize = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getPageSize() : "A4")
				: "A4";
		Integer topMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getTopMargin() : 20)
				: 20;
		Integer bottonMargin = printSettings != null
				? (printSettings.getPageSetup() != null ? printSettings.getPageSetup().getBottomMargin() : 20)
				: 20;
		Integer leftMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getLeftMargin() != 20
						? printSettings.getPageSetup().getLeftMargin()
						: 20)
				: 20;
		Integer rightMargin = printSettings != null
				? (printSettings.getPageSetup() != null && printSettings.getPageSetup().getRightMargin() != null
						? printSettings.getPageSetup().getRightMargin()
						: 20)
				: 20;
		response = jasperReportService.createPDF(ComponentType.MULTIPLE_RECEIPT, parameters, multipleReceiptFileName,
				layout, pageSize, topMargin, bottonMargin, leftMargin, rightMargin,
				Integer.parseInt(parameters.get("contentFontSize").toString()), pdfName.replaceAll("\\s+", ""));

		return response;
	}

	@Override
	public void emailMultipleReceipt(List<String> ids, String emailAddress) {
		MailResponse mailResponse = null;
		MailAttachment mailAttachment = null;
		PatientCollection patient = null;
		UserCollection user = null;
		EmailTrackCollection emailTrackCollection = new EmailTrackCollection();
		try {
			List<DoctorPatientReceiptLookupResponse> doctorPatientReceiptLookupResponses = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(new Criteria("id").in(ids)),
							Aggregation.lookup("doctor_patient_invoice_cl", "invoiceId", "_id", "invoiceCollection"),
							new CustomAggregationOperation(new BasicDBObject("$unwind",
									new BasicDBObject("path", "$invoiceCollection").append("preserveNullAndEmptyArrays",
											true))),
							Aggregation.lookup("patient_cl", "patientId", "userId", "patient"),
							Aggregation.unwind("patient"),
							new CustomAggregationOperation(new BasicDBObject("$redact", new BasicDBObject("$cond",
									new BasicDBObject("if",
											new BasicDBObject("$eq",
													Arrays.asList("$patient.locationId", "$locationId")))
															.append("then", "$$KEEP").append("else", "$$PRUNE")))),
							Aggregation.lookup("user_cl", "patientId", "_id", "patientUser"),
							Aggregation.unwind("patientUser"),
							Aggregation.sort(new Sort(Direction.ASC, "receivedDate"))),
					DoctorPatientReceiptCollection.class, DoctorPatientReceiptLookupResponse.class).getMappedResults();

			if (doctorPatientReceiptLookupResponses != null && !doctorPatientReceiptLookupResponses.isEmpty()) {
				user = doctorPatientReceiptLookupResponses.get(0).getPatientUser();
				patient = doctorPatientReceiptLookupResponses.get(0).getPatient();
				user.setFirstName(patient.getLocalPatientName());
				emailTrackCollection
						.setDoctorId(new ObjectId(doctorPatientReceiptLookupResponses.get(0).getDoctorId()));
				emailTrackCollection
						.setHospitalId(new ObjectId(doctorPatientReceiptLookupResponses.get(0).getHospitalId()));
				emailTrackCollection
						.setLocationId(new ObjectId(doctorPatientReceiptLookupResponses.get(0).getLocationId()));
				emailTrackCollection.setType(ComponentType.RECEIPT.getType());
				emailTrackCollection.setSubject("Receipts");
				if (user != null) {
					emailTrackCollection.setPatientName(patient.getLocalPatientName());
					emailTrackCollection.setPatientId(user.getId());
				}

				JasperReportResponse jasperReportResponse = createJasperForMultipleReceipt(
						doctorPatientReceiptLookupResponses);

				mailAttachment = new MailAttachment();
				mailAttachment.setAttachmentName(FilenameUtils.getName(jasperReportResponse.getPath()));
				mailAttachment.setFileSystemResource(jasperReportResponse.getFileSystemResource());
				UserCollection doctorUser = userRepository.findOne(emailTrackCollection.getDoctorId());
				LocationCollection locationCollection = locationRepository
						.findOne(emailTrackCollection.getLocationId());

				mailResponse = new MailResponse();
				mailResponse.setMailAttachment(mailAttachment);
				mailResponse.setDoctorName(doctorUser.getTitle() + " " + doctorUser.getFirstName());
				String address = (!DPDoctorUtils.anyStringEmpty(locationCollection.getStreetAddress())
						? locationCollection.getStreetAddress() + ", "
						: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLandmarkDetails())
								? locationCollection.getLandmarkDetails() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getLocality())
								? locationCollection.getLocality() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCity())
								? locationCollection.getCity() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getState())
								? locationCollection.getState() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getCountry())
								? locationCollection.getCountry() + ", "
								: "")
						+ (!DPDoctorUtils.anyStringEmpty(locationCollection.getPostalCode())
								? locationCollection.getPostalCode()
								: "");

				if (address.charAt(address.length() - 2) == ',') {
					address = address.substring(0, address.length() - 2);
				}
				mailResponse.setClinicAddress(address);
				mailResponse.setClinicName(locationCollection.getLocationName());
				mailResponse.setPatientName(user.getFirstName());
				emailTackService.saveEmailTrack(emailTrackCollection);

				String body = mailBodyGenerator.generateEMREmailBody(mailResponse.getPatientName(),
						mailResponse.getDoctorName(), mailResponse.getClinicName(), mailResponse.getClinicAddress(), "",
						"Receipts", "multipleEmrMailTemplate.vm");
				Boolean response = mailService.sendEmail(emailAddress,
						mailResponse.getDoctorName() + " sent you Receipts", body, mailResponse.getMailAttachment());
				if (response != null && mailResponse.getMailAttachment() != null
						&& mailResponse.getMailAttachment().getFileSystemResource() != null)
					if (mailResponse.getMailAttachment().getFileSystemResource().getFile().exists())
						mailResponse.getMailAttachment().getFileSystemResource().getFile().delete();
			} else {
				logger.warn("Reciept Ids does not exist");
				throw new BusinessException(ServiceError.NotFound, "Reciept Ids does not exist");
			}
		} catch (Exception e) {
			logger.error("Error while emailing Reciepts" + e);
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, "Error while emailing Reciepts" + e);
		}
	}

	@Override
	@Transactional
	public Boolean changeInvoiceTreatmentStatus(InvoiceItemChangeStatusRequest request) {
		Boolean status = false;
		DoctorPatientInvoiceCollection doctorPatientInvoiceCollection = null;
		try {
			doctorPatientInvoiceCollection = doctorPatientInvoiceRepository
					.findOne(new ObjectId(request.getInvoiceId()));
			if (doctorPatientInvoiceCollection != null) {
				for (InvoiceItem invoiceItem : doctorPatientInvoiceCollection.getInvoiceItems()) {
					if (invoiceItem.getItemId().equals(new ObjectId(request.getItemId()))) {
						invoiceItem.setStatus(request.getStatus());
						status = true;
					}
				}
				doctorPatientInvoiceCollection = doctorPatientInvoiceRepository.save(doctorPatientInvoiceCollection);
			} else {
				throw new BusinessException(ServiceError.NoRecord, "Invoice not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return status;
	}

	@Override
	public DoctorExpense addEditDoctorExpense(DoctorExpense request) {
		DoctorExpense response = null;
		try {
			DoctorExpenseCollection expenseCollection = null;
			UserCollection userCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				expenseCollection = doctorExpenseRepository.findOne(new ObjectId(request.getId()));
				if (expenseCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "Doctor Expense not found with Id");
				}
				request.setCreatedBy(expenseCollection.getCreatedBy());
				request.setCreatedTime(expenseCollection.getCreatedTime());
				request.setUpdatedTime(new Date());
				request.setUniqueExpenseId(expenseCollection.getUniqueExpenseId());
				expenseCollection = new DoctorExpenseCollection();
				BeanUtil.map(request, expenseCollection);
			} else {
				userCollection = userRepository.findOne(new ObjectId(request.getDoctorId()));
				if (userCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "Doctor found with DoctorId");
				}
				expenseCollection = new DoctorExpenseCollection();
				BeanUtil.map(request, expenseCollection);
				expenseCollection.setUniqueExpenseId(
						UniqueIdInitial.EXPENSE.getInitial() + "-" + DPDoctorUtils.generateRandomId());
				expenseCollection.setCreatedBy(
						(DPDoctorUtils.anyStringEmpty(userCollection.getTitle()) ? "Dr." : userCollection.getTitle())
								+ " " + userCollection.getFirstName());
				expenseCollection.setCreatedTime(new Date());
				expenseCollection.setAdminCreatedTime(new Date());
			}
			expenseCollection = doctorExpenseRepository.save(expenseCollection);
			response = new DoctorExpense();
			BeanUtil.map(expenseCollection, response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public List<DoctorExpense> getDoctorExpenses(String expenseType, int page, int size, String doctorId,
			String locationId, String hospitalId, String updatedTime, Boolean discarded, String paymentMode) {
		List<DoctorExpense> response = null;
		try {
			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));

			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
				criteria.and("locationId").is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));
			if (!discarded)
				criteria.and("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(expenseType)) {
				criteria.and("expenseType").is(expenseType.toUpperCase());
			}
			if (!DPDoctorUtils.anyStringEmpty(expenseType)) {
				criteria.and("modeOfPayment").is(paymentMode.toUpperCase());
			}
			if (size > 0) {
				response = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(new Sort(Sort.Direction.DESC, "toDate")),
								Aggregation.skip((page) * size), Aggregation.limit(size)),
						DoctorExpenseCollection.class, DoctorExpense.class).getMappedResults();
			} else {
				response = mongoTemplate.aggregate(
						Aggregation.newAggregation(Aggregation.match(criteria),
								Aggregation.sort(new Sort(Sort.Direction.DESC, "toDate"))),
						DoctorExpenseCollection.class, DoctorExpense.class).getMappedResults();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public Double countDoctorExpenses(String expenseType, String doctorId, String locationId, String hospitalId,
			String updatedTime, Boolean discarded, String paymentMode) {
		Double response = 0.0;
		try {
			long createdTimestamp = Long.parseLong(updatedTime);

			Criteria criteria = new Criteria("updatedTime").gt(new Date(createdTimestamp));

			if (!DPDoctorUtils.anyStringEmpty(doctorId))
				criteria.and("doctorId").is(new ObjectId(doctorId));
			if (!DPDoctorUtils.anyStringEmpty(locationId, hospitalId))
				criteria.and("locationId").is(new ObjectId(locationId)).and("hospitalId").is(new ObjectId(hospitalId));
			if (!discarded)
				criteria.and("discarded").is(discarded);
			if (!DPDoctorUtils.anyStringEmpty(expenseType)) {
				criteria.and("expenseType").is(expenseType.toUpperCase());
			}
			if (!DPDoctorUtils.anyStringEmpty(expenseType)) {
				criteria.and("modeOfPayment").is(paymentMode.toUpperCase());
			}
			DoctorExpense doctorExpense = mongoTemplate.aggregate(
					Aggregation.newAggregation(Aggregation.match(criteria),
							new CustomAggregationOperation(new BasicDBObject("$group",
									new BasicDBObject("_id",
											new BasicDBObject("locationId", "$locationId").append("hospitalId",
													"hospitalId")).append("cost",
															new BasicDBObject("$sum", "$cost"))))),
					DoctorExpenseCollection.class, DoctorExpense.class).getUniqueMappedResult();
			if (doctorExpense != null) {
				response = doctorExpense.getCost();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public DoctorExpense deleteDoctorExpense(String expenseId, Boolean discarded) {
		DoctorExpense response = null;
		try {

			DoctorExpenseCollection expenseCollection = doctorExpenseRepository.findOne(new ObjectId(expenseId));
			if (expenseCollection == null) {
				throw new BusinessException(ServiceError.NoRecord, "Doctor Expense not found with Id");
			}
			expenseCollection.setDiscarded(discarded);
			expenseCollection.setUpdatedTime(new Date());
			response = new DoctorExpense();
			BeanUtil.map(expenseCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public DoctorExpense getDoctorExpense(String expenseId) {
		DoctorExpense response = null;
		try {

			DoctorExpenseCollection expenseCollection = doctorExpenseRepository.findOne(new ObjectId(expenseId));
			response = new DoctorExpense();
			BeanUtil.map(expenseCollection, response);

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException(ServiceError.Unknown, e.getMessage());
		}
		return response;
	}

	@Override
	public ExpenseType addEditExpenseType(ExpenseType request) {
		ExpenseType response = null;
		try {
			ExpenseTypeCollection expenseTypeCollection = null;
			UserCollection userCollection = null;
			if (!DPDoctorUtils.anyStringEmpty(request.getId())) {
				expenseTypeCollection = expenseTypeRepository.findOne(new ObjectId(request.getId()));
				if (expenseTypeCollection == null) {
					throw new BusinessException(ServiceError.InvalidInput, "Expense Not Found with Id");
				}
				request.setCreatedBy(expenseTypeCollection.getCreatedBy());
				request.setCreatedTime(expenseTypeCollection.getCreatedTime());
				expenseTypeCollection = new ExpenseTypeCollection();
				BeanUtil.map(request, expenseTypeCollection);

			} else {
				userCollection = userRepository.findOne(new ObjectId(request.getDoctorId()));
				if (userCollection == null) {
					throw new BusinessException(ServiceError.NoRecord, "Doctor found with DoctorId");
				}
				expenseTypeCollection = new ExpenseTypeCollection();
				expenseTypeCollection.setCreatedBy(
						(DPDoctorUtils.anyStringEmpty(userCollection.getTitle()) ? "Dr." : userCollection.getTitle())
								+ " " + userCollection.getFirstName());
				expenseTypeCollection.setCreatedTime(new Date());

				BeanUtil.map(request, expenseTypeCollection);
			}
			expenseTypeCollection = expenseTypeRepository.save(expenseTypeCollection);
			ESExpenseTypeDocument esDocument = new ESExpenseTypeDocument();
			esExpenseTypeService.addEditExpenseType(esDocument);
			response = new ExpenseType();
			BeanUtil.map(expenseTypeCollection, response);
		} catch (Exception e) {
			logger.error("Error occurred while adding or editing Expense Type", e);
			throw new BusinessException(ServiceError.Unknown, "Error occurred while adding or editing Expense Type");
		}
		return response;
	}

	@Override
	public ExpenseType getExpenseType(String expenseTypeId) {
		ExpenseType response = null;
		try {

			ExpenseTypeCollection expenseTypeCollection = expenseTypeRepository.findOne(new ObjectId(expenseTypeId));
			response = new ExpenseType();
			BeanUtil.map(expenseTypeCollection, response);

		} catch (Exception e) {
			logger.error("Error occurred while getting Expense Type", e);
			throw new BusinessException(ServiceError.Unknown, "Error occurred while getting Expense Type");
		}
		return response;
	}

	@Override
	public List<ExpenseType> getExpenseType(int page, int size, String doctorId, String locationId, String hospitalId,
			String searchTerm, Boolean discarded) {
		List<ExpenseType> response = null;
		try {
			Criteria criteria = new Criteria();

			new Criteria("doctorId").is(new ObjectId(doctorId)).and("locationId").is(new ObjectId(locationId))
					.and("hospitalId").is(new ObjectId(hospitalId));

			if (!DPDoctorUtils.anyStringEmpty(searchTerm)) {
				criteria.orOperator(new Criteria("name").regex("^" + searchTerm, "i"),
						new Criteria("name").regex("^" + searchTerm));
			}
			if (!discarded)
				criteria.and("discarded").is(discarded);
			Aggregation aggregation = null;
			if (size > 0) {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")), Aggregation.skip((page) * size),
						Aggregation.limit(size));
			} else {
				aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
						Aggregation.sort(new Sort(Sort.Direction.DESC, "updatedTime")));
			}

			AggregationResults<ExpenseType> results = mongoTemplate.aggregate(aggregation, ExpenseTypeCollection.class,
					ExpenseType.class);
			response = results.getMappedResults();
		} catch (Exception e) {
			logger.error("Error occurred while getting Expense Type", e);
			throw new BusinessException(ServiceError.Unknown, "Error occurred while getting Expense Type");
		}
		return response;
	}

	@Override
	public Boolean deleteExpenseType(String expenseTypeId, Boolean discarded) {
		Boolean response = false;
		try {
			ExpenseTypeCollection expenseTypeCollection = expenseTypeRepository.findOne(new ObjectId(expenseTypeId));
			if (expenseTypeCollection == null) {
				throw new BusinessException(ServiceError.InvalidInput, "Expense Type Not Found with Id");
			}
			expenseTypeCollection.setUpdatedTime(new Date());
			expenseTypeCollection.setDiscarded(discarded);
			expenseTypeCollection = expenseTypeRepository.save(expenseTypeCollection);
			ESExpenseTypeDocument esDocument = new ESExpenseTypeDocument();
			esExpenseTypeService.addEditExpenseType(esDocument);
			response = true;
		} catch (Exception e) {
			logger.error("Error occurred while getting Expense Type ", e);
			throw new BusinessException(ServiceError.Unknown, "Error occurred while getting Expense Type");
		}
		return response;
	}
}
