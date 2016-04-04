package com.dpdocter.services.impl;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.exceptions.ServiceError;
import com.dpdocter.services.JasperReportService;
import com.jaspersoft.mongodb.connection.MongoDbConnection;

import ar.com.fdvs.dj.domain.constants.Page;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRProperties;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

@Service
public class JasperReportServiceImpl implements JasperReportService {

    private static Logger logger = Logger.getLogger(JasperReportServiceImpl.class.getName());

    private static final String MONGO_HOST_URI = "mongodb://localhost:27017/dpdocter_db";
//10.0.1.207:27017,10.0.1.8:27017,10.0.1.9:27017
    @Value(value = "${JASPER_TEMPLATES_RESOURCE}")
    private String REPORT_NAME;
       
    @SuppressWarnings("deprecation")
    @Override
    public String createPDF(Map<String, Object> parameters, String fileName, String layout, String pageSize, String margins, String pdfName) {
	try {
	    MongoDbConnection mongoConnection = new MongoDbConnection(MONGO_HOST_URI, null, null);

	    parameters.put("REPORT_CONNECTION", mongoConnection);
	    parameters.put("SUBREPORT_DIR", REPORT_NAME);

	    DefaultJasperReportsContext context = DefaultJasperReportsContext.getInstance();
	    context.setValue("net.sf.jasperreports.extension.registry.factory.queryexecuters.mongodb",
		    "com.jaspersoft.mongodb.query.MongoDbQueryExecuterExtensionsRegistryFactory");
	    // JRPropertiesUtil propertiesUtil =
	    // JRPropertiesUtil.getInstance(context);

	    JRProperties.setProperty("net.sf.jasperreports.query.executer.factory.MongoDbQuery", "com.jaspersoft.mongodb.query.MongoDbQueryExecuterFactory");
	    JasperDesign design = JRXmlLoader.load(new File(REPORT_NAME + fileName + ".jrxml"));

	    if (layout.equals("LANDSCAPE")) {
		if (pageSize.equalsIgnoreCase("LETTER")) {
		    design.setPageHeight(Page.Page_Letter_Landscape().getHeight());
		    design.setPageWidth(Page.Page_Letter_Landscape().getWidth());
		} else if (pageSize.equalsIgnoreCase("LEGAL")) {
		    design.setPageHeight(Page.Page_Legal_Landscape().getHeight());
		    design.setPageWidth(Page.Page_Legal_Landscape().getWidth());
		} else {
		    design.setPageHeight(Page.Page_A4_Landscape().getHeight());
		    design.setPageWidth(Page.Page_A4_Landscape().getWidth());
		}
	    } else {
		if (pageSize.equalsIgnoreCase("LETTER")) {
		    design.setPageHeight(Page.Page_Letter_Portrait().getHeight());
		    design.setPageWidth(Page.Page_Letter_Portrait().getWidth());
		} else if (pageSize.equalsIgnoreCase("LEGAL")) {
		    design.setPageHeight(Page.Page_Legal_Portrait().getHeight());
		    design.setPageWidth(Page.Page_Legal_Portrait().getWidth());
		} else {
		    design.setPageHeight(Page.Page_A4_Portrait().getHeight());
		    design.setPageWidth(Page.Page_A4_Portrait().getWidth());
		}
	    }
	    JasperReport jasperReport = JasperCompileManager.compileReport(design);

	    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters);

	    JasperExportManager.exportReportToPdfFile(jasperPrint, REPORT_NAME + pdfName + ".pdf");

	    return REPORT_NAME + pdfName + ".pdf";

	} catch (JRException e) {
	    e.printStackTrace();
	    logger.error(e);
	    throw new BusinessException(ServiceError.Unknown, e.getMessage());
	}
    }
}
