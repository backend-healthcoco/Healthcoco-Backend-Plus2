package com.dpdocter.aop;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.services.MailBodyGenerator;
import com.dpdocter.services.MailService;
import com.mongodb.DuplicateKeyException;

import common.util.web.Response;

@RestController
@Aspect
public class DpDocterLoggingAspect {
	
	@Autowired
	MailService mailService;
	
	@Autowired
	MailBodyGenerator mailBodyGenerator;
	

	private Logger LOGGER = LogManager.getLogger(DpDocterLoggingAspect.class);

	
	@Around(value = "com.dpdocter.aop.PointcutDefinition.serviceLayer() ")
	public Object beforeUpdateAccountDescription(ProceedingJoinPoint pjp ) throws Throwable {
		
		LOGGER.warn("Logging access to " + pjp.getTarget().getClass().getSimpleName() + ". "
					+ " Executing :: " + pjp.getSignature().getName() + "()" );
		long start = System.currentTimeMillis();
	    LOGGER.warn("Going to call the method " + pjp.getSignature().getName() + "() in " + pjp.getTarget().getClass().getSimpleName() + " at " + new Date());
	    Object response =  pjp.proceed();
	    LOGGER.warn("Method execution completed for " + pjp.getSignature().getName() + "() in " + pjp.getTarget().getClass().getSimpleName() + " at " + new Date());
	    long elapsedTime = System.currentTimeMillis() - start;
	    LOGGER.warn("Method " + pjp.getSignature().getName() + "() in " + pjp.getTarget().getClass().getSimpleName() + " took "+ elapsedTime + " milliseconds for execution");
	         	
		return response; 
         
	}

	@AfterThrowing(value = "com.dpdocter.aop.PointcutDefinition.apiLayer()", throwing = "e")
	public Response<Exception> logAfterThrowingAllMethods(JoinPoint pjp ,Exception e) throws Throwable {
		if(!(pjp.getSignature().getName().equalsIgnoreCase("login") || pjp.getSignature().getName().equalsIgnoreCase("loginPatient") || pjp.getSignature().getName().equalsIgnoreCase("getBlogBySlugURL"))){
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		TimeZone timeZone=TimeZone.getTimeZone("IST");
		sdf.setTimeZone(timeZone);
		String expceptionMsg = ExceptionUtils.getStackTrace(e);
		Object oapObject = pjp.getThis();
		mailService.sendExceptionMail("Exception Thrown in "+  pjp.getSignature().getName() + "() in " + pjp.getTarget().getClass().getSimpleName() + " at " + sdf.format(new Date()),mailBodyGenerator.generateExceptionEmailBody(oapObject.toString() + expceptionMsg));
		
		LOGGER.warn("LoggingAspect.logAfterThrowingAllMethods()");
		LOGGER.warn("Exception catched : " + e.getClass().getCanonicalName());
		Response<Exception> response = new Response<Exception>();
		response.setData(e);
		return response;
		}else {
			 return null;
		}
	}

	@AfterThrowing(value = "com.dpdocter.aop.PointcutDefinition.apiLayer()", throwing = "e")
	public Response<Exception> logAfterThrowingAllMethods(JoinPoint pjp ,DuplicateKeyException e) throws Throwable {
		if(!(pjp.getSignature().getName().equalsIgnoreCase("login") || pjp.getSignature().getName().equalsIgnoreCase("loginPatient") || pjp.getSignature().getName().equalsIgnoreCase("getBlogBySlugURL"))){
		
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			TimeZone timeZone=TimeZone.getTimeZone("IST");
			sdf.setTimeZone(timeZone);
			String expceptionMsg = ExceptionUtils.getStackTrace(e);
			Object oapObject = pjp.getThis();
			mailService.sendExceptionMail("Exception Thrown in "+  pjp.getSignature().getName() + "() in " + pjp.getTarget().getClass().getSimpleName() + " at " + sdf.format(new Date()),mailBodyGenerator.generateExceptionEmailBody(oapObject.toString() + expceptionMsg));
			LOGGER.warn("LoggingAspect.logAfterThrowingAllMethods()");
			LOGGER.warn("Exception catched : " + e.getClass().getCanonicalName());
			Response<Exception> response = new Response<Exception>();
			response.setData(e);
			return response;
		}else return null;
	}

	
	@AfterThrowing(value = "com.dpdocter.aop.PointcutDefinition.apiLayer()", throwing = "e")
	public Response<Exception> logAfterThrowingAllMethods(JoinPoint pjp ,BusinessException e) throws Throwable {
		if(!(pjp.getSignature().getName().equalsIgnoreCase("login") || pjp.getSignature().getName().equalsIgnoreCase("loginPatient") || pjp.getSignature().getName().equalsIgnoreCase("getBlogBySlugURL") )){
		
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			TimeZone timeZone=TimeZone.getTimeZone("IST");
			sdf.setTimeZone(timeZone);
			String expceptionMsg = ExceptionUtils.getStackTrace(e);
			Object oapObject = pjp.getThis();
			mailService.sendExceptionMail("Exception Thrown in "+  pjp.getSignature().getName() + "() in " + pjp.getTarget().getClass().getSimpleName() + " at " + sdf.format(new Date()),mailBodyGenerator.generateExceptionEmailBody(oapObject.toString() + expceptionMsg));
			LOGGER.warn("LoggingAspect.logAfterThrowingAllMethods()");
			LOGGER.warn("Exception catched : " + e.getClass().getCanonicalName());
			Response<Exception> response = new Response<Exception>();
			response.setData(e);
			return response;
		}else return null;
	}

}
