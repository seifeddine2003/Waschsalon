package com.start.waschmachine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class WaschmachineApplication {

	public static void main(String[] args) {
		SpringApplication.run(WaschmachineApplication.class, args);
	}

}
