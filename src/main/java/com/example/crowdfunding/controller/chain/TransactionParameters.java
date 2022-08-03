package com.example.crowdfunding.controller.chain;

import org.nervos.ckb.CkbRpcApi;
import org.nervos.ckb.type.CellDep;
import org.nervos.ckb.type.Script;
import org.nervos.indexer.CkbIndexerApi;

import java.util.List;

public class TransactionParameters {
    private String codeHash;
    private Script.HashType hashType;
    private List<CellDep> cellDeps;

    private CkbRpcApi ckbRpcApi;
    private CkbIndexerApi ckbIndexerApi;

    private String sender;
    private String privateKey;

    public String getCodeHash() {
        return codeHash;
    }

    public void setCodeHash(String codeHash) {
        this.codeHash = codeHash;
    }

    public Script.HashType getHashType() {
        return hashType;
    }

    public void setHashType(Script.HashType hashType) {
        this.hashType = hashType;
    }

    public List<CellDep> getCellDeps() {
        return cellDeps;
    }

    public void setCellDeps(List<CellDep> cellDeps) {
        this.cellDeps = cellDeps;
    }

    public CkbRpcApi getCkbRpcApi() {
        return ckbRpcApi;
    }

    public void setCkbRpcApi(CkbRpcApi ckbRpcApi) {
        this.ckbRpcApi = ckbRpcApi;
    }

    public CkbIndexerApi getCkbIndexerApi() {
        return ckbIndexerApi;
    }

    public void setCkbIndexerApi(CkbIndexerApi ckbIndexerApi) {
        this.ckbIndexerApi = ckbIndexerApi;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "TransactionParameters{" +
                "codeHash='" + codeHash + '\'' +
                ", hashType=" + hashType +
                ", cellDeps=" + cellDeps +
                ", ckbRpcApi=" + ckbRpcApi +
                ", ckbIndexerApi=" + ckbIndexerApi +
                ", sender='" + sender + '\'' +
                ", privateKey='" + privateKey + '\'' +
                '}';
    }
}
