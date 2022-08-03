package com.example.crowdfunding.model;

import org.nervos.ckb.type.CellOutput;
import org.nervos.ckb.type.OutPoint;

import javax.persistence.Embedded;
import java.io.Serializable;

public class MilestonePledgeInfo implements Serializable {
//    @Embedded
    private CellOutput output;
//    @Embedded
    private OutPoint outPoint;
    private boolean voteNo;

    public CellOutput getOutput() {
        return output;
    }

    public void setOutput(CellOutput output) {
        this.output = output;
    }

    public OutPoint getOutPoint() {
        return outPoint;
    }

    public void setOutPoint(OutPoint outPoint) {
        this.outPoint = outPoint;
    }

    public boolean isVoteNo() {
        return voteNo;
    }

    public void setVoteNo(boolean voteNo) {
        this.voteNo = voteNo;
    }

    @Override
    public String toString() {
        return "PledgeCell{" +
                "output=" + output +
                ", outPoint=" + outPoint +
                ", voteNo=" + voteNo +
                '}';
    }
}
