package com.dpdocter.services;

import java.io.IOException;
import java.util.Map;

import com.dpdocter.enums.ComponentType;
import com.dpdocter.response.JasperReportResponse;

public interface JasperReportService {

    JasperReportResponse createPDF(ComponentType componentType, Map<String, Object> parameters, String fileName, String layout, String pageSize, Integer topMargin, Integer bottonMargin, Integer leftMargin, Integer rightMargin, Integer contentFontSize, String pdfName, String... subReportFileName) throws IOException;
}
