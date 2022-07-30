package com.example.crowdfunding.model;

public class Backer {
    private String privateKey;
    private long pledgeAmount;  // in CKBytes

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public long getPledgeAmount() {
        return pledgeAmount;
    }

    public void setPledgeAmount(long pledgeAmount) {
        this.pledgeAmount = pledgeAmount;
    }

    @Override
    public String toString() {
        return "Backer{" +
                "privateKey='" + privateKey + '\'' +
                ", pledgeAmount=" + pledgeAmount +
                '}';
    }
}
