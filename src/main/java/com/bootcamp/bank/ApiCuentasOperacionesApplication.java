package com.bootcamp.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.bootcamp.bank.operaciones.*"})
@EnableEurekaClient
public class ApiCuentasOperacionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiCuentasOperacionesApplication.class, args);
	}

}
