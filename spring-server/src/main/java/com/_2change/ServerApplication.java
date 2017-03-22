package com._2change;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) {
		FirebaseOptions options = new FirebaseOptions.Builder().setServiceAccount(new FileInputStream(../../../../2Change-daa80c2e01cb.json))

		SpringApplication.run(ServerApplication.class, args);
	}
}
