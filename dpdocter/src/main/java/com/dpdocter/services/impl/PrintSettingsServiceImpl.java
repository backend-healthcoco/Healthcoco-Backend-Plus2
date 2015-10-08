package com.dpdocter.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.dpdocter.beans.PrintSettings;
import com.dpdocter.beans.PrintSettingsDefaultData;
import com.dpdocter.collections.PrintSettingsCollection;
import com.dpdocter.collections.PrintSettingsDefaultDataCollection;
import com.dpdocter.enums.PrintFilter;
import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.reflections.BeanUtil;
import com.dpdocter.repository.PrintSettingsDefaultDataRepository;
import com.dpdocter.repository.PrintSettingsRepository;
import com.dpdocter.services.PrintSettingsService;
import common.util.web.DPDoctorUtils;

@Service
public class PrintSettingsServiceImpl implements PrintSettingsService {

    private static Logger logger = Logger.getLogger(PrintSettingsServiceImpl.class.getName());

    @Autowired
    private PrintSettingsRepository printSettingsRepository;

    @Autowired
    private PrintSettingsDefaultDataRepository printSettingsDefaultDataRepository;

    @Override
    public PrintSettingsDefaultData saveDefaultSettings(PrintSettingsDefaultData request) {
	PrintSettingsDefaultData response = null;
	PrintSettingsDefaultDataCollection printSettingsDefaultDataCollection = new PrintSettingsDefaultDataCollection();
	try {
	    BeanUtil.map(request, printSettingsDefaultDataCollection);
	    printSettingsDefaultDataCollection = printSettingsDefaultDataRepository.save(printSettingsDefaultDataCollection);
	    response = new PrintSettingsDefaultData();
	    BeanUtil.map(printSettingsDefaultDataCollection, response);

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error occured while saving default settings");
	    throw new BusinessException(ServiceError.Unknown, "Error occured while saving default settings");
	}
	return response;
    }

    @Override
    public List<PrintSettingsDefaultData> getDefaultSettings() {
	List<PrintSettingsDefaultData> response = new ArrayList<PrintSettingsDefaultData>();
	List<PrintSettingsDefaultDataCollection> printSettingsDefaultDataCollection = null;
	try {
	    printSettingsDefaultDataCollection = printSettingsDefaultDataRepository.findAll();
	    BeanUtil.map(printSettingsDefaultDataCollection, response);
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error occured while Getting default settings");
	    throw new BusinessException(ServiceError.Unknown, "Error occured while Getting default settings");
	}
	return response;
    }

    @Override
    public PrintSettings saveSettings(PrintSettings request) {
	PrintSettings response = new PrintSettings();
	PrintSettingsCollection printSettingsCollection = new PrintSettingsCollection();
	try {
	    if (request.getId() == null) {
		PrintSettingsCollection collection = printSettingsRepository.findOne(request.getDoctorId(), request.getLocationId(), request.getHospitalId());
		if (collection != null && !collection.getDiscarded() && request.getComponentType().equals(collection.getComponentType()))
		    request.setId(collection.getId());
	    }
	    BeanUtil.map(request, printSettingsCollection);
	    if (request.getId() == null) {
		printSettingsCollection.setCreatedTime(new Date());
	    } else {
		PrintSettingsCollection oldPrintSettingsCollection = printSettingsRepository.findOne(request.getId());
		if (oldPrintSettingsCollection != null) {
		    printSettingsCollection.setCreatedTime(oldPrintSettingsCollection.getCreatedTime());
		    printSettingsCollection.setCreatedBy(oldPrintSettingsCollection.getCreatedBy());
		    printSettingsCollection.setDiscarded(oldPrintSettingsCollection.getDiscarded());

		    if (request.getPageSetup() == null)
			printSettingsCollection.setPageSetup(oldPrintSettingsCollection.getPageSetup());

		    if (request.getHeaderSetup() == null)
			printSettingsCollection.setHeaderSetup(oldPrintSettingsCollection.getHeaderSetup());

		    if (request.getFooterSetup() == null)
			printSettingsCollection.setFooterSetup(oldPrintSettingsCollection.getFooterSetup());
		}

	    }
	    printSettingsCollection = printSettingsRepository.save(printSettingsCollection);
	    BeanUtil.map(printSettingsCollection, response);

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error occured while saving settings");
	    throw new BusinessException(ServiceError.Unknown, "Error occured while saving settings");
	}
	return response;
    }

    @Override
    public List<PrintSettings> getSettings(String printFilter, String doctorId, String locationId, String hospitalId, int page, int size, String updatedTime,
	    Boolean discarded) {
	List<PrintSettings> response = null;
	List<PrintSettingsCollection> printSettingsCollections = null;
	try {
	    if (doctorId == null) {
		if (size > 0)
		    printSettingsCollections = printSettingsRepository.findAll(new PageRequest(page, size, Direction.DESC, "updatedTime")).getContent();
		else
		    printSettingsCollections = printSettingsRepository.findAll(new Sort(Sort.Direction.DESC, "updatedTime"));

	    } else if (!DPDoctorUtils.allStringsEmpty(updatedTime)) {
		long createdTimeStamp = Long.parseLong(updatedTime);
		if (locationId == null && hospitalId == null) {
		    if (discarded) {
			if (size > 0)
			    printSettingsCollections = printSettingsRepository.findAll(doctorId, new Date(createdTimeStamp), new PageRequest(page, size,
				    Direction.DESC, "updatedTime"));
			else
			    printSettingsCollections = printSettingsRepository.findAll(doctorId, new Date(createdTimeStamp), new Sort(Sort.Direction.DESC,
				    "updatedTime"));
		    } else {
			if (size > 0)
			    printSettingsCollections = printSettingsRepository.findAll(doctorId, new Date(createdTimeStamp), discarded, new PageRequest(page,
				    size, Direction.DESC, "updatedTime"));
			else
			    printSettingsCollections = printSettingsRepository.findAll(doctorId, new Date(createdTimeStamp), discarded, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    }

		} else {
		    if (discarded) {
			if (size > 0)
			    printSettingsCollections = printSettingsRepository.findAll(doctorId, locationId, hospitalId, new Date(createdTimeStamp),
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    printSettingsCollections = printSettingsRepository.findAll(doctorId, locationId, hospitalId, new Date(createdTimeStamp), new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    printSettingsCollections = printSettingsRepository.findAll(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discarded,
				    new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    printSettingsCollections = printSettingsRepository.findAll(doctorId, locationId, hospitalId, new Date(createdTimeStamp), discarded,
				    new Sort(Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    } else {
		if (locationId == null && hospitalId == null) {
		    if (discarded) {
			if (size > 0)
			    printSettingsCollections = printSettingsRepository.findAll(doctorId, new PageRequest(page, size, Direction.DESC, "updatedTime"));
			else
			    printSettingsCollections = printSettingsRepository.findAll(doctorId, new Sort(Sort.Direction.DESC, "updatedTime"));
		    } else {
			if (size > 0)
			    printSettingsCollections = printSettingsRepository.findAll(doctorId, discarded, new PageRequest(page, size, Direction.DESC,
				    "updatedTime"));

			else
			    printSettingsCollections = printSettingsRepository.findAll(doctorId, discarded, new Sort(Sort.Direction.DESC, "updatedTime"));

		    }
		} else {
		    if (discarded) {
			if (size > 0)
			    printSettingsCollections = printSettingsRepository.findAll(doctorId, locationId, hospitalId, new PageRequest(page, size,
				    Direction.DESC, "updatedTime"));
			else
			    printSettingsCollections = printSettingsRepository.findAll(doctorId, locationId, hospitalId, new Sort(Sort.Direction.DESC,
				    "updatedTime"));
		    } else {
			if (size > 0)
			    printSettingsCollections = printSettingsRepository.findAll(doctorId, locationId, hospitalId, discarded, new PageRequest(page, size,
				    Direction.DESC, "updatedTime"));
			else
			    printSettingsCollections = printSettingsRepository.findAll(doctorId, locationId, hospitalId, discarded, new Sort(
				    Sort.Direction.DESC, "updatedTime"));
		    }
		}
	    }
	    if (printSettingsCollections != null) {
		response = new ArrayList<PrintSettings>();

		if (PrintFilter.PAGESETUP.getFilter().equalsIgnoreCase(printFilter)) {
		    for (PrintSettingsCollection collection : printSettingsCollections) {
			collection.setFooterSetup(null);
			collection.setHeaderSetup(null);
		    }
		} else if (PrintFilter.HEADERSETUP.getFilter().equalsIgnoreCase(printFilter)) {
		    for (PrintSettingsCollection collection : printSettingsCollections) {
			collection.setFooterSetup(null);
			collection.setPageSetup(null);
		    }
		} else if (PrintFilter.FOOTERSETUP.getFilter().equalsIgnoreCase(printFilter)) {
		    for (PrintSettingsCollection collection : printSettingsCollections) {
			collection.setPageSetup(null);
			collection.setHeaderSetup(null);
		    }
		} else
		    ;

		BeanUtil.map(printSettingsCollections, response);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e + " Error Occurred While Getting Print Settings");
	    throw new BusinessException(ServiceError.Unknown, "Error Occurred While Getting Print Settings");
	}
	return response;

    }

    @Override
    public Boolean deletePrintSettings(String id, String doctorId, String locationId, String hospitalId) {
	try {
	    PrintSettingsCollection printSettingsCollection = printSettingsRepository.findOne(id);
	    if (printSettingsCollection != null) {
		if (printSettingsCollection.getDoctorId() != null && printSettingsCollection.getHospitalId() != null
			&& printSettingsCollection.getLocationId() != null) {
		    if (printSettingsCollection.getDoctorId().equals(doctorId) && printSettingsCollection.getHospitalId().equals(hospitalId)
			    && printSettingsCollection.getLocationId().equals(locationId)) {
			printSettingsCollection.setDiscarded(true);
			printSettingsCollection.setUpdatedTime(new Date());
			printSettingsRepository.save(printSettingsCollection);
			return true;
		    } else {
			logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
			throw new BusinessException(ServiceError.Unknown, "Invalid Doctor Id, Hospital Id, Or Location Id");
		    }
		} else {
		    logger.warn("Invalid Doctor Id, Hospital Id, Or Location Id");
		    throw new BusinessException(ServiceError.Unknown, "Invalid Doctor Id, Hospital Id, Or Location Id");
		}

	    } else {
		logger.warn("Print Settings not found!");
		throw new BusinessException(ServiceError.Unknown, "Print Settings not found!");
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}

    }

}
