package com.example.crowdfunding.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.nervos.ckb.type.Script;
import org.nervos.ckb.utils.Numeric;
import org.nervos.ckb.utils.address.Address;

import java.time.LocalDate;
import java.util.Map;

class ProjectTest {

    @Test
    void toData() {
//        Project project1 = new Project();
//        project1.setStartDate(LocalDate.of(2020, 1, 1));
//        project1.setTargetCKB(200L);
//        project1.setStartupCKB(100L);
//        project1.setMilestones(new Milestone[]{
//                new Milestone(LocalDate.of(2022, 12, 1), 75, "Publish 3 levels, need 30% of funding", 30),
//                new Milestone(LocalDate.of(2023, 12, 1), 100, "Publish Early Access, need 70% of funding", 60)});
//
//
//        project1.toData();
//
    }

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testJson() throws JsonProcessingException {
        String json = "{\n"
                + "  \"id\": 8,\n"
                + "  \"status\": \"CREATED\",\n"
                + "  \"name\": \"a test project\",\n"
                + "  \"description\": \"the best game\",\n"
                + "  \"creatorAddress\": \"ckt1qzda0cr08m85hc8jlnfp3zer7xulejywt49kt2rr0vthywaa50xwsqvglkprurm00l7hrs3rfqmmzyy3ll7djdsujdm6z\",\n"
                + "  \"targetCKB\": 1000,\n"
                + "  \"startDate\": \"2023-09-20\",\n"
                + "  \"endDate\": \"2023-12-30\",\n"
                + "  \"milestones\": [\n"
                + "    {\n"
                + "      \"dueDate\": \"2023-01-01\",\n"
                + "      \"targetCKBPercentage\": 50,\n"
                + "      \"description\": \"demo\",\n"
                + "      \"approvalRatioThreshold\": 70,\n"
                + "      \"numberOfVotedNo\": 0\n"
                + "    },\n"
                + "    {\n"
                + "      \"dueDate\": \"2023-12-01\",\n"
                + "      \"targetCKBPercentage\": 100,\n"
                + "      \"description\": \"final release\",\n"
                + "      \"approvalRatioThreshold\": 70,\n"
                + "      \"numberOfVotedNo\": 0\n"
                + "    }\n"
                + "  ],\n"
                + "  \"deliveries\": {\n"
                + "    \"200\": { \"reward\": \"discount\", \"numberOfBacker\": 0 },\n"
                + "    \"500\": { \"reward\": \"free game\", \"numberOfBacker\": 0 }\n"
                + "  },\n"
                + "  \"startupCKB\": 500,\n"
                + "  \"nextMilestoneIndex\": null,\n"
                + "  \"pledgedCKB\": 0,\n"
                + "  \"numberOfBacker\": 0,\n"
                + "  \"crowdfundingCell\": {\n"
                + "    \"txHash\": \"0xca840c233954e8e7f836e8829496243b65730d4aa54d97084713d6b25e7cf8ae\",\n"
                + "    \"index\": 0\n"
                + "  },\n"
                + "  \"nextMilestone\": null\n"
                + "}\n";
        Map<String, Object> p = objectMapper.readValue(json, Map.class);
        System.out.println(p.get("crowdfundingCell").getClass());
        System.out.println(p.get("milestones").getClass());
        System.out.println(p);
    }

    @Test
    public void tt1() {
        String addr = "ckt1qzda0cr08m85hc8jlnfp3zer7xulejywt49kt2rr0vthywaa50xwsqdndplz8ndtn096pdx2d69hlh529hnt63sy63ssn";
        Script lock = Address.decode(addr).getScript();
        System.out.println(Numeric.toHexString(lock.computeHash()));
    }

}
