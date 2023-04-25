package com.dpdocter.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class PointcutDefinition {

	@Pointcut("within(com.dpdocter.webservices..*)")
	public void apiLayer() {
	}

	@Pointcut("within(com.dpdocter.services..*) ")
	public void serviceExceptionLayer() {
	}

	@Pointcut("within(com.dpdocter.services..*) ")
	public void serviceLayer() {
	}

}
