package com.example.crowdfunding.controller;

import com.example.crowdfunding.controller.exception.ProjectNotFoundException;
import com.example.crowdfunding.model.Backer;
import com.example.crowdfunding.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProjectController {
    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);

    private final ProjectRepository repository;

    public ProjectController(ProjectRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/projects")
    public List<Project> getAll() {
        // TODO: update crowdfunding progress from CKB
        return repository.findAll();
    }

    @GetMapping("/projects/{id}")
    public Project getOne(@PathVariable Long id) {
        // TODO: update crowdfunding progress from CKB
        return repository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
    }

    @PostMapping("/projects")
    public Project newProject(@RequestBody Project newProject) {
        // TODO: 1. send tx on CKB to create c-cell
        //       2. store on-chain information to DB
        for (Long pledgeAmount: newProject.getDeliveries().keySet()) {
            newProject.setNumberOfBackerInDelivery(pledgeAmount, 0);
        }
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
                    project.setCreatorAddress(newProject.getCreatorAddress());
                    project.setTarget(newProject.getTarget());
                    project.setStarDate(newProject.getStarDate());
                    project.setEndDate(newProject.getEndDate());
                    project.setMilestones(newProject.getMilestones());
                    project.setDeliveries(newProject.getDeliveries());
                    project.setNumberOfBacker(newProject.getNumberOfBacker());
                    project.setNumberOfBackerInDeliveries(newProject.getNumberOfBackerInDeliveries());
                    project.setStatus(newProject.getStatus());
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
        Project project = getOne(id);
        project.incrementNumberOfBacker();
        // update number of backer in delivery
        Long[] pledgeAmounts = project.getDeliveries().keySet().toArray(new Long[0]);
        pledgeAmounts = java.util.Arrays.copyOf(pledgeAmounts, pledgeAmounts.length + 1);
        pledgeAmounts[pledgeAmounts.length - 1] = Long.MAX_VALUE;
        for (int i = pledgeAmounts.length - 1; i >= 0; i++) {
            if (backer.getPledgeAmount() >= pledgeAmounts[i]) {
                project.incrementNumberOfBackerInDelivery(pledgeAmounts[i]);
                break;
            }
        }
        // TODO: Send tx to the cheque-like address
    }

    @PostMapping("/projects/{id}/voteno")
    public void voteNo(@RequestBody Backer backer, @PathVariable Long id) {
        Project project = getOne(id);
        project.getNextMilestone().incrementNumberOfVoteNo();
        // TODO: Send tx to vote no
    }

    @PostMapping("/projects/{id}/refund")
    public void refund(@PathVariable Long id) {
        Project project = getOne(id);
        project.setStatus(Project.Status.FAILED);
        // TODO: send tx to refund
    }
}
