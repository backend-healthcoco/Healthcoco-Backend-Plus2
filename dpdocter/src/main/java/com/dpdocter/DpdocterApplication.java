package com.dpdocter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling

@EnableMongoRepositories(basePackages="com.dpdocter.repository")
@EnableElasticsearchRepositories(basePackages="com.dpdocter.elasticsearch.repository")
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
//@ImportResource("classpath:spring-oauth-security.xml")
public class DpdocterApplication extends SpringBootServletInitializer implements CommandLineRunner{
	

	
		
    public static void main(String[] args) {
        SpringApplication.run(DpdocterApplication.class, args);
        
    }

    @Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return super.configure(builder);
	}
    
    @Override
	public void run(String... args) throws Exception {
//    	OAuth2ClientDetailsCollection oAuth2ClientDetailsCollection = auth2ClientDetailsRepository
//				.findByClientId("healthco2admin@16");
//    	oAuth2ClientDetailsCollection.setClientSecret(new CustomPasswordEncoder().encode("S5HA45KM5M3QX0KKG1"));
//    	auth2ClientDetailsRepository.save(oAuth2ClientDetailsCollection);
    }
}

