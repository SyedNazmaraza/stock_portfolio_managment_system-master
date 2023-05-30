package com.springboot.evaluation_task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class EvaluationTaskApplication {


	public static void main(String[] args) {
		SpringApplication.run(EvaluationTaskApplication.class, args);



//	@Bean
//	CommandLineRunner run(UserService userService){
//		return args -> {
//			userService.getAllPortfolio(new SessionTokenRequest());
//		};
//	}


	}
}
