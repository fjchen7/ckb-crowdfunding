package com.example.crowdfunding.model;

//import org.hibernate.result.Output;

import org.nervos.ckb.type.CellOutput;
import org.nervos.ckb.type.OutPoint;

import java.io.Serializable;
import java.util.Arrays;

public class OnChainCell implements Serializable {
    private CellOutput output;
    private OutPoint outPoint;
    private byte[] outputData;

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

    public byte[] getOutputData() {
        return outputData;
    }

    public void setOutputData(byte[] outputData) {
        this.outputData = outputData;
    }

    @Override
    public String toString() {
        return "OnChainCell{" +
                "output=" + output +
                ", outPoint=" + outPoint +
                ", outputData=" + Arrays.toString(outputData) +
                '}';
    }
}
