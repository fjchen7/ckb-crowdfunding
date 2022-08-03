package com.example.crowdfunding.controller;

import com.example.crowdfunding.model.*;
import org.nervos.ckb.type.CellOutput;
import org.nervos.ckb.type.OutPoint;
import org.nervos.ckb.type.Script;
import org.nervos.ckb.utils.Numeric;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class MockEntityFactory {
    public static Project project1() {
        Project project1 = new Project();
        project1.setName("A project");
        project1.setDescription("blabla");
        project1.setPledgedCKB(600L);
        project1.setTargetCKB(1000000L);
        project1.setStartupCKB(100000L);
        project1.setStartDate(LocalDate.of(2022, 7, 1));
        project1.setEndDate(LocalDate.of(2022, 10, 1));
        project1.setCreatorAddress("ckt1qzda0cr08m85hc8jlnfp3zer7xulejywt49kt2rr0vthywaa50xwsq0yvcdtsu5wcr2jldtl72fhkruf0w5vymsp6rk9r");
        project1.setStatus(Project.Status.PLEDGING);
        project1.setMilestones(new Milestone[]{
                new Milestone(LocalDate.of(2022, 12, 1), 30, "Publish 3 levels, need 30% of funding", 30),
                new Milestone(LocalDate.of(2023, 12, 1), 70, "Publish Early Access, need 70% of funding", 60),
                new Milestone(LocalDate.of(2024, 12, 1), 100, "Publish final version, need 100% of funding", 100)});
        project1.setDeliveries(new TreeMap<Long, Delivery>() {{
            put(1000L, new Delivery("A manual"));
            put(10000L, new Delivery("A manual + instruction book"));
            put(100000L, new Delivery("A manual + instruction book + original drawing design book"));
        }});
        project1.setNextMilestoneIndex(0);
        project1.setNumberOfBacker(15);
        project1.setCrowdfundingCell(new OutPoint(Numeric.hexStringToByteArray("0xeaf38cc4b076a18fe24a6fce83fb7142f64ee85e695e6b0e815d809124d5e1da"), 0));
        return project1;
    }

    public static Project project2() {
        Project project2 = new Project();
        project2.setName("A video game");
        project2.setDescription("A ACT game");
        project2.setPledgedCKB(0L);
        project2.setTargetCKB(10000L);
        project2.setStartupCKB(1000L);
        project2.setStartDate(LocalDate.of(2022, 9, 1));
        project2.setEndDate(LocalDate.of(2022, 12, 1));
        project2.setCreatorAddress("ckt1qzda0cr08m85hc8jlnfp3zer7xulejywt49kt2rr0vthywaa50xwsqvglkprurm00l7hrs3rfqmmzyy3ll7djdsujdm6z");
        project2.setStatus(Project.Status.CREATED);
        project2.setMilestones(new Milestone[]{
                new Milestone(LocalDate.of(2023, 1, 1), 20, "Publish demo, need 20% of funding", 20),
                new Milestone(LocalDate.of(2023, 8, 1), 60, "Publish Early Access, need 70% of funding", 70),
                new Milestone(LocalDate.of(2024, 12, 31), 100, "Publish final version, need 100% of funding", 100)});
        project2.setDeliveries(new TreeMap<Long, Delivery>() {{
            put(1000L, new Delivery("A painting"));
            put(10000L, new Delivery("A painting + base game"));
            put(100000L, new Delivery("A painting + base game + DLC"));
        }});
        project2.setNextMilestoneIndex(0);
        project2.setCrowdfundingCell(new OutPoint(Numeric.hexStringToByteArray("0x60bc7822c4372ba0154fed16ed520d80e7d87505a95bac0561f95134f71899ba"), 0));
        return project2;
    }

    public static List<Backer> backer1() {
        List<Backer> backers = new ArrayList<>();
        Backer backer1 = new Backer();
        backer1.setProjectId(1L);
        backer1.setPledgedCKB(1000L);
        OnChainCell onChainCell = new OnChainCell();
        onChainCell.setOutPoint(new OutPoint(Numeric.hexStringToByteArray("0xf00b0b53e2a3bf866c7b2b6aa2bc379f6b232abdef560d032f20d09242d87adb"), 0));
        onChainCell.setOutput(new CellOutput(1000, new Script(Script.SECP256K1_BLAKE160_SIGNHASH_ALL_CODE_HASH,
                Numeric.hexStringToByteArray("\"0xe4327ffc3d9be56ae179c2f92ddd998667059d424d3ff1d4c912df6e3894bfb0568492c3ac8d084ae09e5378bed31c63fdd018eb\""))));
        onChainCell.setOutputData(new byte[0]);

        backer1.addPledgeCell(onChainCell);
        backer1.setPrivateKey("0xa0f2d51b7c9c2a21736617285115a42a124447f277b6df61370ab996583b169f");
        backers.add(backer1);
        backer1.setVoteNos(new boolean[3]);
        return backers;
    }
}
