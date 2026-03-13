package com.noxus.youshare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class YoushareApplication {

	public static void main(String[] args) {
		SpringApplication.run(YoushareApplication.class, args);
	}

}
