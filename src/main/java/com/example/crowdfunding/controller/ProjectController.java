package com.example.crowdfunding.controller;

import com.example.crowdfunding.controller.exception.ProjectNotFoundException;
import com.example.crowdfunding.model.Backer;
import com.example.crowdfunding.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class ProjectController {
    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);

    private final ProjectRepository repository;

    public ProjectController(ProjectRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/projects")
    public List<Project> all() {
        // TODO: update crowdfunding progress from CKB
        return repository.findAll();
    }

    @GetMapping("/projects/{id}")
    public Project one(@PathVariable Long id) {
        // TODO: update crowdfunding progress from CKB
        return repository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
    }

    @PostMapping("/projects")
    public Project newProject(@RequestBody Project newProject) {
        // TODO: 1. send tx on CKB to create c-cell
        //       2. store on-chain information to DB
        newProject.setStarDate(LocalDate.now());
        Project project = repository.save(newProject);
        log.info("save new project: " + project);
        return project;
    }

    @PutMapping("/projects/{id}")
    public Project replaceProject(@RequestBody Project newProject, @PathVariable Long id) {
        return repository.findById(id)
                .map(project -> {
                    project.setName(newProject.getName());
                    project.setDescription(newProject.getDescription());
                    project.setTarget(newProject.getTarget());
                    project.setMilestoneDates(newProject.getMilestoneDates());
                    project.setMilestoneTargets(newProject.getMilestoneTargets());
                    return repository.save(project);
                })
                .orElseGet(() -> {
                    newProject.setId(id);
                    return repository.save(newProject);
                });
    }

    @DeleteMapping("/projects/{id}")
    public void deleteProject(@PathVariable Long id) {
        repository.deleteById(id);
    }

    @PostMapping("/projects/{id}/pledge")
    public void pledgeProject(@RequestBody Backer backer, @PathVariable Long id) {
        // TODO: Send tx to the cheque-like address
    }

    @PostMapping("/projects/{id}/voteno")
    public void voteNo(@RequestBody Backer backer, @PathVariable Long id) {
        // TODO: Send tx to vote no
    }

    @PostMapping("/projects/{id}/refund")
    public void refund(@PathVariable Long id) {
        // TODO: send tx to refund
    }
}
