package com.dpdocter.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class PointcutDefinition {

	@Pointcut("within(com.dpdocter.webservices..*)")
	public void apiLayer() {
	}

	//&& !@annotation(com.dpdocter.aop.NoLogging) 
	@Pointcut("within(com.dpdocter.services..*) ")
	public void serviceExceptionLayer() {
	}
	@Pointcut("within(com.dpdocter.services..*) ")
	public void serviceLayer() {
	}

	/*@Pointcut("within(com.dpdoctor.repository..*)")
	public void repositoryLayer() {
	}*/

}
