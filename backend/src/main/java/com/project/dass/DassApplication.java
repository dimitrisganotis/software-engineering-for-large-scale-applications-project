package com.project.dass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.project.dass")
public class DassApplication {

	public static void main(String[] args) {
		SpringApplication.run(DassApplication.class, args);
	}

}
