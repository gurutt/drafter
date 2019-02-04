package org.gurutt.drafter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class DrafterApplication {

	public static void main(String[] args) {
		ApiContextInitializer.init();
		SpringApplication.run(DrafterApplication.class, args);
	}

}

