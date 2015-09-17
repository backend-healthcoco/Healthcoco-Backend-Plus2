package com.dpdocter.services.impl;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.JasperReportService;
import com.jaspersoft.mongodb.connection.MongoDbConnection;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

//import com.jaspersoft.mongodb.connection.MongoDbConnection;

@Service
public class JasperReportServiceImpl implements JasperReportService {

	private static Logger logger=Logger.getLogger(JasperReportServiceImpl.class.getName());
	
	private static final String MONGO_HOST_URI = "mongodb://localhost:27017/dpdocter_db";

	@Value(value = "${JASPER_TEMPLATES_RESOURCE}")
    private String REPORT_NAME;

    @Override
    public String createPDF(Map<String, Object> parameters, String fileName) {
	try {
		long createdTime = new Date().getTime();
	    MongoDbConnection mongoConnection = new MongoDbConnection(MONGO_HOST_URI, null, null);

//	    JasperCompileManager.compileReportToFile(REPORT_NAME+fileName + ".jrxml", REPORT_NAME+fileName + ".jasper");

	    parameters.put("REPORT_CONNECTION", mongoConnection);
	    JasperFillManager.fillReportToFile(REPORT_NAME+fileName + ".jasper", REPORT_NAME+fileName+createdTime+".jrprint", parameters);
	    JasperExportManager.exportReportToPdfFile(REPORT_NAME+fileName+createdTime+".jrprint",REPORT_NAME+fileName+createdTime + ".pdf");
	   
	    return REPORT_NAME+fileName+createdTime+".pdf";
	} catch (JRException e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
	
    }
}
