package com.example.crowdfunding.model;

import org.nervos.ckb.Network;
import org.nervos.ckb.crypto.secp256k1.ECKeyPair;
import org.nervos.ckb.type.Script;
import org.nervos.ckb.utils.address.Address;

public class Backer {
    private String privateKey;
    private Long pledgedCKB;

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

    public Address address() {
        ECKeyPair keyPair = ECKeyPair.create(privateKey);
        Script lock = Script.generateSecp256K1Blake160SignhashAllScript(keyPair);
        return new Address(lock, Network.TESTNET);
    }

    @Override
    public String toString() {
        return "Backer{" +
                "privateKey='" + privateKey + '\'' +
                ", pledgedCKB=" + pledgedCKB +
                '}';
    }
}
