package com.example.crowdfunding.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.nervos.ckb.Network;
import org.nervos.ckb.crypto.secp256k1.ECKeyPair;
import org.nervos.ckb.type.OutPoint;
import org.nervos.ckb.type.Script;
import org.nervos.ckb.utils.address.Address;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Arrays;

@Entity
public class Backer {
    @Id
    @GeneratedValue
    private Long id;
    private Long projectId;
    private Long pledgedCKB;

    private Long currentPledgedShannon;
    @Embedded
    private OutPoint currentPledgedCell;
    private boolean[] voteNos;

    private String privateKey;

    public Backer() {
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

    public Long getCurrentPledgedShannon() {
        return currentPledgedShannon;
    }

    public void setCurrentPledgedShannon(Long currentPledgedShannon) {
        this.currentPledgedShannon = currentPledgedShannon;
    }

    public boolean[] getVoteNos() {
        return voteNos;
    }

    public void setVoteNos(boolean[] voteNos) {
        this.voteNos = voteNos;
    }

    public OutPoint getCurrentPledgedCell() {
        return currentPledgedCell;
    }

    public void setCurrentPledgedCell(OutPoint currentPledgedCell) {
        this.currentPledgedCell = currentPledgedCell;
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
                ", currentPledgedShannon=" + currentPledgedShannon +
                ", currentPledgedCell=" + currentPledgedCell +
                ", voteNos=" + Arrays.toString(voteNos) +
                ", privateKey='" + privateKey + '\'' +
                '}';
    }
}
