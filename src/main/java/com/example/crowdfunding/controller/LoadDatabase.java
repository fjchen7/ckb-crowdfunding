package com.example.crowdfunding.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class LoadDatabase implements WebMvcConfigurer {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    // Load data
    @Bean
    CommandLineRunner initDatabase(ProjectRepository repository) {
        return args -> {
            log.info("Preloading " + repository.save(ProjectFactory.project1()));
            log.info("Preloading " + repository.save(ProjectFactory.project2()));
        };
    }
}
