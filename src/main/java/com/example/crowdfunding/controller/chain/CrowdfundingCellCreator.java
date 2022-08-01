package com.example.crowdfunding.controller.chain;

import com.example.crowdfunding.model.Project;
import org.nervos.ckb.CkbRpcApi;
import org.nervos.ckb.Network;
import org.nervos.ckb.service.Api;
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

    public CrowdfundingCellCreator() {
        typeCodeHash = "0x8ca4635b957dcd4e5e462d8f6e1824fe7838aa4b5cc3303528b4af09972123cf";
        typeHashType = Script.HashType.DATA;
        cellDep = new CellDep(new OutPoint(
                Numeric.hexStringToByteArray("0xb71e2993bb2afbfd8adb6d9a37b555344c10751ccd38f39297dba267fcfdb8a9"),
                0),
                CellDep.DepType.CODE);
        ckbRpcApi = new Api("https://testnet.ckb.dev");
        sender = "ckt1qzda0cr08m85hc8jlnfp3zer7xulejywt49kt2rr0vthywaa50xwsqdndplz8ndtn096pdx2d69hlh529hnt63sy63ssn";
        privateKey = "0x3fe6f872224f43242d1189d8d1f8872cee258115b93186ea277b0162e7ca1319";
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

    public com.example.crowdfunding.model.OutPoint createCrowdfundingCell(Project project) {
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
        return new com.example.crowdfunding.model.OutPoint(txHash, 0);
    }
}
