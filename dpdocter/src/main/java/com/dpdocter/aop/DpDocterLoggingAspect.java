package com.dpdocter.aop;

import java.util.Date;

import org.aopalliance.intercept.Joinpoint;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dpdocter.exceptions.BusinessException;
import com.dpdocter.services.MailService;
import com.mongodb.DuplicateKeyException;

import common.util.web.Response;

@Component
@Aspect
public class DpDocterLoggingAspect {
	
	@Autowired
	
	MailService mailService;
	

	private Logger LOGGER = Logger.getLogger(DpDocterLoggingAspect.class);

	
	@Around(value = "com.dpdocter.aop.PointcutDefinition.serviceLayer()) ")
	public Object beforeUpdateAccountDescription(ProceedingJoinPoint pjp ) throws Throwable {
		System.out.println("Logging access to " + pjp.getTarget().getClass().getSimpleName() + ". "
				+ " Executing :: " + pjp.getSignature().getName() + "()" );
		LOGGER.warn("Logging access to " + pjp.getTarget().getClass().getSimpleName() + ". "
				+ " Executing :: " + pjp.getSignature().getName() + "()" );
		 long start = System.currentTimeMillis();
         System.out.println("Going to call the method " + pjp.getSignature().getName() + "() in " + pjp.getTarget().getClass().getSimpleName() + " at " + new Date());
         LOGGER.warn("Going to call the method " + pjp.getSignature().getName() + "() in " + pjp.getTarget().getClass().getSimpleName() + " at " + new Date());
         Object response =  pjp.proceed();
         System.out.println("Method execution completed for " + pjp.getSignature().getName() + "() in " + pjp.getTarget().getClass().getSimpleName() + " at " + new Date());
         LOGGER.warn("Method execution completed for " + pjp.getSignature().getName() + "() in " + pjp.getTarget().getClass().getSimpleName() + " at " + new Date());
         long elapsedTime = System.currentTimeMillis() - start;
         System.out.println("Method " + pjp.getSignature().getName() + "() in " + pjp.getTarget().getClass().getSimpleName() + " took "+ elapsedTime + " milliseconds for execution");
         LOGGER.warn("Method " + pjp.getSignature().getName() + "() in " + pjp.getTarget().getClass().getSimpleName() + " took "+ elapsedTime + " milliseconds for execution");
         return response;
         
	}

	@AfterThrowing(value = "com.dpdocter.aop.PointcutDefinition.serviceLayer()", throwing = "e")
	public void logAfterThrowingAllMethods(JoinPoint pjp ,Exception e) throws Throwable {
		/*System.out.println("AccountLoggingAspect.logAfterThrowingAllMethods()");
		System.out.println("Exception catched : " + e.getClass().getCanonicalName());
		System.out.println("Exception catched : " + e.getLocalizedMessage());
		System.out.println("cause" + e.getCause());
		System.out.println("stack trace" + e);*/
		mailService.sendExceptionMail("Exception Thrown in "+  pjp.getSignature().getName() + "() in " + pjp.getTarget().getClass().getSimpleName() + " at " + new Date(),e.toString());
		LOGGER.warn("LoggingAspect.logAfterThrowingAllMethods()");
		LOGGER.warn("Exception catched : " + e.getClass().getCanonicalName());
	}

	@AfterThrowing(value = "com.dpdocter.aop.PointcutDefinition.serviceLayer()", throwing = "e")
	public void logAfterThrowingAllMethods(DuplicateKeyException e) throws Throwable {
		System.out.println("AccountLoggingAspect.logAfterThrowingAllMethods()");
		System.out.println("Exception catched : " + e.getClass().getCanonicalName());
		
		LOGGER.warn("LoggingAspect.logAfterThrowingAllMethods()");
		LOGGER.warn("Exception catched : " + e.getClass().getCanonicalName());
	}

	
	@AfterThrowing(value = "com.dpdocter.aop.PointcutDefinition.serviceLayer()", throwing = "e")
	public void logAfterThrowingAllMethods(BusinessException e) throws Throwable {
		System.out.println("AccountLoggingAspect.logAfterThrowingAllMethods()");
		System.out.println("Exception catched : " + e.getClass().getCanonicalName());
		
		LOGGER.warn("LoggingAspect.logAfterThrowingAllMethods()");
		LOGGER.warn("Exception catched : " + e.getClass().getCanonicalName());
	}

}
