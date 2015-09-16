package com.dpdocter.services;

import java.util.Map;

public interface JasperReportService {

	String createPDF(Map<String, Object> parameters, String fileName);
}
