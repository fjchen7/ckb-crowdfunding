package com.example.crowdfunding.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.nervos.ckb.Network;
import org.nervos.ckb.crypto.secp256k1.ECKeyPair;
import org.nervos.ckb.type.OutPoint;
import org.nervos.ckb.type.Script;
import org.nervos.ckb.utils.Utils;
import org.nervos.ckb.utils.address.Address;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Backer {
    @Id
    @GeneratedValue
    private Long id;
    private Long projectId;
    private Long pledgedCKB;
    @Embedded
    private OutPoint pledgedCell;
    @ElementCollection(targetClass = MilestonePledgeInfo.class)
    private List<MilestonePledgeInfo> milestonePledgeInfos;
    private String privateKey;

    public Backer() {
        milestonePledgeInfos = new ArrayList<>();
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
        if (milestonePledgeInfos == null || milestonePledgeInfos.size() == 0) {
            return Utils.ckbToShannon(pledgedCKB);
        }
        return milestonePledgeInfos.get(milestonePledgeInfos.size() - 1).getOutput().capacity;
    }

    public OutPoint getPledgedCell() {
        return pledgedCell;
    }

    public void setPledgedCell(OutPoint pledgedCell) {
        this.pledgedCell = pledgedCell;
    }

    public List<MilestonePledgeInfo> getMilestonePledgeInfos() {
        return milestonePledgeInfos;
    }

    public void setMilestonePledgeInfos(List<MilestonePledgeInfo> milestonePledgeInfos) {
        this.milestonePledgeInfos = milestonePledgeInfos;
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
                ", pledgedCell=" + pledgedCell +
                ", milestonePledgeInfos=" + milestonePledgeInfos +
                ", privateKey='" + privateKey + '\'' +
                '}';
    }
}
