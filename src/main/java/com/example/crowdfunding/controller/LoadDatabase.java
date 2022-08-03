package com.example.crowdfunding.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
class LoadDatabase implements WebMvcConfigurer {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private BackerRepository backerRepository;

    // Load data
    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            log.info("Preloading " + projectRepository.save(MockEntityFactory.project1()));
            log.info("Preloading " + projectRepository.save(MockEntityFactory.project2()));
            log.info("Preloading " + backerRepository.save(MockEntityFactory.backer1().get(0)));
        };
    }
}
