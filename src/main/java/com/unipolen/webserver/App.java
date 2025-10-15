package com.unipolen.webserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebApplicationContext;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class App {


	public static final ApplicationContext CONTEXT =
			new AnnotationConfigReactiveWebApplicationContext(WebConfig.class);

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}


}
