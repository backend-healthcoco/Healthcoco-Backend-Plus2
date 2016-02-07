package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.PatientTreatment;
import com.dpdocter.beans.ProductAndService;
import com.dpdocter.collections.DoctorCollection;
import com.dpdocter.collections.PatientTreatmentCollection;
import com.dpdocter.collections.ProductsAndServicesCollection;
import com.dpdocter.collections.ProductsAndServicesCostCollection;
import com.dpdocter.enums.PatientTreatmentStatus;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.DoctorRepository;
import com.dpdocter.repository.PatientTreamentRepository;
import com.dpdocter.repository.ProductsAndServicesCostRepository;
import com.dpdocter.repository.ProductsAndServicesRepository;
import com.dpdocter.response.PatientTreatmentResponse;
import com.dpdocter.services.OTPService;
import com.dpdocter.services.PatientTreatmentServices;
import common.util.web.DPDoctorUtils;

@Service
public class PatientTreatmentServicesImpl implements PatientTreatmentServices {
    private static Logger logger = Logger.getLogger(PatientTreatmentServicesImpl.class);

    @Autowired
    private ProductsAndServicesRepository productsAndServicesRepository;

    @Autowired
    private ProductsAndServicesCostRepository productsAndServicesCostRepository;

    @Autowired
    private PatientTreamentRepository patientTreamentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private OTPService otpService;

    @Override
    public boolean addEditProductService(ProductAndService productAndService) {
	boolean response = false;
	ProductsAndServicesCollection productsAndServicesCollection;
	ProductsAndServicesCostCollection productsAndServicesCostCollection;
	try {
	    if (DPDoctorUtils.anyStringEmpty(productAndService.getId())) {
		productsAndServicesCollection = new ProductsAndServicesCollection();
		BeanUtil.map(productAndService, productsAndServicesCollection);
		productsAndServicesCollection.setCreatedTime(new Date());
		productsAndServicesCollection.setUpdatedTime(new Date());
		productsAndServicesCollection = productsAndServicesRepository.save(productsAndServicesCollection);

		if (productAndService.getCost() != 0.0) {
		    productsAndServicesCostCollection = new ProductsAndServicesCostCollection();
		    BeanUtil.map(productAndService, productsAndServicesCostCollection);
		    productsAndServicesCostCollection.setProductAndServiceId(productsAndServicesCollection.getId());
		    productsAndServicesCostCollection.setCreatedTime(new Date());
		    productsAndServicesCostCollection.setUpdatedTime(new Date());
		    productsAndServicesCostCollection = productsAndServicesCostRepository.save(productsAndServicesCostCollection);
		}
	    } else {
		productsAndServicesCollection = productsAndServicesRepository.findOne(productAndService.getId());
		if (productsAndServicesCollection != null) {
		    List<String> specialityIds = productAndService.getSpecialityIds();
		    if (productAndService.getSpecialityIds() != null && !productAndService.getSpecialityIds().isEmpty()) {
			specialityIds.addAll(productAndService.getSpecialityIds());
			Set<String> tempSpecialityIds = new HashSet<String>(specialityIds);
			specialityIds.clear();
			specialityIds.addAll(tempSpecialityIds);
		    }
		    if (!DPDoctorUtils.anyStringEmpty(productAndService.getName())) {
			productsAndServicesCollection.setName(productAndService.getName());
		    }
		    if (!DPDoctorUtils.anyStringEmpty(productAndService.getLocationId())) {
			productsAndServicesCollection.setLocationId(productAndService.getLocationId());
		    }
		    if (!DPDoctorUtils.anyStringEmpty(productAndService.getHospitalId())) {
			productsAndServicesCollection.setHospitalId(productAndService.getHospitalId());
		    }
		    if (!DPDoctorUtils.anyStringEmpty(productAndService.getDoctorId())) {
			productsAndServicesCollection.setDoctorId(productAndService.getDoctorId());
		    }

		    productsAndServicesCostCollection = productsAndServicesCostRepository.findOne(productAndService.getId());
		    if (productsAndServicesCostCollection != null) {
			String productsAndServicesCostCollectionId = productsAndServicesCostCollection.getId();
			if (productAndService.getCost() != 0.0) {
			    productsAndServicesCostCollection.setCost(productAndService.getCost());
			}
			BeanUtil.map(productsAndServicesCollection, productsAndServicesCostCollection);
			productsAndServicesCostCollection.setId(productsAndServicesCostCollectionId);
		    }

		    productsAndServicesCollection.setUpdatedTime(new Date());
		    productsAndServicesCollection = productsAndServicesRepository.save(productsAndServicesCollection);

		    productsAndServicesCostCollection.setUpdatedTime(new Date());
		    productsAndServicesCostCollection = productsAndServicesCostRepository.save(productsAndServicesCostCollection);
		} else {
		    throw new BusinessException(ServiceError.NotFound, "No product or service found for the given Id");
		}
	    }
	    response = true;
	} catch (Exception e) {
	    logger.error("Error occurred while adding or editing products and services", e);
	    throw new BusinessException(ServiceError.Unknown, "Error occurred while adding or editing products and services");
	}
	return response;
    }

    @Override
    public boolean addEditProductServiceCost(ProductAndService productAndService) {
	boolean response = false;
	ProductsAndServicesCostCollection productAndServiceCostCollection;
	try {
	    productAndServiceCostCollection = productsAndServicesCostRepository.findOne(productAndService.getId(), productAndService.getLocationId(),
		    productAndService.getHospitalId(), productAndService.getDoctorId());
	    if (productAndServiceCostCollection != null) {
		productAndServiceCostCollection.setCost(productAndService.getCost());
	    } else {
		productAndServiceCostCollection = new ProductsAndServicesCostCollection();
		BeanUtil.map(productAndService, productAndServiceCostCollection);
		productAndServiceCostCollection.setId(null);
		productAndServiceCostCollection.setProductAndServiceId(productAndService.getId());
	    }
	    productAndServiceCostCollection.setUpdatedTime(new Date());
	    productAndServiceCostCollection = productsAndServicesCostRepository.save(productAndServiceCostCollection);
	    response = true;
	} catch (Exception e) {
	    logger.error("Error occurred while adding or editing cost for products and services", e);
	    throw new BusinessException(ServiceError.Unknown, "Error occurred while adding or editing cost for products and services");
	}
	return response;
    }

    @Override
    public List<ProductAndService> getProductsAndServices(String locationId, String hospitalId, String doctorId) {
	List<ProductAndService> response = null;
	try {
	    DoctorCollection doctor = doctorRepository.findByUserId(doctorId);
	    List<String> specialityIds = doctor.getSpecialities();
	    List<ProductsAndServicesCollection> productsAndServicesCollections = productsAndServicesRepository.findAll(specialityIds);
	    if (productsAndServicesCollections != null && !productsAndServicesCollections.isEmpty()) {
		response = new ArrayList<ProductAndService>();
		for (ProductsAndServicesCollection productAndServiceCollection : productsAndServicesCollections) {
		    ProductAndService productAndService = new ProductAndService();
		    BeanUtil.map(productAndServiceCollection, productAndService);
		    ProductsAndServicesCostCollection productAndServiceCost = productsAndServicesCostRepository.findOne(productAndServiceCollection.getId(),
			    locationId, hospitalId, doctorId);
		    if (productAndServiceCost != null) {
			productAndService.setCost(productAndServiceCost.getCost());
		    }
		    response.add(productAndService);
		}
	    } else {
		throw new BusinessException(ServiceError.NotFound, "No products and services found");
	    }
	} catch (Exception e) {
	    logger.error("Error occurred getting products and services", e);
	    throw new BusinessException(ServiceError.Unknown, "Error occurred getting products and services");
	}
	return response;
    }

    @Override
    public PatientTreatmentResponse addEditPatientTreatment(String treatmentId, String locationId, String hospitalId, String doctorId,
	    List<PatientTreatment> patientTreatments) {
	PatientTreatmentResponse response;
	PatientTreatmentCollection patientTreatmentCollection;
	double totalCost = 0.0;
	try {
	    if (DPDoctorUtils.anyStringEmpty(treatmentId)) {
		patientTreatmentCollection = new PatientTreatmentCollection();
		patientTreatmentCollection.setCreatedTime(new Date());
	    } else {
		patientTreatmentCollection = patientTreamentRepository.findOne(treatmentId, locationId, hospitalId, doctorId);
		if (patientTreatmentCollection == null) {
		    throw new BusinessException(ServiceError.NotFound, "No treatment found for the given ids");
		}
	    }

	    patientTreatmentCollection.setLocationId(locationId);
	    patientTreatmentCollection.setHospitalId(hospitalId);
	    patientTreatmentCollection.setDoctorId(doctorId);

	    for (PatientTreatment patientTreatment : patientTreatments) {
		if (patientTreatment.getStatus() == null) {
		    patientTreatment.setStatus(PatientTreatmentStatus.NOT_STARTED);
		}
		ProductsAndServicesCostCollection productsAndServicesCost = productsAndServicesCostRepository.findOne(
			patientTreatment.getProductAndServiceId(), locationId, hospitalId, doctorId);
		if (productsAndServicesCost != null) {
		    patientTreatment.setCost(productsAndServicesCost.getCost());
		    totalCost += productsAndServicesCost.getCost();
		}
	    }
	    patientTreatmentCollection.setPatientTreatments(patientTreatments);
	    patientTreatmentCollection.setTotalCost(totalCost);
	    patientTreatmentCollection.setUpdatedTime(new Date());

	    patientTreatmentCollection = patientTreamentRepository.save(patientTreatmentCollection);

	    response = new PatientTreatmentResponse();

	    BeanUtil.map(patientTreatmentCollection, response);
	} catch (Exception e) {
	    logger.error("Error occurred while adding or editing treatment for patients", e);
	    throw new BusinessException(ServiceError.Unknown, "Error occurred while adding or editing treatment for patients");
	}
	return response;
    }

    @Override
    public boolean deletePatientTreatment(String treatmentId, String locationId, String hospitalId, String doctorId) {
	boolean response = false;
	try {
	    PatientTreatmentCollection patientTreatmentCollection = patientTreamentRepository.findOne(treatmentId, locationId, hospitalId, doctorId);

	    if (patientTreatmentCollection != null) {
		patientTreatmentCollection.setDiscarded(true);
		patientTreatmentCollection.setUpdatedTime(new Date());
		patientTreamentRepository.save(patientTreatmentCollection);
	    } else {
		throw new BusinessException(ServiceError.NotFound, "No treatment found for the given ids");
	    }
	} catch (Exception e) {
	    logger.error("Error while deleting treatment", e);
	    throw new BusinessException(ServiceError.Unknown, "Error while deleting treatment");
	}
	return response;
    }

    @Override
    public PatientTreatmentResponse getPatientTreatmentById(String treatmentId) {
	PatientTreatmentResponse response;
	try {
	    PatientTreatmentCollection patientTreatmentCollection = patientTreamentRepository.findOne(treatmentId);
	    if (patientTreatmentCollection != null) {
		response = new PatientTreatmentResponse();

		BeanUtil.map(patientTreatmentCollection, response);
	    } else {
		throw new BusinessException(ServiceError.NotFound, "No treatment found for the given id");
	    }
	} catch (Exception e) {
	    logger.error("Error while getting patient treatments", e);
	    throw new BusinessException(ServiceError.Unknown, "Error while getting patient treatments");
	}
	return response;
    }

    @Override
    public List<PatientTreatmentResponse> getPatientTreatments(String locationId, String hospitalId, String doctorId, String patientId, int page, int size,
	    String updatedTime, Boolean discarded) {
	List<PatientTreatmentResponse> response;
	List<PatientTreatmentCollection> patientTreatmentCollections;
	boolean[] discards = { false };
	try {
	    if (discarded) {
		discards[1] = true;
	    }

	    boolean otpVerified = otpService.checkOTPVerified(doctorId, locationId, hospitalId, patientId);

	    if (size > 0) {
		if (otpVerified) {
		    patientTreatmentCollections = patientTreamentRepository.findAll(patientId, discards, new Date(Long.parseLong(updatedTime)),
			    new PageRequest(page, size, Direction.DESC, "updatedTime"));
		} else {
		    patientTreatmentCollections = patientTreamentRepository.findAll(patientId, locationId, hospitalId, doctorId, discards,
			    new Date(Long.parseLong(updatedTime)), new PageRequest(page, size, Direction.DESC, "updatedTime"));
		}
	    } else {
		if (otpVerified) {
		    patientTreatmentCollections = patientTreamentRepository.findAll(patientId, discards, new Date(Long.parseLong(updatedTime)), new Sort(
			    Sort.Direction.DESC, "updatedTime"));
		} else {
		    patientTreatmentCollections = patientTreamentRepository.findAll(patientId, locationId, hospitalId, doctorId, discards,
			    new Date(Long.parseLong(updatedTime)), new Sort(Sort.Direction.DESC, "updatedTime"));
		}
	    }

	    if (patientTreatmentCollections != null && !patientTreatmentCollections.isEmpty()) {
		response = new ArrayList<PatientTreatmentResponse>();

		BeanUtil.map(patientTreatmentCollections, response);
	    } else {
		throw new BusinessException(ServiceError.NotFound, "No treatment found");
	    }

	} catch (Exception e) {
	    logger.error("Error while getting patient treatments", e);
	    throw new BusinessException(ServiceError.Unknown, "Error while getting patient treatments");
	}
	return response;
    }

}
