package com.example.crowdfunding.controller.chain;

import com.example.crowdfunding.model.Project;
import org.nervos.ckb.CkbRpcApi;
import org.nervos.ckb.Network;
import org.nervos.ckb.sign.TransactionSigner;
import org.nervos.ckb.sign.TransactionWithScriptGroups;
import org.nervos.ckb.type.*;
import org.nervos.ckb.utils.Numeric;
import org.nervos.ckb.utils.address.Address;
import org.nervos.indexer.CkbIndexerApi;
import org.nervos.indexer.InputIterator;

import java.io.IOException;
import java.util.Iterator;

public class CrowdfundingCellCreator {
    private String typeCodeHash;
    private Script.HashType typeHashType;
    private CellDep cellDep;

    private String sender;
    private String privateKey;

    private CkbRpcApi ckbRpcApi;
    private CkbIndexerApi ckbIndexerApi;

    public void setTypeCodeHash(String typeCodeHash) {
        this.typeCodeHash = typeCodeHash;
    }

    public void setTypeHashType(Script.HashType typeHashType) {
        this.typeHashType = typeHashType;
    }

    public void setCellDep(CellDep cellDep) {
        this.cellDep = cellDep;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public void setCkbRpcApi(CkbRpcApi ckbRpcApi) {
        this.ckbRpcApi = ckbRpcApi;
    }

    public void setCkbIndexerApi(CkbIndexerApi ckbIndexerApi) {
        this.ckbIndexerApi = ckbIndexerApi;
    }

    public CellOutput newCrowdfundingCell(Project project) {
        byte[] data = project.toData();

        Script type = newTypeScript(project.toData());
        Script lock = Address.decode(sender).getScript();

        CellOutput cellOutput = new CellOutput(0, lock, type);
        cellOutput.capacity = cellOutput.occupiedCapacity(data);
        return cellOutput;
    }

    public Script newTypeScript(byte[] data) {
        Script type = new Script();
        type.codeHash = Numeric.hexStringToByteArray(typeCodeHash);
        type.hashType = typeHashType;
        type.args = new byte[32];
        return type;
    }

    public OutPoint createCrowdfundingCell(Project project) {
        Network network = Network.TESTNET;
        CellOutput cellOutput = newCrowdfundingCell(project);

        Iterator<TransactionInput> iterator = new InputIterator(ckbIndexerApi).addSearchKey(sender);
        CrowdfundingCellTransactionBuilder builder = new CrowdfundingCellTransactionBuilder(iterator, network);

        builder.addCellDep(cellDep);
        builder.addOutput(cellOutput, project.toData());
        builder.setChangeOutput(sender);

        TransactionWithScriptGroups txWithGroups = builder.build();

        TransactionSigner.getInstance(network)
                .signTransaction(txWithGroups, privateKey);

        byte[] txHash = new byte[0];
        try {
            txHash = ckbRpcApi.sendTransaction(txWithGroups.getTxView());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new OutPoint(txHash, 0);
    }
}
