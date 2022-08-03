package com.example.crowdfunding.controller;

import com.example.crowdfunding.controller.chain.CrowdfundingCellCreator;
import com.example.crowdfunding.controller.chain.Pledger;
import com.example.crowdfunding.controller.exception.NotAllowedPledgedAmountException;
import com.example.crowdfunding.controller.exception.ProjectNotFoundException;
import com.example.crowdfunding.model.Backer;
import com.example.crowdfunding.model.Project;
import org.nervos.ckb.type.OutPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class Controller {
    private static final Logger log = LoggerFactory.getLogger(Controller.class);

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private BackerRepository backerRepository;
    @Autowired
    private CrowdfundingCellCreator creator;
    @Autowired
    private Pledger pledger;

    public Controller() {
    }

    @GetMapping("/projects")
    public List<Project> getAll() {
        // TODO: update crowdfunding progress from CKB
        return projectRepository.findAll();
    }

    @GetMapping("/projects/{id}")
    public Project getOne(@PathVariable Long id) {
        // TODO: update crowdfunding progress from CKB
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
    }

    @GetMapping("/projects/{id}/backers")
    public List<Backer> getBackers(@PathVariable Long id) {
        return backerRepository.findAllByProjectId(id);
    }

    @PostMapping("/projects")
    public Project newProject(@RequestBody Project newProject) {
        Project.init(newProject);
        newProject.setCrowdfundingCell(creator.createCrowdfundingCell(newProject));
        Project project = projectRepository.save(newProject);
        log.info("save new project: " + project);
        return project;
    }

    @PutMapping("/projects/{id}")
    public Project replaceProject(@RequestBody Project newProject, @PathVariable Long id) {
        return projectRepository.findById(id)
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
                    if (newProject.getStartupCKB() != null) {
                        project.setStartupCKB(newProject.getStartupCKB());
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
                    if (newProject.getCrowdfundingCell() != null) {
                        project.setCrowdfundingCell(newProject.getCrowdfundingCell());
                    }
                    return projectRepository.save(project);
                })
                .orElseGet(() -> {
                    newProject.setId(id);
                    return projectRepository.save(newProject);
                });
    }

    @DeleteMapping("/projects/{id}")
    public void deleteProject(@PathVariable Long id) {
        projectRepository.deleteById(id);
    }

    @PostMapping("/projects/{id}/pledge")
    public OutPoint pledgeProject(@RequestBody Backer backer, @PathVariable Long id) {
        Project project = getOne(id);
        if (!project.inAllowedPledgeAmounts(backer.getPledgedCKB())) {
            throw new NotAllowedPledgedAmountException(project.allowedPledgeAmounts(), backer.getPledgedCKB());
        }
        OutPoint outPoint = pledger.pledge(backer, project);
        backer.setPledgedCell(outPoint);
        backer.setProjectId(id);
        backerRepository.save(backer);
        project.incrementNumberOfBacker();
        project.incrementNumberOfBackerInDelivery(backer.getPledgedCKB());
        project.incrementPledgedCKB(backer.getPledgedCKB());
        projectRepository.save(project);
        log.info("pledge project: " + project.getId() + " from backer: " + backer.getAddressType() + " with CKB: " + backer.getPledgedCKB());
        return outPoint;
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
