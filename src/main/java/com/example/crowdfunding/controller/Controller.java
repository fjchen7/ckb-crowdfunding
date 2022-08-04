package com.example.crowdfunding.controller;

import com.example.crowdfunding.controller.chain.Aggregator;
import com.example.crowdfunding.controller.chain.CrowdfundingCellCreator;
import com.example.crowdfunding.controller.chain.Pledger;
import com.example.crowdfunding.controller.exception.NotAllowedPledgedAmountException;
import com.example.crowdfunding.controller.exception.ProjectNotFoundException;
import com.example.crowdfunding.model.Backer;
import com.example.crowdfunding.model.OnChainCell;
import com.example.crowdfunding.model.Project;
import org.nervos.ckb.utils.Numeric;
import org.nervos.ckb.utils.Utils;
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
    @Autowired
    private Aggregator aggregator;

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
        System.out.println("finish init");
        newProject.setCrowdfundingCell(creator.createCrowdfundingCell(newProject));
        System.out.println("finish send tx");
        Project project = projectRepository.save(newProject);
        System.out.println("finish save");
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
    public OnChainCell pledgeProject(@RequestBody Backer backer, @PathVariable Long id) {
        Project project = getOne(id);
        if (!project.inAllowedPledgeAmounts(backer.getPledgedCKB())) {
            throw new NotAllowedPledgedAmountException(project.allowedPledgeAmounts(), backer.getPledgedCKB());
        }
        OnChainCell cell = pledger.pledge(backer, project);
        backer.setProjectId(id);
        backer.addPledgeCell(cell);
        backer.setVoteNos(new boolean[project.getMilestones().length]);
        backerRepository.save(backer);
        project.incrementNumberOfBacker();
        project.incrementNumberOfBackerInDelivery(backer.getPledgedCKB());
        project.incrementPledgedCKB(backer.getPledgedCKB());
        projectRepository.save(project);
        log.info("pledge project: " + project.getId() + " from backer: " + backer.getAddressType() + " with CKB: " + backer.getPledgedCKB());
        return cell;
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

    @PostMapping("/projects/{id}/aggregate")
    public String aggregate(@PathVariable Long id) {
        List<Backer> backers = getBackers(id);
        Project project = getOne(id);
        long amount;
        if (project.getNextMilestoneIndex() == null) {
            amount = project.getStartupCKB();
        } else {
            amount = project.getPledgedCKB() - project.getStartupCKB();
            int percentage = project.getNextMilestone().getTargetCKBPercentage();
            for (int i = project.getNextMilestoneIndex() - 1; i >= 0; i--) {
                percentage -= project.getMilestones()[i].getTargetCKBPercentage();
            }
            amount = amount * percentage / 100;
        }
        System.out.println("aggregate amount: " + amount);
        amount = Utils.ckbToShannon(amount);
        List<OnChainCell> onChainCells = aggregator.aggregate(backers, project, amount);

        for (int i = 0; i < backers.size(); i++) {
            backers.get(i).addPledgeCell(onChainCells.get(i));
            backerRepository.save(backers.get(i));
        }
        project.moveToNextMilestone();
        projectRepository.save(project);
        return Numeric.toHexString(onChainCells.get(0).getOutPoint().txHash);
    }
}
