package com.example.crowdfunding.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.nervos.ckb.Network;
import org.nervos.ckb.crypto.secp256k1.ECKeyPair;
import org.nervos.ckb.type.Script;
import org.nervos.ckb.utils.address.Address;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
public class Backer {
    @Id
    @GeneratedValue
    private Long id;
    private Long projectId;
    private Long pledgedCKB;

    @ElementCollection(targetClass = OnChainCell.class)
    @Column(columnDefinition = "BLOB")
    private List<OnChainCell> pledgedCells;
    private boolean[] voteNos;

    private String privateKey;

    public Backer() {
        pledgedCells = new ArrayList<>();
    }

    @JsonIgnore
    public List<OnChainCell> getPledgedCells() {
        return pledgedCells;
    }

    public OnChainCell getCurrentPledgedCell() {
        if (pledgedCells == null || pledgedCells.size() == 0) {
            return null;
        }
        return pledgedCells.get(pledgedCells.size() - 1);
    }

    public void setPledgedCells(List<OnChainCell> pledgedCells) {
        this.pledgedCells = pledgedCells;
    }

    public void addPledgeCell(OnChainCell pledgedCell) {
        pledgedCells.add(pledgedCell);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public Long getPledgedCKB() {
        return pledgedCKB;
    }

    public void setPledgedCKB(Long pledgedCKB) {
        this.pledgedCKB = pledgedCKB;
    }

    public boolean[] getVoteNos() {
        return voteNos;
    }

    public void setVoteNos(boolean[] voteNos) {
        this.voteNos = voteNos;
    }

    @JsonIgnore
    public Address getAddressType() {
        ECKeyPair keyPair = ECKeyPair.create(privateKey);
        Script lock = Script.generateSecp256K1Blake160SignhashAllScript(keyPair);
        return new Address(lock, Network.TESTNET);
    }

    public String getAddress() {
        return getAddressType().encode();
    }

    @Override
    public String toString() {
        return "Backer{" +
                "id=" + id +
                ", projectId=" + projectId +
                ", pledgedCKB=" + pledgedCKB +
                ", pledgedCells=" + pledgedCells +
                ", voteNos=" + Arrays.toString(voteNos) +
                ", privateKey='" + privateKey + '\'' +
                '}';
    }
}
