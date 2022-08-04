package com.example.crowdfunding.controller;

import com.example.crowdfunding.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.nervos.ckb.type.CellOutput;
import org.nervos.ckb.type.OutPoint;
import org.nervos.ckb.type.Script;
import org.nervos.ckb.utils.Numeric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

@Configuration
class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private BackerRepository backerRepository;

    // Load data
    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            log.info("Preloading " + projectRepository.save(readProject("project1")));
            log.info("Preloading " + projectRepository.save(readProject("project2")));

            log.info("Preloading " + backerRepository.save(readBacker("backer11")));
            log.info("Preloading " + backerRepository.save(readBacker("backer13")));

            //                        log.info("Preloading " + projectRepository.save(MockEntityFactory.project1()));
            //                        log.info("Preloading " + projectRepository.save(MockEntityFactory.project2()));
            //                        log.info("Preloading " + projectRepository.save(MockEntityFactory.project3()));
            //                        log.info("Preloading " + backerRepository.save(MockEntityFactory.backer1().get(0)));
            //                        log.info("Preloading " + backerRepository.saveAll(MockEntityFactory.backer3()));
        };
    }

    private Project readProject(String fileName) throws IOException {
        File resource = new ClassPathResource(
                "static/" + fileName + ".json").getFile();
        String json = new String(
                Files.readAllBytes(resource.toPath()));
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.readValue(json, Map.class);
        Project project = new Project();
        project.setId(Long.valueOf((int) map.get("id")));
        project.setStatus(Project.Status.valueOf((String) map.get("status")));
        project.setName((String) map.get("name"));
        project.setDescription((String) map.get("description"));
        project.setCreatorAddress((String) map.get("creatorAddress"));
        project.setTargetCKB(Long.valueOf((int) map.get("targetCKB")));
        project.setStartDate(parseDate((String) map.get("startDate")));
        project.setEndDate(parseDate((String) map.get("endDate")));

        ArrayList milestonesJson = (ArrayList) (map.get("milestones"));
        Milestone[] milestones = new Milestone[milestonesJson.size()];
        for (int i = 0; i < milestonesJson.size(); i++) {
            Map mmap = (Map) milestonesJson.get(i);
            Milestone m = new Milestone();
            m.setDescription((String) mmap.get("description"));
            m.setTargetCKBPercentage((int) mmap.get("targetCKBPercentage"));
            m.setDueDate(parseDate((String) mmap.get("dueDate")));
            m.setApprovalRatioThreshold((int) mmap.get("approvalRatioThreshold"));
            m.setNumberOfVotedNo((int) mmap.get("numberOfVotedNo"));
            milestones[i] = m;
        }
        project.setMilestones(milestones);

        Map deliveriesJson = (Map) (map.get("deliveries"));
        Map<Long, Delivery> deliveries = new TreeMap<>();
        for (Object index: deliveriesJson.keySet()) {
            Map value = (Map) deliveriesJson.get(index);
            Delivery m = new Delivery();
            m.setReward((String) value.get("reward"));
            m.setNumberOfBacker((int) value.get("numberOfBacker"));
            deliveries.put(Long.valueOf((String) index), m);
        }
        project.setDeliveries(deliveries);
        project.setStartupCKB(Long.valueOf((int) map.get("startupCKB")));
        if (map.get("nextMilestoneIndex") != null) {
            project.setNextMilestoneIndex((int) map.get("nextMilestoneIndex"));
        }
        project.setPledgedCKB(Long.valueOf((int) map.get("pledgedCKB")));
        project.setNumberOfBacker((int) map.get("pledgedCKB"));

        Map crowdfundingCellJson = (Map) map.get("crowdfundingCell");
        OutPoint crowdfundingCell = new OutPoint();
        crowdfundingCell.txHash = Numeric.hexStringToByteArray((String) crowdfundingCellJson.get("txHash"));
        crowdfundingCell.index = (int) crowdfundingCellJson.get("index");
        project.setCrowdfundingCell(crowdfundingCell);
        return project;
    }

    private LocalDate parseDate(String dateString) {
        String[] d = dateString.split("-");
        return LocalDate.of(Integer.valueOf(d[0]), Integer.valueOf(d[1]),
                Integer.valueOf(d[2]));
    }

    private Backer readBacker(String fileName) throws IOException {
        File resource = new ClassPathResource(
                "static/" + fileName + ".json").getFile();
        String json = new String(
                Files.readAllBytes(resource.toPath()));
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.readValue(json, Map.class);
        Backer backer = new Backer();
        backer.setPrivateKey((String) map.get("privateKey"));
        backer.setProjectId(Long.valueOf((int) map.get("projectId")));
        backer.setId(Long.valueOf((int) map.get("id")));
        backer.setPledgedCKB(Long.valueOf((int) map.get("pledgedCKB")));
        ArrayList list = (ArrayList) map.get("voteNos");
        backer.setVoteNos(new boolean[list.size()]);

        OnChainCell cell = new OnChainCell();
        map = (Map) map.get("currentPledgedCell");
        Map outPointJson = (Map) map.get("outPoint");
        OutPoint outPoint = new OutPoint();
        outPoint.txHash = Numeric.hexStringToByteArray((String) outPointJson.get("txHash"));
        outPoint.index = (int) outPointJson.get("index");
        cell.setOutPoint(outPoint);
        Map OutputJson = (Map) map.get("output");
        CellOutput output = new CellOutput();
        output.capacity = Long.valueOf((long) OutputJson.get("capacity"));
        output.lock = parseScript((Map) OutputJson.get("lock"));
        output.type = parseScript((Map) OutputJson.get("type"));
        cell.setOutput(output);
        cell.setOutPoint(outPoint);
        cell.setOutputData(Numeric.hexStringToByteArray((String) map.get("outputData")));
        backer.addPledgeCell(cell);
        System.out.println(backer);
        return backer;
    }

    private Script parseScript(Map scriptJson) {
        if (scriptJson == null) {
            return null;
        }
        Script script = new Script();
        script.codeHash = Numeric.hexStringToByteArray((String) scriptJson.get("codeHash"));
        script.args = Numeric.hexStringToByteArray((String) scriptJson.get("args"));
        script.hashType = Script.HashType.valueOf((String) scriptJson.get("hashType"));
        return script;
    }
}
