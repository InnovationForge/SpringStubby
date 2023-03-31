package com.innovationforge.springstubby;

import com.github.tomakehurst.wiremock.servlet.WireMockHandlerDispatchingServlet;
import com.github.tomakehurst.wiremock.servlet.WireMockWebContextListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.ServletWrappingController;

import java.util.Properties;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

@SpringBootApplication
public class SpringStubbyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringStubbyApplication.class, args);
	}

	@Bean
	WireMockWebContextListener WireMockWebContextListener() {
		return new WireMockWebContextListener();
	}

	@Bean
	ServletWrappingController wireMockController() {
		ServletWrappingController controller = new ServletWrappingController();
		controller.setServletClass(WireMockHandlerDispatchingServlet.class);
		controller.setBeanName("wireMockController");
		Properties properties = new Properties();
		properties.setProperty("RequestHandlerClass", "com.github.tomakehurst.wiremock.http.StubRequestHandler");
		controller.setInitParameters(properties);
		return controller;
	}

	@Bean
	SimpleUrlHandlerMapping wireMockControllerMapping() {
		SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
		Properties urlProperties = new Properties();
		urlProperties.put("/*", "wireMockController");
		mapping.setMappings(urlProperties);
		mapping.setOrder(Integer.MAX_VALUE - 1);
		return mapping;
	}

	@Bean
	ServletWrappingController wireMockAdminController() {
		ServletWrappingController controller = new ServletWrappingController();
		controller.setServletClass(WireMockHandlerDispatchingServlet.class);
		controller.setBeanName("wireMockAdminController");
		Properties properties = new Properties();
		properties.setProperty("RequestHandlerClass", "com.github.tomakehurst.wiremock.http.AdminRequestHandler");
		controller.setInitParameters(properties);
		return controller;
	}

	@Bean
	SimpleUrlHandlerMapping wireMockAdminControllerMapping() {
		SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
		Properties urlProperties = new Properties();
		urlProperties.put("/__admin/*", "wireMockAdminController");
		mapping.setMappings(urlProperties);
		mapping.setOrder(Integer.MAX_VALUE - 2);
		return mapping;
	}

	@EventListener(ApplicationReadyEvent.class)
	void configureStubs() {
		stubFor(get(urlPathMatching("/hello-world")).willReturn(aResponse()
				.withHeader("Content-Type", "text/plain")
				.withBody("Hello World")));
	}
}