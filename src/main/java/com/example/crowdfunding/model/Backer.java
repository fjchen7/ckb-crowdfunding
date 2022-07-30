package com.example.crowdfunding.model;

public class Backer {
    private String privateKey;
    private long amount;  // in CKBytes

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Backer{" +
                "privateKey='" + privateKey + '\'' +
                ", amount=" + amount +
                '}';
    }
}
