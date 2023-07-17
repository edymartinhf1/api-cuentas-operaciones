package com.bootcamp.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.bootcamp.bank.operaciones.*"})
public class ApiCuentasOperacionesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiCuentasOperacionesApplication.class, args);
	}

}
