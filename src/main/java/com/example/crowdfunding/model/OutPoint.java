package com.example.crowdfunding.model;

import java.io.Serializable;

public class OutPoint implements Serializable {
    public String txHash;
    public Integer index;

    public OutPoint() {
    }

    public OutPoint(String txHash, Integer index) {
        this.txHash = txHash;
        this.index = index;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "OutPoint{" +
                "txHash='" + txHash + '\'' +
                ", index=" + index +
                '}';
    }
}
