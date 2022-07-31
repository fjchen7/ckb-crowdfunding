package com.example.crowdfunding.model;

public class Backer {
    private String privateKey;
    private long pledgedCKB;

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public long getPledgedCKB() {
        return pledgedCKB;
    }

    public void setPledgedCKB(long pledgedCKB) {
        this.pledgedCKB = pledgedCKB;
    }

    @Override
    public String toString() {
        return "Backer{" +
                "privateKey='" + privateKey + '\'' +
                ", pledgedCKB=" + pledgedCKB +
                '}';
    }
}
