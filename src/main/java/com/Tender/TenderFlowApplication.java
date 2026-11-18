package com.Tender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TenderFlowApplication {

	public static void main(String[] args) {
		SpringApplication.run(TenderFlowApplication.class, args);
	}

}
