package com.dpdocter.exceptions;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.stereotype.Component;

import common.util.web.JacksonUtil;

@Component
@Provider
@Produces({ "text/html;charset=utf-8", "text/xml;charset=utf-8", "application/json;charset=utf-8" })
public class ServiceExceptionMapper implements ExceptionMapper<BusinessException> {

    @Context
    private ThreadLocal<HttpServletRequest> requestInvoker;

    @Context
    private ThreadLocal<HttpServletResponse> responseInvoker;

    @Context
    private ServletContext servletContext;

    private Marshaller marshaller = null;

    public void dump(BusinessException exception) {
	try {
	    System.out.println("BusinessException " + exception.getErrorCode() + " " + new Date() + ". \007");
	    Enumeration<?> names = requestInvoker.get().getHeaderNames();
	    while (names.hasMoreElements()) {
		String name = names.nextElement().toString();
		String value = requestInvoker.get().getHeader(name);
		System.out.println("Header " + name + ": " + value);
	    }
	    exception.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    @Override
    @SuppressWarnings("unused")
    public Response toResponse(BusinessException exception) {
	try {
	    dump(exception);
	    BusinessExceptionResponse data = new BusinessExceptionResponse(exception);

	    String accept = requestInvoker.get().getHeader("Accept");
	    // String format = requestInvoker.get().getParameter("format");
	    String format = "json";
	    boolean json = true;
	    boolean xml = false;
	    boolean html = false;

	    if (format == null) {
		if (accept == null || accept.toLowerCase().indexOf("html") != -1) {
		    json = false;
		    xml = false;
		    html = true;
		} else if (accept.toLowerCase().indexOf("json") != -1) {
		    json = true;
		    xml = false;
		    html = false;
		} else {
		    json = false;
		    xml = true;
		    html = false;
		}
	    } else if (format.equals("json")) {
		json = true;
		xml = false;
		html = false;
	    } else if (format.equals("xml")) {
		json = false;
		xml = true;
		html = false;
	    }

	    if (html) {
		Object model = data;
		String modelClass = model.getClass().getName();

		HttpServletRequest request = requestInvoker.get();
		HttpServletResponse response = responseInvoker.get();
		Object oldIt = requestInvoker.get().getAttribute("it");
		Object oldModel = request.getAttribute(modelClass);
		String contextPath = servletContext.getContextPath();

		String defaultView = "view";
		try {
		    request.setAttribute(modelClass, model);
		    request.setAttribute("it", model);

		    String uri = (String) request.getAttribute("javax.servlet.include.request_uri");
		    if (uri == null) {
			uri = request.getRequestURI();
		    }
		    if (request.getMethod().equalsIgnoreCase("post")) {
			uri = uri + "/post";
		    }
		    String oopath = uri + "/" + BusinessException.class.getSimpleName() + "/" + exception.getErrorCode().name() + "/index.jsp.oo";
		    oopath = oopath.substring(0, oopath.indexOf(contextPath) + contextPath.length()) + "/" + defaultView
			    + oopath.substring(oopath.indexOf(contextPath) + contextPath.length());
		    servletContext.getRequestDispatcher(oopath).include(request, response);
		} finally {
		    request.setAttribute("it", oldIt);
		    request.setAttribute(modelClass, oldModel);
		}
		return Response.ok().build();
	    } else if (json) {
		String jsonStr = JacksonUtil.obj2Json(data);
		// responseInvoker.get().getWriter().write(jsonStr);
		return Response.status(exception.getErrorCode().getErrorCode()).type(MediaType.APPLICATION_JSON_TYPE).entity(jsonStr).build();
	    } else {
		String xmlStr = getXml(data);
		responseInvoker.get().getWriter().write(xmlStr);
		return Response.status(exception.getErrorCode().getErrorCode()).type(MediaType.TEXT_XML_TYPE).build();
	    }
	} catch (Exception e) {
	    return Response.serverError().build();
	}
    }

    private String getXml(BusinessExceptionResponse data) throws JAXBException, IOException {
	JAXBContext jaxbContext;
	if (marshaller == null) {
	    jaxbContext = JAXBContext.newInstance(BusinessExceptionResponse.class);
	    marshaller = jaxbContext.createMarshaller();
	}
	StringWriter sw = new StringWriter();
	marshaller.marshal(data, sw);
	return sw.toString();
    }
}
