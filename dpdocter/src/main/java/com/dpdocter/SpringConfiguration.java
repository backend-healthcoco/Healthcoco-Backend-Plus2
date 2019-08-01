//package com.dpdocter;
//
//import org.apache.velocity.app.VelocityEngine;
//import org.apache.velocity.runtime.RuntimeConstants;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class SpringConfiguration {
//	@Bean
//	public VelocityEngine velocityEngine() throws Exception {
//		System.out.println("set VelocityEngine");
//	    VelocityEngine velocityEngine = new VelocityEngine();
//	    velocityEngine.setProperty("input.encoding", "UTF-8");
//	    velocityEngine.setProperty("output.encoding", "UTF-8");
//	    velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,"org.apache.velocity.runtime.log.Log4JLogChute" );
//	    velocityEngine.setProperty("runtime.log", "/var/log/dpdocter/velocity.log");
//	    velocityEngine.setProperty("resource.loader", "class, file");
//	    velocityEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
//	    velocityEngine.setProperty("file.resource.loader.class","org.apache.velocity.runtime.resource.loader.FileResourceLoader");
//	    velocityEngine.setProperty("file.resource.loader.path","/opt/tomcat/latest/webapps/dpdocter/WEB-INF/classes");
//	    velocityEngine.setProperty("class.resource.loader.cache", "false");
//	    velocityEngine.setProperty("file.resource.loader.cache", "true");
//
//	    velocityEngine.init();
//	    
//	    return velocityEngine;
//	}
//}
