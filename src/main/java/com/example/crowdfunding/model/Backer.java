package com.example.crowdfunding.model;

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

    @Override
    public String toString() {
        return "Backer{" +
                "privateKey='" + privateKey + '\'' +
                ", pledgedCKB=" + pledgedCKB +
                '}';
    }
}
