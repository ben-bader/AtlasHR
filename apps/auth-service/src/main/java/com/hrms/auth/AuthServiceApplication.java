package com.hrms.auth;

import com.hrms.auth.domain.model.Role;
import com.hrms.auth.domain.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
		log.info("Auth Service started successfully");
	}

	@Bean
	CommandLineRunner init(RoleRepository roleRepository) {
		return args -> {
			log.info("Initializing default roles...");
			
			String[] defaultRoles = {"USER", "ADMIN", "MANAGER"};
			String[] roleDescriptions = {
				"Default user role",
				"Administrator role with full access",
				"Manager role for team management"
			};

			for (int i = 0; i < defaultRoles.length; i++) {
				if (roleRepository.findByName(defaultRoles[i]).isEmpty()) {
					Role role = Role.builder()
							.name(defaultRoles[i])
							.description(roleDescriptions[i])
							.build();
					roleRepository.save(role);
					log.info("Created role: {}", defaultRoles[i]);
				}
			}
		};
	}

}
