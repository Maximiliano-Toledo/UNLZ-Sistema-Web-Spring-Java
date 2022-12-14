package com.unlz.tecjava.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableJpaRepositories({"com.unlz.tecjava.app.models.dao", "com.unlz.tecjava.app.security.token.dao"})
@SpringBootApplication
public class AbmApplication {

	public static void main(String[] args) {
		SpringApplication.run(AbmApplication.class, args);
	}

}
