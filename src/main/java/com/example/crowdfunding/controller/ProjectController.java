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
                    if (newProject.getStatus() != null) {
                        project.setStatus(newProject.getStatus());
                    }
                    if (newProject.getName() != null) {
                        project.setName(newProject.getName());
                    }
                    if (newProject.getDescription() != null) {
                        project.setDescription(newProject.getDescription());
                    }
                    if (newProject.getCreatorAddress() != null) {
                        project.setCreatorAddress(newProject.getCreatorAddress());
                    }
                    if (newProject.getTargetCKB() != null) {
                        project.setTargetCKB(newProject.getTargetCKB());
                    }
                    if (newProject.getStartDate() != null) {
                        project.setStartDate(newProject.getStartDate());
                    }
                    if (newProject.getEndDate() != null) {
                        project.setEndDate(newProject.getEndDate());
                    }
                    if (newProject.getMilestones() != null) {
                        project.setMilestones(newProject.getMilestones());
                    }
                    if (newProject.getDeliveries() != null) {
                        project.setDeliveries(newProject.getDeliveries());
                    }
                    if (newProject.getNextMilestoneIndex() != null) {
                        project.setNextMilestoneIndex(newProject.getNextMilestoneIndex());
                    }
                    if (newProject.getPledgedCKB() != null) {
                        project.setPledgedCKB(newProject.getPledgedCKB());
                    }
                    if (newProject.getNumberOfBacker() != null) {
                        project.setNumberOfBacker(newProject.getNumberOfBacker());
                    }
                    if (newProject.getNumberOfBackerInDeliveries() != null) {
                        project.setNumberOfBackerInDeliveries(newProject.getNumberOfBackerInDeliveries());
                    }
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
        Long[] pledgedAmounts = project.getDeliveries().keySet().toArray(new Long[0]);
        pledgedAmounts = java.util.Arrays.copyOf(pledgedAmounts, pledgedAmounts.length + 1);
        pledgedAmounts[pledgedAmounts.length - 1] = Long.MAX_VALUE;
        for (int i = pledgedAmounts.length - 1; i >= 0; i++) {
            if (backer.getPledgedCKB() >= pledgedAmounts[i]) {
                project.incrementNumberOfBackerInDelivery(pledgedAmounts[i]);
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
