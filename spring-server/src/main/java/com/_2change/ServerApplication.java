package com._2change;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args) throws FileNotFoundException {
		//Inicializar SDK
		FirebaseOptions options = new FirebaseOptions.Builder()
				.setServiceAccount(new FileInputStream("../../../../2Change-daa80c2e01cb.json"))
				.setDatabaseUrl("https://change-64bd0.firebaseio.com/")
				.build();
		FirebaseApp.initializeApp(options);

		SpringApplication.run(ServerApplication.class, args);
	}
}
