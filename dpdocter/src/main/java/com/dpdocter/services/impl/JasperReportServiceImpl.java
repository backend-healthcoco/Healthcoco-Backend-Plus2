package com.dpdocter.services.impl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.dpdocter.services.JasperReportService;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRProperties;

@Service
public class JasperReportServiceImpl implements JasperReportService {

	@Override
	public void createPDF() {
		
		try {
    		String reportName = "jasperTemplate/Template";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("title","Prescription");

			JRProperties.setProperty("net.sf.jasperreports.awt.ignore.missing.font", "true");
			JRProperties.setProperty("net.sf.jasperreports.default.font.name", "Arial");
			
			JasperCompileManager.compileReportToFile(reportName + ".jrxml");
			JasperPrint print = JasperFillManager.fillReport(reportName + ".jasper", parameters, new JREmptyDataSource());
			
			JRExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, new FileOutputStream(reportName + ".pdf"));
			
			exporter.exportReport();
		} catch (JRException | FileNotFoundException e) {
			e.printStackTrace();
		}

		
	}

}
