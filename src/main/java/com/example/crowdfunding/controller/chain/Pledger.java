package com.example.crowdfunding.controller.chain;

import com.example.crowdfunding.model.Backer;
import com.example.crowdfunding.model.Project;
import org.nervos.ckb.CkbRpcApi;
import org.nervos.ckb.Network;
import org.nervos.ckb.crypto.Blake2b;
import org.nervos.ckb.crypto.secp256k1.ECKeyPair;
import org.nervos.ckb.sign.TransactionSigner;
import org.nervos.ckb.sign.TransactionWithScriptGroups;
import org.nervos.ckb.transaction.CkbTransactionBuilder;
import org.nervos.ckb.type.CellDep;
import org.nervos.ckb.type.CellOutput;
import org.nervos.ckb.type.OutPoint;
import org.nervos.ckb.type.Script;
import org.nervos.ckb.utils.Numeric;
import org.nervos.ckb.utils.Utils;
import org.nervos.ckb.utils.address.Address;
import org.nervos.indexer.CkbIndexerApi;
import org.nervos.indexer.InputIterator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Pledger {
    private String lockCodeHash;
    private Script.HashType lockHashType;
    private List<CellDep> cellDeps;

    private String sender;
    private String privateKey;

    private CkbRpcApi ckbRpcApi;
    private CkbIndexerApi ckbIndexerApi;

    public void setLockCodeHash(String lockCodeHash) {
        this.lockCodeHash = lockCodeHash;
    }

    public void setLockHashType(Script.HashType lockHashType) {
        this.lockHashType = lockHashType;
    }

    public void setCellDeps(List<CellDep> cellDeps) {
        this.cellDeps = cellDeps;
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

    public OutPoint pledge(Backer backer, Project project) {
        Network network = Network.TESTNET;
        InputIterator iterator = new InputIterator(ckbIndexerApi);
        String addr = getAddress(backer.getPrivateKey()).encode();
        iterator.addSearchKey(addr);

        CkbTransactionBuilder builder = new CkbTransactionBuilder(iterator, network);
        builder.addCellDeps(cellDeps);
        builder.addOutput(newOutput(backer, project), new byte[0]);
        builder.setFeeRate(1000);
        builder.setChangeOutput(addr);

        TransactionWithScriptGroups txWithGroups = builder.build();

        TransactionSigner.getInstance(network)
                .signTransaction(txWithGroups, backer.getPrivateKey());
        byte[] txHash = new byte[0];
        try {
            txHash = ckbRpcApi.sendTransaction(txWithGroups.getTxView());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new OutPoint(txHash, 0);
    }

    private Address getAddress(String privateKey) {
        ECKeyPair keyPair = ECKeyPair.create(privateKey);
        Script lock = Script.generateSecp256K1Blake160SignhashAllScript(keyPair);
        return new Address(lock, Network.TESTNET);
    }

    private CellOutput newOutput(Backer backer, Project project) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write(Blake2b.digest(project.toData()));
            Script receiverLock = Address.decode(sender).getScript();
            byte[] hash = receiverLock.computeHash();
            out.write(Arrays.copyOfRange(hash, 0, 20));

            Script senderLock = backer.address().getScript();
            hash = senderLock.computeHash();
            Arrays.copyOfRange(hash, 0, 20);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CellOutput output = new CellOutput();
        output.lock = new Script(Numeric.hexStringToByteArray(lockCodeHash), out.toByteArray(), lockHashType);
        output.capacity = Utils.ckbToShannon(backer.getPledgedCKB());
        return output;
    }
}
