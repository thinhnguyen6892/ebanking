package edu.hcmus.project.ebanking.backoffice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.*;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	public static final Contact DEFAULT_CONTACT = new Contact(
			"T2TrC Bank", "http://www.t2TrC.com", "support@t2trc.com");

	public static final ApiInfo DEFAULT_API_INFO = new ApiInfo(
			"T2TrC eBanking API", "The API documentation used for develop e-banking system", "1.0",
			"urn:#terms", DEFAULT_CONTACT,
			"Apache 2.0", "http://www.apache.org/licenses/LICENSE-2.0", new ArrayList());

	private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES = 
			new HashSet<String>(Arrays.asList("application/json"));


	@Bean
	public Docket api_admin() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("administrator-employee-api")
				.select()
				.apis(RequestHandlerSelectors.basePackage("edu.hcmus.project.ebanking.backoffice.resource"))
				.paths(PathSelectors.regex("\\/(accounts|users)(\\/[a-zA-Z\\/\\{}]+|$)"))
				.build()
				.apiInfo(new ApiInfoBuilder().title("Administrator - Employee  API").description("Documentation For Developing Administrator and Employee Functionality").build());
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("all")
				.select()
				.apis(RequestHandlerSelectors.basePackage("edu.hcmus.project.ebanking.backoffice.resource"))
				.build()
				.apiInfo(DEFAULT_API_INFO)
				.produces(DEFAULT_PRODUCES_AND_CONSUMES)
				.consumes(DEFAULT_PRODUCES_AND_CONSUMES);
	}
}
