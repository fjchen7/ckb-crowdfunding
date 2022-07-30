package com.example.crowdfunding.controller;

import com.example.crowdfunding.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;

@Configuration
class LoadDatabase implements WebMvcConfigurer {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    // Load data
    @Bean
    CommandLineRunner initDatabase(ProjectRepository repository) {
        return args -> {
            Project project = new Project();
            project.setName("Project 1");
            project.setDescription("Project 1 description");
            project.setTarget(100);
            project.setMilestoneDates(new LocalDate[]{LocalDate.now(),
                    LocalDate.of(2023, 1, 1)});
            project.setMilestoneTargets(new short[]{10});
            project.setMilestoneTargets(new short[]{30});

            log.info("Preloading " + repository.save(project));
        };
    }
}
